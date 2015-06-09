package fi.vm.sade.koulutusinformaatio.service.impl.query;


import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderQueryTest {

    private final String SINGLE_CHARACTER_TERM = "H";
    private final String EMPTY_TERM = "";
    private final String ASTERISK = "*";
    private final String SIMPLE_TERM = "term";
    private final String AS_ID = "asId";
    private final String BASE_EDUCATION = "base_education";
    private final String TYPE = "provider_type";
    private static final String SORT = "0";
    private static final String ORDER = "asc";

    @Test
    public void testProviderQuerySimpleTerm() {
        ProviderQuery pq = new ProviderQuery(SIMPLE_TERM, AS_ID, Arrays.asList(BASE_EDUCATION), 0, 100, true, true, "fi", false, TYPE);
        assertEquals("name_fi:" + SIMPLE_TERM + "*", pq.getQuery());
        assertEquals(4, pq.getFilterQueries().length);
        assertEquals("type:ORGANISAATIO", pq.getFilterQueries()[0]);
        assertEquals("asIds:asId", pq.getFilterQueries()[1]);
        assertEquals(String.format("%s:%s", SolrUtil.ProviderFields.TYPE_VALUE, TYPE), pq.getFilterQueries()[3]);
    }

    @Test
    public void testProviderQuerySingleCharacterTerm() {
        ProviderQuery pq = new ProviderQuery(SINGLE_CHARACTER_TERM, AS_ID, Arrays.asList(BASE_EDUCATION), 0, 100, true, true, "fi", false, TYPE);
        assertEquals("name_fi:" + SINGLE_CHARACTER_TERM + "*", pq.getQuery());
        assertEquals(4, pq.getFilterQueries().length);
        assertEquals("type:ORGANISAATIO", pq.getFilterQueries()[0]);
        assertEquals("asIds:asId", pq.getFilterQueries()[1]);
        assertEquals(String.format("%s:%s", SolrUtil.ProviderFields.TYPE_VALUE, TYPE), pq.getFilterQueries()[3]);
    }

    @Test
    public void testProviderQueryEmptyTerm() {
        ProviderQuery pq = new ProviderQuery(EMPTY_TERM, AS_ID, Arrays.asList(BASE_EDUCATION), 0, 100, true, true, "fi", false, TYPE);
        assertEquals("name_fi:" + ASTERISK, pq.getQuery());
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
        ProviderQuery pq = new ProviderQuery(SIMPLE_TERM, "fi", Arrays.asList("olType_ffm:oppilatiostyyppi_21"), Arrays.asList("Helsinki"), 0, 100, SORT, ORDER);
        assertEquals(3, pq.getFilterQueries().length);
        assertEquals("type:ORGANISAATIO", pq.getFilterQueries()[0]);
        assertEquals(String.format("%s:%s", "lopHomeplace", "(\"Helsinki\")"), pq.getFilterQueries()[2]);
    }
}
