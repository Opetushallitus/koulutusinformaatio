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

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

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
    private String aoIdentifier;
    @Embedded
    private ApplicationSystemEntity applicationSystem;
    private String educationDegree;
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Embedded
    private List<ChildLOIRefEntity> childLOIRefs = new ArrayList<ChildLOIRefEntity>();
    @Embedded
    private List<HigherEducationLOSRefEntity> higherEdLOSRefs = new ArrayList<HigherEducationLOSRefEntity>();
    private Integer startingQuota;
    private Integer lowestAcceptedScore;
    private Double lowestAcceptedAverage;
    private Date attachmentDeliveryDeadline;
    @Embedded
    private AddressEntity attachmentDeliveryAddress;
    private Integer lastYearApplicantCount;
    private boolean sora;
    private List<String> teachingLanguages;
    @Embedded
    private ParentLOSRefEntity parent;
    @Embedded
    private I18nTextEntity selectionCriteria;
    @Embedded
    private I18nTextEntity soraDescription;


    @Embedded
    private CodeEntity prerequisite;
    private List<String> requiredBaseEducations;
    @Embedded
    private List<ExamEntity> exams;
    private boolean specificApplicationDates;
    private Date applicationStartDate;
    private Date applicationEndDate;
    @Embedded
    private List<ApplicationOptionAttachmentEntity> attachments;
    @Embedded
    private List<EmphasizedSubjectEntity> emphasizedSubjects;
    @Embedded
    private I18nTextEntity additionalInfo;
    @Embedded
    private AdditionalProofEntity additionalProof;
    @Embedded
    private ScoreLimitEntity overallScoreLimit;
    private boolean kaksoistutkinto;
    private boolean athleteEducation;
    private boolean vocational;
    private String educationCodeUri;
    @Embedded
    private I18nTextEntity eligibilityDescription;
    private String type;
    private String educationTypeUri;


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

    public String getAoIdentifier() {
        return aoIdentifier;
    }

    public void setAoIdentifier(String aoIdentifier) {
        this.aoIdentifier = aoIdentifier;
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

    public List<ChildLOIRefEntity> getChildLOIRefs() {
        return childLOIRefs;
    }

    public void setChildLOIRefs(List<ChildLOIRefEntity> childLOIRefs) {
        this.childLOIRefs = childLOIRefs;
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

    public AddressEntity getAttachmentDeliveryAddress() {
        return attachmentDeliveryAddress;
    }

    public void setAttachmentDeliveryAddress(AddressEntity attachmentDeliveryAddress) {
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

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public ParentLOSRefEntity getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefEntity parent) {
        this.parent = parent;
    }

    public I18nTextEntity getSelectionCriteria() {
        return selectionCriteria;
    }

    public void setSelectionCriteria(I18nTextEntity selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }

    public CodeEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<String> getRequiredBaseEducations() {
        return requiredBaseEducations;
    }

    public void setRequiredBaseEducations(List<String> requiredBaseEducations) {
        this.requiredBaseEducations = requiredBaseEducations;
    }

    public List<ExamEntity> getExams() {
        return exams;
    }

    public void setExams(List<ExamEntity> exams) {
        this.exams = exams;
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

    public List<ApplicationOptionAttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationOptionAttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    public List<EmphasizedSubjectEntity> getEmphasizedSubjects() {
        return emphasizedSubjects;
    }

    public void setEmphasizedSubjects(List<EmphasizedSubjectEntity> emphasizedSubjects) {
        this.emphasizedSubjects = emphasizedSubjects;
    }

    public I18nTextEntity getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(I18nTextEntity additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public AdditionalProofEntity getAdditionalProof() {
        return additionalProof;
    }

    public void setAdditionalProof(AdditionalProofEntity additionalProof) {
        this.additionalProof = additionalProof;
    }

    public ScoreLimitEntity getOverallScoreLimit() {
        return overallScoreLimit;
    }

    public void setOverallScoreLimit(ScoreLimitEntity overallScoreLimit) {
        this.overallScoreLimit = overallScoreLimit;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }

    public boolean isAthleteEducation() {
        return athleteEducation;
    }

    public void setAthleteEducation(boolean athleteEducation) {
        this.athleteEducation = athleteEducation;
    }

    public boolean isVocational() {
        return vocational;
    }

    public void setVocational(boolean vocational) {
        this.vocational = vocational;
    }

    public String getEducationCodeUri() {
        return educationCodeUri;
    }

    public void setEducationCodeUri(String educationCodeUri) {
        this.educationCodeUri = educationCodeUri;
    }

    public I18nTextEntity getSoraDescription() {
        return soraDescription;
    }
    public void setSoraDescription(I18nTextEntity soraDescription) {
        this.soraDescription = soraDescription;
    }
    public I18nTextEntity getEligibilityDescription() {
        return eligibilityDescription;
    }
    public void setEligibilityDescription(I18nTextEntity prerequisiteDescription) {
        this.eligibilityDescription = prerequisiteDescription;
    }

    public List<HigherEducationLOSRefEntity> getHigherEdLOSRefs() {
        return higherEdLOSRefs;
    }

    public void setHigherEdLOSRefs(List<HigherEducationLOSRefEntity> higherEdLOSRefs) {
        this.higherEdLOSRefs = higherEdLOSRefs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEducationTypeUri() {
        return educationTypeUri;
    }

    public void setEducationTypeUri(String educationTypeUri) {
        this.educationTypeUri = educationTypeUri;
    }

}
