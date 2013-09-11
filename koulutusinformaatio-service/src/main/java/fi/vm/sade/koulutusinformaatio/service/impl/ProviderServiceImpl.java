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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    private WebResource webResource;
    private ConversionService conversionService;

    @Autowired
    public ProviderServiceImpl(@Value("${organisaatio.api.rest.url}") final String organisaatioResourceUrl,
                               ConversionService conversionService) {
        this.conversionService = conversionService;
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(organisaatioResourceUrl);
    }

    @Override
    public Provider getByOID(String oid) throws KoodistoException {
        WebResource oidResource = webResource.path(oid);
        OrganisaatioRDTO organisaatioRDTO = oidResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                .get(new GenericType<OrganisaatioRDTO>() {
                });
        return conversionService.convert(organisaatioRDTO, Provider.class);
    }
}
