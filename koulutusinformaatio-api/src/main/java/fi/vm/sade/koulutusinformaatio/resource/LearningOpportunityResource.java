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

import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.KoulutusLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.TutkintoLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.dto.SpecialLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SuggestedTermsResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunitySpecificationDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/lo")
@Api(value = "/lo", description = "Koulutukset")
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
    @ApiOperation(value = "Koulutusten etsintärajapinta",
        notes = "",
        response = LOSearchResultListDTO.class)
    public LOSearchResultListDTO searchLearningOpportunities(
            @ApiParam(value = "Teksti") @QueryParam("text") String text,
            @ApiParam(value = "Pohjakoulutus") @QueryParam("prerequisite") String prerequisite,
            @ApiParam(value = "Haun oid") @QueryParam("city") List<String> cities,
            @ApiParam(value = "Koulutuksen hakurajaimet") @QueryParam("facetFilters") List<String> facetFilters,
            @ApiParam(value = "Artikkelin hakurajaimet") @QueryParam("articleFacetFilters") List<String> articleFilters,
            @ApiParam(value = "Tarjoajien hakurajaimet") @QueryParam("providerFacetFilters") List<String> providerFilters,
            @ApiParam(value = "Kieli") @QueryParam("lang") String lang,
            @ApiParam(value = "Vain avoimien hakujen koulutukset") @DefaultValue(value = "false") @QueryParam("ongoing") boolean ongoing,
            @ApiParam(value = "Tulevan kauden hauissa olevat koulutukset") @DefaultValue(value = "false") @QueryParam("upcoming") boolean upcoming,
            @ApiParam(value = "Myöhempien kausien hakujen koulutukuset") @DefaultValue(value = "false") @QueryParam("upcomingLater") boolean upcomingLater,
            @ApiParam(value = "Sivutuksen alku") @DefaultValue(value = "0") @QueryParam("start") int start,
            @ApiParam(value = "Sivun koko") @DefaultValue(value = "100") @QueryParam("rows") int rows,
            @ApiParam(value = "Järjestyskriteeri") @QueryParam("sort") String sort,
            @ApiParam(value = "Järjestyksen suunta") @DefaultValue(value = "asc") @QueryParam("order") String order,
            @ApiParam(value = "Tarjoajarajain") @QueryParam("lopFilter") String lopFilter,
            @ApiParam(value = "Koulutuskoodirajain") @QueryParam("educationCodeFilter") String educationCodeFilter,
            @ApiParam(value = "Pois jätettävät koulutusoidit") @QueryParam("excludes") List<String> excludes,
            @ApiParam(value = "Haun oid") @QueryParam("asId") final String asId,
            @ApiParam(value = "Hakutyyppi") @QueryParam("searchType") SearchType searchType);

    @GET
    @Path("tutkinto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Ammatillisten tutkintojen tutkintosivujen rajapinta",
        notes = "Tuntkintosivulle on koostettu opetustarjoajan ammattitutkinnon hakukohteet.",
        response = TutkintoLOSDTO.class)
    public TutkintoLOSDTO getTutkintoLearningOpportunity(
            @ApiParam(value = "Tutkinnon id") @PathParam("id") String id,
            @ApiParam(value = "Kieli") @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli") @QueryParam("uiLang") String uiLang,
            @ApiParam(value = "PK vai YO-pohjainen tutkinto") @QueryParam("prerequisite") String prerequisite);

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
    @Deprecated
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(
            @PathParam("cloId") String cloId,
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
    @Deprecated
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(
            @PathParam("id") String id,
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
    @ApiOperation(value = "Koulutusten rajapinta",
        notes = "Kaikki koulutukset lukuunottamatta korkeakoulujen koulutuksia.",
        response = KoulutusLOSDTO.class)
    public KoulutusLOSDTO getKoulutusLearningOpportunity(
            @ApiParam(value = "Koulutuksen oid") @PathParam("id") String id,
            @ApiParam(value = "Kieli") @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli") @QueryParam("uiLang") String uiLang);

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
    @Deprecated
    public SpecialLearningOpportunitySpecificationDTO getSpecialLearningOpportunity(
            @PathParam("id") String id,
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
    @ApiOperation(value = "Korkeakoulutuksen rajapinta",
        notes = "Yliopistot ja ammattikorkeakoulutus",
        response = HigherEducationLOSDTO.class)
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(
            @ApiParam(value = "Koulutusken oid") @PathParam("id") String id,
            @ApiParam(value = "Kieli") @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli") @QueryParam("uiLang") String uiLang);

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
    @Deprecated
    public KoulutusLOSDTO getAdultUpperSecondaryLearningOpportunity(
            @PathParam("id") String id,
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
    @Deprecated
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(
            @PathParam("id") String id,
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
    @ApiOperation(value = "Hakupalkin hakuehdotusrajapinta",
        notes = "",
        response = SuggestedTermsResultDTO.class)
    public SuggestedTermsResultDTO getSuggestedTerms(
            @ApiParam(value = "Hakusana") @QueryParam("term") String term,
            @ApiParam(value = "Kieli") @QueryParam("lang") String lang);

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
    @ApiOperation(value = "Esikatselun rajapinta",
        notes = "",
        response = LOSDTO.class)
    public LOSDTO previewLearningOpportunity(
            @ApiParam(value = "Koulutuksen oid")  @PathParam("oid") String oid,
            @ApiParam(value = "Kieli")  @QueryParam("lang") String lang,
            @ApiParam(value = "Käyttöliittymäkieli")  @QueryParam("uiLang") String uiLang,
            @ApiParam(value = "Koulutustyyppi")  @QueryParam("loType") String loType);

    @GET
    @Path("/picture/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakukohteiden etsiminen haulla ja tarjoajalla",
        notes = "",
        response = PictureDTO.class)
    public PictureDTO getPicture(
            @ApiParam(value = "Kuvan id")  @PathParam("id") final String id);
}
