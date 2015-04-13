/**
 * Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;

/**
 * @author risal1
 *
 */
@Service
@Profile("default")
public class PartialUpdateServiceImpl implements PartialUpdateService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PartialUpdateServiceImpl.class);
    
    private long runningSince = 0l;
    private boolean running = false;
    
    @Autowired
    private UpdateService updateService;
    
    @Autowired
    private IncrementalUpdateService incrementalUpdateService;
    
    @Autowired
    private TarjontaRawService tarjontaService;
    
    @Autowired
    private IncrementalApplicationSystemIndexer asIndexer;
    
    @Autowired
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    @Async
    @Override
    public void updateEducation(String oid) {
        if (startRunningIfNoIndexingIsRunning()) {
            //running = false;
        }
    }
    
    @Async
    @Override
    public void updateApplication(String oid) {
        if (startRunningIfNoIndexingIsRunning()) {
            indexHaku(oid);
        }
        dataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
        running = false;
    }

    private void indexHaku(String oid) {
        try {
            LOGGER.debug("Indexing application system: " + oid);
            asIndexer.indexApplicationSystemData(oid);
        } catch (Exception ex) {
            LOGGER.error("Error indexing application system: " + oid, ex);
        }
    }
    
    private boolean startRunningIfNoIndexingIsRunning() {
        if (isRunning() || updateService.isRunning() || incrementalUpdateService.isRunning()) {
            LOGGER.debug("Indexing is running, not starting");
            return false;
        }
        running = true;
        runningSince = System.currentTimeMillis();
        return true;
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
