package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class EducationDataUpdateServiceImplTest extends AbstractEducationServiceTest {

    private EducationDataUpdateServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;
    private HigherEducationLOSDAO higherEdDAO;
    private AdultUpperSecondaryLOSDAO adultUpsecDAO;
    private AdultVocationalLOSDAO adultVocDAO;
    private KoulutusLOSDAO koulutusDAO;
    private TutkintoLOSDAO tutkintoDAO;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunitySpecificationDAO = mockParentDAO();
        applicationOptionDAO = mockApplicationOptionDAO();
        learningOpportunityProviderDAO = mockProviderDAO();
        childLearningOpportunityDAO = mockChildDAO();
        pictureDAO = mockPictureDAO();
        upperSecondaryLearningOpportunitySpecificationDAO = mockUpSecDAO();
        specialLearningOpportunitySpecificationDAO = mockSpecialDAO();
        dataStatusDAO = mockDataStatudDAO();
        higherEdDAO = mockHigherEdDAO();
        adultUpsecDAO = mock(AdultUpperSecondaryLOSDAO.class);
        koulutusDAO = mock(KoulutusLOSDAO.class);
        tutkintoDAO = mock(TutkintoLOSDAO.class);
        adultVocDAO = mock(AdultVocationalLOSDAO.class);
        service = new EducationDataUpdateServiceImpl(modelMapper, parentLearningOpportunitySpecificationDAO, applicationOptionDAO,
                learningOpportunityProviderDAO, childLearningOpportunityDAO, pictureDAO, upperSecondaryLearningOpportunitySpecificationDAO, dataStatusDAO,
                specialLearningOpportunitySpecificationDAO, higherEdDAO, adultUpsecDAO, koulutusDAO, tutkintoDAO, adultVocDAO);
    }

    @Test
    public void testSaveUpperSecondaryLOS() {
        ApplicationOption ao = new ApplicationOption();
        ao.setId("aoid");
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        Provider p = new Provider("providerid", TestUtil.createI18nText("name", "name", "name"));
        UpperSecondaryLOS los = new UpperSecondaryLOS();
        los.setId("losid");
        UpperSecondaryLOI loi = new UpperSecondaryLOI();
        loi.setId("loiid");
        loi.setApplicationOptions(aos);
        los.setLois(Lists.newArrayList(loi));
        los.setProvider(p);
        service.save(los);
        verify(upperSecondaryLearningOpportunitySpecificationDAO, times(1)).save(any(UpperSecondaryLearningOpportunitySpecificationEntity.class));
        verify(applicationOptionDAO, times(1)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(1)).save(any(LearningOpportunityProviderEntity.class));
    }

    @Test
    public void testSaveSpecialLOS() {
        ApplicationOption ao = new ApplicationOption();
        ao.setId("aoid");
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        Provider p = new Provider("providerid", TestUtil.createI18nText("name", "name", "name"));
        ChildLOI loi = new ChildLOI();
        loi.setId("loiid");
        SpecialLOS los = new SpecialLOS();
        los.setId("losid");
        loi.setApplicationOptions(aos);
        los.setLois(Lists.newArrayList(loi));
        los.setProvider(p);
        service.save(los);
        verify(specialLearningOpportunitySpecificationDAO, times(1)).save(any(SpecialLearningOpportunitySpecificationEntity.class));
        verify(applicationOptionDAO, times(1)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(1)).save(any(LearningOpportunityProviderEntity.class));
    }
    
    @Test
    public void testSaveHigherEducationLOS() {
        ApplicationOption ao = new ApplicationOption();
        ao.setId("aoid");
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        Provider p = new Provider("providerid", TestUtil.createI18nText("name", "name", "name"));
        HigherEducationLOS los = new HigherEducationLOS();
        los.setApplicationOptions(aos);
        los.setProvider(p);
        service.save(los);
        verify(higherEdDAO, times(1)).save(any(HigherEducationLOSEntity.class));
        verify(applicationOptionDAO, times(1)).save(any(ApplicationOptionEntity.class));
        verify(learningOpportunityProviderDAO, times(1)).save(any(LearningOpportunityProviderEntity.class));
    }
}
