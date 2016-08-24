package fi.vm.sade.koulutusinformaatio.resource.impl;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/rest/frontProperties")
@Component
public class FrontPropertiesResource {

    @Autowired
    OphProperties urlConfiguration;

    @GET
    @Produces("application/javascript;charset=UTF-8")
    public String frontProperties() {
        return "window.urls.override=" + urlConfiguration.frontPropertiesToJson();
    }
}
