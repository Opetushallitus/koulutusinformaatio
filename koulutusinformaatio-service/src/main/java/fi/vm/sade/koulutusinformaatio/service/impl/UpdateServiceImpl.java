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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    public static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private EducationDataUpdateService educationDataUpdateService;

    private TransactionManager transactionManager;
    private static final int MAX_RESULTS = 100;
    private boolean running = false;
    private long runningSince = 0;
    private LocationService locationService;


    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService, IndexerService indexerService,
            EducationDataUpdateService educationDataUpdateService,
            TransactionManager transactionManager, LocationService locationService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.transactionManager = transactionManager;
        this.locationService = locationService;
    }

    @Override
    @Async
    public synchronized void updateAllEducationData() throws Exception {
        HttpSolrServer loUpdateSolr = this.indexerService.getLoCollectionToUpdate();
        HttpSolrServer lopUpdateSolr = this.indexerService.getLopCollectionToUpdate(loUpdateSolr);
        HttpSolrServer locationUpdateSolr = this.indexerService.getLocationCollectionToUpdate(loUpdateSolr);

        try {
            LOG.info("Starting full education data update");
            running = true;
            runningSince = System.currentTimeMillis();

            this.transactionManager.beginTransaction(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);

            int count = MAX_RESULTS;
            int index = 0;

            while(count >= MAX_RESULTS) {
                LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
                List<String> loOids = tarjontaService.listParentLearnignOpportunityOids(count, index);
                count = loOids.size();
                index += count;
            
                for (String loOid : loOids) {
                    List<LOS> specifications = null;
                    try {
                        specifications = tarjontaService.findParentLearningOpportunity(loOid);
                    } catch (TarjontaParseException e) {
                        LOG.warn(String.format("Exception while updating parent learning opportunity %s: %s", loOid, e.getMessage()));
                        continue;
                    }
                    for (LOS spec : specifications) {
                        this.indexerService.addLearningOpportunitySpecification(spec, loUpdateSolr, lopUpdateSolr);
                        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
                        this.educationDataUpdateService.save(spec);
                    }
                }
            }

            List<HigherEducationLOS> higherEducations = this.tarjontaService.findHigherEducations();
            LOG.debug("Found higher educations: " + higherEducations.size());

            for (HigherEducationLOS curLOS : higherEducations) {
                LOG.debug("Saving highed education: " + curLOS.getId());
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.debug("Higher educations saved: ");
            
            List<Code> edTypeCodes = this.tarjontaService.getEdTypeCodes();
            indexerService.addEdTypeCodes(edTypeCodes, loUpdateSolr);

            List<Location> locations = locationService.getMunicipalities();
            LOG.debug("Got locations");
            indexerService.addLocations(locations, locationUpdateSolr);
            LOG.debug("Added locations");
            indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, true);
            LOG.debug("Committed to solr");
            this.transactionManager.commit(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            LOG.debug("Transaction completed");
            educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));

            LOG.info("Education data update successfully finished");
        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            this.transactionManager.rollBack(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: %s", e.getMessage())));
        } finally {
            running = false;
            runningSince = 0;
        }

    }

    private void indexToSolr(HigherEducationLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        for (HigherEducationLOS curChild: curLOS.getChildren()) {
            indexToSolr(curChild, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getRunningSince() {
        return runningSince;
    }
}
