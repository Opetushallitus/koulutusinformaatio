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
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author Markus
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class UniversityAppliedScienceLOSDTO {

	private String id;
	private String infoAboutTeachingLangs;
	private String content;
	private String goals;
	private String majorSelection;
	private String structure;
	private String finalExam;
	private String careerOpportunities;
	private String internationalization;
	private String cooperation;
	private String competence;
	private String researchFocus;
	private String accessToFurtherStudies;
	private List<ContactPersonDTO> contactPersons = new ArrayList<ContactPersonDTO>();
	private String educationDomain;
	private String name;
	private String koulutuskoodi;
	private String educationDegree;
    private String degreeTitle;
	private Date startDate;
	private String plannedDuration;
	private String plannedDurationUnit;
	private String pduCodeUri;
	private String creditValue;
	private String creditUnit;


	private String degree;
	private String qualification;
	private Boolean chargeable;
	private String educationCode;
	private List<String> teachingLanguages;
	
	private LearningOpportunityProviderDTO provider;
	private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
	
	private List<UniversityAppliedScienceLOSDTO> children = new ArrayList<UniversityAppliedScienceLOSDTO>();
	
	private CodeDTO prerequisite;
	private String translationLanguage;
	private Set<String> availableTranslationLanguages;
	


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInfoAboutTeachingLangs() {
		return infoAboutTeachingLangs;
	}

	public void setInfoAboutTeachingLangs(String infoAboutTeachingLangs) {
		this.infoAboutTeachingLangs = infoAboutTeachingLangs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getMajorSelection() {
		return majorSelection;
	}

	public void setMajorSelection(String majorSelection) {
		this.majorSelection = majorSelection;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getFinalExam() {
		return finalExam;
	}

	public void setFinalExam(String finalExam) {
		this.finalExam = finalExam;
	}

	public String getCareerOpportunities() {
		return careerOpportunities;
	}

	public void setCareerOpportunities(String careerOpportunities) {
		this.careerOpportunities = careerOpportunities;
	}

	public String getInternationalization() {
		return internationalization;
	}

	public void setInternationalization(String internationalization) {
		this.internationalization = internationalization;
	}

	public String getCooperation() {
		return cooperation;
	}

	public void setCooperation(String cooperation) {
		this.cooperation = cooperation;
	}

	public String getCompetence() {
		return competence;
	}

	public void setCompetence(String competence) {
		this.competence = competence;
	}

	public String getResearchFocus() {
		return researchFocus;
	}

	public void setResearchFocus(String researchFocus) {
		this.researchFocus = researchFocus;
	}

	public String getAccessToFurtherStudies() {
		return accessToFurtherStudies;
	}

	public void setAccessToFurtherStudies(String accessToFurtherStudies) {
		this.accessToFurtherStudies = accessToFurtherStudies;
	}

	public List<ContactPersonDTO> getContactPersons() {
		return contactPersons;
	}

	public void setContactPersons(List<ContactPersonDTO> contactPersons) {
		this.contactPersons = contactPersons;
	}

	public String getEducationDomain() {
		return educationDomain;
	}

	public void setEducationDomain(String educationDomain) {
		this.educationDomain = educationDomain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKoulutuskoodi() {
		return koulutuskoodi;
	}

	public void setKoulutuskoodi(String koulutuskoodi) {
		this.koulutuskoodi = koulutuskoodi;
	}

	public String getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(String educationDegree) {
		this.educationDegree = educationDegree;
	}

	public String getDegreeTitle() {
		return degreeTitle;
	}

	public void setDegreeTitle(String degreeTitle) {
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

	public String getPlannedDurationUnit() {
		return plannedDurationUnit;
	}

	public void setPlannedDurationUnit(String plannedDurationUnit) {
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

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
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

	public List<String> getTeachingLanguages() {
		return teachingLanguages;
	}

	public void setTeachingLanguages(List<String> teachingLanguages) {
		this.teachingLanguages = teachingLanguages;
	}

	public LearningOpportunityProviderDTO getProvider() {
		return provider;
	}

	public void setProvider(LearningOpportunityProviderDTO provider) {
		this.provider = provider;
	}

	public List<ApplicationSystemDTO> getApplicationSystems() {
		return applicationSystems;
	}

	public void setApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {
		this.applicationSystems = applicationSystems;
	}

	public List<UniversityAppliedScienceLOSDTO> getChildren() {
		return children;
	}

	public void setChildren(List<UniversityAppliedScienceLOSDTO> children) {
		this.children = children;
	}

	public CodeDTO getPrerequisite() {
		return prerequisite;
	}

	public void setPrerequisite(CodeDTO prerequisite) {
		this.prerequisite = prerequisite;
	}

	public void setTranslationLanguage(String lang) {
		this.translationLanguage = lang;	
	}
	
	public String getTranslationLanguage() {
		return translationLanguage;
	}

	public void setAvailableTranslationLanguages(
			Set<String> availableTranslationLanguages) {
		this.availableTranslationLanguages = availableTranslationLanguages;	
	}
	
	public Set<String> getAvailableTranslationLanguages() {
		return availableTranslationLanguages;
	}

	public void setCreditUnit(String creditUnit) {
		this.creditUnit = creditUnit;
		
	}
	public String getCreditUnit() {
		return creditUnit;
	}

}
