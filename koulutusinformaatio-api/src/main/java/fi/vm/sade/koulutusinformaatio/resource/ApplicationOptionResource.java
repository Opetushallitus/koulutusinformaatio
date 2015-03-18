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

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/ao")
public interface ApplicationOptionResource {

    @GET
    @Path("/search/{asId}/{lopId}")
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
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationOptionDTO getApplicationOption(@PathParam("aoId") final String aoId, @DefaultValue("fi") @QueryParam("lang") String lang,
                                                     @DefaultValue("fi") @QueryParam("uiLang") String uiLang);

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ApplicationOptionDTO> getApplicationOptions(@QueryParam("aoId") List<String> aoId, @DefaultValue("fi") @QueryParam("lang") String lang,
                                                            @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
