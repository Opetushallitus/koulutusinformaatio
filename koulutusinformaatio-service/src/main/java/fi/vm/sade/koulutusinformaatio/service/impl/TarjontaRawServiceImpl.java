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

import fi.vm.sade.properties.OphProperties;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAikuistenPerusopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaRawServiceImpl implements TarjontaRawService {

    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRawServiceImpl.class);
    public static final int CONNECT_TIMEOUT = 1000;
    public static final int READ_TIMEOUT = 30000;
    private final OphProperties urlProperties;

    private final Client clientWithJacksonSerializer;

    @Autowired
    public TarjontaRawServiceImpl(OphProperties urlProperties) {
        this.urlProperties = urlProperties;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule("koulutusDeserializer", new Version(1, 0, 0, null))
                .addDeserializer(KoulutusV1RDTO.class, new KoulutusDeserializer()));
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        clientWithJacksonSerializer = Client.create(cc);
        clientWithJacksonSerializer.setConnectTimeout(CONNECT_TIMEOUT);
        clientWithJacksonSerializer.setReadTimeout(READ_TIMEOUT);
    }

    private WebResource rest(String key, String... params) {
        return clientWithJacksonSerializer.resource(urlProperties.url(key, params));
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                rest("tarjonta-service.koulutus.search")
                        .queryParam("koulutusOid", oid)
                        .queryParam("tila", "KAIKKI"),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    private WebResource rest(String key) {
        return clientWithJacksonSerializer.resource(urlProperties.url(key));
    }

    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(
            String oid) {
        return (ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO>) getWithRetries(
                rest("tarjonta-service.koulutus", oid),
                new GenericType<ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(String oid) {
        return (ResultV1RDTO<AmmattitutkintoV1RDTO>) getWithRetries(
                rest("tarjonta-service.koulutus", oid),
                new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                });
    }

    public ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid) {
        return (ResultV1RDTO<KomoV1RDTO>) getWithRetries(
                rest("tarjonta-service.komo", oid),
                new GenericType<ResultV1RDTO<KomoV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid, boolean onlyPublished) {
        if (onlyPublished) {
            return (ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>) getWithRetries(
                    rest("tarjonta-service.hakukohde.search")
                            .queryParam("koulutusOid", oid)
                            .queryParam("tila", "JULKAISTU"),
                    new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                    });
        } else {
            return (ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>) getWithRetries(
                    rest("tarjonta-service.hakukohde.search")
                            .queryParam("koulutusOid", oid),
                    new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                    });
        }
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohde(String oid) {
        return (ResultV1RDTO<HakukohdeV1RDTO>) getWithRetries(
                rest("tarjonta-service.hakukohde", oid),
                new GenericType<ResultV1RDTO<HakukohdeV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid) {
        return (ResultV1RDTO<HakuV1RDTO>) getWithRetries(
                rest("tarjonta-service.haku", oid),
                new GenericType<ResultV1RDTO<HakuV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(
            String parentOid) {
        return (ResultV1RDTO<Set<String>>) getWithRetries(
                rest("tarjonta-service.link", parentOid),
                new GenericType<ResultV1RDTO<Set<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
            String childKomoOid) {
        return (ResultV1RDTO<Set<String>>) getWithRetries(
                rest("tarjonta-service.link.parents", childKomoOid),
                new GenericType<ResultV1RDTO<Set<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
            String komoOid) {
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                rest("tarjonta-service.koulutus.search")
                        .queryParam("komoOid", komoOid),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        return (ResultV1RDTO<List<KuvaV1RDTO>>) getWithRetries(
                rest("tarjonta-service.koulutus.kuva", koulutusOid),
                new GenericType<ResultV1RDTO<List<KuvaV1RDTO>>>() {
                });
    }

    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod) {
        return rest("tarjonta-service.lastmodified")
                .queryParam("lastModified", String.format("-%s", updatePeriod))
                .accept(JSON_UTF8)
                .get(new GenericType<Map<String, List<String>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(
            String... educationType) {
        WebResource call = rest("tarjonta-service.koulutus.search");
        for (String curType : educationType) {
            call = call.queryParam("toteutustyyppi", curType);
        }
        call.queryParam("tila", "JULKAISTU");
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(call,
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String toteutusTyyppi, String providerOid, String koulutusKoodi) {
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                rest("tarjonta-service.koulutus.search")
                        .queryParam("toteutustyyppi", toteutusTyyppi)
                        .queryParam("organisationOid", providerOid)
                        .queryParam("koulutuskoodi", koulutusKoodi)
                        .queryParam("tila", "JULKAISTU"),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<String>> searchHakus(String hakutapa) {
        return (ResultV1RDTO<List<String>>) getWithRetries(
                rest("tarjonta-service.haku.search")
                        .queryParam("TILA", "JULKAISTU")
                        .queryParam("HAKUTAPA", hakutapa),
                new GenericType<ResultV1RDTO<List<String>>>() {
                });
    }

    private Object getWithRetries(WebResource resource, GenericType type) {
        int retries = 2;
        while (--retries > 0) {
            try {
                return resource
                        .accept(JSON_UTF8)
                        .get(type);
            } catch (Exception e) {
                LOG.warn("Calling resource failed: " + resource);
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        LOG.warn("Calling resource failed, last retry: " + resource);
        try {
            return resource
                    .accept(JSON_UTF8)
                    .get(type);
        } catch (Exception e) {
            LOG.error("Calling resource failed: " + resource);
            throw e;
        }
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        return (ResultV1RDTO<KoulutusV1RDTO>) getWithRetries(
                rest("tarjonta-service.koulutus", oid),
                new GenericType<ResultV1RDTO<KoulutusV1RDTO>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getV1KoulutusByAsId(String asOid) {
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                rest("tarjonta-service.koulutus.search")
                        .queryParam("hakuOid", asOid)
                        .queryParam("tila", "JULKAISTU"),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getV1KoulutusByAoId(String aoOid) {
        return (ResultV1RDTO<List<NimiJaOidRDTO>>) getWithRetries(
                rest("tarjonta-service.hakukohde.koulutukset", aoOid),
                new GenericType<ResultV1RDTO<List<NimiJaOidRDTO>>>() {
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdes() {
        return (ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>) getWithRetries(
                rest("tarjonta-service.hakukohde.search")
                        .queryParam("tila", "JULKAISTU"),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                });
    }
}
