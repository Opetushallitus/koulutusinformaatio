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
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ExamEvent;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamEventDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class ExamEventToDTOTest {

    ExamEvent examEvent;
    Date start = new Date();
    Date end = new Date();

    @Before
    public void init() {
        examEvent = new ExamEvent();
        examEvent.setStart(start);
        examEvent.setEnd(end);
        examEvent.setAddress(new Address());
        examEvent.setDescription("description");
    }

    @Test
    public void testConvert() {
        ExamEventDTO dto = ExamEventToDTO.convert(examEvent, "fi");
        verifyDTO(dto);
    }

    @Test
    public void testConvertNull() {
        examEvent = null;
        assertNull(ExamEventToDTO.convert(examEvent, "fi"));
    }

    @Test
    public void testConvertAll() {
        List<ExamEventDTO> dtos = ExamEventToDTO.convertAll(Lists.newArrayList(examEvent), "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        verifyDTO(dtos.get(0));
    }

    @Test
    public void testConvertAllNull() {
        List<ExamEvent> events = null;
        assertNull(ExamEventToDTO.convertAll(events, ""));
    }

    private void verifyDTO(ExamEventDTO dto) {
        assertNotNull(dto);
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertNotNull(dto.getAddress());
        assertEquals("description", dto.getDescription());

    }
}
