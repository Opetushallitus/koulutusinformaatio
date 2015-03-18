/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.view.Viewable;

import fi.vm.sade.koulutusinformaatio.domain.dto.CodeDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityProviderService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.util.ResourceBundleHelper;

/**
 * Handles directory calls that lists provider and learning opportunity links
 * for crawlers.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/{lang}/hakemisto")
public class DirectoryResource {

    @Context
    UriInfo uri;

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final List<String> alphabets = Lists.newArrayList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Å", "Ä", "Ö");

    private LearningOpportunityService learningOpportunityService;
    private LearningOpportunityProviderService learningOpportunityProviderService;
    private String ngBaseUrl;
    private ResourceBundleHelper resourceBundleHelper;

    @Autowired
    public DirectoryResource(LearningOpportunityService learningOpportunityService,
                             LearningOpportunityProviderService learningOpportunityProviderService,
                             @Value("${koulutusinformaatio.baseurl.learningopportunity}") String ngBaseUrl) {
        this.learningOpportunityService = learningOpportunityService;
        this.learningOpportunityProviderService = learningOpportunityProviderService;
        this.ngBaseUrl = ngBaseUrl;
        this.resourceBundleHelper = new ResourceBundleHelper();
    }

    @GET
    @Path("oppilaitokset")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getProviders(@PathParam("lang") String lang) throws URISyntaxException {
        return Response.seeOther(new URI(String.format("%s/hakemisto/oppilaitokset/A", lang))).build();
    }


    @GET
    @Path("oppilaitokset/{letter}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getProvidersWithFirstLetter(@PathParam("lang") String lang,
                                                @PathParam("letter") String letter
                                                ) throws URISyntaxException {
        List<CodeDTO> types = null;
        try {
            types = learningOpportunityProviderService.getProviderTypes(letter, lang);
        } catch (Exception e) {
            return buildInternalError(lang);
        }

        Map<String, Object> model = initModel(lang);
        String type = types.get(0).getValue();
        String canonical = String.format("%s%s/hakemisto/oppilaitokset/%s/%s", uri.getBaseUri(), lang, letter, type);

        return getProvidersWithFirstLetter(lang, letter, type, canonical);
    }

    @GET
    @Path("oppilaitokset/{letter}/{type}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getProvidersWithFirstLetter(@PathParam("lang") String lang,
                                                @PathParam("letter") String letter,
                                                @PathParam("type") String type, String canonical) throws URISyntaxException {
        Map<String, Object> model = initModel(lang);
        model.put("lang", lang);
        model.put("ngBaseUrl", ngBaseUrl);
        model.put("letter", letter);
        if (canonical != null && !canonical.isEmpty()) {
            model.put("canonical", canonical);
        }
        List<String> characters = null;
        List<CodeDTO> types = null;
        try {
            characters = learningOpportunityProviderService.getProviderNameFirstCharacters(lang);
            types = learningOpportunityProviderService.getProviderTypes(letter, lang);
        } catch (Exception e) {
            return buildInternalError(lang);
        }

        if (type == null && !types.isEmpty()) {
            type = types.get(0).getValue();
        }

        if (alphabets.contains(letter)) {
            List<ProviderSearchResultDTO> providers = null;
            try {
                providers = learningOpportunityProviderService.searchProviders(letter, lang, type);
            } catch (Exception e) {
                return buildInternalError(lang);
            }

            model.put("providers", providers);
            model.put("alphabets", alphabets);
            model.put("validCharacters", characters);
            model.put("providerTypes", types);
            model.put("selectedProviderType", type);
            return Response.status(Response.Status.OK).entity(new Viewable("/providers.ftl", model)).build();
        } else {
            return getProviders(lang);
        }
    }

    @GET
    @Path("koulutukset/{providerId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getLearningOpportunities(@PathParam("lang") String lang,
                                             @PathParam("providerId") final String providerId) {
        Map<String, Object> model = initModel(lang);
        model.put("ngBaseUrl", ngBaseUrl);
        model.put("lang", lang);
        List<LearningOpportunitySearchResultDTO> resultList = null;
        LearningOpportunityProviderDTO provider = null;
        resultList = learningOpportunityService.findLearningOpportunitiesByProviderId(providerId, lang);
        List<String> characters = null;
        try {
            characters = learningOpportunityProviderService.getProviderNameFirstCharacters(lang);
        } catch (Exception e) {
            return new Viewable("/error.ftl", model);
        }
        try {
            provider = learningOpportunityProviderService.getProvider(providerId, lang);
        } catch (Exception e) {
            return new Viewable("/error.ftl", model);
        }
        model.put("letter", provider.getName().substring(0, 1).toUpperCase());
        model.put("alphabets", alphabets);
        model.put("validCharacters", characters);
        model.put("provider", provider.getName());
        model.put("learningOpportunities", resultList);
        return new Viewable("/education.ftl", model);
    }

    private Map<String, Object> initModel(String lang) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("messages", resourceBundleHelper.getBundle(lang));
        if (uri != null) {
            model.put("baseUrl", uri.getBaseUri());
        }
        return model;
    }

    private Response buildInternalError(String lang) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Viewable("/error.ftl", initModel(lang))).build();
    }
}
