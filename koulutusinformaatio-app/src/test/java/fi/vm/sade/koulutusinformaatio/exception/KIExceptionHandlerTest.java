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

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;

/**
 * @author Hannu Lyytikainen
 */
public class KIExceptionHandlerTest {

    @Test
    public void testSearchException() {
        SearchException se = new SearchException("search exception");
        HTTPException e = KIExceptionHandler.resolveException(se);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getResponse().getStatus());
    }

    @Test
    public void testResourceNotFoundException() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("resource not found");
        HTTPException e = KIExceptionHandler.resolveException(resourceNotFoundException);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        assertEquals(resourceNotFoundException.getMessage(),
                ((ErrorPayload)e.getResponse().getEntity()).getMessage());
    }

    @Test
    public void testInvalidParametersException() {
        InvalidParametersException ipe = new InvalidParametersException("invalid parameters");
        HTTPException e = KIExceptionHandler.resolveException(ipe);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        assertEquals(ipe.getMessage(),
                ((ErrorPayload)e.getResponse().getEntity()).getMessage());
    }

    @Test
    public void testUndefinedKIException() {
        TarjontaParseException tpe = new TarjontaParseException("tarjonta parse exception");
        HTTPException e = KIExceptionHandler.resolveException(tpe);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getResponse().getStatus());
    }

    @Test
    public void testNonKIException() {
        NullPointerException npe = new NullPointerException("npe");
        HTTPException e = KIExceptionHandler.resolveException(npe);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getResponse().getStatus());
    }
}
