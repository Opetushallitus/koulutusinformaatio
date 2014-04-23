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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderNameFirstCharactersQueryTest {

    @Test
    public void testQuery() {
        SolrQuery query = new ProviderNameFirstCharactersQuery("fi");
        assertNotNull(query);
        assertEquals("startsWith_fi:*", query.get("q"));
        assertEquals("true", query.get(GroupParams.GROUP));
        assertEquals("startsWith_fi", query.get(GroupParams.GROUP_FIELD));
        assertEquals("0", query.get(GroupParams.GROUP_LIMIT));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), query.getRows());
    }

    @Test
    public void testQuerySv() {
        SolrQuery query = new ProviderNameFirstCharactersQuery("sv");
        assertNotNull(query);
        assertEquals("startsWith_sv:*", query.get("q"));
        assertEquals("startsWith_sv", query.get(GroupParams.GROUP_FIELD));
    }
}
