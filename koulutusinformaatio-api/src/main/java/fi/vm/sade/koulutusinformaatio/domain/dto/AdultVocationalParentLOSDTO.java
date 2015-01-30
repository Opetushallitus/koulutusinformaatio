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
 * 
 * @author Markus
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AdultVocationalParentLOSDTO extends LOSDTO implements Articled {
    
    private String id;
    private String name;
    private String shortTitle;
    private String goals;
    private String type;
    private double charge;
    private boolean chargeable;
    private boolean osaamisala;
    
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;
    
    private String accessToFurtherStudies;
    private String choosingCompetence;
    private String degreeCompletion;
    
    private String educationDomain;
    private String educationKind;
    
    private LearningOpportunityProviderDTO provider;
    private List<LearningOpportunityProviderDTO> additionalProviders = new ArrayList<LearningOpportunityProviderDTO>();
    //private List<ApplicationOptionDTO> applicationOptions;
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
    
    private List<AdultVocationalChildLOSDTO> children;
    private String translationLanguage;
    private List<CodeDTO> availableTranslationLanguages;
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

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(String accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getChoosingCompetence() {
        return choosingCompetence;
    }

    public void setChoosingCompetence(String choosingCompetence) {
        this.choosingCompetence = choosingCompetence;
    }

    public String getDegreeCompletion() {
        return degreeCompletion;
    }

    public void setDegreeCompletion(String degreeCompletion) {
        this.degreeCompletion = degreeCompletion;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public List<AdultVocationalChildLOSDTO> getChildren() {
        return children;
    }

    public void setChildren(List<AdultVocationalChildLOSDTO> children) {
        this.children = children;
    }

    public void setTranslationLanguage(String descriptionLang) {
        this.translationLanguage = descriptionLang;   
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setAvailableTranslationLanguages(List<CodeDTO> translLanguages) {
        this.availableTranslationLanguages = translLanguages;  
    }

    public List<CodeDTO> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public List<ApplicationSystemDTO> getApplicationSystems() {
        return applicationSystems;
    }

    public void setApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {
        this.applicationSystems = applicationSystems;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public String getEducationKind() {
        return educationKind;
    }

    public void setEducationKind(String educationKind) {
        this.educationKind = educationKind;
    }

    public void setEdCodeSuggestions(List<ArticleResultDTO> suggestions) {
        this.edCodeSuggestions = suggestions;
        
    }

    public List<ArticleResultDTO> getEdCodeSuggestions() {
        return edCodeSuggestions;
    }

    public void setEdTypeSuggestions(List<ArticleResultDTO> suggestions) {
        this.edTypeSuggestions = suggestions;
        
    }

    public List<ArticleResultDTO> getEdTypeSuggestions() {
        return edTypeSuggestions;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public boolean isChargeable() {
        return chargeable;
    }

    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }

    public boolean isOsaamisala() {
        return osaamisala;
    }

    public void setOsaamisala(boolean osaamisala) {
        this.osaamisala = osaamisala;
    }

    public List<LearningOpportunityProviderDTO> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<LearningOpportunityProviderDTO> additionalProviders) {
        this.additionalProviders = additionalProviders;
    } 
   
}
