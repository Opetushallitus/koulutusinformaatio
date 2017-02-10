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
import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.ExamEvent;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ScoreLimit;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class ExamToDTOTest {

    Exam exam;

    @Before
    public void init() {
        exam = new Exam();
        exam.setExamEvents(new ArrayList<ExamEvent>());
        I18nText description = new I18nText();
        description.put("fi", "exam description");
        exam.setDescription(description);
        I18nText type = new I18nText();
        type.put("fi", "exam type");
        exam.setType(type);
        exam.setScoreLimit(new ScoreLimit());
    }

    @Test
    public void testConvert() {
        List<ExamDTO> dto = ExamToDTO.convertAll(Lists.newArrayList(exam), "fi");
        assertNotNull(dto);
        assertEquals(1, dto.size());
        verifyDTO(dto.get(0));
    }

    @Test
    public void testConvertNull() {
        assertNull(ExamToDTO.convertAll(new ArrayList<Exam>(), "fi"));
        assertNull(ExamToDTO.convertAll(Lists.newArrayList((Exam) null), "fi"));
    }

    @Test
    public void testConvertAll() {
        List<ExamDTO> dtos = ExamToDTO.convertAll(Lists.newArrayList(exam), "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        verifyDTO(dtos.get(0));
    }

    @Test
    public void testConvertAllNull() {
        assertNull(ExamToDTO.convertAll(null, "fi"));
        assertNull(ExamToDTO.convertAll(new ArrayList<Exam>(), "fi"));
    }

    private void verifyDTO(ExamDTO dto) {
        assertNotNull(dto);
        assertNotNull(dto.getExamEvents());
        assertEquals("exam description", dto.getDescription());
        assertEquals("exam type", dto.getType());
        assertNotNull(dto.getScoreLimit());
    }
}
