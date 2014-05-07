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

package fi.vm.sade.koulutusinformaatio.dao.entity;


import org.mongodb.morphia.annotations.Embedded;

/**
 * @author Hannu Lyytikainen
 */
@Embedded
public class ApplicationOfficeEntity {

    @Embedded
    private I18nTextEntity name;
    @Embedded
    private AddressEntity visitingAddress;
    @Embedded
    private AddressEntity postalAddress;
    private I18nTextEntity phone;
    private I18nTextEntity email;
    private I18nTextEntity www;

    public ApplicationOfficeEntity() {
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public AddressEntity getVisitingAddress() {
        return visitingAddress;
    }

    public void setVisitingAddress(AddressEntity visitingAddress) {
        this.visitingAddress = visitingAddress;
    }

    public AddressEntity getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(AddressEntity postalAddress) {
        this.postalAddress = postalAddress;
    }

    public I18nTextEntity getPhone() {
        return phone;
    }

    public void setPhone(I18nTextEntity phone) {
        this.phone = phone;
    }

    public I18nTextEntity getEmail() {
        return email;
    }

    public void setEmail(I18nTextEntity email) {
        this.email = email;
    }

    public I18nTextEntity getWww() {
        return www;
    }

    public void setWww(I18nTextEntity www) {
        this.www = www;
    }
}
