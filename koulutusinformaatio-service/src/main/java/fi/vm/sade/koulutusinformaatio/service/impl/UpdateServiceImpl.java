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

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.LocationService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;

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
                 
            this.transactionManager.beginTransaction(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);

            int count = MAX_RESULTS;
            int index = 0;
            
            //while(count >= MAX_RESULTS) {
                LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
                //List<String> loOids = tarjontaService.listParentLearnignOpportunityOids(count, index);
                //count = loOids.size();
                //index += count;
                
                List<String> loOids = new ArrayList<String>();
                loOids.add("1.2.246.562.5.2013061010184627597002");
                loOids.add("1.2.246.562.5.2013061010185992084511");

               for (String loOid : loOids) {
                    List<LOS> specifications = null;
                    try {
                        specifications = tarjontaService.findParentLearningOpportunity(loOid);
                    } catch (TarjontaParseException e) {
                        LOG.warn("Exception while updating parent learning opportunity, oidMessage: " + e.getMessage());
                        continue;
                    }
                    for (LOS spec : specifications) {
                        this.indexerService.addLearningOpportunitySpecification(spec, loUpdateSolr, lopUpdateSolr);
                        this.educationDataUpdateService.save(spec);
                    }
                }
                this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            //}
            List<Location> locations = locationService.getMunicipalities();
            indexerService.addLocations(locations, locationUpdateSolr);
            indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            this.transactionManager.commit(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            LOG.info("Education data update successfully finished");
        } catch (Exception e) {
                LOG.error("Education data update failed ", e);
            this.transactionManager.rollBack(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        } finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
