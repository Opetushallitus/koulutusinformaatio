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
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.DateRangeDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ConverterUtilTest {

    @Test
    public void testGetTextByLanguageFound() {
        I18nText i18n = createI18nText("localize");
        String text = ConverterUtil.getTextByLanguage(i18n, "fi");
        assertNotNull(text);
        assertEquals("localize", text);
    }

    @Test
    public void testGetTextByLanguageNotFound () {
        I18nText i18n = createI18nText("localize");
        assertNull(ConverterUtil.getTextByLanguage(i18n, "invalidlang"));
    }

    @Test
    public void testGetTextByLanguageUseFallbackLangLFound() {
        I18nText i18n = createI18nText("localize");
        String text = ConverterUtil.getTextByLanguageUseFallbackLang(i18n, "fi");
        assertNotNull(text);
        assertEquals("localize", text);
    }

    @Test
    public void testGetTextByLanguageUseFallbackLangLNotFound() {
        I18nText i18n = createI18nText("localize");
        String text = ConverterUtil.getTextByLanguageUseFallbackLang(i18n, "invalidlang");
        assertNotNull(text);
    }

    @Test
    public void testSortApplicationSystems() {
        ApplicationSystemDTO asFirst = new ApplicationSystemDTO();
        asFirst.setId("first");
        Calendar startCalFirst1 = new GregorianCalendar();
        startCalFirst1.set(Calendar.YEAR, 2000);
        Calendar endCalFirst1 = new GregorianCalendar();
        endCalFirst1.set(Calendar.YEAR, 2001);
        DateRangeDTO drFirst1 = new DateRangeDTO();
        drFirst1.setStartDate(startCalFirst1.getTime());
        drFirst1.setEndDate(endCalFirst1.getTime());
        asFirst.setApplicationDates(Lists.newArrayList(drFirst1));

        ApplicationSystemDTO asSecond = new ApplicationSystemDTO();
        asSecond.setId("second");
        Calendar startCalSecond1 = new GregorianCalendar();
        int year = startCalSecond1.get(Calendar.YEAR);
        startCalSecond1.set(Calendar.YEAR, year -1);
        Calendar endCalSecond1 = new GregorianCalendar();
        endCalSecond1.set(Calendar.YEAR, year +1);
        DateRangeDTO drSecond1 = new DateRangeDTO();
        drSecond1.setStartDate(startCalSecond1.getTime());
        drSecond1.setEndDate(endCalSecond1.getTime());
        asSecond.setApplicationDates(Lists.newArrayList(drSecond1));

        ApplicationSystemDTO asThird = new ApplicationSystemDTO();
        asThird.setId("third");
        Calendar startCalThird1 = new GregorianCalendar();
        startCalThird1.set(Calendar.YEAR, 2100);
        Calendar endCalThird1 = new GregorianCalendar();
        endCalThird1.set(Calendar.YEAR, 2101);
        DateRangeDTO drThird1 = new DateRangeDTO();
        drThird1.setStartDate(startCalThird1.getTime());
        drThird1.setEndDate(endCalThird1.getTime());
        asThird.setApplicationDates(Lists.newArrayList(drThird1));

        List<ApplicationSystemDTO> asDTOs = Lists.newArrayList(asFirst, asSecond, asThird);
        List<ApplicationSystemDTO> sorted = ConverterUtil.sortApplicationSystems(asDTOs);

        assertNotNull(sorted);
        assertEquals(3, sorted.size());
        assertEquals("second", sorted.get(0).getId());
        assertEquals("third", sorted.get(1).getId());
        assertEquals("first", sorted.get(2).getId());
    }

    @Test
    public void testRegex() {
        String regex = "^.*\\?_escaped_fragment_=.*$";
        Pattern pattern = Pattern.compile(regex);
        String url = "http://localhost:8080/koulutusinformaatio-app/app/?_escaped_fragment_=/tutkinto/1.2.246.562.5.2013111213020035175870_1.2.246.562.10.77155748245_PKYO#PK";
        assertTrue(pattern.matcher(url).matches());
    }

    private I18nText createI18nText(String fi) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        return new I18nText(values);
    }

}
