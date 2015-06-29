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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAikuistenPerusopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusGenericV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaRawServiceImpl implements TarjontaRawService {

    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";

    private WebResource komoResource;
    private WebResource komotoResource;
    private WebResource hakuResource;
    private WebResource hakukohdeResource;
    private WebResource v1Resource;
    private WebResource v1AOResource;
    private WebResource v1ASResource;
    private WebResource v1StructureResource;
    private WebResource v1KomoResource;
    private WebResource lastModifiedResource;
    
    
    @Autowired
    public TarjontaRawServiceImpl(@Value("${tarjonta.api.rest.url}") final String tarjontaApiUrl,
            ConversionService conversionService, KoodistoService koodistoService, ProviderService providerService) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        Client clientWithJacksonSerializer = Client.create(cc);
        komoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komo");
        komotoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komoto");
        hakuResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "haku");
        hakukohdeResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "hakukohde");
        v1Resource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/koulutus");
        v1AOResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/hakukohde");
        v1ASResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/haku");
        v1StructureResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/link");
        v1KomoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/komo");
        lastModifiedResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/lastmodified");
    }

    public TarjontaRawServiceImpl() {
    }


    @Override
    public KomoDTO getKomo(String oid) {
        return komoResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<KomoDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getKomotosByKomo(String oid, int count, int startIndex) {
        return komoResource
                .path(oid)
                .path("komoto")
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomotoDTO getKomoto(String oid) {
        return komotoResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<KomotoDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getHakukohdesByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("hakukohde")
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomoDTO getKomoByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("komo")
                .accept(JSON_UTF8)
                .get(new GenericType<KomoDTO>() {
                });

    }

    @Override
    public HakukohdeDTO getHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<HakukohdeDTO>() {
                });
    }

    @Override
    public HakuDTO getHakuByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("haku")
                .accept(JSON_UTF8)
                .get(new GenericType<HakuDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getKomotosByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("komoto")
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public HakuDTO getHaku(String oid) {
        return hakuResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<HakuDTO>() {
                });
    }
    
    @Override
    public List<OidRDTO> getHakukohdesByHaku(String oid) {
        return hakuResource
                .path(oid)
                .path("hakukohde")
                .queryParam("count", String.valueOf(10000))
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex) {
        return komoResource
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType) {
        return this.v1Resource
                .path("search")
                .queryParam("koulutusastetyyppi", educationType)
                .queryParam("tila", "JULKAISTU")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        return this.v1Resource
                .path("search")
                .queryParam("koulutusOid", oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    
    @Override
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> getHigherEducationLearningOpportunity(
            String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<KoulutusLukioV1RDTO> getUpperSecondaryLearningOpportunity(
            String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusLukioV1RDTO>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(
            String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(
            String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                });
    }
                
    public ResultV1RDTO<KomoV1RDTO> getV1Komo(
            String oid) {
        return this.v1KomoResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KomoV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(
            String oid) {
        return v1AOResource
                .path("search")
                .queryParam("koulutusOid", oid)
                .queryParam("tila", "JULKAISTU")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohode(String oid) {
        return v1AOResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakukohdeV1RDTO>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHigherEducationByHakukohode(String hakukohdeOid) {
        return v1AOResource
                .path(String.format("%s/%s", hakukohdeOid, "koulutukset")) 
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<List<NimiJaOidRDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid) {
        return v1ASResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakuV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(
            String parentOid) {
        return this.v1StructureResource
                .path(parentOid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<Set<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
            String childKomoOid) {
        return this.v1StructureResource
                .path(childKomoOid)
                .path("parents")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<Set<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
            String komoOid) {
        return this.v1Resource
                .path("search")
                .queryParam("komoOid", komoOid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getAdultEducationByKomo(
            String komoOid) {
        return this.v1Resource
                .path("search")
                .queryParam("komoOid", komoOid)
                .queryParam("koulutuslaji", "koulutuslaji_a")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        return v1Resource
                .path(koulutusOid)
                .path("kuva")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<List<KuvaV1RDTO>>>() {
                });
    }

    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod) {
        return this.lastModifiedResource
                .queryParam("lastModified", String.format("-%s", updatePeriod))
                .accept(JSON_UTF8)
                .get(new GenericType<Map<String, List<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(
            String... educationType) {
        WebResource call = this.v1Resource
                .path("search");
        for (String curType : educationType) {
            call = call.queryParam("toteutustyyppi", curType);
        }
        call.queryParam("tila", "JULKAISTU");
        return call
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<String>> searchHakus(String hakutapa) {
        return v1ASResource
                .queryParam("HAKUTAPA", hakutapa)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<List<String>>>() {    
                });
    }

    @Override
    public ResultV1RDTO<ValmistavaKoulutusV1RDTO> getValmistavaKoulutusLearningOpportunity(String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<ValmistavaKoulutusV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<KoulutusGenericV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusGenericV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> getAmmatillinenPerustutkintoLearningOpportunity(String oid) {
        return v1Resource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>>() {
        });
    }

}
