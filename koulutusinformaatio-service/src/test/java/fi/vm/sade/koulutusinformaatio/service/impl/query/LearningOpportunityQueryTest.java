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

package fi.vm.sade.koulutusinformaatio.service.impl.query;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;

import org.apache.solr.common.params.DisMaxParams;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQueryTest {

    private static final String TERM = "term";
    private static final String PREREQUISITE = "PK";
    private static final List<String> CITIES = Lists.newArrayList("city1", "city2");
    private static final List<String> FACET_FILTERS = Lists.newArrayList("someOther:vaat", "someOther:vaat2");
    private static final List<String> FF_TEACH_LANG_FI = Lists.newArrayList( "teachingLangCode_ffm:FI", "someOther_ffm:whatever");
    private static final List<String> FF_TEACH_LANG_SV = Lists.newArrayList( "teachingLangCode_ffm:SV", "someOther_ffm:whatever");
    private static final List<String> FF_TEACH_LANG_OTHER = Lists.newArrayList( "teachingLangCode_ffm:OT", "someOther_ffm:whatever");
    private static final boolean ONGOING = false;
    private static final boolean UPCOMING = false;
    private static final boolean UPCOMING_LATER = false;
    private static final String UPCOMING_DATE = "2014-10-30T00:00:00Z";
    private static final String LANG_FI = "fi";
    private static final int START = 0;
    private static final int ROWS = 10;
    private static final String SORT = "0";
    private static final String ORDER = "asc";

    @Test
    public void testQueryTeachLangNone() {
        LearningOpportunityQuery q = new LearningOpportunityQuery(TERM, PREREQUISITE, CITIES, FACET_FILTERS, LANG_FI, ONGOING, UPCOMING, UPCOMING_LATER, START, ROWS, SORT, ORDER, null, null, null, UPCOMING_DATE, UPCOMING_DATE);
        assertNotNull(q);
        assertEquals(7, q.getFilterQueries().length);
        String prerequisiteFQ = new StringBuilder("prerequisites:").append(PREREQUISITE).toString();
        assertEquals(prerequisiteFQ, q.getFilterQueries()[0]);
        String lopHomeplaceFQ = new StringBuilder("lopHomeplace:(\"")
        .append(Joiner.on("\" OR \"").join(CITIES)).append("\")").toString();
        assertEquals(lopHomeplaceFQ, q.getFilterQueries()[1]);
        assertEquals(TERM, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(Joiner.on(" ").join(SolrUtil.FIELDS), q.getParams(DisMaxParams.QF)[0]);
    }
    
    @Test
    public void testQueryFieldsTeachLangFi() {
        
        LearningOpportunityQuery q = new LearningOpportunityQuery(TERM, PREREQUISITE, CITIES, FF_TEACH_LANG_FI, LANG_FI, ONGOING, UPCOMING,  UPCOMING_LATER, START, ROWS, SORT, ORDER, null, null, null, UPCOMING_DATE, UPCOMING_DATE);
        assertNotNull(q);
        assertEquals(7, q.getFilterQueries().length);
        String prerequisiteFQ = new StringBuilder("prerequisites:").append(PREREQUISITE).toString();
        assertEquals(prerequisiteFQ, q.getFilterQueries()[0]);
        String lopHomeplaceFQ = new StringBuilder("lopHomeplace:(\"")
                .append(Joiner.on("\" OR \"").join(CITIES)).append("\")").toString();
        assertEquals(lopHomeplaceFQ, q.getFilterQueries()[1]);
        assertEquals(TERM, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(Joiner.on(" ").join(SolrUtil.FIELDS_FI), q.getParams(DisMaxParams.QF)[0]);
        
    }
    
    @Test
    public void testQueryFieldsTeachLangSv() {
        
        LearningOpportunityQuery q = new LearningOpportunityQuery(TERM, PREREQUISITE, CITIES, FF_TEACH_LANG_SV, LANG_FI, ONGOING, UPCOMING, UPCOMING_LATER, START, ROWS, SORT, ORDER, null, null, null, UPCOMING_DATE, UPCOMING_DATE);
        assertNotNull(q);
        assertEquals(7, q.getFilterQueries().length);
        String prerequisiteFQ = new StringBuilder("prerequisites:").append(PREREQUISITE).toString();
        assertEquals(prerequisiteFQ, q.getFilterQueries()[0]);
        String lopHomeplaceFQ = new StringBuilder("lopHomeplace:(\"")
        .append(Joiner.on("\" OR \"").join(CITIES)).append("\")").toString();
        assertEquals(lopHomeplaceFQ, q.getFilterQueries()[1]);
        assertEquals(TERM, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(Joiner.on(" ").join(SolrUtil.FIELDS_SV), q.getParams(DisMaxParams.QF)[0]);
        
    }
    
    @Test
    public void testQueryFieldsTeachLangOther() {
        LearningOpportunityQuery q = new LearningOpportunityQuery(TERM, PREREQUISITE, CITIES, FF_TEACH_LANG_OTHER, LANG_FI, ONGOING, UPCOMING, UPCOMING_LATER, START, ROWS, SORT, ORDER, null, null, null, UPCOMING_DATE, UPCOMING_DATE);
        assertNotNull(q);
        assertEquals(7, q.getFilterQueries().length);
        String prerequisiteFQ = new StringBuilder("prerequisites:").append(PREREQUISITE).toString();
        assertEquals(prerequisiteFQ, q.getFilterQueries()[0]);
        String lopHomeplaceFQ = new StringBuilder("lopHomeplace:(\"")
        .append(Joiner.on("\" OR \"").join(CITIES)).append("\")").toString();
        assertEquals(lopHomeplaceFQ, q.getFilterQueries()[1]);
        assertEquals(TERM, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(Joiner.on(" ").join(SolrUtil.FIELDS_FI), q.getParams(DisMaxParams.QF)[0]);
        
    }
    

}
