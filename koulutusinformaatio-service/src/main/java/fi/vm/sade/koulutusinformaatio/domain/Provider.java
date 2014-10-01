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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Learning opportunity provider ie. organization that provides certain education.
 *
 * @author Hannu Lyytikainen
 */
public class Provider {

    private String id;
    private I18nText name;
    private Set<String> applicationSystemIds = new HashSet<String>();
    private Address postalAddress;
    private Address visitingAddress;
    private I18nText webPage;
    private I18nText email;
    private I18nText fax;
    private I18nText phone;
    private I18nText description;
    private I18nText healthcare;
    private I18nText accessibility;
    private I18nText learningEnvironment;
    private I18nText dining;
    private I18nText livingExpenses;
    private I18nText living;
    private I18nText yearClock;
    private I18nText financingStudies;
    private I18nText insurances;
    private I18nText leisureServices;
    private List<Social> social;
    private Picture picture;
    private boolean athleteEducation;
    private String placeOfBusinessCode;
    private I18nText homePlace;
    private I18nText homeDistrict;
    private ApplicationOffice applicationOffice;
    private Code type;

    public Provider(String id, I18nText name) {
        this.id = id;
        this.name = name;
    }

    public Provider() {}

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

    public Set<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(Set<String> applicationSystemIDs) {
        this.applicationSystemIds = applicationSystemIDs;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public Address getVisitingAddress() {
        return visitingAddress;
    }

    public void setVisitingAddress(Address visitingAddress) {
        this.visitingAddress = visitingAddress;
    }

    public I18nText getWebPage() {
        return webPage;
    }

    public void setWebPage(I18nText webPage) {
        this.webPage = webPage;
    }

    public I18nText getEmail() {
        return email;
    }

    public void setEmail(I18nText email) {
        this.email = email;
    }

    public I18nText getFax() {
        return fax;
    }

    public void setFax(I18nText fax) {
        this.fax = fax;
    }

    public I18nText getPhone() {
        return phone;
    }

    public void setPhone(I18nText phone) {
        this.phone = phone;
    }

    public I18nText getDescription() {
        return description;
    }

    public void setDescription(I18nText description) {
        this.description = description;
    }

    public I18nText getHealthcare() {
        return healthcare;
    }

    public void setHealthcare(I18nText healthcare) {
        this.healthcare = healthcare;
    }

    public I18nText getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(I18nText accessibility) {
        this.accessibility = accessibility;
    }

    public I18nText getLearningEnvironment() {
        return learningEnvironment;
    }

    public void setLearningEnvironment(I18nText learningEnvironment) {
        this.learningEnvironment = learningEnvironment;
    }

    public I18nText getDining() {
        return dining;
    }

    public void setDining(I18nText dining) {
        this.dining = dining;
    }

    public I18nText getLivingExpenses() {
        return livingExpenses;
    }

    public void setLivingExpenses(I18nText livingExpenses) {
        this.livingExpenses = livingExpenses;
    }

    public List<Social> getSocial() {
        return social;
    }

    public void setSocial(List<Social> social) {
        this.social = social;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public boolean isAthleteEducation() {
        return athleteEducation;
    }

    public void setAthleteEducation(boolean athleteEducation) {
        this.athleteEducation = athleteEducation;
    }

    public String getPlaceOfBusinessCode() {
        return placeOfBusinessCode;
    }

    public void setPlaceOfBusinessCode(String placeOfBusinessCode) {
        this.placeOfBusinessCode = placeOfBusinessCode;
    }

    public I18nText getHomePlace() {
        return homePlace;
    }

    public void setHomePlace(I18nText homePlace) {
        this.homePlace = homePlace;
    }

    public ApplicationOffice getApplicationOffice() {
        return applicationOffice;
    }

    public void setApplicationOffice(ApplicationOffice applicationOffice) {
        this.applicationOffice = applicationOffice;
    }

    public I18nText getHomeDistrict() {
        return homeDistrict;
    }

    public void setHomeDistrict(I18nText homeDistrict) {
        this.homeDistrict = homeDistrict;
    }

    public I18nText getLiving() {
        return living;
    }

    public void setLiving(I18nText living) {
        this.living = living;
    }

    public I18nText getYearClock() {
        return yearClock;
    }

    public void setYearClock(I18nText yearClock) {
        this.yearClock = yearClock;
    }

    public I18nText getFinancingStudies() {
        return financingStudies;
    }

    public void setFinancingStudies(I18nText financingStudies) {
        this.financingStudies = financingStudies;
    }

    public I18nText getInsurances() {
        return insurances;
    }

    public void setInsurances(I18nText insurances) {
        this.insurances = insurances;
    }

    public I18nText getLeisureServices() {
        return leisureServices;
    }

    public void setLeisureServices(I18nText leisureServices) {
        this.leisureServices = leisureServices;
    }

    public Code getType() {
        return type;
    }

    public void setType(Code type) {
        this.type = type;
    }
}
