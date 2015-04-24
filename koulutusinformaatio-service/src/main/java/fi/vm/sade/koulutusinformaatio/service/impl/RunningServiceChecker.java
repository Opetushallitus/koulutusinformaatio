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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.RunningService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;

/**
 * @author risal1
 *
 */
@Service
@Profile("default")
public class RunningServiceChecker {
    
    private final List<RunningService> services;
    
    @Autowired
    public RunningServiceChecker(UpdateService updateService, 
            PartialUpdateService partialUpdateService,
            IncrementalUpdateService incrementalUpdateService) {
        services = Arrays.asList(updateService, partialUpdateService, incrementalUpdateService);
    }
    
    public boolean isAnyServiceRunning() {
        return Iterables.tryFind(services, new Predicate<RunningService>() {

            @Override
            public boolean apply(RunningService input) {
                return input.isRunning();
            }
            
        }).isPresent();
    }
    
    public Date getRunningSince() {
        for (RunningService service : services) {
            if (service.isRunning()) {
                return new Date(service.getRunningSince());
            }
        }
        return null;
    }

}

