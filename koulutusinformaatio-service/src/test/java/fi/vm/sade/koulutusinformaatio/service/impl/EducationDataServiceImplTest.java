package fi.vm.sade.koulutusinformaatio.service.impl;

import com.mongodb.DBCollection;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class EducationDataServiceImplTest {

    private EducationDataServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO;
    private ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO;
    private DBCollection ploCollection;
    private DBCollection aoCollection;
    private DBCollection lopCollection;
    private DBCollection cloiCollection;
    private DBCollection closCollection;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunitySpecificationDAO = mock(ParentLearningOpportunitySpecificationDAO.class);
        ploCollection = mock(DBCollection.class);
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentLearningOpportunitySpecificationDAO.getCollection()).thenReturn(ploCollection);
        when(parentLearningOpportunitySpecificationDAO.get(eq("1.2.3"))).thenReturn(plo);
        applicationOptionDAO = mock(ApplicationOptionDAO.class);
        aoCollection = mock(DBCollection.class);
        when(applicationOptionDAO.getCollection()).thenReturn(aoCollection);

        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("8.9.0");
        aos.add(ao);
        when(applicationOptionDAO.find(eq("1.1.1"), eq("9.9.9"))).thenReturn(aos);
        learningOpportunityProviderDAO = mock(LearningOpportunityProviderDAO.class);
        lopCollection = mock(DBCollection.class);
        when(learningOpportunityProviderDAO.getCollection()).thenReturn(lopCollection);

        cloiCollection = mock(DBCollection.class);
        childLearningOpportunityInstanceDAO = mock(ChildLearningOpportunityInstanceDAO.class);
        when(childLearningOpportunityInstanceDAO.getCollection()).thenReturn(cloiCollection);

        closCollection = mock(DBCollection.class);
        childLearningOpportunitySpecificationDAO = mock(ChildLearningOpportunitySpecificationDAO.class);
        when(childLearningOpportunitySpecificationDAO.getCollection()).thenReturn(closCollection);

        KoulutusinformaatioObjectBuilder objectBuilder = new KoulutusinformaatioObjectBuilder(modelMapper);

        service = new EducationDataServiceImpl(parentLearningOpportunitySpecificationDAO, applicationOptionDAO,
                learningOpportunityProviderDAO, modelMapper, childLearningOpportunitySpecificationDAO, childLearningOpportunityInstanceDAO,
                objectBuilder);
    }

    @Test
    public void testSaveParentLearningOpportunity() {
        List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
        LearningOpportunityProvider lop = new LearningOpportunityProvider();
        lop.setId("6.7.8");
        ApplicationOption ao = new ApplicationOption();
        ao.setId("3.3.3");
        ao.setProvider(lop);
        applicationOptions.add(ao);
        ParentLOS plo = new ParentLOS();
        plo.setProvider(lop);
        plo.setId("1.2.3");
        plo.setApplicationOptions(applicationOptions);
        ChildLOS clo = new ChildLOS();
        clo.setId("2.2.2");

        ChildLOI cloi = new ChildLOI();
        cloi.setId("5.7.9");
        cloi.setApplicationSystemId("1.2.3.4.5");
        cloi.setApplicationOption(ao);

        List<ChildLOI> childLOIs = new ArrayList<ChildLOI>();
        childLOIs.add(cloi);
        clo.setChildLOIs(childLOIs);

        List<ChildLOS> children = new ArrayList<ChildLOS>();
        children.add(clo);
        plo.setChildren(children);

        service.save(plo);
        verify(parentLearningOpportunitySpecificationDAO, times(1)).save(any(ParentLearningOpportunitySpecificationEntity.class));
        verify(applicationOptionDAO, times(2)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(3)).save(any(LearningOpportunityProviderEntity.class));
    }

    @Test
    public void testDropAll() {
        service.dropAllData();
        verify(ploCollection, times(1)).drop();
        verify(aoCollection, times(1)).drop();
        verify(lopCollection, times(1)).drop();
        verify(closCollection, times(1)).drop();
        verify(cloiCollection, times(1)).drop();
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLO plo = service.getParentLearningOpportunity("1.2.3");
        assertNotNull(plo);
        assertEquals("1.2.3", plo.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetParentLearningOpportunityNotExists() throws ResourceNotFoundException {
        ParentLO plo = service.getParentLearningOpportunity("1.1.1");
    }

    @Test
    public void testFindApplicationOptions() {
        List<ApplicationOption> result = service.findApplicationOptions("1.1.1", "9.9.9");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("8.9.0", result.get(0).getId());
    }
}
