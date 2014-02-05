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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class UpperSecondaryLearningOpportunityInstanceDTO {

    private String id;
    private String name;
    private CodeDTO prerequisite;
    private List<String> formOfTeaching;
    private List<String> teachingLanguages;
    private List<String> formOfEducation;
    private Date startDate;
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
    private String internationalization;
    private String cooperation;
    private String content;
    private List<ContactPersonDTO> contactPersons = new ArrayList<ContactPersonDTO>();
    private List<String> diplomas = new ArrayList<String>();
    private String plannedDuration;
    private String plannedDurationUnit;
    private List<LanguageSelectionDTO> languageSelection;
    private List<String> availableTranslationLanguages;

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

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<String> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<String> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<ApplicationSystemDTO> getApplicationSystems() {
        return applicationSystems;
    }

    public void setApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {
        this.applicationSystems = applicationSystems;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ContactPersonDTO> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonDTO> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<String> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<String> diplomas) {
        this.diplomas = diplomas;
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

    public List<LanguageSelectionDTO> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelectionDTO> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
}
