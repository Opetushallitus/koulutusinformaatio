/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koulutusinformaatio.converter.KoodiTypeToI18nText;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class KoodistoServiceImplTest {

    private KoodistoService koodistoService;
    private KoodiService koodiService;
    private ConversionService conversionService;

    @Before
    public void setUp() {
        koodiService = mock(KoodiService.class);
        KoodiType koodi = new KoodiType();
        koodi.setKoodiArvo("1234");
        koodi.setKoodiUri("test_1234");
        koodi.setVersio(1);
        KoodiMetadataType metaFi = new KoodiMetadataType();
        metaFi.setNimi("nimi_fi");
        metaFi.setKieli(KieliType.FI);
        koodi.getMetadata().add(metaFi);
        KoodiMetadataType metaSv = new KoodiMetadataType();
        metaSv.setNimi("nimi_sv");
        metaSv.setKieli(KieliType.SV);
        koodi.getMetadata().add(metaSv);
        List<KoodiType> koodit = new ArrayList<KoodiType>();
        koodit.add(koodi);
        when(koodiService.searchKoodis(any(SearchKoodisCriteriaType.class))).thenReturn(koodit);
        conversionService = mock(ConversionService.class);
        KoodiTypeToI18nText converter = new KoodiTypeToI18nText();
        when(conversionService.convert(any(KoodiType.class), eq(I18nText.class))).thenReturn(converter.convert(koodi));
        koodistoService = new KoodistoServiceImpl(koodiService, conversionService);
    }

    @Test
    public void testSearch() throws KoodistoException {
        List<I18nText> result = koodistoService.search("test_1234#1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTranslations());
        assertEquals("nimi_fi", result.get(0).getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getTranslations().get("sv"));
    }

    @Test
    public void testSearchWithNoVersion() throws KoodistoException {
        List<I18nText> result = koodistoService.search("test_1234");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTranslations());
        assertEquals("nimi_fi", result.get(0).getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getTranslations().get("sv"));
    }

    @Test(expected = KoodistoException.class)
    public void testSearchWithEmptyUri() throws KoodistoException {
        koodistoService.search("");
    }

    @Test(expected = KoodistoException.class)
    public void testSearchWithNullUri() throws KoodistoException {
        koodistoService.search(null);
    }

    @Test
    public void testSearchFirst() throws KoodistoException {
        I18nText result = koodistoService.searchFirst("test_1234#1");
        assertNotNull(result);
        assertNotNull(result);
        assertNotNull(result.getTranslations());
        assertEquals("nimi_fi", result.getTranslations().get("fi"));
        assertEquals("nimi_sv", result.getTranslations().get("sv"));
    }

    @Test
    public void testSearchFirstWithNoVersion() throws KoodistoException {
        I18nText result = koodistoService.searchFirst("test_1234");
        assertNotNull(result);
        assertNotNull(result);
        assertNotNull(result.getTranslations());
        assertEquals("nimi_fi", result.getTranslations().get("fi"));
        assertEquals("nimi_sv", result.getTranslations().get("sv"));
    }

    @Test(expected = KoodistoException.class)
    public void testSearchFirstWithEmptyUri() throws KoodistoException {
        koodistoService.searchFirst("");
    }

    @Test(expected = KoodistoException.class)
    public void testSearchFirstWithNullUri() throws KoodistoException {
        koodistoService.searchFirst(null);
    }
}
