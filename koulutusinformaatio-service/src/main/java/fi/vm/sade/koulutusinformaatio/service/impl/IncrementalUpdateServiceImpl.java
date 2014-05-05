package fi.vm.sade.koulutusinformaatio.service.impl;

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
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
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
    
    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
                                         UpdateService updateService, 
                                         TransactionManager transactionManager,
                                         EducationDataQueryService dataQueryService) {
        this.tarjontaRawService = tarjontaRawService;
        this.updateService = updateService;
        this.transactionManager = transactionManager;
        this.dataQueryService = dataQueryService;
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
            //ao.get
            
            /*
            for (ChildLOIRef curLoiRef : ao.getChildLOIRefs()) {
                handleLoiRemoval(curLoiRef.getId(), oid);
            }*/
            
            ParentLOSRef parentRef = ao.getParent();
            
            
            
            
        } catch (ResourceNotFoundException notFoundEx) {
            LOG.debug(notFoundEx.getMessage());
        }
        
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
