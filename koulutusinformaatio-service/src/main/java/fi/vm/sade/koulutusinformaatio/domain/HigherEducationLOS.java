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
 * 
 * @author Markus
 */
public class HigherEducationLOS extends LOS {

    //Varmistetut
    private String id;
    private I18nText infoAboutTeachingLangs;
    private I18nText content;
    private I18nText goals;
    private I18nText majorSelection;
    private I18nText structure;
    private I18nText finalExam;
    private I18nText careerOpportunities;
    private I18nText internationalization;
    private I18nText cooperation;
    private I18nText competence;
    private I18nText researchFocus;
    private I18nText accessToFurtherStudies;
    private List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
    private I18nText educationDomain;
    private I18nText name;
    private I18nText koulutuskoodi;
    private String educationDegree;
    private I18nText educationDegreeLang;
    private I18nText degreeTitle;
    private Date startDate;
    private String plannedDuration;
    private I18nText plannedDurationUnit;
    private String pduCodeUri;
    private String creditValue;
    private I18nText creditUnit;
    private I18nText degree;
    private List<I18nText> qualifications;
    private Boolean chargeable;
    private Code educationCode;
    private List<Code> teachingLanguages;

    private Provider provider;
    private List<ApplicationOption> applicationOptions;

    private String komoOid;

    private List<HigherEducationLOS> children = new ArrayList<HigherEducationLOS>();
    private List<HigherEducationLOS> parents = new ArrayList<HigherEducationLOS>();

    private List<Code> prerequisites;
    private List<I18nText> formOfTeaching;
    private List<I18nText> professionalTitles;
    private List<I18nText> teachingTimes;
    private List<I18nText> teachingPlaces;
    private I18nText infoAboutCharge;
    private I18nText startSeason;
    private int startYear;

    //Status of the lo. For preview
    private String status;
    private List<Code> availableTranslationLanguages;
    

    private List<Code> facetPrerequisites;
    private String educationType;
    
    private I18nPicture structureImage;
    
    private List<Code> fotFacet = new ArrayList<Code>();
    
    private List<Code> timeOfTeachingFacet = new ArrayList<Code>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getName() {
        return this.name;
    }

    public void setEducationDegree(String degree) {
        this.educationDegree = degree;
    }

    public String getEducationDegree() {
        return this.educationDegree;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setQualifications(List<I18nText> qualifications) {
        this.qualifications = qualifications;
    }

    public List<I18nText> getQualifications() {
        return qualifications;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void setStructure(I18nText structure) {
        this.structure = structure;
    }

    public I18nText getStructure() {
        return structure;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setInfoAboutTeachingLangs(I18nText i18nText) {
        this.infoAboutTeachingLangs = i18nText;
    }

    public I18nText getInfoAboutTeachingLangs() {
        return infoAboutTeachingLangs;
    }

    public void setContent(I18nText i18nTextEnriched) {
        this.content = i18nTextEnriched;
    }

    public I18nText getContent() {
        return content;
    }

    public void setMajorSelection(I18nText i18nTextEnriched) {
        this.majorSelection = i18nTextEnriched;	
    }

    public I18nText getMajorSelection() {
        return majorSelection;
    }

    public void setFinalExam(I18nText i18nTextEnriched) {
        this.finalExam = i18nTextEnriched;
    }

    public I18nText getFinalExam() {
        return finalExam;
    }

    public void setCareerOpportunities(I18nText i18nTextEnriched) {
        this.careerOpportunities = i18nTextEnriched;	
    }

    public I18nText getCareerOpportunities() {
        return careerOpportunities;
    }

    public void setInternationalization(I18nText i18nTextEnriched) {
        this.internationalization = i18nTextEnriched;
    }

    public I18nText getInternationalization() {
        return internationalization;
    }

    public void setCooperation(I18nText i18nTextEnriched) {
        this.cooperation  = i18nTextEnriched;
    }

    public I18nText getCooperation() {
        return cooperation;
    }

    public void setCompetence(I18nText i18nTextEnriched) {
        this.competence = i18nTextEnriched;
    }

    public I18nText getCompetence() {
        return competence;
    }

    public void setResearchFocus(I18nText i18nTextEnriched) {
        this.researchFocus = i18nTextEnriched;
    }

    public I18nText getResearchFocus() {
        return researchFocus;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public I18nText getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public void setKoulutuskoodi(I18nText koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }	

    public Date getStartDate() {
        return startDate;
    }

    public void setPlannedDuration(String suunniteltuKestoArvo) {
        this.plannedDuration = suunniteltuKestoArvo;
    }

    public String getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDurationUnit(I18nText i18nTextEnriched) {
        this.plannedDurationUnit = i18nTextEnriched;
    }

    public I18nText getPlannedDurationUnit() {
        return plannedDurationUnit;
    }

    public void setPduCodeUri(String uri) {
        this.pduCodeUri = uri;
    }

    public String getPduCodeUri() {
        return pduCodeUri;
    }

    public void setDegree(I18nText i18nText) {
        this.degree = i18nText;
    }

    public I18nText getDegree() {
        return degree;
    }

    public void setChargeable(Boolean opintojenMaksullisuus) {
        this.chargeable = opintojenMaksullisuus;	
    }

    public Boolean getChargeable() {
        return chargeable;
    }

    public void setEducationCode(Code edCode) {
        educationCode = edCode;
    }

    public Code getEducationCode() {
        return educationCode;
    }

    public void setTeachingLanguages(List<Code> createCodes) {
        this.teachingLanguages = createCodes;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }    

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    public List<HigherEducationLOS> getChildren() {
        return children;
    }

    public void setChildren(List<HigherEducationLOS> children) {
        this.children = children;
    }

    public List<Code> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Code> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setFormOfTeaching(List<I18nText> opetusmuodos) {
        this.formOfTeaching = opetusmuodos;	
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setProfessionalTitles(List<I18nText> ammattinimikkees) {
        this.professionalTitles = ammattinimikkees;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }

    public I18nText getEducationDegreeLang() {
        return educationDegreeLang;
    }

    public void setEducationDegreeLang(I18nText educationDegreeLang) {
        this.educationDegreeLang = educationDegreeLang;
    }
    public void setTeachingTimes(List<I18nText> teachingTimes) {
        this.teachingTimes = teachingTimes;

    }
    public List<I18nText> getTeachingTimes() {
        return teachingTimes;
    }
    public void setTeachingPlaces(List<I18nText> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;

    }
    public List<I18nText> getTeachingPlaces() {
        return teachingPlaces;
    }
    public void setInfoAboutCharge(I18nText i18nTextEnriched) {
        this.infoAboutCharge = i18nTextEnriched;

    }
    public I18nText getInfoAboutCharge() {
        return infoAboutCharge;
    }

    public List<HigherEducationLOS> getParents() {
        return parents;
    }

    public void setParents(List<HigherEducationLOS> parents) {
        this.parents = parents;
    }

    public I18nText getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(I18nText startSeason) {
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

    public List<Code> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<Code> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
    
    public List<Code> getFacetPrerequisites() {
        return facetPrerequisites;
    }

    public void setFacetPrerequisites(List<Code> facetPrerequisites) {
        this.facetPrerequisites = facetPrerequisites;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;   
    }
    public String getEducationType() {
        return educationType;   
    }

    public I18nPicture getStructureImage() {
        return structureImage;
    }

    public void setStructureImage(I18nPicture structureImage) {
        this.structureImage = structureImage;
    }

    public List<Code> getFotFacet() {
        return fotFacet;
    }

    public void setFotFacet(List<Code> formOfTeachingFacet) {
        this.fotFacet = formOfTeachingFacet;
    }

    public List<Code> getTimeOfTeachingFacet() {
        return timeOfTeachingFacet;
    }

    public void setTimeOfTeachingFacet(List<Code> timeOfTeachingFacet) {
        this.timeOfTeachingFacet = timeOfTeachingFacet;
    }
    
}
