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

package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

import fi.vm.sade.koulutusinformaatio.domain.Classification;
import fi.vm.sade.koulutusinformaatio.domain.Credits;
import fi.vm.sade.koulutusinformaatio.domain.Description;

/**
 * @author Mikko Majapuro
 */
@Entity("learningOpportunities")
public class ParentLearningOpportunityEntity {

    @Id
    private String id;
    private String name;
    private String educationDegree;

    private Description description;
    private Classification classification;
    private Credits credits;
    
    @Embedded
    private List<ChildLearningOpportunityEntity> children;
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    @Reference
    private LearningOpportunityProviderEntity provider;

    public ParentLearningOpportunityEntity() {
    }

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

    public List<ChildLearningOpportunityEntity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunityEntity> children) {
        this.children = children;
    }

    public List<ApplicationOptionEntity> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionEntity> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public LearningOpportunityProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderEntity provider) {
        this.provider = provider;
    }
    
    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }
}
