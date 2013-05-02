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
import com.google.code.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Entity("applicationOptions")
public class ApplicationOptionEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    private String applicationSystemId;
    private String educationDegree;
    @Reference
    private LearningOpportunityProviderEntity provider;
    private List<I18nTextEntity> childLONames = new ArrayList<I18nTextEntity>();

    public ApplicationOptionEntity() {

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

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public LearningOpportunityProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderEntity provider) {
        this.provider = provider;
    }

    public List<I18nTextEntity> getChildLONames() {
        return childLONames;
    }

    public void setChildLONames(List<I18nTextEntity> childLONames) {
        this.childLONames = childLONames;
    }
}
