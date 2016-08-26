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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystemParameters;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;

/**
 * 
 * @author Markus
 *
 */
@Service
public class ParameterServiceImpl implements ParameterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterServiceImpl.class);
    private final OphHttpClient client;

    ObjectMapper mapper = HttpClient.createJacksonMapper();
    
    private Map<String, ApplicationSystemParameters> cache = new HashMap<String, ApplicationSystemParameters>();

    @Autowired
    public ParameterServiceImpl(HttpClient client) {
        this.client = client.getClient();
        mapper = HttpClient.createJacksonMapper();
    }
    
    @Override
    public ApplicationSystemParameters getParametersForHaku(final String oid) {
        
        if (cache.containsKey(oid)) {
            return cache.get(oid);
        }
        
        try {
            return client.get("ohjausparametrit-service.parametri", oid).expectStatus(200).execute(new OphHttpResponseHandler<ApplicationSystemParameters>() {
                @Override
                public ApplicationSystemParameters handleResponse(OphHttpResponse response) throws IOException {
                    ApplicationSystemParameters params = mapper.readValue(response.asInputStream(), ApplicationSystemParameters.class);
                    cache.put(oid, params);
                    return params;
                }
            });
        } catch (Exception ex) {
            cache.put(oid, new ApplicationSystemParameters());
            LOGGER.debug("Error getting parameters for haku: {}", oid, ex);
        }
        return null;        
    }

    @Override
    public void clearCache() {
        this.cache = new HashMap<String, ApplicationSystemParameters>();
    }

}
