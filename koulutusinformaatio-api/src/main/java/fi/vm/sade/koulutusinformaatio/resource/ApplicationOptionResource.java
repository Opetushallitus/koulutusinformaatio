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

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Path("/ao")
@Api(value = "/ao", description="Kuvaus")
public interface ApplicationOptionResource {

    @GET
    @Path("/search/{asId}/{lopId}")
    @ApiOperation(httpMethod = "GET", value = "/search/{asId}/{lopId} <a href=https://github.com/Opetushallitus/koulutusinformaatio/blob/devel/koulutusinformaatio-api/src/main/java/fi/vm/sade/koulutusinformaatio/resource/ApplicationOptionResource.java search>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Click to see it on GitHub</a>", response = ApplicationOptionSearchResultDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(
            @PathParam("asId") final String asId, @PathParam("lopId") final String lopId,
            @QueryParam("baseEducation") final String baseEducation,
            @DefaultValue("true") @QueryParam("vocational") boolean vocational,
            @DefaultValue("true") @QueryParam("nonVocational") boolean nonVocational,
            @DefaultValue("false") @QueryParam("ongoing") boolean ongoing,
            @DefaultValue("fi") @QueryParam("uiLang") String uiLang);

    @GET
    @Path("/{aoId}")
    @ApiOperation(httpMethod = "GET", value = "/{aoId}", response = ApplicationOptionDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationOptionDTO getApplicationOption(@PathParam("aoId") final String aoId, @DefaultValue("fi") @QueryParam("lang") String lang,
                                                     @DefaultValue("fi") @QueryParam("uiLang") String uiLang);

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ApplicationOptionDTO> getApplicationOptions(@QueryParam("aoId") List<String> aoId, @DefaultValue("fi") @QueryParam("lang") String lang,
                                                            @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
