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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityProviderService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityProviderServiceImplTest {

    private static final String PROVIDER_ID = "provider_id";

    LearningOpportunityProviderService service;

    @Before
    public void init() throws SearchException, ResourceNotFoundException {
        SearchService searchService = mock(SearchService.class);
        EducationDataQueryService educationDataQueryService = mock(EducationDataQueryService.class);
        when(searchService.getProviderFirstCharacterList(eq("fi")))
                .thenReturn(Lists.newArrayList("A", "B", "C"));
        Provider p = new Provider();
        p.setId(PROVIDER_ID);
        Map<String, String> name = Maps.newHashMap();
        name.put("fi", "provider name fi");
        p.setName(new I18nText(name));
        when(educationDataQueryService.getProvider(PROVIDER_ID)).thenReturn(p);
        service = new LearningOpportunityProviderServiceImpl(searchService, educationDataQueryService);
    }

    @Test
    public void testGetProviderNameFirstCharacters() throws SearchException {
        List<String> characters = service.getProviderNameFirstCharacters("fi");
        assertNotNull(characters);
        assertEquals(3, characters.size());
        assertTrue(characters.contains("A"));
        assertTrue(characters.contains("B"));
        assertTrue(characters.contains("C"));
        assertFalse(characters.contains("D"));
    }

    @Test
    public void testGetProvider() throws ResourceNotFoundException {
        LearningOpportunityProviderDTO dto = service.getProvider(PROVIDER_ID, "fi");
        assertNotNull(dto);
        assertEquals(PROVIDER_ID, dto.getId());
        assertEquals("provider name fi", dto.getName());
    }
}
