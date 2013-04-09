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
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunitySearchResult;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunityDTO;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityResource;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.PathParam;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityResourceImpl implements LearningOpportunityResource {

    private SearchService searchService;
    private ModelMapper modelMapper;
    private EducationDataService educationDataService;

    @Autowired
    public LearningOpportunityResourceImpl(SearchService searchService, ModelMapper modelMapper,
                                           EducationDataService educationDataService) {
        this.searchService = searchService;
        this.modelMapper = modelMapper;
        this.educationDataService = educationDataService;
    }

    @Override
    public List<LearningOpportunitySearchResultDTO> searchLearningOpportunities(String text) {
        List<LearningOpportunitySearchResult> learningOpportunities = searchService.searchLearningOpportunities(text);
        return Lists.transform(learningOpportunities, new Function<LearningOpportunitySearchResult, LearningOpportunitySearchResultDTO>() {
            @Override
            public LearningOpportunitySearchResultDTO apply(LearningOpportunitySearchResult input) {
                return modelMapper.map(input, LearningOpportunitySearchResultDTO.class);
            }
        });
    }

    @Override
    public ParentLearningOpportunityDTO getParentLearningOpportunity(String parentId) {
        ParentLearningOpportunity parent = educationDataService.getParentLearningOpportunity(parentId);
        return modelMapper.map(parent, ParentLearningOpportunityDTO.class);
    }
}
