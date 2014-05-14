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

package fi.vm.sade.koulutusinformaatio.dao.entity;

import org.mongodb.morphia.annotations.*;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Entity("specialLearningOpportunitySpecifications")
public class SpecialLearningOpportunitySpecificationEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity shortTitle;
    private String educationDegree;
    @Embedded
    private I18nTextEntity degreeTitle;
    @Embedded
    private I18nTextEntity qualification;
    @Embedded
    private I18nTextEntity goals;
    @Embedded
    private List<ChildLearningOpportunityInstanceEntity> lois;
    @Indexed
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Embedded
    private I18nTextEntity structure;
    @Embedded
    private I18nTextEntity accessToFurtherStudies;
    private String creditValue;
    @Embedded
    private I18nTextEntity creditUnit;
    @Embedded
    private I18nTextEntity educationDomain;
    @Embedded
    private ParentLOSRefEntity parent;
    
    private String educationTypeUri;
    
    @Embedded
    private List<CodeEntity> topics;
    @Embedded
    private List<CodeEntity> themes;
    
    private String type;




    public SpecialLearningOpportunitySpecificationEntity() {
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

    public I18nTextEntity getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nTextEntity shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public I18nTextEntity getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nTextEntity degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nTextEntity getQualification() {
        return qualification;
    }

    public void setQualification(I18nTextEntity qualification) {
        this.qualification = qualification;
    }

    public I18nTextEntity getGoals() {
        return goals;
    }

    public void setGoals(I18nTextEntity goals) {
        this.goals = goals;
    }

    public List<ChildLearningOpportunityInstanceEntity> getLois() {
        return lois;
    }

    public void setLois(List<ChildLearningOpportunityInstanceEntity> lois) {
        this.lois = lois;
    }

    public LearningOpportunityProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderEntity provider) {
        this.provider = provider;
    }

    public I18nTextEntity getStructure() {
        return structure;
    }

    public void setStructure(I18nTextEntity structure) {
        this.structure = structure;
    }

    public I18nTextEntity getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
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

    public I18nTextEntity getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nTextEntity educationDomain) {
        this.educationDomain = educationDomain;
    }

    public ParentLOSRefEntity getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefEntity parent) {
        this.parent = parent;
    }

    public String getEducationTypeUri() {
        return educationTypeUri;
    }

    public void setEducationTypeUri(String educationTypeUri) {
        this.educationTypeUri = educationTypeUri;
    }

    public List<CodeEntity> getTopics() {
        return topics;
    }

    public void setTopics(List<CodeEntity> topics) {
        this.topics = topics;
    }

    public List<CodeEntity> getThemes() {
        return themes;
    }

    public void setThemes(List<CodeEntity> themes) {
        this.themes = themes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
