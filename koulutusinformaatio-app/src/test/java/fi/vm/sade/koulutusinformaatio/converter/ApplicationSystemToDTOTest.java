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
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationSystemToDTOTest {

    @Test
    public void testOngoingTrue() {
        ApplicationSystem as = new ApplicationSystem();
        as.setId("1234");
        Map<String, String> translations = Maps.newHashMap();
        translations.put("fi", "Yhteishaku");
        as.setName(new I18nText(translations));
        Calendar calStart = new GregorianCalendar();
        calStart.set(Calendar.YEAR, 2000);
        calStart.set(Calendar.MONTH, Calendar.JANUARY);
        calStart.set(Calendar.DATE, 1);
        Calendar calEnd = new GregorianCalendar();
        calEnd.set(Calendar.YEAR, 2030);
        calEnd.set(Calendar.MONTH, Calendar.JANUARY);
        calEnd.set(Calendar.DATE, 1);
        DateRange dr = new DateRange(calStart.getTime(), calEnd.getTime());
        as.setApplicationDates(Lists.newArrayList(dr));
        ApplicationSystemDTO dto = ApplicationSystemToDTO.convert(as, "fi");
        assertEquals(as.getId(), dto.getId());
        assertEquals(as.getName().getTranslations().get("fi"), dto.getName());
        assertEquals(true, dto.isAsOngoing());
    }

    @Test
    public void testOngoingFalse() {
        ApplicationSystem as = new ApplicationSystem();
        as.setId("1234");
        Map<String, String> translations = Maps.newHashMap();
        translations.put("fi", "Yhteishaku");
        as.setName(new I18nText(translations));
        Calendar calStart = new GregorianCalendar();
        calStart.set(Calendar.YEAR, 2000);
        calStart.set(Calendar.MONTH, Calendar.JANUARY);
        calStart.set(Calendar.DATE, 1);
        Calendar calEnd = new GregorianCalendar();
        calEnd.set(Calendar.YEAR, 2010);
        calEnd.set(Calendar.MONTH, Calendar.JANUARY);
        calEnd.set(Calendar.DATE, 1);
        DateRange dr = new DateRange(calStart.getTime(), calEnd.getTime());
        as.setApplicationDates(Lists.newArrayList(dr));
        ApplicationSystemDTO dto = ApplicationSystemToDTO.convert(as, "fi");
        assertEquals(as.getId(), dto.getId());
        assertEquals(as.getName().getTranslations().get("fi"), dto.getName());
        assertEquals(false, dto.isAsOngoing());
    }
}
