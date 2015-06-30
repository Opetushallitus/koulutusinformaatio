package fi.vm.sade.koulutusinformaatio.service;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

public interface EducationIncrementalDataQueryService {

    /**
     * Finds application options by the application system, learning opportunity provider and base education.
     * @param asId application system id
     * @param lopId learning opportunity provider id
     * @param baseEducation base education identifier from koodisto
     * @return list of the application options
     */
    List<ApplicationOption> findApplicationOptions(final String asId, final String lopId, final String baseEducation,
                                                   boolean vocational, boolean nonVocational);

    List<ApplicationOption> getApplicationOptions(final List<String> aoIds) throws InvalidParametersException;

    ApplicationOption getApplicationOption(final String aoId) throws ResourceNotFoundException;

    /**
     * Gets the child learning opportunity
     * @param childLoId child learning opportunity id
     * @return child learning opportunity
     */
    ChildLOS getChildLearningOpportunity(final String childLoId) throws ResourceNotFoundException;

    DataStatus getLatestDataStatus();

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
     * Retrieves a special learning opportunity specification.
     *
     * @param id los id
     * @return special los
     * @throws ResourceNotFoundException
     */
    SpecialLOS getSpecialLearningOpportunity(final String id) throws ResourceNotFoundException;
    
    
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

    /**
     * Finds all learning opportunitySpecifications by provider id.
     *
     * @param providerId provider id
     * @return list of learning oppportunity search results
     */
    List<LOS> findLearningOpportunitiesByProviderId(String providerId);
    
    LOS getLos(String losId);
    
    List<LOS> findLearningOpportunitiesByLoiId(String loiId);
    
    DataStatus getLatestSuccessDataStatus();

    List<String> getLearningOpportunityIdsByAS(String asId);

    AdultUpperSecondaryLOS getAdultUpsecLearningOpportunity(String curLosId) throws ResourceNotFoundException;

    TutkintoLOS getTutkinto(String oid) throws ResourceNotFoundException;

}
