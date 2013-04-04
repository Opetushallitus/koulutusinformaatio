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
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class ParentLearningOpportunity {

    private String id;
    private String name;
    private List<ChildLearningOpportunity> children = new ArrayList<ChildLearningOpportunity>();
    private List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
    private LearningOpportunityProvider provider;
    private String educationDegree;

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

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

    public List<ChildLearningOpportunity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunity> children) {
        this.children = children;
    }

    public LearningOpportunityProvider getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProvider provider) {
        this.provider = provider;
    }
}
