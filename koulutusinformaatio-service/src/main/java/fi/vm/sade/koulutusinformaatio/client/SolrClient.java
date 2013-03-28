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

package fi.vm.sade.koulutusinformaatio.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

@Component
public class SolrClient {

    @Value("${solr.base.url}")
    private String solrUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    public String update(final ByteArrayOutputStream result) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        final HttpEntity<byte[]> httpEntity = new HttpEntity<byte[]>(result.toByteArray(), headers);
        final HashMap<String, String> uriVariables = new HashMap<String, String>();
        uriVariables.put("optimize", "true");
        return restTemplate.postForObject(solrUrl + "update?optimize=true", httpEntity, String.class, uriVariables);
    }
}
