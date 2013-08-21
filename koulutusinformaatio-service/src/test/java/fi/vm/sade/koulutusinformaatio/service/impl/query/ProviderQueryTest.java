package fi.vm.sade.koulutusinformaatio.service.impl.query;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderQueryTest {

    private final String TERM = "term";
    private final String AS_ID = "asId";
    private final String PREREQUISITE = "prerequisite";

    @Test
    public void testProviderQuery() {
        ProviderQuery pq = new ProviderQuery(TERM, AS_ID, PREREQUISITE);
        assertEquals("name:" + TERM, pq.getQuery());
        assertEquals(2, pq.getFilterQueries().length);
        assertEquals("asId:" + AS_ID, pq.getFilterQueries()[0]);
        assertEquals("prerequisites:" + PREREQUISITE, pq.getFilterQueries()[1]);
    }
}
