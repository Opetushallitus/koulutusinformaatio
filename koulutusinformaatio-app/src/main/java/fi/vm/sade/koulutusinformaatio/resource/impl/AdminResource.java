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

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.DataStatusDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/admin")
public class AdminResource {

    private UpdateService updateService;
    private LearningOpportunityService learningOpportunityService;
    private ModelMapper modelMapper;

    @Autowired
    public AdminResource(UpdateService updateService,
                         LearningOpportunityService learningOpportunityService,
                         ModelMapper modelMapper) {
        this.updateService = updateService;
        this.learningOpportunityService = learningOpportunityService;
        this.modelMapper = modelMapper;
    }

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
        return Response.seeOther(new URI("admin/status")).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public DataStatusDTO dataStatus() {
        DataStatus status = learningOpportunityService.getLastDataStatus();
        DataStatusDTO dto = new DataStatusDTO();
        dto.setLastUpdated(status.getLastUpdated());
        dto.setLastUpdatedStr(status.getLastUpdated().toString());
        long millis = status.getLastUpdateDuration();
        dto.setLastUpdateDuration(millis);
        dto.setLastUpdateDurationStr(String.format("%d hours, %d minutes", millis/(1000*60*60), millis/(1000*60)));
        dto.setRunning(updateService.isRunning());
        return dto;
    }

}
