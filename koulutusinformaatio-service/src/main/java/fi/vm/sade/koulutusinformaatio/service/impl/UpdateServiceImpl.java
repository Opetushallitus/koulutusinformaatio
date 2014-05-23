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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.ArticleService;
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
    private ArticleService articleService;

    private TransactionManager transactionManager;
    private static final int MAX_RESULTS = 100;
    private boolean running = false;
    private long runningSince = 0;
    private LocationService locationService;

    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService, IndexerService indexerService,
            EducationDataUpdateService educationDataUpdateService,
            TransactionManager transactionManager, LocationService locationService,
            ArticleService articleService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.transactionManager = transactionManager;
        this.locationService = locationService;
        this.articleService = articleService;
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
            

            /*while (count >= MAX_RESULTS) {
            LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
            List<String> loOids = tarjontaService.listParentLearnignOpportunityOids(count, index);
            count = loOids.size();
            index += count;*/
            
            
            List<String> loOids = Arrays.asList(
                    "1.2.246.562.5.2013061010184443434255", //amm
                    "1.2.246.562.5.2013061010191530269331", //lukio
                    "1.2.246.562.5.2013112814572429147350", //valmistava
                    "1.2.246.562.5.2013061010184317101998", //amm kuvataide
                    "1.2.246.562.5.2013061010184614853416"  //kotitalousopetus
                    );
            
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
            //}

            List<HigherEducationLOS> higherEducations = this.tarjontaService.findHigherEducations();
            LOG.debug("Found higher educations: " + higherEducations.size());

            for (HigherEducationLOS curLOS : higherEducations) {
                LOG.debug("Saving highed education: " + curLOS.getId());
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.debug("Higher educations saved.");
            
            List<Code> edTypeCodes = this.tarjontaService.getEdTypeCodes();
            indexerService.addEdTypeCodes(edTypeCodes, loUpdateSolr);
            LOG.debug("Education types indexded.");

            List<Location> locations = locationService.getMunicipalities();
            LOG.debug("Got locations");
            indexerService.addLocations(locations, locationUpdateSolr);
            LOG.debug("Added locations");
            List<Article> articles = this.articleService.fetchArticles();
            LOG.debug("Articles fetched");
            indexerService.addArticles(loUpdateSolr, articles);
            LOG.debug("Articles indexed to solr");
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

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void setRunningSince(long runningSince) {
        this.runningSince = runningSince;
    }

    @Override
    @Async
    public void updateArticles() throws Exception {
        
        if (this.running) {
            return;
        }
        
        LOG.info("Indexing articles");
        
        try {
            running = true;
            runningSince = System.currentTimeMillis();
            this.indexerService.removeArticles();
            
            List<Article> articles = this.articleService.fetchArticles();
            LOG.debug("Articles fetched");
            indexerService.addArticles(articles);
            
            //educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
            LOG.info("Articles succesfully indexed");
        } catch (Exception ex) {
            indexerService.rollbackIncrementalSolrChanges();
            educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: Article indexing %s", ex.getMessage())));
            LOG.error("Article update failed ", ex);
            
        } finally {
            running = false;
            runningSince = 0;
        }
        
        
    }

}

