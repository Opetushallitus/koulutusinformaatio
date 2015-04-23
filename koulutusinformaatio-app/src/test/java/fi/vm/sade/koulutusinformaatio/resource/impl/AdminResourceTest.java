package fi.vm.sade.koulutusinformaatio.resource.impl;

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
import fi.vm.sade.koulutusinformaatio.service.RunningService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private ModelMapper modelMapper;
    
    @Mock
    private SEOService seoService;
    
    private AdminResource adminResource;
    
    @Before
    public void init() {
        when(modelMapper.getConfiguration()).thenReturn(mock(Configuration.class));
        when(learningOpportunityService.getLastDataStatus()).thenReturn(new DataStatus(new Date(), 5l, 
                "SUCCESS"));
        adminResource = new AdminResource(updateService, learningOpportunityService, modelMapper, 
                seoService, incrementalUpdateService, partialUpdateService);
    }
    
    @Test
    public void doesNotSetRunningValuesWhenNoServicesAreRunning() {
        DataStatusDTO dto = adminResource.dataStatus();
        assertFalse(dto.isRunning());
        assertNull(dto.getLastSuccessfulFinished());
        assertNull(dto.getLastSuccessfulFinishedStr());
    }
    
    @Test
    public void setsRunningValuesWhenUpdateServiceIsRunning() {
        runningServiceReturnsRunInformation(updateService);
        assertRunningValuesExist(adminResource.dataStatus());
    }
    
    @Test
    public void setsRunningValuesWhenIncrementalUpdateServiceIsRunning() {
        runningServiceReturnsRunInformation(incrementalUpdateService);
        assertRunningValuesExist(adminResource.dataStatus());
    }
    
    @Test
    public void setsRunningValuesWhenPartialUpdateServiceIsRunning() {
        runningServiceReturnsRunInformation(partialUpdateService);
        assertRunningValuesExist(adminResource.dataStatus());
    }
    
    private void assertRunningValuesExist(DataStatusDTO dto) {
        assertTrue(dto.isRunning());
        assertNotNull(dto.getRunningSince());
        assertNotNull(dto.getRunningSinceStr());
    }
    
    private void runningServiceReturnsRunInformation(RunningService rs) {
        when(rs.isRunning()).thenReturn(true);
        when(rs.getRunningSince()).thenReturn(50l);
    }
}
