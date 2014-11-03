package fi.vm.sade.koulutusinformaatio.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;

@Path("/as")
public interface ApplicationSystemResource {

    
    @GET
    @Path("/fetchForCalendar")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CalendarApplicationSystemDTO> fetchApplicationSystemsForCalendar(
            @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
