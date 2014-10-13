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

import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;

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

    public static final String BASE_EDUCATION = "baseEducation";
    public static final String VOCATIONAL = "vocational";
    public static final String NON_VOCATIONAL = "nonVocational";
    public static final String ASID = "asId";
    public static final String TERM = "term";
    public static final String LANG = "lang";
    public static final String LANG_FI = "fi";

    /**
     * Searches providers by the search term. The search term can be
     * the name of a provider or a part of a name. Query parameters
     * limit the search results.
     *
     * @param term search term ()
     * @param asId application system id, limits providers to the ones that
     *             are related to this application system
     * @param baseEducation limits the providers by the base education that is
     *                      required for education provided by given provider
     * @param vocational includes vocational providers if true (default value true)
     * @param nonVocational includes non vocational providers if true (default value true)
     * @param start start index of the search results (0 is the first result)
     * @param rows search result max row count to return
     *
     * @return list of providers
     */
    @GET
    @Path("search/{" + TERM + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ProviderSearchResultDTO> searchProviders(@PathParam(TERM) final String term,
                                                 @QueryParam(ASID) final String asId,
                                                 @QueryParam(BASE_EDUCATION) final List<String> baseEducation,
                                                 @DefaultValue(value = "true") @QueryParam(VOCATIONAL) final boolean vocational,
                                                 @DefaultValue(value = "true") @QueryParam(NON_VOCATIONAL) final boolean nonVocational,
                                                 @DefaultValue(value = "0") @QueryParam("start") int start,
                                                 @DefaultValue(value = "50") @QueryParam("rows") int rows,
                                                 @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

    @GET
    @Path("{lopId}/picture")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public PictureDTO getProviderPicture(@PathParam("lopId") final String lopId);
    
    @GET
    @Path("{lopId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LearningOpportunityProviderDTO getProvider(@PathParam("lopId") final String lopId,
                                  @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

}
