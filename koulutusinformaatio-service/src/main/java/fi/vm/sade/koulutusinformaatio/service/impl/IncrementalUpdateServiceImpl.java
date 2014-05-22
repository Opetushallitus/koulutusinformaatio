package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
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
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.SingleParentLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.SingleSpecialLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.SingleUpperSecondaryLOSBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

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

    private LOSObjectCreator losCreator;
    private SingleParentLOSBuilder parentLosBuilder;
    private SingleSpecialLOSBuilder specialLosBuilder;
    private SingleUpperSecondaryLOSBuilder upperSecLosBuilder;


    Map<String,LOS> lossToRemove;
    Map<String,LOS> lossToUpdate;

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    private boolean higherEdReindexed = false;

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

        this.losCreator = new LOSObjectCreator(this.koodistoService, this.tarjontaRawService, this.providerService);
        this.parentLosBuilder = new SingleParentLOSBuilder(losCreator, tarjontaRawService);
        this.specialLosBuilder = new SingleSpecialLOSBuilder(losCreator, tarjontaRawService);
        this.upperSecLosBuilder = new SingleUpperSecondaryLOSBuilder(losCreator, tarjontaRawService);
    }

    @Override
    @Async
    public void updateChangedEducationData() throws Exception {
        
        if (this.updateService.isRunning()) {
            LOG.debug("Indexing is running, not starting");
            return;
        }
        
        LOG.info("updateChangedEducationData on its way");

        //Getting get update period
        long updatePeriod = getUpdatePeriod();

        LOG.debug(String.format("Update period: %s", updatePeriod));

        try {

            higherEdReindexed = false;
            lossToRemove = new HashMap<String,LOS>();
            lossToUpdate = new HashMap<String,LOS>();

            //Fetching changes within the update period
            Map<String,List<String>> result = listChangedLearningOpportunities(updatePeriod);


            LOG.debug("Starting incremental update");
            this.updateService.setRunning(true);
            this.updateService.setRunningSince(System.currentTimeMillis());
            this.transactionManager.beginIncrementalTransaction();
            
            //If there are changes in komo-data, a full update is performed
            if ((result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) || updatePeriod == 0) {
                LOG.warn(String.format("Komos changed. Update period was: %s", updatePeriod));
                
                for (String curKomoOid : result.get("koulutusmoduuli")) {
                    if (isHigherEdKomo(curKomoOid) && !higherEdReindexed) {
                        
                        reIndexHigherEducation();
                        higherEdReindexed = true;
                        
                        
                    }
                }
                
            } 

            //If changes in haku objects indexing them
            if (result.containsKey("haku")) {
                LOG.debug("Haku changes: " + result.get("haku").size());

                for (String curOid : result.get("haku")) {
                    LOG.debug("Changed haku: " + curOid);
                    indexApplicationSystemData(curOid);
                }

            }


            List<String> changedHakukohdeOids = new ArrayList<String>();

            //If changes in hakukohde, indexing them   
            if (result.containsKey("hakukohde")) {
                changedHakukohdeOids = result.get("hakukohde");
                LOG.debug("Haku changes: " + changedHakukohdeOids.size());

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
                    List<OidRDTO> aoOidDtos = this.tarjontaRawService.getHakukohdesByKomoto(curOid);
                    if (isLoiAlreadyHandled(aoOidDtos, changedHakukohdeOids)) {
                        LOG.debug("Komoto: " + curOid + " was handled during hakukohde process");
                    } else {
                        LOG.debug("Will index changed komoto: " + curOid);
                        indexLoiData(curOid);
                    }
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

        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            this.indexerService.rollbackIncrementalSolrChanges();
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - this.updateService.getRunningSince(), String.format("FAIL: %s", e.getMessage())));
            
        } finally {
            this.updateService.setRunning(false);
            this.updateService.setRunningSince(0);
        }
    }

    /*
     * Re indexing of higher educations. 
     */
    private void reIndexHigherEducation() throws IOException, SolrServerException, Exception {
        
        this.providerService.clearCache();
        
        this.dataUpdateService.clearHigherEducations(this.indexerService, this.loHttpSolrServer);
        List<HigherEducationLOS> higherEducations = this.tarjontaService.findHigherEducations();
        LOG.debug("Found higher educations: " + higherEducations.size());

        for (HigherEducationLOS curLOS : higherEducations) {
            LOG.debug("Saving highed education: " + curLOS.getId());
            indexToSolr(curLOS, this.loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer);
            this.dataUpdateService.save(curLOS);
        }
        LOG.debug("Higher educations saved.");
        
    }
    
    /*
     * Indexing of an added higher education to solr
     */
    private void indexToSolr(HigherEducationLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        for (HigherEducationLOS curChild: curLOS.getChildren()) {
            indexToSolr(curChild, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        }
    }

    private boolean isLoiAlreadyHandled(List<OidRDTO> aoOidDtos, List<String> changedHakukohdeOids) {

        for (OidRDTO curOidDto : aoOidDtos) {
            if (changedHakukohdeOids.contains(curOidDto.getOid())) {
                return true;
            }
        }

        return false;
    }

    //Indexes changed loi data
    private void indexLoiData(String komotoOid) throws Exception {
        LOG.debug(String.format("Indexing loi: %s", komotoOid));
        KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        LOG.debug(String.format("Loi: %s, status: %s", komotoOid, komotoDto.getTila()));
        
        if (isSecondaryEducation(komotoDto)) {
            if (!isLoiProperlyPublished(komotoDto)) {//(komotoDto.getTila().name().equals(TarjontaConstants.STATE_PUBLISHED))) {
                LOG.debug(String.format("Loi need to be removed: %s", komotoOid));
                handleLoiRemoval(komotoDto);
            } else {
                LOG.debug(String.format("Loi need to be indexed: %s", komotoOid));
                handleLoiAdditionOrUpdate(komotoDto);
            }
        } else if (!this.higherEdReindexed && this.isHigherEdKomo(komotoDto.getKomoOid())) {
            this.reIndexHigherEducation();
            this.higherEdReindexed = true;
        }

    }

    private boolean isLoiProperlyPublished(KomotoDTO komotoDto) {
        
        if (!komotoDto.getTila().name().equals(TarjontaConstants.STATE_PUBLISHED)) {
            return false;
        }
        
        List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByKomoto(komotoDto.getOid());
        
        if (hakukohdeOids != null) {
            for (OidRDTO curOidDto : hakukohdeOids) {
                HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOidDto.getOid());
                HakuDTO hakuDto = this.tarjontaRawService.getHaku(aoDto.getHakuOid());
                if (hakuDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED) && hakuDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean isSecondaryEducation(KomotoDTO komotoDto) {
       return !isHigherEdKomo(komotoDto.getKomoOid()) 
               && komotoDto.getKoulutuslajiUris() != null 
               && !komotoDto.getKoulutuslajiUris().isEmpty() 
               && !komotoDto.getKoulutuslajiUris().get(0).contains("koulutuslaji_a");
    }
    
    private boolean isHigherEdKomo(String komoOid) {
        KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);
        return komo != null && komo.getKoulutustyyppi() != null && komo.getKoulutustyyppi().equals("KORKEAKOULUTUS");
    }

    private void handleLoiAdditionOrUpdate(KomotoDTO komotoDto) throws KoodistoException, SolrServerException, IOException, TarjontaParseException {
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
    private void handleLoiRemoval(KomotoDTO komotoDto) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {


        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            for (LOS referencedLos : referencedLoss) {
                if (referencedLos instanceof ParentLOS) {
                    handleLoiRemovalFromParentLOS((ParentLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof SpecialLOS) {
                    handleLoiRemovalFromSpecialLOS((SpecialLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof ChildLOS) {
                    handleLoiRemovalFromChildLos((ChildLOS)referencedLos, komotoDto.getOid());      
                } else if (referencedLos instanceof UpperSecondaryLOS) {
                    handleLoiRemovalFromUpperSecondarLos((UpperSecondaryLOS)referencedLos);
                }
            }
        } 
    }

    private void handleLoiRemovalFromSpecialLOS(SpecialLOS los, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        KomoDTO komo = this.tarjontaRawService.getKomoByKomoto(komotoOid);
        this.reCreateSpecialLOS(los, komotoDto, komo, komotoDto.getTarjoajaOid());
    }

    private void handleLoiRemovalFromParentLOS(ParentLOS los, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("handling removal from parent los");
        this.reCreateParentLOS(los.getId().split("_")[0], los);
    }

    private void handleLoiRemovalFromChildLos(ChildLOS referencedLos, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        this.reCreateChildLOS(referencedLos);

    }

    private void handleLoiRemovalFromUpperSecondarLos(UpperSecondaryLOS los) throws TarjontaParseException, KoodistoException, IOException, SolrServerException {

        this.reCreateUpperSecondaryLOS(los);

    }

    //Indexing of based on changes in application systems
    private void indexApplicationSystemData(String asOid) throws Exception {
        HakuDTO asDto = this.tarjontaRawService.getHaku(asOid);
        if (isSecondaryAS(asDto)) {
            indexSecondaryEducationAsData(asDto);
        } else if (!higherEdReindexed) {
            this.reIndexHigherEducation();
            this.higherEdReindexed = true;
        }

    }



    private boolean isSecondaryAS(HakuDTO asDto) {
        return asDto != null && asDto.getKohdejoukkoUri() != null && !asDto.getKohdejoukkoUri().contains("haunkohdejoukko_12");
    }

    private void indexSecondaryEducationAsData(HakuDTO asDto) throws Exception {
        //Indexing application options connected to the changed application system
        List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asDto.getOid());
        
        
        
        if (hakukohdeOids != null && hakukohdeOids.isEmpty()) {
            for (OidRDTO curOid : hakukohdeOids) {
                HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid.getOid());
                indexApplicationOptionData(aoDto, asDto);
            }
        }
    }


    private void indexApplicationOptionData(HakukohdeDTO aoDto, HakuDTO asDto) throws Exception {

        
        if (isSecondaryAS(asDto)) {
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
        } else if (!this.higherEdReindexed) {
            this.reIndexHigherEducation();
            this.higherEdReindexed = true;
        }

    }


    private void updateApplicationOption(ApplicationOption ao, HakukohdeDTO aoDto) throws KoodistoException, TarjontaParseException, SolrServerException, IOException {

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
                if (TarjontaConstants.STATE_PUBLISHED.equals(komotoDto.getTila().name())) {
                    LOG.debug("komoto to add is published");
                    handleSecondaryLoiAddition(komotoDto);

                }

            }
        }
        catch (Exception ex) {
            LOG.warn(String.format("Problem indexing application option: %s. %s", aoDto.getOid(), ex.getMessage()));
        }

    }


    private void handleSecondaryLoiAddition(KomotoDTO komotoDto) throws KoodistoException, SolrServerException, IOException {
        LOG.debug(String.format("Adding loi: %s", komotoDto.getOid()));
        try {
            String providerOid = komotoDto.getTarjoajaOid();
            String komoOid = komotoDto.getKomoOid();
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String basicLosId = String.format("%s_%s", komoOid, providerOid);
            
            if (isRehabLOS(komo)) {
                basicLosId = String.format("%s_%s", basicLosId, komotoDto.getOid());
            } else if (isSpecialVocationalLos(komotoDto)) {
                basicLosId = String.format("%s_er", basicLosId);
            }

            LOS los = this.dataQueryService.getLos(basicLosId);
            if (los != null && los instanceof ChildLOS) {
                LOG.debug(String.format("A suitable child los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                reCreateChildLOS((ChildLOS)los);
            } else if (los != null && los instanceof UpperSecondaryLOS) {
                LOG.debug(String.format("A suitable upper secondary los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                this.reCreateUpperSecondaryLOS((UpperSecondaryLOS)los);
            } else if (los != null && los instanceof SpecialLOS) {
                LOG.debug(String.format("A suitable special los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                this.reCreateSpecialLOS((SpecialLOS)los, komotoDto, komo, komotoDto.getTarjoajaOid());
                //Trying to find parent los
            } else if (los == null) {
                List<String> ylamoduulit = komo.getYlaModuulit();
                if (ylamoduulit != null && ylamoduulit.size() > 0) {
                    los = this.dataQueryService.getLos(String.format("%s_%s", ylamoduulit.get(0), providerOid));
                }

            }
            //If parent los is found, it is recreated
            if (los != null && los instanceof ParentLOS) {
                LOG.debug(String.format("A suitable parent los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                KomoDTO parentKomo = this.tarjontaRawService.getKomoByKomoto(komotoDto.getParentKomotoOid());
                los = reCreateParentLOS(parentKomo.getOid(), (ParentLOS)los);
                LOG.debug("Parent los recreated");
            } 

            //if no matching los was there to be updated, a new los is created completely
            if (los == null) {
                LOG.debug(String.format("There was no los for komoto: %s, creating it", komotoDto.getOid()));
                createNewLos(komo, komotoDto);
            }

        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem adding loi incrementally with loi: %s. %s",  komotoDto.getOid(), ex.getMessage()));
        }
    }

    private void createNewLos(KomoDTO komo, KomotoDTO komotoDto) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOS los = null;
        String komoOid = komo.getOid();
        String educationType = komo.getKoulutusTyyppiUri();
        if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) && !this.isSpecialVocationalLos(komotoDto)) {

            if (komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
                komoOid = komo.getYlaModuulit().get(0);
            }
            los = createParentLOS(komoOid, komotoDto.getTarjoajaOid());
            LOG.debug(String.format("Created parentLos %s for komoto: %s, creating it", (los != null ? los.getId() : null), komotoDto.getOid()));
        } else if (this.isSpecialVocationalLos(komotoDto) || this.isRehabLOS(komo)) {
            los = createSpecialLOS(komo, komotoDto, komotoDto.getTarjoajaOid());

        } else if (educationType.equals(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE) && komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            los = this.createUpperSecondaryLOS(komo, komotoDto.getTarjoajaOid());
        }
        if (los == null) {
            LOG.warn("Los is still null after everything was tried. Komo oid is: " + komo.getOid());
        }

    }

    private boolean isSpecialVocationalLos(KomotoDTO komotoDto) {
        return komotoDto.getPohjakoulutusVaatimusUri().contains(TarjontaConstants.PREREQUISITE_URI_ER);        
    }

    private boolean isRehabLOS(KomoDTO komo) {

        String educationType  = komo.getKoulutusTyyppiUri();

        if (educationType.equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return true;
        } 
        else if ((educationType.equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE) 
                || educationType.equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)
                || educationType.endsWith(TarjontaConstants.KANSANOPISTO_TYPE))
                && komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return true;
        }
        return false;
    }

    private void reCreateChildLOS(ChildLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {

        ParentLOSRef parentRef = ((ChildLOS) los).getParent();
        ParentLOS parent = (ParentLOS)(this.dataQueryService.getLos(parentRef.getId()));

        this.reCreateParentLOS(parent.getId().split("_")[0], parent);

    }

    private ParentLOS reCreateParentLOS(String parentKomoOid, ParentLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Recreating parent los: " + los.getId());

        this.deleteParentLOSRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);

        ParentLOS parent = createParentLOS(parentKomoOid, los.getProvider().getId());

        return parent;
    }

    private ParentLOS createParentLOS(String parentKomoOid, String providerId) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Creating parent los: " + parentKomoOid + "_" + providerId);

        KomoDTO parentKomo = this.tarjontaRawService.getKomo(parentKomoOid);

        ParentLOS parent = this.parentLosBuilder.createParentLOS(parentKomo, providerId);
        LOG.debug("Creating child losses for parent: " + parent.getId());
        List<ChildLOS> children = this.parentLosBuilder.createChildLoss(parentKomo, parent);
        LOG.debug("Assembing child losses which amount to: " + children.size());
        this.parentLosBuilder.assembleParentLos(parent, children);
        LOG.debug("Filtering child losses which amount to: " + parent.getChildren().size());
        parent = this.parentLosBuilder.filterParentLos(parent);


        if (parent != null) {
            LOG.debug("Saving parent los: " +parent.getId() + " with " + parent.getChildren().size() + " children");
            this.dataUpdateService.save(parent);
            this.updateSolrMaps(parent, ADDITION);
        } 

        return parent;
    }

    private SpecialLOS reCreateSpecialLOS(SpecialLOS los, KomotoDTO komotoDto, KomoDTO komo, String providerId) throws SolrServerException, IOException, TarjontaParseException, KoodistoException {

        this.deleteSpecialLosRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        SpecialLOS specialLos = this.createSpecialLOS(komo, komotoDto, providerId);

        return specialLos;

    }


    private SpecialLOS createSpecialLOS(KomoDTO komo, KomotoDTO komoto, String providerId) throws SolrServerException, IOException, TarjontaParseException, KoodistoException {
        SpecialLOS specialLos = this.specialLosBuilder.createSpecialLOS(komo, komoto, this.isRehabLOS(komo), providerId);

        if (specialLos != null) {
            LOG.debug("Saving special los: " + specialLos.getId() + " with " + specialLos.getLois().size() + " children");
            this.dataUpdateService.save(specialLos);
            this.updateSolrMaps(specialLos, ADDITION);
        }
        return specialLos;
    }

    private UpperSecondaryLOS reCreateUpperSecondaryLOS(UpperSecondaryLOS los) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        LOG.debug("recreating upper secondary los: " + los.getId());
        this.deleteUpperSecondaryLosRecursive(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        KomoDTO komo = this.tarjontaRawService.getKomo(los.getId().split("_")[0]);
        return this.createUpperSecondaryLOS(komo, los.getProvider().getId());
    }

    private UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, String providerId) throws TarjontaParseException, KoodistoException {
        UpperSecondaryLOS newLos = this.upperSecLosBuilder.createUpperSecondaryLOS(komo, providerId);
        if (newLos != null) {
            LOG.debug("Saving special los: " + newLos.getId() + " with " + newLos.getLois().size() + " children");
            this.dataUpdateService.save(newLos);
            this.updateSolrMaps(newLos, ADDITION);
        }
        return newLos;
    }


    private void deleteUpperSecondaryLosRecursive(UpperSecondaryLOS los) {
        this.dataUpdateService.deleteLos(los);
        for (UpperSecondaryLOI curLoi : los.getLois()) {
            for (ApplicationOption curAo : curLoi.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }

    }

    private void deleteSpecialLosRecursively(SpecialLOS los) {
        this.dataUpdateService.deleteLos(los);
        for (ChildLOI curChildLoi : los.getLois()) {
            for (ApplicationOption curAo :curChildLoi.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }
    }

    private void deleteParentLOSRecursively(ParentLOS los) {

        this.dataUpdateService.deleteLos(los); 

        for (ParentLOI curParentLOI : los.getLois()) {
            for (ApplicationOption curAo : curParentLOI.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }

        for (ChildLOS curChild : los.getChildren()) {
            this.dataUpdateService.deleteLos(curChild);
            for (ChildLOI curChildLoi : curChild.getLois()) {
                for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                    this.dataUpdateService.deleteAo(curAo);
                }
            }
        }
    }

    //Handling removal of application option and related data
    private void removeApplicationOption(String oid) throws KoodistoException, TarjontaParseException, SolrServerException, IOException {

        LOG.debug("In remove application option");
        try {

            //Getting ao from mongo
            ApplicationOption ao = this.dataQueryService.getApplicationOption(oid);

            //Getting parent ref
            ParentLOSRef parentRef = ao.getParent();

            //If there is a parent ref, handling removal of parent data
            if (parentRef != null && parentRef.getId() != null) {
                LOG.debug("There is a parent ref in ao");
                LOS los  = this.dataQueryService.getLos(parentRef.getId());

                if (los != null && los instanceof ParentLOS) {
                    LOG.debug("The referenced los is ParentLOS");
                    handleParentLOSReferenceRemoval((ParentLOS)los);
                } else if (los != null && los instanceof SpecialLOS) {
                    LOG.debug("The referenced los is SpecialLOS");
                    handleSpecialLOSReferenceRemoval((SpecialLOS)los);
                } 
            }


            //If there are child loi refs, handling removal based on child references
            if (ao.getChildLOIRefs() != null
                    && !ao.getChildLOIRefs().isEmpty()) {
                LOG.debug("Handling childLOIRefs for ao");
                handleLoiRefRemoval(ao);
            }

            
            //If application option is not in mongo, nothing needs to be done    
        } catch (ResourceNotFoundException notFoundEx) {
            LOG.debug(notFoundEx.getMessage());
        }
    }

    //Removal based on child loi references
    private void handleLoiRefRemoval(ApplicationOption ao) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("In handleLoiRefRemoval from ao ");
        for (ChildLOIRef curChildRef : ao.getChildLOIRefs()) {
            LOS referencedLos = this.dataQueryService.getLos(curChildRef.getLosId());
            if (referencedLos instanceof UpperSecondaryLOS) {
                LOG.debug("The referenced los is UpperSecondary");
                handleUpperSecondarLosReferenceRemoval((UpperSecondaryLOS)referencedLos);
            } else if (referencedLos instanceof SpecialLOS) {
                LOG.debug("The referenced los is SpecialLOS");
                this.handleSpecialLOSReferenceRemoval((SpecialLOS)referencedLos);
            }
        }
    }

    private void handleUpperSecondarLosReferenceRemoval(
            UpperSecondaryLOS los) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        LOG.debug("Handling uppersecondary los reference removal, related to ao");
        this.reCreateUpperSecondaryLOS(los);
    }


    

    private void handleSpecialLOSReferenceRemoval(SpecialLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        String komoOid = los.getId().split("_")[0];
        String tarjoaja = los.getId().split("_")[1];

        KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);

        if (los.getType().equals(TarjontaConstants.TYPE_SPECIAL)) {
            this.reCreateSpecialLOS(los, null, komo, tarjoaja);
        } else {
            KomotoDTO komoto = this.tarjontaRawService.getKomoto(los.getId().split("_")[2]);
            this.reCreateSpecialLOS(los, komoto, komo, tarjoaja);
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

    private void handleParentLOSReferenceRemoval(ParentLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Currently in parent los reference removal, with  parent los: " + los.getId());
        String parentKomoOid = los.getId().split("_")[0];
        LOG.debug("parent komo to handle: " + parentKomoOid);
        this.reCreateParentLOS(parentKomoOid, los);
    }

    private Map<String, List<String>> listChangedLearningOpportunities(long updatePeriod) {
        Map<String, List<String>> changemap = this.tarjontaRawService.listModifiedLearningOpportunities(updatePeriod);
        LOG.debug("Tarjonta called");

        LOG.debug("Number of changes: " + changemap.size());

        for (Entry<String, List<String>> curEntry : changemap.entrySet()) {
            LOG.debug(curEntry.getKey() + ", " + curEntry.getValue());
        }

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
