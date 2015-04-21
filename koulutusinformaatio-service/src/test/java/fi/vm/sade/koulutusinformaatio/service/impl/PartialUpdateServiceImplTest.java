package fi.vm.sade.koulutusinformaatio.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartialUpdateServiceImplTest {
    
    private static final int INNER_THREAD_DELAY = 50;
    private static final int OUTER_THREAD_DELAY = 20;
    private final static String EDUCATION_OID = "19.231.4142";
    private final static String APPLICATION_OID = "123.123.123";
    
    
    @Mock
    private UpdateService updateService;
    
    @Mock
    private IncrementalUpdateService incrementalUpdateService;
    
    @Mock
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    @Mock
    private IncrementalApplicationSystemIndexer indexer;
    
    @Mock
    private IncrementalLOSIndexer losIndexer;
    
    @InjectMocks
    private PartialUpdateService service = new PartialUpdateServiceImpl();
    
    @Before
    public void init() throws Exception {
        Answer<Void> delayedAnswer = new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(INNER_THREAD_DELAY);
                return null;
            }
            
        };
        doAnswer(delayedAnswer).when(indexer).indexApplicationSystemData(APPLICATION_OID);
        doAnswer(delayedAnswer).when(losIndexer).indexLoiData(EDUCATION_OID);
    }
    
    @Test
    public void isNotInitiallyRunning() {
        assertFalse(service.isRunning());
    }
    
    @Test
    public void doesNotStartRunningIfUpdateServiceIsRunning() {
        when(updateService.isRunning()).thenReturn(true);
        service.updateEducation(EDUCATION_OID);
        service.updateApplicationSystem(APPLICATION_OID);
        assertFalse(service.isRunning());
    }
    
    @Test
    public void doesNotStartRunningIfIncrementalUpdateServiceIsRunning() {
        when(incrementalUpdateService.isRunning()).thenReturn(true);
        service.updateEducation(EDUCATION_OID);
        service.updateApplicationSystem(APPLICATION_OID);
        assertFalse(service.isRunning());
    }
    
    @Test
    public void startsRunningApplicationIndexing() throws Exception {
        updateApplicationOnSeparateThreadAndSleep();
        assertTrue(service.isRunning());
        assertTrue(service.getRunningSince() > 0l);
    }
    
    @Test
    public void startsRunningEducationIndexing() throws Exception {
        updateEducationOnSeparateThreadAndSleep();
        assertTrue(service.isRunning());
        assertTrue(service.getRunningSince() > 0l);
    }
    
    private void updateEducationOnSeparateThreadAndSleep() throws Exception {
        new Thread(new Runnable() {

            @Override
            public void run() {
                service.updateEducation(EDUCATION_OID);
            }
        }).start();
        Thread.sleep(OUTER_THREAD_DELAY);
    }
    
    private void updateApplicationOnSeparateThreadAndSleep() throws Exception {
        new Thread(new Runnable() {

            @Override
            public void run() {
                service.updateApplicationSystem(APPLICATION_OID);
            }
        }).start();
        Thread.sleep(OUTER_THREAD_DELAY);
    }
}
