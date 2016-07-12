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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import fi.vm.sade.koulutusinformaatio.configuration.UrlConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;

import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityProviderService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;

/**
 * @author Hannu Lyytikainen
 */
public class DirectoryResourceTest {

    DirectoryResource resource;

    @Before
    public void init() throws SearchException, ResourceNotFoundException {
        LearningOpportunityProviderService providerService = mock(LearningOpportunityProviderService.class);
        when(providerService.getProviderNameFirstCharacters(eq("fi")))
                .thenReturn(Lists.newArrayList("A", "B", "C"));
        ProviderSearchResultDTO psr1 = new ProviderSearchResultDTO();
        psr1.setId("1.2.3");
        psr1.setName("provider search result 1");
        ProviderSearchResultDTO psr2 = new ProviderSearchResultDTO();
        psr2.setId("2.3.4");
        psr2.setName("provider search result 2");
        when(providerService.searchProviders(anyString(), eq("fi"), anyString())).thenReturn(Lists.newArrayList(psr1, psr2));

        LearningOpportunityService learningOpportunityService = mock(LearningOpportunityService.class);
        LearningOpportunitySearchResultDTO losr = new LearningOpportunitySearchResultDTO();
        losr.setId("3.4.5");
        losr.setName("learning opportuinity name");
        when(learningOpportunityService.findLearningOpportunitiesByProviderId(eq("4.5.6"), eq("fi")))
                .thenReturn(Lists.newArrayList(losr));

        LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
        provider.setId("4.5.6");
        provider.setName("provider name");
        when(providerService.getProvider(eq("4.5.6"), eq("fi")))
                .thenReturn(provider);

        resource = new DirectoryResource(learningOpportunityService, providerService, new UrlConfiguration().addOverride("host.oppija", "localhost:9012"));
    }

    @Test
    public void testGetProviders() throws URISyntaxException {
        Response response = resource.getProviders("fi");
        assertNotNull(response);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetProvidersWithFirstLetter() throws URISyntaxException {
        Response response = resource.getProvidersWithFirstLetter("fi", "A", "10", null);
        assertNotNull(response);
        Viewable view = ((Viewable) response.getEntity());
        Map<String, Object> model = (HashMap<String, Object>) view.getModel();
        List<ProviderSearchResultDTO> providers = (List<ProviderSearchResultDTO>) model.get("providers");
        assertNotNull(providers);
        assertEquals(2, providers.size());
        assertEquals("1.2.3", providers.get(0).getId());
        assertEquals("provider search result 1", providers.get(0).getName());
        assertEquals("2.3.4", providers.get(1).getId());
        assertEquals("provider search result 2", providers.get(1).getName());
        assertNotNull(model.get("alphabets"));
        assertEquals("A", model.get("letter"));
        assertEquals("https://localhost:9012/app/#!/", model.get("ngBaseUrl"));
        assertEquals("fi", model.get("lang"));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetProvidersInvalidCharacter() throws URISyntaxException {
        Response response = resource.getProvidersWithFirstLetter("fi", "?", "00", null);
        assertNotNull(response);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLearningOpportunities() {
        Viewable view = resource.getLearningOpportunities("fi", "4.5.6");
        assertNotNull(view);
        Map<String, Object> model = (HashMap<String, Object>)view.getModel();
        assertNotNull(model);
        assertNotNull(model.get("alphabets"));
        String providerName = (String) model.get("provider");
        assertNotNull(providerName);
        assertEquals("provider name", providerName);
        assertEquals("P", model.get("letter"));
        assertEquals("https://localhost:9012/app/#!/", model.get("ngBaseUrl"));
        assertEquals("fi", model.get("lang"));
    }

}
