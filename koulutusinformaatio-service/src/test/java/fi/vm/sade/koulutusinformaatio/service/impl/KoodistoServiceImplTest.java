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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphRequestParameters;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koulutusinformaatio.domain.CodeUriAndVersion;
import fi.vm.sade.koulutusinformaatio.service.impl.metrics.RollingAverageLogger;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoClient;
import fi.vm.sade.koulutusinformaatio.converter.KoodiTypeToCode;
import fi.vm.sade.koulutusinformaatio.converter.KoodiTypeToI18nText;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;

/**
 * @author Mikko Majapuro
 */
public class KoodistoServiceImplTest {

    private KoodistoService koodistoService;

    @Before
    public void setUp() {
        KoodistoClient koodiService = mock(KoodistoClient.class);
        KoodiType koodi = new KoodiType();
        koodi.setKoodiArvo("1234");
        koodi.setKoodiUri("test_1234");
        koodi.setVersio(1);
        KoodiMetadataType metaFi = new KoodiMetadataType();
        metaFi.setNimi("nimi_fi");
        metaFi.setLyhytNimi("nimi_lyhyt_fi");
        metaFi.setKuvaus("kuvaus_fi");
        metaFi.setKieli(KieliType.FI);
        koodi.getMetadata().add(metaFi);
        KoodiMetadataType metaSv = new KoodiMetadataType();
        metaSv.setNimi("nimi_sv");
        metaSv.setLyhytNimi("nimi_lyhyt_sv");
        metaSv.setKuvaus("kuvaus_sv");
        metaSv.setKieli(KieliType.SV);
        koodi.getMetadata().add(metaSv);
        List<KoodiType> koodit = new ArrayList<KoodiType>();
        koodit.add(koodi);
        when(koodiService.searchKoodis(any(SearchKoodisCriteriaType.class))).thenReturn(koodit);
        KoodiTypeToI18nText converter = new KoodiTypeToI18nText();
        KoodiTypeToCode koodiTypeToCode = new KoodiTypeToCode();
        koodistoService = new KoodistoServiceImpl(koodiService, new RollingAverageLogger());
    }

    @Test
    public void testSearchNames() throws KoodistoException {
        List<I18nText> result = koodistoService.searchNames("test_1234#1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTranslations());
        assertEquals("nimi_fi", result.get(0).getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getTranslations().get("sv"));
    }

    @Test
    public void testSearchNamesWithNoVersion() throws KoodistoException {
        List<I18nText> result = koodistoService.searchNames("test_1234");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTranslations());
        assertEquals("nimi_fi", result.get(0).getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getTranslations().get("sv"));
    }

    @Test
    public void testSearchNamesWithEmptyUri() throws KoodistoException {
        assertNull(koodistoService.searchNames(""));
    }

    @Test
    public void testSearchNamesWithNullUri() throws KoodistoException {
        assertNull(koodistoService.searchNames(null));
    }

    @Test
    public void testSearchFirstName() throws KoodistoException {
        I18nText result = koodistoService.searchFirstName("test_1234#1");
        assertNotNull(result);
        assertNotNull(result);
        assertNotNull(result.getTranslations());
        assertEquals("nimi_fi", result.getTranslations().get("fi"));
        assertEquals("nimi_sv", result.getTranslations().get("sv"));
    }

    @Test
    public void testSearchFirstNameWithNoVersion() throws KoodistoException {
        I18nText result = koodistoService.searchFirstName("test_1234");
        assertNotNull(result);
        assertNotNull(result);
        assertNotNull(result.getTranslations());
        assertEquals("nimi_fi", result.getTranslations().get("fi"));
        assertEquals("nimi_sv", result.getTranslations().get("sv"));
    }

    @Test
    public void testSearchFirstWithEmptyUri() throws KoodistoException {
        assertNull(koodistoService.searchFirst(""));
    }

    @Test
    public void testSearchFirstWithNullUri() throws KoodistoException {
        assertNull(koodistoService.searchFirst(null));
    }

    @Test
    public void testSearch() throws KoodistoException {
        List<Code> result = koodistoService.search("test_1234#1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234", result.get(0).getValue());
        assertNotNull(result.get(0).getDescription().getTranslations());
        assertEquals("nimi_fi", result.get(0).getName().getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getName().getTranslations().get("sv"));
        assertEquals("kuvaus_fi", result.get(0).getDescription().getTranslations().get("fi"));
        assertEquals("kuvaus_sv", result.get(0).getDescription().getTranslations().get("sv"));
    }

    @Test
    public void testSearchWithNoVersion() throws KoodistoException {
        List<Code> result = koodistoService.search("test_1234");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234", result.get(0).getValue());
        assertNotNull(result.get(0).getDescription().getTranslations());
        assertEquals("nimi_fi", result.get(0).getName().getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getName().getTranslations().get("sv"));
        assertEquals("kuvaus_fi", result.get(0).getDescription().getTranslations().get("fi"));
        assertEquals("kuvaus_sv", result.get(0).getDescription().getTranslations().get("sv"));
    }

    @Test
    public void testSearchWithEmptyUri() throws KoodistoException {
        assertNull(koodistoService.search(""));
    }

    @Test
    public void testSearchWithNullUri() throws KoodistoException {
        assertNull(koodistoService.search(null));
    }

    @Test
    public void testSearchMultiple() throws KoodistoException {
        List<String> uris = Lists.newArrayList("test_1234", "test_1234");
        List<Code> result = koodistoService.searchMultiple(uris);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getValue());
        assertEquals("1234", result.get(0).getValue());
        assertNotNull(result.get(1).getValue());
        assertEquals("1234", result.get(1).getValue());
        assertNotNull(result.get(0).getDescription().getTranslations());
        assertEquals("nimi_fi", result.get(0).getName().getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(0).getName().getTranslations().get("sv"));
        assertEquals("kuvaus_fi", result.get(0).getDescription().getTranslations().get("fi"));
        assertEquals("kuvaus_sv", result.get(0).getDescription().getTranslations().get("sv"));
        assertNotNull(result.get(1).getDescription().getTranslations());
        assertEquals("nimi_fi", result.get(1).getName().getTranslations().get("fi"));
        assertEquals("nimi_sv", result.get(1).getName().getTranslations().get("sv"));
        assertEquals("kuvaus_fi", result.get(1).getDescription().getTranslations().get("fi"));
        assertEquals("kuvaus_sv", result.get(1).getDescription().getTranslations().get("sv"));
    }

    @Test
    public void listsAreCorrectlyEncoded() {
        KoodistoClient client = new CachingKoodistoClient("");
        CodeUriAndVersion codeUriAndVersion = new CodeUriAndVersion("koodi_uri", 1);
        SearchKoodisCriteriaType sc = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeUriAndVersion.getUri());
        OphHttpRequest request = client.buildSearchKoodiRequest(sc);
        OphRequestParameters.MultiValueMap<String, String> params = request.getRequestParameters().params;
        for (List<String> paramValues : params.values()) {
                for (String paramValue : paramValues) {
                    boolean containsBrackets = paramValue.contains("[") || paramValue.contains("]");
                    assertFalse(containsBrackets);
                }
        }
    }

    @Test
    public void nullAndEmptyParamsAreExcluded() {
        KoodistoClient client = new CachingKoodistoClient("");
        CodeUriAndVersion codeUriAndVersion = new CodeUriAndVersion("koodi_uri", 1);
        SearchKoodisCriteriaType sc = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeUriAndVersion.getUri());
        OphHttpRequest request = client.buildSearchKoodiRequest(sc);
        Set<String> keys = request.getRequestParameters().params.keySet();
        boolean containsKeysThatWereNotSpecified = keys.contains("validAt") || keys.contains("koodiTilas");
        assertFalse(containsKeysThatWereNotSpecified);
    }

    @Test
    public void versionParamIsIncluded() {
        KoodistoClient client = new CachingKoodistoClient("");
        SearchKoodisCriteriaType sc = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion("koodi_uri", 1);
        OphHttpRequest request = client.buildSearchKoodiRequest(sc);
        Set<String> keys = request.getRequestParameters().params.keySet();
        boolean containsVersionParam = keys.contains("koodiVersio");
        assertTrue(containsVersionParam);
    }

}
