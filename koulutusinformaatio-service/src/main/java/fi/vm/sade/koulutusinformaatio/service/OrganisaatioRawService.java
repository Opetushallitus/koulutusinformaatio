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

package fi.vm.sade.koulutusinformaatio.service;


import fi.vm.sade.koulutusinformaatio.domain.exception.OrganisaatioException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

/**
 * Can be used to access organisaatio APIs. Returns raw organisaatio DTO objects as they are
 * returned from API.
 *
 */
public interface OrganisaatioRawService {

    /**
     * Fetches the organizations with the given oid from Organization service.
     * @param oid The given oid
     * @return The Organization with the given oid.
     * @throws ResourceNotFoundException
     */
    OrganisaatioRDTO getOrganisaatio(String oid) throws OrganisaatioException;
    
    /**
     * Fetches organizations of given organisaatioType from organisaatio service.
     * 
     * @param organisaatioType The type of organization, e.g. Oppilaitos
     * @return A result object containing a list of organizations of the given type.
     * @throws ResourceNotFoundException
     */
    OrganisaatioHakutulos fetchOrganisaatiosByType(String organisaatioType);

    OrganisaatioHakutulos findOrganisaatio(String oid) throws OrganisaatioException;

}