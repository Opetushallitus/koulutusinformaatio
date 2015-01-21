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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.DataStatusDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/admin")
public class AdminResource {

    private UpdateService updateService;
    private IncrementalUpdateService incrementalUpdateService;
    private LearningOpportunityService learningOpportunityService;
    private ModelMapper modelMapper;
    private SEOService seoService;

    @Autowired
    public AdminResource(UpdateService updateService,
                         LearningOpportunityService learningOpportunityService,
                         ModelMapper modelMapper, SEOService seoService,
                         IncrementalUpdateService incrementalUpdateService) {
        this.updateService = updateService;
        this.learningOpportunityService = learningOpportunityService;
        this.modelMapper = modelMapper;
        this.seoService = seoService;
        this.incrementalUpdateService = incrementalUpdateService;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @GET
    @Path("/update")
    public Response updateEducationData() throws URISyntaxException {
        try {
            if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                updateService.updateAllEducationData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
    @GET
    @Path("/updateArticles")
    public Response updateArticles() throws URISyntaxException {
        try {
            if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                updateService.updateArticles();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
    @GET
    @Path("/increment")
    public Response incrementEducationData() throws URISyntaxException {
        try {
            if (!updateService.isRunning() && !incrementalUpdateService.isRunning()) {
                incrementalUpdateService.updateChangedEducationData();
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
        dto.setLastUpdateFinished(status.getLastUpdateFinished());
        dto.setLastUpdateFinishedStr(status.getLastUpdateFinished().toString());
        long millis = status.getLastUpdateDuration();
        dto.setLastUpdateDuration(millis);
        dto.setLastUpdateDurationStr(String.format("%d hours, %d minutes", millis / 3600000, millis / 60000 % 60));
        dto.setLastUpdateOutcome(status.getLastUpdateOutcome());
        dto.setRunning(updateService.isRunning() || incrementalUpdateService.isRunning());
        if (dto.isRunning() && updateService.isRunning()) {
            dto.setRunningSince(new Date(updateService.getRunningSince()));
            dto.setRunningSinceStr(new Date(updateService.getRunningSince()).toString());
        } else if (dto.isRunning() && incrementalUpdateService.isRunning()) {
            dto.setRunningSince(new Date(incrementalUpdateService.getRunningSince()));
            dto.setRunningSinceStr(new Date(incrementalUpdateService.getRunningSince()).toString());
        }
        dto.setSnapshotRenderingRunning(seoService.isRunning());
        DataStatus succStatus = learningOpportunityService.getLastSuccesfulDataStatus();
        if (succStatus != null) {
            dto.setLastSuccessfulFinished(succStatus.getLastUpdateFinished());
            dto.setLastSuccessfulFinishedStr(succStatus.getLastUpdateFinished().toString());
        }
        
        
        return dto;
    }

    @GET
    @Path("/seo")
    public Response prerender() throws URISyntaxException {
        if (!seoService.isRunning()) {
            seoService.update();
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
}
