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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOffice;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOfficeToDTOTest {

    @Test
    public void testConvert() {
        Provider prov = new Provider();
        ApplicationOffice office = new ApplicationOffice();
        prov.setApplicationOffice(office);
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "officeName");
        office.setName(new I18nText(nameTranslations));
        office.setEmail(createI18Text("application@office.com"));
        office.setPhone(createI18Text("55512345"));
        office.setPostalAddress(new Address());
        office.setVisitingAddress(new Address());
        office.setWww(createI18Text("office.com"));

        ApplicationOfficeDTO dto = ApplicationOfficeToDTO.convert(prov, "fi");
        assertNotNull(dto);
        assertEquals("officeName", dto.getName());
        assertEquals("application@office.com", dto.getEmail());
        assertEquals("55512345", dto.getPhone());
        assertNull(dto.getPostalAddress());
        assertNull(dto.getVisitingAddress());
        assertEquals("office.com", dto.getWww());
    }

    @Test
    public void testConvertNull() {
        Provider prov = new Provider();
        assertNull(ApplicationOfficeToDTO.convert(prov, ""));
    }
    
    
    private I18nText createI18Text(String text) {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", text);
        translations.put("sv", text);
        translations.put("en", text);
        return new I18nText(translations);
    }
}
