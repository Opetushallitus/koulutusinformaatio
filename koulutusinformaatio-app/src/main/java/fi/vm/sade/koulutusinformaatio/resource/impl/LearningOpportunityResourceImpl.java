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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityResourceImpl implements LearningOpportunityResource {

    private SearchService searchService;
    private ModelMapper modelMapper;
    private LearningOpportunityService learningOpportunityService;

    @Autowired
    public LearningOpportunityResourceImpl(SearchService searchService, ModelMapper modelMapper,
            LearningOpportunityService learningOpportunityService) {
        this.searchService = searchService;
        this.modelMapper = modelMapper;
        this.learningOpportunityService = learningOpportunityService;
    }

    @Override
    public LOSearchResultListDTO searchLearningOpportunities(String text, String prerequisite, 
            List<String> cities, List<String> facetFilters, String lang, boolean ongoing, boolean upcoming, 
            int start, int rows, String sort, String order, String lopFilter, String educationCodeFilter,
            List<String> excludes, SearchType searchType) {
        String key = null;
        try {
            key = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            key = text;
        }
        try {
            sort = (sort != null && !sort.isEmpty()) ? sort : null;
            LOSearchResultList learningOpportunities = searchService.searchLearningOpportunities(key, prerequisite,
                    cities, facetFilters, lang, ongoing, upcoming, start, rows, sort, order, 
                    lopFilter, educationCodeFilter, excludes, searchType);
            return modelMapper.map(learningOpportunities, LOSearchResultListDTO.class);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang, String uiLang) {
        try {
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                return learningOpportunityService.getParentLearningOpportunity(parentId);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getParentLearningOpportunity(parentId, uiLang.toLowerCase());
            }
            else {
                return learningOpportunityService.getParentLearningOpportunity(parentId, lang.toLowerCase(), uiLang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String lang, String uiLang) {
        try {
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                return learningOpportunityService.getChildLearningOpportunity(cloId);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getChildLearningOpportunity(cloId, uiLang.toLowerCase());
            }
            else {
                return learningOpportunityService.getChildLearningOpportunity(cloId, lang.toLowerCase(), uiLang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(
            String id, String lang, String uiLang) {
        try {
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                return learningOpportunityService.getUpperSecondaryLearningOpportunity(id);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getUpperSecondaryLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                return learningOpportunityService.getUpperSecondaryLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public SpecialLearningOpportunitySpecificationDTO getSpecialLearningOpportunity(String id, String lang, String uiLang) {
        try {
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                return learningOpportunityService.getSpecialSecondaryLearningOpportunity(id);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getSpecialSecondaryLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                return learningOpportunityService.getSpecialSecondaryLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public SuggestedTermsResultDTO getSuggestedTerms(String term, String lang) {
        String key = null;
        try {
            key = URLDecoder.decode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            key = term;
        }
        try {
            SuggestedTermsResult suggestedTerms = this.searchService.searchSuggestedTerms(key, lang);
            return modelMapper.map(suggestedTerms, SuggestedTermsResultDTO.class);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(String id,
            String lang, String uiLang) {
        try {
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                return learningOpportunityService.getHigherEducationLearningOpportunity(id);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getHigherEducationLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                return learningOpportunityService.getHigherEducationLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public HigherEducationLOSDTO previewLearningOpportunity(String oid,
            String lang, String uiLang) {
        try {
            return learningOpportunityService.previewLearningOpportunity(oid, lang, uiLang);
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
