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
import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;


/**
 * 
 * @author Markus
 */
@Entity("universityAppliedScienceLOS")
public class HigherEducationLOSEntity {


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
    private I18nTextEntity shortTitle;
    @Embedded
    private I18nTextEntity koulutuskoodi;
    private String educationDegree;
    @Embedded
    private I18nTextEntity educationDegreeLang;

    @Embedded
    private I18nTextEntity degreeTitle;
    @Embedded
    private List<I18nTextEntity> degreeTitles;
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
    private List<I18nTextEntity> qualifications;
    private Boolean chargeable;
    private String hinta;
    @Embedded
    private CodeEntity educationCode;
    @Embedded
    private I18nTextEntity infoAboutCharge;
    @Embedded
    private List<CodeEntity> teachingLanguages;
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Reference
    private List<LearningOpportunityProviderEntity> additionalProviders = new ArrayList<LearningOpportunityProviderEntity>();
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;

    private String komoOid;
    @Embedded
    private List<I18nTextEntity> formOfTeaching;
    @Embedded
    private List<I18nTextEntity> professionalTitles;

    @Reference
    private List<HigherEducationLOSEntity> children;
    @Reference
    private List<HigherEducationLOSEntity> parents;

    @Embedded
    private List<CodeEntity> prerequisites = new ArrayList<CodeEntity>();
    @Embedded
    private List<I18nTextEntity> teachingTimes;
    @Embedded
    private List<I18nTextEntity> teachingPlaces;

    private String type;
    @Embedded
    private I18nTextEntity startSeason;
    private int startYear;
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;

    @Embedded
    private List<CodeEntity> facetPrerequisites = new ArrayList<CodeEntity>();
    
    @Embedded
    private List<CodeEntity> topics;
    @Embedded
    private List<CodeEntity> themes;

    @Embedded
    private Map<String, List<String>> subjects;
    
    private String educationType;
    
    @Embedded
    private I18nPictureEntity structureImage;
    
    @Embedded
    private List<CodeEntity> fotFacet = new ArrayList<CodeEntity>();
    
    @Embedded
    private List<CodeEntity> timeOfTeachingFacet = new ArrayList<CodeEntity>();
    @Embedded
    private List<CodeEntity> formOfStudyFacet = new ArrayList<CodeEntity>();
    
    @Embedded
    private CodeEntity koulutuslaji;

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

    public I18nTextEntity getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nTextEntity shortTitle) {
        this.shortTitle = shortTitle;
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
    public List<I18nTextEntity> getQualifications() {
        return qualifications;
    }
    public void setQualifications(List<I18nTextEntity> qualifications) {
        this.qualifications = qualifications;
    }
    public Boolean getChargeable() {
        return chargeable;
    }
    public void setChargeable(Boolean chargeable) {
        this.chargeable = chargeable;
    }
    public CodeEntity getEducationCode() {
        return educationCode;
    }
    public void setEducationCode(CodeEntity educationCode) {
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
    public List<HigherEducationLOSEntity> getChildren() {
        return children;
    }
    public void setChildren(List<HigherEducationLOSEntity> children) {
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
    public I18nTextEntity getEducationDegreeLang() {
        return educationDegreeLang;
    }
    public void setEducationDegreeLang(I18nTextEntity educationDegreeLang) {
        this.educationDegreeLang = educationDegreeLang;
    }
    public I18nTextEntity getInfoAboutCharge() {
        return infoAboutCharge;
    }
    public void setInfoAboutCharge(I18nTextEntity infoAboutCharge) {
        this.infoAboutCharge = infoAboutCharge;
    }
    public List<I18nTextEntity> getTeachingTimes() {
        return teachingTimes;
    }
    public void setTeachingTimes(List<I18nTextEntity> teachingTimes) {
        this.teachingTimes = teachingTimes;
    }
    public List<I18nTextEntity> getTeachingPlaces() {
        return teachingPlaces;
    }
    public void setTeachingPlaces(List<I18nTextEntity> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;
    }
    public List<HigherEducationLOSEntity> getParents() {
        return parents;
    }
    public void setParents(List<HigherEducationLOSEntity> parents) {
        this.parents = parents;
    }
    public I18nTextEntity getStartSeason() {
        return startSeason;
    }
    public void setStartSeason(I18nTextEntity startSeason) {
        this.startSeason = startSeason;
    }
    public int getStartYear() {
        return startYear;
    }
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }
    public void setAvailableTranslationLanguages(List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
    public List<CodeEntity> getFacetPrerequisites() {
        return facetPrerequisites;
    }
    public void setFacetPrerequisites(List<CodeEntity> facetPrerequisites) {
        this.facetPrerequisites = facetPrerequisites;
    }
    public List<CodeEntity> getTopics() {
        return topics;
    }
    public void setTopics(List<CodeEntity> topics) {
        this.topics = topics;
    }
    public List<CodeEntity> getThemes() {
        return themes;
    }
    public void setThemes(List<CodeEntity> themes) {
        this.themes = themes;
    }
    public String getEducationType() {
        return educationType;
    }
    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }
    public I18nPictureEntity getStructureImage() {
        return structureImage;
    }
    public void setStructureImage(I18nPictureEntity structureImage) {
        this.structureImage = structureImage;
    }
    public List<CodeEntity> getFotFacet() {
        return fotFacet;
    }
    public void setFotFacet(List<CodeEntity> fotFacet) {
        this.fotFacet = fotFacet;
    }
    public List<CodeEntity> getTimeOfTeachingFacet() {
        return timeOfTeachingFacet;
    }
    public void setTimeOfTeachingFacet(List<CodeEntity> timeOfTeachingFacet) {
        this.timeOfTeachingFacet = timeOfTeachingFacet;
    }
    public List<CodeEntity> getFormOfStudyFacet() {
        return formOfStudyFacet;
    }
    public void setFormOfStudyFacet(List<CodeEntity> formOfStudyFacet) {
        this.formOfStudyFacet = formOfStudyFacet;
    }
    public CodeEntity getKoulutuslaji() {
        return koulutuslaji;
    }
    public void setKoulutuslaji(CodeEntity koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }
    public List<LearningOpportunityProviderEntity> getAdditionalProviders() {
        return additionalProviders;
    }
    public void setAdditionalProviders(
            List<LearningOpportunityProviderEntity> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }
	public List<I18nTextEntity> getDegreeTitles() {
		return degreeTitles;
	}
	public void setDegreeTitles(List<I18nTextEntity> degreeTitles) {
		this.degreeTitles = degreeTitles;
	}
    public Map<String, List<String>> getSubjects() {
        return subjects;
    }
    public void setSubjects(Map<String, List<String>> subjects) {
        this.subjects = subjects;
    }

    public String getHinta() {
        return hinta;
    }

    public void setHinta(String hinta) {
        this.hinta = hinta;
    }
}
