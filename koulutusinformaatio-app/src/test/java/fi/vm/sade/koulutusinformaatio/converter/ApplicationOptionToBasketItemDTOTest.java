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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionToBasketItemDTOTest {

    ApplicationOption ao;
    Date attachmentDeadline = new Date();
    DateRange applicationSystemDates = new DateRange(new Date(), new Date());

    @Before
    public void init() {
        ao = new ApplicationOption();
        ao.setId("1.2.3");
        ao.setType("aoType");
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "aoName");
        ao.setName(new I18nText(nameTranslations));
        ao.setEducationDegree("100");
        ao.setSora(false);
        ao.setTeachingLanguages(Lists.newArrayList("fi"));
        ao.setParent(new ParentLOSRef());
        ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
        ao.setAttachmentDeliveryDeadline(attachmentDeadline);
        ao.setAttachments(new ArrayList<ApplicationOptionAttachment>());
        ao.setExams(new ArrayList<Exam>());
        ao.setAoIdentifier("123");
        ao.setKaksoistutkinto(false);
        ao.setVocational(true);
        ao.setEducationCodeUri("educationCodeUri");
        ao.setEducationTypeUri("educationTypeUri");
        ao.setPrerequisite(new Code());

        Provider p = new Provider();
        p.setId("2.3.4");
        Map<String, String> providerNameTranslations = Maps.newHashMap();
        providerNameTranslations.put("fi", "providerName");
        p.setName(new I18nText(providerNameTranslations));
        p.setAthleteEducation(true);
        ao.setProvider(p);

        ApplicationSystem as = new ApplicationSystem();
        as.setId("3.4.5");
        as.setMaxApplications(5);
        Map<String, String> asNameTranslations = Maps.newHashMap();
        asNameTranslations.put("fi", "asName");
        asNameTranslations.put("sv", "asNameSv");
        as.setName(new I18nText(asNameTranslations));
        as.setApplicationDates(Lists.newArrayList(applicationSystemDates));
        ao.setApplicationSystem(as);

    }

    @Test
    public void testConvert() {
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        List<BasketItemDTO> basketItems = ApplicationOptionToBasketItemDTO.convert(aos, "fi");
        assertNotNull(basketItems);
        assertEquals(1, basketItems.size());
        BasketItemDTO itemDTO = basketItems.get(0);
        assertEquals("3.4.5", itemDTO.getApplicationSystemId());
        assertEquals("asName", itemDTO.getApplicationSystemName());
        assertEquals(5, itemDTO.getMaxApplicationOptions());
        assertNotNull(itemDTO.getApplicationDates());
        assertFalse(itemDTO.isAsOngoing());
        assertNotNull(itemDTO.getApplicationOptions());
        assertEquals(1, itemDTO.getApplicationOptions().size());
        BasketApplicationOptionDTO baoDTO = itemDTO.getApplicationOptions().get(0);
        assertNotNull(baoDTO);
        assertEquals("1.2.3", baoDTO.getId());
        assertEquals("aoType", baoDTO.getType());
        assertEquals("aoName", baoDTO.getName());
        assertEquals("100", baoDTO.getEducationDegree());
        assertFalse(baoDTO.isSora());
        assertEquals("fi", baoDTO.getTeachingLanguages().get(0));
        assertNotNull(baoDTO.getParent());
        assertNotNull(baoDTO.getChildren());
        assertEquals(attachmentDeadline, baoDTO.getAttachmentDeliveryDeadline());
        //assertNotNull(baoDTO.getAttachments());
        assertNotNull(baoDTO.getExams());
        assertEquals("123", baoDTO.getAoIdentifier());
        assertFalse(baoDTO.isKaksoistutkinto());
        assertTrue(baoDTO.isVocational());
        assertEquals("educationCodeUri", baoDTO.getEducationCodeUri());
        assertEquals("educationTypeUri", baoDTO.getEducationTypeUri());
        assertNotNull(baoDTO.getPrerequisite());
        assertFalse(baoDTO.isHigherEducation());
        assertEquals("2.3.4", baoDTO.getProviderId());
        assertTrue(baoDTO.isAthleteEducation());
        assertEquals("providerName", baoDTO.getProviderName());
    }

    public void testHigherEducation() {
        ParentLOSRef parent = new ParentLOSRef();
        parent.setLosType(TarjontaConstants.TYPE_KK);
        ao.setParent(parent);
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        List<BasketItemDTO> basketItems = ApplicationOptionToBasketItemDTO.convert(aos, "sv");
        BasketApplicationOptionDTO baoDTO = basketItems.get(0).getApplicationOptions().get(0);
        assertTrue(baoDTO.isHigherEducation());
    }

    public void testUILang() {
        List<ApplicationOption> aos = Lists.newArrayList(ao);
        List<BasketItemDTO> basketItems = ApplicationOptionToBasketItemDTO.convert(aos, "sv");
        assertEquals("asNameSv", basketItems.get(0).getApplicationSystemName());
    }
}
