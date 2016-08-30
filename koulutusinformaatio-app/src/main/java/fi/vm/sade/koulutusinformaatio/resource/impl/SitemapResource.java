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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.sun.jersey.api.view.Viewable;

import fi.vm.sade.koulutusinformaatio.service.SEOService;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/sitemap.xml")
@Produces("application/xml")
public class SitemapResource {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private SEOService seoService;
    private final OphProperties urlProperties;

    @Autowired
    public SitemapResource(SEOService seoService, OphProperties urlProperties) {
        this.seoService = seoService;
        this.urlProperties = urlProperties;
    }

    @GET
    public Viewable getSitemap() {
        Date lastModified = seoService.getSitemapTimestamp();
        Map<String, Object> model = Maps.newHashMap();
        model.put("sitemapLastModified", SDF.format(lastModified));
        model.put("sitemapUrl", urlProperties.url("koulutusinformaatio-app-web.sitemap"));
        model.put("wpSitemapUrl", urlProperties.url("wp.sitemap"));
        model.put("wpSitemapLastModified", SDF.format(new Date()));
        return new Viewable("/sitemap.ftl", model);
    }

}
