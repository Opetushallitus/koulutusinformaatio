package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.*;

public interface EducationIncrementalDataUpdateService {
    
    void save(final LOS learningOpportunitySpecification);

    void save(final DataStatus dataStatus);
    
    void deleteLos(LOS los);
    
    void deleteAo(ApplicationOption ao);

    void updateHigherEdLos(HigherEducationLOS curParent);

    void updateAdultUpsecLos(AdultUpperSecondaryLOS createdLos);

    void updateAdultVocationalLos(
            CompetenceBasedQualificationParentLOS createdLos);

    void updateKoulutusLos(KoulutusLOS createdLos);

    void updateTutkintoLos(TutkintoLOS los);

}
