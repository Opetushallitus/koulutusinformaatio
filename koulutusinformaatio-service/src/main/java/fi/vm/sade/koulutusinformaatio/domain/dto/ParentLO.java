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

import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;
import fi.vm.sade.koulutusinformaatio.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
public class ParentLO {

    private String id;
    private I18nText name;
    private Set<ApplicationOption> applicationOptions = new HashSet<ApplicationOption>();
    private Provider provider;
    private String educationDegree;
    private I18nText structureDiagram;
    private I18nText accessToFurtherStudies;
    private I18nText degreeProgramSelection;
    private I18nText goals;
    private I18nText educationDomain;
    private I18nText stydyDomain;
    private String creditValue;
    private I18nText creditUnit;
    private List<ParentLOI> lois = new ArrayList<ParentLOI>();

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

    public Set<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public I18nText getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(I18nText structureDiagram) {
        this.structureDiagram = structureDiagram;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nText getDegreeProgramSelection() {
        return degreeProgramSelection;
    }

    public void setDegreeProgramSelection(I18nText degreeProgramSelection) {
        this.degreeProgramSelection = degreeProgramSelection;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getStydyDomain() {
        return stydyDomain;
    }

    public void setStydyDomain(I18nText stydyDomain) {
        this.stydyDomain = stydyDomain;
    }

    public List<ParentLOI> getLois() {
        return lois;
    }

    public void setLois(List<ParentLOI> lois) {
        this.lois = lois;
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
