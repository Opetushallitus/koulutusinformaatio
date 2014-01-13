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
import fi.vm.sade.koulutusinformaatio.comparator.ProviderSearchResultComparator;
import fi.vm.sade.koulutusinformaatio.converter.ConverterUtil;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityProviderResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityProviderResourceImpl implements LearningOpportunityProviderResource {

    private SearchService searchService;
    private LearningOpportunityService learningOpportunityService;

    @Autowired
    public LearningOpportunityProviderResourceImpl(SearchService searchService, ModelMapper modelMapper,
                                                   LearningOpportunityService learningOpportunityService) {
        this.searchService = searchService;
        this.learningOpportunityService = learningOpportunityService;
    }

    @Override
    public List<ProviderSearchResult> searchProviders(String term, String asId, String baseEducation, boolean vocational,
                                                      boolean nonVocational, int start, int rows, final String lang) {
        List<Provider> learningOpportunityProviders = null;
        try {
            String key = null;
            try {
                key = URLDecoder.decode(term, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                key = term;
            }
            key = key.replace("*", "");
            learningOpportunityProviders = searchService.searchLearningOpportunityProviders(key, asId, baseEducation, vocational,
                    nonVocational, start, rows, lang, false);
            List<ProviderSearchResult> result = Lists.newArrayList(Lists.transform(learningOpportunityProviders, new Function<Provider, ProviderSearchResult>() {
                @Override
                public ProviderSearchResult apply(Provider lop) {
                    ProviderSearchResult result = new ProviderSearchResult();
                    result.setId(lop.getId());
                    result.setName(ConverterUtil.getTextByLanguageUseFallbackLang(lop.getName(), lang));
                    return result;
                }
            }));

            Collections.sort(result, new ProviderSearchResultComparator());
            return result;
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }

    }

    @Override
    public PictureDTO getProviderPicture(String lopId) {
        try {
            return learningOpportunityService.getPicture(lopId);
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
