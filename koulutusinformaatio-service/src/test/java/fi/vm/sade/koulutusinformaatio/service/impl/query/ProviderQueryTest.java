package fi.vm.sade.koulutusinformaatio.service.impl.query;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderQueryTest {

    private final String TERM = "term";
    private final String AS_ID = "asId";
    private final String BASE_EDUCATION = "base_education";
    private final String TYPE = "provider_type";
    private static final String SORT = "0";
    private static final String ORDER = "asc";

    @Test
    public void testProviderQuery() {
        ProviderQuery pq = new ProviderQuery(TERM, AS_ID, Arrays.asList(BASE_EDUCATION), 0, 100, true, true, "fi", false, TYPE);
        assertEquals("name_fi:" + TERM + "*", pq.getQuery());
        assertEquals(4, pq.getFilterQueries().length);
        assertEquals("type:ORGANISAATIO", pq.getFilterQueries()[0]);
        assertEquals("asIds:asId", pq.getFilterQueries()[1]);
        assertEquals(String.format("%s:%s", SolrUtil.ProviderFields.TYPE_VALUE, TYPE), pq.getFilterQueries()[3]);
    }
    
    /**
     * Tests the second constructor of ProviderQuery. This constructor is used in KI's Organization search.
     */
    @Test
    public void testProviderQuery2() {
        ProviderQuery pq = new ProviderQuery(TERM, "fi", Arrays.asList("olType_ffm:oppilatiostyyppi_21"), Arrays.asList("Helsinki"), 0, 100, SORT, ORDER);
        assertEquals(3, pq.getFilterQueries().length);
        assertEquals("type:ORGANISAATIO", pq.getFilterQueries()[0]);
        assertEquals(String.format("%s:%s", "lopHomeplace", "(\"Helsinki\")"), pq.getFilterQueries()[2]);
    }
}
