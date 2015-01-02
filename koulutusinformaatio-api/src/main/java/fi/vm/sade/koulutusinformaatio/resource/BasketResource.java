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

import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Path("/basket")
@Api(value = "/basket", description="Kuvaus")
public interface BasketResource {

    @GET
    @Path("/items")
    @ApiOperation(httpMethod = "GET", value = "/items <a href=https://github.com/Opetushallitus/koulutusinformaatio/blob/devel/koulutusinformaatio-api/src/main/java/fi/vm/sade/koulutusinformaatio/resource/BasketResource.java search>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Click to see it on GitHub</a>", response = BasketItemDTO.class)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    List<BasketItemDTO> getBasketItems(@QueryParam("aoId") List<String> aoId, @DefaultValue("fi") @QueryParam("uiLang") String uiLang);
}
