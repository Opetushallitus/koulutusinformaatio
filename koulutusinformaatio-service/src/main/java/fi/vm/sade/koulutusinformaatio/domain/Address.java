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

package fi.vm.sade.koulutusinformaatio.domain;

/**
 * @author Mikko Majapuro
 */
public class Address {

    private I18nText streetAddress;
    private I18nText secondForeignAddr;
    private I18nText postalCode;
    private I18nText postOffice;

    public I18nText getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(I18nText streetAddress) {
        this.streetAddress = streetAddress;
    }

    public I18nText getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(I18nText postalCode) {
        this.postalCode = postalCode;
    }

    public I18nText getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(I18nText postOffice) {
        this.postOffice = postOffice;
    }

    public I18nText getSecondForeignAddr() {
        return secondForeignAddr;
    }

    public void setSecondForeignAddr(I18nText secondForeignAddr) {
        this.secondForeignAddr = secondForeignAddr;
    }
}
