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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.converter.CodeToDTO;
import fi.vm.sade.koulutusinformaatio.converter.ProviderToDTO;
import fi.vm.sade.koulutusinformaatio.converter.ProviderToSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.CodeDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityProviderService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class LearningOpportunityProviderServiceImpl implements LearningOpportunityProviderService {

    private SearchService searchService;
    private EducationDataQueryService educationDataQueryService;

    @Autowired
    public LearningOpportunityProviderServiceImpl(SearchService searchService, EducationDataQueryService educationDataQueryService) {
        this.searchService = searchService;
        this.educationDataQueryService = educationDataQueryService;
    }

    @Override
    public List<String> getProviderNameFirstCharacters(String lang) throws SearchException {
        return searchService.getProviderFirstCharacterList(lang);
    }

    @Override
    public LearningOpportunityProviderDTO getProvider(String id, String lang) throws ResourceNotFoundException {
        return ProviderToDTO.convert(educationDataQueryService.getProvider(id), lang, lang, lang);
    }

    @Override
    public List<ProviderSearchResultDTO> searchProviders(String term, String lang, String type) throws SearchException {
        return ProviderToSearchResult.convertAll(
                searchService.searchLearningOpportunityProviders(term, lang, true, type), lang);
    }

    @Override
    public List<CodeDTO> getProviderTypes(String firstCharacter, String lang) throws SearchException {
        return CodeToDTO.convertAll(searchService.getProviderTypes(firstCharacter, lang), lang);
    }
}
