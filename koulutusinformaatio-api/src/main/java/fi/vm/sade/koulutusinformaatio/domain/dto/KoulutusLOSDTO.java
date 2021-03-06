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
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class KoulutusLOSDTO extends StandaloneLOSDTO implements Articled {
    private String id;
    private String content;
    private String goals;
    private String structure;
    private String internationalization;
    private String cooperation;
    private String accessToFurtherStudies;
    private List<ContactPersonDTO> contactPersons = new ArrayList<ContactPersonDTO>();
    private String educationDomain;
    private String name;
    private String koulutuskoodi;
    private String educationDegree;
    private String educationDegreeName;
    private String degreeTitle;
    private Date startDate;
    private Date endDate;
    private List<Date> startDates;
    private String plannedDuration;
    private String plannedDurationUnit;
    private String pduCodeUri;
    private String creditValue;
    private String creditUnit;
    private String creditUnitShort;
    private String degree;
    private String educationCode;
    private List<String> teachingLanguages;
    private LearningOpportunityProviderDTO provider;
    private List<LearningOpportunityProviderDTO> additionalProviders = new ArrayList<LearningOpportunityProviderDTO>();
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
    private List<CodeDTO> prerequisites;
    private String translationLanguage;
    private List<CodeDTO> availableTranslationLanguages;
    private List<String> formOfTeaching;
    private List<String> teachingTimes;
    private List<String> teachingPlaces;
    private List<String> qualifications;
    private List<String> degreeTitles;
    private String startSeason;
    private int startYear;
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;
    private String educationType;
    private String targetGroup;
    private String subjectsAndCourses;
    private List<LanguageSelectionDTO> languageSelection;
    private List<String> diplomas = new ArrayList<String>();
    private List<ArticleResultDTO> edCodeSuggestions;
    private List<ArticleResultDTO> edTypeSuggestions;
    private String workingLifePlacement;
    private String linkToCurriculum;
    private Set<ChildLOIRefDTO> opintojaksos;
    private Set<ChildLOIRefDTO> siblings;
    private Set<ParentLOSRefDTO> parentLos = Sets.newHashSet();
    private CodeDTO koulutusPrerequisite;
    private List<String> subjects;
    private String opettaja;

    private String opinnonTyyppiUri;

    private String vastaavaKorkeakoulu;
    private String maksullisuus;
    private String edeltavatOpinnot;
    private String arviointi;
    private String opetuksenAikaJaPaikka;
    private String lisatietoja;
    private String competence;
    private String charge;
    private ToteutustyyppiEnum toteutustyyppi;
    private String hakijalleNaytettavaTunniste;

    // Status of the lo. For preview
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
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

    public String getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;
    }

    public String getCreditUnitShort() {
        return creditUnitShort;
    }

    public void setCreditUnitShort(String creditUnitShort) {
        this.creditUnitShort = creditUnitShort;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
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

    public List<CodeDTO> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<CodeDTO> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public List<CodeDTO> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeDTO> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
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

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
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

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
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

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getSubjectsAndCourses() {
        return subjectsAndCourses;
    }

    public void setSubjectsAndCourses(String subjectsAndCourses) {
        this.subjectsAndCourses = subjectsAndCourses;
    }

    public List<LanguageSelectionDTO> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelectionDTO> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<String> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<String> diplomas) {
        this.diplomas = diplomas;
    }

    public String getEducationDegreeName() {
        return educationDegreeName;
    }

    public void setEducationDegreeName(String educationDegreeName) {
        this.educationDegreeName = educationDegreeName;
    }

    public List<LearningOpportunityProviderDTO> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<LearningOpportunityProviderDTO> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public void setEdCodeSuggestions(List<ArticleResultDTO> edCodeSuggestions) {
        this.edCodeSuggestions = edCodeSuggestions;
    }

    public List<ArticleResultDTO> getEdCodeSuggestions() {
        return edCodeSuggestions;
    }

    public void setEdTypeSuggestions(List<ArticleResultDTO> edTypeSuggestions) {
        this.edTypeSuggestions = edTypeSuggestions;

    }

    public List<ArticleResultDTO> getEdTypeSuggestions() {
        return edTypeSuggestions;
    }

    public List<String> getDegreeTitles() {
        return degreeTitles;
    }

    public void setDegreeTitles(List<String> degreeTitles) {
        this.degreeTitles = degreeTitles;
    }

    public List<Date> getStartDates() {
        return startDates;
    }

    public void setStartDates(List<Date> startDates) {
        this.startDates = startDates;
    }

    public String getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(String workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public String getLinkToCurriculum() {
        return linkToCurriculum;
    }

    public void setLinkToCurriculum(String linkToCurriculum) {
        this.linkToCurriculum = linkToCurriculum;
    }

    public Set<ChildLOIRefDTO> getOpintojaksos() {
        return opintojaksos;
    }

    public void setOpintojaksos(Set<ChildLOIRefDTO> opintojaksos) {
        this.opintojaksos = opintojaksos;
    }

    public Set<ChildLOIRefDTO> getSiblings() {
        return siblings;
    }

    public void setSiblings(Set<ChildLOIRefDTO> siblings) {
        this.siblings = siblings;
    }

    public Set<ParentLOSRefDTO> getParentLos() {
        return parentLos;
    }

    public void appendParentLos(ParentLOSRefDTO parentLos) {
        this.parentLos.add(parentLos);
    }

    public CodeDTO getKoulutusPrerequisite() {
        return koulutusPrerequisite;
    }

    public void setKoulutusPrerequisite(CodeDTO koulutusPrerequisite) {
        this.koulutusPrerequisite = koulutusPrerequisite;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getOpettaja() {
        return opettaja;
    }

    public void setOpettaja(String opettaja) {
        this.opettaja = opettaja;
    }

    public String getOpinnonTyyppiUri() {
        return opinnonTyyppiUri;
    }

    public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
        this.opinnonTyyppiUri = opinnonTyyppiUri;
    }

    public String getMaksullisuus() {
        return maksullisuus;
    }

    public void setMaksullisuus(String maksullisuus) {
        this.maksullisuus = maksullisuus;
    }

    public String getEdeltavatOpinnot() {
        return edeltavatOpinnot;
    }

    public void setEdeltavatOpinnot(String edeltavatOpinnot) {
        this.edeltavatOpinnot = edeltavatOpinnot;
    }

    public String getArviointi() {
        return arviointi;
    }

    public void setArviointi(String arviointi) {
        this.arviointi = arviointi;
    }

    public String getOpetuksenAikaJaPaikka() {
        return opetuksenAikaJaPaikka;
    }

    public void setOpetuksenAikaJaPaikka(String opetuksenAikaJaPaikka) {
        this.opetuksenAikaJaPaikka = opetuksenAikaJaPaikka;
    }

    public String getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(String lisatietoja) {
        this.lisatietoja = lisatietoja;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getVastaavaKorkeakoulu() {
        return vastaavaKorkeakoulu;
    }

    public void setVastaavaKorkeakoulu(String vastaavaKorkeakoulu) {
        this.vastaavaKorkeakoulu = vastaavaKorkeakoulu;
    }

    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
    }

    public String getHakijalleNaytettavaTunniste() {
        return hakijalleNaytettavaTunniste;
    }

    public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
        this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
    }
}
