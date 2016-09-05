/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



/**
 * 
 * @author Markus
 *
 */
@Service
@Profile("default")
public class IncrementalUpdateServiceImpl implements IncrementalUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(IncrementalUpdateServiceImpl.class);

    private TarjontaRawService tarjontaRawService;

    private EducationIncrementalDataQueryService dataQueryService;
    //private EducationDataQueryService prodDataQueryService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;

    private IncrementalApplicationSystemIndexer asIndexer;
    private IncrementalLOSIndexer losIndexer;

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    private boolean isRunning = false;
    private long runningSince = 0;

    @Autowired
    public IncrementalUpdateServiceImpl(TarjontaRawService tarjontaRawService, 
            EducationIncrementalDataQueryService dataQueryService,
            EducationIncrementalDataUpdateService dataUpdateService,
            KoodistoService koodistoService,
            TarjontaService tarjontaService,
            IndexerService indexerService,
            ParameterService parameterService,
            @Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
            @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
            @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loAliasSolrServer;
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;

        this.losIndexer = new IncrementalLOSIndexer(this.tarjontaRawService,
                this.tarjontaService, 
                this.dataUpdateService,
                this.dataQueryService,
                this.indexerService,
                this.loHttpSolrServer,
                this.lopHttpSolrServer,
                this.locationHttpSolrServer);
        this.asIndexer = new IncrementalApplicationSystemIndexer(this.tarjontaService,
                                                                this.dataQueryService, 
                                                                this.losIndexer,
                                                                this.indexerService,
                                                                this.loHttpSolrServer,
                                                                this.lopHttpSolrServer,
                                                                this.locationHttpSolrServer);
    }

    @Override
    @Async
    public void updateChangedEducationData() {

        LOG.debug("updateChangedEducationData on its way");
        // Getting get update period
        long updatePeriod = getUpdatePeriod();
        LOG.debug(String.format("Update period: %s", updatePeriod));

        try {
            // Fetching changes within the update period
            runningSince = System.currentTimeMillis();
            isRunning = true;
            Map<String, List<String>> result = listChangedLearningOpportunities(updatePeriod);
            if (!hasChanges(result)) {
                LOG.debug("No incremental changes. Stopping now.");
                isRunning = false;
                runningSince = 0;
                return;
            }
            LOG.info("Starting incremental update:");

            this.tarjontaService.clearProcessedLists();
            // If there are changes in komo-data, a full update is performed
            if ((result.containsKey("koulutusmoduuli") && !result.get("koulutusmoduuli").isEmpty()) || updatePeriod == 0) {
                LOG.info(String.format("Update period was: %s", updatePeriod));
                LOG.info(String.format("Komos changed: " + result.get("koulutusmoduuli")));
                indexKomoChanges(result.get("koulutusmoduuli"));
            }

            Set<String> affectedKoulutusOids = Sets.newHashSet();
            if (result.containsKey("haku") && !result.get("haku").isEmpty()) {
                LOG.info("Haku changes: " + result.get("haku"));
                for (String asOid : result.get("haku")) {
                    affectedKoulutusOids.addAll(tarjontaService.findKoulutusOidsByHaku(asOid));
                    asIndexer.indexApplicationSystemForCalendar(asOid);
                }
            }
            if (result.containsKey("hakukohde") && !result.get("hakukohde").isEmpty()) {
                LOG.info("Hakukohde changes: " + result.get("hakukohde"));
                for (String aoOid : result.get("hakukohde")) {
                    affectedKoulutusOids.addAll(tarjontaService.findKoulutusOidsByAo(aoOid));
                }
            }
            if (result.containsKey("koulutusmoduuliToteutus") && !result.get("koulutusmoduuliToteutus").isEmpty()) {
                LOG.info("Changed komotos: " + result.get("koulutusmoduuliToteutus"));
                affectedKoulutusOids.addAll(result.get("koulutusmoduuliToteutus"));
            }

            if (!affectedKoulutusOids.isEmpty()){
                LOG.info("Incremental indexing will be run for {} koulutus.", affectedKoulutusOids.size());
                indexKomotoChanges(affectedKoulutusOids);
            }

            LOG.debug("Committing to solr");
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            LOG.debug("Saving successful status");
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS-INCREMENTAL"));
            LOG.info("Incremental indexing finished in {} s.", (System.currentTimeMillis() - runningSince) / 1000);

        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: %s", e.getMessage())));
        } finally {
            this.tarjontaService.clearProcessedLists();
            this.isRunning = false;
            this.runningSince = 0;
        }
    }

    private void indexKomotoChanges(Set<String> komotoChanges) {
        int indexed = 0;
        for (String curOid : komotoChanges) {
            try {
                if (!tarjontaService.hasAlreadyProcessedOid(curOid)) {
                    LOG.debug("Will index changed komoto: " + curOid);
                    this.losIndexer.indexLoiData(curOid);
                } else {
                    LOG.debug("Koulutus {} already indexed, skipping.", curOid);
                }
            } catch (Exception ex) {
                LOG.warn("problem indexing komoto: " + curOid, ex);
            } finally {
                indexed++;
                if(indexed % 100 == 0) {
                    LOG.info("Indexed {}/{} komotos", indexed, komotoChanges.size());
                }
            }
        }
    }

    private void indexKomoChanges(List<String> komoChanges) {
        for (String curKomoOid : komoChanges) {
            try {
                if (this.losIndexer.isHigherEdKomo(curKomoOid)) { 
                    this.losIndexer.indexHigherEdKomo(curKomoOid);
                }
            } catch (Exception ex) {
                LOG.warn("Error indexing komo: " + curKomoOid, ex);
            }
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
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public long getRunningSince() {
        return runningSince;
    }
}
