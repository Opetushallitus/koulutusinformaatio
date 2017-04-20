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
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.converter.ArticleResultToDTO;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class LearningOpportunityResourceImpl implements LearningOpportunityResource {

    private SearchService searchService;
    private ModelMapper modelMapper;
    private LearningOpportunityService learningOpportunityService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningOpportunityResourceImpl.class);
    private static final String LANG_FI = "fi";

    @Autowired
    public LearningOpportunityResourceImpl(SearchService searchService, ModelMapper modelMapper,
            LearningOpportunityService learningOpportunityService) {
        this.searchService = searchService;
        this.modelMapper = modelMapper;
        this.learningOpportunityService = learningOpportunityService;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public LOSearchResultListDTO searchLearningOpportunities(String text,
                                                            String prerequisite,
                                                            List<String> cities,
                                                            List<String> facetFilters,
                                                            List<String> articleFilters,
                                                            List<String> providerFilters,
                                                            String lang,
                                                            boolean ongoing,
                                                            boolean upcoming,
                                                            boolean upcomingLater,
                                                            int start,
                                                            int rows,
                                                            String sort,
                                                            String order,
                                                            String lopFilter,
                                                            String educationCodeFilter,
                                                            List<String> excludes,
                                                            String asId,
                                                            SearchType searchType) {
        try {
            sort = (sort != null && !sort.isEmpty()) ? sort : null;
            LOSearchResultList learningOpportunities = searchService.searchLearningOpportunities(text, prerequisite,
                    cities, facetFilters, articleFilters, providerFilters, lang, ongoing, upcoming, upcomingLater, start, rows, sort, order,
                    lopFilter, educationCodeFilter, excludes, asId, searchType);
            return modelMapper.map(learningOpportunities, LOSearchResultListDTO.class);
        } catch (SearchException e) {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("text", text);
            paramMap.put("prerequisite", prerequisite);
            paramMap.put("cities", cities);
            paramMap.put("facetFilters", facetFilters);
            paramMap.put("articleFilters", articleFilters);
            paramMap.put("providerFilters", providerFilters);
            paramMap.put("lang", lang);
            paramMap.put("ongoing", ongoing);
            paramMap.put("upcoming", upcoming);
            paramMap.put("upcomingLater", upcomingLater);
            paramMap.put("start", start);
            paramMap.put("rows", rows);
            paramMap.put("sort", sort);
            paramMap.put("order", order);
            paramMap.put("lopFilter", lopFilter);
            paramMap.put("educationCodeFilter", educationCodeFilter);
            paramMap.put("excludes", excludes);
            paramMap.put("asId", asId);
            paramMap.put("searchType", searchType);
            LOGGER.warn("Search failed to exception. Parameters: {}", paramMap, e);
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public TutkintoLOSDTO getTutkintoLearningOpportunity(String id, String lang, String uiLang, String prerequisite) {
        try {
            TutkintoLOSDTO dto = null;

            dto = learningOpportunityService.getTutkintoLearningOpportunity(id, lang, uiLang, prerequisite);

            setArticles(uiLang, dto, dto.getKoulutuskoodi(), SolrUtil.SolrConstants.ED_TYPE_AMMATILLINEN);

            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException ex) {
            throw KIExceptionHandler.resolveException(ex);
        }
    }

    @Override
    public KoulutusLOSDTO getChildLearningOpportunity(String id, String lang, String uiLang) {
        return getKoulutusLearningOpportunity(id, lang, uiLang);
    }

    @Override
    public KoulutusLOSDTO getUpperSecondaryLearningOpportunity(
            String id, String lang, String uiLang) {
        return getKoulutusLearningOpportunity(id, lang, uiLang);
    }


    @Override
    public KoulutusLOSDTO getKoulutusLearningOpportunity(
            String id, String lang, String uiLang) {
        try {

            KoulutusLOSDTO dto;

            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                dto = learningOpportunityService.getKoulutusLearningOpportunity(id);
            }
            else if (Strings.isNullOrEmpty(lang)) {
                dto = learningOpportunityService.getKoulutusLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                dto = learningOpportunityService.getKoulutusLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }

            setArticles(uiLang, dto, dto.getKoulutuskoodi(), dto.getEducationType());

            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException ex) {
            throw KIExceptionHandler.resolveException(ex);
        }
    }

    @Override
    public KoulutusLOSDTO getSpecialLearningOpportunity(String id, String lang, String uiLang) {
        return getKoulutusLearningOpportunity(id, lang, uiLang);
    }

    @Override
    public SuggestedTermsResultDTO getSuggestedTerms(String term, String lang) {
        try {
            SuggestedTermsResult suggestedTerms = this.searchService.searchSuggestedTerms(term, lang);
            return modelMapper.map(suggestedTerms, SuggestedTermsResultDTO.class);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(String id,
            String lang, String uiLang) {
        try {

            HigherEducationLOSDTO dto = null;

            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                dto = learningOpportunityService.getHigherEducationLearningOpportunity(id);
                uiLang = (dto.getTeachingLanguages() != null && !dto.getTeachingLanguages().isEmpty())
                        ? dto.getTeachingLanguages().get(0).toLowerCase() : LANG_FI;
            }
            else if (Strings.isNullOrEmpty(lang)) {
                dto = learningOpportunityService.getHigherEducationLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                dto = learningOpportunityService.getHigherEducationLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }

            setArticles(uiLang, dto, dto.getKoulutuskoodi(), dto.getEducationType());

            if (dto.getQualifications() != null) {
                dto.setQualifications(dto.getQualifications().stream().distinct().collect(Collectors.<String>toList()));
            }

            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException ex) {
            throw KIExceptionHandler.resolveException(ex);
        }
    }

    @Override
    public LOSDTO previewLearningOpportunity(String oid,
            String lang, String uiLang, String loType) {
        try {
            if ("korkeakoulu".equals(loType)) {
                HigherEducationLOSDTO dto = learningOpportunityService.previewHigherEdLearningOpportunity(oid, lang, uiLang);
                setArticles(uiLang, dto, dto.getKoulutuskoodi(), dto.getEducationType());
                return dto;
            } else if ("aikuislukio".equals(loType)
                    || "aikuistenperusopetus".equals(loType)
                    || "koulutus".equals(loType)) {
                return learningOpportunityService.previewKoulutusLearningOpportunity(oid, lang, uiLang);
            } else if ("ammatillinenaikuiskoulutus".equals(loType)) {
                AdultVocationalParentLOSDTO dto = learningOpportunityService.previewAdultVocationalLearningOpportunity(oid, lang, uiLang);
                String koulutuskoodi = dto.getChildren().get(0).getKoulutuskoodi();
                String edType = dto.getChildren().get(0).getEducationType();
                setArticles(uiLang, dto, koulutuskoodi, edType);
                return dto;
            }
            throw new ResourceNotFoundException("No preview implemented for loType: " + loType);
        } catch (KIException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public PictureDTO getPicture(String id) {
        try {
            return learningOpportunityService.getPicture(id);
        } catch (ResourceNotFoundException ex) {
            throw KIExceptionHandler.resolveException(ex);
        }
    }

    @Override
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(
            String id, String lang, String uiLang) {

        try {

            AdultVocationalParentLOSDTO dto = null;

            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                dto = learningOpportunityService.getAdultVocationalLearningOpportunity(id);
                /*uiLang = (dto.getChildren() != null
                        && !dto.getChildren().isEmpty()
                        && dto.getChildren().get(0).getTeachingLanguages() != null
                            && !dto.getChildren().get(0).getTeachingLanguages().isEmpty())
                        ? dto.getChildren().get(0).getTeachingLanguages().get(0).toLowerCase() : LANG_FI;*/
            }
            else if (Strings.isNullOrEmpty(lang)) {
                dto = this.learningOpportunityService.getAdultVocationalLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                dto = learningOpportunityService.getAdultVocationalLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }

            String koulutuskoodi = dto.getChildren().get(0).getKoulutuskoodi();
            String edType = dto.getChildren().get(0).getEducationType();
            setArticles(uiLang, dto, koulutuskoodi, edType);

            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException se) {
            throw KIExceptionHandler.resolveException(se);
        }
    }

    private void setArticles(String uiLang, Articled dto, String koulutuskoodi, String edType) throws SearchException {
        List<ArticleResult> edCodeSuggestions = this.searchService.searchArticleSuggestions(String.format("%s:%s", LearningOpportunity.ARTICLE_EDUCATION_CODE, koulutuskoodi), uiLang);
        List<ArticleResult> edTypeSuggestions = this.searchService.searchArticleSuggestions(String.format("%s:%s", LearningOpportunity.EDUCATION_TYPE, edType), uiLang);
        if (edCodeSuggestions.size() < edTypeSuggestions.size()) {
            dto.setEdCodeSuggestions(ArticleResultToDTO.convert(edCodeSuggestions, 3));
            dto.setEdTypeSuggestions(ArticleResultToDTO.convert(edTypeSuggestions, 6 - dto.getEdCodeSuggestions().size()));
        } else {
            dto.setEdTypeSuggestions(ArticleResultToDTO.convert(edTypeSuggestions, 3));
            dto.setEdCodeSuggestions(ArticleResultToDTO.convert(edCodeSuggestions, 6 - dto.getEdTypeSuggestions().size()));
        }
    }

    @Override
    public KoulutusLOSDTO getAdultUpperSecondaryLearningOpportunity(String id, String lang, String uiLang) {
        return getKoulutusLearningOpportunity(id, lang, uiLang);
    }
}
