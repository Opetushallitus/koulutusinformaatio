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

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

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
    private I18nTextEntity shortTitle;
    @Embedded
    private String educationDegree;
    @Indexed
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Reference
    private List<LearningOpportunityProviderEntity> additionalProviders = new ArrayList<LearningOpportunityProviderEntity>();
    @Embedded
    private I18nTextEntity structure;
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
    
    @Embedded
    private List<CodeEntity> teachingLanguages;
    
    @Embedded
    private List<CodeEntity> topics;
    @Embedded
    private List<CodeEntity> themes;
    
    private String type;
    
    private boolean kotitalousopetus;

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
    
    public List<CodeEntity> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
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

    public boolean isKotitalousopetus() {
        return kotitalousopetus;
    }

    public void setKotitalousopetus(boolean kotitalousopetus) {
        this.kotitalousopetus = kotitalousopetus;
    }

    public List<LearningOpportunityProviderEntity> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(
            List<LearningOpportunityProviderEntity> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }
}
