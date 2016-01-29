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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

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
    private List<HigherEducationLOSRef> higherEdLOSRefs = new ArrayList<HigherEducationLOSRef>();
    private Provider provider;
    private ApplicationOffice applicationOffice;
    // "aloituspaikatLkm" : 10,
    private Integer startingQuota;
    private Integer firstTimerStartingQuota;
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
    // opetuskielikoodien nimet
    private List<I18nText> teachingLanguageNames;
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
    private I18nText applicationPeriodName;
    private String InternalASDateRef;
    private List<ApplicationOptionAttachment> attachments;
    private List<EmphasizedSubject> emphasizedSubjects;
    private I18nText additionalInfo;
    private AdditionalProof additionalProof;
    private ScoreLimit overallScoreLimit;
    private boolean kaksoistutkinto;
    // lukion urheilulinjat jne.
    private boolean athleteEducation;
    //ammatillinen koulutus
    private boolean vocational;
    // koulutuskoodiuri
    private String educationCodeUri;
    private I18nText soraDescription;
    private String status;
    private I18nText eligibilityDescription;
    private String type;
    private String educationTypeUri;
    private List<OrganizationGroup> organizationGroups;
    private I18nText startingQuotaDescription;
    // Tällä AOlla ei ole osaamisalaa
    private boolean isPseudo;
    private List<String> komotoOids = new ArrayList<String>();
    private boolean paid;
    private boolean isJosYoEiMuitaLiitepyyntoja;
    private boolean kysytaanHarkinnanvaraiset;
    private List<String> pohjakoulutusLiitteet = new ArrayList<>();

    private I18nText hakuMenettelyKuvaukset;
    private I18nText peruutusEhdotKuvaukset;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ApplicationOption that = (ApplicationOption) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setSoraDescription(I18nText i18nText) {
        this.soraDescription = i18nText;
    }

    public I18nText getSoraDescription() {
        return soraDescription;
    }

    public void setStatus(String tila) {
        this.status = tila;
    }

    public String getStatus() {
        return status;
    }

    public List<DateRange> getApplicationDates() {
        if (applicationStartDate != null && applicationEndDate != null) {
            return Lists.newArrayList(new DateRange(applicationStartDate, applicationEndDate));
        }
        else {
            return applicationSystem.getApplicationDates();
        }
    }

    public void setEligibilityDescription(I18nText i18nText) {
        this.eligibilityDescription = i18nText;
    }

    public I18nText getEligibilityDescription() {
        return eligibilityDescription;
    }

    public List<HigherEducationLOSRef> getHigherEdLOSRefs() {
        return higherEdLOSRefs;
    }

    public void setHigherEdLOSRefs(List<HigherEducationLOSRef> higherEdLOSRefs) {
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

    public String getInternalASDateRef() {
        return InternalASDateRef;
    }

    public void setInternalASDateRef(String internalASDateRef) {
        InternalASDateRef = internalASDateRef;
    }

    public void setOrganizationGroups(List<OrganizationGroup> organizationGroups) {
        this.organizationGroups = organizationGroups;
    }

    public List<OrganizationGroup> getOrganizationGroups() {
        return organizationGroups;
    }

    public I18nText getApplicationPeriodName() {
        return applicationPeriodName;
    }

    public void setApplicationPeriodName(I18nText applicationPeriodName) {
        this.applicationPeriodName = applicationPeriodName;
    }

    public void setStartingQuotaDescription(I18nText aloituspaikkaKuvaus) {
        this.startingQuotaDescription = aloituspaikkaKuvaus;
    }

    public I18nText getStartingQuotaDescription() {
        return startingQuotaDescription;
    }

    public List<I18nText> getTeachingLanguageNames() {
        return teachingLanguageNames;
    }

    public void setTeachingLanguageNames(List<I18nText> teachingLanguageNames) {
        this.teachingLanguageNames = teachingLanguageNames;
    }

    public boolean isPseudo() {
        return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
        this.isPseudo = isPseudo;
    }

    public boolean showInOpintopolku() {
        Date now = new Date();
        for (DateRange dr : getApplicationDates()) {
            if (dr.getStartDate().after(now)) { // In Future
                return true;
            }

            Date endDate = dr.getEndDate();
            if (endDate == null || endDate.after(now)) {
                return true;
            }

            if (getLastDayToShow(endDate).after(now)) {
                return true;
            }
        }
        return false;
    }

    private Date getLastDayToShow(Date endDate) {
        Calendar cal = Calendar.getInstance();
        Date lastDayToShow = getApplicationSystem().getShowEducationsUntil();

        if (lastDayToShow == null) {
            cal.setTime(endDate);
            cal.add(Calendar.MONTH, 10);
        }
        else {
            cal.setTime(lastDayToShow);
        }

        // End of day
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    public ApplicationOffice getApplicationOffice() {
        return applicationOffice;
    }

    public void setApplicationOffice(ApplicationOffice applicationOffice) {
        this.applicationOffice = applicationOffice;
    }

    public List<String> getKomotoOids() {
        return komotoOids;
    }

    public void setKomotoOids(List<String> komotoOids) {
        this.komotoOids = komotoOids;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public I18nText getHakuMenettelyKuvaukset() {
        return hakuMenettelyKuvaukset;
    }

    public void setHakuMenettelyKuvaukset(I18nText hakuMenettelyKuvaukset) {
        this.hakuMenettelyKuvaukset = hakuMenettelyKuvaukset;
    }

    public I18nText getPeruutusEhdotKuvaukset() {
        return peruutusEhdotKuvaukset;
    }

    public void setPeruutusEhdotKuvaukset(I18nText peruutusEhdotKuvaukset) {
        this.peruutusEhdotKuvaukset = peruutusEhdotKuvaukset;
    }

    public boolean isKysytaanHarkinnanvaraiset() {
        return kysytaanHarkinnanvaraiset;
    }

    public void setKysytaanHarkinnanvaraiset(boolean kysytaanHarkinnanvaraiset) {
        this.kysytaanHarkinnanvaraiset = kysytaanHarkinnanvaraiset;
    }

}
