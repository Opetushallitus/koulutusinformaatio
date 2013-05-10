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
    private String educationDegree;

    private Description description;
    private Classification classification;
    private Credits credits;


    private I18nText koulutusOhjelma;
    private I18nText tutkintonimike;
    public I18nText getKoulutusOhjelma() {
        return koulutusOhjelma;
    }
    public void setKoulutusOhjelma(I18nText koulutusOhjelma) {
        this.koulutusOhjelma = koulutusOhjelma;
    }
    public I18nText getTutkintonimike() {
        return tutkintonimike;
    }
    public void setTutkintonimike(I18nText tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }








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

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

}
