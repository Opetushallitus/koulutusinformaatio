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
import fi.vm.sade.koulutusinformaatio.domain.EmphasizedSubject;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.EmphasizedSubjectDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class EmphasizedSubjectToDTOTest {

    EmphasizedSubject emphasizedSubject;

    @Before
    public void inti() {
        emphasizedSubject = new EmphasizedSubject();
        I18nText subject = new I18nText();
        subject.put("fi", "subject fi");
        emphasizedSubject.setSubject(subject);
        emphasizedSubject.setValue("val string");
    }

    @Test
    public void testConvert() {
        EmphasizedSubjectDTO dto = EmphasizedSubjectToDTO.convert(emphasizedSubject, "fi");
        assertNotNull(dto);
        assertEquals("subject fi", dto.getSubject());
        assertEquals("val string", dto.getValue());
    }

    @Test
    public void testConvertNull() {
        emphasizedSubject = null;
        assertNull(EmphasizedSubjectToDTO.convert(emphasizedSubject, ""));
    }

    @Test
    public void testConvertAll() {
        List<EmphasizedSubjectDTO> dtos = EmphasizedSubjectToDTO.convertAll(Lists.newArrayList(emphasizedSubject), "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals("subject fi", dtos.get(0).getSubject());
        assertEquals("val string", dtos.get(0).getValue());
    }

    @Test
    public void testConvertAllNull() {
        List<EmphasizedSubject> nullList = null;
        assertNull(EmphasizedSubjectToDTO.convertAll(nullList, "fi"));
    }
}
