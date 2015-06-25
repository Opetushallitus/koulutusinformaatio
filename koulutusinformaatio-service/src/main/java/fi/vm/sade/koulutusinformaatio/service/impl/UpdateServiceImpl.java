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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.ArticleService;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.LocationService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;





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
    private ProviderService providerService;

    private TransactionManager transactionManager;
    private static final int MAX_RESULTS = 100;
    private boolean running = false;
    private long runningSince = 0;
    private LocationService locationService;

    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService, IndexerService indexerService,
            EducationDataUpdateService educationDataUpdateService,
            TransactionManager transactionManager, LocationService locationService,
            ProviderService providerService,
            ArticleService articleService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.transactionManager = transactionManager;
        this.locationService = locationService;
        this.articleService = articleService;
        this.providerService = providerService;
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
            this.indexerService.clearProcessedLists();

            this.transactionManager.beginTransaction(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            int count = MAX_RESULTS;
            int index = 0;

            LOG.debug("Starting V0 indexing");
            while (count >= MAX_RESULTS) {
                LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
                List<String> loOids = tarjontaService.listParentLearnignOpportunityOids(count, index);
                count = loOids.size();
                index += count;

                for (String loOid : loOids) {
                    List<LOS> specifications = null;
                    try {
                        specifications = tarjontaService.findParentLearningOpportunity(loOid);
                    } catch (TarjontaParseException e) {
                        LOG.debug(String.format("Exception while updating parent learning opportunity %s: %s", loOid, e.getMessage()));
                        continue;
                    }
                    if (specifications != null) {
                        LOG.debug("Specifications foud: " + specifications.size());
                    }
                    for (LOS spec : specifications) {
                        try {
                            this.indexerService.addLearningOpportunitySpecification(spec, loUpdateSolr, lopUpdateSolr);
                            this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
                            this.educationDataUpdateService.save(spec);
                        } catch (Exception exc) {
                            LOG.error("Problem indexing los: " + spec.getId(), exc);
                            throw exc;
                        }
                    }
                }
            }
            LOG.info("V0 indexing finished");

            tarjontaService.clearProcessedLists();
            List<KoulutusHakutulosV1RDTO> vocationalEducations = this.tarjontaService.findAmmatillinenKoulutusDTOs();
            LOG.debug("Found vocational educations: " + vocationalEducations.size());
            for (KoulutusHakutulosV1RDTO curDTO : vocationalEducations) {
                LOG.debug("Indexing vocational education: " + curDTO.getOid());
                List<KoulutusLOS> losses = tarjontaService.createAmmatillinenKoulutusLOS(curDTO);
                for (KoulutusLOS curLOS : losses) {
                    indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                    this.educationDataUpdateService.save(curLOS);
                }
                TutkintoLOS tutkintolos = losses.get(0).getTutkinto();
                // indexToSolr(tutkintolos, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(tutkintolos);
            }
            LOG.info("Vocational educations saved.");

            List<HigherEducationLOS> higherEducations = this.tarjontaService.findHigherEducations();
            LOG.debug("Found higher educations: " + higherEducations.size());

            for (HigherEducationLOS curLOS : higherEducations) {
                LOG.debug("Saving highed education: " + curLOS.getId());

                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Higher educations saved.");

            // Includes Aikuisten lukiokoulutus and Aikuisten perusopetus
            List<AdultUpperSecondaryLOS> adultEducations = this.tarjontaService.findAdultUpperSecondariesAndBaseEducation();
            LOG.debug("Found adult educations: " + adultEducations.size());

            for (AdultUpperSecondaryLOS curLOS : adultEducations) {
                LOG.debug("Saving adult education: " + curLOS.getId());
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Adult upper secondary and base educations saved.");

            List<CompetenceBasedQualificationParentLOS> adultVocationals = this.tarjontaService.findAdultVocationals();
            LOG.debug("Found " + adultVocationals.size() + " adult vocational educations");
            for (CompetenceBasedQualificationParentLOS curLOS : adultVocationals) {
                LOG.debug("Saving adult vocational los: " + curLOS.getId() + " with name: " + curLOS.getName().get("fi"));
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Adult vocational educations saved.");

            List<KoulutusLOS> valmistavaList = this.tarjontaService.findValmistavaKoulutusEducations();
            LOG.debug("Found " + valmistavaList.size() + " valmistava educations");
            for (KoulutusLOS curLOS : valmistavaList) {
                LOG.debug("Saving valmistava los: " + curLOS.getId() + " with name: " + curLOS.getName().get("fi"));
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Valmistava educations saved.");

            this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
            indexProviders(lopUpdateSolr, loUpdateSolr, locationUpdateSolr);
            LOG.info("Providers indexed");

            List<Code> edTypeCodes = this.tarjontaService.getEdTypeCodes();
            indexerService.addFacetCodes(edTypeCodes, loUpdateSolr);
            LOG.info("Education types indexded.");

            List<Code> edBaseEdCodes = this.tarjontaService.getEdBaseEducationCodes();
            indexerService.addFacetCodes(edBaseEdCodes, loUpdateSolr);
            LOG.info("Base educations indexded.");

            List<Location> locations = locationService.getMunicipalities();
            LOG.debug("Got locations");
            indexerService.addLocations(locations, locationUpdateSolr);
            LOG.info("Location indexed");

            List<CalendarApplicationSystem> applicationSystems = this.tarjontaService.findApplicationSystemsForCalendar();
            for (CalendarApplicationSystem curAs : applicationSystems) {
                LOG.debug("Indexing application system: " + curAs.getId());
                this.indexerService.indexASToSolr(curAs, loUpdateSolr);
            }
            this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
            LOG.info("Application systems indexed");

            List<Article> articles = this.articleService.fetchArticles();
            LOG.debug("Articles fetched");
            indexerService.addArticles(loUpdateSolr, articles);
            LOG.info("Articles indexed to solr");

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

    /*
     * 
     * Handles the indexing of providers from organisaatio service to solr and MongoDB.
     * This method is used when indexing organizations (Oppilaitos, Toimipiste) which 
     * are not providers of learning opportunities.
     * 
     */
    private void indexProviders(HttpSolrServer lopUpdateSolr, HttpSolrServer loUpdateSolr, HttpSolrServer locationUpdateSolr) throws MalformedURLException, ResourceNotFoundException, IOException, KoodistoException, SolrServerException {

        List<OrganisaatioPerustieto> orgBasics = this.providerService.fetchOpplaitokset();
        LOG.debug("Oppilaitokset fetched");
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        LOG.debug("Oppilaitokset saved");
        orgBasics = this.providerService.fetchToimipisteet();
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        orgBasics = this.providerService.fetchOppisopimusToimipisteet();
        createAndSaveProviders(orgBasics, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        LOG.debug("toimipisteet saved");
    }

    /*
     * Indexes and saves the given list of organizations. 
     */
    private void createAndSaveProviders(List<OrganisaatioPerustieto> orgBasics,
            HttpSolrServer lopUpdateSolr) throws KoodistoException, MalformedURLException, ResourceNotFoundException, IOException, SolrServerException {
        LOG.debug("organisations length: " + orgBasics.size());
        for (OrganisaatioPerustieto curOrg : orgBasics) {
       
            LOG.debug("Fetching org " + curOrg.getOid());
            if (!indexerService.isDocumentInIndex(curOrg.getOid(), lopUpdateSolr)) {
                LOG.debug("Indexing organisaatio: " + curOrg.getOid());
                Provider curProv = null;
                try {
                    curProv = this.providerService.getByOID(curOrg.getOid());
                } catch (Exception ex) {
                    LOG.warn("Problem indexing organization: " + curOrg.getOid(), ex);
                    continue;
                }
                if (curProv.getOlTypeFacets() != null && !curProv.getOlTypeFacets().isEmpty()) {
                    this.educationDataUpdateService.save(curProv);
                    this.indexerService.createProviderDocs(curProv, lopUpdateSolr, new HashSet<String>(), new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
                    LOG.debug("Indexed and saved organisaatio: " + curOrg.getOid());
                }
                
            }
        }
    }

    private void indexToSolr(KoulutusLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
        if (curLOS instanceof HigherEducationLOS) {
            for (HigherEducationLOS curChild: ((HigherEducationLOS)curLOS).getChildren()) {
                indexToSolr(curChild, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            }
        }
    }

    private void indexToSolr(TutkintoLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
    }


    private void indexToSolr(CompetenceBasedQualificationParentLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
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
            LOG.warn("Article update failed ", ex);

        } finally {
            running = false;
            runningSince = 0;
        }


    }

}

