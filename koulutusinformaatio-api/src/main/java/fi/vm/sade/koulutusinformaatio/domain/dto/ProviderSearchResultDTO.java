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

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ProviderSearchResultDTO {

    private String id;
    private String name;
    private String address;
    private String thumbnailEncoded;
    private boolean providerOrg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String description) {
        this.address = description;
    }

    public String getThumbnailEncoded() {
        return thumbnailEncoded;
    }

    public void setThumbnailEncoded(String thumbnailEncoded) {
        this.thumbnailEncoded = thumbnailEncoded;
    }

    public boolean isProviderOrg() {
        return providerOrg;
    }

    public void setProviderOrg(boolean providerOrg) {
        this.providerOrg = providerOrg;
    }
}
