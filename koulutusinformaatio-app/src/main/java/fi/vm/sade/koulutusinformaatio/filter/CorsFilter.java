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

package fi.vm.sade.koulutusinformaatio.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @author Mikko Majapuro
 */
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {

        if ( containerRequest.getRequestHeaders().containsKey("access-control-request-method") ) {
            for ( String value : containerRequest.getRequestHeaders().get("access-control-request-method") ) {
                containerResponse.getHttpHeaders().add("Access-Control-Allow-Methods", value );
            }
        }
        if ( containerRequest.getRequestHeaders().containsKey("access-control-request-headers") ) {
            for ( String value : containerRequest.getRequestHeaders().get("access-control-request-headers") ) {
                containerResponse.getHttpHeaders().add("Access-Control-Allow-Headers", value );
            }
        }
        containerResponse.getHttpHeaders().add("Access-Control-Allow-Origin", "https://test-oppija.oph.ware.fi https://itest-oppija.oph.ware.fi https://testi.opintopolku.fi https://opintopolku.fi https://koulutus.opintopolku.fi https://itest-virkailija.oph.ware.fi https://test-virkailija.oph.ware.fi https://testi.virkailija.opintopolku.fi https://virkailija.opintopolku.fi https://koulutus.virkailija.opintopolku.fi");
        return containerResponse;
    }
}
