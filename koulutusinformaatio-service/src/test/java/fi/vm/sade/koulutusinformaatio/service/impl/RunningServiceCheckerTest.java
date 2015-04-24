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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.RunningService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

/**
 * @author risal1
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RunningServiceCheckerTest {
    
    /**
     * 
     */
    private static final long TIME = 5L;

    @Mock
    private UpdateService updateService;
    
    @Mock
    private PartialUpdateService partialUpdateService;
    
    @Mock
    private IncrementalUpdateService incrementalUpdateService;
    
    @InjectMocks
    private RunningServiceChecker checker;

    @Test
    public void returnsFalseIfNoServiceIsRunning() throws Exception {
        assertFalse(checker.isAnyServiceRunning());
        assertNull(checker.getRunningSince());
    }  
    
    @Test
    public void returnsRunningInformationWhenUpdateServiceIsRunning() throws Exception {
        serviceReturnsRunningInformation(updateService);
        assertRunningInformation();
    }  
    

    @Test
    public void returnsRunningInformationWhenPartialUpdateServiceIsRunning() throws Exception {
        serviceReturnsRunningInformation(partialUpdateService);
        assertRunningInformation();
    }  
    
    @Test
    public void returnsRunningInformationWhenIncrementaServiceIsRunning() throws Exception {
        serviceReturnsRunningInformation(incrementalUpdateService);
        assertRunningInformation();
    }  
    
    private void assertRunningInformation() {
        assertTrue(checker.isAnyServiceRunning());
        assertEquals(new Date(5L), checker.getRunningSince());
    }

    private void serviceReturnsRunningInformation(RunningService service){
        when(service.isRunning()).thenReturn(true);
        when(service.getRunningSince()).thenReturn(TIME);
    }
    
}
