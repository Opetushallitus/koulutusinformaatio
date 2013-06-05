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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ParentLearningOpportunitySpecificationDTO {

    private String id;
    private String name;
    private List<ChildLORefDTO> children = new ArrayList<ChildLORefDTO>();
    private Set<ApplicationOptionDTO> applicationOptions = new HashSet<ApplicationOptionDTO>();
    private LearningOpportunityProviderDTO provider;
    private String educationDegree;
    private String structureDiagram;
    private String accessToFurtherStudies;
    private String goals;
    private String educationDomain;
    private String stydyDomain;
    private List<ParentLearningOpportunityInstanceDTO> lois = new ArrayList<ParentLearningOpportunityInstanceDTO>();

    private String translationLanguage;
    private Set<String> availableTranslationLanguages = new HashSet<String>();

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

    public List<ChildLORefDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLORefDTO> children) {
        this.children = children;
    }

    public Set<ApplicationOptionDTO> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOptionDTO> applicationOptions) {
        this.applicationOptions = applicationOptions;
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

    public Set<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(Set<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public String getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(String structureDiagram) {
        this.structureDiagram = structureDiagram;
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
}
