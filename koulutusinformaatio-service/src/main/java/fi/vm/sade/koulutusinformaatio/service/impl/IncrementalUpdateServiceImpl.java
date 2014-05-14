package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
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
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
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
    private EducationDataQueryService prodDataQueryService;
    private EducationDataUpdateService dataUpdateService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    Map<String,LOS> lossToRemove;
    Map<String,LOS> lossToUpdate;
    
    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;
    
    private final HttpSolrServer locationHttpSolrServer;
    
    private static final int REMOVAL = 0;
    private static final int UPDATE = 1;
    private static final int ADDITION = 2;
    
    
    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
                                         UpdateService updateService, 
                                         TransactionManager transactionManager,
                                         EducationIncrementalDataQueryService dataQueryService,
                                         EducationDataQueryService prodDataQueryService,
                                         EducationDataUpdateService dataUpdateService,
                                         KoodistoService koodistoService,
                                         ProviderService providerService,
                                         TarjontaService tarjontaService,
                                         IndexerService indexerService,
                                         @Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
                                         @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
                                         @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer) {
        this.tarjontaRawService = tarjontaRawService;
        this.updateService = updateService;
        this.transactionManager = transactionManager;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loAliasSolrServer;
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;
        this.prodDataQueryService = prodDataQueryService;
    }
    
    @Override
    @Async
    public void updateChangedEducationData() throws Exception {
        LOG.info("updateChangedEducationData on its way");
        
        //Getting get update period
        long updatePeriod = getUpdatePeriod();
        
        LOG.debug(String.format("Update period: %s", updatePeriod));
        
        try {
            
        lossToRemove = new HashMap<String,LOS>();
        lossToUpdate = new HashMap<String,LOS>();
        
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
          //If changes in haku objects indexing them
            if (result.containsKey("haku")) {
                LOG.debug("Haku changes: " + result.get("haku").size());
                
                for (String curOid : result.get("haku")) {
                    LOG.debug("Changed haku: " + curOid);
                    indexApplicationSystemData(curOid);
                }
             
            }
          //If changes in hakukohde, indexing them   
            if (result.containsKey("hakukohde")) {
                LOG.debug("Haku changes: " + result.get("hakukohde").size());
                for (String curOid : result.get("hakukohde")) {
                    LOG.debug("Changed hakukohde: " + curOid);
                    HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid);
                    HakuDTO asDto = this.tarjontaRawService.getHaku(aoDto.getHakuOid());
                    indexApplicationOptionData(aoDto, asDto);
                }
               
            }
          //If changes in koulutusmoduuliToteutus, indexing them 
            if (result.containsKey("koulutusmoduuliToteutus")) {
                LOG.debug("Changed komotos: " + result.get("koulutusmoduuliToteutus").size());
                for (String curOid : result.get("koulutusmoduuliToteutus")) {
                    LOG.debug("Changed komoto: " + curOid);
                    indexLoiData(curOid);
                }
            }
            
            LOG.debug("Losses to remove in solr: " + this.lossToRemove.size());
            LOG.debug("Losses to update in solr: " + this.lossToUpdate.size());
            
            for (LOS curLos : this.lossToUpdate.values()) {
                LOG.debug("Indexing to update: " + curLos.getId());
                this.indexerService.addLearningOpportunitySpecification(curLos, loHttpSolrServer, lopHttpSolrServer);
            }
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, false);
            for (LOS curLos : this.lossToRemove.values()) {
                LOG.debug("Indexing to remove: " + curLos.getId());
                this.indexerService.removeLos(curLos, loHttpSolrServer);
            }
            
            LOG.debug("Committing to solr");
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            LOG.debug("Saving successful status");
            this.transactionManager.commitIncrementalTransaction();
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - this.updateService.getRunningSince(), "SUCCESS"));
            LOG.debug("Committing.");
            this.updateService.setRunning(false);
            this.updateService.setRunningSince(0);
        }
        
        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - this.updateService.getRunningSince(), String.format("FAIL: %s", e.getMessage())));
        }
    }


    //Indexes changed loi data
    private void indexLoiData(String komotoOid) throws KoodistoException {
        LOG.debug(String.format("Indexing loi: %s", komotoOid));
        KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        LOG.debug(String.format("Loi: %s, status: %s", komotoOid, komotoDto.getTila()));
        if (!(komotoDto.getTila().name().equals(TarjontaConstants.STATE_PUBLISHED))) {
            LOG.debug(String.format("Loi need to be removed: %s", komotoOid));
            handleLoiRemoval(komotoDto);
        } else {
            LOG.debug(String.format("Loi need to be indexed: %s", komotoOid));
           handleLoiAdditionOrUpdate(komotoDto);
        }
        
    }
    
    private void handleLoiAdditionOrUpdate(KomotoDTO komotoDto) throws KoodistoException {
        LOG.debug(String.format("Handling addition or update: %s", komotoDto.getOid()));
        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        //Loi is in mongo, thus it is updated
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            LOG.debug(String.format("Loi is in mongo, it should be updated: %s", komotoDto.getOid()));
            handleLoiRemoval(komotoDto);
            handleSecondaryLoiAddition(komotoDto);
        //Loi is not in mongo, so it is added    
        } else {
            LOG.debug(String.format("Loi is not in mongo, it should be added: %s", komotoDto.getOid()));
            handleSecondaryLoiAddition(komotoDto);;
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
            this.updateSolrMaps(los, UPDATE);
            los.setLois(remainingChildLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.updateSolrMaps(los, REMOVAL);
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
         LOG.debug("handling removal from parent los");
        List<ChildLOS> remainingChildren = new ArrayList<ChildLOS>();
        List<ApplicationOption> aosToRemove = new ArrayList<ApplicationOption>();
        
        for (ChildLOS curChildLos : los.getChildren()) {
            LOG.debug("curChildLos: " + curChildLos.getId());
            List<ChildLOI> remainingChildLOIs = new ArrayList<ChildLOI>();
            for (ChildLOI curChildLoi : curChildLos.getLois()) {
                if (!curChildLoi.getId().equals(komotoOid)) {
                    LOG.debug("Adding childLoi: " + curChildLoi.getId() + " to curChildLos: " + curChildLos.getId());
                    remainingChildLOIs.add(curChildLoi);
                } else {
                    LOG.debug("Not adding childLoi: " + curChildLoi.getId() + " to curChildLos: " + curChildLos.getId());
                    aosToRemove.addAll(handleAoReferenceRemovals(curChildLoi)); 
                    
                }
            }
            if (!remainingChildLOIs.isEmpty()) {
                LOG.debug("curChildLos is retained: " + curChildLos.getId());
                curChildLos.setLois(remainingChildLOIs);
                remainingChildren.add(curChildLos);
            } else {
                LOG.debug("curChildLos is removed: " + curChildLos.getId());
                this.dataUpdateService.deleteLos(curChildLos);
            }
            
        }
        
        if (remainingChildren.isEmpty()) {
            this.updateSolrMaps(los, REMOVAL);
            this.dataUpdateService.deleteLos(los);
        } else {
            
            
            
            los.setChildren(remainingChildren);
            this.updateSolrMaps(los, UPDATE);
            LOG.debug("Deleting parent los");
            this.dataUpdateService.deleteLos(los);
            LOG.debug("Saving parent los");
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
            this.updateSolrMaps(los, UPDATE);
            los.setLois(remainingLOIs);
            this.dataUpdateService.save(los);
        } else {
            this.updateSolrMaps(los, REMOVAL);
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
                            this.updateSolrMaps(curLos, ADDITION);
                        }
                        
                    //If loi is not found it needs to be added
                    } else {
                        handleSecondaryLoiAddition(komotoDto);
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
            
            UpperSecondaryLOI updatedLoi = loiCreator.createUpperSecondaryLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, SolrConstants.ED_TYPE_LUKIO_SHORT);
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
            LOSObjectCreator losCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);

            List<ChildLOI> tempLois = new ArrayList<ChildLOI>();
            for (ChildLOI curLoi : los.getLois()) {
                if (!curLoi.getId().equals(komotoDto.getOid())) {
                    tempLois.add(curLoi);
                }
            }
            
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];

            ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, losCreator.resolveEducationType(los));
            if (childLoi != null) {
                tempLois.add(childLoi);
            }
            
            
            this.dataUpdateService.save(los);
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem indexing incrementally ao: %s with loi: %s. %s",  aoDto.getOid(), komotoDto.getOid(), ex.getMessage()));
        }   
    }

    private void handleSecondaryLoiAddition(KomotoDTO komotoDto) throws KoodistoException {
        LOG.debug(String.format("Adding loi: %s", komotoDto.getOid()));
        try {

            LOIObjectCreator loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);
            LOSObjectCreator losCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);

            String providerOid = komotoDto.getTarjoajaOid();
            String komoOid = komotoDto.getKomoOid();
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String koulutuskoodi = komo.getKoulutusKoodiUri().split("#")[0];

            String basicLosId = String.format("%s_%s", komoOid, providerOid);

            LOS los = this.dataQueryService.getLos(basicLosId);

            if (los != null && los instanceof ChildLOS) {

                LOG.debug(String.format("A suitable child los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, SolrConstants.ED_TYPE_AMMATILLINEN_SHORT);
                
                if (childLoi != null) {
                    
                    List<ChildLOI> otherLois = new ArrayList<ChildLOI>();
                    for (ChildLOI curLoi : ((ChildLOS)los).getLois()) {
                        if (curLoi.getId().equals(childLoi.getId())) {
                            otherLois.add(curLoi);
                        }
                    }
                    
                    otherLois.add(childLoi);
                    
                    ((ChildLOS)los).setLois(otherLois);
                }

            } else if (los != null && los instanceof UpperSecondaryLOS) {
                
                LOG.debug(String.format("A suitable upper secondary los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                
                UpperSecondaryLOI updatedLoi = loiCreator.createUpperSecondaryLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, SolrConstants.ED_TYPE_LUKIO_SHORT);
                if (updatedLoi != null) {
                   ((UpperSecondaryLOS)los).getLois().add(updatedLoi);
                }

            } else if (los != null && los instanceof SpecialLOS) {
                
                LOG.debug(String.format("A suitable special los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, losCreator.resolveEducationType((SpecialLOS)los));
                if (childLoi != null) {
                    ((SpecialLOS)los).getLois().add(childLoi);
                }

            } else if (los == null) {
                los = this.dataQueryService.getLos(String.format("%s_%s", basicLosId, komotoDto.getOid()));
            }
            if (los != null && los instanceof SpecialLOS) {

                LOG.debug(String.format("A suitable joint or stuff special los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, losCreator.resolveEducationType((SpecialLOS)los));
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
                
                LOG.debug(String.format("A suitable parent los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                
                List<KomotoDTO> childKomotos = Arrays.asList(komotoDto);
                ChildLOS childLos = losCreator.createChildLOS(komo, basicLosId, childKomotos);
                ParentLOSRef parentRef = new ParentLOSRef();
                parentRef.setId(los.getId());
                parentRef.setName(los.getName());
                parentRef.setLosType(los.getType());
                childLos.setParent(parentRef);
                ((ParentLOS)los).getChildren().add(childLos);

            } 
            //If there was an existing los for the created loi, the los is saved
            if (los != null) {
                LOG.debug(String.format("Saving the updated los: %s", los.getId()));
                if (los instanceof ChildLOS) {
                    ParentLOSRef parentRef = ((ChildLOS) los).getParent();
                    ParentLOS parent = (ParentLOS)(this.dataQueryService.getLos(parentRef.getId()));
                    this.dataUpdateService.save(parent);
                    this.updateSolrMaps(parent, ADDITION);
                } else {
                    this.dataUpdateService.save(los);
                    this.updateSolrMaps(los, ADDITION);
                }
            //Otherwise the los is created    
            } else {
                LOG.debug(String.format("There was no los for komoto: %s, creating it", komotoDto.getOid()));
                String educationType = komo.getKoulutusTyyppiUri();
                if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) &&
                        komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
                    komoOid = komo.getYlaModuulit().get(0);
                }
                
               List<LOS> newLosses = this.tarjontaService.findParentLearningOpportunity(komoOid);
               LOG.debug(String.format("Created: %s loss", newLosses.size()));
               for (LOS curLos : newLosses) {
                   LOG.debug(String.format("current created los: %s", curLos.getId()));
                   if (this.dataQueryService.getLos(curLos.getId()) == null) {
                       LOG.debug(String.format("saving created los: %s", curLos.getId()));
                       this.dataUpdateService.save(curLos);
                       this.updateSolrMaps(curLos, ADDITION);
                   }
               }
            }
            
            
            
        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem adding loi incrementally with loi: %s. %s",  komotoDto.getOid(), ex.getMessage()));
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
                ChildLOI childLoi = loiCreator.createChildLOI(komotoDto, los.getId(), los.getName(), koulutuskoodi, SolrConstants.ED_TYPE_AMMATILLINEN_SHORT);
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
            /*if (ao.getHigherEdLOSRefs() != null 
                    && !ao.getHigherEdLOSRefs().isEmpty()) {
                handleHigherEdLOSsReferenceRemoval(ao);
            }*/
            
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
            updateSolrMaps(los, UPDATE);
        } else {
            updateSolrMaps(los, REMOVAL);
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
            updateSolrMaps(los, UPDATE);
        } else {
            updateSolrMaps(los, REMOVAL);
            this.dataUpdateService.deleteLos(los);
        }
        
    }
    
    private void updateSolrMaps(LOS los, int updateStatus) {
        if (UPDATE == updateStatus && !this.lossToRemove.containsKey(los.getId())) {
            LOG.debug("removing from solr index");
            this.lossToUpdate.put(los.getId(), los);
        } else if (REMOVAL == updateStatus) {
            LOG.debug("updating to solr index");
            this.lossToRemove.put(los.getId(), los);
            this.lossToUpdate.remove(los.getId());
        } else if (ADDITION == updateStatus) {
            LOG.debug("Adding to solr index");
            this.lossToUpdate.put(los.getId(), los);
            this.lossToRemove.remove(los.getId());
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
            updateSolrMaps(los, REMOVAL);
            this.dataUpdateService.deleteLos(los);
        } else {
            updateSolrMaps(los, UPDATE);
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
        DataStatus status = this.prodDataQueryService.getLatestSuccessDataStatus();
        if (status != null) {
            long period = (System.currentTimeMillis() - status.getLastUpdateFinished().getTime()) + status.getLastUpdateDuration();
            return period;
        }
        return 0;
    }

}
