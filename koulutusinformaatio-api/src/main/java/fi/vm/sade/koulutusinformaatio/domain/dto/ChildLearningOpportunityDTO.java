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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunityDTO {

    private String id;
    private String name;
    private String qualification;
    private String degreeTitle;
    private List<ApplicationOptionDTO> applicationOptions;
    private List<ChildLORefDTO> related = new ArrayList<ChildLORefDTO>();

    private Date startDate;
    private List<String> formOfEducation = new ArrayList<String>();
    private Map<String, String> webLinks;
    private List<String> formOfTeaching = new ArrayList<String>();
    private CodeDTO prerequisite;

    private String translationLanguage;
    private Set<String> availableTranslationLanguages = new HashSet<String>();
    private Set<String> teachingLanguages = new HashSet<String>();
    private List<String> professionalTitles;
    private String workingLifePlacement;
    private String internationalization;
    private String cooperation;
    private String content;
    private String degreeGoal;

    private ParentLOSRefDTO parent;

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

    public List<ApplicationOptionDTO> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionDTO> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
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

    public List<String> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<String> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getTranslationLanguage() {
        return translationLanguage;

    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public Set<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(Set<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<ChildLORefDTO> getRelated() {
        return related;
    }

    public Set<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(Set<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public void setRelated(List<ChildLORefDTO> related) {
        this.related = related;

    }

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }

    public List<String> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<String> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public String getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(String workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
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

    public String getDegreeGoal() {
        return degreeGoal;
    }

    public void setDegreeGoal(String degreeGoal) {
        this.degreeGoal = degreeGoal;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
