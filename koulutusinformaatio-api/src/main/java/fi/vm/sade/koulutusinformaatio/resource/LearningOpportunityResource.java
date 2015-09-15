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

import fi.vm.sade.koulutusinformaatio.domain.dto.AdultUpperSecondaryLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.dto.SpecialLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.StandaloneLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SuggestedTermsResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunitySpecificationDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/lo")
public interface LearningOpportunityResource {

    /**
     * Searches learning opportunities.
     *
     * @param text search key
     * @param prerequisite base education prerequisite (pohjakoulutusvaatimus) filter
     *                     if none is provided, no filtering is used
     * @return list of search results
     */
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public LOSearchResultListDTO searchLearningOpportunities(@QueryParam("text") String text,
                                                             @QueryParam("prerequisite") String prerequisite,
                                                             @QueryParam("city") List<String> cities,
                                                             @QueryParam("facetFilters") List<String> facetFilters,
                                                             @QueryParam("articleFacetFilters") List<String> articleFilters,
                                                             @QueryParam("providerFacetFilters") List<String> providerFilters,
                                                             @QueryParam("lang") String lang,
                                                             @DefaultValue(value = "false") @QueryParam("ongoing") boolean ongoing,
                                                             @DefaultValue(value = "false") @QueryParam("upcoming") boolean upcoming,
                                                             @DefaultValue(value = "false") @QueryParam("upcomingLater") boolean upcomingLater,
                                                             @DefaultValue(value = "0") @QueryParam("start") int start,
                                                             @DefaultValue(value = "100") @QueryParam("rows") int rows,
                                                             @QueryParam("sort") String sort,
                                                             @DefaultValue(value = "asc") @QueryParam("order") String order,
                                                             @QueryParam("lopFilter") String lopFilter,
                                                             @QueryParam("educationCodeFilter") String educationCodeFilter,
                                                             @QueryParam("excludes") List<String> excludes,
                                                             @QueryParam("asId") final String asId,
                                                             @QueryParam("searchType") SearchType searchType);

    @GET
    @Path("tutkinto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ParentLearningOpportunitySpecificationDTO getTutkintoLearningOpportunity(@PathParam("id") String id,
                                                                                  @QueryParam("lang") String lang,
                                                                                  @QueryParam("uiLang") String uiLang,
                                                                                  @QueryParam("prerequisite") String prerequisite);

    /**
     * Fetches a child learning opportunity that belongs to the specified parent.
     * Child lo texts are translated to language corresponding given lang parameter.
     * If the given language is not found or the parameter is null it fall back to default (education) language.
     *
     * @param cloId child learning opportunity id
     * @param lang translation language (optional)
     * @param uiLang user interface language, used to translate some information
     * @return child learning opportunity dto object
     */
    @GET
    @Path("child/{cloId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(@PathParam("cloId") String cloId,
                                                                                @QueryParam("lang") String lang,
                                                                                @QueryParam("uiLang") String uiLang);
    /**
     * Fetches an upper secondary (lukio) learning opportunity
     *
     * @param id learning opportunity identifier
     * @param lang language
     * @param uiLang user interface language
     * @return upper secondary learning opportunity
     */
    @GET
    @Path("upsec/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(@PathParam("id") String id,
                                                                                                  @QueryParam("lang") String lang,
                                                                                                  @QueryParam("uiLang") String uiLang);

    /**
     * Fetches an generic v1 learning opportunity (standalone)
     *
     * @param id learning opportunity identifier
     * @param lang language
     * @param uiLang user interface language
     * @return upper secondary learning opportunity
     */
    @GET
    @Path("koulutus/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StandaloneLOSDTO getKoulutusLearningOpportunity(@PathParam("id") String id,
                                                                                                  @QueryParam("lang") String lang,
                                                                                                  @QueryParam("uiLang") String uiLang);

    /**
     * Fetches a special learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return special learning opportunity specification
     */
    @GET
    @Path("special/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpecialLearningOpportunitySpecificationDTO getSpecialLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);

    /**
     * Fetches a higher education learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return higher education learning opportunity specification
     */
    @GET
    @Path("highered/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);

    /**
     * Fetches an adult upper secondary learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return adult upper secondary learning opportunity specification
     */
    @GET
    @Path("adultupsec/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AdultUpperSecondaryLOSDTO getAdultUpperSecondaryLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);


    /**
     * Fetches an adult upper secondary learning opportunity specification.
     *
     * @param id los id
     * @param lang translation language
     * @param uiLang secondary translation language
     * @return adult upper secondary learning opportunity specification
     */
    @GET
    @Path("adultvocational/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(@PathParam("id") String id,
                                                                                    @QueryParam("lang") String lang,
                                                                                    @QueryParam("uiLang") String uiLang);


    /**
     * Fetches suggested terms to be used in free text search.
     * The returned terms match the term given as parameter.
     *
     * @param term for which matching terms are searched
     * @param lang language
     * @return upper secondary learning opportunity
     */
    @GET
    @Path("autocomplete")
    @Produces(MediaType.APPLICATION_JSON)
    public SuggestedTermsResultDTO getSuggestedTerms(@QueryParam("term") String term,
                                                     @QueryParam("lang") String lang);

    /**
     * Fetches a higher education learning opportunity. To be used in preview for learning opportunity.
     *
     * @param oid of the learning opportunity
     * @param lang language
     * @return higher education learning opportunity
     */
    @GET
    @Path("preview/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public LOSDTO previewLearningOpportunity(@PathParam("oid") String oid,
                                                     @QueryParam("lang") String lang,
                                                     @QueryParam("uiLang") String uiLang,
                                                     @QueryParam("loType") String loType);

    @GET
    @Path("/picture/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public PictureDTO getPicture(@PathParam("id") final String id);
}
