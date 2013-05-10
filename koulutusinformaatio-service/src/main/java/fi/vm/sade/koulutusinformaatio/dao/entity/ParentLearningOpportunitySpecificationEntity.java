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

/**
 * @author Mikko Majapuro
 */
@Entity("parentLearningOpportunitySpecifications")
public class ParentLearningOpportunitySpecificationEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    private String educationDegree;
    @Embedded
    private DescriptionEntity description;
    @Embedded
    private ClassificationEntity classification;
    @Embedded
    private CreditsEntity credits;
    @Reference(lazy = true)
    private List<ChildLearningOpportunitySpecificationEntity> children;
    @Embedded
    private List<ChildLORefEntity> childRefs;
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    @Reference
    private LearningOpportunityProviderEntity provider;

    public ParentLearningOpportunitySpecificationEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public List<ChildLearningOpportunitySpecificationEntity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunitySpecificationEntity> children) {
        this.children = children;
    }

    public List<ChildLORefEntity> getChildRefs() {
        return childRefs;
    }

    public void setChildRefs(List<ChildLORefEntity> childRefs) {
        this.childRefs = childRefs;
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

    public CreditsEntity getCredits() {
        return credits;
    }

    public void setCredits(CreditsEntity credits) {
        this.credits = credits;
    }

    public DescriptionEntity getDescription() {
        return description;
    }

    public void setDescription(DescriptionEntity description) {
        this.description = description;
    }

    public ClassificationEntity getClassification() {
        return classification;
    }

    public void setClassification(ClassificationEntity classification) {
        this.classification = classification;
    }
}
