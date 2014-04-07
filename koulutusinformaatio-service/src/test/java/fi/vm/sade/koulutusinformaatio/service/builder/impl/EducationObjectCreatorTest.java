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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class EducationObjectCreatorTest extends KoodistoAwareTest {

    EducationObjectCreator creator;

    final String examTypeUri = "examtypecode";
    final String examTypeFi = "examTypeFi";
    final I18nText examType = TestUtil.createI18nText(examTypeFi);
    final String postCodeUri = "postcodeuri";
    final String postCode = "12345";
    final String attachmentTypeUri = "attachmentTypeUri";
    final String attachmentTypeFi = "attachmentType";
    final I18nText attachmentType = TestUtil.createI18nText(attachmentTypeFi);

    @Before
    public void init() throws KoodistoException {
        when(koodistoService.searchFirst(eq(examTypeUri))).thenReturn(examType);
        when(koodistoService.searchFirstCodeValue(eq(postCodeUri))).thenReturn(postCode);
        when(koodistoService.searchFirst(eq(attachmentTypeUri))).thenReturn(attachmentType);
        creator = new EducationObjectCreator(koodistoService);

    }

    @Test
    public void testCreateVocationalExams() throws KoodistoException {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        examDTO.setTyyppiUri(examTypeUri);
        Map<String, String> description = Maps.newHashMap();
        description.put(getFiUri(), "examdescription");
        examDTO.setKuvaus(description);
        ValintakoeAjankohtaRDTO eventDTO = new ValintakoeAjankohtaRDTO();
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        eventDTO.setOsoite(addressDTO);
        eventDTO.setLisatiedot("eventinfo");
        Date starts = new Date();
        Date ends = new Date();
        eventDTO.setAlkaa(starts);
        eventDTO.setLoppuu(ends);
        examDTO.setValintakoeAjankohtas(Lists.newArrayList(eventDTO));

        List<Exam> exams = creator.createVocationalExams(Lists.newArrayList(examDTO));
        assertNotNull(exams);
        assertEquals(1, exams.size());
        Exam exam = exams.get(0);
        assertNotNull(exam);
        assertEquals(examType.getTranslations().get(getFi()), exam.getType().getTranslations().get(getFi()));
        assertEquals("examdescription", exam.getDescription().getTranslations().get(getFi()));
        assertEquals(examDTO.getValintakoeAjankohtas().size(), exam.getExamEvents().size());
        ExamEvent event  = exam.getExamEvents().get(0);
        Address address = event.getAddress();
        assertEquals(postCode, address.getPostalCode());
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("fi"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("fi"));
        assertEquals("eventinfo", event.getDescription());
        assertEquals(starts, event.getStart());
        assertEquals(ends, event.getEnd());
    }

    @Test
    public void testCreateAddress() throws KoodistoException {
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        addressDTO.setOsoiterivi2("streetaddress2");
        Address address = creator.createAddress(addressDTO);
        assertNotNull(address);
        assertEquals(postCode, address.getPostalCode());
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("fi"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("fi"));
        assertEquals("streetaddress2", address.getStreetAddress2().getTranslations().get("fi"));
    }

    @Test
    public void testCreateAdditionalProof() throws KoodistoException {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        Map<String, String> additionalProofMap = Maps.newHashMap();
        additionalProofMap.put(getFiUri(), "additionalproof");
        examDTO.setLisanaytot(additionalProofMap);
        ValintakoePisterajaRDTO scoreLimitDTO = new ValintakoePisterajaRDTO();
        scoreLimitDTO.setAlinPistemaara(10.0);
        scoreLimitDTO.setAlinHyvaksyttyPistemaara(11.0);
        scoreLimitDTO.setYlinPistemaara(12.0);
        scoreLimitDTO.setTyyppi("Lisapisteet");
        examDTO.setValintakoePisterajas(Lists.newArrayList(scoreLimitDTO));

        AdditionalProof ap = creator.createAdditionalProof(Lists. newArrayList(examDTO));
        assertNotNull(ap);
        assertEquals("additionalproof", ap.getDescreption().getTranslations().get(getFi()));
    }

    @Test
    public void testResolvePointLimit() {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        ValintakoePisterajaRDTO scoreLimitDTO = new ValintakoePisterajaRDTO();
        scoreLimitDTO.setAlinPistemaara(10.0);
        scoreLimitDTO.setAlinHyvaksyttyPistemaara(11.0);
        scoreLimitDTO.setYlinPistemaara(12.0);
        scoreLimitDTO.setTyyppi("Lisapisteet");
        examDTO.setValintakoePisterajas(Lists.newArrayList(scoreLimitDTO));

        ScoreLimit scoreLimit = creator.resolvePointLimit(examDTO, "Lisapisteet");
        assertNotNull(scoreLimit);
        assertEquals(10.0, scoreLimit.getLowestScore(), 0.0);
        assertEquals(11.0, scoreLimit.getLowestAcceptedScore(), 0.0);
        assertEquals(12.0, scoreLimit.getHighestScore(), 0.0);

    }

    @Test
    public void testResolvePointLimitInvalidType() {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        ValintakoePisterajaRDTO scoreLimitDTO = new ValintakoePisterajaRDTO();
        scoreLimitDTO.setTyyppi("invalid");
        examDTO.setValintakoePisterajas(Lists.newArrayList(scoreLimitDTO));
        ScoreLimit scoreLimit = creator.resolvePointLimit(examDTO, "Lisapisteet");
        assertNull(scoreLimit);
    }

    @Test
    public void testCreateUpperSecondaryExams() throws KoodistoException {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        examDTO.setTyyppiUri(examTypeUri);
        Map<String, String> description = Maps.newHashMap();
        description.put(getFiUri(), "examdescription");
        examDTO.setKuvaus(description);
        ValintakoeAjankohtaRDTO eventDTO = new ValintakoeAjankohtaRDTO();
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        eventDTO.setOsoite(addressDTO);
        eventDTO.setLisatiedot("eventinfo");
        Date starts = new Date();
        Date ends = new Date();
        eventDTO.setAlkaa(starts);
        eventDTO.setLoppuu(ends);
        examDTO.setValintakoeAjankohtas(Lists.newArrayList(eventDTO));
        examDTO.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());

        List<Exam> exams = creator.createUpperSecondaryExams(Lists.newArrayList(examDTO));
        assertNotNull(exams);
        assertEquals(1, exams.size());
        Exam exam = exams.get(0);
        assertNotNull(exam);
        assertEquals("examdescription", exam.getDescription().getTranslations().get(getFi()));
        assertEquals(examDTO.getValintakoeAjankohtas().size(), exam.getExamEvents().size());
        ExamEvent event  = exam.getExamEvents().get(0);
        Address address = event.getAddress();
        assertEquals(postCode, address.getPostalCode());
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("fi"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("fi"));
        assertEquals("eventinfo", event.getDescription());
        assertEquals(starts, event.getStart());
        assertEquals(ends, event.getEnd());
    }


    @Test
    public void testCreateUpperSecondaryExamsInvalid() throws KoodistoException {
        ValintakoeRDTO descriptionNull = new ValintakoeRDTO();
        ValintakoeRDTO eventsNull = new ValintakoeRDTO();
        eventsNull.setKuvaus(new HashMap<String, String>());
        ValintakoeRDTO eventEmpty = new ValintakoeRDTO();
        eventEmpty.setKuvaus(new HashMap<String, String>());
        eventEmpty.setValintakoeAjankohtas(new ArrayList<ValintakoeAjankohtaRDTO>());
        List<ValintakoeRDTO> examDTOs = Lists.newArrayList(descriptionNull, eventsNull, eventEmpty);
        List<Exam> exams = creator.createUpperSecondaryExams(examDTOs);
        assertNotNull(exams);
        assertEquals(0, exams.size());
    }

    @Test
    public void testCreateUpperSecondaryExamsNull() throws KoodistoException {
        List<Exam> exams = creator.createUpperSecondaryExams(null);
        assertNull(exams);
    }

    @Test
    public void testCreateHigherEducationExams() throws KoodistoException {
        ValintakoeRDTO examDTO = new ValintakoeRDTO();
        examDTO.setTyyppiUri(examTypeUri);
        Map<String, String> description = Maps.newHashMap();
        description.put(getFiUri(), "examdescription");
        examDTO.setKuvaus(description);
        ValintakoeAjankohtaRDTO eventDTO = new ValintakoeAjankohtaRDTO();
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        eventDTO.setOsoite(addressDTO);
        eventDTO.setLisatiedot("eventinfo");
        Date starts = new Date();
        Date ends = new Date();
        eventDTO.setAlkaa(starts);
        eventDTO.setLoppuu(ends);
        examDTO.setValintakoeAjankohtas(Lists.newArrayList(eventDTO));

        List<Exam> exams = creator.createVocationalExams(Lists.newArrayList(examDTO));
        assertNotNull(exams);
        assertEquals(1, exams.size());
        Exam exam = exams.get(0);
        assertNotNull(exam);
        assertEquals(examType.getTranslations().get(getFi()), exam.getType().getTranslations().get(getFi()));
        assertEquals("examdescription", exam.getDescription().getTranslations().get(getFi()));
        assertEquals(examDTO.getValintakoeAjankohtas().size(), exam.getExamEvents().size());
        ExamEvent event  = exam.getExamEvents().get(0);
        Address address = event.getAddress();
        assertEquals(postCode, address.getPostalCode());
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("fi"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("fi"));
        assertEquals("eventinfo", event.getDescription());
        assertEquals(starts, event.getStart());
        assertEquals(ends, event.getEnd());
    }

    @Test
    public void testCreateApplicationOptionAttachments() throws KoodistoException {
        HakukohdeLiiteDTO attachmentDTO = new HakukohdeLiiteDTO();
        Date due = new Date();
        attachmentDTO.setErapaiva(due);
        attachmentDTO.setLiitteenTyyppiUri(attachmentTypeUri);
        Map<String, String> descritpionMap = Maps.newHashMap();
        descritpionMap.put(getFiUri(), "description");
        attachmentDTO.setKuvaus(descritpionMap);

        List<ApplicationOptionAttachment> attachments = creator.createApplicationOptionAttachments(Lists.newArrayList(attachmentDTO));
        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        ApplicationOptionAttachment attachment = attachments.get(0);
        assertNotNull(attachment);
        assertEquals(due, attachment.getDueDate());
        assertEquals(attachmentTypeFi, attachment.getType().getTranslations().get(getFi()));
        assertEquals("description", attachment.getDescreption().getTranslations().get(getFi()));
    }
}
