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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;
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
                                                             List<String> cities, int start, int rows) {
        String key = null;
        try {
            key = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            key = text;
        }
        try {
            LOSearchResultList learningOpportunities = searchService.searchLearningOpportunities(key, prerequisite,
                    cities, start, rows);
            return modelMapper.map(learningOpportunities, LOSearchResultListDTO.class);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang) {
        try {
            if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getParentLearningOpportunity(parentId);
            } else {
                return learningOpportunityService.getParentLearningOpportunity(parentId, lang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public ChildLearningOpportunityDTO getChildLearningOpportunity(String cloId, String lang) {
        try {
            if (Strings.isNullOrEmpty(lang)) {
                return learningOpportunityService.getChildLearningOpportunity(cloId);
            } else {
                return learningOpportunityService.getChildLearningOpportunity(cloId, lang.toLowerCase());
            }
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
