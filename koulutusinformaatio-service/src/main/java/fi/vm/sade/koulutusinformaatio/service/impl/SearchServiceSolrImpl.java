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

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LearningOpportunityQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LocationQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderQuery;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SearchServiceSolrImpl implements SearchService {

    public static final Logger LOG = LoggerFactory.getLogger(SearchServiceSolrImpl.class);

    public static final String ID = "AOId";
    public static final String AS_START_DATE_PREFIX = "asStart_";
    public static final String AS_END_DATE_PREFIX = "asEnd_";

    private HttpSolrServer httpSolrServer;

    private final HttpSolrServer lopHttpSolrServer;
    private final HttpSolrServer loHttpSolrServer;
    private final HttpSolrServer locationHttpSolrServer;

    @Autowired
    public SearchServiceSolrImpl(@Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
                                 @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
                                 @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer) {
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.loHttpSolrServer = loAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;
    }

    @Override
    public List<Provider> searchLearningOpportunityProviders(
            String term, String asId, String baseEducation, boolean vocational) throws SearchException {
        Set<Provider> providers = new HashSet<Provider>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {

            SolrQuery query = new ProviderQuery(term + "*", asId, baseEducation);

            QueryResponse queryResponse = null;
            try {
                queryResponse = lopHttpSolrServer.query(query);
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument result : queryResponse.getResults()) {
                Provider provider = new Provider();
                provider.setId(result.get("id").toString());

                // TODO: i18n handling
                Map<String, String> texts = Maps.newHashMap();
                texts.put("fi", result.get("name").toString());

                provider.setName(new I18nText(texts));
                providers.add(provider);
            }

        }
        return new ArrayList<Provider>(providers);
    }

    @Override
    public LOSearchResultList searchLearningOpportunities(String term, String prerequisite,
                                                          List<String> cities, List<String> facetFilters, String lang, boolean ongoing, int start, int rows) throws SearchException {
        LOSearchResultList searchResultList = new LOSearchResultList();
        String trimmed = term.trim();
        if (!trimmed.isEmpty()) {
            SolrQuery query = new LearningOpportunityQuery(term, prerequisite, cities, facetFilters, lang, ongoing, start, rows);

            try {
                LOG.debug(
                        URLDecoder.decode(
                                new StringBuilder().append("Searching learning opportunities with query string: ").append(query.toString()).toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.debug("Could not log search query");
            }

            QueryResponse response = null;
            try {
                response = loHttpSolrServer.query(query);
                searchResultList.setTotalCount(response.getResults().getNumFound());
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument doc : response.getResults()) {
                String parentId = doc.get("parentId") != null ? doc.get("parentId").toString() : null;
                String losId = doc.get("losId") != null ? doc.get("losId").toString() : null;
                String id = doc.get("losId") != null ? doc.get("losId").toString() : doc.get("id").toString();
                String prerequisiteText = doc.get("prerequisite") != null ? doc.get("prerequisite").toString() : null;
                String prerequisiteCodeText = doc.get("prerequisiteCode") != null ? doc.get("prerequisiteCode").toString() : null;

                LOSearchResult lo = null;
                try {
                    lo = new LOSearchResult(
                            id, doc.get("name").toString(),
                            doc.get("lopId").toString(), doc.get("lopName").toString(), prerequisiteText,
                            prerequisiteCodeText, parentId, losId, doc.get("type").toString());

                    updateAsStatus(lo, doc);
                } catch (Exception e) {
                    continue;
                }
                searchResultList.getResults().add(lo);
            }
            
            addFacetsToResult(searchResultList, response, lang, facetFilters);
        }

        return searchResultList;
    }

    private void addFacetsToResult(LOSearchResultList searchResultList,
            QueryResponse response, String lang, List<String> facetFilters) {
        
        /*
         * Teaching language facet
         */
        FacetField teachingLangF = response.getFacetField(LearningOpportunityQuery.TEACHING_LANG);
        Facet teachingLangFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (teachingLangF != null) {
            for (Count curC : teachingLangF.getValues()) {
                if (curC.getCount() > 0) {
                    FacetValue newVal = new FacetValue(LearningOpportunityQuery.TEACHING_LANG,  
                                                        getLocalizedFacetName(curC.getName(), lang), 
                                                        curC.getCount(), 
                                                        curC.getName());
                    values.add(newVal);
                }
            }
        }
        teachingLangFacet.setFacetValues(values);
        searchResultList.setTeachingLangFacet(teachingLangFacet);
        
        /*
         * Facet composed of user's selections.
         * Used in search ui to display 0-selections.
         */
        Facet fFilFacet = new Facet();
        List<FacetValue> qVals = new ArrayList<FacetValue>();
        for (String curFacFilter: facetFilters) {
            int index = curFacFilter.indexOf(':');
            String facId = curFacFilter.substring(index + 1);
            String facField = curFacFilter.substring(0, index);
            SolrDocument facDoc = this.getFacetDoc(facId, lang);
            String facName = String.format("%s", facDoc.getFieldValue(String.format("%s_fname", lang)));
            FacetValue newVal = new FacetValue(facField, facName, 0, facId);
            qVals.add(newVal);
            
        }
        fFilFacet.setFacetValues(qVals);
        searchResultList.setFilterFacet(fFilFacet);
        
    }
    
    /*
     * Getting the update timestamp for the lo-collection.
     */
    private String getLocalizedFacetName(String id, String lang) {
        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("id:%s", id));
        query.setFields("id", String.format("%s_fname", lang));
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = loHttpSolrServer.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return String.format("%s", curDoc.getFieldValue(String.format("%s_fname", lang)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /*
     * Getting the update timestamp for the lo-collection.
     */
    private SolrDocument getFacetDoc(String id, String lang) {
        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("id:%s", id));
        query.setFields("id", String.format("%s_fname", lang));
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = loHttpSolrServer.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return curDoc;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void updateAsStatus(LOSearchResult lo, SolrDocument doc) {
        lo.setAsOngoing(false);
        Date now = new Date();
        Date nextStarts = null;

        for (String startKey : doc.keySet()) {
            if (startKey.startsWith(AS_START_DATE_PREFIX)) {
                String endKey = new StringBuilder().append(AS_END_DATE_PREFIX)
                        .append(startKey.split("_")[1]).toString();

                Date start = ((List<Date>) doc.get(startKey)).get(0);
                Date end = ((List<Date>) doc.get(endKey)).get(0);

                if (start.before(now) && now.before(end)) {
                    lo.setAsOngoing(true);
                    return;
                }

                if ((nextStarts == null && start.after(now)) || (start.after(now) && start.before(nextStarts))) {
                    nextStarts = start;
                }

            }
        }

        lo.setNextApplicationPeriodStarts(nextStarts);

    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }

    @Override
    public List<Location> searchLocations(String term, String lang) throws SearchException {
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            SolrQuery query = new LocationQuery(term + "*", lang);
            return executeSolrQuery(query);
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public List<Location> getLocations(List<String> codes, String lang) throws SearchException {
        if (codes != null && !codes.isEmpty()) {
            SolrQuery query = new LocationQuery(codes, lang);
            return executeSolrQuery(query);
        } else {
            return Lists.newArrayList();
        }
    }
    
    private List<Location> executeSolrQuery(final SolrQuery query) throws SearchException {
        List<Location> locations = Lists.newArrayList();
        QueryResponse queryResponse = null;
        try {
            queryResponse = locationHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException("Solr search error occured.");
        }

        for (SolrDocument result : queryResponse.getResults()) {
            Location location = new Location();
            location.setName(result.get("name").toString());
            location.setCode(result.get("code").toString());
            locations.add(location);
        }
        return locations;
    }
	

}
