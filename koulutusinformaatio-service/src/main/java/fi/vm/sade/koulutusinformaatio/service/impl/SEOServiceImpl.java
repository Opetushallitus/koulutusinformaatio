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

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.koulutusinformaatio.service.TextVersionService;

import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class SEOServiceImpl implements SEOService {

    private static final Logger LOG = LoggerFactory.getLogger(SEOServiceImpl.class);
    private SitemapBuilder sitemapBuilder;
    private SnapshotService snapshotService;
    private TextVersionService textVersionService;
    private Datastore mongoDatastore;
    private boolean running = false;
    private Map<String, String> sitemapParams;
    private String sitemapLocation;


    @Autowired
    public SEOServiceImpl(SnapshotService snapshotService, TextVersionService textVersionService,
                          Datastore primaryDatastore,
                          @Value("${koulutusinformaatio.baseurl.learningopportunity}") String baseUrl,
                          @Value("${koulutusinformaatio.sitemap.filepath}") String sitemapLocation) {
        this.snapshotService = snapshotService;
        this.textVersionService = textVersionService;
        this.mongoDatastore = primaryDatastore;
        this.sitemapBuilder = new SitemapBuilder();
        this.sitemapParams = Maps.newHashMap();
        this.sitemapParams.put(SitemapBuilder.PROPERTY_BASE_URL, baseUrl);
        String collections = "tutkinto:parentLearningOpportunitySpecifications," +
                "koulutusohjelma:childLearningOpportunities," +
                "lukio:upperSecondaryLearningOpportunitySpecifications," +
                "valmentava:specialLearningOpportunitySpecifications:-creditValue" +
                "erityisopetus:specialLearningOpportunitySpecifications:+creditValue";
        this.sitemapParams.put(SitemapBuilder.PROPERTY_COLLECTIONS, collections);
        this.sitemapLocation = sitemapLocation;
    }

    @Async
    @Override
    public void update() {
        try {
            running = true;
            snapshotService.renderSnapshots();
            byte[] sitemapBytes= sitemapBuilder.buildSitemap(mongoDatastore, sitemapParams);
            File dest = new File(this.sitemapLocation);
            Files.write(sitemapBytes, dest);
            
            // generate text version
            textVersionService.update();
        } catch (Exception e) {
            LOG.error(String.format("SEO batch execution error: %s", e.getMessage()));
        } finally {
            running = false;
        }

    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Date getSitemapTimestamp() {
        File sitemap = new File(sitemapLocation);
        return new Date(sitemap.lastModified());
    }
}