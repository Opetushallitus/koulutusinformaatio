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

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LocationFields;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
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
    private HttpSolrServer locationHttpSolrServer;


    @Before
    public void setUp() throws SolrServerException {

        SolrDocumentList lopDocs = new SolrDocumentList();
        SolrDocument lop1 = new SolrDocument();
        lop1.put("id", "1.2.3.4.5");
        lop1.put("name_fi", "LOP NAME");
        lop1.put("name_sv", "LOP NAME");
        lop1.put("athleteEducation", false);
        lopDocs.add(lop1);
        QueryResponse lopQueryResponse = mock(QueryResponse.class);
        when(lopQueryResponse.getResults()).thenReturn(lopDocs);
        lopHttpSolrServer = mock(HttpSolrServer.class);
        when(lopHttpSolrServer.query((SolrParams)any())).thenReturn(lopQueryResponse);

        SolrDocumentList loDocs = new SolrDocumentList();
        SolrDocument lo1 = new SolrDocument();
        lo1.put("id", "1.2.3.4.5");
        lo1.put("name_fi", "test learning opportunity");
        lo1.put("name", "test learning opportunity");
        lo1.put("lopId", "6.7.8.9");
        lo1.put("lopName_fi", "LOP NAME");
        lo1.put("lopName", "LOP NAME");
        lo1.put("type", "TYPE");
        loDocs.add(lo1);
        QueryResponse loQueryResponse = mock(QueryResponse.class);
        when(loQueryResponse.getResults()).thenReturn(loDocs);
        
        FacetField nameF = mock(FacetField.class);
        Count count = mock(Count.class);
        count.setCount(1);
        count.setName("term1");
        List<Count> counts = Arrays.asList(count);
        
        when(nameF.getValues()).thenReturn(counts);
        when(loQueryResponse.getFacetField(LearningOpportunity.NAME_AUTO + "_fi")).thenReturn(nameF);
        
        SolrDocumentList locDocs = new SolrDocumentList();
        SolrDocument loc1 = new SolrDocument();
        loc1.addField(LocationFields.CODE, "code_location");
        loc1.addField(LocationFields.ID, "id_location");
        loc1.addField(LocationFields.LANG, "fi");
        loc1.addField(LocationFields.NAME, "Location");
        loc1.addField(LocationFields.TYPE, "LocationType");
        locDocs.add(loc1);
        
        QueryResponse locQueryResponse = mock(QueryResponse.class);
        when(locQueryResponse.getResults()).thenReturn(locDocs);
        
        
        
        loHttpSolrServer = mock(HttpSolrServer.class);
        when(loHttpSolrServer.query((SolrParams)any())).thenReturn(loQueryResponse);
        locationHttpSolrServer = mock(HttpSolrServer.class);
        when(locationHttpSolrServer.query((SolrParams)any())).thenReturn(locQueryResponse);

        service = new SearchServiceSolrImpl(lopHttpSolrServer, loHttpSolrServer, locationHttpSolrServer);
    }

    @Test
    public void testSearchProviders() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "query", "1.2.3.4", "PK", true, true, 0, 100, "fi", false);
        assertEquals(1, lops.size());
    }

    @Test
    public void testSearchProvidersEmptyTerm() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "", "1.2.3.4", "PK", true, true, 0, 100, "fi", false);
        assertEquals(1, lops.size());
    }

    @Test
    public void testSearchLearningOpportunities() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("query", "PK", Lists.newArrayList("HELSINKI"), Lists.newArrayList("teachingLang:suomi"), "fi", false, false, 0, 100, "0", "asc");
        assertEquals(1, results.getResults().size());
    }

    @Test
    public void testSearchLearningOpportunitiesEmptyTerm() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("", "PK", Lists.newArrayList("HELSINKI"), Lists.newArrayList("teachingLang:suomi"), "fi", false, false, 0, 100, "0", "asc");
        assertEquals(0, results.getResults().size());
    }
    
    @Test
    public void testAutucompleteSearch() throws SearchException {
        SuggestedTermsResult res = service.searchSuggestedTerms("te", "fi");
        assertEquals(1, res.getLoNames().size());
    }
    
    @Test
    public void testGetDistricts() throws SearchException {
        List<Location> locs = service.getDistricts("fi");
        assertEquals(1, locs.size());
    }
    
    @Test
    public void testChildLocations() throws SearchException {
        List<Location> locs = service.getChildLocations(Arrays.asList("uusimaa"), "fi");
        assertEquals(1, locs.size());
    }
    
    

}
