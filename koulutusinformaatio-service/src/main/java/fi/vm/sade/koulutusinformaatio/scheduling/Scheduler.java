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

import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public Scheduler(final UpdateService updateService) {
        this.updateService = updateService;
    }

    @Scheduled(cron = "${scheduling.cron}")
    public void doTask() {
        LOG.info("Starting scheduled data update {}", new Date());
        try {
            updateService.updateAllEducationData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
