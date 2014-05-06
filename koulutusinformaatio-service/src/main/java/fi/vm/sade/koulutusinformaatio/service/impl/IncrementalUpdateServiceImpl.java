package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

@Service
@Profile("default")
public class IncrementalUpdateServiceImpl implements IncrementalUpdateService {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalUpdateServiceImpl.class);
    
    private TarjontaRawService tarjontaRawService;
    private UpdateService updateService;
    private TransactionManager transactionManager;
    private EducationDataQueryService dataQueryService;
    private EducationDataUpdateService dataUpdateService;
    
    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
                                         UpdateService updateService, 
                                         TransactionManager transactionManager,
                                         EducationDataQueryService dataQueryService,
                                         EducationDataUpdateService dataUpdateService) {
        this.tarjontaRawService = tarjontaRawService;
        this.updateService = updateService;
        this.transactionManager = transactionManager;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
    }
    
    @Override
    @Async
    public void updateChangedEducationData() throws Exception {
        LOG.info("updateChangedEducationData on its way");
        
        long updatePeriod = getUpdatePeriod();
        
        
        Map<String,List<String>> result = listChangedLearningOpportunities(updatePeriod);
        
        if (result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) {
            updateService.updateAllEducationData();
        } else {
            this.transactionManager.beginIncrementalTransaction();
        }
        
        if (result.containsKey("haku")) {
            
            for (String curOid : result.get("haku")) {
                indexApplicationSystemData(curOid);
            }
        }   
        
    }
    
    private long getUpdatePeriod() {
        long period = 0;
        return period;
    }

    private void indexApplicationSystemData(String asOid) {
        
        HakuDTO asDto = this.tarjontaRawService.getHaku(asOid);
        String asState = asDto.getTila();
        //if (asState.equals(TarjontaConstants.STATE_PUBLISHED)) {
            List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asOid);
            if (hakukohdeOids != null && hakukohdeOids.isEmpty()) {
                for (OidRDTO curOid : hakukohdeOids) {
                    indexApplicationOptionData(curOid.getOid(), asDto);
                }
            }
        //}
        
    }

    private void indexApplicationOptionData(String oid, HakuDTO asDto) {
        
        if (!TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila())) {
            removeApplicationOption(oid);
        }
        
        
    }
    
    private void removeApplicationOption(String oid) {
        
        try {
            ApplicationOption ao = this.dataQueryService.getApplicationOption(oid);
            
            ParentLOSRef parentRef = ao.getParent();
            
            LOS los  = this.dataQueryService.getLos(parentRef.getId());
            
            if (los != null && los instanceof ParentLOS) {
                handleParentLOSReferenceRemoval((ParentLOS)los, ao, true);
            } else if (los != null && los instanceof SpecialLOS) {
                handleSpecialLOSReferenceRemoval((SpecialLOS)los, ao, true);
            } 
            
            if (ao.getHigherEdLOSRefs() != null 
                    && !ao.getHigherEdLOSRefs().isEmpty()) {
                handleHigherEdLOSsReferenceRemoval(ao);
            }
            
            if (ao.getChildLOIRefs() != null
                    && !ao.getChildLOIRefs().isEmpty()) {
                handleLoiRefRemoval(ao);
            }
            
            
            
        } catch (ResourceNotFoundException notFoundEx) {
            LOG.debug(notFoundEx.getMessage());
        }
        
    }

    private void handleLoiRefRemoval(ApplicationOption ao) {
        
        for (ChildLOIRef curChildRef : ao.getChildLOIRefs()) {
            LOS referencedLos = this.dataQueryService.getLos(curChildRef.getLosId());
            if (referencedLos instanceof ParentLOS) {
                this.handleParentLOSReferenceRemoval((ParentLOS)referencedLos, ao, false);
            } else if (referencedLos instanceof SpecialLOS) {
                this.handleSpecialLOSReferenceRemoval((SpecialLOS)referencedLos, ao, false);
            } else if (referencedLos instanceof ChildLOS) {
                handleChildLosReferenceRemoval((ChildLOS)referencedLos, ao);      
            } else if (referencedLos instanceof UpperSecondaryLOS) {
                
            }
        }
        
    }

    private void handleChildLosReferenceRemoval(ChildLOS referencedLos,
            ApplicationOption ao) {
        
        ParentLOSRef parentRef = referencedLos.getParent();
        LOS los = this.dataQueryService.getLos(parentRef.getId());
        if (los instanceof ParentLOS) {
            this.handleParentLOSReferenceRemoval((ParentLOS)los, ao, false);
        } 
        
    }

    private void handleHigherEdLOSsReferenceRemoval(ApplicationOption ao) {
        
        for (HigherEducationLOSRef curEdRef : ao.getHigherEdLOSRefs()) {
            LOS los = this.dataQueryService.getLos(curEdRef.getId());
            if (los != null && los instanceof HigherEducationLOS) {
                HigherEducationLOS higherEd = (HigherEducationLOS)los;
                if (!hasOtherApplicationOptions(higherEd, ao)) {
                    removeReferencesFromParents(higherEd);
                    removeReferencesFromChildren(higherEd);
                    this.dataUpdateService.deleteLos(higherEd);
                } else {
                    this.dataUpdateService.save(higherEd);
                }
                
            }
        }
        
    }

    private boolean hasOtherApplicationOptions(
            HigherEducationLOS los, ApplicationOption ao) {
        List<ApplicationOption> remainingAos = new ArrayList<ApplicationOption>();
        for (ApplicationOption curAo : los.getApplicationOptions()) {
            if (!curAo.getId().equals(ao.getId())) {
                remainingAos.add(curAo);
            }
        }
        if (!remainingAos.isEmpty()) {
            los.setApplicationOptions(remainingAos);
            return true;
        }
        return false;
    }

    private void removeReferencesFromChildren(HigherEducationLOS higherEd) {
        if (higherEd.getChildren() != null) {
            for (HigherEducationLOS curChild : higherEd.getChildren()) {
                List<HigherEducationLOS> remainingParents = new ArrayList<HigherEducationLOS>();
                for (HigherEducationLOS curParent : curChild.getParents()) {
                    if (!curParent.getId().equals(higherEd.getId())) {
                        remainingParents.add(curParent);
                    }
                }
                curChild.setParents(remainingParents);
                this.dataUpdateService.save(curChild);
            }
        }
        
    }

    private void removeReferencesFromParents(HigherEducationLOS higherEd) {
        if (higherEd.getParents() != null) {
            
            for (HigherEducationLOS curParent : higherEd.getParents()) {
                List<HigherEducationLOS> remainingChildren = new ArrayList<HigherEducationLOS>();
                for (HigherEducationLOS curChild : curParent.getChildren()) {
                    if (!curChild.getId().equals(higherEd.getId())) {
                        remainingChildren.add(curChild);
                    }
                }
                curParent.setChildren(remainingChildren);
                this.dataUpdateService.save(curParent);
            }
        }
        
    }

    private void handleSpecialLOSReferenceRemoval(SpecialLOS los,
            ApplicationOption ao, boolean withAoReferenceRemoval) {
        
        Map<String,ChildLOIRef> childLoiMap = constructChildLoiMap(ao.getChildLOIRefs());
        
        List<String> matchedLoiIds = new ArrayList<String>();
        
        List<ChildLOI> remainingChildLOIs = new ArrayList<ChildLOI>();
        for (ChildLOI curChildLoi : los.getLois()) {
            if (!childLoiMap.containsKey(curChildLoi.getId()) || hasOtherAoReferences(curChildLoi, ao)) {
                remainingChildLOIs.add(curChildLoi);
            } else {
                matchedLoiIds.add(curChildLoi.getId());
            }
        }
        if (withAoReferenceRemoval) { 
            for (String curLoiId : matchedLoiIds) {
                childLoiMap.remove(curLoiId);
            }
        
            if (!childLoiMap.isEmpty()) {
                ao.setChildLOIRefs(new ArrayList<ChildLOIRef>(childLoiMap.values()));
            } else {
                ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
            }
        
            ao.setParent(null);
        }
        
        if (!remainingChildLOIs.isEmpty()) {
            los.setLois(remainingChildLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.dataUpdateService.deleteLos(los);
        }
        
    }

    private void handleParentLOSReferenceRemoval(ParentLOS los,
            ApplicationOption ao, boolean withAoReferenceRemoval) {
        
        Map<String,ChildLOIRef> childLoiMap = constructChildLoiMap(ao.getChildLOIRefs());
        
        List<String> matchedLoiIds = new ArrayList<String>();
        
        List<ChildLOS> remainingChildren = new ArrayList<ChildLOS>();
        
        for (ChildLOS curChildLos : los.getChildren()) {
            List<ChildLOI> remainingChildLOIs = new ArrayList<ChildLOI>();
            for (ChildLOI curChildLoi : curChildLos.getLois()) {
                if (!childLoiMap.containsKey(curChildLoi.getId()) || hasOtherAoReferences(curChildLoi, ao)) {
                    remainingChildLOIs.add(curChildLoi);
                } else {
                    matchedLoiIds.add(curChildLoi.getId());
                }
            }
            if (!remainingChildLOIs.isEmpty()) {
                curChildLos.setLois(remainingChildLOIs);
                remainingChildren.add(curChildLos);
            } else {
                this.dataUpdateService.deleteLos(curChildLos);
            }
            
        }
        if (withAoReferenceRemoval) {
            for (String curLoiId : matchedLoiIds) {
                childLoiMap.remove(curLoiId);
            }
        
            if (!childLoiMap.isEmpty()) {
                ao.setChildLOIRefs(new ArrayList<ChildLOIRef>(childLoiMap.values()));
            } else {
                ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
            }
        
            ao.setParent(null);
        }
        
        
        if (remainingChildren.isEmpty()) {
            this.dataUpdateService.deleteLos(los);
        } else {
            los.setChildren(remainingChildren);
            this.dataUpdateService.save(los);
        }
        
        
    }

    private boolean hasOtherAoReferences(ChildLOI curChildLoi,
            ApplicationOption ao) {
        List<ApplicationOption> remainingAos = new ArrayList<ApplicationOption>();
        for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
            if (!curAo.getId().equals(ao.getId())) {
                remainingAos.add(curAo);
            }
        }
        if (!remainingAos.isEmpty()) {
            curChildLoi.setApplicationOptions(remainingAos);
            return true;
        }
        return false;
    }

    private Map<String, ChildLOIRef> constructChildLoiMap(
            List<ChildLOIRef> childLOIRefs) {
        Map<String,ChildLOIRef> childLoiMap = new HashMap<String,ChildLOIRef>();
        for (ChildLOIRef curChild : childLOIRefs) {
            childLoiMap.put(curChild.getId(), curChild);
        }
        return childLoiMap;
    }

    private void handleLoiRemoval(String loiId, String aoId) {
        
        //this.dataQueryService.getC
        
    }

    private Map<String, List<String>> listChangedLearningOpportunities(long updatePeriod) {
        Map<String, List<String>> changemap = this.tarjontaRawService.listModifiedLearningOpportunities(updatePeriod);
        LOG.debug("Tarjonta called");
        
        LOG.debug("Number of changes: " + changemap.size());
        
        for (Entry<String, List<String>> curEntry : changemap.entrySet()) {
            LOG.debug(curEntry.getKey() + ", " + curEntry.getValue());
        }
        
        /*
        for (String curLoi : changemap.get("koulutusmoduuliToteutus")) {
            LOG.debug("current loi: " + curLoi);
            KomotoDTO childKomoto = tarjontaRawService.getKomoto(curLoi);
            
            
            
        }*/
        
        
        
        return changemap;
    }

}
