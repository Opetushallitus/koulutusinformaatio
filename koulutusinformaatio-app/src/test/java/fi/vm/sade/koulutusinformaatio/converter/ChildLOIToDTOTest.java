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
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityInstanceDTO;
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
public class ChildLOIToDTOTest {

    ChildLOI loi;
    Date startDate;

    @Before
    public void init() {
        loi = new ChildLOI();
        loi.setId("loi id");
        Map<String, String> tl = Maps.newHashMap();
        tl.put("en", "translation en");
        loi.setAvailableTranslationLanguages(Lists.newArrayList(
                new Code("val", new I18nText(tl), new I18nText(tl))));
        startDate = new Date();
        loi.setStartDate(startDate);
        loi.setTeachingLanguages(new ArrayList<Code>());
        loi.setRelated(new ArrayList<ChildLOIRef>());
        Map<String, String> formOfTeachingTranslations = Maps.newHashMap();
        formOfTeachingTranslations.put("en", "teaching form en");
        loi.setFormOfTeaching(Lists.newArrayList(new I18nText(formOfTeachingTranslations)));
        Map<String, String> weblinks = Maps.newHashMap();
        weblinks.put("link key", "link value");
        loi.setWebLinks(weblinks);
        Map<String, String> formOfEducationTranslations = Maps.newHashMap();
        formOfEducationTranslations.put("en", "education form en");
        loi.setFormOfEducation(Lists.newArrayList(new I18nText(formOfEducationTranslations)));
        Map<String, String> prerequisite = Maps.newHashMap();
        prerequisite.put("en", "prerequisite en");
        loi.setPrerequisite(new Code("val", new I18nText(prerequisite), new I18nText(prerequisite)));
        Map<String, String> titlesTranslations = Maps.newHashMap();
        titlesTranslations.put("fi", "professional title fi");
        loi.setProfessionalTitles(Lists.newArrayList(new I18nText(titlesTranslations)));
        Map<String, String> wlp = Maps.newHashMap();
        wlp.put("fi", "wlp fi");
        loi.setWorkingLifePlacement(new I18nText(wlp));
        Map<String, String> i18n = Maps.newHashMap();
        i18n.put("fi", "i18n fi");
        loi.setInternationalization(new I18nText(i18n));
        Map<String, String> coop = Maps.newHashMap();
        coop.put("fi", "coop fi");
        loi.setCooperation(new I18nText(coop));
        Map<String, String> content = Maps.newHashMap();
        content.put("fi", "content fi");
        loi.setContent(new I18nText(content));
        Map<String, String> sdp = Maps.newHashMap();
        sdp.put("fi", "sdp fi");
        loi.setSelectingDegreeProgram(new I18nText(sdp));
        loi.setPlannedDuration("duration");
        Map<String, String> pdu = Maps.newHashMap();
        pdu.put("en", "pdu en");
        loi.setPlannedDurationUnit(new I18nText(pdu));
        ApplicationOption ao1 = new ApplicationOption();
        ao1.setId("aoid1");
        ApplicationOption ao2 = new ApplicationOption();
        ao1.setId("aoid2");
        ApplicationSystem as = new ApplicationSystem();
        as.setId("asid");
        Map<String, String> asName = Maps.newHashMap();
        asName.put("sv", "as name sv");
        as.setName(new I18nText(asName));
        ao1.setApplicationSystem(as);
        ao2.setApplicationSystem(as);
        loi.setApplicationOptions(Lists.newArrayList(ao1, ao2));
        loi.setContactPersons(Lists.newArrayList(new ContactPerson()));
    }

    @Test
    public void testConvert() {
        ChildLearningOpportunityInstanceDTO dto = ChildLOIToDTO.convert(loi, "fi", "en", "sv");
        assertNotNull(dto);
        assertEquals("loi id", dto.getId());
        assertEquals("translation en", dto.getAvailableTranslationLanguages().get(0).getDescription());
        assertEquals(startDate, dto.getStartDate());
        assertNotNull(dto.getTeachingLanguages());
        assertNotNull(dto.getRelated());
        assertEquals("teaching form en", dto.getFormOfTeaching().get(0));
        assertEquals("link value", dto.getWebLinks().get("link key"));
        assertEquals("education form en", dto.getFormOfEducation().get(0));
        assertEquals("prerequisite en", dto.getPrerequisite().getDescription());
        assertEquals("professional title fi", dto.getProfessionalTitles().get(0));
        assertEquals("wlp fi", dto.getWorkingLifePlacement());
        assertEquals("i18n fi", dto.getInternationalization());
        assertEquals("coop fi", dto.getCooperation());
        assertEquals("content fi", dto.getContent());
        assertEquals("sdp fi", dto.getSelectingDegreeProgram());
        assertEquals("duration", dto.getPlannedDuration());
        assertEquals("pdu en", dto.getPlannedDurationUnit());
        assertNotNull(dto.getApplicationSystems());
        assertEquals(1, dto.getApplicationSystems().size());
        assertEquals("as name sv", dto.getApplicationSystems().get(0).getName());
        assertEquals(2, dto.getApplicationSystems().get(0).getApplicationOptions().size());
        assertNotNull(dto.getContactPersons());
    }

    @Test
    public void testConvertNull() {
        loi = null;
        assertNull(ChildLOIToDTO.convert(loi, "", "", ""));
    }

    @Test
    public void testConvertMultiple() {
        List<ChildLOI> lois = Lists.newArrayList(loi);
        List<ChildLearningOpportunityInstanceDTO> dtos = ChildLOIToDTO.convert(lois, "fi", "fi", "fi");
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    public void testConvertMultipleWithNullList() {
        List<ChildLOI> lois = null;
        assertNull(ChildLOIToDTO.convert(lois, "", "", ""));
    }
}
