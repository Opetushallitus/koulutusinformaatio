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

package fi.vm.sade.koulutusinformaatio.resource;

import fi.vm.sade.koulutusinformaatio.domain.search.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.search.Organization;
import fi.vm.sade.koulutusinformaatio.service.ApplicationOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Controller for education institute search
 *
 * @author Mikko Majapuro
 */
@Component
@Path(EducationResource.EDUCATION_CONTROLLER_PATH)
public class EducationResource {

    public static final String TERM = "term";
    public static final String PREREQUISITE = "prerequisite";
    public static final String VOCATIONAL = "vocational";
    public static final String EDUCATION_CONTROLLER_PATH = "/education";

    @Qualifier("applicationOptionServiceSolrImpl")
    @Autowired
    ApplicationOptionService applicationOptionService;

    @GET
    @Path("/{hakuId}/organisaatio/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Organization> searchOrganisaatio(@PathParam("hakuId") final String hakuId,
                                                 @QueryParam(TERM) final String term,
                                                 @QueryParam(PREREQUISITE) final String prerequisite,
                                                 @QueryParam(VOCATIONAL) final String vocational) {
        return applicationOptionService.searchOrganisaatio(hakuId, term, prerequisite, vocational);
    }

    @GET
    @Path("/{hakuId}/hakukohde/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ApplicationOption> searchHakukohde(@PathParam("hakuId") final String hakuId,
                                                   @QueryParam("organisaatioId") final String organisaatioId,
                                                   @QueryParam(PREREQUISITE) final String prerequisite,
                                                   @QueryParam(VOCATIONAL) final String vocational) {
        return applicationOptionService.searchHakukohde(hakuId, organisaatioId, prerequisite, vocational);
    }
}
