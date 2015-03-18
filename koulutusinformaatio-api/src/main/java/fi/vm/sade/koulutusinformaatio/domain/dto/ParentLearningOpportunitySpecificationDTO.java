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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ParentLearningOpportunitySpecificationDTO implements Articled {

    private String id;
    private String name;
    private LearningOpportunityProviderDTO provider;
    private List<LearningOpportunityProviderDTO> additionalProviders = new ArrayList<LearningOpportunityProviderDTO>();
    private String educationDegree;
    private String structure;
    private String accessToFurtherStudies;
    private String goals;
    private String educationDomain;
    private String stydyDomain;
    private List<ParentLearningOpportunityInstanceDTO> lois = new ArrayList<ParentLearningOpportunityInstanceDTO>();
    private String creditValue;
    private String creditUnit;
    private boolean containsPseudoChildLOS = false;

    private String translationLanguage;
    
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;
    
    private List<ArticleResultDTO> edCodeSuggestions;
    private List<ArticleResultDTO> edTypeSuggestions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(String accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public String getStydyDomain() {
        return stydyDomain;
    }

    public void setStydyDomain(String stydyDomain) {
        this.stydyDomain = stydyDomain;
    }

    public List<ParentLearningOpportunityInstanceDTO> getLois() {
        return lois;
    }

    public void setLois(List<ParentLearningOpportunityInstanceDTO> lois) {
        this.lois = lois;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public String getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;
    }

    public List<CodeDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<CodeDTO> topics) {
        this.topics = topics;
    }

    public List<CodeDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<CodeDTO> themes) {
        this.themes = themes;
    }

    public List<LearningOpportunityProviderDTO> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<LearningOpportunityProviderDTO> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }
    
    public void setContainsPseudoChildLOS(boolean containsPseudChildLOS) {
        this.containsPseudoChildLOS = containsPseudChildLOS;
    }
    
    public boolean isContainsPseudoChildLOS() {
        return containsPseudoChildLOS;
    }
    
    public void setEdCodeSuggestions(List<ArticleResultDTO> edCodeSuggestions) {
        this.edCodeSuggestions = edCodeSuggestions;
    }
    
    public List<ArticleResultDTO> getEdCodeSuggestions() {
        return edCodeSuggestions;
    }
    
    public void setEdTypeSuggestions(List<ArticleResultDTO> edTypeSuggestions) {
        this.edTypeSuggestions = edTypeSuggestions;
    }
    
    public List<ArticleResultDTO> getEdTypeSuggestions() {
        return edTypeSuggestions;
    }

    @Override
    public String toString() {
        return "ParentLearningOpportunitySpecificationDTO [id=" + id + ", name=" + name + ", provider=" + provider + ", additionalProviders="
                + additionalProviders + ", educationDegree=" + educationDegree + ", structure=" + structure + ", accessToFurtherStudies="
                + accessToFurtherStudies + ", goals=" + goals + ", educationDomain=" + educationDomain + ", stydyDomain=" + stydyDomain + ", lois=" + lois
                + ", creditValue=" + creditValue + ", creditUnit=" + creditUnit + ", containsPseudoChildLOS=" + containsPseudoChildLOS
                + ", translationLanguage=" + translationLanguage + ", topics=" + topics + ", themes=" + themes + ", edCodeSuggestions=" + edCodeSuggestions
                + ", edTypeSuggestions=" + edTypeSuggestions + "]";
    }
    
    
}
