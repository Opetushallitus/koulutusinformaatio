package fi.vm.sade.koulutusinformaatio.configuration;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class UrlConfiguration extends OphProperties {
    public static final String SPRING_IT_PROFILE = "it";

    public UrlConfiguration() {
        this(System.getProperty("spring.profiles.active"));
    }

    public UrlConfiguration(String activeSpringProfile) {
        //debugMode();
        addFiles("/koulutusinformaatio-app-oph.properties");
        if(!SPRING_IT_PROFILE.equals(activeSpringProfile)) {
            addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        }
        frontProperties.put("tarjonta-app.baseUrl", "${url-virkailija}");
    }
}
