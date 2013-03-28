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
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RowsParameterAppenderTest {
    private SolrQuery solrQuery = new SolrQuery();

    @Test
    public void testAppend() throws Exception {
        Integer expected = 1;
        RowsParameterAppender RowsParameterAppender = new RowsParameterAppender();
        Map.Entry<String, List<String>> entryWithValue = MapEntryUtil.createEntryWithValue(expected.toString());
        RowsParameterAppender.append(solrQuery, entryWithValue);
        assertEquals(expected, solrQuery.getRows());
    }
}
