package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;

public interface EducationIncrementalDataUpdateService {
    
    void save(final DataStatus dataStatus);
    
    void deleteLos(LOS los);
    
    void updateHigherEdLos(HigherEducationLOS curParent);

    void updateAdultVocationalLos(
            CompetenceBasedQualificationParentLOS createdLos);

    void updateKoulutusLos(KoulutusLOS createdLos);

    void updateTutkintoLos(TutkintoLOS los);

}
