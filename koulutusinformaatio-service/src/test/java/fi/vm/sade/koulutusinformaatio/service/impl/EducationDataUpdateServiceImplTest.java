package fi.vm.sade.koulutusinformaatio.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.SpecialLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * @author Mikko Majapuro
 */
public class EducationDataUpdateServiceImplTest extends AbstractEducationServiceTest {

    private EducationDataUpdateServiceImpl service;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;
    private HigherEducationLOSDAO higherEdDAO;
    private AdultVocationalLOSDAO adultVocDAO;
    private KoulutusLOSDAO koulutusDAO;
    private TutkintoLOSDAO tutkintoDAO;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        applicationOptionDAO = mockApplicationOptionDAO();
        learningOpportunityProviderDAO = mockProviderDAO();
        childLearningOpportunityDAO = mockChildDAO();
        pictureDAO = mockPictureDAO();
        upperSecondaryLearningOpportunitySpecificationDAO = mockUpSecDAO();
        specialLearningOpportunitySpecificationDAO = mockSpecialDAO();
        dataStatusDAO = mockDataStatudDAO();
        higherEdDAO = mockHigherEdDAO();
        koulutusDAO = mock(KoulutusLOSDAO.class);
        tutkintoDAO = mock(TutkintoLOSDAO.class);
        adultVocDAO = mock(AdultVocationalLOSDAO.class);
        service = new EducationDataUpdateServiceImpl(modelMapper, applicationOptionDAO, learningOpportunityProviderDAO,
                pictureDAO, upperSecondaryLearningOpportunitySpecificationDAO, dataStatusDAO, specialLearningOpportunitySpecificationDAO,
                higherEdDAO, koulutusDAO, tutkintoDAO, adultVocDAO);
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
