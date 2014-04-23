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
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionCreatorTest extends KoodistoAwareTest {

    TarjontaRawService tarjontaRawService;
    ApplicationOptionCreator creator;

    HakukohdeDTO hakukohde;
    KomotoDTO komoto;

    @Before
    public void init() throws KoodistoException {
        tarjontaRawService = mock(TarjontaRawService.class);
        hakukohde = new HakukohdeDTO();
        komoto = new KomotoDTO();

        hakukohde.setOid("1.2.3.4");
        String hakukohdeNameUri = "hakukohdeNameUri";
        I18nText name  = TestUtil.createI18nText("hakukohdeName");
        when(koodistoService.searchFirstName(eq(hakukohdeNameUri))).thenReturn(name);
        hakukohde.setHakukohdeNimiUri(hakukohdeNameUri);
        String aoIdentifierAthlete = "aoIdentifier";
        when(koodistoService.searchFirstCodeValue(eq(hakukohdeNameUri))).thenReturn(aoIdentifierAthlete);

        // athlete
        String athelteEducationUri = "atheleteUri";
        String applicationOptionsUri = "applicationOptionsUri";
        Code aoIdentifierCode = new Code();
        aoIdentifierCode.setValue(aoIdentifierAthlete);
        when(koodistoService.searchSuperCodes(
                eq(TarjontaConstants.ATHLETE_EDUCATION_KOODISTO_URI),
                eq(TarjontaConstants.APPLICATION_OPTIONS_KOODISTO_URI)))
                .thenReturn(Lists.newArrayList(aoIdentifierCode));

        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAlinHyvaksyttavaKeskiarvo(4);
        hakukohde.setLiitteidenToimitusPvm(new Date());
        hakukohde.setEdellisenVuodenHakijatLkm(10);
        Map<String, String> selectionCriteria = Maps.newHashMap();
        selectionCriteria.put(getFiUri(), "selectionCriteria");
        hakukohde.setValintaperustekuvaus(selectionCriteria);
        hakukohde.setKaksoisTutkinto(false);

        Code baseEducation1 = new Code();
        baseEducation1.setValue("1");
        Code baseEducation2 = new Code();
        baseEducation2.setValue("2");
        komoto.setPohjakoulutusVaatimusUri("prerequisiteUri");
        when(koodistoService.searchSubCodes(
                eq(komoto.getPohjakoulutusVaatimusUri()),
                eq(TarjontaConstants.BASE_EDUCATION_KOODISTO_URI)))
                .thenReturn(Lists.newArrayList(baseEducation1, baseEducation2));

        hakukohde.setSoraKuvausKoodiUri("soraUri");

        komoto.setOpetuskieletUris(Lists.newArrayList("fiUri"));
        when(koodistoService.searchCodeValuesMultiple(komoto.getOpetuskieletUris())).thenReturn(Lists.newArrayList("fi"));

        Map<String, String> additionalInfo = Maps.newHashMap();
        additionalInfo.put(getFiUri(), "additionalInfo");
        hakukohde.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
        hakukohde.setLisatiedot(additionalInfo);

    }

    @Test
    public void testCreateVocationalApplicationOption() throws KoodistoException {
        String educationCodeUri = "educationCodeUri";
        Code prerequisite = new Code();
        prerequisite.setValue("prerequisite");
        prerequisite.setName(TestUtil.createI18nText("peruskoulu"));
        when(tarjontaRawService.getKomotosByHakukohde(eq(hakukohde.getOid()))).thenReturn(new ArrayList<OidRDTO>());
        creator = new ApplicationOptionCreator(koodistoService, tarjontaRawService);

        ApplicationOption ao = creator.createVocationalApplicationOption(hakukohde, null, komoto, prerequisite, educationCodeUri);
        assertNotNull(ao);
        assertEquals("1.2.3.4", ao.getId());
        assertEquals("hakukohdeName", ao.getName().getTranslations().get("fi"));
        assertEquals("aoIdentifier", ao.getAoIdentifier());
        assertTrue(ao.isAthleteEducation());
        assertEquals(new Integer(10), ao.getStartingQuota());
        assertEquals(new Integer(5), ao.getLowestAcceptedScore());
        assertEquals(new Double(4), ao.getLowestAcceptedAverage());
        assertEquals(hakukohde.getLiitteidenToimitusPvm(), ao.getAttachmentDeliveryDeadline());
        assertEquals(new Integer(10), ao.getLastYearApplicantCount());
        assertEquals("selectionCriteria", ao.getSelectionCriteria().getTranslations().get("fi"));
        assertFalse(ao.isKaksoistutkinto());
        assertEquals(educationCodeUri, ao.getEducationCodeUri());
        assertNotNull(ao.getRequiredBaseEducations());
        assertEquals(2, ao.getRequiredBaseEducations().size());
        assertEquals("1", ao.getRequiredBaseEducations().get(0));
        assertEquals("2", ao.getRequiredBaseEducations().get(1));
        assertNotNull(ao.getTeachingLanguages());
        assertEquals(1, ao.getTeachingLanguages().size());
        assertEquals("fi", ao.getTeachingLanguages().get(0));
        assertEquals(prerequisite.getValue(), ao.getPrerequisite().getValue());
        assertEquals(prerequisite.getName().getTranslations().get("fi"), ao.getPrerequisite().getName().getTranslations().get("fi"));
        assertFalse(ao.isSpecificApplicationDates());
        assertEquals("additionalInfo", ao.getAdditionalInfo().getTranslations().get("fi"));
        assertTrue(ao.isVocational());

    }




}
