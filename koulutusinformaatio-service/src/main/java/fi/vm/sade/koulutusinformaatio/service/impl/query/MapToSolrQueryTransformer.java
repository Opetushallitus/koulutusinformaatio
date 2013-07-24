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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class MapToSolrQueryTransformer {

    private static final Map<String, SolrQueryAppender> CREATOR_MAP = new HashMap<String, SolrQueryAppender>();

    private static final SolrQueryAppender DEFAULT_SOLR_QUERY_APPENDER = new QueryParameterAppender();

    static {
        CREATOR_MAP.put("sort", new SortParameterAppender());
        CREATOR_MAP.put("start", new StartParameterAppender());
        CREATOR_MAP.put("rows", new RowsParameterAppender());
        CREATOR_MAP.put("fl", new FieldsParameterAppender());
        CREATOR_MAP.put("fq", new FilterQueryAppender());
    }

    public SolrQuery transform(final Set<Map.Entry<String, List<String>>> parameters) {
        SolrQuery solrQuery = new SolrQuery();
        for (Map.Entry<String, List<String>> parameter : parameters) {
            String key = parameter.getKey();
            if (CREATOR_MAP.containsKey(key)) {
                SolrQueryAppender solrQueryAppender = CREATOR_MAP.get(key);
                solrQueryAppender.append(solrQuery, parameter);
            } else {
                DEFAULT_SOLR_QUERY_APPENDER.append(solrQuery, parameter);
            }
        }
        return solrQuery;
    }
}
