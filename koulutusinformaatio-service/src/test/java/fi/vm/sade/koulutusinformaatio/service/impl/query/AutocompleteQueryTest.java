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

import org.junit.Test;

/**
 * @author Markus
 */
public class AutocompleteQueryTest {
    
    private static final String TERM = "term";
    private static final String STAR = "*";
    private static final String LANG = "fi";
    
    @Test
    public void testAutocompleteQuery() {
        AutocompleteQuery q = new AutocompleteQuery(TERM, LANG);
        assertNotNull(q);        
        assertEquals(STAR, q.getQuery().toString());
        assertEquals("edismax", q.getParams("defType")[0]);
        assertEquals(2, q.getFacetFields().length);
    }
    
}
