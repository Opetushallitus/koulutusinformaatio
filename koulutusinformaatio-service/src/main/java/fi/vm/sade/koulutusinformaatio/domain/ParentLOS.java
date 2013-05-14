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
 * Parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOS {

    private String id;
    private I18nText name;
    private List<ChildLOS> children = new ArrayList<ChildLOS>();
    private List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
    private LearningOpportunityProvider provider;
    // rakenne
    private I18nText structureDiagram;
    // jatko-opintomahdollisuudet
    private I18nText accessToFurtherStudies;
    // tavoitteet
    private I18nText goals;
    //koulutusala, Sosiaali-, terveys- ja liikunta-ala
    private I18nText educationDomain;
    //opintoala, Hammaslääketiede ja muu hammashuolto
    private I18nText stydyDomain;
    // koulutusaste, Ammatillinen koulutus
    private I18nText educationDegree;

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

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public List<ChildLOS> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLOS> children) {
        this.children = children;
    }

    public LearningOpportunityProvider getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProvider provider) {
        this.provider = provider;
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

    public I18nText getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(I18nText educationDegree) {
        this.educationDegree = educationDegree;
    }
}
