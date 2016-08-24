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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.impl.RunningServiceChecker;

/**
 * @author Mikko Majapuro
 */
@Component
public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    private UpdateService updateService;
    private IncrementalUpdateService incrementalUpdateService;
    private SEOService seoService;
    private RunningServiceChecker runningServiceChecker;
    private boolean enabled;
    private boolean seoEnabled;
    private boolean incrementalEnabled;
    private boolean articlesEnabled;

    @Autowired
    public Scheduler(final UpdateService updateService, 
            final IncrementalUpdateService incrementalUpdateService,
            final SEOService seoService,
            final RunningServiceChecker runningServiceChecker,
            @Value("${scheduling.enabled}") boolean enabled,
            @Value("${scheduling.seo.enabled}") boolean seoEnabled, 
            @Value("${scheduling.data.incremental.enabled}") boolean incrementalEnabled,
            @Value("${scheduling.data.articles.enabled}") boolean articlesEnabled) {
        this.updateService = updateService;
        this.seoService = seoService;
        this.runningServiceChecker = runningServiceChecker;
        this.enabled = enabled;
        this.seoEnabled = seoEnabled;
        this.incrementalEnabled = incrementalEnabled;
        this.incrementalUpdateService = incrementalUpdateService;
        this.articlesEnabled = articlesEnabled;
    }

    @Scheduled(cron = "${scheduling.data.cron}")
    public void runDateUpdate() {
        if (enabled) {
            try {
                if (!runningServiceChecker.isAnyServiceRunning()) {
                    LOG.debug("Starting scheduled data update {}", new Date());
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
            LOG.debug("Starting scheduled SEO update {}", new Date());
            try {
                if (!seoService.isRunning()) {
                    seoService.update();
                }
            } catch (Exception e) {
                LOG.error("SEO execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
    
    @Scheduled(cron = "${scheduling.data.incremental.cron}")
    public void runIncrementalDataUpdate() {
        if (incrementalEnabled) {
            LOG.debug("Starting scheduled incremental data update {}", new Date());
            
            try {
                if (!runningServiceChecker.isAnyServiceRunning()) {
                    LOG.debug("indexing is not running, starting incremental indexing.");
                    this.incrementalUpdateService.updateChangedEducationData();
                } else {
                    LOG.debug("indexing is running, not starting incremental indexing.");
                }
            } catch (Exception e) {
                LOG.error("Incremental data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
    
    @Scheduled(cron = "${scheduling.data.articles.cron}")
    public void runArticleUpdate() {
        if (this.articlesEnabled) {
            LOG.debug("Starting scheduled article update {}", new Date());
            
            try {
                if (!runningServiceChecker.isAnyServiceRunning()) {
                    LOG.debug("indexing is not running, starting article indexing.");
                    this.updateService.updateArticles();
                } else {
                    LOG.debug("indexing is running, not starting article indexing.");
                }
            } catch (Exception e) {
                LOG.error("Incremental data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }
}
