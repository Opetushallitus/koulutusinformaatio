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

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLOS extends LOS {

    private String id;
    private I18nText name;
    private String educationDegree;
    private I18nText degreeTitle;
    private I18nText qualification;
    private I18nText goals;
    private List<UpperSecondaryLOI> lois;
    private Provider provider;
    private I18nText structure;
    private I18nText accessToFurtherStudies;
    private String creditValue;
    private I18nText creditUnit;
    

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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public List<UpperSecondaryLOI> getLois() {
        return lois;
    }

    public void setLois(List<UpperSecondaryLOI> lois) {
        this.lois = lois;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public I18nText getStructure() {
        return structure;
    }

    public void setStructure(I18nText structure) {
        this.structure = structure;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }
}