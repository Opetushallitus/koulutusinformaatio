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
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLOI extends LOI {

    private String id;
    private I18nText name;
    private List<I18nText> formOfTeaching;
    private List<Code> teachingLanguages;
    private List<I18nText> formOfEducation;
    private Date startDate;
    private I18nText internationalization;
    private I18nText cooperation;
    private I18nText content;
    private List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
    private List<I18nText> diplomas = new ArrayList<I18nText>();
    private String plannedDuration;
    private I18nText plannedDurationUnit;
    private List<LanguageSelection> languageSelection;
    private boolean kaksoistutkinto;
    private List<String> availableTranslationLanguages;

    //planned duration unit code uri (used in indexing for solr)
    private String pduCodeUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nText> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<I18nText> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nText> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public I18nText getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(I18nText internationalization) {
        this.internationalization = internationalization;
    }

    public I18nText getCooperation() {
        return cooperation;
    }

    public void setCooperation(I18nText cooperation) {
        this.cooperation = cooperation;
    }

    public I18nText getContent() {
        return content;
    }

    public void setContent(I18nText content) {
        this.content = content;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<I18nText> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<I18nText> diplomas) {
        this.diplomas = diplomas;
    }

    public String getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(String plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public I18nText getPlannedDurationUnit() {
        return plannedDurationUnit;
    }

    public void setPlannedDurationUnit(I18nText plannedDurationUnit) {
        this.plannedDurationUnit = plannedDurationUnit;
    }

    public List<LanguageSelection> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelection> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }

    public String getPduCodeUri() {
        return pduCodeUri;
    }

    public void setPduCodeUri(String pduCodeUri) {
        this.pduCodeUri = pduCodeUri;
    }

    public List<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
}