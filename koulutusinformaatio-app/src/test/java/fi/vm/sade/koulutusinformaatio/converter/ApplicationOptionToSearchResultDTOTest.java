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

package fi.vm.sade.koulutusinformaatio.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionToSearchResultDTOTest {

    ApplicationOption ao;

    @Before
    public void init() {
        ao = new ApplicationOption();
        ao.setId("1.2.3");
        Map<String, String> aoNameTranslations = Maps.newHashMap();
        aoNameTranslations.put("fi", "aoNameFi");
        ao.setName(new I18nText(aoNameTranslations));
        ao.setAoIdentifier("123");
        ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
        ao.setSora(true);
        ao.setTeachingLanguages(Lists.newArrayList("fi"));
        ao.setEducationDegree("32");
        ao.setKaksoistutkinto(false);
        ao.setVocational(true);
        ao.setEducationCodeUri("educationCodeUri");
        ao.setAthleteEducation(true);
    }

    @Test
    public void testConvert() {
        ApplicationOptionSearchResultDTO dto = ApplicationOptionToSearchResultDTO.convert(ao, "fi", "fi");
        assertNotNull(dto);
        assertEquals("1.2.3", dto.getId());
        assertEquals("aoNameFi", dto.getName());
        assertNotNull(dto.getChildLONames());
        assertEquals("32", dto.getEducationDegree());
        assertTrue(dto.isSora());
        assertNotNull(dto.getTeachingLanguages());
        assertEquals(1, dto.getTeachingLanguages().size());
        assertEquals("fi", dto.getTeachingLanguages().get(0));
        assertFalse(dto.isKaksoistutkinto());
        assertTrue(dto.isVocational());
        assertEquals("educationCodeUri", dto.getEducationCodeUri());
    }

    @Test
    public void testFallbackName() {
        Map<String, String> aoNameTranslations = Maps.newHashMap();
        aoNameTranslations.put("sv", "aoNameSv");
        ao.setName(new I18nText(aoNameTranslations));
        ApplicationOptionSearchResultDTO dto = ApplicationOptionToSearchResultDTO.convert(ao, "fi", "sv");
        assertEquals("aoNameSv", dto.getName());
    }

    @Test
    public void testAthleticProvider() {
        Provider p = new Provider();
        p.setAthleteEducation(true);
        ao.setProvider(p);
        ao.setAthleteEducation(false);
        ApplicationOptionSearchResultDTO dto = ApplicationOptionToSearchResultDTO.convert(ao, "fi", "fi");
        assertTrue(dto.isAthleteEducation());
    }

    @Test
    public void testNull() {
        assertNull(ApplicationOptionToSearchResultDTO.convert(null, "fi", "fi"));
    }


}
