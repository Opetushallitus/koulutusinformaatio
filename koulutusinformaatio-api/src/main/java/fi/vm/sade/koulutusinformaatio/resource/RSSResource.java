package fi.vm.sade.koulutusinformaatio.resource;

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
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSFeedDTO;

/*
 * Interface for RSS resources
 */
@Path("/rss")
@Api(value = "/rss", description = "RSS feedin rajapinta")
public interface RSSResource {

    /*
     * Returns all application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Kaikki tulevat yhteishaut",
    notes = "",
    response = RSSFeedDTO.class)
    public RSSFeedDTO getApplicationSystemCalendarAsRss(
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns higher education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/higherEducation")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Korkeakoulutuksen yhteishaut",
        notes = "",
        response = RSSFeedDTO.class)
    public RSSFeedDTO getApplicationSystemCalendarForHigherEducationAsRss(
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns vocational education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/vocationalEducation")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Ammatillisen koulutuksen yhteishaut",
        notes = "",
        response = RSSFeedDTO.class)
    public RSSFeedDTO getApplicationSystemCalendarForVocationalEducationAsRss(
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("lang") String lang);
    
    /*
     * Returns preparatory education after basic education application systems in RSS feed format
     */
    @GET
    @Path("/asCalendar/preparatoryEducation")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Perusopetuken jälkeiset yhteishaut",
        notes = "",
        response = RSSFeedDTO.class)
    public RSSFeedDTO getApplicationSystemCalendarForPreparatoryEducationAsRss(
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("lang") String lang);
    
}
