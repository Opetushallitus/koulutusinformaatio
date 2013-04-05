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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityProvider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityProviderResource;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityProviderResourceImpl implements LearningOpportunityProviderResource {


    private SearchService searchService;
    private ModelMapper modelMapper;

    @Autowired
    public LearningOpportunityProviderResourceImpl(SearchService searchService, ModelMapper modelMapper) {
        this.searchService = searchService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProviderSearchResult> searchProviders(String term, String asId, String prerequisite, boolean vocational) {
        List<LearningOpportunityProvider> learningOpportunityProviders = searchService.searchLearningOpportunityProviders(term, asId, prerequisite, vocational);
        return Lists.transform(learningOpportunityProviders, new Function<LearningOpportunityProvider, ProviderSearchResult>() {
            @Override
            public ProviderSearchResult apply(LearningOpportunityProvider lop) {
                return modelMapper.map(lop, ProviderSearchResult.class);
            }
        });
    }
}
