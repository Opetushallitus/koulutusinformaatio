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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.converter.ArticleResultToDTO;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultUpperSecondaryLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.Articled;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.dto.SpecialLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SuggestedTermsResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

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
                                                            String educationDomainFilter,
                                                            List<String> excludes, 
                                                            SearchType searchType) {
        try {
            sort = (sort != null && !sort.isEmpty()) ? sort : null;
            LOSearchResultList learningOpportunities = searchService.searchLearningOpportunities(text, prerequisite,
                    cities, facetFilters, articleFilters, providerFilters, lang, ongoing, upcoming, upcomingLater, start, rows, sort, order, 
                    lopFilter, educationCodeFilter, educationDomainFilter, excludes, searchType);
            return modelMapper.map(learningOpportunities, LOSearchResultListDTO.class);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang, String uiLang) {
        try {
            ParentLearningOpportunitySpecificationDTO dto = null;
            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                dto = learningOpportunityService.getParentLearningOpportunity(parentId);
                uiLang = dto.getTranslationLanguage();
            } else if (Strings.isNullOrEmpty(lang)) {
                dto = learningOpportunityService.getParentLearningOpportunity(parentId, uiLang.toLowerCase());
            } else {
                dto = learningOpportunityService.getParentLearningOpportunity(parentId, lang.toLowerCase(), uiLang.toLowerCase());
            }
            String educationType = getEducationTypeForParent(dto);
            if (!StringUtils.isBlank(educationType)) {
                setArticles(uiLang, dto, dto.getName(), educationType);
            }
            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    private String getEducationTypeForParent(ParentLearningOpportunitySpecificationDTO dto) {
        String type = null;
        try {
            ApplicationOptionDTO option = dto.getLois().get(0).getApplicationSystems().get(0).getApplicationOptions().get(0);
            if (!option.isKotitalous() && !option.isKaksoistutkinto() && option.isVocational()) {
                type = SolrConstants.ED_TYPE_AMMATILLINEN;
            } else if (option.isKaksoistutkinto()) {
                type = SolrConstants.ED_TYPE_KAKSOIS;
            } else if (option.isKotitalous()) {
                type = SolrConstants.ED_TYPE_KOTITALOUS;
            }
            type = SolrConstants.ED_TYPE_MUU;
        } catch (IndexOutOfBoundsException aoe) {
            LOGGER.warn(dto.getClass().getSimpleName() + " did not have any " + ApplicationOptionDTO.class.getSimpleName() + " nested within. Contents were: " + dto.toString());
        }
        return type;
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
            } else if ("aikuislukio".equals(loType)) {
                return learningOpportunityService.previewAdultUpperSecondaryLearningOpportunity(oid, lang, uiLang);
            } else if ("ammatillinenaikuiskoulutus".equals(loType)) {
                AdultVocationalParentLOSDTO dto = learningOpportunityService.previewAdultVocationalLearningOpportunity(oid, lang, uiLang);
                String koulutuskoodi = dto.getChildren().get(0).getKoulutuskoodi();
                String edType = dto.getChildren().get(0).getEducationType();
                setArticles(uiLang, dto, koulutuskoodi, edType);
                return dto; 
            }
            throw new ResourceNotFoundException("No preview implemented for loType: " + loType);
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException ex) {
            throw KIExceptionHandler.resolveException(ex);
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
    public AdultUpperSecondaryLOSDTO getAdultUpperSecondaryLearningOpportunity(
            String id, String lang, String uiLang) {

        try {

            AdultUpperSecondaryLOSDTO dto = null;

            if (Strings.isNullOrEmpty(lang) && Strings.isNullOrEmpty(uiLang)) {
                dto = learningOpportunityService.getAdultUpperSecondaryLearningOpportunity(id);
                uiLang = (dto.getTeachingLanguages() != null && !dto.getTeachingLanguages().isEmpty()) 
                        ? dto.getTeachingLanguages().get(0).toLowerCase() : LANG_FI;
            }
            else if (Strings.isNullOrEmpty(lang)) {
                dto = this.learningOpportunityService.getAdultUpperSecondaryLearningOpportunity(id, uiLang.toLowerCase());
            }
            else {
                dto = learningOpportunityService.getAdultUpperSecondaryLearningOpportunity(id, lang.toLowerCase(), uiLang.toLowerCase());
            }
            
            String koulutuskoodi = dto.getKoulutuskoodi();
            String edType = dto.getEducationType();
            
            setArticles(uiLang, dto, koulutuskoodi, edType);

            return dto;
        } catch (ResourceNotFoundException e) {
            throw KIExceptionHandler.resolveException(e);
        } catch (SearchException se) {
            throw KIExceptionHandler.resolveException(se);
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
}
