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

package fi.vm.sade.koulutusinformaatio.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;

/**
 * @author Mikko Majapuro
 */
@Path("/basket")
@Api(value = "/basket", description = "Muistilista")
public interface BasketResource {

    @GET
    @Path("items")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Muistilistan populoimiseen tarkoitettu hakukohteiden rajapinta",
        notes = "",
        response = BasketItemDTO.class,
        responseContainer = "List")
    List<BasketItemDTO> getBasketItems(
            @ApiParam(value = "Lista oideja") @QueryParam("aoId") List<String> aoId, 
            @ApiParam(value = "Kieli") @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
