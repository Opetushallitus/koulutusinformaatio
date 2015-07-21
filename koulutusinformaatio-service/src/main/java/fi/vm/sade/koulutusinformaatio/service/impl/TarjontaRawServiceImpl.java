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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaRawServiceImpl implements TarjontaRawService {

    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";

    private WebResource komotoResource;
    private WebResource v1KoulutusResource;
    private WebResource v1AOResource;
    private WebResource v1ASResource;
    private WebResource v1StructureResource;
    private WebResource v1KomoResource;
    private WebResource lastModifiedResource;
    
    
    @Autowired
    public TarjontaRawServiceImpl(@Value("${tarjonta.api.rest.url}") final String tarjontaApiUrl) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule("koulutusDeserializer", new Version(1, 0, 0, null))
                .addDeserializer(KoulutusV1RDTO.class, new KoulutusDeserializer()));
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        Client clientWithJacksonSerializer = Client.create(cc);
        komotoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komoto");
        v1KoulutusResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/koulutus");
        v1AOResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/hakukohde");
        v1ASResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/haku");
        v1StructureResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/link");
        v1KomoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/komo");
        lastModifiedResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/lastmodified");
    }

    public TarjontaRawServiceImpl() {
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
    public List<OidV1RDTO> getHakukohdesByHaku(String oid) {
        return v1ASResource
                .path(oid)
                .path("hakukohde")
                .queryParam("count", String.valueOf(10000))
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType) {
        return this.v1KoulutusResource
                .path("search")
                .queryParam("koulutusastetyyppi", educationType)
                .queryParam("tila", "JULKAISTU")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        return this.v1KoulutusResource
                .path("search")
                .queryParam("koulutusOid", oid)
                .queryParam("tila", "KAIKKI") // include POISTETTU
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(
            String oid) {
        return v1KoulutusResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO>>() {
                });
    }
    
    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(String oid) {
        return v1KoulutusResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                });
    }
                
    public ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid) {
        return this.v1KomoResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KomoV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid) {
        return v1AOResource
                .path("search")
                .queryParam("koulutusOid", oid)
                .queryParam("tila", "JULKAISTU")
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohde(String oid) {
        return v1AOResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakukohdeV1RDTO>>() {
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
        return this.v1KoulutusResource
                .path("search")
                .queryParam("komoOid", komoOid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        return v1KoulutusResource
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
        WebResource call = this.v1KoulutusResource
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
    public ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        return v1KoulutusResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<ResultV1RDTO<KoulutusV1RDTO>>() {
        });
    }

}
