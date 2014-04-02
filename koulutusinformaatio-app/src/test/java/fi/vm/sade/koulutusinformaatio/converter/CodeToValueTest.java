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
import fi.vm.sade.koulutusinformaatio.domain.Code;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class CodeToValueTest {

    Code code;

    @Before
    public void init() {
        code = new Code();
        code.setValue("codeValue");
    }

    @Test
    public void testConvert() {
        String value = CodeToValue.convert(code);
        assertNotNull(value);
        assertEquals("codeValue", value);
    }

    @Test
    public void testConvertNull() {
        assertNull(CodeToValue.convert(null));
    }

    @Test
    public void testConvertAll() {
        List<Code> codes = Lists.newArrayList(code);
        List<String> values = CodeToValue.convertAll(codes);
        assertNotNull(values);
        assertEquals(1, values.size());
    }

    @Test
    public void testConvertAllWithNullList() {
        assertNull(CodeToValue.convertAll(null));
    }
}
