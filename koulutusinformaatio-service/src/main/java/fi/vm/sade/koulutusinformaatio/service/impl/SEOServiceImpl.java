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
import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.IndexingException;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.properties.OphProperties;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.xml.transform.TransformerException;
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
    private Datastore mongoDatastore;
    private boolean running = false;
    private Map<String, String> sitemapParams;
    private SEOSnapshotService seoSnapshotService;

    @Autowired
    public SEOServiceImpl(SnapshotService snapshotService,
                          Datastore primaryDatastore,
                          OphProperties urlProperties,
                          SEOSnapshotService seoSnapshotService) {
        this.snapshotService = snapshotService;
        this.seoSnapshotService = seoSnapshotService;
        this.mongoDatastore = primaryDatastore;
        this.sitemapBuilder = new SitemapBuilder();
        this.sitemapParams = Maps.newHashMap();
        this.sitemapParams.put(SitemapBuilder.PROPERTY_BASE_URL, urlProperties.url("koulutusinformaatio-app-web.learningopportunity.base"));
        String collections =
                "tutkinto:tutkintoLOS," +
                "koulutus:koulutusLOS," +
                "korkeakoulu:universityAppliedScienceLOS," +
                "ammatillinenaikuiskoulutus:competenceBasedQualificationParentLOS," +
                "aikuislukio:koulutusLOS";
        this.sitemapParams.put(SitemapBuilder.PROPERTY_COLLECTIONS, collections);
    }

    @Async
    @Override
    public void update() {
        try {
            running = true;
            snapshotService.renderAllSnapshots();
            createSitemap();
        } catch (TransformerException | IndexingException e) {
            LOG.error("SEO batch execution error", e);
        } finally {
            running = false;
        }
    }

    @Async
    @Override
    public void updateLastModified() {
        try {
            running = true;
            snapshotService.renderLastModifiedSnapshots();
            createSitemap();
        } catch (TransformerException | IndexingException e) {
            LOG.error("SEO batch execution error", e);
        } finally {
            running = false;
        }
    }

    private void createSitemap() throws TransformerException {
        byte[] sitemapBytes = sitemapBuilder.buildSitemap(mongoDatastore, sitemapParams);
        seoSnapshotService.setSitemap(new String(sitemapBytes));
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    @Nullable
    public Date getSitemapTimestamp() {
        SnapshotDTO sitemap = seoSnapshotService.getSitemap();
        return sitemap == null ? null : sitemap.getSnapshotCreated();
    }
}
