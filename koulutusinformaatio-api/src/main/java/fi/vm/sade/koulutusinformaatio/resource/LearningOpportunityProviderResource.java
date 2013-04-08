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

import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Resources that can be used to access learning opportunity providers.
 *
 * @author Hannu Lyytikainen
 */
@Path("/lop")
public interface LearningOpportunityProviderResource {

    public static final String PREREQUISITE = "prerequisite";
    public static final String VOCATIONAL = "vocational";
    public static final String ASID = "asId";
    public static final String TERM = "term";

    /**
     * Searches providers by the search term. The search term can be
     * the name of a provider or a part of a name. Query parameters
     * limit the search results.
     *
     * @param term search term ()
     * @param asId application system id, limits providers to the ones that
     *             are related to this application system
     * @param prerequisite limits the providers by related learning opportunity instances' prerequisites
     *                     (backgraound education of the user)
     * @param vocational limits providers to those related to vocational studies
     *                   (user has vocational degree (true|false))
     * @return list of providers
     */
    @GET
    @Path("search/{" + TERM + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ProviderSearchResult> searchProviders(@PathParam(TERM) final String term,
                                                 @QueryParam(ASID) final String asId,
                                                 @QueryParam(PREREQUISITE) final String prerequisite,
                                                 @QueryParam(VOCATIONAL) final boolean vocational);

}
