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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

/**
 * @author Hannu Lyytikainen
 */
public class CodeToNameTest {

    @Test
    public void testConvert() {
        Code code = new Code();
        code.setValue("codevalue");
        Map<String, String> translations = Maps.newHashMap();
        translations.put("fi", "nameFi");
        translations.put("sv", "nameSv");
        I18nText name = new I18nText(translations);
        code.setName(name);

        String nameFi = CodeToName.convert(code, "fi");
        String nameSv = CodeToName.convert(code, "sv");
        assertEquals("nameFi", nameFi);
        assertEquals("nameSv", nameSv);
    }

    @Test
    public void testConvertAll() {
        Code code = new Code();
        code.setValue("codevalue");
        Code code2 = new Code();
        code2.setValue("codevalue2");
        Map<String, String> translations = Maps.newHashMap();
        translations.put("fi", "nameFi");
        translations.put("sv", "nameSv");
        I18nText name = new I18nText(translations);
        code.setName(name);
        code2.setName(name);
        List<String> names = CodeToName.convertAll(Lists.newArrayList(code, code2), "fi");

        assertNotNull(names);
        assertEquals(2, names.size());
    }
}
