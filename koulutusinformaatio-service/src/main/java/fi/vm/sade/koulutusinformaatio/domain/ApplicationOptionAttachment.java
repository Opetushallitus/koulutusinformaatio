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

import java.util.Date;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionAttachment {

    private Date dueDate;
    private I18nText type;
    private I18nText descreption;
    private Address address;
    private String emailAddr;
    private boolean usedInApplicationForm = true;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public I18nText getType() {
        return type;
    }

    public void setType(I18nText type) {
        this.type = type;
    }

    public I18nText getDescreption() {
        return descreption;
    }

    public void setDescreption(I18nText descreption) {
        this.descreption = descreption;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setEmailAddr(String sahkoinenToimitusosoite) {
        this.emailAddr = sahkoinenToimitusosoite;
    }

    public String getEmailAddr() {
        return emailAddr;
    }
    
    public void setUsedInApplicationForm(boolean used) {
        this.usedInApplicationForm = used;
    }

    public boolean isUsedInApplicationForm() {
        return usedInApplicationForm;
    }
}