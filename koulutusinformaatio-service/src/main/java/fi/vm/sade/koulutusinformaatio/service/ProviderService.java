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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

/**
 * Hides integration to the Organisaatio service.
 *
 * @author Hannu Lyytikainen
 */
public interface ProviderService {

    public Provider getByOID(String oid) throws KoodistoException, MalformedURLException, IOException, ResourceNotFoundException;
    public List<OrganisaatioPerustieto> fetchOpplaitokset() throws MalformedURLException, IOException, ResourceNotFoundException;
    public List<OrganisaatioPerustieto> fetchToimipisteet() throws MalformedURLException, IOException, ResourceNotFoundException;
    void clearCache();
}
