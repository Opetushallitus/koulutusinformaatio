package fi.vm.sade.koulutusinformaatio.service.impl;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

@Service
public class OrganisaatioRawServiceImpl implements OrganisaatioRawService {

    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    public static final int CONNECT_TIMEOUT = 1000;
    public static final int READ_TIMEOUT = 30000;

    private final String organisaatioResourceUrl;
    private final WebResource orgRootRes;
    private final WebResource orgRes;

    @Autowired
    public OrganisaatioRawServiceImpl(@Value("${organisaatio.api.rest.url}") final String organisaatioResourceUrl) {
        this.organisaatioResourceUrl = organisaatioResourceUrl;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        Client clientWithJacksonSerializer = Client.create(cc);
        clientWithJacksonSerializer.setConnectTimeout(CONNECT_TIMEOUT);
        clientWithJacksonSerializer.setReadTimeout(READ_TIMEOUT);

        orgRes = clientWithJacksonSerializer.resource(String.format("%s/hae", this.organisaatioResourceUrl));
        orgRootRes = clientWithJacksonSerializer.resource(String.format("%s", this.organisaatioResourceUrl));
    }

    @Override
    public OrganisaatioRDTO getOrganisaatio(String oid) {
        return orgRootRes
                .path(oid)
                .queryParam("includeImage", "true")
                .accept(JSON_UTF8).get(OrganisaatioRDTO.class);
    }

    @Override
    public OrganisaatioHakutulos findOrganisaatio(String oid) {
        return orgRes
                .queryParam("noCache", String.format("%s", System.currentTimeMillis()))
                .queryParam("aktiiviset", "true")
                .queryParam("lakkautetut", "false")
                .queryParam("suunnitellut", "false")
                .queryParam("oid", oid)
                .queryParam("searchstr", "")
                .accept(JSON_UTF8)
                .get(new GenericType<OrganisaatioHakutulos>() {
                });
    }

    @Override
    public OrganisaatioHakutulos fetchOrganisaatiosByType(String organisaatioType) {
        return orgRes
                .queryParam("noCache", String.format("%s", System.currentTimeMillis()))
                .queryParam("aktiiviset", "true")
                .queryParam("lakkautetut", "false")
                .queryParam("suunnitellut", "false")
                .queryParam("organisaatiotyyppi", organisaatioType)
                .queryParam("searchstr", "")
                .accept(JSON_UTF8)
                .get(new GenericType<OrganisaatioHakutulos>() {
                });
    }

}
