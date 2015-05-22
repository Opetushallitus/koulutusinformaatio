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

/**
 * @author Mikko Majapuro
 */
@Embedded
public class AddressEntity {

    @Embedded
    private I18nTextEntity streetAddress;
    @Embedded
    private I18nTextEntity secondForeignAddr;
    @Embedded
    private I18nTextEntity postOffice;
    @Embedded
    private I18nTextEntity postalCode;

    public AddressEntity() {}

    public I18nTextEntity getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(I18nTextEntity streetAddress) {
        this.streetAddress = streetAddress;
    }

    public I18nTextEntity getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(I18nTextEntity postalCode) {
        this.postalCode = postalCode;
    }

    public I18nTextEntity getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(I18nTextEntity postOffice) {
        this.postOffice = postOffice;
    }

    public I18nTextEntity getSecondForeignAddr() {
        return secondForeignAddr;
    }

    public void setSecondForeignAddr(I18nTextEntity secondForeignAddr) {
        this.secondForeignAddr = secondForeignAddr;
    }
}
