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
    private static final String APPLICATION_SYSTEM_ID = "123";
    private static final int START = 0;
    private static final int ROWS = 10;

    @Test
    public void testQuery() {
        LearningOpportunityQuery q = new LearningOpportunityQuery(TERM, PREREQUISITE, CITIES, APPLICATION_SYSTEM_ID, START, ROWS);
        assertNotNull(q);
        assertEquals(3, q.getFilterQueries().length);
        String prerequisiteFQ = new StringBuilder("prerequisites:").append(PREREQUISITE).toString();
        assertEquals(prerequisiteFQ, q.getFilterQueries()[0]);
        String lopHomeplaceFQ = new StringBuilder("lopHomeplace:(")
                .append(Joiner.on(" OR ").join(CITIES)).append(")").toString();
        assertEquals(lopHomeplaceFQ, q.getFilterQueries()[1]);
        String applicationSystemFQ = new StringBuilder("applicationSystems:").append(APPLICATION_SYSTEM_ID).toString();
        assertEquals(applicationSystemFQ, q.getFilterQueries()[2]);
        assertEquals(TERM, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
    }
}
