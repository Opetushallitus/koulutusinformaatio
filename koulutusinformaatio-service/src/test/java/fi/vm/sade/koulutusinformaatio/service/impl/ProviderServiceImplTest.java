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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import fi.vm.sade.koulutusinformaatio.converter.OrganisaatioRDTOToProvider;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.io.IOException;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderServiceImplTest {

    ProviderServiceImpl service;
    private static final int PORT = 8800;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String ORGANISAATIO_OID = "1.2.3.4.5";
    private static final String HOMEPLACE_URI = "homeplaceuri";
    private static final I18nText HOMEPLACE = TestUtil.createI18nText("homeplace fi", "homeplace sv", "homeplace en");
    private static final String POSTNUMBER_URI = "postnumberuri";
    private static final String POSTALNUMBER = "00100";
    private static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    private static final String PLACE_OF_BUSINESS_KOODISTO_URI = "opetuspisteet";
    private static final String LANG_SELECTION_FI = "kielivalikoima_fi";
    private static final String LANG_FI = "fi";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Before
    public void setup() throws IOException, KoodistoException {
        stubFor(get(urlEqualTo("/" + ORGANISAATIO_OID))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ORGANISAATIO_JSON))
        );
        KoodistoService koodistoService = mock(KoodistoService.class);
        ConversionService conversionService = mock(ConversionService.class);
        when(koodistoService.searchFirst(eq(HOMEPLACE_URI))).thenReturn(HOMEPLACE);
        when(koodistoService.searchFirstCodeValue(eq(POSTNUMBER_URI))).thenReturn(POSTALNUMBER);
        when(koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                PLACE_OF_BUSINESS_KOODISTO_URI)).thenReturn(new ArrayList<Code>());
        when(koodistoService.searchFirstCodeValue(eq(LANG_SELECTION_FI))).thenReturn(LANG_FI);
        ObjectMapper objectMapper = new ObjectMapper();
        OrganisaatioRDTO ordto = objectMapper.readValue(ORGANISAATIO_JSON, OrganisaatioRDTO.class);
        OrganisaatioRDTOToProvider converter = new OrganisaatioRDTOToProvider(koodistoService);
        Provider provider = converter.convert(ordto);
        when(conversionService.convert(any(OrganisaatioRDTO.class), eq(Provider.class))).thenReturn(provider);
        service = new ProviderServiceImpl(BASE_URL, conversionService);
    }

    @Test
    public void testGetByOid() throws KoodistoException {
        Provider p = service.getByOID(ORGANISAATIO_OID);
        assertNotNull(p);
        assertEquals(p.getId(), ORGANISAATIO_OID);
        assertEquals("Porvoon lukio", p.getName().getTranslations().get("fi"));
        assertEquals("Borgå Gymnasium", p.getName().getTranslations().get("sv"));
        assertEquals("borga.gymnasium@porvoo.fi", p.getEmail());
        assertEquals("0407488664", p.getPhone());
        assertEquals(HOMEPLACE.getTranslations().get("fi"), p.getHomePlace().getTranslations().get("fi"));
        assertEquals(HOMEPLACE.getTranslations().get("sv"), p.getHomePlace().getTranslations().get("sv"));
        assertEquals(HOMEPLACE.getTranslations().get("en"), p.getHomePlace().getTranslations().get("en"));
        assertEquals(POSTALNUMBER, p.getVisitingAddress().getPostalCode());
        assertEquals(POSTALNUMBER, p.getPostalAddress().getPostalCode());
        assertEquals("yleiskuvaus", p.getDescription().getTranslations().get("fi"));
        assertEquals("terveydenhuolto", p.getHealthcare().getTranslations().get("fi"));
    }

    private static final String ORGANISAATIO_JSON = "{\n" +
            "  \"version\" : 7,\n" +
            "  \"metadata\" : {\n" +
            "    \"data\" : {\n" +
            "      \"YLEISKUVAUS\" : {\n" +
            "        \"kielivalikoima_fi\" : \"yleiskuvaus\"\n" +
            "      },\n" +
            "      \"TERVEYDENHUOLTOPALVELUT\" : {\n" +
            "        \"kielivalikoima_fi\" : \"terveydenhuolto\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"nimi\" : {\n" +
            "    },\n" +
            "    \"luontiPvm\" : 1377503960887,\n" +
            "    \"muokkausPvm\" : 1377503960887,\n" +
            "    \"hakutoimistonNimi\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"maaUri\" : \"maatjavaltiot1_fin\",\n" +
            "  \"kotipaikkaUri\" : \"" + HOMEPLACE_URI + "\",\n" +
            "  \"oid\" : \"" + ORGANISAATIO_OID + "\",\n" +
            "  \"parentOid\" : \"1.2.246.562.10.67094744702\",\n" +
            "  \"oppilaitosKoodi\" : \"00024\",\n" +
            "  \"vuosiluokat\" : [ ],\n" +
            "  \"tyypit\" : [ \"Oppilaitos\" ],\n" +
            "  \"nimi\" : {\n" +
            "    \"fi\" : \"Porvoon lukio\",\n" +
            "    \"sv\" : \"Borgå Gymnasium\"\n" +
            "  },\n" +
            "  \"alkuPvm\" : \"1992-01-01\",\n" +
            "  \"parentOidPath\" : \"|1.2.246.562.10.00000000001|1.2.246.562.10.67094744702|\",\n" +
            "  \"wwwOsoite\" : \"http://www.pedanet/veraja/porvoo/borgagymnasium\",\n" +
            "  \"puhelinnumero\" : \"0407488664\",\n" +
            "  \"postiosoite\" : {\n" +
            "    \"osoiteTyyppi\" : \"posti\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.854073253410\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"PB 8\",\n" +
            "    \"postitoimipaikka\" : \"PORVOO\",\n" +
            "    \"ytjPaivitysPvm\" : \"null\",\n" +
            "    \"lng\" : null,\n" +
            "    \"lap\" : null,\n" +
            "    \"coordinateType\" : null,\n" +
            "    \"osavaltio\" : null,\n" +
            "    \"extraRivi\" : null,\n" +
            "    \"maaUri\" : null\n" +
            "  },\n" +
            "  \"kayntiosoite\" : {\n" +
            "    \"osoiteTyyppi\" : \"kaynti\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.67136319307\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"Gymnasiegatan 10\",\n" +
            "    \"postitoimipaikka\" : \"PORVOO\",\n" +
            "    \"ytjPaivitysPvm\" : \"null\",\n" +
            "    \"lng\" : null,\n" +
            "    \"lap\" : null,\n" +
            "    \"coordinateType\" : null,\n" +
            "    \"osavaltio\" : null,\n" +
            "    \"extraRivi\" : null,\n" +
            "    \"maaUri\" : null\n" +
            "  },\n" +
            "  \"emailOsoite\" : \"borga.gymnasium@porvoo.fi\",\n" +
            "  \"kieletUris\" : [ \"kielivalikoima_sv\" ],\n" +
            "  \"kuvaus2\" : {\n" +
            "  },\n" +
            "  \"oppilaitosTyyppiUri\" : \"oppilaitostyyppi_15#1\"\n" +
            "}";
}
