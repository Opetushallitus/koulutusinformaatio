package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.IncrementalUpdateServiceImpl;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class SingleParentLOSBuilder {
    
    public static final Logger LOG = LoggerFactory.getLogger(SingleParentLOSBuilder.class);
    private static final String NOT_IN_STATE = " not in state ";
    
    private LOSObjectCreator losCreator;
    private TarjontaRawService tarjontaRawService;
    
    public SingleParentLOSBuilder(LOSObjectCreator losCreator, TarjontaRawService tarjontaRawService) {
        this.losCreator = losCreator;
        this.tarjontaRawService = tarjontaRawService;
    }
    
    public ParentLOS createParentLOS(KomoDTO parentKomo, String providerId) throws TarjontaParseException, KoodistoException {
        LOG.debug("Recreating parent los: " + parentKomo.getOid() + "_" + providerId);
        //String providerId = los.getProvider().getId();
        
        List<OidRDTO> parentKomotoOids = tarjontaRawService.getKomotosByKomo(parentKomo.getOid(), Integer.MAX_VALUE, 0);
        if (parentKomotoOids == null || parentKomotoOids.size() == 0) {
            throw new TarjontaParseException("No instances found in parent LOS " + parentKomo.getOid());
        }
        
        List<KomotoDTO> providersParentKomotos = new ArrayList<KomotoDTO>();
        
        for (OidRDTO parentKomotoOid : parentKomotoOids) {
            KomotoDTO parentKomoto = tarjontaRawService.getKomoto(parentKomotoOid.getOid());
            if (parentKomoto.getTarjoajaOid().equals(providerId)) {
                providersParentKomotos.add(parentKomoto);
            }
        }
        LOG.debug("Populating parent los data: " + parentKomo.getOid() + "_" + providerId);
        return this.losCreator.createParentLOS(parentKomo, providerId, providersParentKomotos);
    }
    
    public List<ChildLOS> createChildLoss(KomoDTO parentKomo, ParentLOS los) throws TarjontaParseException, KoodistoException {
        List<ChildLOS> childLoss = new ArrayList<ChildLOS>();
        
        
        List<String> childKomoIds = parentKomo.getAlaModuulit();
        for (String childKomoId : childKomoIds) {
            KomoDTO childKomo = tarjontaRawService.getKomo(childKomoId);

            // A helper data structure that groups child komoto KomotoDTO objects by their provider and komo (ChildLOS id = komo oid + provider oid)
             List<KomotoDTO> childKomotos = new ArrayList<KomotoDTO>();


            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                LOG.debug("Invalid child komo " + childKomo.getOid() + ": " + e.getMessage());
                continue;
            }
            List<OidRDTO> childKomotoOids = tarjontaRawService.getKomotosByKomo(childKomoId, Integer.MAX_VALUE, 0);
            for (OidRDTO childKomotoOid : childKomotoOids) {
                KomotoDTO childKomoto = tarjontaRawService.getKomoto(childKomotoOid.getOid());

                if (isSpecialEdKomoto(childKomoto)) {
                    // ER
                    LOG.warn("Here is a special ed komoto, it should not be here.");

                }
                else if (childKomoto.getTarjoajaOid().equals(los.getProvider().getId())) {
                    // PK & YO
                    childKomotos.add(childKomoto);
                    
                    
                }
            }
            
            if (!childKomotos.isEmpty()) {
                childLoss.add(this.losCreator.createChildLOS(childKomo, this.resolveLosId(childKomo.getOid(), los.getProvider().getId()), childKomotos));
            }
        }

        
        return childLoss;
    }

    public void assembleParentLos(ParentLOS parent, List<ChildLOS> children) {
       

            Multimap<String, ApplicationOption> applicationOptionsByParentLOIId = HashMultimap.create();
            Multimap<String, Code> availableTranslationLangsByParentLOIId = HashMultimap.create();

            
            List<ChildLOS> validChildren = Lists.newArrayList(
                    Collections2.filter(children, new Predicate<ChildLOS>() {
                        @Override
                        public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ChildLOS input) {
                            return isChildLOSValid(input);
                        }
                    })
            );
            
            // add children to parent los
            // filter out children without lois

            Map<String,Code> codeLang = new HashMap<String,Code>();
            for (ChildLOS childLOS : validChildren) {

                // set parent ref
                childLOS.setParent(new ParentLOSRef(parent.getId(), parent.getName()));

                for (ChildLOI childLOI : childLOS.getLois()) {

                    // add info to ao
                    for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                        ao.setProvider(parent.getProvider());
                        ao.setParent(new ParentLOSRef(parent.getId(), parent.getName()));
                        ao.setEducationDegree(parent.getEducationDegree());
                        parent.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
                        ao.setType(parent.getType());
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
            for (ParentLOI parentLOI : parent.getLois()) {
                parentLOI.setApplicationOptions(Lists.newArrayList(applicationOptionsByParentLOIId.get(parentLOI.getId())));
                parentLOI.setAvailableTranslationLanguages(Lists.newArrayList(availableTranslationLangsByParentLOIId.get(parentLOI.getId())));
            }
            parent.setChildren(validChildren);
            parent.setTeachingLanguages(new ArrayList<Code>(codeLang.values()));
        
    }
    
    

    
    
    
    
    
    private String resolveLosId(String komoId, String providerId) {
        return String.format("%s_%s", komoId, providerId);
    }
    
    public static boolean isSpecialEdKomoto(KomotoDTO komoto) {
        return komoto.getPohjakoulutusVaatimusUri().contains(TarjontaConstants.PREREQUISITE_URI_ER);
    }

    
    protected void validateParentKomo(KomoDTO komo) throws TarjontaParseException {
        // parent check
        if (!komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("Komo not of type " + TarjontaConstants.MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Parent komo state not " + TarjontaTila.JULKAISTU.toString());
        }
    }

    protected void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komo " + komo.getOid() + NOT_IN_STATE + TarjontaTila.JULKAISTU.toString());
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("Child KomoDTO koulutusOhjelmaKoodiUri (name) is null");
        }
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("Child KomoDTO tutkinto nimike uri is null");
        }
    }

    protected void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komoto " + komoto.getOid() + NOT_IN_STATE + TarjontaTila.JULKAISTU.toString());
        }

    }

    protected void validateHakukohde(HakukohdeDTO hakukohde) throws TarjontaParseException {
        if (!hakukohde.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application option " + hakukohde.getOid() + NOT_IN_STATE + TarjontaConstants.STATE_PUBLISHED);
        }
    }

    protected void validateHaku(HakuDTO haku) throws TarjontaParseException {
        if (!haku.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application system " + haku.getOid() + NOT_IN_STATE + TarjontaConstants.STATE_PUBLISHED);
        }
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
    
    private ChildLOIRef buildChildLOIRef(final ChildLOI childLOI) {
        if (childLOI != null) {
            ChildLOIRef ref = new ChildLOIRef();
            ref.setId(childLOI.getId());
            ref.setLosId(childLOI.getLosId());
            ref.setName(childLOI.getName());
            ref.setNameByTeachingLang(VocationalLearningOpportunityBuilder.getTextByEducationLanguage(childLOI.getShortName(), childLOI.getTeachingLanguages()));
            ref.setPrerequisite(childLOI.getPrerequisite());
            return ref;
        }
        return null;
    }

    public ParentLOS filterParentLos(ParentLOS parentLOS) {
        
        // filter out empty parent lois
        Set<String> parentLOIIdsInUse = Sets.newHashSet();
        
        //Map<String,ApplicationOption> aos = Maps.newHashMap();
        List<ApplicationOption> aoList = new ArrayList<ApplicationOption>();
        //for (ParentLOS parentLOS : this.parentLOSs) {
            
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
        //}
        
        
        

        // filter out empty parent LOSs
        /*this.parentLOSs = Lists.newArrayList(
                Collections2.filter(this.parentLOSs, new Predicate<ParentLOS>() {
                    @Override
                    public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ParentLOS input) {
                        return isParentLOSValid(input);
                    }
                })
        );*/
        
        //filtering out non-existing childLOIRefs from application options
        Set<String> childLosIdsInUse = Sets.newHashSet();
        //for (ParentLOS parentLOS : this.parentLOSs) {
            
            for (ChildLOS childLOS : parentLOS.getChildren()) {
                childLosIdsInUse.add(childLOS.getId());
            }
        //}
        for (ApplicationOption curAo : aoList) {
            List<ChildLOIRef> childRefs = new ArrayList<ChildLOIRef>();
            for (ChildLOIRef curchild : curAo.getChildLOIRefs()) {
                if (childLosIdsInUse.contains(curchild.getLosId())) {
                    childRefs.add(curchild);
                }
            }
            curAo.setChildLOIRefs(childRefs);
        }

     if (parentLOS.getChildren() == null || parentLOS.getChildren().isEmpty()) {
         return null;
     }
     return parentLOS;
        
    }


}
