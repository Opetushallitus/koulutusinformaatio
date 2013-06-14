/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/admin")
public class AdminResource {

    @Autowired
    UpdateService updateService;

    @Autowired
    IndexerService indexerService;

    @Autowired
    private LearningOpportunityService learningOpportunityService;

    @GET
    @Path("/update")
    public Response updateEducationData() throws URISyntaxException {
        try {
            if (!updateService.isRunning()) {
                updateService.updateAllEducationData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw KIExceptionHandler.resolveException(e);
        }

        return Response.seeOther(new URI("status")).build();
    }

    @GET
    @Path("/status")
    public String dataStatus() {
        Date lastUpdate = learningOpportunityService.getLastDataUpdated();
        String msg = updateService.isRunning() ? "Data update is running... Last data update " : "Last data update ";
        return msg + lastUpdate;
    }

}
