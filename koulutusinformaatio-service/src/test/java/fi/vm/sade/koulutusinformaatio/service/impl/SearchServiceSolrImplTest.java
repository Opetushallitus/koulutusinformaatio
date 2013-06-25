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

import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class SearchServiceSolrImplTest {

    private SearchServiceSolrImpl service;
    private HttpSolrServer loHttpSolrServer;
    private HttpSolrServer lopHttpSolrServer;



    @Before
    public void setUp() throws SolrServerException {

        SolrDocumentList lopDocs = new SolrDocumentList();
        SolrDocument lop1 = new SolrDocument();
        lop1.put("id", "1.2.3.4.5");
        lop1.put("name", "LOP NAME");
        lop1.put("athleteEducation", false);
        lopDocs.add(lop1);
        QueryResponse lopQueryResponse = mock(QueryResponse.class);
        when(lopQueryResponse.getResults()).thenReturn(lopDocs);
        lopHttpSolrServer = mock(HttpSolrServer.class);
        when(lopHttpSolrServer.query((SolrParams)any())).thenReturn(lopQueryResponse);

        SolrDocumentList loDocs = new SolrDocumentList();
        SolrDocument lo1 = new SolrDocument();
        lo1.put("id", "1.2.3.4.5");
        lo1.put("name", "test learning opportunity");
        lo1.put("lopId", "6.7.8.9");
        lo1.put("lopName", "LOP NAME");
        loDocs.add(lo1);
        QueryResponse loQueryResponse = mock(QueryResponse.class);
        when(loQueryResponse.getResults()).thenReturn(loDocs);
        loHttpSolrServer = mock(HttpSolrServer.class);
        when(loHttpSolrServer.query((SolrParams)any())).thenReturn(loQueryResponse);

        service = new SearchServiceSolrImpl(lopHttpSolrServer, loHttpSolrServer);
    }

    @Test
    public void testSearchProviders() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "query", "1.2.3.4", null, Boolean.parseBoolean(null));
        assertEquals(1, lops.size());
    }

    @Test
    public void testSearchProvidersEmptyTerm() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "", "1.2.3.4", null, Boolean.parseBoolean(null));
        assertEquals(0, lops.size());
    }

    @Test
    public void testSearchLearningOpportunities() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("query", "PK", 0, 100);
        assertEquals(1, results.getResults().size());
    }

    @Test
    public void testSearchLearningOpportunitiesEmptyTerm() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("", "PK", 0, 100);
        assertEquals(0, results.getResults().size());
    }

}
