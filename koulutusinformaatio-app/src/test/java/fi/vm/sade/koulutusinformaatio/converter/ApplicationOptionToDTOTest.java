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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.koulutusinformaatio.domain.*;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionToDTOTest {

    ApplicationOption ao;
    Date attachmentsDue;

    @Before
    public void init() {
        ao = new ApplicationOption();
        ao.setId("ao id");
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put("fi", "aoName");
        ao.setName(new I18nText(nameTranslations));
        ao.setEducationCodeUri("aoEducationCode");
        Map<String, String> infoTranslations = Maps.newHashMap();
        infoTranslations.put("fi", "additionalInfo");
        ao.setAdditionalInfo(new I18nText(infoTranslations));
        ao.setAdditionalProof(new AdditionalProof());
        ao.setAoIdentifier("aoIdentifier");
        ao.setApplicationSystem(new ApplicationSystem());
        ao.setAthleteEducation(false);
        ao.setAttachmentDeliveryAddress(new Address());
        Map<String, String> translation = new HashMap<String, String>();
        translation.put("fi", "addrss");
        ao.getAttachmentDeliveryAddress().setStreetAddress(new I18nText(translation));
        attachmentsDue = new Date();
        ao.setAttachmentDeliveryDeadline(attachmentsDue);
        ao.setAttachments(new ArrayList<ApplicationOptionAttachment>());
        ao.setLastYearApplicantCount(10);
        ao.setLowestAcceptedAverage(5.0);
        ao.setLowestAcceptedScore(5);
        ao.setStartingQuota(100);
        ao.setSora(true);
        ao.setEducationDegree("32");
        ao.setTeachingLanguages(Lists.newArrayList("fi", "sv"));
        Map<String, String> criteriaTranslations = Maps.newHashMap();
        criteriaTranslations.put("fi", "selection criteria");
        ao.setSelectionCriteria(new I18nText(criteriaTranslations));
        Map<String, String> soraTranslations = Maps.newHashMap();
        soraTranslations.put("fi", "sora description");
        ao.setSoraDescription(new I18nText(soraTranslations));
        Code prerequisite = new Code();
        prerequisite.setValue("PK");
        Map<String, String> prerequisiteTranslations = Maps.newHashMap();
        prerequisiteTranslations.put("fi", "peruskoulu");
        prerequisite.setDescription(new I18nText(prerequisiteTranslations));
        ao.setPrerequisite(prerequisite);
        ao.setExams(new ArrayList<Exam>());
        ao.setProvider(new Provider());
        ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
        ao.setSpecificApplicationDates(false);
        ao.setRequiredBaseEducations(Lists.newArrayList("1", "2"));
        ao.setVocational(true);
        ao.setKaksoistutkinto(false);
        ao.setStatus("status");
    }

    @Test
    public void testConvert() {
        ApplicationOptionDTO dto = ApplicationOptionToDTO.convert(ao, "fi", "fi", "fi");
        assertNotNull(dto);
        assertEquals("ao id", dto.getId());
        assertEquals("aoName", dto.getName());
        assertEquals("aoEducationCode", dto.getEducationCodeUri());
        assertEquals("additionalInfo", dto.getAdditionalInfo());
        assertNotNull(dto.getAdditionalProof());
        assertEquals("aoIdentifier", dto.getAoIdentifier());
        assertFalse(dto.isAthleteEducation());
        assertEquals(attachmentsDue, dto.getAttachmentDeliveryDeadline());
        assertNotNull(dto.getAttachmentDeliveryAddress());
        //assertNotNull(dto.getAttachments());
        assertEquals(Integer.valueOf(10), dto.getLastYearApplicantCount());
        assertEquals(Double.valueOf(5.0), dto.getLowestAcceptedAverage());
        assertEquals(Integer.valueOf(5), dto.getLowestAcceptedScore());
        assertEquals(Integer.valueOf(100), dto.getStartingQuota());
        assertTrue(dto.isSora());
        assertEquals("32", dto.getEducationDegree());
        assertNotNull(dto.getTeachingLanguages());
        assertEquals(2, dto.getTeachingLanguages().size());
        assertEquals("selection criteria", dto.getSelectionCriteria());
        assertEquals("sora description", dto.getSoraDescription());
        assertEquals("PK", dto.getPrerequisite().getValue());
        assertEquals("peruskoulu", dto.getPrerequisite().getDescription());
        assertNotNull(dto.getExams());
        assertNotNull(dto.getProvider());
        assertNotNull(dto.getChildRefs());
        assertFalse(dto.isSpecificApplicationDates());
        assertNotNull(dto.getRequiredBaseEducations());
        assertEquals(2, dto.getRequiredBaseEducations().size());
        assertTrue(dto.isVocational());
        assertFalse(dto.isKaksoistutkinto());
        assertEquals("status", dto.getStatus());
    }

    @Test
    public void testSpecificDatesNow() {
        ao.setSpecificApplicationDates(true);
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH) - 1);
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, endCal.get(Calendar.MONTH) + 1);
        ao.setApplicationStartDate(startCal.getTime());
        ao.setApplicationEndDate(endCal.getTime());
        ApplicationOptionDTO dto = ApplicationOptionToDTO.convert(ao, "fi", "fi", "fi");
        assertTrue(dto.isSpecificApplicationDates());
        assertTrue(dto.isCanBeApplied());
    }


    @Test
    public void testSpecificDatesFuture() {
        ao.setSpecificApplicationDates(true);
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH) + 1);
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, endCal.get(Calendar.MONTH) + 2);
        ao.setApplicationStartDate(startCal.getTime());
        ao.setApplicationEndDate(endCal.getTime());
        ApplicationOptionDTO dto = ApplicationOptionToDTO.convert(ao, "fi", "fi", "fi");
        assertTrue(dto.isSpecificApplicationDates());
        assertFalse(dto.isCanBeApplied());
    }

    @Test
    public void testSpecificDatesPast() {
        ao.setSpecificApplicationDates(true);
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.MONTH,  -2);
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.MONTH,  -1);
        ao.setApplicationStartDate(startCal.getTime());
        ao.setApplicationEndDate(endCal.getTime());
        ApplicationOptionDTO dto = ApplicationOptionToDTO.convert(ao, "fi", "fi", "fi");
        assertTrue(dto.isSpecificApplicationDates());
        assertFalse(dto.isCanBeApplied());
    }

}
