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
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationOptionDTO {

    private String id;
    private String name;
    private String aoIdentifier;
    private Integer startingQuota;
    private Integer firstTimerStartingQuota;
    private String startingQuotaDescription;
    private Integer lowestAcceptedScore;
    private Double lowestAcceptedAverage;
    private Date attachmentDeliveryDeadline;
    private AddressDTO attachmentDeliveryAddress;
    private Integer lastYearApplicantCount;
    private boolean sora;
    private String educationDegree;
    private List<String> teachingLanguages;
    private List<String> teachingLanguageNames;
    private String selectionCriteria;
    private String soraDescription;
    private CodeDTO prerequisite;
    private List<ExamDTO> exams;
    private List<ChildLOIRefDTO> childRefs = new ArrayList<ChildLOIRefDTO>();
    private List<HigherEducationLOSRefDTO> higherEdLOSRefs = new ArrayList<HigherEducationLOSRefDTO>();
    private LearningOpportunityProviderDTO provider;
    private ApplicationOfficeDTO applicationOffice;
    private boolean specificApplicationDates;
    private Date applicationStartDate;
    private Date applicationEndDate;
    private String applicationPeriodName;
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
    private boolean athleteEducation;
    private boolean vocational;
    private String educationCodeUri;
    private String status;
    private String eligibilityDescription;
    private String type;
    private String educationTypeUri;
    private boolean isKotitalous;
    private String hakuaikaId;
    private List<OrganizationGroupDTO> organizationGroups;
    private boolean paid;
    private String hakuMenettelyKuvaukset;
    private String peruutusEhdotKuvaukset;
    private List<String> pohjakoulutusLiitteet;
    private boolean isJosYoEiMuitaLiitepyyntoja;
    private boolean kysytaanHarkinnanvaraiset;
    private String applicationFormLink;


    public boolean isJosYoEiMuitaLiitepyyntoja() {
        return isJosYoEiMuitaLiitepyyntoja;
    }

    public void setJosYoEiMuitaLiitepyyntoja(boolean isJosYoEiMuitaLiitepyyntoja) {
        this.isJosYoEiMuitaLiitepyyntoja = isJosYoEiMuitaLiitepyyntoja;
    }

    public List<String> getPohjakoulutusLiitteet() {
        return pohjakoulutusLiitteet;
    }

    public void setPohjakoulutusLiitteet(List<String> pohjakoulutusLiitteet) {
        this.pohjakoulutusLiitteet = pohjakoulutusLiitteet;
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

    public Integer getFirstTimerStartingQuota() {
        return firstTimerStartingQuota;
    }

    public void setFirstTimerStartingQuota(Integer firstTimerStartingQuota) {
        this.firstTimerStartingQuota = firstTimerStartingQuota;
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

    public String getSoraDescription() {
        return soraDescription;
    }

    public void setSoraDescription(String soraDescription) {
        this.soraDescription = soraDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEligibilityDescription() {
        return eligibilityDescription;
    }

    public void setEligibilityDescription(String prerequisiteDescription) {
        this.eligibilityDescription = prerequisiteDescription;
    }

    public List<HigherEducationLOSRefDTO> getHigherEdLOSRefs() {
        return higherEdLOSRefs;
    }

    public void setHigherEdLOSRefs(List<HigherEducationLOSRefDTO> higherEdLOSRefs) {
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

    public boolean isKotitalous() {
        return isKotitalous;
    }

    public void setKotitalous(boolean isKotitalous) {
        this.isKotitalous = isKotitalous;
    }

    public String getHakuaikaId() {
        return hakuaikaId;
    }

    public void setHakuaikaId(String hakuaikaId) {
        this.hakuaikaId = hakuaikaId;
    }

    public List<OrganizationGroupDTO> getOrganizationGroups() {
        return organizationGroups;
    }

    public void setOrganizationGroups(List<OrganizationGroupDTO> organizationGroups) {
        this.organizationGroups = organizationGroups;
    }

    public String getApplicationPeriodName() {
        return applicationPeriodName;
    }

    public void setApplicationPeriodName(String applicationPeriodName) {
        this.applicationPeriodName = applicationPeriodName;
    }
    public String getStartingQuotaDescription() {
        return startingQuotaDescription;
    }

    public void setStartingQuotaDescription(String startingQuotaDescription) {
        this.startingQuotaDescription = startingQuotaDescription;
    }

    public List<String> getTeachingLanguageNames() {
        return teachingLanguageNames;
    }

    public void setTeachingLanguageNames(List<String> teachingLanguageNames) {
        this.teachingLanguageNames = teachingLanguageNames;
    }

    public ApplicationOfficeDTO getApplicationOffice() {
        return applicationOffice;
    }

    public void setApplicationOffice(ApplicationOfficeDTO applicationOffice) {
        this.applicationOffice = applicationOffice;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getHakuMenettelyKuvaukset() {
        return hakuMenettelyKuvaukset;
    }

    public void setHakuMenettelyKuvaukset(String hakuMenettelyKuvaukset) {
        this.hakuMenettelyKuvaukset = hakuMenettelyKuvaukset;
    }

    public String getPeruutusEhdotKuvaukset() {
        return peruutusEhdotKuvaukset;
    }

    public void setPeruutusEhdotKuvaukset(String peruutusEhdotKuvaukset) {
        this.peruutusEhdotKuvaukset = peruutusEhdotKuvaukset;
    }

    public boolean isKysytaanHarkinnanvaraiset() {
        return kysytaanHarkinnanvaraiset;
    }

    public void setKysytaanHarkinnanvaraiset(boolean kysytaanHarkinnanvaraiset) {
        this.kysytaanHarkinnanvaraiset = kysytaanHarkinnanvaraiset;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }
}
