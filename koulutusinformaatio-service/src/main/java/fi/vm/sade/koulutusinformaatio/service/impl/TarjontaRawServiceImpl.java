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

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaRawServiceImpl implements TarjontaRawService {

    private static final int RETRY_DELAY_MS = 2500;
    private static final int MAX_RETRY_TIME = (int) TimeUnit.MINUTES.toMillis(10);
    private static final int MAX_RETRY_COUNT = MAX_RETRY_TIME / RETRY_DELAY_MS;
    private final OphHttpClient httpclient;
    private final ObjectMapper mapper;

    @Autowired
    public TarjontaRawServiceImpl(HttpClient client) {
        this.httpclient = client.getClient();
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule("koulutusDeserializer", new Version(1, 0, 0, null))
                .addDeserializer(KoulutusV1RDTO.class, new KoulutusDeserializer()));
    }

    private OphHttpRequest get(String key, String... params) {
        return httpclient.get(key, params);
    }

    private <T> T executeWithRetries(OphHttpRequest resource, final TypeReference<T> type) {
        return execute(resource.retryOnError(MAX_RETRY_COUNT, RETRY_DELAY_MS), type);
    }

    private <T> T execute(OphHttpRequest resource, final TypeReference<T> type) {
        return resource
                .accept(JSON)
                .execute(new OphHttpResponseHandler<T>() {
                    @Override
                    public T handleResponse(OphHttpResponse response) throws IOException {
                        return mapper.readValue(response.asInputStream(), type);
                    }
                });
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus.search")
                        .param("koulutusOid", oid)
                        .param("tila", "KAIKKI"),
                new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(
            String oid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus", oid),
                new TypeReference<ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(String oid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus", oid), new TypeReference<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                }
        );
    }

    public ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid) {
        return executeWithRetries(
                get("tarjonta-service.komo", oid), new TypeReference<ResultV1RDTO<KomoV1RDTO>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid, boolean onlyPublished) {
        if (onlyPublished) {
            return executeWithRetries(
                    get("tarjonta-service.hakukohde.search")
                            .param("koulutusOid", oid)
                            .param("tila", "JULKAISTU"), new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                    }
            );
        } else {
            return executeWithRetries(
                    get("tarjonta-service.hakukohde.search")
                            .param("koulutusOid", oid), new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                    }
            );
        }
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1Hakukohde(String oid) {
        return executeWithRetries(
                get("tarjonta-service.hakukohde", oid), new TypeReference<ResultV1RDTO<HakukohdeV1RDTO>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> getV1HakuByOid(String oid) {
        return executeWithRetries(
                get("tarjonta-service.haku", oid), new TypeReference<ResultV1RDTO<HakuV1RDTO>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(
            String parentOid) {
        return executeWithRetries(
                get("tarjonta-service.link", parentOid), new TypeReference<ResultV1RDTO<Set<String>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
            String childKomoOid) {
        return executeWithRetries(
                get("tarjonta-service.link.parents", childKomoOid), new TypeReference<ResultV1RDTO<Set<String>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
            String komoOid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus.search")
                        .param("komoOid", komoOid), new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus.kuva", koulutusOid), new TypeReference<ResultV1RDTO<List<KuvaV1RDTO>>>() {
                }
        );
    }

    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod) {
        return execute(
                get("tarjonta-service.lastmodified").param("lastModified", String.format("-%s", updatePeriod)),
                new TypeReference<Map<String, List<String>>>() {
                       }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(String... educationType) {
        OphHttpRequest request = get("tarjonta-service.koulutus.search");
        for (String curType : educationType) {
            request = request.param("toteutustyyppi", curType);
        }
        request.param("tila", "JULKAISTU");
        return executeWithRetries(request,
                new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String toteutusTyyppi, String providerOid, String koulutusKoodi) {
        return executeWithRetries(
                get("tarjonta-service.koulutus.search")
                        .param("toteutustyyppi", toteutusTyyppi)
                        .param("organisationOid", providerOid)
                        .param("koulutuskoodi", koulutusKoodi)
                        .param("tila", "JULKAISTU"),
                new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<List<String>> searchHakus(String hakutapa) {
        return executeWithRetries(
                get("tarjonta-service.haku.search")
                        .param("TILA", "JULKAISTU")
                        .param("HAKUTAPA", hakutapa),
                new TypeReference<ResultV1RDTO<List<String>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus", oid),
                new TypeReference<ResultV1RDTO<KoulutusV1RDTO>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getV1KoulutusByAsId(String asOid) {
        return executeWithRetries(
                get("tarjonta-service.koulutus.search")
                        .param("hakuOid", asOid)
                        .param("tila", "JULKAISTU"),
                new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getV1KoulutusByAoId(String aoOid) {
        return executeWithRetries(
                get("tarjonta-service.hakukohde.koulutukset", aoOid),
                new TypeReference<ResultV1RDTO<List<NimiJaOidRDTO>>>() {
                }
        );
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdes() {
        return executeWithRetries(
                get("tarjonta-service.hakukohde.search")
                        .param("tila", "JULKAISTU"),
                new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
                }
        );
    }
}
