package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.ArrayList;
import java.util.Date;
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

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationOptionIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleParentLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleSpecialLOSBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.SingleUpperSecondaryLOSBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

@Service
@Profile("default")
public class IncrementalUpdateServiceImpl implements IncrementalUpdateService {

    public static final Logger LOG = LoggerFactory.getLogger(IncrementalUpdateServiceImpl.class);

    private TarjontaRawService tarjontaRawService;
    private UpdateService updateService;

    private EducationIncrementalDataQueryService dataQueryService;
    //private EducationDataQueryService prodDataQueryService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;

    private LOSObjectCreator losCreator;
    private SingleParentLOSBuilder parentLosBuilder;
    private SingleSpecialLOSBuilder specialLosBuilder;
    private SingleUpperSecondaryLOSBuilder upperSecLosBuilder;
    
    private IncrementalApplicationSystemIndexer asIndexer;
    private IncrementalApplicationOptionIndexer aoIndexer;
    private IncrementalLOSIndexer losIndexer;

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
            UpdateService updateService, 
            EducationIncrementalDataQueryService dataQueryService,
            EducationIncrementalDataUpdateService dataUpdateService,
            KoodistoService koodistoService,
            ProviderService providerService,
            TarjontaService tarjontaService,
            IndexerService indexerService,
            @Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
            @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
            @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer) {
        this.tarjontaRawService = tarjontaRawService;
        this.updateService = updateService;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loAliasSolrServer;
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;

        this.losCreator = new LOSObjectCreator(this.koodistoService, this.tarjontaRawService, this.providerService);
        this.parentLosBuilder = new SingleParentLOSBuilder(losCreator, tarjontaRawService);
        this.specialLosBuilder = new SingleSpecialLOSBuilder(losCreator, tarjontaRawService);
        this.upperSecLosBuilder = new SingleUpperSecondaryLOSBuilder(losCreator, tarjontaRawService);
        this.losIndexer = new IncrementalLOSIndexer(this.tarjontaRawService, 
                this.tarjontaService, 
                this.dataUpdateService,
                this.dataQueryService,
                this.indexerService,
                this.loHttpSolrServer,
                this.lopHttpSolrServer,
                this.locationHttpSolrServer,
                this.parentLosBuilder,
                this.specialLosBuilder,
                this.upperSecLosBuilder);
        this.aoIndexer = new IncrementalApplicationOptionIndexer(this.tarjontaRawService, this.dataQueryService, this.dataUpdateService, this.losIndexer);
        this.asIndexer = new IncrementalApplicationSystemIndexer(this.tarjontaRawService, this.dataQueryService, this.koodistoService, this.aoIndexer, this.losIndexer);
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


            //Fetching changes within the update period
            Map<String,List<String>> result = listChangedLearningOpportunities(updatePeriod);


            LOG.debug("Starting incremental update");

            if (!hasChanges(result)) {
                return;
            }
            
            long runningSince = System.currentTimeMillis();
            this.updateService.setRunning(true);
            this.updateService.setRunningSince(runningSince);
            this.losIndexer.clearCreatedLOS();

            //If there are changes in komo-data, a full update is performed
            if ((result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) || updatePeriod == 0) {
                LOG.warn(String.format("Komos changed. Update period was: %s", updatePeriod));

                for (String curKomoOid : result.get("koulutusmoduuli")) {
                    if (this.losIndexer.isHigherEdKomo(curKomoOid)) { //&& !higherEdReindexed) {

                        this.losIndexer.indexHigherEdKomo(curKomoOid);


                    }
                }

            } 

            //If changes in haku objects indexing them
            if (result.containsKey("haku")) {
                LOG.debug("Haku changes: " + result.get("haku").size());

                for (String curOid : result.get("haku")) {
                    LOG.debug("Changed haku: " + curOid);
                    this.asIndexer.indexApplicationSystemData(curOid);
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
                    this.aoIndexer.indexApplicationOptionData(aoDto, asDto);
                }

            }

            //If changes in koulutusmoduuliToteutus, indexing them 
            if (result.containsKey("koulutusmoduuliToteutus")) {
                LOG.debug("Changed komotos: " + result.get("koulutusmoduuliToteutus").size());
                for (String curOid : result.get("koulutusmoduuliToteutus")) {
                    List<OidRDTO> aoOidDtos = this.tarjontaRawService.getHakukohdesByKomoto(curOid);
                    if (this.losIndexer.isLoiAlreadyHandled(aoOidDtos, changedHakukohdeOids)) {
                        LOG.debug("Komoto: " + curOid + " was handled during hakukohde process");
                    } else {
                        LOG.debug("Will index changed komoto: " + curOid);
                        this.losIndexer.indexLoiData(curOid);
                    }
                }
            }

            LOG.debug("Committing to solr");
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            LOG.debug("Saving successful status");
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
            LOG.debug("All done");

        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - this.updateService.getRunningSince(), String.format("FAIL: %s", e.getMessage())));
        } finally {
            this.updateService.setRunning(false);
            this.updateService.setRunningSince(0);
        }
    }

    private boolean hasChanges(Map<String, List<String>> result) {

        return (result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty())
                || (result.containsKey("haku") && !result.get("haku").isEmpty())
                || (result.containsKey("hakukohde") && !result.get("hakukohde").isEmpty())
                || (result.containsKey("koulutusmoduuliToteutus") && !result.get("koulutusmoduuliToteutus").isEmpty());

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
        DataStatus status = this.dataQueryService.getLatestSuccessDataStatus();
        if (status != null) {
            long period = (System.currentTimeMillis() - status.getLastUpdateFinished().getTime()) + status.getLastUpdateDuration();
            return period;
        }
        return 0;
    }

}
