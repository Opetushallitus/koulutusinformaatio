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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Hannu Lyytikainen
 */
public class ChildLOSToDTOTest {

    @Test
    public void testConvert() {
        ChildLOS los = new ChildLOS();
        los.setId("los id");
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "los name fi");
        los.setName(new I18nText(nameTranslations));
        Map<String, String> qualificationTranslations = Maps.newHashMap();
        qualificationTranslations.put("fi", "qualification fi");
        los.setQualification(new I18nText(qualificationTranslations));
        los.setLois(new ArrayList<ChildLOI>());
        los.setParent(new ParentLOSRef());
        Map<String, String> goalsTranslations = Maps.newHashMap();
        goalsTranslations.put("fi", "goals fi");
        goalsTranslations.put("en", "goals en");
        los.setGoals(new I18nText(goalsTranslations));

        ChildLearningOpportunitySpecificationDTO dto = ChildLOSToDTO.convert(los, "en", "fi", "fi");
        assertNotNull(dto);
        assertEquals("los id", dto.getId());
        assertEquals("los name fi", dto.getName());
        assertEquals("qualification fi", dto.getQualification());
        assertNotNull(dto.getLois());
        assertNotNull(dto.getParent());
        assertEquals("goals en", dto.getGoals());
        assertEquals("en", dto.getTranslationLanguage());
    }

    @Test
    public void testConvertNull() {
        assertNull(ChildLOSToDTO.convert(null, "", "", ""));
    }
}
