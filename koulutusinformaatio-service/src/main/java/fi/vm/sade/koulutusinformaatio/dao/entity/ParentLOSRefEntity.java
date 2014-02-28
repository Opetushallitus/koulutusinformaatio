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
public class ParentLOSRefEntity {

    private String id;
    @Embedded
    private I18nTextEntity name;
    private String losType;

    public ParentLOSRefEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public String getLosType() {
        return losType;
    }

    public void setLosType(String losType) {
        this.losType = losType;
    }
}
