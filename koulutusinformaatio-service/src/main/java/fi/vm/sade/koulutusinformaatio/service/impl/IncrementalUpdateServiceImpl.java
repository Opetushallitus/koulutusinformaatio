package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import fi.vm.sade.koulutusinformaatio.domain.BasicLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOIObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

@Service
@Profile("default")
public class IncrementalUpdateServiceImpl implements IncrementalUpdateService {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalUpdateServiceImpl.class);
    
    private TarjontaRawService tarjontaRawService;
    private UpdateService updateService;
    private TransactionManager transactionManager;
    private EducationIncrementalDataQueryService dataQueryService;
    private EducationDataUpdateService dataUpdateService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaService tarjontaService;
    
    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
                                         UpdateService updateService, 
                                         TransactionManager transactionManager,
                                         EducationIncrementalDataQueryService dataQueryService,
                                         EducationDataUpdateService dataUpdateService,
                                         KoodistoService koodistoService,
                                         ProviderService providerService,
                                         TarjontaService tarjontaService) {
        this.tarjontaRawService = tarjontaRawService;
        this.updateService = updateService;
        this.transactionManager = transactionManager;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaService = tarjontaService;
    }
    
    @Override
    @Async
    public void updateChangedEducationData() throws Exception {
        LOG.info("updateChangedEducationData on its way");
        
        //Getting get update period
        long updatePeriod = getUpdatePeriod();
        
        //Fetching changes within the update period
        Map<String,List<String>> result = listChangedLearningOpportunities(updatePeriod);
        
        //If there are changes in komo-data, a full update is performed
        if ((result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) || updatePeriod == 0) {
            LOG.warn(String.format("Starting full update instead of incremental. Update period was: %s", updatePeriod));
            updateService.updateAllEducationData();
        //Otherwise doing incremental indexing    
        } else {
            LOG.debug("Starting incremental update");
            this.updateService.setRunning(true);
            this.updateService.setRunningSince(System.currentTimeMillis());
            this.transactionManager.beginIncrementalTransaction();
        }
        
        //If changes in haku objects indexing them
        if (result.containsKey("haku")) {
            
            for (String curOid : result.get("haku")) {
                indexApplicationSystemData(curOid);
            }
        //If changes in hakukohde, indexing them    
        } else if (result.containsKey("hakukohde")) {
            
            for (String curOid : result.get("hakukohde")) {
                
                HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid);
                HakuDTO asDto = this.tarjontaRawService.getHaku(aoDto.getHakuOid());
                indexApplicationOptionData(aoDto, asDto);
            }
         //If changes in koulutusmoduuliToteutus, indexing them   
        } else if (result.containsKey("koulutusmoduuliToteutus")) {
            for (String curOid : result.get("koulutusmoduuliToteutus")) {
                indexLoiData(curOid);
            }
        }
        
        dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - this.updateService.getRunningSince(), "SUCCESS"));
        this.transactionManager.commitIncrementalTransaction();
        this.updateService.setRunning(false);
        this.updateService.setRunningSince(0);
    }


    //Indexes changed loi data
    private void indexLoiData(String komotoOid) {
        
        KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        if (!komotoDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
            handleLoiRemoval(komotoDto);
        } else {
           handleLoiAdditionOrUpdate(komotoDto);
        }
        
    }
    
    private void handleLoiAdditionOrUpdate(KomotoDTO komotoDto) {
       
        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        //Loi is in mongo, thus it is updated
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            this.handleLoiRemoval(komotoDto);
            this.handleLoiAdditionOrUpdate(komotoDto);
        //Loi is not in mongo, so it is added    
        } else {
            this.handleLoiAdditionOrUpdate(komotoDto);
        }
        
    }

    //Handles removal of loi
    private void handleLoiRemoval(KomotoDTO komotoDto) {


        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            for (LOS referencedLos : referencedLoss) {
                if (referencedLos instanceof ParentLOS) {
                    this.handleLoiRemovalFromParentLOS((ParentLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof SpecialLOS) {
                    this.handleLoiRemovalFromSpecialLOS((SpecialLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof ChildLOS) {
                    handleLoiRemovalFromChildLos((ChildLOS)referencedLos, komotoDto.getOid());      
                } else if (referencedLos instanceof UpperSecondaryLOS) {
                    handleLoiRemovalFromUpperSecondarLos((UpperSecondaryLOS)referencedLos, komotoDto.getOid());
                }

            }

        } 

    }
    
    private void handleLoiRemovalFromSpecialLOS(SpecialLOS los, String komotoOid) {
        
        List<ChildLOI> remainingChildLOIs = new ArrayList<ChildLOI>();
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        for (ChildLOI curChildLoi : los.getLois()) {
            
            if (!curChildLoi.equals(komotoOid)) {
                remainingChildLOIs.add(curChildLoi);
            } else {
                aosToRemove.addAll(handleAoReferenceRemovals(curChildLoi));
            }
        }

        if (!remainingChildLOIs.isEmpty()) {
            los.setLois(remainingChildLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.dataUpdateService.deleteLos(los);
        }
        
        for(ApplicationOption curAo : aosToRemove) {
            this.dataUpdateService.deleteAo(curAo);
        }
        
    }
    
    private List<ApplicationOption> handleAoReferenceRemovals(ChildLOI curChildLoi) {
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        if (curChildLoi.getApplicationOptions() != null && !curChildLoi.getApplicationOptions().isEmpty()) {
            
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (!hasOtherLoiReferences(curAo, curChildLoi.getId())) {
                    aosToRemove.add(curAo);
                }
            }
        }
        
        return aosToRemove;
        
    }
    
    private List<ApplicationOption> handleAoReferenceRemovals(UpperSecondaryLOI curChildLoi) {
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        if (curChildLoi.getApplicationOptions() != null && !curChildLoi.getApplicationOptions().isEmpty()) {
            
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (!hasOtherLoiReferences(curAo, curChildLoi.getId())) {
                    aosToRemove.add(curAo);
                }
            }
        }
        
        return aosToRemove;
        
    }

    private boolean hasOtherLoiReferences(ApplicationOption curAo, String id) {
        List<ChildLOIRef> remainingLois = new ArrayList<ChildLOIRef>();
        for (ChildLOIRef curChild : curAo.getChildLOIRefs()) {
            if (!curChild.getId().equals(id)) {
                remainingLois.add(curChild);
            }
        }
        if (remainingLois.isEmpty()) {
            return false;
        } else {
            curAo.setChildLOIRefs(remainingLois);
            return true;
        }
        
        
        
    }

    private void handleLoiRemovalFromParentLOS(ParentLOS los, String komotoOid) {
         
        List<ChildLOS> remainingChildren = new ArrayList<ChildLOS>();
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        
        for (ChildLOS curChildLos : los.getChildren()) {
            List<ChildLOI> remainingChildLOIs = new ArrayList<ChildLOI>();
            for (ChildLOI curChildLoi : curChildLos.getLois()) {
                if (!curChildLoi.getId().equals(komotoOid)) {
                    remainingChildLOIs.add(curChildLoi);
                } else {
                    aosToRemove.addAll(handleAoReferenceRemovals(curChildLoi)); 
                    
                }
            }
            if (!remainingChildLOIs.isEmpty()) {
                curChildLos.setLois(remainingChildLOIs);
                remainingChildren.add(curChildLos);
            } else {
                this.dataUpdateService.deleteLos(curChildLos);
            }
            
        }
        
        if (remainingChildren.isEmpty()) {
            this.dataUpdateService.deleteLos(los);
        } else {
            los.setChildren(remainingChildren);
            this.dataUpdateService.save(los);
        }
                
        for(ApplicationOption curAo : aosToRemove) {
            this.dataUpdateService.deleteAo(curAo);
        }
        
    }
    
    private void handleLoiRemovalFromChildLos(ChildLOS referencedLos, String komotoOid) {
        
        ParentLOSRef parentRef = referencedLos.getParent();
        LOS los = this.dataQueryService.getLos(parentRef.getId());
        if (los instanceof ParentLOS) {
            this.handleLoiRemovalFromParentLOS((ParentLOS)los, komotoOid);
        } else {
            LOG.warn("Child los has reference to parent that is not of type ParentLOS.");
        }
        
    }
    
    private void handleLoiRemovalFromUpperSecondarLos(UpperSecondaryLOS los, String komotoOid) {
        
        //Map<String,ChildLOIRef> loiMap = constructChildLoiMap(ao.getChildLOIRefs());
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        List<UpperSecondaryLOI> remainingLOIs = new ArrayList<UpperSecondaryLOI>();
        for (UpperSecondaryLOI curLoi : los.getLois()) {
            if (curLoi.getId().equals(komotoOid)) {
                remainingLOIs.add(curLoi);
            } else {
                aosToRemove.addAll(handleAoReferenceRemovals(curLoi));
            }
        }
        
        
        if (!remainingLOIs.isEmpty()) {
            los.setLois(remainingLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.dataUpdateService.deleteLos(los);
        }
        
        for (ApplicationOption curAo : aosToRemove) {
            this.dataUpdateService.deleteAo(curAo);
        }
        
    }

    //Indexing of based on changes in application systems
    private void indexApplicationSystemData(String asOid) {

            indexSecondaryEducationAsData(asOid);
        
    }

    private void indexSecondaryEducationAsData(String asOid) {
        HakuDTO asDto = this.tarjontaRawService.getHaku(asOid);
            
            //Indexing application options connected to the changed application system
            List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asOid);
            if (hakukohdeOids != null && hakukohdeOids.isEmpty()) {
                for (OidRDTO curOid : hakukohdeOids) {
                    HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid.getOid());
                    indexApplicationOptionData(aoDto, asDto);
                }
            }
    }

    
    private void indexApplicationOptionData(HakukohdeDTO aoDto, HakuDTO asDto) {
        
        if (!TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila())) {
            removeApplicationOption(aoDto.getOid());
        } else {
            try {
                ApplicationOption ao = this.dataQueryService.getApplicationOption(aoDto.getOid());
                updateApplicationOption(ao, aoDto);
            } catch (ResourceNotFoundException ex) {
                LOG.debug(ex.getMessage());
                addApplicationOption(aoDto);
            }
        }
        
        
    }
    
    private void updateApplicationOption(ApplicationOption ao, HakukohdeDTO aoDto) {
        
        removeApplicationOption(ao.getId());
        addApplicationOption(aoDto);
        
        
    }

    //when adding an application option, the lois that are connected to it
    //need to be added or updated.
    private void addApplicationOption(HakukohdeDTO aoDto) {


        try {

            List<String> koulutusOids = aoDto.getHakukohdeKoulutusOids();
            for (String curKoulutusOid : koulutusOids) {
                
                KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(curKoulutusOid);

                //if komoto (loi) is in state published it needs to be added or updated
                if (TarjontaConstants.STATE_PUBLISHED.equals(komotoDto.getTila())) {
                    List<LOS> losses = this.dataQueryService.findLearningOpportunitiesByLoiId(curKoulutusOid);
                    //If loi is found, it is updated with the new ao data
                    if (losses != null && !losses.isEmpty()) {
                        for (LOS curLos : losses) {
                            if (curLos instanceof ChildLOS) {
                                updateChildLosRefForAo(aoDto, komotoDto, (ChildLOS)curLos);
                            } else if (curLos instanceof SpecialLOS) {
                                updateSpecialLosReferencesForAo(aoDto, komotoDto, (SpecialLOS)curLos);
                            } else if (curLos instanceof UpperSecondaryLOS) {
                                updateUpperSecondaryLosReferencesForAo(aoDto, komotoDto, (UpperSecondaryLOS)curLos);
                            } 
                        }
                    //If loi is not found it needs to be added
                    } else {
                        handleSecondaryLoiAddition(komotoDto, aoDto);
                    }
                }

            }
        }
        catch (Exception ex) {
            LOG.warn(String.format("Problem indexing application option: %s. %s", aoDto.getOid(), ex.getMessage()));
        }

    }

    private void updateUpperSecondaryLosReferencesForAo(HakukohdeDTO aoDto,
            KomotoDTO komotoDto, UpperSecondaryLOS los) throws KoodistoException {
        
        try {
            
            LOIObjectCreator loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);
            
            List<UpperSecondaryLOI> tempLois = new ArrayList<UpperSecondaryLOI>();
            for (UpperSecondaryLOI curLoi : los.getLois()) {
                if (!curLoi.getId().equals(komotoDto.getOid())) {
                    tempLois.add(curLoi);
                }
            }
            
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];
            
            UpperSecondaryLOI updatedLoi = loiCreator.createUpperSecondaryLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
            if (updatedLoi != null) {
                tempLois.add(updatedLoi);
            }
            this.dataUpdateService.save(los);
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem indexing incrementally ao: %s with loi: %s. %s",  aoDto.getOid(), komotoDto.getOid(), ex.getMessage()));
        }
        
        
    }

    private void updateSpecialLosReferencesForAo(HakukohdeDTO aoDto,
            KomotoDTO komotoDto, SpecialLOS los) throws KoodistoException {

        try {
            LOIObjectCreator loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);

            List<ChildLOI> tempLois = new ArrayList<ChildLOI>();
            for (ChildLOI curLoi : los.getLois()) {
                if (!curLoi.getId().equals(komotoDto.getOid())) {
                    tempLois.add(curLoi);
                }
            }
            
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];

            ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
            if (childLoi != null) {
                tempLois.add(childLoi);
            }
            
            
            this.dataUpdateService.save(los);
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem indexing incrementally ao: %s with loi: %s. %s",  aoDto.getOid(), komotoDto.getOid(), ex.getMessage()));
        }   
    }

    private void handleSecondaryLoiAddition(KomotoDTO komotoDto, HakukohdeDTO aoDto) throws KoodistoException {

        try {

            LOIObjectCreator loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);

            String providerOid = komotoDto.getTarjoajaOid();
            String komoOid = komotoDto.getKomoOid();
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];

            String basicLosId = String.format("%s_%s", komoOid, providerOid);

            LOS los = this.dataQueryService.getLos(basicLosId);

            if (los != null && los instanceof ChildLOS) {

                
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
                if (childLoi != null) {
                    ((ChildLOS)los).getLois().add(childLoi);
                }

            } else if (los != null && los instanceof UpperSecondaryLOS) {
                
                UpperSecondaryLOI updatedLoi = loiCreator.createUpperSecondaryLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
                if (updatedLoi != null) {
                   ((UpperSecondaryLOS)los).getLois().add(updatedLoi);
                }

            } else if (los != null && los instanceof SpecialLOS) {
                
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
                if (childLoi != null) {
                    ((SpecialLOS)los).getLois().add(childLoi);
                }

            } else if (los == null) {
                los = this.dataQueryService.getLos(String.format("%s_%s", basicLosId, komotoDto.getOid()));
            }
            if (los != null && los instanceof SpecialLOS) {

                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
                if (childLoi != null) {
                    ((SpecialLOS)los).getLois().add(childLoi);
                }
                
            } else if (los == null) {
                //KomoDTO childKomo = this.tarjontaRawService.getKomo(komoOid);
                List<String> ylamoduulit = komo.getYlaModuulit();
                if (ylamoduulit != null && ylamoduulit.size() > 0) {
                    los = this.dataQueryService.getLos(String.format("%s_%s", ylamoduulit.get(0), providerOid));
                }

            }

            if (los != null && los instanceof ParentLOS) {
                
                LOSObjectCreator losCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
                
                List<KomotoDTO> childKomotos = Arrays.asList(komotoDto);
                ChildLOS childLos = losCreator.createChildLOS(komo, basicLosId, childKomotos);
                ((ParentLOS)los).getChildren().add(childLos);

            } 
            //If there was an existing los for the created loi, the los is saved
            if (los != null) {
                this.dataUpdateService.save(los);
                
            //Otherwise the los is created    
            } else {
                String educationType = komo.getKoulutusTyyppiUri();
                if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) &&
                        komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
                    komoOid = komo.getYlaModuulit().get(0);
                }
                
               List<LOS> newLosses = this.tarjontaService.findParentLearningOpportunity(komoOid);
               for (LOS curLos : newLosses) {
                   this.dataUpdateService.save(curLos);
               }
            }
            
            
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem indexing incrementally ao: %s with loi: %s. %s",  aoDto.getOid(), komotoDto.getOid(), ex.getMessage()));
        }

    }

    private void updateChildLosRefForAo(HakukohdeDTO aoDto,
            KomotoDTO komotoDto, ChildLOS los) throws KoodistoException {

        try {

            LOIObjectCreator loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);
            
            String komoOid = komotoDto.getKomoOid();
            
           

            List<ChildLOI> tempLois = new ArrayList<ChildLOI>();
            for (ChildLOI curLoi : los.getLois()) {
                if (!curLoi.getId().equals(komotoDto.getOid())) {
                    tempLois.add(curLoi);
                }
            }

            ParentLOSRef parentRef = los.getParent();

            LOS parentLos = this.dataQueryService.getLos(parentRef.getId());

            if (parentLos instanceof ParentLOS) { 
                KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);
                String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi);
                if (childLoi != null) {
                    tempLois.add(childLoi);
                }
            }
            
            los.setLois(tempLois);
            
            this.dataUpdateService.save(los);
            
            
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem indexing incrementally ao: %s with loi: %s. %s",  aoDto.getOid(), komotoDto.getOid(), ex.getMessage()));
        }

    }

    //Handling removal of application option and related data
    private void removeApplicationOption(String oid) {
        
        try {
            
            //Getting ao from mongo
            ApplicationOption ao = this.dataQueryService.getApplicationOption(oid);
            
            //Getting parent ref
            ParentLOSRef parentRef = ao.getParent();
            
            //If there is a parent ref, handling removal of parent data
            if (parentRef != null && parentRef.getId() != null) {
                LOS los  = this.dataQueryService.getLos(parentRef.getId());
            
                if (los != null && los instanceof ParentLOS) {
                    handleParentLOSReferenceRemoval((ParentLOS)los, ao, true);
                } else if (los != null && los instanceof SpecialLOS) {
                    handleSpecialLOSReferenceRemoval((SpecialLOS)los, ao, true);
                } 
            }
            //If there is a higher education los ref, then handling removal of higher education data
            if (ao.getHigherEdLOSRefs() != null 
                    && !ao.getHigherEdLOSRefs().isEmpty()) {
                handleHigherEdLOSsReferenceRemoval(ao);
            }
            
            //If there are child loi refs, handling removal based on child references
            if (ao.getChildLOIRefs() != null
                    && !ao.getChildLOIRefs().isEmpty()) {
                handleLoiRefRemoval(ao);
            }
            
            //In the end deleting the ao itself
            this.dataUpdateService.deleteAo(ao);
            
            
        //If application option is not in mongo, nothing needs to be done    
        } catch (ResourceNotFoundException notFoundEx) {
            LOG.debug(notFoundEx.getMessage());
        }
        
    }

    //Removal based on child loi references
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
                handleUpperSecondarLosReferenceRemoval((UpperSecondaryLOS)referencedLos, ao);
            }
            
        }
        
    }

    private void handleUpperSecondarLosReferenceRemoval(
            UpperSecondaryLOS los, ApplicationOption ao) {
        
        Map<String,ChildLOIRef> loiMap = constructChildLoiMap(ao.getChildLOIRefs());
        
        List<UpperSecondaryLOI> remainingLOIs = new ArrayList<UpperSecondaryLOI>();
        for (UpperSecondaryLOI curLoi : los.getLois()) {
            if (!loiMap.containsKey(curLoi.getId()) || hasOtherAoReferences(curLoi, ao)) {
                remainingLOIs.add(curLoi);
            } 
        }
        
        
        if (!remainingLOIs.isEmpty()) {
            los.setLois(remainingLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.dataUpdateService.deleteLos(los);
        }
        
    }

    private void handleChildLosReferenceRemoval(ChildLOS referencedLos,
            ApplicationOption ao) {
        
        ParentLOSRef parentRef = referencedLos.getParent();
        LOS los = this.dataQueryService.getLos(parentRef.getId());
        if (los instanceof ParentLOS) {
            this.handleParentLOSReferenceRemoval((ParentLOS)los, ao, false);
        } else {
            LOG.warn("Child los has reference to parent that is not of type ParentLOS.");
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

    private boolean hasOtherAoReferences(BasicLOI curChildLoi,
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
    
    
    private long getUpdatePeriod() {
        DataStatus status = this.dataQueryService.getLatestDataStatus();
        if (status != null) {
            long period = (System.currentTimeMillis() - status.getLastUpdateFinished().getTime()) + status.getLastUpdateDuration();
            return period;
        }
        return 0;
    }

}
