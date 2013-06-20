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

import com.google.code.morphia.annotations.Embedded;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ChildLORefEntity {

    private String childLOId;
    private List<String> asIds;
    @Embedded
    private I18nTextEntity name;
    private String nameByTeachingLang;
    @Embedded
    private I18nTextEntity qualification;
    @Embedded
    private I18nTextEntity prerequisite;

    public ChildLORefEntity() {}

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

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public String getNameByTeachingLang() {
        return nameByTeachingLang;
    }

    public void setNameByTeachingLang(String nameByTeachingLang) {
        this.nameByTeachingLang = nameByTeachingLang;
    }

    public I18nTextEntity getQualification() {
        return qualification;
    }

    public void setQualification(I18nTextEntity qualification) {
        this.qualification = qualification;
    }

    public I18nTextEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nTextEntity prerequisite) {
        this.prerequisite = prerequisite;
    }
}
