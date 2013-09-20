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
    private String phone;
    private String email;
    private String www;
    private Address visitingAddress;
    private Address postalAddress;

    public ApplicationOffice(I18nText name, String phone, String email, String www, Address visitingAddress, Address postalAddress) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }
}
