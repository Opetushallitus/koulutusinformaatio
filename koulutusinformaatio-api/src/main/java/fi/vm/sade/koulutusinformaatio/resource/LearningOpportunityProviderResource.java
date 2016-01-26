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
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;

/**
 * Resources that can be used to access learning opportunity providers.
 *
 * @author Hannu Lyytikainen
 */
@Path("/lop")
@Api(value = "/lop", description = "Opetustarjoajat")
public interface LearningOpportunityProviderResource {

    public static final String BASE_EDUCATION = "baseEducation";
    public static final String VOCATIONAL = "vocational";
    public static final String ONGOING = "ongoing";
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
    @ApiOperation(value = "Tarjoajien etsiminen",
        notes = "",
        response = ProviderSearchResultDTO.class,
        responseContainer = "List")
    public List<ProviderSearchResultDTO> searchProviders(
            @ApiParam(value = "Hakusana") @PathParam(TERM) final String term,
            @ApiParam(value = "Haun oid") @QueryParam(ASID) final String asId,
            @ApiParam(value = "Pohjakoulutus") @QueryParam(BASE_EDUCATION) final List<String> baseEducation,
            @ApiParam(value = "Vain ammatilliset") @DefaultValue(value = "true") @QueryParam(VOCATIONAL) final boolean vocational,
            @ApiParam(value = "Vain ei-ammatilliset") @DefaultValue(value = "true") @QueryParam(NON_VOCATIONAL) final boolean nonVocational,
            @ApiParam(value = "Sivutuksen alku") @DefaultValue(value = "0") @QueryParam("start") int start,
            @ApiParam(value = "Sivun suuruus") @DefaultValue(value = "50") @QueryParam("rows") int rows,
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang,
            @ApiParam(value = "Tarjoajalla on koulutusta avoimessa haussa") @DefaultValue("false") @QueryParam(ONGOING) boolean ongoing,
            @ApiParam(value = "Tyyppi") @QueryParam("type") String type);

    @GET
    @Path("{lopId}/picture")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Kuva",
        notes = "",
        response = PictureDTO.class)
    public PictureDTO getProviderPicture(
            @ApiParam(value = "Tarjoajan oid") @PathParam("lopId") final String lopId);
    
    @GET
    @Path("{lopId}/thumbnail")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Pikkukuvan",
        notes = "",
        response = PictureDTO.class)
    public PictureDTO getProviderThumbnail(
            @ApiParam(value = "Tarjoajan oid") @PathParam("lopId") final String lopId);
    
    /**
     * Returns the Learning opportunity provider (organization) with the given id (lopId).
     * The information is given with the language specified as parameter.
     * 
     * @param lopId The id of the organization.
     * @param lang The language with which the information is given.
     * @return The organization with the given id.
     */
    @GET
    @Path("{lopId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Tarjoajan haku oidilla",
        notes = "",
        response = LearningOpportunityProviderDTO.class)
    public LearningOpportunityProviderDTO getProvider(
            @ApiParam(value = "Tarjoajan oid") @PathParam("lopId") final String lopId,
            @ApiParam(value = "Kieli") @DefaultValue(LANG_FI) @QueryParam(LANG) String lang);

}
