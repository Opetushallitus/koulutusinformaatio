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

import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ChildLORef {

    private String childLOId;
    private List<String> asIds;
    private I18nText name;
    private String nameByTeachingLang;
    private I18nText qualification;
    private I18nText prerequisite;

    public String getChildLOId() {
        return childLOId;
    }

    public void setChildLOId(String childLOId) {
        this.childLOId = childLOId;
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

    public I18nText getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nText prerequisite) {
        this.prerequisite = prerequisite;
    }
}
