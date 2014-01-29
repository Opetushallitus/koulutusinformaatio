package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

public interface PreviewService {
	
	/**
     * Gets the parent learning opportunity by oid
     * @param oid
     * @return
     */
    UniversityAppliedScienceLOS previewHigherEducationLearningOpportunity(final String oid) throws ResourceNotFoundException;

}
