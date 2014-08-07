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
 * 
 * @author Markus
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class HigherEducationLOSDTO extends StandaloneLOSDTO {

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
    private String infoAboutCharge;
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
    private List<String> qualifications;
    private Boolean chargeable;
    private String educationCode;
    private List<String> teachingLanguages;

    private LearningOpportunityProviderDTO provider;
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();

    private List<HigherEducationChildLosReferenceDTO> children = new ArrayList<HigherEducationChildLosReferenceDTO>();
    private List<HigherEducationChildLosReferenceDTO> parents = new ArrayList<HigherEducationChildLosReferenceDTO>();

    private List<CodeDTO> prerequisites;
    private String translationLanguage;
    private List<CodeDTO> availableTranslationLanguages;

    private List<String> formOfTeaching;

    private List<String> professionalTitles;
    private String educationDegreeName;
    private List<String> teachingTimes;
    private List<String> teachingPlaces;
    private String startSeason;
    private int startYear;
    private String status;
    
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;
    
    private List<ArticleResultDTO> edCodeSuggestions = new ArrayList<ArticleResultDTO>();
    private List<ArticleResultDTO> edTypeSuggestions = new ArrayList<ArticleResultDTO>();
    private String educationType;
    
    private String structureImageId;
    
    private PictureDTO structureImage;
    

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

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
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

    public List<HigherEducationChildLosReferenceDTO> getChildren() {
        return children;
    }

    public void setChildren(List<HigherEducationChildLosReferenceDTO> children) {
        this.children = children;
    }

    public List<CodeDTO> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<CodeDTO> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setTranslationLanguage(String lang) {
        this.translationLanguage = lang;	
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setAvailableTranslationLanguages(
            List<CodeDTO> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;	
    }

    public List<CodeDTO> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;

    }
    public String getCreditUnit() {
        return creditUnit;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<String> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<String> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public String getEducationDegreeName() {
        return educationDegreeName;
    }

    public void setEducationDegreeName(String educationDegreeName) {
        this.educationDegreeName = educationDegreeName;
    }
    public List<String> getTeachingTimes() {
        return teachingTimes;
    }

    public void setTeachingTimes(List<String> teachingTimes) {
        this.teachingTimes = teachingTimes;
    }

    public List<String> getTeachingPlaces() {
        return teachingPlaces;
    }
    public void setTeachingPlaces(List<String> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;
    }

    public String getInfoAboutCharge() {
        return infoAboutCharge;
    }

    public void setInfoAboutCharge(String infoAboutCharge) {
        this.infoAboutCharge = infoAboutCharge;
    }

    public List<HigherEducationChildLosReferenceDTO> getParents() {
        return parents;
    }

    public void setParents(List<HigherEducationChildLosReferenceDTO> parents) {
        this.parents = parents;
    }

    public String getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(String startSeason) {
        this.startSeason = startSeason;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<CodeDTO> getTopics() {
        return topics;
    }
    public void setTopics(List<CodeDTO> topics) {
        this.topics = topics;
    }
    public List<CodeDTO> getThemes() {
        return themes;
    }
    public void setThemes(List<CodeDTO> themes) {
        this.themes = themes;
    }

    public List<ArticleResultDTO> getEdCodeSuggestions() {
        return edCodeSuggestions;
    }

    public void setEdCodeSuggestions(List<ArticleResultDTO> edCodeSuggestions) {
        this.edCodeSuggestions = edCodeSuggestions;
    }

    public List<ArticleResultDTO> getEdTypeSuggestions() {
        return edTypeSuggestions;
    }

    public void setEdTypeSuggestions(List<ArticleResultDTO> edTypeSuggestions) {
        this.edTypeSuggestions = edTypeSuggestions;
    }

    public String getEducationType() {
        return educationType;
    }
    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public String getStructureImageId() {
        return structureImageId;
    }

    public void setStructureImageId(String structureImageId) {
        this.structureImageId = structureImageId;
    }

    public PictureDTO getStructureImage() {
        return structureImage;
    }

    public void setStructureImage(PictureDTO structureImage) {
        this.structureImage = structureImage;
    }
}
