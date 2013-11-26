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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOption {

    private String id;
    private I18nText name;
    private String aoIdentifier;
    private ApplicationSystem applicationSystem;
    private String educationDegree;
    private List<ChildLOIRef> childLOIRefs = new ArrayList<ChildLOIRef>();
    private Provider provider;
    // "aloituspaikatLkm" : 10,
    private Integer startingQuota;
    // "alinValintaPistemaara" : 0,
    private Integer lowestAcceptedScore;
    // "alinHyvaksyttavaKeskiarvo" : 0.0,
    private Double lowestAcceptedAverage;
    // "liitteidenToimitusPvm" : 1367874000000,
    private Date attachmentDeliveryDeadline;
    private Address attachmentDeliveryAddress;
    // edellisenVuodenHakijatLkm
    private Integer lastYearApplicantCount;
    // onko soravaatimuksia
    private boolean sora;
    // opetuskielikoodit : [FI, SV]
    private List<String> teachingLanguages;
    // tutkinnon viite
    private ParentLOSRef parent;
    // valintaperustekuvaus
    private I18nText selectionCriteria;
    private Code prerequisite;
    // base educations, one of these is required to be able to apply to this application option
    private List<String> requiredBaseEducations;
    private List<Exam> exams;
    // application option specific application dates or application system dates
    private boolean specificApplicationDates;
    private Date applicationStartDate;
    private Date applicationEndDate;
    private List<ApplicationOptionAttachment> attachments;
    private List<EmphasizedSubject> emphasizedSubjects;
    private I18nText additionalInfo;
    private AdditionalProof additionalProof;
    private ScoreLimit overallScoreLimit;
    private boolean kaksoistutkinto;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public String getAoIdentifier() {
        return aoIdentifier;
    }

    public void setAoIdentifier(String aoIdentifier) {
        this.aoIdentifier = aoIdentifier;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public void setApplicationSystem(ApplicationSystem applicationSystem) {
        this.applicationSystem = applicationSystem;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public List<ChildLOIRef> getChildLOIRefs() {
        return childLOIRefs;
    }

    public void setChildLOIRefs(List<ChildLOIRef> childLOIRefs) {
        this.childLOIRefs = childLOIRefs;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
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

    public Address getAttachmentDeliveryAddress() {
        return attachmentDeliveryAddress;
    }

    public void setAttachmentDeliveryAddress(Address attachmentDeliveryAddress) {
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

    public ParentLOSRef getParent() {
        return parent;
    }

    public void setParent(ParentLOSRef parent) {
        this.parent = parent;
    }

    public I18nText getSelectionCriteria() {
        return selectionCriteria;
    }

    public void setSelectionCriteria(I18nText selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }

    public Code getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<String> getRequiredBaseEducations() {
        return requiredBaseEducations;
    }

    public void setRequiredBaseEducations(List<String> requiredBaseEducations) {
        this.requiredBaseEducations = requiredBaseEducations;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
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

    public List<ApplicationOptionAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationOptionAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<EmphasizedSubject> getEmphasizedSubjects() {
        return emphasizedSubjects;
    }

    public void setEmphasizedSubjects(List<EmphasizedSubject> emphasizedSubjects) {
        this.emphasizedSubjects = emphasizedSubjects;
    }

    public I18nText getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(I18nText additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public AdditionalProof getAdditionalProof() {
        return additionalProof;
    }

    public void setAdditionalProof(AdditionalProof additionalProof) {
        this.additionalProof = additionalProof;
    }

    public ScoreLimit getOverallScoreLimit() {
        return overallScoreLimit;
    }

    public void setOverallScoreLimit(ScoreLimit overallScoreLimit) {
        this.overallScoreLimit = overallScoreLimit;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationOption that = (ApplicationOption) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
