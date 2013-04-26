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

import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

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

    @GET
    @Path("/drop")
    public String dropIndexes() {
        try {
            indexerService.dropLOPs();
            indexerService.dropLOs();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
        return "indexes dropped";
    }

    @GET
    @Path("/update")
    public String updateEducationData() {
        try {
            updateService.updateAllEducationData();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }

        return "education data updated";
    }

}
