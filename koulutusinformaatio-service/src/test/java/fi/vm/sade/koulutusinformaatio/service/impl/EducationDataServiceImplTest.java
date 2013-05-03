package fi.vm.sade.koulutusinformaatio.service.impl;

import com.mongodb.DBCollection;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.*;
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
    private ParentLearningOpportunityDAO parentLearningOpportunityDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private DBCollection ploCollection;
    private DBCollection aoCollection;
    private DBCollection lopCollection;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunityDAO = mock(ParentLearningOpportunityDAO.class);
        ploCollection = mock(DBCollection.class);
        ParentLearningOpportunityEntity plo = new ParentLearningOpportunityEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentLearningOpportunityDAO.getCollection()).thenReturn(ploCollection);
        when(parentLearningOpportunityDAO.get(eq("1.2.3"))).thenReturn(plo);
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
        service = new EducationDataServiceImpl(parentLearningOpportunityDAO, applicationOptionDAO,
                learningOpportunityProviderDAO, modelMapper);
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
        ParentLearningOpportunity plo = new ParentLearningOpportunity();
        plo.setProvider(lop);
        plo.setId("1.2.3");
        plo.setApplicationOptions(applicationOptions);
        ChildLearningOpportunity clo = new ChildLearningOpportunity();
        clo.setId("2.2.2");
        clo.setApplicationOptions(applicationOptions);
        List<ChildLearningOpportunity> children = new ArrayList<ChildLearningOpportunity>();
        children.add(clo);
        plo.setChildren(children);

        service.save(plo);
        verify(parentLearningOpportunityDAO, times(1)).save(any(ParentLearningOpportunityEntity.class));
        verify(applicationOptionDAO, times(1)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(2)).save(any(LearningOpportunityProviderEntity.class));
    }

    @Test
    public void testDropAll() {
        service.dropAllData();
        verify(ploCollection, times(1)).drop();
        verify(aoCollection, times(1)).drop();
        verify(lopCollection, times(1)).drop();
    }

    @Test
    public void testGetParentLearningOpportunity() {
        ParentLearningOpportunity plo = service.getParentLearningOpportunity("1.2.3");
        assertNotNull(plo);
        assertEquals("1.2.3", plo.getId());
    }

    @Test
    public void testGetParentLearningOpportunityNotExists() {
        ParentLearningOpportunity plo = service.getParentLearningOpportunity("1.1.1");
        assertNull(plo);
    }

    @Test
    public void testFindApplicationOptions() {
        List<ApplicationOption> result = service.findApplicationOptions("1.1.1", "9.9.9");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("8.9.0", result.get(0).getId());
    }
}
