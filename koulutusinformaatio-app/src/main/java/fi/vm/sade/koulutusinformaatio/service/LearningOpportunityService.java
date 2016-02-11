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

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.KoulutusLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.StandaloneLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.TutkintoLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

/**
 * @author Mikko Majapuro
 */
public interface LearningOpportunityService {

    TutkintoLOSDTO getTutkintoLearningOpportunity(String id, String lang, String uiLang, String prerequisite) throws ResourceNotFoundException;

    /**
     * Gets a higher education learning opportunity.
     *
     * @param id
     *            oid of the learning opportunity
     * @return higher education learning opportunity
     * @throws ResourceNotFoundException
     */
    HigherEducationLOSDTO getHigherEducationLearningOpportunity(String id) throws ResourceNotFoundException;

    /**
     * Gets a higher education learning opportunity.
     *
     * @param id
     *            oid of the learning opportunity
     * @param uiLang
     *            the language of the user interface
     * @return higher education learning opportunity
     * @throws ResourceNotFoundException
     */
    HigherEducationLOSDTO getHigherEducationLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    /**
     * Gets a higher education learning opportunity.
     *
     * @param id
     *            oid of the learning opportunity
     * @param lang
     *            the preferred language for displaying the content
     * @param uiLang
     *            the language of the user interface
     * @return higher education learning opportunity
     * @throws ResourceNotFoundException
     */
    HigherEducationLOSDTO getHigherEducationLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;

    List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, String lopId, String baseEducation, boolean vocational, boolean nonVocational,
            boolean ongoing, String uiLang);

    ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) throws ResourceNotFoundException;

    List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, String lang, String uiLang) throws InvalidParametersException;

    List<BasketItemDTO> getBasketItems(List<String> aoId, String uiLang) throws InvalidParametersException;

    DataStatus getLastDataStatus();

    PictureDTO getPicture(final String id) throws ResourceNotFoundException;

    List<LearningOpportunitySearchResultDTO> findLearningOpportunitiesByProviderId(String providerId, String lang);

    /**
     *
     * Fetches (from tarjonta) and returns a higher education learning opportunity for preview.
     *
     * @param id
     *            the oid of the learning opportunity
     * @param lang
     *            the lang
     * @param uiLang
     *            the language of the user interface.
     * @return the requested higher education learning opportunity
     * @throws ResourceNotFoundException
     */
    HigherEducationLOSDTO previewHigherEdLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;

    DataStatus getLastSuccesfulDataStatus();

    KoulutusLOSDTO getKoulutusLearningOpportunity(String id) throws ResourceNotFoundException;

    KoulutusLOSDTO getKoulutusLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    KoulutusLOSDTO getKoulutusLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;

    AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(String id) throws ResourceNotFoundException;

    AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException;

    AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(String id, String lang, String uiLang) throws ResourceNotFoundException;

    AdultVocationalParentLOSDTO previewAdultVocationalLearningOpportunity(String oid, String lang, String uiLang) throws ResourceNotFoundException;

    StandaloneLOSDTO previewKoulutusLearningOpportunity(String oid, String lang, String uiLang) throws ResourceNotFoundException;

    LearningOpportunityProviderDTO getProvider(String lopId, String lang) throws ResourceNotFoundException;

    PictureDTO getThumbnail(String lopId) throws ResourceNotFoundException;
}
