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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.domain.AoSolrSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResult;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.Facet;
import fi.vm.sade.koulutusinformaatio.domain.FacetValue;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.ProviderResult;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ApplicationOptionQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ApplicationSystemQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ArticleQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.AutocompleteQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LearningOpportunityQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LocationQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderNameFirstCharactersQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderQuery;
import fi.vm.sade.koulutusinformaatio.service.impl.query.ProviderTypeQuery;

@Component
public class SearchServiceSolrImpl implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceSolrImpl.class);

    private static final String AS_START_DATE_PREFIX = "asStart_";
    private static final String AS_END_DATE_PREFIX = "asEnd_";
    private static final String DISTRICT = "maakunta";
    private static final String SOLR_ERROR = "Solr search error occured.";
    private static final String S_FNAME = "%s_fname";

    private final HttpSolrServer lopHttpSolrServer;
    private final HttpSolrServer loHttpSolrServer;
    private final HttpSolrServer locationHttpSolrServer;

    private EducationDataQueryService educationDataQueryService;
    

    @Autowired
    public SearchServiceSolrImpl(@Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasSolrServer,
            @Qualifier("loAliasSolrServer") final HttpSolrServer loAliasSolrServer,
            @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasSolrServer,
            EducationDataQueryService educationDataQueryService) {
        this.lopHttpSolrServer = lopAliasSolrServer;
        this.loHttpSolrServer = loAliasSolrServer;
        this.locationHttpSolrServer = locationAliasSolrServer;
        this.educationDataQueryService = educationDataQueryService;
    }

    @Override
    public List<Provider> searchLearningOpportunityProviders(String term,
            String asId,
            List<String> baseEducations,
            boolean vocational,
            boolean nonVocational,
            int start,
            int rows,
            String lang,
            boolean prefix,
            String type) throws SearchException {

        List<Provider> providers = new ArrayList<Provider>();
        SolrQuery query = new ProviderQuery(term, asId, baseEducations, start, rows, vocational, nonVocational, lang, prefix, type);

        QueryResponse queryResponse = null;
        try {
            queryResponse = lopHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException(SOLR_ERROR);
        }

        for (SolrDocument result : queryResponse.getResults()) {
            Provider provider = new Provider();
            provider.setId(result.get("id").toString());

            // TODO: i18n handling
            Map<String, String> texts = Maps.newHashMap();
            if (result != null && result.get("name_fi") != null) {
                texts.put("fi", result.get("name_fi").toString());
            }
            if (result != null && result.get("name_sv") != null) {
                texts.put("sv", result.get("name_sv").toString());
            }
            if (result != null && result.get("name_en") != null) {
                texts.put("en", result.get("name_en").toString());
            }

            provider.setName(new I18nText(texts));
            providers.add(provider);
        }
        return providers;
    }

    @Override
    public List<Provider> searchLearningOpportunityProviders(String term, String lang, boolean prefix, String type) throws SearchException {
        return searchLearningOpportunityProviders(term, null, null, false, false, 0, Integer.MAX_VALUE, lang, prefix, type);
    }

    @Override
    public List<AoSolrSearchResult> searchOngoingApplicationOptions(String applicationSystemId, List<Provider> learningOpportunityProviders,
            List<String> baseEducations)
            throws SearchException {

        List<AoSolrSearchResult> aos = new ArrayList<AoSolrSearchResult>();
        SolrQuery query = new ApplicationOptionQuery(applicationSystemId, baseEducations);

        QueryResponse queryResponse = null;
        try {
            queryResponse = lopHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException(SOLR_ERROR);
        }
        for (SolrDocument result : queryResponse.getResults()) {
            String id = (String) result.getFieldValue(SolrUtil.AoFields.ID);
            String lopId = (String) result.getFieldValue(SolrUtil.AoFields.LOP_ID);
            List<String> prerequisites = (List<String>) result.getFieldValue(SolrUtil.AoFields.PREREQUISITES);
            String asId = (String) result.getFieldValue(SolrUtil.AoFields.AS_ID);
            Date startDate = (Date) result.getFieldValue(SolrUtil.AoFields.START_DATE);
            Date endDate = (Date) result.getFieldValue(SolrUtil.AoFields.END_DATE);
            AoSolrSearchResult ao = new AoSolrSearchResult(id, lopId, prerequisites, asId, startDate, endDate);
            aos.add(ao);
        }
        return aos;

    }

    @Override
    public List<ArticleResult> searchArticleSuggestions(String filter, String lang) throws SearchException {

        LOG.debug("Searching suggestions: {}", filter);

        List<ArticleResult> articles = new ArrayList<ArticleResult>();

        SolrQuery query = new ArticleQuery(filter, lang);

        try {
            LOG.debug(
                    URLDecoder.decode(
                            new StringBuilder().append(
                                    "Searching learning opportunities with query string: ").append(
                                    query.toString()).toString(), "utf-8"
                            )
                    );
        } catch (UnsupportedEncodingException e) {
            LOG.debug("Could not log search query");
        }

        QueryResponse queryResponse = null;
        try {
            queryResponse = loHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException(SOLR_ERROR);
        }

        LOG.debug("Response size: {}", queryResponse.getResults().size());
        for (SolrDocument result : queryResponse.getResults()) {
            try {
                articles.add(createArticleSearchResult(result));
            } catch (Exception ex) {
                LOG.warn(ex.getMessage());
            }
        }

        return articles;
    }

    private String getDateLimitStr(boolean upcomingLater) {
        Calendar limit = Calendar.getInstance();
        if (!upcomingLater) {
            int month = limit.get(Calendar.MONTH) <= 5 ? 5 : 11;
            int dayOfMonth = limit.get(Calendar.MONTH) <= 5 ? 30 : 31;

            limit.set(Calendar.MONTH, month);
            limit.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        } else {
            int month = limit.get(Calendar.MONTH) <= 5 ? 11 : 5;
            int year = limit.get(Calendar.MONTH) <= 5 ? limit.get(Calendar.YEAR) : limit.get(Calendar.YEAR) + 1;
            int dayOfMonth = limit.get(Calendar.MONTH) <= 5 ? 31 : 30;

            limit.set(Calendar.MONTH, month);
            limit.set(Calendar.YEAR, year);
            limit.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return dateFormat.format(limit.getTime());

    }

    @Override
    public LOSearchResultList searchLearningOpportunities(String term, String prerequisite,
            List<String> cities, List<String> facetFilters,
            List<String> articleFilters,
            List<String> providerFilters,
            String lang, boolean ongoing, boolean upcoming, boolean upcomingLater,
            int start, int rows, String sort, String order,
            String lopFilter, String educationCodeFilter, List<String> excludes, String asId, SearchType searchType) throws SearchException {
        LOSearchResultList searchResultList = new LOSearchResultList();
        String trimmed = term.trim();
        String fixed = SolrUtil.fixString(trimmed);
        if (!trimmed.isEmpty()) {

            String upcomingLimit = getDateLimitStr(false);
            String upcomingLaterLimit = getDateLimitStr(true);

            SolrQuery query = null;
            if (SearchType.LO.equals(searchType)) {
                query = new LearningOpportunityQuery(fixed, prerequisite,
                        cities, facetFilters,
                        lang, ongoing, upcoming,
                        upcomingLater,
                        start, rows, sort, order,
                        lopFilter, educationCodeFilter, excludes, upcomingLimit, upcomingLaterLimit, asId);
            } else if (SearchType.ARTICLE.equals(searchType)) {//lopFilter == null && educationCodeFilter == null && (excludes == null || excludes.isEmpty())) {
                query = new ArticleQuery(fixed, lang,
                        start, rows, sort, order,
                        facetFilters, articleFilters);
            } else if (SearchType.PROVIDER.equals(searchType)) {
                query = new ProviderQuery(fixed, lang, providerFilters, cities, start, rows, sort, order);
            }

            try {
                LOG.debug(
                        URLDecoder.decode(
                                new StringBuilder().append(
                                        "Searching learning opportunities with query string: ").append(
                                        query != null ? query.toString() : "").toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.debug("Could not log search query");
            }

            QueryResponse response = null;
            try {
                if (query != null && !SearchType.PROVIDER.equals(searchType)) {
                    response = loHttpSolrServer.query(query);
                } else {
                    response = lopHttpSolrServer.query(query);
                }
                setResultCount(searchResultList, response, searchType);
            } catch (SolrServerException e) {
                throw new SearchException(SOLR_ERROR);
            }

            if (response != null) {
                for (SolrDocument doc : response.getResults()) {
                    try {
                        if (SearchType.LO.equals(searchType)) {
                            searchResultList.getResults().add(createLOSearchResult(doc, lang));
                        } else if (SearchType.ARTICLE.equals(searchType)) {
                            searchResultList.getArticleresults().add(createArticleSearchResult(doc));
                        } else if (SearchType.PROVIDER.equals(searchType)) {
                            searchResultList.getProviderResults().add(createProviderSearchResult(doc, lang));
                        }
                    } catch (Exception ex) {
                        LOG.warn("Exception while creating search result", ex);
                        continue;
                    }
                }
            }

            if (SearchType.LO.equals(searchType)) {
                addFacetsToResult(searchResultList, response, lang, facetFilters, upcomingLimit, upcomingLaterLimit);
            } else if (response != null && SearchType.ARTICLE.equals(searchType)) {
                addArticleFacetsToResult(searchResultList, response, lang, articleFilters);
            } else if (response != null) {
                addProviderFacetsToResult(searchResultList, response, lang, providerFilters);
            }

            if (lopFilter != null) {
                searchResultList.setLopRecommendationFilter(getRecommendationFilter(lopFilter, "lopFilter"));
            }

            if (educationCodeFilter != null) {
                searchResultList.setEducationCodeRecommendationFilter(getRecommendationFilter(educationCodeFilter, "educationCodeFilter"));
            }

            //Setting result counts of other searches (one of article, provider or lo)
            if (searchType.LO.equals(searchType)) {
                setOtherResultCounts(fixed, lang, start, sort, order, cities, facetFilters, articleFilters, providerFilters, ongoing, upcoming, upcomingLater,
                        lopFilter, educationCodeFilter, excludes, SearchType.ARTICLE, searchResultList, upcomingLimit, upcomingLaterLimit, asId);
            } else if (SearchType.ARTICLE.equals(searchType)) {
                setOtherResultCounts(fixed, lang, start, sort, order, cities, facetFilters, articleFilters, providerFilters, ongoing, upcoming, upcomingLater,
                        lopFilter, educationCodeFilter, excludes, SearchType.LO, searchResultList, upcomingLimit, upcomingLaterLimit, asId);
            } else if (SearchType.PROVIDER.equals(searchType)) {
                setOtherResultCounts(fixed, lang, start, sort, order, cities, facetFilters, articleFilters, providerFilters, ongoing, upcoming, upcomingLater,
                        lopFilter, educationCodeFilter, excludes, SearchType.PROVIDER, searchResultList, upcomingLimit, upcomingLaterLimit, asId);
            }

            searchResultList.setTotalCount(searchResultList.getArticleCount() + searchResultList.getLoCount() + searchResultList.getOrgCount());

        }

        return searchResultList;
    }

    private void addProviderFacetsToResult(LOSearchResultList searchResultList,
            QueryResponse response, String lang, List<String> providerFilters) {

        FacetField providerTypeF = response.getFacetField("oltype_ffm");
        Facet providerTypeFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (providerTypeF != null) {
            for (Count curC : providerTypeF.getValues()) {

                FacetValue newVal = new FacetValue("oltype_ffm",
                        this.getLocalizedLopFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());
                values.add(newVal);
            }
        }
        providerTypeFacet.setFacetValues(values);
        searchResultList.setProviderTypeFacet(providerTypeFacet);

    }

    @SuppressWarnings("unchecked")
    private ProviderResult createProviderSearchResult(SolrDocument doc, String lang) {

        ProviderResult result = new ProviderResult();
        String name = getTranslatedValue(doc, lang,
                LearningOpportunity.NAME_FI,
                LearningOpportunity.NAME_SV,
                LearningOpportunity.NAME_EN,
                LearningOpportunity.NAME_FI,
                "ORGANISAATIO");
        result.setName(name);
        result.setId(doc.getFieldValue("id").toString());
        String descr = getTranslatedValue(doc, lang,
                "address_fi_str_display",
                "address_sv_str_display",
                "address_en_str_display",
                "address_fi_str_display",
                "ORGANISAATIO");
        result.setAddress(descr);

        List<String> asIds = (List<String>) (doc.getFieldValue("asIds"));
        result.setProviderOrg(asIds != null && asIds.size() > 0);

        try {
            Picture pict = this.educationDataQueryService.getPicture(result.getId());
            result.setThumbnailEncoded(pict.getThumbnailEncoded());
        } catch (ResourceNotFoundException ex) {
            LOG.debug("No thumbnail for: {}", result.getId());
        }

        return result;
    }

    private void setOtherResultCounts(String term, String lang, int start,
            String sort, String order, List<String> cities,
            List<String> facetFilters, List<String> articleFilters, List<String> providerFilters,
            boolean ongoing, boolean upcoming, boolean upcomingLater,
            String lopFilter, String educationCodeFilter, List<String> excludes, SearchType searchType, LOSearchResultList searchResultList,
            String upcomingLimit, String upcomingLaterLimit, String asId) throws SearchException {

        SolrQuery query = null;

        if (SearchType.PROVIDER.equals(searchType)) {
            query = new LearningOpportunityQuery(term, null,
                    cities, facetFilters,
                    lang, ongoing, upcoming, upcomingLater,
                    start, 0, sort, order,
                    lopFilter, educationCodeFilter, excludes, upcomingLimit, upcomingLaterLimit, asId);
            try {
                QueryResponse response = loHttpSolrServer.query(query);
                setResultCount(searchResultList, response, SearchType.LO);
            } catch (SolrServerException e) {
                throw new SearchException(SOLR_ERROR);
            }

            query = new ArticleQuery(term, lang,
                    start, 0, sort, order,
                    facetFilters, articleFilters);

            try {
                QueryResponse response = loHttpSolrServer.query(query);
                setResultCount(searchResultList, response, SearchType.ARTICLE);
            } catch (SolrServerException e) {
                throw new SearchException(SOLR_ERROR);
            }

        } else {

            if (SearchType.LO.equals(searchType)) {
                query = new LearningOpportunityQuery(term, null,
                        cities, facetFilters,
                        lang, ongoing, upcoming, upcomingLater,
                        start, 0, sort, order,
                        lopFilter, educationCodeFilter, excludes, upcomingLimit, upcomingLaterLimit, asId);
            } else if (SearchType.ARTICLE.equals(searchType)) {//lopFilter == null && educationCodeFilter == null && (excludes == null || excludes.isEmpty())) {
                query = new ArticleQuery(term, lang,
                        start, 0, sort, order,
                        facetFilters, articleFilters);
            }
            if (query != null) {
                try {
                    QueryResponse response = loHttpSolrServer.query(query);
                    setResultCount(searchResultList, response, searchType);
                } catch (SolrServerException e) {
                    throw new SearchException(SOLR_ERROR);
                }
            } else {
                searchResultList.setArticleCount(0);
            }

            query = new ProviderQuery(term, lang, providerFilters, cities, start, 0, sort, order);
            try {
                QueryResponse response = lopHttpSolrServer.query(query);
                setResultCount(searchResultList, response, SearchType.PROVIDER);
            } catch (SolrServerException ex) {
                throw new SearchException(SOLR_ERROR);
            }
        }

    }

    private void setResultCount(LOSearchResultList searchResultList,
            QueryResponse response, SearchType searchType) {

        long count = 0;
        if (response != null) {
            count = response.getResults().getNumFound();
        }

        if (SearchType.ARTICLE.equals(searchType)) {
            searchResultList.setArticleCount(count);
        } else if (SearchType.PROVIDER.equals(searchType)) {
            searchResultList.setOrgCount(count);
        } else {
            searchResultList.setLoCount(count);
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
        String parentId = doc.get(LearningOpportunity.PARENT_ID) != null ? doc.get(LearningOpportunity.PARENT_ID).toString() : null;
        String losId = doc.get(LearningOpportunity.LOS_ID) != null ? doc.get(LearningOpportunity.LOS_ID).toString() : null;
        String id = doc.get(LearningOpportunity.LOS_ID) != null ? doc.get(LearningOpportunity.LOS_ID).toString() : doc.get(LearningOpportunity.ID).toString();
        String prerequisiteText = getPrerequisiteText(doc, lang);
        String prerequisiteCodeText = doc.get(LearningOpportunity.PREREQUISITE_CODE) != null
                ? doc.get(LearningOpportunity.PREREQUISITE_CODE).toString() : null;
        String credits = getCredits(doc, lang);
        List<String> lopNames = getLopNames(doc, lang);
        String edType = doc.get(LearningOpportunity.EDUCATION_TYPE_DISPLAY) != null
                ? doc.getFieldValue(LearningOpportunity.EDUCATION_TYPE_DISPLAY).toString().replace(".", "") : null;
        String edDegree = getEdDegree(doc, lang);
        String edDegreeCode = doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE) != null
                ? doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE).toString() : null;
        String name = getName(doc, lang);
        String homeplace = getHomeplace(doc, lang);
        List<String> lopId = doc.get(LearningOpportunity.LOP_ID) != null ? (List<String>) (doc.get(LearningOpportunity.LOP_ID)) : new ArrayList<String>();
        String childName = doc.get(LearningOpportunity.CHILD_NAME) != null ? getChildName(doc) : null;
        List<String> subjects = getSubjects(doc, lang);

        LOG.debug("gathered info now creating search result: {}", id);

        LOSearchResult lo = new LOSearchResult(
                id, name,
                lopId, lopNames, prerequisiteText,
                prerequisiteCodeText, parentId, losId, doc.get("type").toString(),
                credits, edType, edDegree, edDegreeCode, homeplace, childName, subjects);

        LOG.debug("Created search result: {}", id);

        updateAsStatus(lo, doc);

        LOG.debug("Updated as status: {}", id);

        return lo;

    }

    private List<String> getSubjects(SolrDocument doc, String lang) {
        if (lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(LearningOpportunity.SUBJECT_FI) != null) {
            return (List<String>) doc.getFieldValue(LearningOpportunity.SUBJECT_FI);
        }
        if (lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(LearningOpportunity.SUBJECT_SV) != null) {
            return (List<String>) doc.getFieldValue(LearningOpportunity.SUBJECT_SV);
        }
        if (lang.equalsIgnoreCase("en")
                && doc.getFieldValue(LearningOpportunity.SUBJECT_EN) != null) {
            return (List<String>) doc.getFieldValue(LearningOpportunity.SUBJECT_EN);
        }
        return null;
    }

    private String getChildName(SolrDocument doc) {
        @SuppressWarnings("unchecked")
        List<String> childNames = (List<String>) (doc.get(LearningOpportunity.CHILD_NAME));
        if (childNames != null && !childNames.isEmpty()) {
            return childNames.get(0);
        }
        return null;
    }

    private String getPrerequisiteText(SolrDocument doc, String lang) {
        if (lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_FI) != null) {
            return doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_FI).toString();
        }
        if (lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_SV) != null) {
            return doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_SV).toString();
        }
        if (lang.equalsIgnoreCase("en")
                && doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_EN) != null) {
            return doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY_EN).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY) != null) {
            return doc.getFieldValue(LearningOpportunity.PREREQUISITE_DISPLAY).toString();
        }
        return null;
    }

    private String getHomeplace(SolrDocument doc, String lang) {
        if (lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_FI) != null) {
            return doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_FI).toString();
        }
        if (lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_SV) != null) {
            return doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_SV).toString();
        }
        if (lang.equalsIgnoreCase("en")
                && doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_EN) != null) {
            return doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY_EN).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY) != null) {
            return doc.getFieldValue(LearningOpportunity.HOMEPLACE_DISPLAY).toString();
        }
        return null;
    }

    private String getEdDegree(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang,
                LearningOpportunity.EDUCATION_DEGREE_FI,
                LearningOpportunity.EDUCATION_DEGREE_SV,
                LearningOpportunity.EDUCATION_DEGREE_EN,
                LearningOpportunity.EDUCATION_DEGREE,
                TarjontaConstants.TYPE_KK);
    }

    private String getName(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang,
                LearningOpportunity.NAME_DISPLAY_FI,
                LearningOpportunity.NAME_DISPLAY_SV,
                LearningOpportunity.NAME_DISPLAY_EN,
                LearningOpportunity.NAME,
                TarjontaConstants.TYPE_KK);
    }

    private String getCredits(SolrDocument doc, String lang) {
        return getTranslatedValue(doc, lang,
                LearningOpportunity.CREDITS_FI,
                LearningOpportunity.CREDITS_SV,
                LearningOpportunity.CREDITS_EN,
                LearningOpportunity.CREDITS,
                TarjontaConstants.TYPE_KK);
    }

    @SuppressWarnings("unchecked")
    private List<String> getLopNames(SolrDocument doc, String lang) {
        if (lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_FI) != null) {
            return (List<String>) (doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_FI));//toString();
        }
        if (lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_SV) != null) {
            return (List<String>) (doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_SV));//.toString();
        }
        if (lang.equalsIgnoreCase("en")
                && doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_EN) != null) {
            return (List<String>) (doc.getFieldValue(LearningOpportunity.LOP_NAME_DISPLAY_EN));//.toString();
        }
        if (doc.getFieldValue(LearningOpportunity.LOP_NAME) != null) {
            return (List<String>) (doc.getFieldValue(LearningOpportunity.LOP_NAME));//.toString();
        }
        return new ArrayList<String>();
    }

    private String getTranslatedValue(SolrDocument doc, String lang, String fieldFi, String fieldSv, String fieldEn, String field, String type) {
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(type)//TarjontaConstants.TYPE_KK)
                && lang.equalsIgnoreCase("fi")
                && doc.getFieldValue(fieldFi) != null) {
            return doc.getFieldValue(fieldFi).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(type)
                && lang.equalsIgnoreCase("sv")
                && doc.getFieldValue(fieldSv) != null) {
            return doc.getFieldValue(fieldSv).toString();
        }
        if (doc.getFieldValue(LearningOpportunity.TYPE) != null
                && doc.getFieldValue(LearningOpportunity.TYPE).toString().equals(type)
                && lang.equalsIgnoreCase("en")
                && doc.getFieldValue(fieldEn) != null) {
            return doc.getFieldValue(fieldEn).toString();
        }
        if (doc.getFieldValue(field) != null) {
            return doc.getFieldValue(field).toString();
        }

        return null;
    }

    private void addArticleFacetsToResult(LOSearchResultList searchResultList,
            QueryResponse response, String lang, List<String> articleFilters) {
        FacetField articleContentTypeF = response.getFacetField(LearningOpportunity.ARTICLE_CONTENT_TYPE);
        Facet articleContentTypeFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        if (articleContentTypeF != null) {
            for (Count curC : articleContentTypeF.getValues()) {

                FacetValue newVal = new FacetValue(LearningOpportunity.ARTICLE_CONTENT_TYPE,
                        curC.getName(),
                        curC.getCount(),
                        curC.getName());
                values.add(newVal);

            }
        }
        articleContentTypeFacet.setFacetValues(values);
        searchResultList.setArticleContentTypeFacet(articleContentTypeFacet);

    }

    /*
     * Adding facets to result
     */
    private void addFacetsToResult(LOSearchResultList searchResultList,
            QueryResponse response, String lang, List<String> facetFilters,
            String upcomingLimit, String upcomingLaterLimit) {

        searchResultList.setTeachingLangFacet(getTeachingLangFacet(response, lang, facetFilters));
        searchResultList.setAppStatusFacet(getHaunTila(response, upcomingLimit, upcomingLaterLimit));
        searchResultList.setApplicationSystemFacet(getAppSystemFacet(response, lang, facetFilters));
        searchResultList.setEdTypeFacet(getEdTypeFacet(response, lang));
        searchResultList.setFilterFacet(getFilterFacet(facetFilters, lang));
        searchResultList.setPrerequisiteFacet(getPrerequisiteFacet(response, facetFilters, lang));
        searchResultList.setTopicFacet(getTopicFacet(response, lang));
        searchResultList.setFotFacet(getFotFacet(response, lang, facetFilters));
        searchResultList.setTimeOfTeachingFacet(getTimeOfTeachingFacet(response, lang, facetFilters));
        searchResultList.setFormOfStudyFacet(getFormOfStudyFacet(response, lang, facetFilters));

    }

    private Facet getAppSystemFacet(QueryResponse response, String lang, List<String> facetFilters) {
        FacetField asF = response.getFacetField(LearningOpportunity.AS_FACET);
        Facet asFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        boolean isFilterSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.contains(LearningOpportunity.AS_FACET)) {
                isFilterSet = true;
            }
        }

        if (asF != null) {
            for (Count curC : asF.getValues()) {

                long count = isFilterSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.AS_FACET,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
                        curC.getName());
                values.add(newVal);

            }
        }
        asFacet.setFacetValues(values);
        return asFacet;
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
                if (newVal.getChildValues() != null) {
                    for (FacetValue curchild : newVal.getChildValues()) {
                        curchild.setParentId(newVal.getValueId());
                    }
                }

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
    private Facet getPrerequisiteFacet(QueryResponse response, List<String> facetFilters, String lang) {
        FacetField prerequisiteF = response.getFacetField(LearningOpportunity.PREREQUISITES);
        Facet prerequisiteFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        boolean isPrereqSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.startsWith("prerequisites")) {
                isPrereqSet = true;
            }
        }
        if (prerequisiteF != null) {
            for (Count curC : prerequisiteF.getValues()) {

                long count = isPrereqSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.PREREQUISITES,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
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
        List<FacetValue> roots = new ArrayList<FacetValue>();
        Map<String, List<FacetValue>> resMap = new HashMap<String, List<FacetValue>>();

        if (edTypeField != null) {
            for (Count curC : edTypeField.getValues()) {

                FacetValue newVal = new FacetValue(LearningOpportunity.EDUCATION_TYPE,
                        getLocalizedFacetName(curC.getName(), lang),
                        curC.getCount(),
                        curC.getName());

                values.add(newVal);

                String[] splits = curC.getName().split("\\.");

                if ((splits.length >= 2 && !splits[0].equals("et01")) || (splits.length >= 3 && splits[0].equals("et01"))) {
                    int endIndex = curC.getName().lastIndexOf('.');
                    String parentStr = curC.getName().substring(0, endIndex);
                    if (resMap.containsKey(parentStr)) {
                        resMap.get(parentStr).add(newVal);
                    } else {
                        List<FacetValue> children = new ArrayList<FacetValue>();
                        children.add(newVal);
                        resMap.put(parentStr, children);
                    }
                } else {
                    roots.add(newVal);
                }

            }
        }

        for (FacetValue curVal : values) {
            curVal.setChildValues(resMap.get(curVal.getValueId()));
            if (curVal.getChildValues() != null) {
                for (FacetValue curChild : curVal.getChildValues()) {
                    curChild.setParentId(curVal.getValueId());
                }
            }
        }

        edTypeFacet.setFacetValues(roots);
        return edTypeFacet;
    }

    /*
     * Teaching language facet
     */
    private Facet getTeachingLangFacet(QueryResponse response, String lang, List<String> facetFilters) {

        FacetField teachingLangF = response.getFacetField(LearningOpportunity.TEACHING_LANGUAGE);
        Facet teachingLangFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();

        boolean isFilterSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.contains(LearningOpportunity.TEACHING_LANGUAGE)) {
                isFilterSet = true;
            }
        }

        if (teachingLangF != null) {
            for (Count curC : teachingLangF.getValues()) {

                long count = isFilterSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.TEACHING_LANGUAGE,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
                        curC.getName());
                values.add(newVal);

            }
        }
        teachingLangFacet.setFacetValues(values);
        return teachingLangFacet;
    }

    /*
     * Form of teaching facet
     */
    private Facet getFotFacet(QueryResponse response, String lang, List<String> facetFilters) {

        FacetField fotF = response.getFacetField(LearningOpportunity.FORM_OF_TEACHING);
        Facet fotFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();
        boolean isFilterSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.contains(LearningOpportunity.FORM_OF_TEACHING)) {
                isFilterSet = true;
            }
        }

        if (fotF != null) {
            for (Count curC : fotF.getValues()) {

                long count = isFilterSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.FORM_OF_TEACHING,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
                        curC.getName());
                values.add(newVal);

            }
        }
        fotFacet.setFacetValues(values);
        return fotFacet;
    }

    /*
     * Time of teaching facet
     */
    private Facet getTimeOfTeachingFacet(QueryResponse response, String lang, List<String> facetFilters) {

        FacetField timeOfTeachingF = response.getFacetField(LearningOpportunity.TIME_OF_TEACHING);
        Facet timeOfTeachingFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();

        boolean isFilterSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.contains(LearningOpportunity.TIME_OF_TEACHING)) {
                isFilterSet = true;
            }
        }

        if (timeOfTeachingF != null) {
            for (Count curC : timeOfTeachingF.getValues()) {

                long count = isFilterSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.TIME_OF_TEACHING,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
                        curC.getName());
                values.add(newVal);

            }
        }
        timeOfTeachingFacet.setFacetValues(values);
        return timeOfTeachingFacet;
    }

    /*
     * Form of study facet
     */
    private Facet getFormOfStudyFacet(QueryResponse response, String lang, List<String> facetFilters) {

        FacetField timeOfTeachingF = response.getFacetField(LearningOpportunity.FORM_OF_STUDY);
        Facet timeOfTeachingFacet = new Facet();
        List<FacetValue> values = new ArrayList<FacetValue>();

        boolean isFilterSet = false;
        for (String curFilter : facetFilters) {
            if (curFilter.contains(LearningOpportunity.FORM_OF_STUDY)) {
                isFilterSet = true;
            }
        }

        if (timeOfTeachingF != null) {
            for (Count curC : timeOfTeachingF.getValues()) {

                long count = isFilterSet ? 0 : curC.getCount();

                FacetValue newVal = new FacetValue(LearningOpportunity.FORM_OF_STUDY,
                        getLocalizedFacetName(curC.getName(), lang),
                        count,
                        curC.getName());
                values.add(newVal);
            }
        }
        timeOfTeachingFacet.setFacetValues(values);
        return timeOfTeachingFacet;
    }

    /*
     * Getting haun tila
     */
    private Facet getHaunTila(QueryResponse response, String upcomingLimit, String upcomingLaterLimit) {
        Facet haunTila = new Facet();
        List<FacetValue> haunTilaVals = new ArrayList<FacetValue>();
        for (String curKey : response.getFacetQuery().keySet()) {
            if (curKey.contains("[* TO NOW] AND asEnd_0:[NOW TO *])")) {
                FacetValue facVal = new FacetValue(SolrUtil.APP_STATUS,
                        SolrUtil.APP_STATUS_ONGOING,
                        response.getFacetQuery().get(curKey).longValue(),
                        SolrUtil.APP_STATUS_ONGOING);
                haunTilaVals.add(facVal);
            } else if (curKey.contains(String.format("NOW TO %s])", upcomingLimit))) {
                LOG.debug("upcoming limit: {}", upcomingLimit);
                String[] valueName = upcomingLimit.split("-");
                String kausi = Integer.parseInt(valueName[1]) > 6 ? "fall" : "spring";

                FacetValue facVal = new FacetValue(SolrUtil.APP_STATUS,
                        String.format("%s|%s", valueName[0], kausi),
                        response.getFacetQuery().get(curKey).longValue(),
                        SolrUtil.APP_STATUS_UPCOMING);
                haunTilaVals.add(facVal);
            } else if (curKey.contains(String.format("%s TO %s])", upcomingLimit, upcomingLaterLimit))) {
                LOG.debug("upcoming later limit: {}", upcomingLaterLimit);
                String[] valueName = upcomingLaterLimit.split("-");
                String kausi = Integer.parseInt(valueName[1]) > 6 ? "fall" : "spring";
                FacetValue facVal = new FacetValue(SolrUtil.APP_STATUS,
                        String.format("%s|%s", valueName[0], kausi),
                        response.getFacetQuery().get(curKey).longValue(),
                        SolrUtil.APP_STATUS_UPCOMING_LATER);
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
            String facName = facDoc != null ? String.format("%s", facDoc.getFieldValue(String.format(S_FNAME, lang))) : facId;
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
        query.setFields("id", String.format(S_FNAME, lang));
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = loHttpSolrServer.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return String.format("%s", curDoc.getFieldValue(String.format(S_FNAME, lang)));
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
        return null;
    }

    /*
     * Getting the localized name for the facet value.
     */
    private String getLocalizedLopFacetName(String id, String lang) {
        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("id:%s", id));
        query.setFields("id", String.format(S_FNAME, lang));
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = lopHttpSolrServer.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return String.format("%s", curDoc.getFieldValue(String.format(S_FNAME, lang)));
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
        return null;
    }

    /*
     * Getting the facet doc.
     */
    private SolrDocument getFacetDoc(String id, String lang) {
        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("id:%s", id));
        query.setFields("id", String.format(S_FNAME, lang));
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = loHttpSolrServer.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return curDoc;
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
        return null;
    }

    private void updateAsStatus(LOSearchResult lo, SolrDocument doc) {
        lo.setAsOngoing(false);
        Date now = new Date();
        //Date nextStarts = null;

        for (Map.Entry<String, Object> start : doc.entrySet()) {

            if (start.getKey().startsWith(AS_START_DATE_PREFIX)) {

                String endKey = new StringBuilder().append(AS_END_DATE_PREFIX)
                        .append(start.getKey().split("_")[1]).toString();

                // end date may be null for jatkuva haku
                Date startDate = ((List<Date>) start.getValue()).get(0);
                Date endDate = doc.get(endKey) != null ? ((List<Date>) doc.get(endKey)).get(0) : null;

                if (endDate != null) {
                    if (startDate.before(now) && now.before(endDate)) {
                        lo.setAsOngoing(true);
                        return;
                    }
                } else {
                    if (startDate.before(now)) {
                        lo.setAsOngoing(true);
                        return;
                    }
                }

                if (startDate.after(now)) {
                    lo.getNextApplicationPeriodStarts().add(startDate);
                }
            }
        }

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
            throw new SearchException(SOLR_ERROR);
        }

        return result;
    }

    @Override
    public List<String> getProviderFirstCharacterList(String lang) throws SearchException {
        SolrQuery query = new ProviderNameFirstCharactersQuery(lang);
        QueryResponse response = null;
        try {
            response = lopHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException(e.getMessage());
        }
        List<String> characters = Lists.newArrayList();
        for (GroupCommand gc : response.getGroupResponse().getValues()) {
            if (gc.getName().startsWith("startsWith")) {
                for (Group g : gc.getValues()) {
                    characters.add(g.getGroupValue());
                }
                break;
            }
        }
        return characters;
    }

    @Override
    public List<Code> getProviderTypes(String firstCharacter, String lang) throws SearchException {
        SolrQuery query = new ProviderTypeQuery(firstCharacter, lang);
        QueryResponse response = null;
        try {
            response = lopHttpSolrServer.query(query);
        } catch (SolrServerException e) {
            throw new SearchException(e.getMessage());
        }
        List<Code> types = Lists.newArrayList();
        for (GroupCommand gc : response.getGroupResponse().getValues()) {
            if (gc.getName().equals(SolrUtil.ProviderFields.TYPE_VALUE)) {
                for (Group g : gc.getValues()) {
                    if (g.getGroupValue() != null) {
                        SolrDocument result = g.getResult().get(0);
                        I18nText name = null;
                        if (!result.get(SolrUtil.ProviderFields.TYPE_VALUE).equals(SolrUtil.SolrConstants.PROVIDER_TYPE_UNKNOWN)) {
                            Map<String, String> nameTranslations = Maps.newHashMap();
                            nameTranslations.put("fi", (String) result.get(SolrUtil.ProviderFields.TYPE_FI));
                            nameTranslations.put("sv", (String) result.get(SolrUtil.ProviderFields.TYPE_SV));
                            nameTranslations.put("en", (String) result.get(SolrUtil.ProviderFields.TYPE_EN));
                            name = new I18nText(nameTranslations);
                        }
                        types.add(new Code((String) result.get(SolrUtil.ProviderFields.TYPE_VALUE), name));
                    }
                }
                break;
            }
        }
        return types;
    }

    @Override
    public List<CalendarApplicationSystem> findApplicationSystemsForCalendar(String targetGroupCode)
            throws SearchException {

        SolrQuery asQuery = new ApplicationSystemQuery(targetGroupCode);
        return queryCalendarApplicationSystems(asQuery);
    }

    @Override
    public List<CalendarApplicationSystem> findApplicationSystemsForCalendar()
            throws SearchException {

        SolrQuery asQuery = new ApplicationSystemQuery();
        return queryCalendarApplicationSystems(asQuery);

    }

    private List<CalendarApplicationSystem> queryCalendarApplicationSystems(SolrQuery query)
            throws SearchException {
        QueryResponse response = null;

        List<CalendarApplicationSystem> results = new ArrayList<CalendarApplicationSystem>();
        try {
            response = loHttpSolrServer.query(query);

            for (SolrDocument result : response.getResults()) {
                CalendarApplicationSystem as = new CalendarApplicationSystem();
                as.setId((String) (result.get(SolrUtil.LearningOpportunity.ID)));

                LOG.debug("Creating applicatoin system: {}", as.getId());

                Map<String, String> nameTranslations = Maps.newHashMap();
                String nameFi = (String) result.get(SolrUtil.LearningOpportunity.NAME_DISPLAY_FI);
                if (nameFi != null) {
                    nameTranslations.put("fi", nameFi);
                }
                String nameSv = (String) result.get(SolrUtil.LearningOpportunity.NAME_DISPLAY_SV);
                if (nameSv != null) {
                    nameTranslations.put("sv", nameSv);
                }
                String nameEn = (String) result.get(SolrUtil.LearningOpportunity.NAME_DISPLAY_EN);
                if (nameEn != null) {
                    nameTranslations.put("en", nameEn);
                }
                I18nText name = new I18nText(nameTranslations);
                as.setName(name);

                Boolean isVarsinainenHaku = (Boolean) result.get(SolrUtil.LearningOpportunity.AS_IS_VARSINAINEN);
                as.setVarsinainenHaku(isVarsinainenHaku != null ? isVarsinainenHaku.booleanValue() : true);
                setApplicationDates(as, result);

                results.add(as);

            }

        } catch (SolrServerException ex) {
            throw new SearchException(ex.getMessage());
        }

        return results;
    }

    private void setApplicationDates(CalendarApplicationSystem as, SolrDocument doc) {
        //Date now = new Date();
        //Date nextStarts = null;

        for (Map.Entry<String, Object> start : doc.entrySet()) {

            if (start.getKey().startsWith(AS_START_DATE_PREFIX)) {
                String indexStr = start.getKey().split("_")[1].toString();

                String endKey = new StringBuilder().append(AS_END_DATE_PREFIX)
                        .append(start.getKey().split("_")[1]).toString();

                // end date may be null for jatkuva haku
                Date startDate = ((List<Date>) start.getValue()).get(0);
                Date endDate = doc.get(endKey) != null ? ((List<Date>) doc.get(endKey)).get(0) : null;
                String periodNameFi = (String) (doc.get(new StringBuilder().append("asPeriodName").append("_").append(indexStr).append("_fi_ss").toString()));
                String periodNameSv = (String) (doc.get(new StringBuilder().append("asPeriodName").append("_").append(indexStr).append("_sv_ss").toString()));
                String periodNameEn = (String) (doc.get(new StringBuilder().append("asPeriodName").append("_").append(indexStr).append("_en_ss").toString()));

                DateRange curRange = new DateRange();
                curRange.setStartDate(startDate);
                curRange.setEndDate(endDate);

                ApplicationPeriod ap = new ApplicationPeriod();
                ap.setDateRange(curRange);

                I18nText nameI = new I18nText();
                nameI.put("fi", periodNameFi);
                nameI.put("sv", periodNameSv);
                nameI.put("en", periodNameEn);

                ap.setName(nameI);

                as.getApplicationPeriods().add(ap);
                //as.getApplicationDates().add(curRange);
            }
        }

    }

}
