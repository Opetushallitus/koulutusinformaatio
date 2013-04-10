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

import javax.ws.rs.WebApplicationException;

/**
 * Provides utility methods that turn
 * {@link fi.vm.sade.koulutusinformaatio.domain.exception.KIException} objects thrown
 * by the service layer into {@link javax.ws.rs.WebApplicationException}
 * objects that can be thrown from resource methods.
 *
 * @author Hannu Lyytikainen
 */
public class KIExceptionHandler {

    public static WebApplicationException resolveException(Exception e) {
        WebApplicationException webException = new WebApplicationException();



        return webException;
    }


}
