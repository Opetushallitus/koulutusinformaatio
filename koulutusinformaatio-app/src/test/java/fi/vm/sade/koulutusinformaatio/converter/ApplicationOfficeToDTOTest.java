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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOffice;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOfficeToDTOTest {

    @Test
    public void testConvert() {
        ApplicationOffice office = new ApplicationOffice();
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "officeName");
        office.setName(new I18nText(nameTranslations));
        office.setEmail("application@office.com");
        office.setPhone("55512345");
        office.setPostalAddress(new Address());
        office.setVisitingAddress(new Address());
        office.setWww("office.com");

        ApplicationOfficeDTO dto = ApplicationOfficeToDTO.convert(office, "fi");
        assertNotNull(dto);
        assertEquals("officeName", dto.getName());
        assertEquals("application@office.com", dto.getEmail());
        assertEquals("55512345", dto.getPhone());
        assertNotNull(dto.getPostalAddress());
        assertNotNull(dto.getVisitingAddress());
        assertEquals("office.com", dto.getWww());
    }

    @Test
    public void testConvertNull() {
        assertNull(ApplicationOfficeToDTO.convert(null, ""));
    }
}
