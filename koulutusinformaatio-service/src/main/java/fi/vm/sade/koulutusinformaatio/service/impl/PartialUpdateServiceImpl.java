/**
 * Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.Date;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.partial.PartialUpdateIndexer;

/**
 * @author risal1
 *
 */
@Service
@Profile("default")
public class PartialUpdateServiceImpl implements PartialUpdateService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PartialUpdateServiceImpl.class);
    
    private long runningSince = 0l;
    private boolean running = false;
    
    @Autowired
    private UpdateService updateService;
    
    @Autowired
    private IncrementalUpdateService incrementalUpdateService;
    
    @Autowired
    private IncrementalLOSIndexer losIndexer;
    
    @Autowired
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    @Autowired
    private IndexerService indexerService;
    
    @Autowired
    private PartialUpdateIndexer updateIndexer;
    
    @Autowired @Qualifier("lopAliasSolrServer") 
    private HttpSolrServer lopHttpSolrServer;
    
    @Autowired @Qualifier("loAliasSolrServer")
    private HttpSolrServer loHttpSolrServer;
    
    @Autowired @Qualifier("locationAliasSolrServer") 
    private HttpSolrServer locationHttpSolrServer;
    
    @Async
    @Override
    public void updateEducation(String oid) {
        doUpdate(oid, new EducationUpdater());
    }
    
    @Async
    @Override
    public void updateApplicationSystem(String oid) {
        doUpdate(oid, new ApplicationSystemUpdater());
    }
    
    private void doUpdate(String oid, Updater updater) {
        if (startRunningIfNoIndexingIsRunning()) {
            try {
                runUpdate(oid, updater);
            } catch (Exception e) {
                LOGGER.error("Error indexing " + updater.getUpdateProcessName() + ": " + oid, e);
                dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: %s", e.getMessage())));
            } finally {
                running = false;
            }
        }
    }

    private void runUpdate(String oid, Updater updater) throws Exception {
        updater.update(oid);
        LOGGER.debug("Committing to solr");
        indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        LOGGER.debug("Saving successful status");
        dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
        LOGGER.info(String.format("Partial indexing finished for %s with oid: %s. Indexing took %s", updater.getUpdateProcessName(),
                oid, System.currentTimeMillis() - runningSince));
    }

    private boolean startRunningIfNoIndexingIsRunning() {
        if (isRunning() || updateService.isRunning() || incrementalUpdateService.isRunning()) {
            LOGGER.debug("Indexing is running, not starting");
            return false;
        }
        running = true;
        runningSince = System.currentTimeMillis();
        return true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getRunningSince() {
        return runningSince;
    }
    
    private abstract class Updater {
        abstract void update(String oid) throws Exception;
        abstract String getUpdateProcessName();
        
    }
    
    private class ApplicationSystemUpdater extends Updater {

        @Override
        void update(String oid) throws Exception {
            LOGGER.debug("Indexing + " + this.getUpdateProcessName() + ": " + oid);
            updateIndexer.update(oid);
        }

        @Override
        String getUpdateProcessName() {
            return "application system";
        }
        
    }
    
    private class EducationUpdater extends Updater {

        @Override
        void update(String oid) throws Exception {
            LOGGER.debug("Indexing + " + this.getUpdateProcessName() + ": " + oid);
            losIndexer.indexLoiData(oid);
        }

        @Override
        String getUpdateProcessName() {
            return "education";
        }
    }

}
