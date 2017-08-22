package fi.vm.sade.koulutusinformaatio.resource.impl;

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.SnapshotResource;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.net.URI;

@Component
public class SnapshotResourceImpl implements SnapshotResource {
    private final SEOSnapshotService seoSnapshotService;

    @Autowired
    public SnapshotResourceImpl(SEOSnapshotService seoSnapshotService) {
        this.seoSnapshotService = seoSnapshotService;
    }

    @Override
    public Response getSnapshotContent(final String oid) {
        try {
            SnapshotDTO snapshot = seoSnapshotService.getSnapshot(oid);
            if (snapshot == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity(snapshot.getContent()).build();
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
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
