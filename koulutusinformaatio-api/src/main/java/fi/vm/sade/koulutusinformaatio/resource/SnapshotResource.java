package fi.vm.sade.koulutusinformaatio.resource;

import com.wordnik.swagger.annotations.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/snapshot")
@Api(value = "/snapshot", description = "Hakukohteiden hakukoneoptimoidun HTML-muotoisen kuvauksen tallennus ja haku")
public interface SnapshotResource {
    @GET
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    @ApiOperation(value = "Hakukohteen hakukoneoptimoidun HTML-muotoisen kuvauksen haku oid:llä",
            notes = "",
            response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Haettu HTML-muotoinen kuvaus palautettu kutsujalle onnistuneesti"),
            @ApiResponse(code = 404, message = "Haettua HTML-muotoista kuvuasta ei löytynyt")})
    public Response getSnapshotContent(@ApiParam(value = "Hakukohteen oid, josta HTML-muotoinen kuvaus on generoitu") @PathParam("oid") String oid);

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Tallentaa hakukohteen hakukoneoptimoidun HTML-kuvauksen tietokantaan",
            notes = "",
            response = SnapshotDTO.class)
    @ApiResponse(code = 201, message = "HTML-muotoisen kuvauksen tallennus onnistui")
    public Response createSnapshot(@ApiParam("Hakukohteen HTML-muotoinen kuvaus") SnapshotDTO snapshot);
}
