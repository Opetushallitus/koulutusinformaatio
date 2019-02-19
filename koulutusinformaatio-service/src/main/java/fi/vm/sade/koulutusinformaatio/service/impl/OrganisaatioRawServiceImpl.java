package fi.vm.sade.koulutusinformaatio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import fi.vm.sade.koulutusinformaatio.service.impl.metrics.RollingAverageLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrganisaatioRawServiceImpl implements OrganisaatioRawService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisaatioRawServiceImpl.class);

    private final OphHttpClient client;
    private final RollingAverageLogger rollingAverageLogger;
    private ObjectMapper mapper = HttpClient.createJacksonMapper();
    private static final int RETRY_DELAY_MS = 2500;
    private static final int MAX_RETRY_TIME = (int) TimeUnit.MINUTES.toMillis(10);
    private static final int MAX_RETRY_COUNT = MAX_RETRY_TIME / RETRY_DELAY_MS;
    private final Cache<String, OrganisaatioRDTO> orgCache;
    private final Cache<String, OrganisaatioHakutulos> orgHakutulosCache;

    @Autowired
    public OrganisaatioRawServiceImpl(HttpClient httpClient, RollingAverageLogger rollingAverageLogger) {
        this.client = httpClient.getClient();
        this.rollingAverageLogger = rollingAverageLogger;
        this.orgCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
        this.orgHakutulosCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();

    }

    @Override
    public OrganisaatioRDTO getOrganisaatio(String oid) {
        return getOrganisaatioWithCache(oid);
        //fixme, either by removing altogether or choosing which callers should use this and which not.
        /*
        return parseJson(OrganisaatioRDTO.class, client.get("organisaatio-service.organisaatio", oid)
                .param("includeImage", "true")
        );
        */
    }

    @Override
    public OrganisaatioRDTO getOrganisaatioWithCache(String oid) {
        LOGGER.info("Getting organisaatio: " + oid);
        AtomicReference<String> access = new AtomicReference<>("CACHE");
        OrganisaatioRDTO result = null;
        try {
            result = orgCache.get(oid, () -> {
                access.set("REST");
                return parseJson(OrganisaatioRDTO.class, client.get("organisaatio-service.organisaatio", oid)
                        .param("includeImage", "true"));
            });
        } catch (Exception e) {
            LOGGER.error("Error getting organisaatio: " + e);
        }
        LOGGER.info("Getting organisaatio: " + oid + ", got by: " + access.get());
        return result;
    }

    private <R> R parseJson(final Class<R> clazz, OphHttpRequest request) {
        return request.accept(OphHttpClient.JSON)
                .retryOnError(MAX_RETRY_COUNT, RETRY_DELAY_MS)
                .execute(new OphHttpResponseHandler<R>() {
                    @Override
                    public R handleResponse(OphHttpResponse response) throws IOException {
                        return mapper.readValue(response.asInputStream(), clazz);
                    }
                });
    }

    @Override
    public OrganisaatioHakutulos findOrganisaatio(String oid) {
        rollingAverageLogger.start("findOrganisaatio");
        AtomicReference<String> access = new AtomicReference<>("CACHE");
        OrganisaatioHakutulos r = null;
        try {
             r = orgHakutulosCache.get(oid, () -> {
                access.set("REST");
                return parseJson(OrganisaatioHakutulos.class, client.get("organisaatio-service.hae")
                        .param("noCache", System.currentTimeMillis())
                        .param("aktiiviset", "true")
                        .param("lakkautetut", "false")
                        .param("suunnitellut", "false")
                        .param("oid", oid)
                        .param("searchstr", "")
                );
            });
        } catch (Exception e) {
            LOGGER.error("Error finding Organisaatio: " + e);
        }
        LOGGER.info("findOrganisaatio: " + oid + ", got by: " + access.get());
        rollingAverageLogger.stop("findOrganisaatio");
        return r;
    }

    @Override
    public OrganisaatioHakutulos fetchOrganisaatiosByType(String organisaatioType) {
        rollingAverageLogger.start("fetchOrganisaatiosByType");
        OrganisaatioHakutulos result = parseJson(OrganisaatioHakutulos.class, client.get("organisaatio-service.hae")
                .param("noCache", System.currentTimeMillis())
                .param("aktiiviset", "true")
                .param("lakkautetut", "false")
                .param("suunnitellut", "false")
                .param("organisaatiotyyppi", organisaatioType)
                .param("searchstr", "")
        );
        rollingAverageLogger.stop("fetchOrganisaatiosByType");
        return result;
    }
}
