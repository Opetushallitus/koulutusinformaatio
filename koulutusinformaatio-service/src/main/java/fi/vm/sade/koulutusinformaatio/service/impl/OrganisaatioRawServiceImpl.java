package fi.vm.sade.koulutusinformaatio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import fi.vm.sade.koulutusinformaatio.domain.exception.OrganisaatioException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.impl.metrics.RollingAverageLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
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
    public OrganisaatioRawServiceImpl(HttpClient httpClient, RollingAverageLogger rollingAverageLogger,
                                      @Value("${koulutusinformaatio.organisaatioservice.cache.lifetime.minutes}") int cacheLifetimeMinutes) {
        this.client = httpClient.getClient();
        this.rollingAverageLogger = rollingAverageLogger;
        this.orgCache = CacheBuilder.newBuilder().expireAfterWrite(cacheLifetimeMinutes, TimeUnit.MINUTES).build();
        this.orgHakutulosCache = CacheBuilder.newBuilder().expireAfterWrite(cacheLifetimeMinutes, TimeUnit.MINUTES).build();

    }

    @Override
    public OrganisaatioRDTO getOrganisaatio(String oid) throws OrganisaatioException {
        AtomicReference<String> access = new AtomicReference<>("CACHE");
        OrganisaatioRDTO result;
        try {
                result = orgCache.get(oid, () -> {
                access.set("REST");
                return parseJson(OrganisaatioRDTO.class, client.get("organisaatio-service.organisaatio", oid)
                        .param("includeImage", "true"));
            });
        } catch (ExecutionException e) {
            LOGGER.error("Failure getting organisaatio: ", e);
            throw new OrganisaatioException("Failed to get organisaatio from organisaatiopalvelu with oid " + oid);
        }
        LOGGER.debug("Getting organisaatio: " + oid + ", got by: " + access.get());
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
    public OrganisaatioHakutulos findOrganisaatio(String oid) throws OrganisaatioException {
        rollingAverageLogger.start("findOrganisaatio");
        AtomicReference<String> access = new AtomicReference<>("CACHE");
        OrganisaatioHakutulos result;
        try {
            result = orgHakutulosCache.get(oid, () -> {
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
        } catch (ExecutionException e) {
            LOGGER.error("Failure finding organisaatio: ", e);
            throw new OrganisaatioException("Failed to find organisaatio from organisaatiopalvelu with oid " + oid);
        }
        LOGGER.debug("findOrganisaatio: " + oid + ", got by: " + access.get());
        rollingAverageLogger.stop("findOrganisaatio");
        return result;
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
