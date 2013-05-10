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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ChildLO {

    private String losId;
    private String loiId;
    private I18nText name;
    private I18nText qualification;
    private I18nText degreeTitle;
    private ApplicationOption applicationOption;
    private List<ChildLORef> related = new ArrayList<ChildLORef>();
    private ParentLORef parent;

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public String getLoiId() {
        return loiId;
    }

    public void setLoiId(String loiId) {
        this.loiId = loiId;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public ApplicationOption getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOption applicationOption) {
        this.applicationOption = applicationOption;
    }

    public List<ChildLORef> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLORef> related) {
        this.related = related;
    }

    public ParentLORef getParent() {
        return parent;
    }

    public void setParent(ParentLORef parent) {
        this.parent = parent;
    }
}
