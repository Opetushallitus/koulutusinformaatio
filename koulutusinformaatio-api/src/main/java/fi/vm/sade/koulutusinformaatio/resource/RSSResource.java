package fi.vm.sade.koulutusinformaatio.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSFeedDTO;

/*
 * Interface for RSS resources
 */
@Path("/rss")
public interface RSSResource {

    /*
     * Returns application systems for calendar in RSS feed format
     */
    @GET
    @Path("/asCalendar")
    @Produces(MediaType.APPLICATION_XML)
    public RSSFeedDTO getApplicationSystemCalendarAsRss(@DefaultValue("fi") @QueryParam("lang") String lang);
    
}
