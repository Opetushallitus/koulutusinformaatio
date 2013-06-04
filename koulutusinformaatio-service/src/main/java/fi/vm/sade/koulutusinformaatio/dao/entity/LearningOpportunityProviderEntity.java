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

import java.util.Set;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

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
    private String webPage;
    private String email;
    private String fax;
    private String phone;

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
}
