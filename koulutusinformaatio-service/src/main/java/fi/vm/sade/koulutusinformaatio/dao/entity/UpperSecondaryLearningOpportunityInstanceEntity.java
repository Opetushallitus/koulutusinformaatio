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

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Embedded
public class UpperSecondaryLearningOpportunityInstanceEntity {

    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity shortName;
    @Embedded
    private CodeEntity prerequisite;
    @Embedded
    private List<I18nTextEntity> formOfTeaching;
    @Embedded
    private List<CodeEntity> teachingLanguages;
    @Embedded
    private List<I18nTextEntity> formOfEducation;
    private Date startDate;
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    @Embedded
    private I18nTextEntity internationalization;
    @Embedded
    private I18nTextEntity cooperation;
    @Embedded
    private I18nTextEntity content;
    @Embedded
    private List<ContactPersonEntity> contactPersons = new ArrayList();
    @Embedded
    private List<I18nTextEntity> diplomas = new ArrayList();
    private String plannedDuration;
    @Embedded
    private I18nTextEntity plannedDurationUnit;
    @Embedded
    private List<LanguageSelectionEntity> languageSelection;
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;
    @Embedded
    private List<CodeEntity> fotFacet = new ArrayList<CodeEntity>();
    @Embedded
    private List<CodeEntity> timeOfTeachingFacet = new ArrayList<CodeEntity>();

    public UpperSecondaryLearningOpportunityInstanceEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public I18nTextEntity getShortName() {
        return shortName;
    }

    public void setShortName(I18nTextEntity shortName) {
        this.shortName = shortName;
    }

    public CodeEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nTextEntity> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nTextEntity> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<CodeEntity> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<I18nTextEntity> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nTextEntity> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<ApplicationOptionEntity> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionEntity> applicationOptions) {
        this.applicationOptions = applicationOptions;
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

    public I18nTextEntity getContent() {
        return content;
    }

    public void setContent(I18nTextEntity content) {
        this.content = content;
    }

    public List<ContactPersonEntity> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonEntity> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<I18nTextEntity> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<I18nTextEntity> diplomas) {
        this.diplomas = diplomas;
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

    public List<LanguageSelectionEntity> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelectionEntity> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
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
}
