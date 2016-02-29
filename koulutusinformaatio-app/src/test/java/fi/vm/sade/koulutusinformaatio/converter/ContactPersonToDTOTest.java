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

import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.dto.ContactPersonDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ContactPersonToDTOTest {

    ContactPerson cp;

    @Before
    public void init() {
        cp = new ContactPerson();
        cp.setName("first names lastname");
        cp.setEmail("email address");
        cp.setPhone("phone number");
        cp.setTitle("person title");
        cp.setType("person type");
    }

    @Test
    public void testConvert() {
        ContactPersonDTO dto = ContactPersonToDTO.convert(cp);
        assertNotNull(dto);
        assertEquals("first names lastname", dto.getName());
        assertEquals("email address", dto.getEmail());
        assertEquals("phone number", dto.getPhone());
        assertEquals("person title", dto.getTitle());
        assertEquals("person type", dto.getType());
    }

    @Test
    public void testConvertNull() {
        assertNull(ContactPersonToDTO.convert(null));
    }

    @Test
    public void testConvertAll() {
        List<ContactPerson> cps = Lists.newArrayList(cp);
        List<ContactPersonDTO> dtos = ContactPersonToDTO.convertAll(cps);
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    public void testConvertAllWithNullList() {
        assertNull(ContactPersonToDTO.convertAll(null));
    }

}
