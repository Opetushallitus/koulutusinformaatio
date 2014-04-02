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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.CodeDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class CodeToDTOTest {

    Code code;

    @Before
    public void init() {
        code = new Code();
        code.setValue("value");
        code.setUri("uri");
        Map<String, String> name = Maps.newHashMap();
        name.put("fi", "name fi");
        code.setName(new I18nText(name));
        Map<String, String> description = Maps.newHashMap();
        description.put("fi", "description fi");
        code.setDescription(new I18nText(description));
    }

    @Test
    public void testConvert() {
        CodeDTO dto = CodeToDTO.convert(code, "fi");
        assertNotNull(dto);
        assertEquals("value", dto.getValue());
        assertEquals("uri", dto.getUri());
        assertEquals("name fi", dto.getName());
        assertEquals("description fi", dto.getDescription());
    }

    @Test
    public void testConvertNull() {
        assertNull(CodeToDTO.convert(null, ""));
    }

    @Test
    public void testConvertAll() {
        List<Code> codes = Lists.newArrayList(code);
        List<CodeDTO> dtos = CodeToDTO.convertAll(codes, "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    public void testConvertAllWithNullList() {
        assertNull(CodeToDTO.convertAll(null, ""));
    }

}
