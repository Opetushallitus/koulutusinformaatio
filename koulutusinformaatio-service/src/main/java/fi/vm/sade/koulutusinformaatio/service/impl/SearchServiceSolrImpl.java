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
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.query.*;

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
import java.util.*;

@Component
public class SearchServiceSolrImpl implements SearchService {

    public static final Logger LOG = LoggerFactory.getLogger(SearchServiceSolrImpl.class);

    public static final String ID = "AOId";
    public static final String AS_START_DATE_PREFIX = "asStart_";
    public static final String AS_END_DATE_PREFIX = "asEnd_";
    private final static String DISTRICT = "maakunta";

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
            String term, String asId, String baseEducation, boolean vocational, boolean nonVocational, int start, int rows, String lang, boolean prefix) throws SearchException {
        List<Provider> providers = new ArrayList<Provider>();
        SolrQuery query = new ProviderQuery(term, asId, baseEducation, start, rows, vocational, nonVocational, lang, prefix);

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
            texts.put("fi", result.get("name_fi").toString());
            texts.put("sv", result.get("name_sv").toString());

            provider.setName(new I18nText(texts));
            providers.add(provider);
        }
        return providers;
    }

    @Override
    public List<Provider> searchLearningOpportunityProviders(String term, String lang, boolean prefix) throws SearchException {
        return searchLearningOpportunityProviders(term, null, null, false, false, 0, Integer.MAX_VALUE, lang, prefix);
    }
    

    private String fixString(String term) {
        String[] splits = term.split(" ");
        String fixed = "";
        for (String curSplit : splits) {
            if (curSplit.length() > 1 || curSplit.equals("*")) {
                fixed += curSplit + " ";
            }
        }
        return fixed.trim();
    }

    @Override
    public LOSearchResultList searchLearningOpportunities(String term, String prerequisite,
            List<String> cities, List<String> facetFilters,  
            String lang, boolean ongoing, boolean upcoming, 
            int start, int rows, String sort, String order, 
            String lopFilter, String educationCodeFilter,
            List<String> excludes, SearchType searchType) throws SearchException {
        LOSearchResultList searchResultList = new LOSearchResultList();
        String trimmed = term.trim();
        String fixed = fixString(trimmed);
        if (!trimmed.isEmpty()) {
            SolrQuery query = new LearningOpportunityQuery(fixed, prerequisite, 
                    cities, facetFilters, 
                    lang, ongoing, upcoming, 
                    start, rows, sort, order,
                    lopFilter, educationCodeFilter, excludes, searchType);

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
                setResultCount(searchResultList, response, searchType);
            } catch (SolrServerException e) {
                throw new SearchException("Solr search error occured.");
            }

            for (SolrDocument doc : response.getResults()) {
                try {
                    if (SearchType.LO.equals(searchType)) {
                        searchResultList.getResults().add(createLOSearchResult(doc, lang));
                    } else if (SearchType.ARTICLE.equals(searchType)) {
                        searchResultList.getArticleresults().add(createArticleSearchResult(doc));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
            
            if (SearchType.LO.equals(searchType)) {
                addFacetsToResult(searchResultList, response, lang, facetFilters);
            }
            
            if (lopFilter != null) {
                searchResultList.setLopRecommendationFilter(getRecommendationFilter(lopFilter, "lopFilter"));
            }
            if (educationCodeFilter != null) {
                searchResultList.setEducationCodeRecommendationFilter(getRecommendationFilter(educationCodeFilter, "educationCodeFilter"));
            }
            
            //Setting result counts of other searches (one of article, provider or lo)
            if (searchType.LO.equals(searchType)) {
                setOtherResultCounts(fixed, lang, start, sort, order, cities, facetFilters, ongoing, upcoming, lopFilter, educationCodeFilter, excludes, SearchType.ARTICLE, searchResultList);
            } else if (SearchType.ARTICLE.equals(searchType)) {
                setOtherResultCounts(fixed, lang, start, sort, order, cities, facetFilters, ongoing, upcoming, lopFilter, educationCodeFilter, excludes, SearchType.LO, searchResultList);
            }
            
            searchResultList.setTotalCount(searchResultList.getArticleCount() + searchResultList.getLoCount());
            

        }

        return searchResultList;
    }


    private void setOtherResultCounts(String term, String lang, int start,
            String sort, String order, List<String> cities, 
            List<String> facetFilters, 
            boolean ongoing, boolean upcoming,
            String lopFilter, String educationCodeFilter, List<String> excludes,
            SearchType searchType, LOSearchResultList searchResultList) throws SearchException {
        
        /*if (SearchType.LO.equals(searchType) && (facetFilters == null || facetFilters.isEmpty())) {
            facetFilters = Arrays.asList(new String[]{String.format("%s:%s", LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase())});
        }*/
        
        SolrQuery query = new LearningOpportunityQuery(term, null, 
                cities, facetFilters, 
                lang, ongoing, upcoming, 
                start, 0, sort, order,
                lopFilter, educationCodeFilter, excludes, searchType);
        
        try {
            QueryResponse response = loHttpSolrServer.query(query);
            setResultCount(searchResultList, response, searchType);
        } catch (SolrServerException e) {
            throw new SearchException("Solr search error occured.");
        }
        
        
    }

    private void setResultCount(LOSearchResultList searchResultList,
            QueryResponse response, SearchType searchType) {
        
        if (SearchType.ARTICLE.equals(searchType)) {
            searchResultList.setArticleCount(response.getResults().getNumFound());
        } else if (SearchType.PROVIDER.equals(searchType)) {
            searchResultList.setOrgCount(response.getResults().getNumFound());
        } else {
            searchResultList.setLoCount(response.getResults().getNumFound());
        }
        
    }

    private ArticleResult createArticleSearchResult(SolrDocument doc) throws Exception {
        String imageUrl = doc.getFieldValue(LearningOpportunity.ARTICLE_PICTURE) != null 
                ? doc.getFieldValue(LearningOpportunity.ARTICLE_PICTURE).toString() : null;
        ArticleResult article = new ArticleResult(doc.getFieldValue(LearningOpportunity.TYPE).toString(), 
                                                doc.getFieldValue(LearningOpportunity.ARTICLE_URL).toString(), 
                                                doc.getFieldValue(LearningOpportunity.NAME).toString(), 
                                                doc.getFieldValue(LearningOpportunity.ARTICLE_EXCERPT).toString(), 
                                                imageUrl);
        return article;
    }

    private LOSearchResult createLOSearchResult(SolrDocument doc, String lang) throws Exception {
        String parentId = doc.get("parentId") != null ? doc.get("parentId").toString() : null;
        String losId = doc.get("losId") != null ? doc.get("losId").toString() : null;
        String id = doc.get("losId") != null ? doc.get("losId").toString() : doc.get("id").toString();
        String prerequisiteText = doc.get("prerequisite") != null ? doc.get("prerequisite").toString() : null;
        String prerequisiteCodeText = doc.get("prerequisiteCode") != null ? doc.get("prerequisiteCode").toString() : null;
        String credits = doc.get(LearningOpportunity.CREDITS) != null ? doc.get(LearningOpportunity.CREDITS).toString() : null;
        String lopName = getLopName(doc, lang);
        String edType = doc.get(LearningOpportunity.EDUCATION_TYPE) != null ? getEdType(doc) : null;
        String edDegree = getEdDegree(doc, lang);
        String edDegreeCode = doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE) != null ? doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE).toString() : null;
        String name = getName(doc, lang);
        String homeplace = getHomeplace(doc, lang);
        String lopId =  doc.get("lopId") != null ? doc.get("lopId").toString() : null;

        LOSearchResult lo = new LOSearchResult(
                id, name,
                lopId, lopName, prerequisiteText,
                prerequisiteCodeText, parentId, losId, doc.get("type").toString(), 
                credits, edType, edDegree, edDegreeCode, homeplace);

        updateAsStatus(lo, doc);
        return lo;

    }

    private String getHomeplace(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang, 
                LearningOpportunity.HOMEPLACE_DISPLAY_FI, 
                LearningOpportunity.HOMEPLACE_DISPLAY_SV, 
                LearningOpportunity.HOMEPLACE_DISPLAY_EN, 
                LearningOpportunity.HOMEPLACE_DISPLAY);
    }

    private String getEdDegree(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang, 
                LearningOpportunity.EDUCATION_DEGREE_FI, 
                LearningOpportunity.EDUCATION_DEGREE_SV, 
                LearningOpportunity.EDUCATION_DEGREE_EN, 
                LearningOpportunity.EDUCATION_DEGREE);
    }

    private String getName(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang, 
                LearningOpportunity.NAME_DISPLAY_FI, 
                LearningOpportunity.NAME_DISPLAY_SV, 
                LearningOpportunity.NAME_DISPLAY_EN, 
                LearningOpportunity.NAME);
    }

    private String getLopName(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang, 
                LearningOpportunity.LOP_NAME_DISPLAY_FI, 
                LearningOpportunity.LOP_NAME_DISPLAY_SV, 
                LearningOpportunity.LOP_NAME_DISPLAY_EN, 
                LearningOpportunity.LOP_NAME);
    }
    
    private String getTranslatedValue(SolrDocument doc, String lang, String fieldFi, String fieldSv, String fieldEn, String field) {
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(TarjontaConstants.TYPE_KK)
                && lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(fieldFi) != null) {
            return doc.getFieldValue(fieldFi).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(TarjontaConstants.TYPE_KK)
                && lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(fieldSv) != null) {
            return doc.getFieldValue(fieldSv).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(TarjontaConstants.TYPE_KK)
                && lang.equalsIgnoreCase("en")
                && doc.getFieldValue(fieldEn) != null) {
            return doc.getFieldValue(fieldEn).toString();
        }
        if (doc.getFieldValue(field) != null) {
            return doc.getFieldValue(field).toString();
        }
        
        return null;
    }

    @Override
    public List<LOSearchResult> searchLearningOpportunitiesByProvider(String lopId, String lang) throws SearchException {
        List<LOSearchResult> resultList = Lists.newArrayList();
        SolrQuery query = new LearningOpportunityByProviderQuery(lopId);
        QueryResponse response = null;
        try {
            response = loHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException("Solr search error occured.");
        }

        for (SolrDocument doc : response.getResults()) {
            String parentId = doc.get("parentId") != null ? doc.get("parentId").toString() : null;
            String losId = doc.get("losId") != null ? doc.get("losId").toString() : null;
            String id = doc.get("losId") != null ? doc.get("losId").toString() : doc.get("id").toString();
            String prerequisiteText = doc.get("prerequisite") != null ? doc.get("prerequisite").toString() : null;
            String prerequisiteCodeText = doc.get("prerequisiteCode") != null ? doc.get("prerequisiteCode").toString() : null;
            String credits = doc.get(LearningOpportunity.CREDITS) != null ? doc.get(LearningOpportunity.CREDITS).toString() : null;
            String lopName = getLopName(doc, lang);
            String edType = doc.get(LearningOpportunity.EDUCATION_TYPE) != null ? getEdType(doc) : null;
            String edDegree = getEdDegree(doc, lang);
            String edDegreeCode = doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE) != null ? doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE).toString() : null;
            String name = getName(doc, lang);
            String homeplace = getHomeplace(doc, lang);
            
            LOSearchResult lo = null;
            try {
                lo = new LOSearchResult(
                        id, name,
                        doc.get("lopId").toString(), lopName, prerequisiteText,
                        prerequisiteCodeText, parentId, losId, doc.get("type").toString(), 
                        credits, edType, edDegree, edDegreeCode, homeplace);

                updateAsStatus(lo, doc);
            } catch (Exception e) {
                continue;
            }
            resultList.add(lo);
        }
        return resultList;
    }

    private String getEdType(SolrDocument doc) {
        for (Object valO : doc.getFieldValues(LearningOpportunity.EDUCATION_TYPE)) {
            String val = valO.toString();
            if (!val.equals(SolrConstants.ED_TYPE_KAKSOIS)) {
                return val;
            }
        }
        return null;
    }

    /*
     * Adding facets to result
     */
    private void addFacetsToResult(LOSearchResultList searchResultList,
                                   QueryResponse response, String lang, List<String> facetFilters) {

        searchResultList.setTeachingLangFacet(getTeachingLangFacet(response, lang));
        searchResultList.setAppStatusFacet(getHaunTila(response));
        searchResultList.setEdTypeFacet(getEdTypeFacet(response, lang));
        searchResultList.setFilterFacet(getFilterFacet(facetFilters, lang));
        searchResultList.setPrerequisiteFacet(getPrerequisiteFacet(response, lang));
        searchResultList.setTopicFacet(getTopicFacet(response, lang));
        

    }

    private FacetValue getRecommendationFilter(String recommendationFilter, String fieldId) {
        FacetValue recFilter = new FacetValue(fieldId,
                fieldId,
                1,
                recommendationFilter);
        return recFilter;
    }

    /*
     * Adding the topic/theme facet to the search result
     */
    private Facet getTopicFacet(QueryResponse response, String lang) {
        FacetField themeF = response.getFacetField(LearningOpportunity.THEME);
        FacetField topicF = response.getFacetField(LearningOpportunity.TOPIC);
        Map<String, List<FacetValue>> themeTopicMap = createThemeTopicMap(topicF, lang);

        Facet topicFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (themeF != null) {
            for (Count curC : themeF.getValues()) {

                FacetValue newVal = new FacetValue(LearningOpportunity.THEME,
                        getLocalizedFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());
                newVal.setChildValues(themeTopicMap.get(curC.getName()));
                values.add(newVal);

            }
        }
        topicFacet.setFacetValues(values);
        return topicFacet;
    }

    private Map<String, List<FacetValue>> createThemeTopicMap(FacetField topicF, String lang) {
        Map<String, List<FacetValue>> resMap = new HashMap<String, List<FacetValue>>();
        if (topicF != null) {
            for (Count curC : topicF.getValues()) {
                FacetValue topic = new FacetValue(LearningOpportunity.TOPIC,
                        getLocalizedFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());
                String themeStr = curC.getName().split("\\.")[0];
                if (resMap.containsKey(themeStr)) {
                    resMap.get(themeStr).add(topic);
                } else {
                    List<FacetValue> topics = new ArrayList<FacetValue>();
                    topics.add(topic);
                    resMap.put(themeStr, topics);
                }
            }
        }
        return resMap;
    }

    /*
     * Adding the prerequisite facet to the search result.
     */
    private Facet getPrerequisiteFacet(QueryResponse response, String lang) {
        FacetField prerequisiteF = response.getFacetField(LearningOpportunity.PREREQUISITES);
        Facet prerequisiteFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (prerequisiteF != null) {
            for (Count curC : prerequisiteF.getValues()) {


                FacetValue newVal = new FacetValue(LearningOpportunity.PREREQUISITES,
                        curC.getName(),
                        curC.getCount(),
                        curC.getName());
                values.add(newVal);

            }
        }
        prerequisiteFacet.setFacetValues(values);
        return prerequisiteFacet;
    }

    /*
     * Education type facet
     */
    private Facet getEdTypeFacet(QueryResponse response, String lang) {
        Facet edTypeFacet = new Facet();
        FacetField edTypeField = response.getFacetField(LearningOpportunity.EDUCATION_TYPE);
        List<FacetValue> values = new ArrayList<FacetValue>();
        Map<String, List<FacetValue>> resMap = new HashMap<String, List<FacetValue>>();
        
        if (edTypeField != null) {
            for (Count curC : edTypeField.getValues()) {

                FacetValue newVal = new FacetValue(LearningOpportunity.EDUCATION_TYPE,
                        getLocalizedFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());
                
                String[] splits = curC.getName().split("\\.");
                
                if (splits.length == 2 ) {
                    String parentStr = splits[0];
                    if (resMap.containsKey(parentStr)) {
                        resMap.get(parentStr).add(newVal);
                    } else {
                        List<FacetValue> children = new ArrayList<FacetValue>();
                        children.add(newVal);
                        resMap.put(parentStr, children);
                    }
                } else {
                    values.add(newVal);
                }

            }
        }
        
        for (FacetValue curVal : values) {
            curVal.setChildValues(resMap.get(curVal.getValueId()));
        }
        
        edTypeFacet.setFacetValues(values);
        return edTypeFacet;
    }

    /*
     * Teaching language facet
     */
    private Facet getTeachingLangFacet(QueryResponse response, String lang) {

        FacetField teachingLangF = response.getFacetField(LearningOpportunity.TEACHING_LANGUAGE);
        Facet teachingLangFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (teachingLangF != null) {
            for (Count curC : teachingLangF.getValues()) {


                FacetValue newVal = new FacetValue(LearningOpportunity.TEACHING_LANGUAGE,
                        getLocalizedFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());
                values.add(newVal);

            }
        }
        teachingLangFacet.setFacetValues(values);
        return teachingLangFacet;
    }

    /*
     * Getting haun tila
     */
    private Facet getHaunTila(QueryResponse response) {
        Facet haunTila = new Facet();
        List<FacetValue> haunTilaVals = new ArrayList<FacetValue>();
        for (String curKey : response.getFacetQuery().keySet()) {
            if (curKey.contains("(asStart_0:[* TO NOW] AND asEnd_0:[NOW TO *])")) {
                FacetValue facVal = new FacetValue(LearningOpportunityQuery.APP_STATUS,
                        LearningOpportunityQuery.APP_STATUS_ONGOING,
                        response.getFacetQuery().get(curKey).longValue(),
                        LearningOpportunityQuery.APP_STATUS_ONGOING);
                haunTilaVals.add(facVal);
            } else if (curKey.contains("(asStart_0:[NOW TO *])")) {
                FacetValue facVal = new FacetValue(LearningOpportunityQuery.APP_STATUS,
                        LearningOpportunityQuery.APP_STATUS_UPCOMING,
                        response.getFacetQuery().get(curKey).longValue(),
                        LearningOpportunityQuery.APP_STATUS_UPCOMING);
                haunTilaVals.add(facVal);
            }
        }
        haunTila.setFacetValues(haunTilaVals);
        return haunTila;
    }

    /*
     * Facet composed of user's selections.
     * Used in search ui to display 0-selections.
     */
    private Facet getFilterFacet(List<String> facetFilters, String lang) {

        Facet fFilFacet = new Facet();
        List<FacetValue> qVals = new ArrayList<FacetValue>();
        for (String curFacFilter : facetFilters) {
            int index = curFacFilter.indexOf(':');
            String facId = curFacFilter.substring(index + 1);
            String facField = curFacFilter.substring(0, index);
            SolrDocument facDoc = this.getFacetDoc(facId, lang);
            String facName = facDoc != null ? String.format("%s", facDoc.getFieldValue(String.format("%s_fname", lang))) : facId;
            FacetValue newVal = new FacetValue(facField, facName, 0, facId);
            qVals.add(newVal);

        }
        fFilFacet.setFacetValues(qVals);
        return fFilFacet;
    }

    /*
     * Getting the localized name for the facet value.
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
     * Getting the facet doc.
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

        for (Map.Entry<String, Object> start : doc.entrySet()) {
            if (start.getKey().startsWith(AS_START_DATE_PREFIX)) {
                String endKey = new StringBuilder().append(AS_END_DATE_PREFIX)
                        .append(start.getKey().split("_")[1]).toString();

                Date startDate = ((List<Date>) start.getValue()).get(0);
                Date endDate = ((List<Date>) doc.get(endKey)).get(0);

                if (startDate.before(now) && now.before(endDate)) {
                    lo.setAsOngoing(true);
                    return;
                }

                if ((nextStarts == null && startDate.after(now)) || (startDate.after(now) && startDate.before(nextStarts))) {
                    nextStarts = startDate;
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

    @Override
    public List<Location> getDistricts(String lang) throws SearchException {
        SolrQuery query = new LocationQuery(LocationFields.TYPE, DISTRICT, lang);
        return executeSolrQuery(query);
    }

    @Override
    public List<Location> getChildLocations(List<String> districts, String lang)
            throws SearchException {
        SolrQuery query = new LocationQuery(LocationFields.PARENT, districts, lang);
        return executeSolrQuery(query);
    }

    @Override
    public SuggestedTermsResult searchSuggestedTerms(String term, String lang)
            throws SearchException {

        SolrQuery query = new AutocompleteQuery(term, lang);

        SuggestedTermsResult result = new SuggestedTermsResult();

        QueryResponse response = null;
        try {
            response = loHttpSolrServer.query(query);

            FacetField nameF = response.getFacetField(String.format("%s_%s", LearningOpportunity.NAME_AUTO, lang.toLowerCase()));
            if (nameF != null) {
                List<String> terms = new ArrayList<String>();
                for (Count curC : nameF.getValues()) {

                    terms.add(curC.getName());

                }
                result.setLoNames(terms);
            }

            FacetField freeF = response.getFacetField(String.format("%s_%s", LearningOpportunity.FREE_AUTO, lang.toLowerCase()));
            if (freeF != null) {
                List<String> terms = new ArrayList<String>();
                for (Count curC : freeF.getValues()) {

                    terms.add(curC.getName());

                }
                result.setKeywords(terms);
            }

        } catch (SolrServerException e) {
            throw new SearchException("Solr search error occured.");
        }

        return result;
    }


}
