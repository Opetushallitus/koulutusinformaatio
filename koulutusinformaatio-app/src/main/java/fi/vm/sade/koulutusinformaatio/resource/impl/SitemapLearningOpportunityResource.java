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

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Component
@Path("/sitemap-learningopportunity.xml")
@Produces("application/xml")
public class SitemapLearningOpportunityResource {

    private final SEOSnapshotService seoSnapshotService;
    private static final Logger LOG = LoggerFactory.getLogger(SitemapLearningOpportunityResource.class);

    @Autowired
    public SitemapLearningOpportunityResource(SEOSnapshotService seoSnapshotService) {
        this.seoSnapshotService = seoSnapshotService;
    }

    @GET
    public Response getSitemap() {
        SnapshotDTO sitemap = seoSnapshotService.getSitemap();
        if (sitemap != null && sitemap.getContent() != null) {
            return Response.ok(sitemap.getContent()).build();
        }
        LOG.warn("Mongo did not contain a sitemap to serve.");
        return Response.status(404).build();
    }


}
