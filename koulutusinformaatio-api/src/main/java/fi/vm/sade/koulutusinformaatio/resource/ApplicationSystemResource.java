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
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;

@Path("/as")
@Api(value = "/as", description = "Kalenterin haut")
public interface ApplicationSystemResource {

    
    @GET
    @Path("/fetchForCalendar")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Kalenterissa näytettävien avointen hakujen rajapinta",
        notes = "",
        response = CalendarApplicationSystemDTO.class,
        responseContainer = "List")
    public List<CalendarApplicationSystemDTO> fetchApplicationSystemsForCalendar(
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
