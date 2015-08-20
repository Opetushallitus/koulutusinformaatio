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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOptionAttachment;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionAttachmentToDTOTest {

    @Test
    public void testConvert() {
        ApplicationOptionAttachment aoa = new ApplicationOptionAttachment();
        Date due = new Date();
        aoa.setDueDate(due);
        aoa.setAddress(new Address());
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", "address");
        aoa.getAddress().setStreetAddress(new I18nText(translations ));
        Map<String, String> descriptionTranslations = Maps.newHashMap();
        descriptionTranslations.put("fi", "description");
        aoa.setDescreption(new I18nText(descriptionTranslations));
        Map<String, String> typeTranslations = Maps.newHashMap();
        typeTranslations.put("fi", "attachmentType");
        aoa.setType(new I18nText(typeTranslations));

        ApplicationOptionAttachmentDTO dto = ApplicationOptionAttachmentToDTO.convert(aoa, "fi");
        assertNotNull(dto);
        assertEquals(due, dto.getDueDate());
        assertNotNull(dto.getAddress());
        assertNull(dto.getAddress().getPostalCode());
        assertNull(dto.getAddress().getPostOffice());
        assertNull(dto.getAddress().getStreetAddress2());
        assertEquals("address", dto.getAddress().getStreetAddress());
        assertEquals("description", dto.getDescreption());
        assertEquals("attachmentType", dto.getType());
    }

    @Test
    public void testConvertNull() {
        assertNull(ApplicationOptionAttachmentToDTO.convert(null, ""));
    }

    @Test
    public void testConvertAll() {
        List<ApplicationOptionAttachment> aoas = Lists.newArrayList(
                new ApplicationOptionAttachment(),
                new ApplicationOptionAttachment(),
                new ApplicationOptionAttachment()
        );
        aoas.get(0).setType(new I18nText());
        aoas.get(1).setType(new I18nText());
        aoas.get(2).setType(new I18nText());
        List<ApplicationOptionAttachmentDTO> dtos = ApplicationOptionAttachmentToDTO.convertAll(aoas, "fi");
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
    }
    
    @Test
    public void testConvertAllNull() {
        assertNull(ApplicationOptionAttachmentToDTO.convertAll(null, ""));
    }
    
    private ApplicationOptionAttachment createAttachment(String lang) {
        ApplicationOptionAttachment aoa = new ApplicationOptionAttachment();
        Map<String, String> typeTranslations = Maps.newHashMap();
        typeTranslations.put(lang, "attachmentType");
        aoa.setType(new I18nText(typeTranslations));
        
        return aoa;
    }
}
