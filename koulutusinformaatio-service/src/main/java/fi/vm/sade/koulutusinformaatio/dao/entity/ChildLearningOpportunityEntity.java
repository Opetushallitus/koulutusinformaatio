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

package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Entity("childLearningOpportunities")
public class ChildLearningOpportunityEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity degreeTitle;
    @Embedded
    private I18nTextEntity degreeGoal;
    @Embedded
    private I18nTextEntity qualification;
    @Embedded
    private ParentLOSRefEntity parent;
    @Embedded
    private List<ChildLearningOpportunityInstanceEntity> lois;

    public ChildLearningOpportunityEntity() {}

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

    public I18nTextEntity getQualification() {
        return qualification;
    }

    public void setQualification(I18nTextEntity qualification) {
        this.qualification = qualification;
    }

    public I18nTextEntity getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nTextEntity degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nTextEntity getDegreeGoal() {
        return degreeGoal;
    }

    public void setDegreeGoal(I18nTextEntity degreeGoal) {
        this.degreeGoal = degreeGoal;
    }

    public ParentLOSRefEntity getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefEntity parent) {
        this.parent = parent;
    }

    public List<ChildLearningOpportunityInstanceEntity> getLois() {
        return lois;
    }

    public void setLois(List<ChildLearningOpportunityInstanceEntity> lois) {
        this.lois = lois;
    }
}
