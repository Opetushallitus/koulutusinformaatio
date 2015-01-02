package fi.vm.sade.koulutusinformaatio.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.koulutusinformaatio.domain.dto.LocationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSFeedDTO;

/*
 * Interface for RSS resources
 */
@Path("/rss")
@Api(value = "/rss", description="Kuvaus")
public interface RSSResource {

    /*
     * Returns all application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar")
    @ApiOperation(httpMethod = "GET", value = "/asCalendar <a href=https://github.com/Opetushallitus/koulutusinformaatio/blob/devel/koulutusinformaatio-api/src/main/java/fi/vm/sade/koulutusinformaatio/resource/RSSResource.java search>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Click to see it on GitHub</a>", response = RSSFeedDTO.class)
    @Produces(MediaType.APPLICATION_XML)
    public RSSFeedDTO getApplicationSystemCalendarAsRss(@DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns higher education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/higherEducation")
    @ApiOperation(httpMethod = "GET", value = "/asCalendar/higherEducation", response = RSSFeedDTO.class)
    @Produces(MediaType.APPLICATION_XML)
    public RSSFeedDTO getApplicationSystemCalendarForHigherEducationAsRss(@DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns vocational education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/vocationalEducation")
    @ApiOperation(httpMethod = "GET", value = "/asCalendar/vocationalEducation", response = RSSFeedDTO.class)
    @Produces(MediaType.APPLICATION_XML)
    public RSSFeedDTO getApplicationSystemCalendarForVocationalEducationAsRss(@DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns preparatory education after basic education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/preparatoryEducation")
    @ApiOperation(httpMethod = "GET", value = "/asCalendar/preparatoryEducation", response = RSSFeedDTO.class)
    @Produces(MediaType.APPLICATION_XML)
    public RSSFeedDTO getApplicationSystemCalendarForPreparatoryEducationAsRss(@DefaultValue("fi") @QueryParam("lang") String lang);
    
}
