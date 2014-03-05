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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class LOSearchResultListDTO {

    private List<LearningOpportunitySearchResultDTO> results = new ArrayList<LearningOpportunitySearchResultDTO>();
    private long totalCount;
    private FacetDTO teachingLangFacet;
    private FacetDTO filterFacet;
    private FacetDTO appStatusFacet; 
    private FacetDTO edTypeFacet;
    private FacetDTO prerequisiteFacet;
    private FacetDTO topicFacet;
    private FacetValueDTO lopRecommendationFilter;
    

    private FacetValueDTO educationCodeRecommendationFilter;


    public List<LearningOpportunitySearchResultDTO> getResults() {
        return results;
    }

    public void setResults(List<LearningOpportunitySearchResultDTO> results) {
        this.results = results;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public FacetDTO getTeachingLangFacet() {
        return teachingLangFacet;
    }

    public void setTeachingLangFacet(FacetDTO teachingLangFacet) {
        this.teachingLangFacet = teachingLangFacet;
    }

    public FacetDTO getFilterFacet() {
        return filterFacet;
    }

    public void setFilterFacet(FacetDTO filterFacet) {
        this.filterFacet = filterFacet;
    }

    public FacetDTO getAppStatusFacet() {
        return appStatusFacet;
    }

    public void setAppStatusFacet(FacetDTO appStatusFacet) {
        this.appStatusFacet = appStatusFacet;
    }
    

    public FacetDTO getEdTypeFacet() {
        return edTypeFacet;
    }

    public void setEdTypeFacet(FacetDTO edTypeFacet) {
        this.edTypeFacet = edTypeFacet;
    }

    public FacetDTO getPrerequisiteFacet() {
        return prerequisiteFacet;
    }

    public void setPrerequisiteFacet(FacetDTO prerequisiteFacet) {
        this.prerequisiteFacet = prerequisiteFacet;
    }

    public FacetDTO getTopicFacet() {
        return topicFacet;
    }

    public void setTopicFacet(FacetDTO topicFacet) {
        this.topicFacet = topicFacet;
    }
    
    public FacetValueDTO getLopRecommendationFilter() {
        return lopRecommendationFilter;
    }

    public void setLopRecommendationFilter(FacetValueDTO lopRecommendationFilter) {
        this.lopRecommendationFilter = lopRecommendationFilter;
    }

    public FacetValueDTO getEducationCodeRecommendationFilter() {
        return educationCodeRecommendationFilter;
    }

    public void setEducationCodeRecommendationFilter(
            FacetValueDTO educationCodeRecommendationFilter) {
        this.educationCodeRecommendationFilter = educationCodeRecommendationFilter;
    }
}
