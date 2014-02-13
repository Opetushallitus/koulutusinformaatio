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

import fi.vm.sade.koulutusinformaatio.domain.dto.LocationDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Path("/location")
public interface LocationResource {

    public static final String TERM = "term";
    public static final String LANG = "lang";
    public static final String LANG_FI = "fi";

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocationDTO> getLocations(@QueryParam("code") List<String> code, @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

    @GET
    @Path("search/{" + TERM + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocationDTO> searchLocations(@PathParam(TERM) final String term, @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);
    
    @GET
    @Path("districts")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocationDTO> getDistricts(@DefaultValue(LANG_FI) @QueryParam(LANG) String lang);
    
    @GET
    @Path("child-locations")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocationDTO> getChildLocations(@QueryParam("districts") List<String> districts, @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);
    
}
