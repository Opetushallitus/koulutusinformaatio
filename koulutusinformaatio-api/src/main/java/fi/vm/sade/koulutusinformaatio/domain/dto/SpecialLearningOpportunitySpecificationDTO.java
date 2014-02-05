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

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SpecialLearningOpportunitySpecificationDTO {

    private String id;
    private String name;
    private String educationDegree;
    private String degreeTitle;
    private String qualification;
    private String goals;
    private List<ChildLearningOpportunityInstanceDTO> lois;
    private LearningOpportunityProviderDTO provider;
    private String structure;
    private String accessToFurtherStudies;
    private String creditValue;
    private String creditUnit;
    private String translationLanguage;
    private String educationDomain;
    private ParentLOSRefDTO parent;

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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(String degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public List<ChildLearningOpportunityInstanceDTO> getLois() {
        return lois;
    }

    public void setLois(List<ChildLearningOpportunityInstanceDTO> lois) {
        this.lois = lois;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
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

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }
}
