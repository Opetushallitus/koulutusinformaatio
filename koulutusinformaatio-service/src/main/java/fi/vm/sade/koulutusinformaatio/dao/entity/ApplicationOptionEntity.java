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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Entity("applicationOptions")
public class ApplicationOptionEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private ApplicationSystemEntity applicationSystem;
    private String educationDegree;
    @Reference
    private LearningOpportunityProviderEntity provider;
    private List<I18nTextEntity> childLONames = new ArrayList<I18nTextEntity>();
    private Integer startingQuota;
    private Integer lowestAcceptedScore;
    private Double lowestAcceptedAverage;
    private Date attachmentDeliveryDeadline;
    private Integer lastYearApplicantCount;
    private boolean sora;
    private List<String> teachingLanguages;

    public ApplicationOptionEntity() {

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

    public ApplicationSystemEntity getApplicationSystem() {
        return applicationSystem;
    }

    public void setApplicationSystem(ApplicationSystemEntity applicationSystem) {
        this.applicationSystem = applicationSystem;
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

    public List<I18nTextEntity> getChildLONames() {
        return childLONames;
    }

    public void setChildLONames(List<I18nTextEntity> childLONames) {
        this.childLONames = childLONames;
    }

    public Integer getStartingQuota() {
        return startingQuota;
    }

    public void setStartingQuota(Integer startingQuota) {
        this.startingQuota = startingQuota;
    }

    public Integer getLowestAcceptedScore() {
        return lowestAcceptedScore;
    }

    public void setLowestAcceptedScore(Integer lowestAcceptedScore) {
        this.lowestAcceptedScore = lowestAcceptedScore;
    }

    public Double getLowestAcceptedAverage() {
        return lowestAcceptedAverage;
    }

    public void setLowestAcceptedAverage(Double lowestAcceptedAverage) {
        this.lowestAcceptedAverage = lowestAcceptedAverage;
    }

    public Date getAttachmentDeliveryDeadline() {
        return attachmentDeliveryDeadline;
    }

    public void setAttachmentDeliveryDeadline(Date attachmentDeliveryDeadline) {
        this.attachmentDeliveryDeadline = attachmentDeliveryDeadline;
    }

    public Integer getLastYearApplicantCount() {
        return lastYearApplicantCount;
    }

    public void setLastYearApplicantCount(Integer lastYearApplicantCount) {
        this.lastYearApplicantCount = lastYearApplicantCount;
    }

    public boolean isSora() {
        return sora;
    }

    public void setSora(boolean sora) {
        this.sora = sora;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }
}
