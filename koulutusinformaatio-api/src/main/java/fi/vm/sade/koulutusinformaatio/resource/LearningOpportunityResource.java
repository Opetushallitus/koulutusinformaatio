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

import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunityDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Path("/lo")
public interface LearningOpportunityResource {

    /**
     * Searches learning opportunities.
     *
     * @param text search key
     * @return list of search results
     */
    @GET
    @Path("search/{text}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LearningOpportunitySearchResultDTO> searchLearningOpportunities(@PathParam("text") String text);

    /**
     * Fetches a parent learning opportunity. Contains parent information and
     * all the child learning opportunities that belong to the parent.
     *
     * @param parentId learning opportunity id
     * @return parent learning opportunity dto object
     */
    @GET
    @Path("{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ParentLearningOpportunityDTO getParentLearningOpportunity(@PathParam("parentId") String parentId);

}
