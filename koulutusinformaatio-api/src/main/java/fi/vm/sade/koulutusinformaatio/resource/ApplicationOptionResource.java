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

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/ao")
@Api(value = "/ao", description = "Hakukohteet")
public interface ApplicationOptionResource {

    @GET
    @Path("/search/{asId}/{lopId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakukohteiden etsiminen haulla ja tarjoajalla",
            notes = "",
            response = ApplicationOptionSearchResultDTO.class,
            responseContainer = "List")
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(
            @ApiParam(value = "Haun oid") @PathParam("asId") final String asId, 
            @ApiParam(value = "Tarjoajan oid") @PathParam("lopId") final String lopId,
            @ApiParam(value = "Pohjakoulutus") @QueryParam("baseEducation") final String baseEducation,
            @ApiParam(value = "Ammatillisia koulutuksia") @DefaultValue("true") @QueryParam("vocational") boolean vocational,
            @ApiParam(value = "Ei-ammatillista") @DefaultValue("true") @QueryParam("nonVocational") boolean nonVocational,
            @ApiParam(value = "Haku on auki") @DefaultValue("false") @QueryParam("ongoing") boolean ongoing,
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("uiLang") String uiLang);

    @GET
    @Path("/{aoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakukohteen hakeminen oidilla",
            notes = "",
            response = ApplicationOptionSearchResultDTO.class)
    public ApplicationOptionDTO getApplicationOption(
            @ApiParam(value = "Oid") @PathParam("aoId") final String aoId,
            @ApiParam(value = "Hakukohteen kieli") @DefaultValue("fi") @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli") @DefaultValue("fi") @QueryParam("uiLang") String uiLang);

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Usean hakukohten hakeminen oideilla",
            notes = "",
            response = ApplicationOptionDTO.class,
            responseContainer = "List")
    public List<ApplicationOptionDTO> getApplicationOptions(
            @ApiParam(value = "Lista oideja") @QueryParam("aoId") List<String> aoId, 
            @ApiParam(value = "Hakukohteen kieli", required = true) @DefaultValue("fi") @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli") @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
