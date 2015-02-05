/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

import javax.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class VocationalLearningOpportunityBuilder extends LearningOpportunityBuilder<LOS> {

    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator losObjectCreator;

    // Parent komo KomoDTO object that corresponds to the oid
    private KomoDTO parentKomo;

    // List of ParentLOS objects that are returned when the build() method is invoked
    private List<ParentLOS> parentLOSs;

    // List of special education loses
    private List<SpecialLOS> specialLOSs;

    // full list of
    private List<LOS> allLOSs;

    // A helper data structure that groups parent komoto KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> parentKomotosByProviderId;

    // A helper data structure that groups ChildLO objects by their ParentLOS id
    ArrayListMultimap<String, ChildLOS> childLOSsByParentLOSId;

    public VocationalLearningOpportunityBuilder(TarjontaRawService tarjontaRawService,
                                                ProviderService providerService,
                                                KoodistoService koodistoService, KomoDTO parentKomo,
                                                OrganisaatioRawService organisaatioRawService,
                                                ParameterService parameterService) {
        this.tarjontaRawService = tarjontaRawService;
        this.losObjectCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService,
                organisaatioRawService, parameterService);
        this.parentKomo = parentKomo;
        this.parentKomotosByProviderId = ArrayListMultimap.create();
        this.childLOSsByParentLOSId = ArrayListMultimap.create();
        this.parentLOSs = Lists.newArrayList();
        this.allLOSs = Lists.newArrayList();
        this.specialLOSs = Lists.newArrayList();
    }

    @Override
    public LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        LOG.debug(Joiner.on(" ").join("Resolving parent LOSs for komo: ", parentKomo.getOid()));
        validateParentKomo(parentKomo);
        List<OidRDTO> parentKomotoOids = tarjontaRawService.getKomotosByKomo(parentKomo.getOid(), Integer.MAX_VALUE, 0);
        if (parentKomotoOids == null || parentKomotoOids.size() == 0) {
            throw new TarjontaParseException("No instances found in parent LOS " + parentKomo.getOid());
        }
        parentKomotosByProviderId = ArrayListMultimap.create();
        for (OidRDTO parentKomotoOid : parentKomotoOids) {
            KomotoDTO parentKomoto = tarjontaRawService.getKomoto(parentKomotoOid.getOid());
            //if (isNuortenKoulutus(parentKomoto)) {
                parentKomotosByProviderId.put(parentKomoto.getTarjoajaOid(), parentKomoto);
            //}
        }

        for (String key : parentKomotosByProviderId.keySet()) {
            parentLOSs.add(losObjectCreator.createParentLOS(parentKomo, key, parentKomotosByProviderId.get(key)));
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        List<String> childKomoIds = parentKomo.getAlaModuulit();
        for (String childKomoId : childKomoIds) {
            KomoDTO childKomo = tarjontaRawService.getKomo(childKomoId);

            // A helper data structure that groups child komoto KomotoDTO objects by their provider and komo (ChildLOS id = komo oid + provider oid)
            ArrayListMultimap<String, KomotoDTO> childKomotosByChildLOSId = ArrayListMultimap.create();
            // holds special education child komotos by provider and komo
            ArrayListMultimap<String, KomotoDTO> specialChildKomotosByChildLOSId = ArrayListMultimap.create();


            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                LOG.debug("Invalid child komo " + childKomo.getOid() + ": " + e.getMessage());
                continue;
            }
            List<OidRDTO> childKomotoOids = tarjontaRawService.getKomotosByKomo(childKomoId, Integer.MAX_VALUE, 0);
            for (OidRDTO childKomotoOid : childKomotoOids) {
                KomotoDTO childKomoto = tarjontaRawService.getKomoto(childKomotoOid.getOid());
                try {
                    validateChildKomoto(childKomoto);
                } catch (TarjontaParseException e) {
                    LOG.debug("Invalid child komo " + childKomoto.getOid() + ": " + e.getMessage());
                    continue;
                }
                if (isSpecialEdKomoto(childKomoto)) {
                    // ER
                    String id = String.format("%s_er", resolveLOSId(childKomoId, childKomoto.getTarjoajaOid()));
                    specialChildKomotosByChildLOSId.put(id, childKomoto);

                }
                else if (isNuortenKoulutus(childKomoto)) {
                    // PK & YO
                    childKomotosByChildLOSId.put(resolveLOSId(childKomoId, childKomoto.getTarjoajaOid()), childKomoto);
                }
            }
            for (String childLOSId : childKomotosByChildLOSId.keySet()) {
                childLOSsByParentLOSId.put(resolveLOSId(parentKomo.getOid(), resolveProviderId(childLOSId)),
                        losObjectCreator.createChildLOS(childKomo, childLOSId, childKomotosByChildLOSId.get(childLOSId)));
            }
            for (String specialChildLOSId : specialChildKomotosByChildLOSId.keySet()) {
                specialLOSs.add(losObjectCreator.createSpecialLOS(childKomo, parentKomo, specialChildLOSId,
                        specialChildKomotosByChildLOSId.get(specialChildLOSId), resolveProviderId(specialChildLOSId)));
            }
        }
        return this;
    }

    private boolean isSpecialEdKomoto(KomotoDTO komoto) {
        return komoto.getPohjakoulutusVaatimusUri() != null && komoto.getPohjakoulutusVaatimusUri().contains(TarjontaConstants.PREREQUISITE_URI_ER) && komoto.getKoulutuslajiUris() != null && !komoto.getKoulutuslajiUris().isEmpty() && komoto.getKoulutuslajiUris().get(0).contains(TarjontaConstants.NUORTEN_KOULUTUS);
    }

    @Override
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (ParentLOS parentLOS : parentLOSs) {

            Multimap<String, ApplicationOption> applicationOptionsByParentLOIId = HashMultimap.create();
            Multimap<String, Code> availableTranslationLangsByParentLOIId = HashMultimap.create();

            // add children to parent los
            // filter out children without lois
            List<ChildLOS> children = Lists.newArrayList(
                    Collections2.filter(childLOSsByParentLOSId.get(parentLOS.getId()), new Predicate<ChildLOS>() {
                        @Override
                        public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ChildLOS input) {
                            return isChildLOSValid(input);
                        }
                    })
            );

            Map<String,Code> codeLang = new HashMap<String,Code>();
            for (ChildLOS childLOS : children) {

                // set parent ref
                childLOS.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));

                for (ChildLOI childLOI : childLOS.getLois()) {

                    // add info to ao
                    for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                        ao.setProvider(parentLOS.getProvider());
                        ao.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));
                        ao.setEducationDegree(parentLOS.getEducationDegree());
                        parentLOS.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                        ao.setType(parentLOS.getType());
                    }

                    // save application options to be added to parent loi
                    applicationOptionsByParentLOIId.putAll(childLOI.getParentLOIId(), childLOI.getApplicationOptions());
                    // add related child refs to child
                    childLOI.setRelated(new ArrayList<ChildLOIRef>());
                    for (ChildLOI ref : childLOS.getLois()) {
                        if (!childLOI.getId().equals(ref.getId()) &&
                                childLOI.getPrerequisite().getValue().equals(ref.getPrerequisite().getValue())) {
                            ChildLOIRef cRef = buildChildLOIRef(ref);
                            if (cRef != null) {
                                childLOI.getRelated().add(cRef);
                            }
                        }
                    }
                    
                    for (Code lang : childLOI.getTeachingLanguages()) {
                        codeLang.put(lang.getValue(), lang);
                        availableTranslationLangsByParentLOIId.put(childLOI.getParentLOIId(), lang);
                    }
                }
                
            }
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                parentLOI.setApplicationOptions(Lists.newArrayList(applicationOptionsByParentLOIId.get(parentLOI.getId())));
                parentLOI.setAvailableTranslationLanguages(Lists.newArrayList(availableTranslationLangsByParentLOIId.get(parentLOI.getId())));
            }
            parentLOS.setChildren(children);
            parentLOS.setTeachingLanguages(new ArrayList<Code>(codeLang.values()));
        }
        
        for (SpecialLOS curSpecial : this.specialLOSs) {
            for (ChildLOI curChild : curSpecial.getLois()) {
                for (ApplicationOption curAo : curChild.getApplicationOptions()) {
                    curAo.setType(TarjontaConstants.TYPE_SPECIAL);
                    curAo.setParent(new ParentLOSRef(curSpecial.getId(), curSpecial.getName()));
                    curSpecial.getProvider().getApplicationSystemIds().add(curAo.getApplicationSystem().getId());
                    curAo.setProvider(curSpecial.getProvider());
                }
            }
        }
        
        return this;
    }

    private ChildLOIRef buildChildLOIRef(final ChildLOI childLOI) {
        if (childLOI != null) {
            ChildLOIRef ref = new ChildLOIRef();
            ref.setId(childLOI.getId());
            ref.setLosId(childLOI.getLosId());
            ref.setName(childLOI.getName());
            ref.setNameByTeachingLang(getTextByEducationLanguage(childLOI.getShortName(), childLOI.getTeachingLanguages()));
            ref.setPrerequisite(childLOI.getPrerequisite());
            return ref;
        }
        return null;
    }

    public static String getTextByEducationLanguage(final I18nText text, List<Code> languages) {
        if (text != null && text.getTranslations() != null && !text.getTranslations().isEmpty()) {
            if (languages != null && !languages.isEmpty()) {
                for (Code code : languages) {
                    if (code.getValue().equalsIgnoreCase(TarjontaConstants.LANG_FI)) {
                        return text.getTranslations().get(TarjontaConstants.LANG_FI);
                    }
                }
                String val = text.getTranslations().get(languages.get(0).getValue().toLowerCase());
                if (val != null) {
                    return val;
                }
            }
            return text.getTranslations().values().iterator().next();
        }
        return null;
    }

    @Override
    public LearningOpportunityBuilder filter() {

        // filter out empty parent lois
        Set<String> parentLOIIdsInUse = Sets.newHashSet();
        
        //Map<String,ApplicationOption> aos = Maps.newHashMap();
        List<ApplicationOption> aoList = new ArrayList<ApplicationOption>();
        for (ParentLOS parentLOS : this.parentLOSs) {
            
            for (ChildLOS childLOS : parentLOS.getChildren()) {
                for (ChildLOI childLOI : childLOS.getLois()) {
                    parentLOIIdsInUse.add(childLOI.getParentLOIId());
                    for (ApplicationOption ao: childLOI.getApplicationOptions()) {
                        aoList.add(ao);
                    }
                    
                }
                
                
                
            }
            List<ParentLOI> parentLOIsInUse = Lists.newArrayList();
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                if (parentLOIIdsInUse.contains(parentLOI.getId())) {
                    parentLOIsInUse.add(parentLOI);   
                }
                for (ApplicationOption ao: parentLOI.getApplicationOptions()) {
                    aoList.add(ao);
                }
            }
            parentLOS.setLois(parentLOIsInUse);
        }
        
        
        

        // filter out empty parent LOSs
        this.parentLOSs = Lists.newArrayList(
                Collections2.filter(this.parentLOSs, new Predicate<ParentLOS>() {
                    @Override
                    public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ParentLOS input) {
                        return isParentLOSValid(input);
                    }
                })
        );
        
        //filtering out non-existing childLOIRefs from application options
        Set<String> childLosIdsInUse = Sets.newHashSet();
        for (ParentLOS parentLOS : this.parentLOSs) {
            
            for (ChildLOS childLOS : parentLOS.getChildren()) {
                childLosIdsInUse.add(childLOS.getId());
            }
        }
        for (ApplicationOption curAo : aoList) {
            List<ChildLOIRef> childRefs = new ArrayList<ChildLOIRef>();
            for (ChildLOIRef curchild : curAo.getChildLOIRefs()) {
                if (childLosIdsInUse.contains(curchild.getLosId())) {
                    childRefs.add(curchild);
                }
            }
            curAo.setChildLOIRefs(childRefs);
        }

        // filter out special LOSs
        this.specialLOSs = Lists.newArrayList(
                Collections2.filter(this.specialLOSs, CreatorUtil.specialLOSValid));
        return this;
    }

    @Override
    public List<LOS> build() {
        this.allLOSs.addAll(parentLOSs);
        this.allLOSs.addAll(specialLOSs);
        return allLOSs;
    }

    private boolean isParentLOSValid(ParentLOS parentLOS) {
        if (parentLOS.getChildren() == null || parentLOS.getChildren().isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

    private boolean isChildLOSValid(ChildLOS childLOS) {
        if (childLOS.getLois() != null) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getApplicationOptions() != null && childLOI.getApplicationOptions().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private String resolveProviderId(String losId) {
        return losId.split("_")[1];
    }

}