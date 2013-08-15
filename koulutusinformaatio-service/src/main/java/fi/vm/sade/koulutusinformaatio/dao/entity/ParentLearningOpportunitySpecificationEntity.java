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

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Entity("parentLearningOpportunitySpecifications")
public class ParentLearningOpportunitySpecificationEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private String educationDegree;
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Embedded
    private I18nTextEntity structureDiagram;
    @Embedded
    private I18nTextEntity degreeProgramSelection;
    @Embedded
    private I18nTextEntity accessToFurtherStudies;
    @Embedded
    private I18nTextEntity goals;
    @Embedded
    private I18nTextEntity educationDomain;
    @Embedded
    private I18nTextEntity stydyDomain;
    private String creditValue;
    @Embedded
    private I18nTextEntity creditUnit;
    @Embedded
    private List<ParentLearningOpportunityInstanceEntity> lois;
    @Reference
    private List<ChildLearningOpportunitySpecificationEntity> children;


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

    public LearningOpportunityProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderEntity provider) {
        this.provider = provider;
    }

    public I18nTextEntity getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(I18nTextEntity structureDiagram) {
        this.structureDiagram = structureDiagram;
    }

    public I18nTextEntity getDegreeProgramSelection() {
        return degreeProgramSelection;
    }

    public void setDegreeProgramSelection(I18nTextEntity degreeProgramSelection) {
        this.degreeProgramSelection = degreeProgramSelection;
    }

    public I18nTextEntity getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nTextEntity getGoals() {
        return goals;
    }

    public void setGoals(I18nTextEntity goals) {
        this.goals = goals;
    }

    public I18nTextEntity getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nTextEntity educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nTextEntity getStydyDomain() {
        return stydyDomain;
    }

    public void setStydyDomain(I18nTextEntity stydyDomain) {
        this.stydyDomain = stydyDomain;
    }

    public List<ParentLearningOpportunityInstanceEntity> getLois() {
        return lois;
    }

    public void setLois(List<ParentLearningOpportunityInstanceEntity> lois) {
        this.lois = lois;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public I18nTextEntity getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nTextEntity creditUnit) {
        this.creditUnit = creditUnit;
    }

    public List<ChildLearningOpportunitySpecificationEntity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunitySpecificationEntity> children) {
        this.children = children;
    }
}
