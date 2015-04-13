package fi.vm.sade.koulutusinformaatio.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PartialUpdateServiceImplTest {
    
    private final static String EDUCATION_OID = "19.231.4142";

    @Mock
    private UpdateService updateService;
    
    @Mock
    private IncrementalUpdateService incrementalUpdateService;
    
    @InjectMocks
    private PartialUpdateService service = new PartialUpdateServiceImpl();
    
    @Test
    public void isNotInitiallyRunning() {
        assertFalse(service.isRunning());
    }
    
    @Test
    public void doesNotStartRunningIfUpdateServiceIsRunning() {
        when(updateService.isRunning()).thenReturn(true);
        service.updateEducation(EDUCATION_OID);
        assertFalse(service.isRunning());
    }
    
    @Test
    public void doesNotStartRunningIfIncrementalUpdateServiceIsRunning() {
        when(incrementalUpdateService.isRunning()).thenReturn(true);
        service.updateEducation(EDUCATION_OID);
        assertFalse(service.isRunning());
    }
    
    @Test
    public void startsRunning() {
        service.updateEducation(EDUCATION_OID);
        assertTrue(service.isRunning());
        assertTrue(service.getRunningSince() > 0l);
    }
}
