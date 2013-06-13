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

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
            @QueryParam("prerequisite") final List<String> prerequisite);
}
