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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionResourceImplTest {

    private ApplicationOptionResourceImpl applicationOptionResource;
    private EducationDataService educationDataService;
    private final String asId = "1.2.3.4";
    private final String lopId = "5.6.7.8";

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        educationDataService = mock(EducationDataService.class);
        List<ApplicationOption> aos = new ArrayList<ApplicationOption>();

        ApplicationOption ao = new ApplicationOption();
        ao.setId("1.1.2");
        ao.setName(createI18nText("ao 1 fi", "ao 1 sv", "ao 1 en"));
        ao.setEducationDegree("degree 1");
        List<I18nText> cloNames = new ArrayList<I18nText>();
        cloNames.add(createI18nText("clo name 1 fi", "clo name 1 sv", "clo name 1 en"));
        cloNames.add(createI18nText("clo name 2 fi", "clo name 2 sv", "clo name 2 en"));
        ao.setChildLOINames(cloNames);

        ApplicationOption ao2 = new ApplicationOption();
        ao2.setId("2.3.2");
        ao2.setName(createI18nText("ao 2 fi", "ao 2 sv", "ao 2 en"));
        ao2.setEducationDegree("degree 2");

        aos.add(ao);
        aos.add(ao2);

        when(educationDataService.findApplicationOptions(eq(asId), eq(lopId))).thenReturn(aos);
        applicationOptionResource = new ApplicationOptionResourceImpl(educationDataService, modelMapper);
    }

    @Test
    public void testSearchApplicationOptions() {
        List<ApplicationOptionSearchResultDTO> result = applicationOptionResource.searchApplicationOptions(asId, lopId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1.1.2", result.get(0).getId());
        assertEquals("2.3.2", result.get(1).getId());
    }

    private I18nText createI18nText(String fi, String sv, String en) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        values.put("sv", sv);
        values.put("en", en);
        return new I18nText(values);
    }
}
