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

import fi.vm.sade.koulutusinformaatio.service.SEOService;
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
    private SEOService seoService;
    private boolean enabled;

    @Autowired
    public Scheduler(final UpdateService updateService, final SEOService seoService, @Value("${scheduling.enabled}") boolean enabled) {
        this.updateService = updateService;
        this.seoService = seoService;
        this.enabled = enabled;
    }

    @Scheduled(cron = "${scheduling.data.cron}")
    public void runDateUpdate() {
        if (enabled) {
            LOG.info("Starting scheduled data update {}", new Date());
            try {
                if (!updateService.isRunning()) {
                    updateService.updateAllEducationData();
                }
            } catch (Exception e) {
                LOG.error("Data update execution failed: {}", e.getStackTrace().toString());
            }
        }
    }

    @Scheduled(cron = "${scheduling.seo.cron}")
    public void runSEOUpdate() {
        LOG.info("Starting scheduled SEO update {}", new Date());
        try {
            if (!seoService.isRunning()) {
                seoService.update();
            }
        }
        catch (Exception e) {
            LOG.error("SEO execution failed: {}", e.getStackTrace().toString());
        }
    }
}
