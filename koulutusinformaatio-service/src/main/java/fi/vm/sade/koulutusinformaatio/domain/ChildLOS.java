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
 * Child level learning opportunity.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOS {

    private String id;
    private I18nText name;
    // koulutusohjelma
    private I18nText degreeTitle;
    //tutkintonimike
    private I18nText qualification;
    //tavoite
    private I18nText goals;
    private ParentLOSRef parent;
    private List<ChildLOI> lois;

    public ChildLOS(String id, I18nText name) {
        this.id = id;
        this.name = name;
    }

    public ChildLOS() {

    }

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

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public ParentLOSRef getParent() {
        return parent;
    }

    public void setParent(ParentLOSRef parent) {
        this.parent = parent;
    }

    public List<ChildLOI> getLois() {
        return lois;
    }

    public void setLois(List<ChildLOI> lois) {
        this.lois = lois;
    }
}
