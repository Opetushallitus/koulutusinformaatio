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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.dto.DataStatusDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.PartialUpdateService;
import fi.vm.sade.koulutusinformaatio.service.SEOService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import fi.vm.sade.koulutusinformaatio.service.impl.RunningServiceChecker;
import fi.vm.sade.koulutusinformaatio.service.tester.HakukohdeTester;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/admin")
public class AdminResource {
    private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

    private UpdateService updateService;
    private IncrementalUpdateService incrementalUpdateService;
    private LearningOpportunityService learningOpportunityService;
    private PartialUpdateService partialUpdateService;
    private ModelMapper modelMapper;
    private SEOService seoService;
    private RunningServiceChecker runningServiceChecker;
    private HakukohdeTester hakukohdeTester;

    @Autowired
    public AdminResource(UpdateService updateService,
                         LearningOpportunityService learningOpportunityService,
                         ModelMapper modelMapper, SEOService seoService,
                         IncrementalUpdateService incrementalUpdateService,
                         PartialUpdateService partialUpdateService,
                         RunningServiceChecker runningServiceChecker,
                         HakukohdeTester hakukohdeTester) {
        this.updateService = updateService;
        this.learningOpportunityService = learningOpportunityService;
        this.modelMapper = modelMapper;
        this.seoService = seoService;
        this.incrementalUpdateService = incrementalUpdateService;
        this.partialUpdateService = partialUpdateService;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.runningServiceChecker = runningServiceChecker;
        this.hakukohdeTester = hakukohdeTester;
    }

    @GET
    @Path("/update")
    public Response updateEducationData() throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                updateService.updateAllEducationData();
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }

    @GET
    @Path("/updateArticles")
    public Response updateArticles() throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                updateService.updateArticles();
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
    @GET
    @Path("/increment")
    public Response incrementEducationData() throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                incrementalUpdateService.updateChangedEducationData();
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
    @GET
    @Path("/partial/lo/{oid}")
    public Response partialUpdateEducationData(@PathParam("oid") String oid) throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                partialUpdateService.updateEducation(oid);
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }
    
    @GET
    @Path("/partial/as/{oid}")
    public Response partialUpdateApplicatonSystemData(@PathParam("oid") String oid) throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                partialUpdateService.updateApplicationSystem(oid);
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }

    @GET
    @Path("/partial/ao/{oid}")
    public Response partialUpdateApplicationOptionData(@PathParam("oid") String oid) throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                partialUpdateService.updateApplicationOption(oid);
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }

    @GET
    @Path("/partial/general")
    public Response partialUpdateApplicationOptionData() throws URISyntaxException {
        try {
            if (!runningServiceChecker.isAnyServiceRunning()) {
                partialUpdateService.updateGeneralData();
            }
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
        return Response.seeOther(new URI("admin/status")).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public DataStatusDTO dataStatus() {
        DataStatus status = learningOpportunityService.getLastDataStatus();
        if (status == null) {
            DataStatusDTO dto = new DataStatusDTO();
            dto.setRunning(true);
            dto.setLastSuccessfulFinishedStr("Finalizing indexing.");
            return dto;
        }
        DataStatusDTO dto = new DataStatusDTO();
        dto.setLastUpdateFinished(status.getLastUpdateFinished());
        dto.setLastUpdateFinishedStr(status.getLastUpdateFinished().toString());
        long millis = status.getLastUpdateDuration();
        dto.setLastUpdateDuration(millis);
        dto.setLastUpdateDurationStr(String.format("%d hours, %d minutes", millis / 3600000, millis / 60000 % 60));
        dto.setLastUpdateOutcome(status.getLastUpdateOutcome());
        Date runningSince = runningServiceChecker.getRunningSince();
        dto.setRunning(runningSince != null);
        dto.setRunningSince(runningSince);
        dto.setRunningSinceStr(runningSince != null ? runningSince.toString() : null);
        dto.setSnapshotRenderingRunning(seoService.isRunning());
        DataStatus succStatus = learningOpportunityService.getLastSuccesfulDataStatus();
        if (succStatus != null) {
            dto.setLastSuccessfulFinished(succStatus.getLastUpdateFinished());
            dto.setLastSuccessfulFinishedStr(succStatus.getLastUpdateFinished().toString());
            long successmillis = succStatus.getLastUpdateDuration();
            dto.setLastSuccessfulDurationStr(String.format("%d hours, %d minutes", successmillis / 3600000, successmillis / 60000 % 60));
            if (updateService.isRunning()) {
                String setFullUpdateProgressStr = succStatus.getProgressCounter() > 0 ? updateService.getProgressCounter() * 100
                        / succStatus.getProgressCounter() + " %"
                        : "No prior data stored";
                dto.setFullUpdateProgressStr(setFullUpdateProgressStr);
            } else {
                dto.setFullUpdateProgressStr("Full update not running");
            }
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
    
    @GET
    @Path("/test")
    public Response test() throws URISyntaxException {
        hakukohdeTester.testHakukohteet();
        return Response.seeOther(new URI("admin/status")).build();
    }

}
