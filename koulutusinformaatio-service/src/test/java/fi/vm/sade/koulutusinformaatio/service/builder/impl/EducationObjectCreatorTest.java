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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.AdditionalProof;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ScoreLimit;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;

/**
 * @author Hannu Lyytikainen
 */
public class EducationObjectCreatorTest extends KoodistoAwareTest {

    EducationObjectCreator creator;

    OrganisaatioRawService organisaatioRawService;

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
        when(koodistoService.searchFirstName(eq(examTypeUri))).thenReturn(examType);
        when(koodistoService.searchFirstCodeValue(eq(postCodeUri))).thenReturn(postCode);
        when(koodistoService.searchFirstName(eq(attachmentTypeUri))).thenReturn(attachmentType);
        creator = new EducationObjectCreator(koodistoService, organisaatioRawService);
    }

    @Test
    public void testCreateAddress() throws KoodistoException {
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        addressDTO.setOsoiterivi2("streetaddress2");
        Address address = creator.createAddress(addressDTO, "kieli_fi");
        assertNotNull(address);
        assertEquals(postCode, address.getPostalCode().get("fi"));
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("fi"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("fi"));
        assertEquals("streetaddress2", address.getSecondForeignAddr().getTranslations().get("fi"));
    }

    @Test
    public void testCreateAddressSve() throws KoodistoException {
        OsoiteRDTO addressDTO = new OsoiteRDTO();
        addressDTO.setPostinumero(postCodeUri);
        addressDTO.setPostitoimipaikka("postoffice");
        addressDTO.setOsoiterivi1("streetaddress");
        addressDTO.setOsoiterivi2("streetaddress2");
        Address address = creator.createAddress(addressDTO, "kieli_sv");
        assertNotNull(address);
        assertEquals(postCode, address.getPostalCode().get("sv"));
        assertEquals("postoffice", address.getPostOffice().getTranslations().get("sv"));
        assertEquals("streetaddress", address.getStreetAddress().getTranslations().get("sv"));
        assertEquals("streetaddress2", address.getSecondForeignAddr().getTranslations().get("sv"));
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
}
