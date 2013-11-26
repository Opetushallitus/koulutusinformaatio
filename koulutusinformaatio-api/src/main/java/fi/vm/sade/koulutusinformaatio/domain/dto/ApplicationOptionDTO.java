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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationOptionDTO {

    private String id;
    private String name;
    private String aoIdentifier;
    private Integer startingQuota;
    private Integer lowestAcceptedScore;
    private Double lowestAcceptedAverage;
    private Date attachmentDeliveryDeadline;
    private AddressDTO attachmentDeliveryAddress;
    private Integer lastYearApplicantCount;
    private boolean sora;
    private String educationDegree;
    private List<String> teachingLanguages;
    private String selectionCriteria;
    private CodeDTO prerequisite;
    private List<ExamDTO> exams;
    private List<ChildLOIRefDTO> childRefs = new ArrayList<ChildLOIRefDTO>();
    private LearningOpportunityProviderDTO provider;
    private boolean specificApplicationDates;
    private Date applicationStartDate;
    private Date applicationEndDate;
    // if has specific application dates are present, indicates if can be applied at a given moment
    private boolean canBeApplied;
    private Date nextApplicationPeriodStarts;
    private List<String> requiredBaseEducations;
    private List<ApplicationOptionAttachmentDTO> attachments;
    private List<EmphasizedSubjectDTO> emphasizedSubjects;
    private String additionalInfo;
    private AdditionalProofDTO additionalProof;
    private ScoreLimitDTO overallScoreLimit;
    private boolean kaksoistutkinto;

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

    public String getAoIdentifier() {
        return aoIdentifier;
    }

    public void setAoIdentifier(String aoIdentifier) {
        this.aoIdentifier = aoIdentifier;
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

    public AddressDTO getAttachmentDeliveryAddress() {
        return attachmentDeliveryAddress;
    }

    public void setAttachmentDeliveryAddress(AddressDTO attachmentDeliveryAddress) {
        this.attachmentDeliveryAddress = attachmentDeliveryAddress;
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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public String getSelectionCriteria() {
        return selectionCriteria;
    }

    public void setSelectionCriteria(String selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<ExamDTO> getExams() {
        return exams;
    }

    public void setExams(List<ExamDTO> exams) {
        this.exams = exams;
    }

    public List<ChildLOIRefDTO> getChildRefs() {
        return childRefs;
    }

    public void setChildRefs(List<ChildLOIRefDTO> childRefs) {
        this.childRefs = childRefs;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public boolean isSpecificApplicationDates() {
        return specificApplicationDates;
    }

    public void setSpecificApplicationDates(boolean specificApplicationDates) {
        this.specificApplicationDates = specificApplicationDates;
    }

    public Date getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(Date applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public Date getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(Date applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }

    public boolean isCanBeApplied() {
        return canBeApplied;
    }

    public void setCanBeApplied(boolean canBeApplied) {
        this.canBeApplied = canBeApplied;
    }

    public Date getNextApplicationPeriodStarts() {
        return nextApplicationPeriodStarts;
    }

    public void setNextApplicationPeriodStarts(Date nextApplicationPeriodStarts) {
        this.nextApplicationPeriodStarts = nextApplicationPeriodStarts;
    }

    public List<String> getRequiredBaseEducations() {
        return requiredBaseEducations;
    }

    public void setRequiredBaseEducations(List<String> requiredBaseEducations) {
        this.requiredBaseEducations = requiredBaseEducations;
    }

    public List<ApplicationOptionAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationOptionAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<EmphasizedSubjectDTO> getEmphasizedSubjects() {
        return emphasizedSubjects;
    }

    public void setEmphasizedSubjects(List<EmphasizedSubjectDTO> emphasizedSubjects) {
        this.emphasizedSubjects = emphasizedSubjects;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public AdditionalProofDTO getAdditionalProof() {
        return additionalProof;
    }

    public void setAdditionalProof(AdditionalProofDTO additionalProof) {
        this.additionalProof = additionalProof;
    }

    public ScoreLimitDTO getOverallScoreLimit() {
        return overallScoreLimit;
    }

    public void setOverallScoreLimit(ScoreLimitDTO overallScoreLimit) {
        this.overallScoreLimit = overallScoreLimit;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }
}
