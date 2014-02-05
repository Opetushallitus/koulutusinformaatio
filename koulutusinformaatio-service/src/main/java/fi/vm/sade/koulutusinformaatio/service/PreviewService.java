package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

public interface PreviewService {
	
	/**
     * Gets the parent learning opportunity by oid
     * @param oid
     * @return
     */
    HigherEducationLOS previewHigherEducationLearningOpportunity(final String oid) throws ResourceNotFoundException;

}
