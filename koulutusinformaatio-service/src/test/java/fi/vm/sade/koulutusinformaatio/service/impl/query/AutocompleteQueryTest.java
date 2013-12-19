package fi.vm.sade.koulutusinformaatio.service.impl.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AutocompleteQueryTest {
    
    private static final String STAR = "*";
    private static final String TERM = "term";
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
