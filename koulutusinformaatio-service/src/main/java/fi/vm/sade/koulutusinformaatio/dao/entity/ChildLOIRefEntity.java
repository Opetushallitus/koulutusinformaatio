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

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ChildLOIRefEntity {

    private String id;
    private String losId;
    private List<String> asIds;
    @Embedded
    private I18nTextEntity name;
    private String nameByTeachingLang;
    @Embedded
    private I18nTextEntity qualification;
    @Embedded
    private CodeEntity prerequisite;

    public ChildLOIRefEntity() {}

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

    public CodeEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }
}
