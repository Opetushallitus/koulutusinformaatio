package fi.vm.sade.koulutusinformaatio.resource.impl;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/raamit")
public class RaamitResource {

    private String raamitLocation;

    @Autowired
    public RaamitResource(@Value("${oppija-raamit.url}") String raamitLocation) {
        this.raamitLocation = raamitLocation;
    }

    @GET
    @Path("/load")
    public Response load() throws URISyntaxException {

        StringBuilder b = new StringBuilder()
                .append("if (document.location.hash.indexOf(\"skipRaamit\") < 0) {\n")
                .append("    var raamit = document.getElementById(\"apply-raamit\")\n")
                .append("    if(!raamit) {\n")
                .append("        raamit = document.createElement(\"script\")\n")
                .append("        raamit.id = \"apply-raamit\"\n")
                .append("        raamit.src = \"").append(raamitLocation).append("/oppija-raamit/apply-raamit.js\"\n")
                .append("        document.getElementsByTagName(\"head\")[0].appendChild(raamit)")
                .append("    }\n")
                .append("}\n");

        return Response.ok(b.toString(), MediaType.APPLICATION_JSON).build();
    }
}
