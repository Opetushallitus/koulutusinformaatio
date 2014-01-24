/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.collect.Maps;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/sitemap.xml")
@Produces("application/xml")
public class SitemapResource {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private SEOService seoService;
    private String sitemapUrl;
    private String wordpressSitemapUrl;

    @Autowired
    public SitemapResource(SEOService seoService,
                           @Value("${koulutusinformaatio.sitemap.url}") String sitemapUrl,
                           @Value("${koulutusinformaatio.sitemap.wp-url}") String wordpressSitemapUrl) {
        this.seoService = seoService;
        this.sitemapUrl = sitemapUrl;
        this.wordpressSitemapUrl = wordpressSitemapUrl;
    }

    @GET
    public Viewable getSitemap() {
        Date lastModified = seoService.getSitemapTimestamp();
        Map<String, Object> model = Maps.newHashMap();
        model.put("sitemapLastModified", SDF.format(lastModified));
        model.put("sitemapUrl", sitemapUrl);
        model.put("wpSitemapUrl", wordpressSitemapUrl);
        model.put("wpSitemapLastModified", SDF.format(new Date()));
        return new Viewable("/sitemap.ftl", model);
    }

}
