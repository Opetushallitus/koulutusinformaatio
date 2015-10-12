package fi.vm.sade.koulutusinformaatio.service;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public interface EducationIncrementalDataQueryService {

    LOS getLos(String losId);

    List<KoulutusLOS> getKoulutusLos(ToteutustyyppiEnum toteutustyyppi, String tarjoaja, String koulutusKoodi) throws ResourceNotFoundException;
    
    List<LOS> findLearningOpportunitiesByLoiId(String loiId);
    
    DataStatus getLatestSuccessDataStatus();

    List<String> getLearningOpportunityIdsByAS(String asId);


    TutkintoLOS getTutkinto(String oid) throws ResourceNotFoundException;


}
