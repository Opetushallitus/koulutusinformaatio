package fi.vm.sade.koulutusinformaatio.service;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public interface EducationIncrementalDataQueryService {

    List<ApplicationOption> getApplicationOptions(final List<String> aoIds) throws InvalidParametersException;

    ApplicationOption getApplicationOption(final String aoId) throws ResourceNotFoundException;

    Picture getPicture(final String id) throws ResourceNotFoundException;

    /**
     * Retrieves an upper secondary learning opportunity specification.
     *
     * @param id los id
     * @return upper secondary los
     * @throws ResourceNotFoundException
     */
    UpperSecondaryLOS getUpperSecondaryLearningOpportunity(final String id) throws ResourceNotFoundException;

    /**
     * Retrieves a university of applied science learning opportunity specification.
     *
     * @param id los id
     * @return university of applied science los
     * @throws ResourceNotFoundException
     */
    HigherEducationLOS getHigherEducationLearningOpportunity(final String oid) throws ResourceNotFoundException;
    
    /**
     * Retrieves a learning opportunity provider.
     *
     * @param id provider id
     * @return provider
     * @throws ResourceNotFoundException no provider found with the id
     */
    Provider getProvider(final String id) throws ResourceNotFoundException;

    LOS getLos(String losId);

    List<KoulutusLOS> getKoulutusLos(ToteutustyyppiEnum toteutustyyppi, String tarjoaja, String koulutusKoodi) throws ResourceNotFoundException;
    
    List<LOS> findLearningOpportunitiesByLoiId(String loiId);
    
    DataStatus getLatestSuccessDataStatus();

    List<String> getLearningOpportunityIdsByAS(String asId);

    AdultUpperSecondaryLOS getAdultUpsecLearningOpportunity(String curLosId) throws ResourceNotFoundException;

    TutkintoLOS getTutkinto(String oid) throws ResourceNotFoundException;


}
