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

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunityDTO {

    private String losId;
    private String loiId;
    private String name;
    private String qualification;
    private String degreeTitle;
    private ApplicationOptionDTO applicationOption;
    private List<ChildLORefDTO> related = new ArrayList<ChildLORefDTO>();

    private Date startDate;
    private List<String> formOfEducation = new ArrayList<String>();
    private Map<String, String> webLinks;
    private List<String> formOfTeaching = new ArrayList<String>();
    private String prerequisite;

    private String translationLanguage;
    private Set<String> availableTranslationLanguages = new HashSet<String>();
    private Set<String> teachingLanguages = new HashSet<String>();

    private ParentLOSRefDTO parent;

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public String getLoiId() {
        return loiId;
    }

    public void setLoiId(String loiId) {
        this.loiId = loiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationOptionDTO getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOptionDTO applicationOption) {
        this.applicationOption = applicationOption;
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

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
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
}
