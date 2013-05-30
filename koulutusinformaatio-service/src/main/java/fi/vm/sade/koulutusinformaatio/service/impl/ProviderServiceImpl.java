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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.OrganisaatioResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderServiceImpl implements ProviderService {

    private OrganisaatioResource organisaatioResource;
    private ConversionService conversionService;
    @Autowired
    private KoodistoService koodistoService;

    public ProviderServiceImpl(OrganisaatioResource organisaatioResource, ConversionService conversionService) {
        this.organisaatioResource = organisaatioResource;
        this.conversionService = conversionService;
    }

    @Override
    public Provider getByOID(String oid) throws KoodistoException {
        Provider provider = conversionService.convert(organisaatioResource.getOrganisaatioByOID(oid), Provider.class);
        return updateCodeValues(provider);
    }

    private Provider updateCodeValues(final Provider provider) throws KoodistoException {
        if (provider != null) {
            updateAddressCodeValues(provider.getPostalAddress());
            updateAddressCodeValues(provider.getVisitingAddress());
        }
        return provider;
    }

    private void updateAddressCodeValues(final Address addrs) throws KoodistoException {
        if (addrs != null) {
            addrs.setPostalCode(koodistoService.searchFirstCodeValue(addrs.getPostalCode()));
        }
    }
}
