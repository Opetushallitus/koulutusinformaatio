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

package fi.vm.sade.koulutusinformaatio.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LocationDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/location")
@Api(value = "/location", description = "Kuntien hakurajapinta")
public interface LocationResource {

    public static final String TERM = "term";
    public static final String LANG = "lang";
    public static final String LANG_FI = "fi";

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Kunnan tietojen haku koodilla",
        notes = "",
        response = LocationDTO.class,
        responseContainer = "List")
    public List<LocationDTO> getLocations(
            @ApiParam(value = "Kuntakoodi") @QueryParam("code") List<String> code,
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

    @GET
    @Path("search/{" + TERM + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Kunnan etsiminen sanahaulla",
        notes = "",
        response = LocationDTO.class,
        responseContainer = "List")
    public List<LocationDTO> searchLocations(
            @ApiParam(value = "Hakusana") @PathParam(TERM) final String term,
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

    @GET
    @Path("districts")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Maakuntalistaus",
        notes = "",
        response = LocationDTO.class,
        responseContainer = "List")
    public List<LocationDTO> getDistricts(
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

    @GET
    @Path("child-locations")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Maakunnan kuntien listaaminen",
        notes = "",
        response = LocationDTO.class,
        responseContainer = "List")
    public List<LocationDTO> getChildLocations(
            @ApiParam(value = "Lista maakuntia") @QueryParam("districts") List<String> districts,
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

}
