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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class BasketApplicationOptionDTO {

    private String id;
    private String name;
    private String educationDegree;
    private boolean sora;
    private List<String> teachingLanguages;
    private String providerName;
    private String providerId;
    private String providerLocation;
    private String qualification;
    private String baseEducationRequirement;
    private ParentLOSRefDTO parent;
    private List<HigherEducationLOSRefDTO> losRefs;
    private List<ChildLOIRefDTO> children;
    private Date attachmentDeliveryDeadline;
    private List<ApplicationOptionAttachmentDTO> attachments;
    private List<ExamDTO> exams;
    private boolean athleteEducation;
    private String aoIdentifier;
    private boolean kaksoistutkinto;
    private boolean vocational;
    private String educationCodeUri;
    private boolean isHigherEducation;
    private String type;
    private String educationTypeUri;
    private CodeDTO prerequisite;
    private List<DateRangeDTO> applicationDates = new ArrayList<DateRangeDTO>();
    private boolean canBeApplied;
    private Date nextApplicationPeriodStarts;
    private String hakutapaUri;
    private String applicationFormLink;
    private String asId;
    private String asName;
    private boolean kotitalous;
    private String hakuaikaId;
    private boolean useSystemApplicationForm;
    private boolean isPseudo;
    private boolean isPaid;


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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderLocation() {
        return providerLocation;
    }

    public void setProviderLocation(String providerLocation) {
        this.providerLocation = providerLocation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getBaseEducationRequirement() {
        return baseEducationRequirement;
    }

    public void setBaseEducationRequirement(String baseEducationRequirement) {
        this.baseEducationRequirement = baseEducationRequirement;
    }

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }

    public List<ChildLOIRefDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLOIRefDTO> children) {
        this.children = children;
    }


    public Date getAttachmentDeliveryDeadline() {
        return attachmentDeliveryDeadline;
    }

    public void setAttachmentDeliveryDeadline(Date attachmentDeliveryDeadline) {
        this.attachmentDeliveryDeadline = attachmentDeliveryDeadline;
    }


    public List<ApplicationOptionAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationOptionAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<ExamDTO> getExams() {
        return exams;
    }

    public void setExams(List<ExamDTO> exams) {
        this.exams = exams;
    }

    public boolean isAthleteEducation() {
        return athleteEducation;
    }

    public void setAthleteEducation(boolean athleteEducation) {
        this.athleteEducation = athleteEducation;
    }

    public String getAoIdentifier() {
        return aoIdentifier;
    }

    public void setAoIdentifier(String aoIdentifier) {
        this.aoIdentifier = aoIdentifier;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }

    public boolean isVocational() {
        return vocational;
    }

    public void setVocational(boolean vocational) {
        this.vocational = vocational;
    }

    public String getEducationCodeUri() { return educationCodeUri; }

    public void setEducationCodeUri(String educationCodeUri) { this.educationCodeUri = educationCodeUri; }

    public boolean isHigherEducation() {
        return isHigherEducation;
    }

    public void setHigherEducation(boolean isHigherEducation) {
        this.isHigherEducation = isHigherEducation;
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

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<HigherEducationLOSRefDTO> getLosRefs() {
        return losRefs;
    }

    public void setLosRefs(List<HigherEducationLOSRefDTO> losRefs) {
        this.losRefs = losRefs;
    }

    public List<DateRangeDTO> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRangeDTO> applicationDates) {
        this.applicationDates = applicationDates;
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

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this.hakutapaUri = hakutapaUri;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }

    public String getAsId() {
        return asId;
    }

    public void setAsId(String asId) {
        this.asId = asId;
    }

    public String getAsName() {
        return asName;
    }

    public void setAsName(String asName) {
        this.asName = asName;
    }
    
    public boolean isKotitalous() {
        return kotitalous;
    }

    public void setKotitalous(boolean kotitalous) {
        this.kotitalous = kotitalous;
    }

    public String getHakuaikaId() {
        return hakuaikaId;
    }

    public void setHakuaikaId(String hakuaikaId) {
        this.hakuaikaId = hakuaikaId;
    }

    public boolean isUseSystemApplicationForm() {
        return useSystemApplicationForm;
    }

    public void setUseSystemApplicationForm(boolean useSystemApplicationForm) {
        this.useSystemApplicationForm = useSystemApplicationForm;
    }

    public boolean isPseudo() {
        return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
        this.isPseudo = isPseudo;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
}
