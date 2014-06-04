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

package fi.vm.sade.koulutusinformaatio.scheduling;

import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.TextVersionService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Mikko Majapuro
 */
@Component
public class Scheduler {

    public static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    private UpdateService updateService;
    private IncrementalUpdateService incrementalUpdateService;
    private SEOService seoService;
    private TextVersionService textVersionService;
    private boolean enabled;
    private boolean seoEnabled;
    private boolean textVersionEnabled;
    private boolean incrementalEnabled;
    private boolean articlesEnabled;

    @Autowired
    public Scheduler(final UpdateService updateService, 
            final IncrementalUpdateService incrementalUpdateService,
            final SEOService seoService, 
            final TextVersionService textVersionService,
            @Value("${scheduling.enabled}") boolean enabled,
            @Value("${scheduling.seo.enabled}") boolean seoEnabled, 
            @Value("${scheduling.textversion.enabled}") boolean textVersionEnabled,
            @Value("${scheduling.data.incremental.enabled}") boolean incrementalEnabled,
            @Value("${scheduling.data.articles.enabled}") boolean articlesEnabled) {
        this.updateService = updateService;
        this.seoService = seoService;
        this.textVersionService = textVersionService;
        this.enabled = enabled;
        this.seoEnabled = seoEnabled;
        this.textVersionEnabled = textVersionEnabled;
        this.incrementalEnabled = incrementalEnabled;
        this.incrementalUpdateService = incrementalUpdateService;
        this.articlesEnabled = articlesEnabled;
    }

    @Scheduled(cron = "${scheduling.data.cron}")
    public void runDateUpdate() {
        if (enabled) {
            LOG.info("Starting scheduled data update {}", new Date());
            try {
                if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                    updateService.updateAllEducationData();
                }
            } catch (Exception e) {
                LOG.error("Data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }

    @Scheduled(cron = "${scheduling.seo.cron}")
    public void runSEOUpdate() {
        if (seoEnabled) {
            LOG.info("Starting scheduled SEO update {}", new Date());
            try {
                if (!seoService.isRunning()) {
                    seoService.update();
                }
            } catch (Exception e) {
                LOG.error("SEO execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
    
    @Scheduled(cron = "${scheduling.textversion.cron}")
    public void runTextVersionUpdate() {
        if (textVersionEnabled) {
            LOG.info("Starting scheduled text version update {}", new Date());
            try {
                if (!textVersionService.isRunning()) {
                    textVersionService.update();
                }
            } catch (Exception e) {
                LOG.error("Text version generation execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
    
    @Scheduled(cron = "${scheduling.data.incremental.cron}")
    public void runIncrementalDataUpdate() {
        if (incrementalEnabled) {
            LOG.info("Starting scheduled incremental data update {}", new Date());
            
            try {
                if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                    LOG.debug("indexing is not running, starting incremental indexing.");
                    this.incrementalUpdateService.updateChangedEducationData();
                } else {
                    LOG.debug("\n\nindexing is running, not starting incremental indexing.\n\n");
                }
            } catch (Exception e) {
                LOG.error("Incremental data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
    
    @Scheduled(cron = "${scheduling.data.articles.cron}")
    public void runArticleUpdate() {
        if (this.articlesEnabled) {
            LOG.info("Starting scheduled article update {}", new Date());
            
            try {
                if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                    LOG.debug("indexing is not running, starting article indexing.");
                    this.updateService.updateArticles();
                } else {
                    LOG.debug("\n\nindexing is running, not starting article indexing.\n\n");
                }
            } catch (Exception e) {
                LOG.error("Incremental data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
}
