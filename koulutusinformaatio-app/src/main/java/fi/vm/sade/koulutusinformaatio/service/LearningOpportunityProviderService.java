/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

import fi.vm.sade.koulutusinformaatio.domain.dto.CodeDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface LearningOpportunityProviderService {

    /**
     * Returns list of first characters that are in use in provider names.
     * Characters are in upper case.
     *
     * @return list of characters
     */
    List<String> getProviderNameFirstCharacters(String lang) throws SearchException;

    /**
     * Fetches a provider with the id. Internationalized languages
     * are translated with the lang param.
     *
     * @param id provider id
     * @param lang language that is used for translations
     * @return probider DTO object
     */
    LearningOpportunityProviderDTO getProvider(String id, String lang) throws ResourceNotFoundException;

    /**
     * Searches learning opportunity providers by name.
     * The search term can be a full provider name
     * or a substring from the beginnning of the name.
     *
     * @param term search term
     * @param lang in which language the provider name is matched
     * @param type type of the provider (oppilaitostyyppi)
     * @return matching providers
     */
    List<ProviderSearchResult> searchProviders(String term, String lang, String type) throws SearchException;

    List<CodeDTO> getProviderTypes(String firstCharacter, String lang) throws SearchException;
}
