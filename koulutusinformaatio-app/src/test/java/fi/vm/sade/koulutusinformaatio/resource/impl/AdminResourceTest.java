package fi.vm.sade.koulutusinformaatio.resource.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.DataStatusDTO;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.impl.RunningServiceChecker;
import fi.vm.sade.koulutusinformaatio.service.tester.HakukohdeTester;

@RunWith(MockitoJUnitRunner.class)
public class AdminResourceTest {

    @Mock
    private LearningOpportunityService learningOpportunityService;
    
    @Mock
    private UpdateService updateService;
    
    @Mock
    private PartialUpdateService partialUpdateService;
    
    @Mock
    private IncrementalUpdateService incrementalUpdateService;
    
    @Mock
    private RunningServiceChecker checker;
    
    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private SEOService seoService;

    @Mock
    private HakukohdeTester hakukohdeTester;
    
    private AdminResource adminResource;
    
    @Before
    public void init() {
        when(modelMapper.getConfiguration()).thenReturn(mock(Configuration.class));
        when(learningOpportunityService.getLastDataStatus()).thenReturn(new DataStatus(new Date(), 5l, 
                "SUCCESS"));
        adminResource = new AdminResource(updateService, learningOpportunityService, modelMapper, 
                seoService, incrementalUpdateService, partialUpdateService, checker, hakukohdeTester);
    }
    
    @Test
    public void doesNotSetRunningValuesWhenNoServicesAreRunning() {
        DataStatusDTO dto = adminResource.dataStatus();
        assertFalse(dto.isRunning());
        assertNull(dto.getLastSuccessfulFinished());
        assertNull(dto.getLastSuccessfulFinishedStr());
    }
    
    @Test
    public void setsRunningValuesWhenServiceIsRunning() {
        checkerReturnsRunInformation();
        assertRunningValuesExist(adminResource.dataStatus());
    }
    
    
    private void assertRunningValuesExist(DataStatusDTO dto) {
        assertTrue(dto.isRunning());
        assertNotNull(dto.getRunningSince());
        assertNotNull(dto.getRunningSinceStr());
    }
    
    private void checkerReturnsRunInformation() {
        when(checker.isAnyServiceRunning()).thenReturn(true);
        when(checker.getRunningSince()).thenReturn(new Date());
    }
}
