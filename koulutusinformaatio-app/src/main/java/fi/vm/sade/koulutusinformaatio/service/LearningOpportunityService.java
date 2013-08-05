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

import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public interface LearningOpportunityService {

    ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException;

    ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang) throws ResourceNotFoundException;

    ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId) throws ResourceNotFoundException;

    ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String lang) throws ResourceNotFoundException;

    List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, String lopId, String baseEducation);

    ApplicationOptionDTO getApplicationOption(String aoId, String lang) throws ResourceNotFoundException;
    List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, String lang);

    List<BasketItemDTO> getBasketItems(List<String> aoId, String lang);

    Date getLastDataUpdated();

    PictureDTO getPicture(final String id) throws ResourceNotFoundException;
}
