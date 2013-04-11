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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityProvider;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunitySearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.MapToSolrQueryTransformer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SearchServiceSolrImpl implements SearchService {

    public static final String ID = "AOId";

    private HttpSolrServer httpSolrServer;

    private final HttpSolrServer lopHttpSolrServer;
    private final HttpSolrServer loHttpSolrServer;
    private final MapToSolrQueryTransformer mapToSolrQueryTransformer = new MapToSolrQueryTransformer();

    @Autowired
    public SearchServiceSolrImpl(@Qualifier("lopHttpSolrServer") final HttpSolrServer lopHttpSolrServer,
                                 @Qualifier("loHttpSolrServer") final HttpSolrServer loHttpSolrServer) {
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.loHttpSolrServer = loHttpSolrServer;
    }

    @Override
    public List<LearningOpportunityProvider> searchLearningOpportunityProviders(
            String term, String asId, String prerequisite, boolean vocational) throws SearchException {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(3);
        Set<LearningOpportunityProvider> providers = new HashSet<LearningOpportunityProvider>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            parameters.put("name", createParameter(term + "*"));
            parameters.put("asId", createParameter(asId));
            //parameters = addPrerequisite(parameters, prerequisite, vocational);
            SolrQuery query = mapToSolrQueryTransformer.transform(parameters.entrySet());

            QueryResponse queryResponse = null;
            try {
                queryResponse = lopHttpSolrServer.query(query);
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument result : queryResponse.getResults()) {
                LearningOpportunityProvider provider = new LearningOpportunityProvider();
                provider.setId(result.get("id").toString());
                provider.setName(result.get("name").toString());
                providers.add(provider);
            }

        }
        return new ArrayList<LearningOpportunityProvider>(providers);
    }

    @Override
    public List<LearningOpportunitySearchResult> searchLearningOpportunities(String term) throws SearchException {
        List<LearningOpportunitySearchResult> learningOpportunities = new ArrayList<LearningOpportunitySearchResult>();
        String trimmed = term.trim();
        if (!trimmed.isEmpty()) {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(1);
            parameters.put("text", Lists.newArrayList(term));
            SolrQuery query = mapToSolrQueryTransformer.transform(parameters.entrySet());

            QueryResponse response = null;
            try {
                response = loHttpSolrServer.query(query);
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument doc : response.getResults()) {
                LearningOpportunitySearchResult lo = new LearningOpportunitySearchResult(
                        doc.get("id").toString(), doc.get("name").toString(),
                        doc.get("lopId").toString(), doc.get("lopName").toString());
                learningOpportunities.add(lo);
            }
        }

        return learningOpportunities;
    }

    private MultiValueMap<String, String> addPrerequisite(MultiValueMap<String, String> parameters, String prerequisite, boolean vocational) {
        String realPrerequisite = prerequisite;
        if (realPrerequisite.equals("KESKEYTYNYT") || realPrerequisite.equals("ULKOMAINEN_TUTKINTO")) {
            return parameters; // Ei suodatusta
        }
        if (realPrerequisite.equals("YLIOPPILAS")) {
            parameters.put("LOIPrerequisite", createParameter("(5 OR 9)"));
        } else if (realPrerequisite.equals("PERUSKOULU")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5)"));
        } else if (realPrerequisite.equals("OSITTAIN_YKSILOLLISTETTY")
                || realPrerequisite.equals("ERITYISOPETUKSEN_YKSILOLLISTETTY")
                || realPrerequisite.equals("YKSILOLLISTETTY")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5 OR 6)"));
        }
        if (vocational) {
            parameters.put("AOEducationDegree", createParameter("(NOT 32)"));
        }
        return parameters;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }


//
//    public SearchResult search(final Set<Map.Entry<String, List<String>>> parameters) throws SearchException {
//        final SolrQuery solrQuery = mapToSolrQueryTransformer.transform(parameters);
//        return query(solrQuery);
//    }
//
//
//    public SearchResult query(final SolrQuery query) {
//        List<Map<String, Collection<Object>>> results = new ArrayList<Map<String, Collection<Object>>>();
//        try {
//            QueryResponse rsp = httpSolrServer.query(query);
//            for (SolrDocument doc : rsp.getResults()) {
//                final Map<String, Collection<Object>> fieldValuesMap = doc.getFieldValuesMap();
//                results.add(fieldValuesMap);
//            }
//        } catch (SolrServerException e) {
//            throw new SearchException("Error running query", e);
//        }
//        return new SearchResult(results);
//    }

//
//    public Map<String, Object> searchById(final String id) {
//        SolrQuery query = new SolrQuery();
//        query.setQuery(ID + ":" + id);
//        SearchResult searchResult = query(query);
//        Map<String, Object> itemFromResult = getItemFromResult(searchResult);
//        if (itemFromResult.isEmpty()) {
//            throw new SearchException("Koulutuskuvausta " + id + " ei löytynyt: ");
//        }
//        return itemFromResult;
//    }
//
//    private Collection<String> getUniqValuesByField(final String field) {
//        SolrQuery query = new SolrQuery();
//        query.setFacet(true);
//        query.addFacetField(field);
//        Set<String> uniqNames = new HashSet<String>();
//        try {
//            QueryResponse rsp = httpSolrServer.query(query);
//            List<FacetField> facetFields = rsp.getFacetFields();
//            for (FacetField facetField : facetFields) {
//                List<FacetField.Count> values = facetField.getValues();
//                for (FacetField.Count value : values) {
//                    uniqNames.add(value.getName());
//                }
//            }
//        } catch (SolrServerException e) {
//            throw new SearchException("Error running query", e);
//        }
//        return uniqNames;
//
//    }
//

//
//    private Map<String, Object> getItemFromResult(final SearchResult searchResult) {
//        List<Map<String, Object>> items = searchResult.getItems();
//        if (items.size() == 0) {
//            return Collections.<String, Object>emptyMap();
//        } else if (items.size() == 1) {
//            return items.get(0);
//        } else {
//            throw new SearchException("Multiple hits");
//        }
//    }

}
