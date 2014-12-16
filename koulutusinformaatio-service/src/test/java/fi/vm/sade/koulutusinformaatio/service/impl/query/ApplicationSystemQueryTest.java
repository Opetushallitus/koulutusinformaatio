package fi.vm.sade.koulutusinformaatio.service.impl.query;

import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import static org.junit.Assert.*;

public class ApplicationSystemQueryTest {
    
    @Test
    public void testApplicationSystemQuery() {
        ApplicationSystemQuery q = new ApplicationSystemQuery();
        assertNotNull(q);
        assertEquals("*", q.getQuery());
        assertEquals("type:HAKU", q.getFilterQueries()[0]);
    }
    
    @Test
    public void testApplicationSystemQueryByTargetGroup() {
        ApplicationSystemQuery q = new ApplicationSystemQuery(SolrUtil.SolrConstants.AS_TARGET_GROUP_CODE_HIGHERED);
        assertNotNull(q);
        assertEquals("*", q.getQuery());
        assertEquals("type:HAKU", q.getFilterQueries()[0]);
        assertEquals(new StringBuilder(SolrUtil.LearningOpportunity.AS_TARGET_GROUP_CODE).append(":").append(SolrUtil.SolrConstants.AS_TARGET_GROUP_CODE_HIGHERED).toString(), q.getFilterQueries()[2]);
    }

}
