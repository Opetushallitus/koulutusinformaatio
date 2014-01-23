/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public interface LearningOpportunityService {

    ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException;

    ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String uiLang) throws ResourceNotFoundException;

    ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang, String uiLang) throws ResourceNotFoundException;

    ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId) throws ResourceNotFoundException;

    ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String uiLang) throws ResourceNotFoundException;

    ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String lang, String uiLang) throws ResourceNotFoundException;

    UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException;

    UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;
    
    SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id) throws ResourceNotFoundException;

    SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;
    
    UniversityAppliedScienceLOSDTO getUniversityAppliedScienceLearningOpportunity(String id) throws ResourceNotFoundException;

    UniversityAppliedScienceLOSDTO getUniversityAppliedScienceLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    UniversityAppliedScienceLOSDTO getUniversityAppliedScienceLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;

    List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, String lopId, String baseEducation, boolean vocational, boolean nonVocational);

    ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) throws ResourceNotFoundException;

    List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, String lang, String uiLang) throws InvalidParametersException;

    List<BasketItemDTO> getBasketItems(List<String> aoId, String uiLang) throws InvalidParametersException;

    DataStatus getLastDataStatus();

    PictureDTO getPicture(final String id) throws ResourceNotFoundException;

    List<LearningOpportunitySearchResultDTO> findLearningOpportunitiesByProviderId(String providerId, String lang);
}
