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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSRefDTO;

/**
 * @author Hannu Lyytikainen
 */
public class HigherEducationLOSRefToDTOTest {

    HigherEducationLOSRef ref;

    @Before
    public void inti() {
        ref = new HigherEducationLOSRef();
        ref.setId("ref id");
        ref.setPrerequisite(new Code());
        I18nText qualification = new I18nText();
        qualification.put("fi", "qualification fi");
        ref.setQualifications(Lists.newArrayList(qualification));
        I18nText name = new I18nText();
        name.put("fi", "ref name fi");
        ref.setName(name);
    }

    @Test
    public void testConvert() {
        HigherEducationLOSRefDTO dto = HigherEducationLOSRefToDTO.convert(ref, "fi");
        validateDTO(dto);
    }

    @Test
    public void testConvertAll() {
        List<HigherEducationLOSRefDTO> dtos =
                HigherEducationLOSRefToDTO.convert(Lists.newArrayList(ref), "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        validateDTO(dtos.get(0));
    }

    @Test
    public void testConvertAllToI18nText() {
        List<I18nText> texts =
                HigherEducationLOSRefToDTO.convert(Lists.newArrayList(ref));
        assertNotNull(texts);
        assertEquals(1, texts.size());
        I18nText text = texts.get(0);
        assertNotNull(text);
        assertEquals("ref name fi", text.get("fi"));
    }

    @Test
    public void testConvertAllToI18nTextWithNull() {
        List<HigherEducationLOSRef> refs = null;
        assertNull(HigherEducationLOSRefToDTO.convert(refs));
    }

    private void validateDTO(HigherEducationLOSRefDTO dto) {
        assertNotNull(dto);
        assertEquals("ref id", dto.getId());
        assertNotNull(ref.getPrerequisite());
        assertNotNull(dto.getQualifications());
        assertEquals(1, dto.getQualifications().size());
        assertEquals("qualification fi", dto.getQualifications().get(0));
        assertEquals("ref name fi", dto.getName());
    }

}
