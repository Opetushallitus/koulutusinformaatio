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
public class CodeEntity {

    private String value;
    @Embedded
    private I18nTextEntity description;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity shortTitle;
    private String uri;

    public CodeEntity() {}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public I18nTextEntity getDescription() {
        return description;
    }

    public void setDescription(I18nTextEntity description) {
        this.description = description;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public I18nTextEntity getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nTextEntity shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
