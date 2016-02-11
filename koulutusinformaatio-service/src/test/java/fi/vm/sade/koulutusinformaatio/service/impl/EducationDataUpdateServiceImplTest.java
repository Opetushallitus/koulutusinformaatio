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
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * @author Mikko Majapuro
 */
public class EducationDataUpdateServiceImplTest extends AbstractEducationServiceTest {

    private EducationDataUpdateServiceImpl service;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
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
        pictureDAO = mockPictureDAO();
        dataStatusDAO = mockDataStatudDAO();
        higherEdDAO = mockHigherEdDAO();
        koulutusDAO = mock(KoulutusLOSDAO.class);
        tutkintoDAO = mock(TutkintoLOSDAO.class);
        adultVocDAO = mock(AdultVocationalLOSDAO.class);
        service = new EducationDataUpdateServiceImpl(modelMapper, applicationOptionDAO, learningOpportunityProviderDAO,
                pictureDAO, dataStatusDAO,
                higherEdDAO, koulutusDAO, tutkintoDAO, adultVocDAO);
    }

    @Test
    public void testSaveHigherEducationLOS() throws KIException {
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
