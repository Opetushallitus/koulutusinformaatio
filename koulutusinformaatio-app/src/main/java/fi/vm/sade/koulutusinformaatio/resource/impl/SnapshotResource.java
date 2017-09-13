package fi.vm.sade.koulutusinformaatio.resource.impl;

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Component
@Path("/snapshot")
public class SnapshotResource {
    private final SEOSnapshotService seoSnapshotService;
    private static final Logger LOG = LoggerFactory.getLogger(SnapshotResource.class);

    @Autowired
    public SnapshotResource(SEOSnapshotService seoSnapshotService) {
        this.seoSnapshotService = seoSnapshotService;
    }

    @GET
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response getSnapshotContent(final String oid) {
        try {
            SnapshotDTO snapshot = seoSnapshotService.getSnapshot(oid);
            if (snapshot == null) {
                LOG.warn("No snapshot found for oid {}", oid);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity(snapshot.getContent()).build();
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSnapshot(final SnapshotDTO snapshot) {
        try {
            seoSnapshotService.createSnapshot(snapshot);
            URI redirectTo = URI.create("/snapshot/" + snapshot.getOid());
            return Response.created(redirectTo).entity(snapshot).build();
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
