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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
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
    private final OphProperties urlProperties;

    ObjectMapper mapper = new ObjectMapper();
    
    private Map<String, ApplicationSystemParameters> cache = new HashMap<String, ApplicationSystemParameters>();

    @Autowired
    public ParameterServiceImpl(OphProperties urlProperties) {
        this.urlProperties = urlProperties;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    @Override
    public ApplicationSystemParameters getParametersForHaku(String oid) {
        
        if (cache.containsKey(oid)) {
            return cache.get(oid);
        }
        
        try {
            URL url = new URL(urlProperties.url("ohjausparametrit-service.parametri", oid));

            HttpURLConnection conn = (HttpURLConnection) (url.openConnection());

            conn.setRequestMethod(SolrConstants.GET);
            conn.connect();

            ApplicationSystemParameters params = mapper.readValue(conn.getInputStream(), ApplicationSystemParameters.class);
            cache.put(oid, params);
            return params;
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
