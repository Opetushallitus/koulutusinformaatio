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

package fi.vm.sade.koulutusinformaatio.exception;

import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods that turn
 * {@link fi.vm.sade.koulutusinformaatio.domain.exception.KIException} objects thrown
 * by the service layer into {@link javax.ws.rs.WebApplicationException}
 * objects that can be thrown from resource methods.
 *
 * @author Hannu Lyytikainen
 */
public class KIExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KIExceptionHandler.class);
    
    public static HTTPException resolveException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        HTTPException webException = null;
        if (e instanceof KIException) {
            if(e instanceof SearchException) {
                webException = new HTTPException(Response.Status.INTERNAL_SERVER_ERROR, "Error occurred while searching");
            } else if (e instanceof ResourceNotFoundException) {
                webException = new HTTPException(Response.Status.NOT_FOUND, e.getMessage());
            } else if (e instanceof InvalidParametersException) {
                webException = new HTTPException(Response.Status.BAD_REQUEST, e.getMessage());
            } else {
                webException = new HTTPException(Response.Status.INTERNAL_SERVER_ERROR, "Internal error occurred");
            }
        }
        else {
            webException = new HTTPException(Response.Status.INTERNAL_SERVER_ERROR, "Internal error occurred");
        }
        
        return webException;
    }


}
