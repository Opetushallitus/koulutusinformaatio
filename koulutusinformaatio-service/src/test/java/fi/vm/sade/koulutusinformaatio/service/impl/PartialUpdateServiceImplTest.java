package fi.vm.sade.koulutusinformaatio.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationOptionIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalLOSIndexer;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartialUpdateServiceImplTest {
    
    private static final int INNER_THREAD_DELAY = 50;
    private static final int OUTER_THREAD_DELAY = 20;
    private final static String EDUCATION_OID = "19.231.4142";
    private final static String APPLICATION_OID = "123.123.123";
    private final static String APPLICATION_OPTION_OID = "321.234.551.5354";
    
    
    @Mock
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    @Mock
    private IncrementalApplicationSystemIndexer indexer;
    
    @Mock
    private IncrementalApplicationOptionIndexer aoIndexer;
    
    @Mock
    private IncrementalLOSIndexer losIndexer;
    
    @Mock
    private TransactionManager transactionManager;

    @Mock
    private IndexerService indexerService;
    
    @Mock
    private TarjontaRawService tarjontaService;
    
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
        doAnswer(delayedAnswer).when(aoIndexer).indexApplicationOptionData(any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class));
        
        HakukohdeV1RDTO ao = new HakukohdeV1RDTO();
        ao.setOid(APPLICATION_OPTION_OID);
        ao.setHakuOid(APPLICATION_OID);
        when(tarjontaService.getV1EducationHakukohode(APPLICATION_OPTION_OID)).thenReturn(new ResultV1RDTO<HakukohdeV1RDTO>(ao));
        when(tarjontaService.getV1EducationHakuByOid(APPLICATION_OID)).thenReturn(new ResultV1RDTO<HakuV1RDTO>(new HakuV1RDTO()));
    }
    
    @Test
    public void isNotInitiallyRunning() {
        assertFalse(service.isRunning());
    }
    
    @Test
    public void startsRunningApplicationIndexing() throws Exception {
        updateApplicationOnSeparateThreadAndSleep();
        assertServiceIsRunning();
    }

    
    @Test
    public void startsRunningEducationIndexing() throws Exception {
        updateEducationOnSeparateThreadAndSleep();
        assertServiceIsRunning();
    }
    
    @Test
    public void startsRunningApplicationOptionIndexing() throws Exception {
        updateApplicationOptionOnSeparateThreadAndSleep();
        assertServiceIsRunning();
    }
    
    @Test
    public void indexesEducation() throws Exception {
        service.updateEducation(EDUCATION_OID);
        verify(losIndexer).indexLoiData(EDUCATION_OID);
    }
    
    @Test
    public void indexesApplicationSystem() throws Exception {
        service.updateApplicationSystem(APPLICATION_OID);
        verify(indexer).indexApplicationSystemData(APPLICATION_OID);
    }
    
    @Test
    public void indexesApplicationOption() throws Exception {
        service.updateApplicationOption(APPLICATION_OPTION_OID);
        verify(aoIndexer).indexApplicationOptionData(any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class));
    }
    
    private void assertServiceIsRunning() {
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
    
    private void updateApplicationOptionOnSeparateThreadAndSleep() throws Exception {
        new Thread(new Runnable() {

            @Override
            public void run() {
                service.updateApplicationOption(APPLICATION_OPTION_OID);
            }
        }).start();
        Thread.sleep(OUTER_THREAD_DELAY);
    }
}
