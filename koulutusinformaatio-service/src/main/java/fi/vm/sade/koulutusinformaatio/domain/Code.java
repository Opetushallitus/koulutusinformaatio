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

/**
 * Represents a code retrieved from KoodistoService.
 * Contains a value and an internationalized description.
 *
 * @author Hannu Lyytikainen
 */
public class Code {

    private String value;
    private I18nText name;
    private I18nText shortTitle;
    private I18nText description;
    private Code parent;
    private String uri;

    public Code() {}

    public Code(String value, I18nText name) {
        this.value = value;
        this.name = name;
    }

    public Code(String value, I18nText name, I18nText description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }
    
    public Code(String value, I18nText name, I18nText shortName, I18nText description, String uri) {
        this.value = value;
        this.name = name;
        this.shortTitle = shortName;
        this.description = description;
        this.uri = uri;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nText shortName) {
        this.shortTitle = shortName;
    }

    public I18nText getDescription() {
        return description;
    }

    public void setDescription(I18nText description) {
        this.description = description;
    }

    public Code getParent() {
        return parent;
    }

    public void setParent(Code parent) {
        this.parent = parent;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return this.getUri();
    }
}
