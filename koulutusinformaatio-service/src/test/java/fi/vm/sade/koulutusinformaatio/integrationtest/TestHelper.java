package fi.vm.sade.koulutusinformaatio.integrationtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;

@Service
public class TestHelper {

    @Autowired
    ApplicationOptionDAO applicationOptionDAO;

    @Autowired
    LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @Autowired
    ChildLearningOpportunityDAO childLearningOpportunityDAO;

    @Autowired
    KoulutusLOSDAO koulutusLOSDAO;

    @Autowired
    AdultVocationalLOSDAO adultVocationalLOSDAO;

    @Autowired
    TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    HigherEducationLOSDAO higherEducationLOSDAO;

    @Autowired
    UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunityDAO;

    @Autowired
    SpecialLearningOpportunitySpecificationDAO specialLearningOpportunityDAO;

    public void removeTestData() {
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        childLearningOpportunityDAO.getCollection().drop();
        koulutusLOSDAO.getCollection().drop();
        adultVocationalLOSDAO.getCollection().drop();
        tutkintoLOSDAO.getCollection().drop();
        higherEducationLOSDAO.getCollection().drop();
        upperSecondaryLearningOpportunityDAO.getCollection().drop();
        specialLearningOpportunityDAO.getCollection().drop();

    }

}

