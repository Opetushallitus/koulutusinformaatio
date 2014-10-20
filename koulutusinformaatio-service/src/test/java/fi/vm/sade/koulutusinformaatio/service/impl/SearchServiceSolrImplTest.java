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

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ApplicationSystemQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderNameFirstCharactersQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderQuery;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
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
    private EducationDataQueryService queryService;


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
        when(lopHttpSolrServer.query(argThat(isProviderQuery()))).thenReturn(lopQueryResponse);

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
        when(loHttpSolrServer.query(argThat(isNotApplicationSystemQuery()))).thenReturn(loQueryResponse);
        locationHttpSolrServer = mock(HttpSolrServer.class);
        when(locationHttpSolrServer.query((SolrParams)any())).thenReturn(locQueryResponse);

        Group a = mock(Group.class);
        when(a.getGroupValue()).thenReturn("A");
        Group b = mock(Group.class);
        when(b.getGroupValue()).thenReturn("B");
        Group c = mock(Group.class);
        when(c.getGroupValue()).thenReturn("C");
        GroupCommand gc = mock(GroupCommand.class);
        when(gc.getName()).thenReturn("startsWith_fi");
        when(gc.getValues()).thenReturn(Lists.newArrayList(a, b, c));
        GroupResponse groupResponse = mock(GroupResponse.class);
        when(groupResponse.getValues()).thenReturn(Lists.newArrayList(gc));
        QueryResponse firstCharResponse = mock(QueryResponse.class);
        when(firstCharResponse.getGroupResponse()).thenReturn(groupResponse);
        when(lopHttpSolrServer.query(argThat(isProviderNameFirstCharactersQuery()))).thenReturn(firstCharResponse);
        
        queryService = mock(EducationDataQueryService.class);

        service = new SearchServiceSolrImpl(lopHttpSolrServer, loHttpSolrServer, locationHttpSolrServer, queryService);
        
        mockCalendarApplicationSystemSearch();
    }
    
    private void mockCalendarApplicationSystemSearch() throws SolrServerException {
        
        SolrDocumentList calDocs = new SolrDocumentList();
        SolrDocument calDoc = new SolrDocument();
        calDoc.addField(LocationFields.ID, "id_calendar1");
        calDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_FI , "Kalenterihaku fi");
        calDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_SV , "Kalenterihaku sv");
        calDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_EN , "Kalenterihaku en");
        calDoc.addField(LocationFields.TYPE, "HAKU");
        calDoc.addField("asStart_0", Arrays.asList(new Date()));
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);
        calDoc.addField("asEnd_0", Arrays.asList(end.getTime()));
        calDoc.addField("asPeriodName_0_ss", "Hakuajalle annettu nimi");
        calDocs.add(calDoc);
        
        QueryResponse calQueryResponse = mock(QueryResponse.class);
        when(calQueryResponse.getResults()).thenReturn(calDocs);
        when(this.loHttpSolrServer.query(argThat(isApplicationSystemQuery()))).thenReturn(calQueryResponse);
        
        
    }
    

    @Test
    public void testFindCalendarApplicationSystems() throws SearchException {
        List<CalendarApplicationSystem> results = service.findApplicationSystemsForCalendar();
        CalendarApplicationSystem cal = results.get(0);
        assertEquals("id_calendar1", cal.getId());
    }

    @Test
    public void testSearchProviders() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "query", "1.2.3.4", Arrays.asList("PK"), true, true, 0, 100, "fi", false, "type");
        assertEquals(1, lops.size());
    }

    @Test
    public void testSearchProvidersEmptyTerm() throws SearchException {
        List<Provider> lops = service.searchLearningOpportunityProviders(
                "", "1.2.3.4", Arrays.asList("PK"), true, true, 0, 100, "fi", false, "type");
        assertEquals(1, lops.size());
    }

    @Test
    public void testSearchLearningOpportunities() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("query", "PK", Lists.newArrayList("HELSINKI"), Lists.newArrayList("teachingLang:suomi"), Lists.newArrayList("contentType:muu"), Lists.newArrayList("contentType:muu"), "fi", false, false, false, 0, 100, "0", "asc", null, null, null, SearchType.LO);
        assertEquals(1, results.getResults().size());
    }

    @Test
    public void testSearchLearningOpportunitiesEmptyTerm() throws SearchException {
        LOSearchResultList results = service.searchLearningOpportunities("", "PK", Lists.newArrayList("HELSINKI"), Lists.newArrayList("teachingLang:suomi"), Lists.newArrayList("contentType:muu"), Lists.newArrayList("olType:muu"), "fi", false, false, false, 0, 100, "0", "asc", null, null, null, SearchType.LO);
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

    @Test
    public void testGetProviderFirstCharacterList() throws SearchException {
        List<String> characters = service.getProviderFirstCharacterList("fi");
        assertNotNull(characters);
        assertEquals(3, characters.size());
        assertTrue(characters.contains("A"));
        assertTrue(characters.contains("B"));
        assertTrue(characters.contains("C"));
        assertFalse(characters.contains("D"));
    }

    private static ArgumentMatcher<SolrParams> isProviderQuery() {
        return new ArgumentMatcher<SolrParams>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ProviderQuery;
            }
        };
    }

    private static ArgumentMatcher<SolrParams> isProviderNameFirstCharactersQuery() {
        return new ArgumentMatcher<SolrParams>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ProviderNameFirstCharactersQuery;
            }
        };
    }
    
    private ArgumentMatcher<SolrParams> isApplicationSystemQuery() {
        return new ArgumentMatcher<SolrParams>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ApplicationSystemQuery;
            }
        };
    }
    
    private ArgumentMatcher<SolrParams> isNotApplicationSystemQuery() {
        return new ArgumentMatcher<SolrParams>() {
            @Override
            public boolean matches(Object o) {
                return !(o instanceof ApplicationSystemQuery);
            }
        };
    }

}
