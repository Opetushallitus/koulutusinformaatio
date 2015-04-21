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

import java.util.List;

/**
 * 
 * @author Markus
 *
 */
public class AdultVocationalLOS extends StandaloneLOS {
    
    private ParentLOSRef parent;
    private boolean valmistavaKoulutus;
    
    
    private boolean chargeable;
    private String charge;
    
    private List<I18nText> professionalTitles;
    
    private I18nText personalization;
    private List<ContactPerson> preparatoryContactPersons;
    private I18nText organizer;
    
    public ParentLOSRef getParent() {
        return parent;
    }

    public void setParent(ParentLOSRef parent) {
        this.parent = parent;
    }

    public boolean isValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    public void setValmistavaKoulutus(boolean valmistava) {
        this.valmistavaKoulutus = valmistava;
    }

    public boolean isChargeable() {
        return chargeable;
    }

    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String string) {
        this.charge = string;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public I18nText getPersonalization() {
        return personalization;
    }

    public void setPersonalization(I18nText personalization) {
        this.personalization = personalization;
    }

    public void setPreparatoryContactPersons(List<ContactPerson> persons) {
        this.preparatoryContactPersons = persons;
        
    }

    public List<ContactPerson> getPreparatoryContactPersons() {
        return preparatoryContactPersons;
    }

    public void setOrganizer(I18nText name) {
        this.organizer = name;
        
    }

    public I18nText getOrganizer() {
        return organizer;
    }
    

}
