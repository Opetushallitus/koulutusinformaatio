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
import java.util.Set;

/**
 * Learning opportunity provider ie. organization that provides certain education.
 *
 * @author Hannu Lyytikainen
 */
public class Provider {

    private String id;
    private I18nText name;
    private Set<String> applicationSystemIDs = new HashSet<String>();
    private Address postalAddress;
    private Address visitingAddress;

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

    public Set<String> getApplicationSystemIDs() {
        return applicationSystemIDs;
    }

    public void setApplicationSystemIDs(Set<String> applicationSystemIDs) {
        this.applicationSystemIDs = applicationSystemIDs;
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
}
