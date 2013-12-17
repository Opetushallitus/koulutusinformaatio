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
 * Parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOS extends LOS {

    private String id;
    private I18nText name;
    private List<ParentLOI> lois;
    private List<ChildLOS> children;
    private Provider provider;
    // rakenne
    private I18nText structure;
    // jatko-opintomahdollisuudet
    private I18nText accessToFurtherStudies;
    // tavoitteet
    private I18nText goals;
    //koulutusala, Sosiaali-, terveys- ja liikunta-ala
    private I18nText educationDomain;
    //opintoala, Hammaslääketiede ja muu hammashuolto
    private I18nText stydyDomain;
    // koulutusaste, 32
    private String educationDegree;
    //laajuus arvo, 120
    private String creditValue;
    //laajuus yksikkö opintoviikko
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

    public List<ParentLOI> getLois() {
        return lois;
    }

    public void setLois(List<ParentLOI> lois) {
        this.lois = lois;
    }

    public List<ChildLOS> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLOS> children) {
        this.children = children;
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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
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
