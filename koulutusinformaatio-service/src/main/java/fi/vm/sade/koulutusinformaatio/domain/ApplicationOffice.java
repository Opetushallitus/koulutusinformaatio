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

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOffice {

    private I18nText name;
    private I18nText phone;
    private I18nText email;
    private I18nText www;
    private Address visitingAddress;
    private Address postalAddress;

    public ApplicationOffice(I18nText name, I18nText phone, I18nText email, I18nText www, Address visitingAddress, Address postalAddress) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.www = www;
        this.visitingAddress = visitingAddress;
        this.postalAddress = postalAddress;
    }

    public ApplicationOffice() {}

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public Address getVisitingAddress() {
        return visitingAddress;
    }

    public void setVisitingAddress(Address visitingAddress) {
        this.visitingAddress = visitingAddress;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public I18nText getPhone() {
        return phone;
    }

    public void setPhone(I18nText phone) {
        this.phone = phone;
    }

    public I18nText getEmail() {
        return email;
    }

    public void setEmail(I18nText email) {
        this.email = email;
    }

    public I18nText getWww() {
        return www;
    }

    public void setWww(I18nText www) {
        this.www = www;
    }
}
