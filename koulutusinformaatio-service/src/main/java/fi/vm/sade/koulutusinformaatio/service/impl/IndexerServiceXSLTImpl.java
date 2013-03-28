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


import fi.vm.sade.koulutusinformaatio.client.SolrClient;
import fi.vm.sade.koulutusinformaatio.client.TarjontaClient;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Deprecated
public class IndexerServiceXSLTImpl implements IndexerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceXSLTImpl.class);

    private final HttpSolrServer httpSolrServer;

    private final TarjontaClient tarjontaClient;

    private final SolrClient client;
    @Autowired
    public IndexerServiceXSLTImpl(@Qualifier("HttpSolrServer") HttpSolrServer httpSolrServer,
                                  TarjontaClient tarjontaClient, SolrClient client) {
        this.httpSolrServer = httpSolrServer;
        this.tarjontaClient = tarjontaClient;
        this.client = client;
    }

    @Override
    public String update() {

        final Source source = tarjontaClient.retrieveTarjontaAsSource();
        try {
            final ByteArrayOutputStream result = transform(source);
            return client.update(result);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return "Indeksin päivitys epäonnistui ";
        }
    }

    private ByteArrayOutputStream transform(Source source) throws IOException, TransformerException {
        final ClassPathResource classPathResource = new ClassPathResource("xml/xslt/tarjonta.xsl");
        StreamSource streamSource = new StreamSource(classPathResource.getInputStream());
        //next line is needed for relative imports in xslt to work
        streamSource.setSystemId(classPathResource.getFile());
        Transformer transformer = TransformerFactory.newInstance().newTransformer(streamSource);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final StreamResult outputTarget = new StreamResult(outputStream);
        transformer.transform(source, outputTarget);
        return outputStream;
    }


    public boolean drop() {
        boolean dropped = false;
        try {
            httpSolrServer.deleteByQuery("*:*");
            dropped = true;
        } catch (Exception e) {
            LOGGER.error("drop failed", e);
        }
        return dropped;
    }

    @Override
    public void updateIndexes(LearningOpportunityData data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
