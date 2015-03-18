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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.dto.DateRangeDTO;

/**
 * @author Hannu Lyytikainen
 */
public class DateRangeToDTOTest {

    DateRange dateRange;
    Date start;
    Date end;

    @Before
    public void init() {
        dateRange = new DateRange();
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, Calendar.FEBRUARY);
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, Calendar.MARCH);
        start = startCal.getTime();
        end = endCal.getTime();
        dateRange.setStartDate(start);
        dateRange.setEndDate(end);
    }

    @Test
    public void testConvert() {
        DateRangeDTO dto = DateRangeToDTO.convert(dateRange);
        assertNotNull(dto);
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getEndDate());
    }

    @Test
    public void testConvertNull() {
        dateRange = null;
        assertNull(DateRangeToDTO.convert(dateRange));
    }

    @Test
    public void testConvertList() {
        List<DateRangeDTO> dtos = DateRangeToDTO.convert(Lists.newArrayList(dateRange));
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(start, dtos.get(0).getStartDate());
        assertEquals(end, dtos.get(0).getEndDate());
    }

    @Test
    public void testConvertListNull() {
        List<DateRange> dateRanges = null;
        assertNull(DateRangeToDTO.convert(dateRanges));
    }
}
