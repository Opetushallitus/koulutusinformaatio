package fi.vm.sade.koulutusinformaatio.integrationtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;

@Service
public class TestHelper {

    @Autowired
    ApplicationOptionDAO applicationOptionDAO;

    @Autowired
    LearningOpportunityProviderDAO learningOpportunityProviderDAO;


    @Autowired
    KoulutusLOSDAO koulutusLOSDAO;

    @Autowired
    AdultVocationalLOSDAO adultVocationalLOSDAO;

    @Autowired
    TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    HigherEducationLOSDAO higherEducationLOSDAO;

    public void removeTestData() {
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        koulutusLOSDAO.getCollection().drop();
        adultVocationalLOSDAO.getCollection().drop();
        tutkintoLOSDAO.getCollection().drop();
        higherEducationLOSDAO.getCollection().drop();
    }

}

