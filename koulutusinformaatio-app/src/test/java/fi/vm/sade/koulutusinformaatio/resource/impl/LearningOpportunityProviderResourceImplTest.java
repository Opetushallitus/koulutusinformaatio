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
package fi.vm.sade.koulutusinformaatio.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.domain.AoSolrSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.resource.LearningOpportunityProviderResource;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

/**
 * 
 * @author Markus
 */
public class LearningOpportunityProviderResourceImplTest {
    
    private SearchService searchService;
    private LearningOpportunityService learningOpportunityService;
    private KoodistoService koodistoService;
    LearningOpportunityProviderResource providerResource;

    @Before
    public void init() throws SearchException, ResourceNotFoundException {
        
        searchService = mock(SearchService.class);
        learningOpportunityService = mock(LearningOpportunityService.class);
        koodistoService = mock(KoodistoService.class);
        
        List<Provider> providers = new ArrayList<Provider>();
        
        Provider provider = new Provider();
        provider.setId("prov111");
        provider.setAccessibility(createI18Text("accessibility"));
        provider.setDescription(createI18Text("description"));
        provider.setEmail(createI18Text("has@email.fi"));
        provider.setHomePlace(createI18Text("Vantaa"));
        provider.setName(createI18Text("prov111"));
        
        Provider provider2 = new Provider();
        provider2.setId("prov211");
        provider2.setAccessibility(createI18Text("accessibility 2"));
        provider2.setDescription(createI18Text("description 2"));
        provider2.setEmail(createI18Text("has@email.fi2"));
        provider2.setHomePlace(createI18Text("Vantaa 2"));
        provider2.setName(createI18Text("prov111 2"));
        
        providers.add(provider);
        providers.add(provider2);

        when(searchService.searchLearningOpportunityProviders("prov", "asID", Arrays.asList(""), true, true, 10, 10, "fi", false, null)).thenReturn(providers);
        when(searchService.searchLearningOpportunityProviders("prov* AND toinen", "asID", Arrays.asList(""), true, true, 10, 10, "fi", false, null)).thenReturn(
                providers.subList(1, 2));

        LOSearchResultList educations = new LOSearchResultList();
        List<LOSearchResult> results = new ArrayList<LOSearchResult>();
        LOSearchResult los = new LOSearchResult();
        los.setLopIds(Arrays.asList("prov211"));
        results.add(los);
        educations.setResults(results);

        when(searchService.searchLearningOpportunities("*", null, null, new ArrayList<String>(), new ArrayList<String>(),
                new ArrayList<String>(), "fi", true, false, false, 0, 9999999, null, null, null, null, null, "asID", SearchType.LO))
                .thenReturn(educations);

        PictureDTO pict = new PictureDTO();
        pict.setId("pict1");
        
        when(learningOpportunityService.getPicture("prov1")).thenReturn(pict);
        
        LearningOpportunityProviderDTO dto = new LearningOpportunityProviderDTO(); 
        dto.setId("prov111");
        
        when(learningOpportunityService.getProvider("prov111", "fi")).thenReturn(dto);
        
        providerResource = new LearningOpportunityProviderResourceImpl(searchService, null, learningOpportunityService, koodistoService);

        List<AoSolrSearchResult> aos = new ArrayList<AoSolrSearchResult>();
        AoSolrSearchResult ao = new AoSolrSearchResult(null, "prov211", null, null, null, null);
        aos.add(ao);
        when(searchService.searchOngoingApplicationOptions(eq("asID"), anyListOf(Provider.class), anyListOf(String.class))).thenReturn(aos);
        
    }
    
    /**
     * Tests the fetching of provider.
     */
    @Test
    public void testGetProvider() {
        LearningOpportunityProviderDTO dto = this.providerResource.getProvider("prov111", "fi");
        assertTrue(dto.getId().equals("prov111"));
    }

    @Test
    public void testSearchProviders() {
        List<ProviderSearchResultDTO> results = providerResource.searchProviders("prov", "asID", Arrays.asList(""), true, true, 10, 10, "fi", false, null);
        assertEquals(results.size(), 2);
        boolean foundOne = false;
        boolean foundTwo = false;
        for (ProviderSearchResultDTO resultDTO : results) {
            if (resultDTO.getId().equals("prov111")) {
                foundOne = true;
            }
            if (resultDTO.getId().equals("prov211")) {
                foundTwo = true;
            }

        }
        assertTrue("Provider prov111 not found", foundOne);
        assertTrue("Provider prov211 not found", foundTwo);
    }

    @Test
    public void testSearchProvidersWtihSpace() {
        List<ProviderSearchResultDTO> results = providerResource.searchProviders("prov toinen", "asID", Arrays.asList(""), true, true, 10, 10, "fi", false, null);
        assertEquals(results.size(), 1);
        boolean foundOne = false;
        boolean foundTwo = false;
        for (ProviderSearchResultDTO resultDTO : results) {
            if (resultDTO.getId().equals("prov111")) {
                foundOne = true;
            }
            if (resultDTO.getId().equals("prov211")) {
                foundTwo = true;
            }

        }
        assertFalse("Provider prov111 found", foundOne);
        assertTrue("Provider prov211 not found", foundTwo);
    }
    
    @Test
    public void testSearchOngoingProviders() {
        List<ProviderSearchResultDTO> results = providerResource.searchProviders("prov", "asID", Arrays.asList(""), true, true, 10, 10, "fi", true, null);
        assertEquals(results.size(), 1);
        boolean foundOne = false;
        boolean foundTwo = false;
        for (ProviderSearchResultDTO resultDTO : results) {
            if (resultDTO.getId().equals("prov111")) {
                foundOne = true;
            }
            if (resultDTO.getId().equals("prov211")) {
                foundTwo = true;
            }

        }
        assertFalse("Provider prov111 found", foundOne);
        assertTrue("Provider prov211 not found", foundTwo);
    }

    @Test
    public void testGetPicture() {
        PictureDTO result = providerResource.getProviderPicture("prov1");
        assertEquals(result.getId(), "pict1");
    }
    
    private I18nText createI18Text(String text) {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", text + " fi");
        translations.put("sv", text + " sv");
        translations.put("en", text + " en");
        return new I18nText(translations);
    }
    
}
