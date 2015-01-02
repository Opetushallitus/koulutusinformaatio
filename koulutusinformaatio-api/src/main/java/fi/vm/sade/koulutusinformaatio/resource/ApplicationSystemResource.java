package fi.vm.sade.koulutusinformaatio.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;

@Path("/as")
@Api(value = "/as", description="Kuvaus")
public interface ApplicationSystemResource {

    
    @GET
    @Path("/fetchForCalendar")
    @ApiOperation(httpMethod = "GET", value = "/fetchForCalendar <a href=https://github.com/Opetushallitus/koulutusinformaatio/blob/devel/koulutusinformaatio-api/src/main/java/fi/vm/sade/koulutusinformaatio/resource/ApplicationSystemResource.java search>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Click to see it on GitHub</a>", response = CalendarApplicationSystemDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public List<CalendarApplicationSystemDTO> fetchApplicationSystemsForCalendar(
            @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
