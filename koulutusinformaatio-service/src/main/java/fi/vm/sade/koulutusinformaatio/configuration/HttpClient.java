package fi.vm.sade.koulutusinformaatio.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {
    public static final String CALLER_ID = "1.2.246.562.10.00000000001.koulutusinformaatio.backend";
    private OphHttpClient client;

    @Autowired
    public HttpClient(OphProperties urlConfiguration) {
        client = ApacheOphHttpClient.createDefaultOphClient(CALLER_ID, urlConfiguration, 60000, 600L);
    }

    public static ObjectMapper createJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public OphHttpClient getClient() {
        return client;
    }
}
