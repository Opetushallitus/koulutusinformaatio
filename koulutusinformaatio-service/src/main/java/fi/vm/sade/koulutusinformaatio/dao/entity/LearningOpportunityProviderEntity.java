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

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
@Entity("learningOpportunityProviders")
public class LearningOpportunityProviderEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    private Set<String> applicationSystemIds;
    @Embedded
    private AddressEntity postalAddress;
    @Embedded
    private AddressEntity visitingAddress;
    private I18nTextEntity webPage;
    private I18nTextEntity email;
    private I18nTextEntity fax;
    private I18nTextEntity phone;
    private I18nTextEntity description;
    private I18nTextEntity healthcare;
    private I18nTextEntity accessibility;
    private I18nTextEntity learningEnvironment;
    private I18nTextEntity dining;
    private I18nTextEntity livingExpenses;
    private I18nTextEntity living;
    
    private I18nTextEntity yearClock;
    private I18nTextEntity financingStudies;
    private I18nTextEntity insurances;
    private I18nTextEntity leisureServices;
    
    @Embedded
    private List<SocialEntity> social;
    @Reference
    private PictureEntity picture;
    private boolean athleteEducation;
    private String placeOfBusinessCode;
    @Embedded
    private ApplicationOfficeEntity applicationOffice;

    public LearningOpportunityProviderEntity() {
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

    public Set<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(Set<String> applicationSystemIds) {
        this.applicationSystemIds = applicationSystemIds;
    }

    public AddressEntity getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(AddressEntity postalAddress) {
        this.postalAddress = postalAddress;
    }

    public AddressEntity getVisitingAddress() {
        return visitingAddress;
    }

    public void setVisitingAddress(AddressEntity visitingAddress) {
        this.visitingAddress = visitingAddress;
    }

    public I18nTextEntity getWebPage() {
        return webPage;
    }

    public void setWebPage(I18nTextEntity webPage) {
        this.webPage = webPage;
    }

    public I18nTextEntity getEmail() {
        return email;
    }

    public void setEmail(I18nTextEntity email) {
        this.email = email;
    }

    public I18nTextEntity getFax() {
        return fax;
    }

    public void setFax(I18nTextEntity fax) {
        this.fax = fax;
    }

    public I18nTextEntity getPhone() {
        return phone;
    }

    public void setPhone(I18nTextEntity phone) {
        this.phone = phone;
    }

    public I18nTextEntity getDescription() {
        return description;
    }

    public void setDescription(I18nTextEntity description) {
        this.description = description;
    }

    public I18nTextEntity getHealthcare() {
        return healthcare;
    }

    public void setHealthcare(I18nTextEntity healthcare) {
        this.healthcare = healthcare;
    }

    public I18nTextEntity getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(I18nTextEntity accessibility) {
        this.accessibility = accessibility;
    }

    public I18nTextEntity getLearningEnvironment() {
        return learningEnvironment;
    }

    public void setLearningEnvironment(I18nTextEntity learningEnvironment) {
        this.learningEnvironment = learningEnvironment;
    }

    public I18nTextEntity getDining() {
        return dining;
    }

    public void setDining(I18nTextEntity dining) {
        this.dining = dining;
    }

    public I18nTextEntity getLivingExpenses() {
        return livingExpenses;
    }

    public void setLivingExpenses(I18nTextEntity livingExpenses) {
        this.livingExpenses = livingExpenses;
    }

    public List<SocialEntity> getSocial() {
        return social;
    }

    public void setSocial(List<SocialEntity> social) {
        this.social = social;
    }

    public PictureEntity getPicture() {
        return picture;
    }

    public void setPicture(PictureEntity picture) {
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

    public ApplicationOfficeEntity getApplicationOffice() {
        return applicationOffice;
    }

    public void setApplicationOffice(ApplicationOfficeEntity applicationOffice) {
        this.applicationOffice = applicationOffice;
    }

    public I18nTextEntity getLiving() {
        return living;
    }

    public void setLiving(I18nTextEntity living) {
        this.living = living;
    }

    public I18nTextEntity getYearClock() {
        return yearClock;
    }

    public void setYearClock(I18nTextEntity yearClock) {
        this.yearClock = yearClock;
    }

    public I18nTextEntity getFinancingStudies() {
        return financingStudies;
    }

    public void setFinancingStudies(I18nTextEntity financingStudies) {
        this.financingStudies = financingStudies;
    }

    public I18nTextEntity getInsurances() {
        return insurances;
    }

    public void setInsurances(I18nTextEntity insurances) {
        this.insurances = insurances;
    }

    public I18nTextEntity getLeisureServices() {
        return leisureServices;
    }

    public void setLeisureServices(I18nTextEntity leisureServices) {
        this.leisureServices = leisureServices;
    }
}
