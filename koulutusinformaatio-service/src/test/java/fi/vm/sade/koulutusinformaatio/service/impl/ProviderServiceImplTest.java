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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import fi.vm.sade.koulutusinformaatio.configuration.UrlConfiguration;
import fi.vm.sade.koulutusinformaatio.service.impl.metrics.RollingAverageLogger;
import fi.vm.sade.properties.OphProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.core.convert.ConversionService;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fi.vm.sade.koulutusinformaatio.converter.OrganisaatioRDTOToProvider;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderServiceImplTest {

    ProviderServiceImpl service;
    private static final int PORT = 8800;
    private static final String CHILD_ORGANISAATIO_OID = "1.2.3.4.5";
    private static final String PARENT_ORGANISAATIO_OID = "11.22.33.44.55";
    private static final String HOMEPLACE_URI = "homeplaceuri";
    private static final I18nText HOMEPLACE = TestUtil.createI18nText("homeplace fi", "homeplace sv", "homeplace en");
    private static final String POSTNUMBER_URI = "postnumberuri";
    private static final String POSTALNUMBER = "00100";
    private static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    private static final String PLACE_OF_BUSINESS_KOODISTO_URI = "opetuspisteet";
    private static final String LANG_SELECTION_FI = "kielivalikoima_fi";
    private static final String LANG_FI = "fi";
    private static final String LANG_FI_URI = "kieli_fi#1";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Before
    public void setup() throws Exception {
        stubFor(get(urlEqualTo("/organisaatio-service/rest/organisaatio/" + CHILD_ORGANISAATIO_OID + "?includeImage=true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(CHILD_ORGANISAATIO_JSON))
        );
        stubFor(get(urlEqualTo("/organisaatio-service/rest/organisaatio/" + PARENT_ORGANISAATIO_OID + "?includeImage=true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(PARENT_ORGANISAATIO_JSON))
        );
        KoodistoService koodistoService = mock(KoodistoService.class);
        ConversionService conversionService = mock(ConversionService.class);
        when(koodistoService.searchFirstName(eq(HOMEPLACE_URI))).thenReturn(HOMEPLACE);
        when(koodistoService.searchFirstCodeValue(eq(POSTNUMBER_URI))).thenReturn(POSTALNUMBER);
        when(koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                PLACE_OF_BUSINESS_KOODISTO_URI)).thenReturn(new ArrayList<Code>());
        when(koodistoService.searchFirstCodeValue(eq(LANG_SELECTION_FI))).thenReturn(LANG_FI);
        when(koodistoService.searchFirstCodeValue(eq(LANG_FI_URI))).thenReturn(LANG_FI);
        ObjectMapper objectMapper = new ObjectMapper();
        OrganisaatioRDTOToProvider converter = new OrganisaatioRDTOToProvider(koodistoService);
        OrganisaatioRDTO childOrganisaatio = objectMapper.readValue(CHILD_ORGANISAATIO_JSON, OrganisaatioRDTO.class);
        OrganisaatioRDTO parentOrganisaatio = objectMapper.readValue(PARENT_ORGANISAATIO_JSON, OrganisaatioRDTO.class);
        Provider childProvider = converter.convert(childOrganisaatio);
        Provider parentProvider = converter.convert(parentOrganisaatio);
        when(conversionService.convert(argThat(new IsChildOrganisaatio()), eq(Provider.class))).thenReturn(childProvider);
        when(conversionService.convert(argThat(new IsParentOrganisaatio()), eq(Provider.class))).thenReturn(parentProvider);

        OphProperties ophProperties = new UrlConfiguration().addDefault("host.virkailija", "localhost:" + PORT).addDefault("organisaatio-service.baseUrl", "http://localhost:" + PORT);
        OrganisaatioRawService organisaatioRawService = new OrganisaatioRawServiceImpl(new HttpClient(ophProperties), new RollingAverageLogger(), 15);
        service = new ProviderServiceImpl(conversionService, organisaatioRawService, koodistoService);
        
    }

    @Test
    public void testGetByOid() throws Exception {
        Provider p = service.getByOID(CHILD_ORGANISAATIO_OID);
        assertNotNull(p);
        assertEquals(p.getId(), CHILD_ORGANISAATIO_OID);
        assertEquals("Porvoon lukio", p.getName().getTranslations().get("fi"));
        assertEquals("Borgå Gymnasium", p.getName().getTranslations().get("sv"));
        assertEquals("borga.gymnasium@porvoo.fi", p.getEmail().get("fi"));
        assertEquals("0407488664", p.getPhone().get("fi"));
        assertEquals(HOMEPLACE.getTranslations().get("fi"), p.getHomePlace().getTranslations().get("fi"));
        assertEquals(HOMEPLACE.getTranslations().get("sv"), p.getHomePlace().getTranslations().get("sv"));
        assertEquals(HOMEPLACE.getTranslations().get("en"), p.getHomePlace().getTranslations().get("en"));
        assertEquals(POSTALNUMBER, p.getVisitingAddress().getPostalCode().get("fi"));
        assertEquals(POSTALNUMBER, p.getPostalAddress().getPostalCode().get("fi"));
        assertEquals("yleiskuvaus", p.getDescription().getTranslations().get("fi"));
        assertEquals("terveydenhuolto", p.getHealthcare().getTranslations().get("fi"));
        assertEquals("ruokailu", p.getDining().getTranslations().get("fi"));
        assertEquals("kustannukset", p.getLivingExpenses().getTranslations().get("fi"));
    }
    
    /**
     * 
     * Tests fetching of organizations with type Oppilaitos from organisaatio service.
     * 
     * @throws MalformedURLException
     * @throws ResourceNotFoundException
     * @throws IOException
     */
    @Test
    public void testFetchOppilaitokset() throws Exception {
        
        ProviderService service = prepareWithMockRawService();
        List<OrganisaatioPerustieto> result = service.fetchOpplaitokset();
        assertEquals(1, result.size());
        assertEquals("1.1.1.oppilaitos", result.get(0).getOid());
    }
    
    /**
     * 
     * Tests fetching of organizations with type oppisopimustoimipiste from organisaatio service.
     * 
     * @throws MalformedURLException
     * @throws ResourceNotFoundException
     * @throws IOException
     */
    @Test
    public void testFetchOppisopimustoimipisteet() throws Exception {
        
        ProviderService service = prepareWithMockRawService();
        List<OrganisaatioPerustieto> result = service.fetchOppisopimusToimipisteet();
        assertEquals(1, result.size());
        assertEquals("1.1.1.oppisopimusToimipiste", result.get(0).getOid());
    }
    
    /**
     * 
     * Tests fetching of organizations with type Toimipiste from organisaatio service.
     * 
     * @throws MalformedURLException
     * @throws ResourceNotFoundException
     * @throws IOException
     */
    @Test
    public void testFetchToimipisteet() throws Exception {
        
        ProviderService service = prepareWithMockRawService();
        List<OrganisaatioPerustieto> result = service.fetchToimipisteet();
        assertEquals(1, result.size());
        assertEquals("1.1.1.toimipiste", result.get(0).getOid());
    }

    class IsChildOrganisaatio extends ArgumentMatcher<OrganisaatioRDTO> {
        @Override
        public boolean matches(Object o) {
            return o != null && ((OrganisaatioRDTO) o).getOid().equals(CHILD_ORGANISAATIO_OID);
        }
    }

    class IsParentOrganisaatio extends ArgumentMatcher<OrganisaatioRDTO> {
        @Override
        public boolean matches(Object o) {
            return o != null && ((OrganisaatioRDTO) o).getOid().equals(PARENT_ORGANISAATIO_OID);
        }
    }
    
    /*
     * Prepares a ProviderServiceImpl with a mock OrganisaatioRawService.
     */
    private ProviderServiceImpl prepareWithMockRawService() throws Exception {
        KoodistoService koodistoService = mock(KoodistoService.class);
        ConversionService conversionService = mock(ConversionService.class);
        OrganisaatioRawService organisaatioRawService = mock(OrganisaatioRawService.class);
        
        OrganisaatioPerustieto orgPerus = new OrganisaatioPerustieto();
        orgPerus.setOid("1.1.1.oppilaitos");
        orgPerus.setOppilaitostyyppi("olType1");
        
        OrganisaatioHakutulos orgRes = new OrganisaatioHakutulos();
        orgRes.setOrganisaatiot(Arrays.asList(orgPerus));
        
        List<Code> olFasetCodes = Arrays.asList(new Code());
        
        when(organisaatioRawService.fetchOrganisaatiosByType("Oppilaitos")).thenReturn(orgRes);
        when(koodistoService.searchSuperCodes("olType1", "oppilaitostyyppifasetti")).thenReturn(olFasetCodes);
        
        OrganisaatioPerustieto orgPerus2 = new OrganisaatioPerustieto();
        orgPerus2.setOid("1.1.1.toimipiste");
        orgPerus2.setOppilaitostyyppi("olType1");
        
        OrganisaatioHakutulos orgRes2 = new OrganisaatioHakutulos();
        orgRes2.setOrganisaatiot(Arrays.asList(orgPerus2));
        
        when(organisaatioRawService.fetchOrganisaatiosByType("Toimipiste")).thenReturn(orgRes2);
        
        OrganisaatioPerustieto orgPerus3 = new OrganisaatioPerustieto();
        orgPerus3.setOid("1.1.1.oppisopimusToimipiste");
        
        OrganisaatioHakutulos orgRes3 = new OrganisaatioHakutulos();
        orgRes3.setOrganisaatiot(Arrays.asList(orgPerus3));
        
        when(organisaatioRawService.fetchOrganisaatiosByType("Oppisopimustoimipiste")).thenReturn(orgRes3);
        
        
        return new ProviderServiceImpl(conversionService, organisaatioRawService, koodistoService);
    }


    private static final String CHILD_ORGANISAATIO_JSON = "{\n" +
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
            "  \"oid\" : \"" + CHILD_ORGANISAATIO_OID + "\",\n" +
            "  \"parentOid\" : \"" + PARENT_ORGANISAATIO_OID + "\",\n" +
            "  \"oppilaitosKoodi\" : \"00024\",\n" +
            "  \"vuosiluokat\" : [ ],\n" +
            "  \"tyypit\" : [ \"Oppilaitos\" ],\n" +
            "  \"nimi\" : {\n" +
            "    \"fi\" : \"Porvoon lukio\",\n" +
            "    \"sv\" : \"Borgå Gymnasium\"\n" +
            "  },\n" +
            "  \"alkuPvm\" : \"1992-01-01\",\n" +
            "  \"parentOidPath\" : \"|1.2.246.562.10.00000000001|1.2.246.562.10.67094744702|\",\n" +
            //"  \"wwwOsoite\" : \"http://www.pedanet/veraja/porvoo/borgagymnasium\",\n" +
            //"  \"puhelinnumero\" : \"0407488664\",\n" +
            "  \"yhteystiedot\" : [ {\n" +
            "    \"kieli\" : \"kieli_fi#1\",\n" +
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
            "  }, {\n" +
            "    \"kieli\" : \"kieli_fi#1\",\n" +
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
            "  }, {\n" +
            "    \"kieli\" : \"kieli_fi#1\",\n" +
            "    \"id\" : \"217339\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.91862370631\",\n" +
            "    \"www\" : \"http://www.pedanet/veraja/porvoo/borgagymnasium\"\n" +
            "  }, {\n" +
            "   \"kieli\" : \"kieli_fi#1\",\n" +
            "   \"id\" : \"217199\",\n" +
            "   \"yhteystietoOid\" : \"13945230737530.19501560464365164\",\n" +
            "   \"email\" : \"borga.gymnasium@porvoo.fi\"\n" +
            "  }, {\n" +
            "   \"tyyppi\" : \"puhelin\",\n" +
            "   \"kieli\" : \"kieli_fi#1\",\n" +
            "   \"id\" : \"217201\",\n" +
            "   \"yhteystietoOid\" : \"1.2.246.562.5.2014031109311375622541\",\n" +
            "   \"numero\" : \"0407488664\"\n" +
            " }\n" +
            "], \n" +
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
            //"  \"emailOsoite\" : \"borga.gymnasium@porvoo.fi\",\n" +
            "  \"kieletUris\" : [ \"kielivalikoima_sv\" ],\n" +
            "  \"kuvaus2\" : {\n" +
            "  },\n" +
            "  \"oppilaitosTyyppiUri\" : \"oppilaitostyyppi_15#1\"\n" +
            "}";

    private static final String PARENT_ORGANISAATIO_JSON = "{\n" +
            "  \"version\" : 1,\n" +
            "  \"metadata\" : {\n" +
            "    \"data\" : {\n" +
            "      \"OPISKELIJARUOKAILU\" : {\n" +
            "        \"kielivalikoima_fi\" : \"ruokailu\"\n" +
            "      },\n" +
            "      \"KUSTANNUKSET\" : {\n" +
            "        \"kielivalikoima_fi\" : \"kustannukset\"\n" +
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
            "  \"oid\" : \"" + PARENT_ORGANISAATIO_OID + "\",\n" +
            "  \"vuosiluokat\" : [ ],\n" +
            "  \"tyypit\" : [ \"Koulutustoimija\" ],\n" +
            "  \"nimi\" : {\n" +
            "    \"fi\" : \"Porvoon kaupunki\"\n" +
            "  },\n" +
            "  \"ytunnus\" : \"1061512-1\",\n" +
            "  \"alkuPvm\" : \"1990-01-01\",\n" +
            "  \"parentOidPath\" : \"|1.2.246.562.10.00000000001|\",\n" +
            "  \"wwwOsoite\" : \"www.porvoo.fi\",\n" +
            "  \"puhelinnumero\" : \"019  520 211\",\n" +
            "  \"yhteystiedot\" : [ {\n" +
            "    \"kieli\" : \"kieli_fi#1\",\n" +
            "    \"osoiteTyyppi\" : \"posti\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.344428525210\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"PL 23\",\n" +
            "    \"postitoimipaikka\" : \"PORVOO\",\n" +
            "    \"ytjPaivitysPvm\" : \"null\",\n" +
            "    \"lng\" : null,\n" +
            "    \"lap\" : null,\n" +
            "    \"coordinateType\" : null,\n" +
            "    \"osavaltio\" : null,\n" +
            "    \"extraRivi\" : null,\n" +
            "    \"maaUri\" : null\n" +
            "  }, { \n" +
            "    \"kieli\" : \"kieli_fi#1\",\n" +
            "    \"osoiteTyyppi\" : \"kaynti\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.86846741535\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"Taidetehtaankatu 1\",\n" +
            "    \"postitoimipaikka\" : \"PORVOO\",\n" +
            "    \"ytjPaivitysPvm\" : \"null\",\n" +
            "    \"lng\" : null,\n" +
            "    \"lap\" : null,\n" +
            "    \"coordinateType\" : null,\n" +
            "    \"osavaltio\" : null,\n" +
            "    \"extraRivi\" : null,\n" +
            "    \"maaUri\" : null\n" +
            "  }\n" +
            "], \n" +
            "  \"postiosoite\" : {\n" +
            "    \"osoiteTyyppi\" : \"posti\",\n" +
            "    \"yhteystietoOid\" : \"1.2.246.562.5.344428525210\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"PL 23\",\n" +
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
            "    \"yhteystietoOid\" : \"1.2.246.562.5.86846741535\",\n" +
            "    \"postinumeroUri\" : \"" + POSTNUMBER_URI + "\",\n" +
            "    \"osoite\" : \"Taidetehtaankatu 1\",\n" +
            "    \"postitoimipaikka\" : \"PORVOO\",\n" +
            "    \"ytjPaivitysPvm\" : \"null\",\n" +
            "    \"lng\" : null,\n" +
            "    \"lap\" : null,\n" +
            "    \"coordinateType\" : null,\n" +
            "    \"osavaltio\" : null,\n" +
            "    \"extraRivi\" : null,\n" +
            "    \"maaUri\" : null\n" +
            "  },\n" +
            "  \"kieletUris\" : [ \"kielivalikoima_fi\" ],\n" +
            "  \"kuvaus2\" : {\n" +
            "  }\n" +
            "}";
}
