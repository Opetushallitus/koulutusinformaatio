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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class LearningOpportunityProviderDTO {

    private String id;
    private String name;
    private Set<String> applicationSystemIds;
    private AddressDTO postalAddress;
    private AddressDTO visitingAddress;
    private String webPage;
    private String email;
    private String fax;
    private String phone;
    private String description;
    private String healthcare;
    private String accessibility;
    private String learningEnvironment;
    private String dining;
    private String livingExpenses;
    private String living;
    
    private String yearClock;
    private String financingStudies;
    private String insurances;
    private String leisureServices;
    
    
    private List<SocialDTO> social;
    private boolean pictureFound = false;
    private boolean athleteEducation;
    private ApplicationOfficeDTO applicationOffice;
    private String homeplace;

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

    public Set<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(Set<String> applicationSystemIds) {
        this.applicationSystemIds = applicationSystemIds;
    }

    public AddressDTO getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(AddressDTO postalAddress) {
        this.postalAddress = postalAddress;
    }

    public AddressDTO getVisitingAddress() {
        return visitingAddress;
    }

    public void setVisitingAddress(AddressDTO visitingAddress) {
        this.visitingAddress = visitingAddress;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHealthcare() {
        return healthcare;
    }

    public void setHealthcare(String healthcare) {
        this.healthcare = healthcare;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getLearningEnvironment() {
        return learningEnvironment;
    }

    public void setLearningEnvironment(String learningEnvironment) {
        this.learningEnvironment = learningEnvironment;
    }

    public String getDining() {
        return dining;
    }

    public void setDining(String dining) {
        this.dining = dining;
    }

    public String getLivingExpenses() {
        return livingExpenses;
    }

    public void setLivingExpenses(String livingExpenses) {
        this.livingExpenses = livingExpenses;
    }

    public List<SocialDTO> getSocial() {
        return social;
    }

    public void setSocial(List<SocialDTO> social) {
        this.social = social;
    }

    public boolean isPictureFound() {
        return pictureFound;
    }

    public void setPictureFound(boolean pictureFound) {
        this.pictureFound = pictureFound;
    }

    public boolean isAthleteEducation() {
        return athleteEducation;
    }

    public void setAthleteEducation(boolean athleteEducation) {
        this.athleteEducation = athleteEducation;
    }

    public ApplicationOfficeDTO getApplicationOffice() {
        return applicationOffice;
    }

    public void setApplicationOffice(ApplicationOfficeDTO applicationOffice) {
        this.applicationOffice = applicationOffice;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public String getYearClock() {
        return yearClock;
    }

    public void setYearClock(String yearClock) {
        this.yearClock = yearClock;
    }

    public String getFinancingStudies() {
        return financingStudies;
    }

    public void setFinancingStudies(String financingStudies) {
        this.financingStudies = financingStudies;
    }

    public String getInsurances() {
        return insurances;
    }

    public void setInsurances(String insurances) {
        this.insurances = insurances;
    }

    public String getLeisureServices() {
        return leisureServices;
    }

    public void setLeisureServices(String leisureServices) {
        this.leisureServices = leisureServices;
    }

    public String getHomeplace() {
        return homeplace;
    }

    public void setHomeplace(String homeplace) {
        this.homeplace = homeplace;
    }
}
