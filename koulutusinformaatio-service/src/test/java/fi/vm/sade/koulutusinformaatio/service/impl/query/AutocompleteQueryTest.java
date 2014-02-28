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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.solr.common.params.DisMaxParams;
import org.junit.Test;

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;

/**
 * @author Markus
 */
public class AutocompleteQueryTest {
    
    private static final String TERM = "term";
    private static final String STAR = "*";
    private static final String LANG_FI = "fi";
    private static final String LANG_SV = "sv";
    private static final String LANG_EN = "en";
    
    @Test
    public void testAutocompleteQuery() {
        AutocompleteQuery q = new AutocompleteQuery(TERM, LANG_FI);
        assertNotNull(q);        
        assertEquals(STAR, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(2, q.getFacetFields().length);
    }
    
    @Test
    public void testQueryFieldsFi() {
        AutocompleteQuery q = new AutocompleteQuery(TERM, LANG_FI);
        assertNotNull(q); 
        assertEquals(Joiner.on(" ").join(LearningOpportunityQuery.FIELDS_FI), q.getParams(DisMaxParams.QF)[0]);
        assertTrue(q.getFacetFields().length == 2);
        assertEquals(String.format("%s_%s", LearningOpportunity.NAME_AUTO, LANG_FI), q.getFacetFields()[0]);
    }
    
    @Test
    public void testQueryFieldsSv() {
        AutocompleteQuery q = new AutocompleteQuery(TERM, LANG_SV);
        assertNotNull(q); 
        assertEquals(Joiner.on(" ").join(LearningOpportunityQuery.FIELDS_SV), q.getParams(DisMaxParams.QF)[0]);
        assertTrue(q.getFacetFields().length == 2);
        assertEquals(String.format("%s_%s", LearningOpportunity.NAME_AUTO, LANG_SV), q.getFacetFields()[0]);
    }
    
    @Test
    public void testQueryFieldsEn() {
        AutocompleteQuery q = new AutocompleteQuery(TERM, LANG_EN);
        assertNotNull(q); 
        assertEquals(Joiner.on(" ").join(LearningOpportunityQuery.FIELDS_EN), q.getParams(DisMaxParams.QF)[0]);
        assertTrue(q.getFacetFields().length == 2);
        assertEquals(String.format("%s_%s", LearningOpportunity.NAME_AUTO, LANG_EN), q.getFacetFields()[0]);
    }
    
    
    
}
