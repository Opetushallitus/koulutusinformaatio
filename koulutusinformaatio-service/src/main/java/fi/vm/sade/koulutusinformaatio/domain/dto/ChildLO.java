/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

import fi.vm.sade.koulutusinformaatio.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class ChildLO {

    private String losId;
    private String loiId;
    private I18nText name;
    private I18nText qualification;
    private I18nText degreeTitle;
    private ApplicationOption applicationOption;
    private List<ChildLORef> related = new ArrayList<ChildLORef>();
    private ParentLORef parent;
    private List<Code> teachingLanguages = new ArrayList<Code>();
    private List<I18nText> formOfEducation;
    private Map<String, String> webLinks;
    private List<I18nText> formOfTeaching;
    private I18nText prerequisite;

    private Date startDate;


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

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public ApplicationOption getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOption applicationOption) {
        this.applicationOption = applicationOption;
    }

    public List<ChildLORef> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLORef> related) {
        this.related = related;
    }

    public ParentLORef getParent() {
        return parent;
    }

    public void setParent(ParentLORef parent) {
        this.parent = parent;
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

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nText> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public I18nText getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nText prerequisite) {
        this.prerequisite = prerequisite;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
