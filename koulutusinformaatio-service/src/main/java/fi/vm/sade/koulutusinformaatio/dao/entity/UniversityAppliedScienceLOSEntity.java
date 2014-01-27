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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;


/**
 * 
 * @author Markus
 */
@Entity("universityAppliedScienceLOS")
public class UniversityAppliedScienceLOSEntity {
	

	//Varmistetut
	@Id
    private String id;
    @Embedded
	private I18nTextEntity infoAboutTeachingLangs;
    @Embedded
	private I18nTextEntity content;
    @Embedded
	private I18nTextEntity goals;
    @Embedded
	private I18nTextEntity majorSelection;
    @Embedded
	private I18nTextEntity structure;
    @Embedded
	private I18nTextEntity finalExam;
    @Embedded
	private I18nTextEntity careerOpportunities;
    @Embedded
	private I18nTextEntity internationalization;
    @Embedded
	private I18nTextEntity cooperation;
    @Embedded
	private I18nTextEntity competence;
    @Embedded
	private I18nTextEntity researchFocus;
    @Embedded
	private I18nTextEntity accessToFurtherStudies;
    @Embedded
	private List<ContactPersonEntity> contactPersons = new ArrayList<ContactPersonEntity>();
    @Embedded
	private I18nTextEntity educationDomain;
    @Embedded
	private I18nTextEntity name;
    @Embedded
	private I18nTextEntity koulutuskoodi;
	private String educationDegree;
	@Embedded
    private I18nTextEntity educationDegreeName;

	@Embedded
    private I18nTextEntity degreeTitle;
	private Date startDate;
	private String plannedDuration;
	
	@Embedded
	private I18nTextEntity plannedDurationUnit;
	private String pduCodeUri;
	private String creditValue;
	@Embedded
	private I18nTextEntity creditUnit;
	@Embedded
	private I18nTextEntity degree;
	@Embedded
	private I18nTextEntity qualification;
	private Boolean chargeable;
	private String educationCode;
	@Embedded
	private List<CodeEntity> teachingLanguages;
	@Reference
    private LearningOpportunityProviderEntity provider;
	@Reference
    private List<ApplicationOptionEntity> applicationOptions;
	
	private String komoOid;
	private List<String> childKomoOids;
	@Embedded
	private List<I18nTextEntity> formOfTeaching;
	@Embedded
	private List<I18nTextEntity> professionalTitles;
	
	@Reference
	private List<UniversityAppliedScienceLOSEntity> children;
	
	@Embedded
	private List<CodeEntity> prerequisites;
	

	private String type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public I18nTextEntity getInfoAboutTeachingLangs() {
		return infoAboutTeachingLangs;
	}
	public void setInfoAboutTeachingLangs(I18nTextEntity infoAboutTeachingLangs) {
		this.infoAboutTeachingLangs = infoAboutTeachingLangs;
	}
	public I18nTextEntity getContent() {
		return content;
	}
	public void setContent(I18nTextEntity content) {
		this.content = content;
	}
	public I18nTextEntity getGoals() {
		return goals;
	}
	public void setGoals(I18nTextEntity goals) {
		this.goals = goals;
	}
	public I18nTextEntity getMajorSelection() {
		return majorSelection;
	}
	public void setMajorSelection(I18nTextEntity majorSelection) {
		this.majorSelection = majorSelection;
	}
	public I18nTextEntity getStructure() {
		return structure;
	}
	public void setStructure(I18nTextEntity structure) {
		this.structure = structure;
	}
	public I18nTextEntity getFinalExam() {
		return finalExam;
	}
	public void setFinalExam(I18nTextEntity finalExam) {
		this.finalExam = finalExam;
	}
	public I18nTextEntity getCareerOpportunities() {
		return careerOpportunities;
	}
	public void setCareerOpportunities(I18nTextEntity careerOpportunities) {
		this.careerOpportunities = careerOpportunities;
	}
	public I18nTextEntity getInternationalization() {
		return internationalization;
	}
	public void setInternationalization(I18nTextEntity internationalization) {
		this.internationalization = internationalization;
	}
	public I18nTextEntity getCooperation() {
		return cooperation;
	}
	public void setCooperation(I18nTextEntity cooperation) {
		this.cooperation = cooperation;
	}
	public I18nTextEntity getCompetence() {
		return competence;
	}
	public void setCompetence(I18nTextEntity competence) {
		this.competence = competence;
	}
	public I18nTextEntity getResearchFocus() {
		return researchFocus;
	}
	public void setResearchFocus(I18nTextEntity researchFocus) {
		this.researchFocus = researchFocus;
	}
	public I18nTextEntity getAccessToFurtherStudies() {
		return accessToFurtherStudies;
	}
	public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
		this.accessToFurtherStudies = accessToFurtherStudies;
	}
	public List<ContactPersonEntity> getContactPersons() {
		return contactPersons;
	}
	public void setContactPersons(List<ContactPersonEntity> contactPersons) {
		this.contactPersons = contactPersons;
	}
	public I18nTextEntity getEducationDomain() {
		return educationDomain;
	}
	public void setEducationDomain(I18nTextEntity educationDomain) {
		this.educationDomain = educationDomain;
	}
	public I18nTextEntity getName() {
		return name;
	}
	public void setName(I18nTextEntity name) {
		this.name = name;
	}
	public I18nTextEntity getKoulutuskoodi() {
		return koulutuskoodi;
	}
	public void setKoulutuskoodi(I18nTextEntity koulutuskoodi) {
		this.koulutuskoodi = koulutuskoodi;
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getPlannedDuration() {
		return plannedDuration;
	}
	public void setPlannedDuration(String plannedDuration) {
		this.plannedDuration = plannedDuration;
	}
	public I18nTextEntity getPlannedDurationUnit() {
		return plannedDurationUnit;
	}
	public void setPlannedDurationUnit(I18nTextEntity plannedDurationUnit) {
		this.plannedDurationUnit = plannedDurationUnit;
	}
	public String getPduCodeUri() {
		return pduCodeUri;
	}
	public void setPduCodeUri(String pduCodeUri) {
		this.pduCodeUri = pduCodeUri;
	}
	public String getCreditValue() {
		return creditValue;
	}
	public void setCreditValue(String creditValue) {
		this.creditValue = creditValue;
	}
	public I18nTextEntity getDegree() {
		return degree;
	}
	public void setDegree(I18nTextEntity degree) {
		this.degree = degree;
	}
	public I18nTextEntity getQualification() {
		return qualification;
	}
	public void setQualification(I18nTextEntity qualification) {
		this.qualification = qualification;
	}
	public Boolean getChargeable() {
		return chargeable;
	}
	public void setChargeable(Boolean chargeable) {
		this.chargeable = chargeable;
	}
	public String getEducationCode() {
		return educationCode;
	}
	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}
	public List<CodeEntity> getTeachingLanguages() {
		return teachingLanguages;
	}
	public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
		this.teachingLanguages = teachingLanguages;
	}	
	public LearningOpportunityProviderEntity getProvider() {
		return provider;
	}
	public void setProvider(LearningOpportunityProviderEntity provider) {
		this.provider = provider;
	}
	public List<ApplicationOptionEntity> getApplicationOptions() {
		return applicationOptions;
	}
	public void setApplicationOptions(
			List<ApplicationOptionEntity> applicationOptions) {
		this.applicationOptions = applicationOptions;
	}
	public String getKomoOid() {
		return komoOid;
	}
	public void setKomoOid(String komoOid) {
		this.komoOid = komoOid;
	}
	public List<String> getChildKomoOids() {
		return childKomoOids;
	}
	public void setChildKomoOids(List<String> childKomoOids) {
		this.childKomoOids = childKomoOids;
	}
	public List<UniversityAppliedScienceLOSEntity> getChildren() {
		return children;
	}
	public void setChildren(List<UniversityAppliedScienceLOSEntity> children) {
		this.children = children;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<I18nTextEntity> getFormOfTeaching() {
		return formOfTeaching;
	}
	public void setFormOfTeaching(List<I18nTextEntity> formOfTeaching) {
		this.formOfTeaching = formOfTeaching;
	}
	public List<I18nTextEntity> getProfessionalTitles() {
		return professionalTitles;
	}
	public void setProfessionalTitles(List<I18nTextEntity> professionalTitles) {
		this.professionalTitles = professionalTitles;
	}
	public List<CodeEntity> getPrerequisites() {
		return prerequisites;
	}
	public void setPrerequisites(List<CodeEntity> prerequisites) {
		this.prerequisites = prerequisites;
	}
	public I18nTextEntity getCreditUnit() {
		return creditUnit;
	}
	public void setCreditUnit(I18nTextEntity creditUnit) {
		this.creditUnit = creditUnit;
	}
    public I18nTextEntity getEducationDegreeName() {
		return educationDegreeName;
	}
	public void setEducationDegreeName(I18nTextEntity educationDegreeName) {
		this.educationDegreeName = educationDegreeName;
	}
}
