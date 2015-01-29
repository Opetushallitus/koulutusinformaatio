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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ChildLOIRef {

    private String id;
    private String losId;
    private List<String> asIds;
    private I18nText name;
    private String nameByTeachingLang;
    private I18nText qualification;
    private List<I18nText> qualifications;
    private Code prerequisite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public List<String> getAsIds() {
        return asIds;
    }

    public void setAsIds(List<String> asIds) {
        this.asIds = asIds;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public String getNameByTeachingLang() {
        return nameByTeachingLang;
    }

    public void setNameByTeachingLang(String nameByTeachingLang) {
        this.nameByTeachingLang = nameByTeachingLang;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public Code getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nText> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<I18nText> qualifications) {
        this.qualifications = qualifications;
    }
}
