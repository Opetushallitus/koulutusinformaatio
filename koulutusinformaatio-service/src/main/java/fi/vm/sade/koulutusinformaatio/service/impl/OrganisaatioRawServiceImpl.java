package fi.vm.sade.koulutusinformaatio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.GenericType;

import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

import java.io.IOException;

@Service
public class OrganisaatioRawServiceImpl implements OrganisaatioRawService {

    private final OphHttpClient client;
    private ObjectMapper mapper = HttpClient.createJacksonMapper();

    @Autowired
    public OrganisaatioRawServiceImpl(HttpClient httpClient) {
        this.client = httpClient.getClient();
    }

    @Override
    public OrganisaatioRDTO getOrganisaatio(String oid) {
        return parseJson(OrganisaatioRDTO.class, client.get("organisaatio-service.organisaatio", oid)
                .param("includeImage", "true")
        );
    }

    private <R> R parseJson(final Class<R> clazz, OphHttpRequest request) {
        return request.accept(OphHttpClient.JSON)
                .execute(new OphHttpResponseHandler<R>() {
                    @Override
                    public R handleResponse(OphHttpResponse response) throws IOException {
                        return mapper.readValue(response.asInputStream(), clazz);
                    }
                });
    }

    @Override
    public OrganisaatioHakutulos findOrganisaatio(String oid) {
        return parseJson(OrganisaatioHakutulos.class, client.get("organisaatio-service.hae")
                .param("noCache", System.currentTimeMillis())
                .param("aktiiviset", "true")
                .param("lakkautetut", "false")
                .param("suunnitellut", "false")
                .param("oid", oid)
                .param("searchstr", "")
        );
    }

    @Override
    public OrganisaatioHakutulos fetchOrganisaatiosByType(String organisaatioType) {
        return parseJson(OrganisaatioHakutulos.class, client.get("organisaatio-service.hae")
                .param("noCache", System.currentTimeMillis())
                .param("aktiiviset", "true")
                .param("lakkautetut", "false")
                .param("suunnitellut", "false")
                .param("organisaatiotyyppi", organisaatioType)
                .param("searchstr", "")
        );
    }
}
