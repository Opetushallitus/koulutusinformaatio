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
 * Learning opportunity result. Can be a specification (LOS) or an instance (LOI).
 *
 * @author Hannu Lyytikainen
 */
public class LOSearchResult {

    private String id;
    private I18nText name;
    private String lopId;
    private I18nText lopName;
    private String parentId;

    public LOSearchResult(String id, I18nText name, String lopId, I18nText lopName, String parentId) {
        this.id = id;
        this.name = name;
        this.lopId = lopId;
        this.lopName = lopName;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public String getLopId() {
        return lopId;
    }

    public void setLopId(String lopId) {
        this.lopId = lopId;
    }

    public I18nText getLopName() {
        return lopName;
    }

    public void setLopName(I18nText lopName) {
        this.lopName = lopName;
    }

    public String getParentId() {
        return parentId;
    }
}
