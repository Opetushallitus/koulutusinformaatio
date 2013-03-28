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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.search.SearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.MapToSolrQueryTransformer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchServiceSolrImpl implements SearchService {

    public static final String ID = "AOId";

    private final HttpSolrServer httpSolrServer;
    private final MapToSolrQueryTransformer mapToSolrQueryTransformer = new MapToSolrQueryTransformer();

    @Autowired
    public SearchServiceSolrImpl(@Qualifier("HttpSolrServer") final HttpSolrServer httpSolrServer) {
        this.httpSolrServer = httpSolrServer;
    }

    @Override
    public SearchResult search(final Set<Map.Entry<String, List<String>>> parameters) throws SearchException {
        final SolrQuery solrQuery = mapToSolrQueryTransformer.transform(parameters);
        return query(solrQuery);
    }

    @Override
    public Map<String, Object> searchById(final String id) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ID + ":" + id);
        SearchResult searchResult = query(query);
        Map<String, Object> itemFromResult = getItemFromResult(searchResult);
        if (itemFromResult.isEmpty()) {
            throw new SearchException("Koulutuskuvausta " + id + " ei löytynyt: ");
        }
        return itemFromResult;
    }

    @Override
    public Collection<String> getUniqValuesByField(final String field) {
        SolrQuery query = new SolrQuery();
        query.setFacet(true);
        query.addFacetField(field);
        Set<String> uniqNames = new HashSet<String>();
        try {
            QueryResponse rsp = httpSolrServer.query(query);
            List<FacetField> facetFields = rsp.getFacetFields();
            for (FacetField facetField : facetFields) {
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count value : values) {
                    uniqNames.add(value.getName());
                }
            }
        } catch (SolrServerException e) {
            throw new SearchException("Error running query", e);
        }
        return uniqNames;

    }


    private SearchResult query(final SolrQuery query) {
        List<Map<String, Collection<Object>>> results = new ArrayList<Map<String, Collection<Object>>>();
        try {
            QueryResponse rsp = httpSolrServer.query(query);
            for (SolrDocument doc : rsp.getResults()) {
                final Map<String, Collection<Object>> fieldValuesMap = doc.getFieldValuesMap();
                results.add(fieldValuesMap);
            }
        } catch (SolrServerException e) {
            throw new SearchException("Error running query", e);
        }
        return new SearchResult(results);
    }

    private Map<String, Object> getItemFromResult(final SearchResult searchResult) {
        List<Map<String, Object>> items = searchResult.getItems();
        if (items.size() == 0) {
            return Collections.<String, Object>emptyMap();
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            throw new SearchException("Multiple hits");
        }
    }
}
