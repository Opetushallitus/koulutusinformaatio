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
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class VocationalLearningOpportunityBuilder extends LearningOpportunityBuilder<ParentLOS> {

    private TarjontaRawService tarjontaRawService;
    private ProviderService providerService;
    private KoodistoService koodistoService;
    private LOSObjectCreator losObjectCreator;

    // Parent komo KomoDTO object that corresponds to the oid
    private KomoDTO parentKomo;

    // List of ParentLOS objects that are returned when the build() method is invoked
    private List<ParentLOS> parentLOSs;

    // A helper data structure that groups parent komoto KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> parentKomotosByProviderId;

    // A helper data structure that groups ChildLO objects by their ParentLOS id
    ArrayListMultimap<String, ChildLOS> childLOSsByParentLOSId;

    public VocationalLearningOpportunityBuilder(TarjontaRawService tarjontaRawService,
                                                ProviderService providerService,
                                                KoodistoService koodistoService, KomoDTO parentKomo) {
        this.tarjontaRawService = tarjontaRawService;
        this.providerService = providerService;
        this.koodistoService = koodistoService;
        this.losObjectCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
        this.parentKomo = parentKomo;
        parentKomotosByProviderId = ArrayListMultimap.create();
        childLOSsByParentLOSId = ArrayListMultimap.create();
        parentLOSs = Lists.newArrayList();
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
            parentKomotosByProviderId.put(parentKomoto.getTarjoajaOid(), parentKomoto);
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

            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                LOG.debug("Invalid child komo " + childKomo.getOid() + ": " + e.getMessage());
                continue;
            }
            List<OidRDTO> childKomotoOids = tarjontaRawService.getKomotosByKomo(childKomoId, Integer.MAX_VALUE, 0);
            for (OidRDTO childKomotoOid : childKomotoOids) {
                KomotoDTO childKomoto = tarjontaRawService.getKomoto(childKomotoOid.getOid());
                childKomotosByChildLOSId.put(resolveLOSId(childKomoId, childKomoto.getTarjoajaOid()), childKomoto);
            }
            for (String childLOSId : childKomotosByChildLOSId.keySet()) {
                childLOSsByParentLOSId.put(resolveLOSId(parentKomo.getOid(), resolveProviderId(childLOSId)),
                        losObjectCreator.createChildLOS(childKomo, childLOSId, childKomotosByChildLOSId.get(childLOSId)));
            }
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (ParentLOS parentLOS : parentLOSs) {

            HashMultimap<String, ApplicationOption> applicationOptionsByParentLOIId = HashMultimap.create();

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

            for (ChildLOS childLOS : children) {

                // set parent ref
                childLOS.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));

                for (ChildLOI childLOI : childLOS.getLois()) {

                    // add info to ao
                    for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                        ao.setProvider(parentLOS.getProvider());
                        ao.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));
                        ao.setEducationDegree(parentLOS.getEducationDegree());
                        parentLOS.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
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
                }
            }
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                parentLOI.setApplicationOptions(applicationOptionsByParentLOIId.get(parentLOI.getId()));
            }
            parentLOS.setChildren(children);
        }
        return this;
    }

    private ChildLOIRef buildChildLOIRef(final ChildLOI childLOI) {
        if (childLOI != null) {
            ChildLOIRef ref = new ChildLOIRef();
            ref.setId(childLOI.getId());
            ref.setLosId(childLOI.getLosId());
            ref.setName(childLOI.getName());
            ref.setNameByTeachingLang(getTextByEducationLanguage(childLOI.getName(), childLOI.getTeachingLanguages()));
            ref.setPrerequisite(childLOI.getPrerequisite());
            return ref;
        }
        return null;
    }

    private static String getTextByEducationLanguage(final I18nText text, List<Code> languages) {
        if (text != null && text.getTranslationsShortName() != null && !text.getTranslationsShortName().isEmpty()) {
            if (languages != null && !languages.isEmpty()) {
                for (Code code : languages) {
                    if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                        return text.getTranslationsShortName().get(LANG_FI);
                    }
                }
                String val = text.getTranslationsShortName().get(languages.get(0).getValue().toLowerCase());
                if (val != null) {
                    return val;
                }
            }
            return text.getTranslationsShortName().values().iterator().next();
        }
        return null;
    }

    @Override
    public LearningOpportunityBuilder filter() {

        // filter out empty parent lois
        Set<String> parentLOIIdsInUse = Sets.newHashSet();
        for (ParentLOS parentLOS : this.parentLOSs) {
            for (ChildLOS childLOS : parentLOS.getChildren()) {
                for (ChildLOI childLOI : childLOS.getLois()) {
                    parentLOIIdsInUse.add(childLOI.getParentLOIId());
                }
            }
            List<ParentLOI> parentLOIsInUse = Lists.newArrayList();
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                if (parentLOIIdsInUse.contains(parentLOI.getId())) {
                    parentLOIsInUse.add(parentLOI);
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
        return this;
    }

    @Override
    public List<ParentLOS> build() {
        return parentLOSs;
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