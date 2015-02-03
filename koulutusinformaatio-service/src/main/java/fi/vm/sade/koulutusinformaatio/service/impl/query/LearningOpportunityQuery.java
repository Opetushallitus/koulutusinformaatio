package fi.vm.sade.koulutusinformaatio.service.impl.query;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.service.impl.SearchServiceSolrImpl;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.DisMaxParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQuery extends SolrQuery {

    private static final long serialVersionUID = -4340177833703968140L;
    
    public static final Logger LOG = LoggerFactory.getLogger(LearningOpportunityQuery.class);


    public LearningOpportunityQuery(String term, String prerequisite,
            List<String> cities, List<String> facetFilters, String lang, 
            boolean ongoing, boolean upcoming, boolean upcomingLater,
            int start, int rows, String sort, String order, 
            String lopFilter, String educationCodeFilter, String educationDomainFilter, List<String> excludes, 
            String upcomingDate, String upcomingLaterDate) {
        super(term);
        LOG.debug(String.format("Query term: (%s)", term));
        
        if (prerequisite != null) {
            this.addFilterQuery(String.format("%s:%s", LearningOpportunity.PREREQUISITES, prerequisite));
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty()) {
            this.addFilterQuery(
                    String.format("%s:(\"%s\")", LearningOpportunity.LOP_HOMEPLACE, Joiner.on("\" OR \"").join(cities))
                    );
        }
        
        setApplicationStatusFilters(ongoing, upcoming, upcomingLater, upcomingDate, upcomingLaterDate);
        
        if (lopFilter != null) {
            addLopRecommendationFilter(lopFilter, lang);
        }
        
        if (educationCodeFilter != null) {
            addEducationCodeRecommendationFilter(educationCodeFilter, lang);
        }
        
        if (educationDomainFilter != null) {
            addEducationDomainFilter(educationDomainFilter);
        }
        
        if (excludes != null) {
            for (String curExclude : excludes) {
                String[] parts = curExclude.split(":");
                if (parts.length == 2) {
                    this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, parts[0], parts[1]));
                }
            }
        }
        
        //leaving the faces, timestamps and application systems docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_ARTICLE));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_APPLICATION_SYSTEM));
        
        
        addFacetsToQuery(facetFilters);
        
        this.setParam("defType", "edismax");
        
        SolrUtil.setSearchFields(facetFilters, this);
        
        this.setParam("q.op", "AND");
        if (sort != null && "fi".equals(lang)) {
            this.addSort(LearningOpportunity.NAME_FI_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null && "sv".equals(lang)) {
            this.addSort(LearningOpportunity.NAME_SV_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null && "en".equals(lang)) {
            this.addSort(LearningOpportunity.NAME_EN_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null) {
            this.addSort(sort, order.equals("asc") ? ORDER.asc : ORDER.desc);
        }
    }
    
    private void setApplicationStatusFilters(boolean ongoing, boolean upcoming, boolean upcomingLater, String upcomingLimit, String upcomingLaterLimit) {
        StringBuilder ongoingFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            ongoingFQ.append(String.format("(asStart_%d:[* TO NOW] AND asEnd_%d:[NOW TO *])", i, i));
            ongoingFQ.append(String.format("OR (asStart_%d:[* TO NOW] AND -asEnd_%d:[* TO *])", i, i)); // jatkuvalla haulla ei välttämättä ole päättymisaikaa
            if (i != SolrUtil.AS_COUNT-1) {
                ongoingFQ.append(" OR ");
            }
        }
        if (ongoing) {
            this.addFilterQuery(ongoingFQ.toString());
        }
        
        StringBuilder upcomingFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            upcomingFQ.append(String.format("(asStart_%d:[NOW TO %s])", i, upcomingLimit));
            if (i != SolrUtil.AS_COUNT-1) {
                upcomingFQ.append(" OR ");
            }
        }
        if (upcoming) {
            this.addFilterQuery(upcomingFQ.toString());
        }
        
        
        
        StringBuilder upcomingLaterFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            upcomingLaterFQ.append(String.format("(asStart_%d:[%s TO %s])", i, upcomingLimit, upcomingLaterLimit));
            if (i != SolrUtil.AS_COUNT-1) {
                upcomingLaterFQ.append(" OR ");
            }
        }
        
        if (upcomingLater) {
            this.addFilterQuery(upcomingLaterFQ.toString());
        }
        
        this.addFacetQuery(ongoingFQ.toString());
        this.addFacetQuery(upcomingFQ.toString());
        this.addFacetQuery(upcomingLaterFQ.toString());
        
    }

    private void addFacetsToQuery(List<String> facetFilters) {
        this.setFacet(true);
        this.setFacetLimit(-1);
        this.addFacetField(LearningOpportunity.TEACHING_LANGUAGE);
        this.addFacetField(LearningOpportunity.EDUCATION_TYPE);
        this.addFacetField(LearningOpportunity.PREREQUISITES);
        this.addFacetField(LearningOpportunity.TOPIC);
        this.addFacetField(LearningOpportunity.THEME);
        this.addFacetField(LearningOpportunity.FORM_OF_TEACHING);
        this.addFacetField(LearningOpportunity.TIME_OF_TEACHING);
        this.addFacetField(LearningOpportunity.FORM_OF_STUDY);
        this.addFacetField(LearningOpportunity.AS_FACET);
        this.setFacetSort("index");
        
        for (String curFilter : facetFilters) {
            this.addFilterQuery(curFilter);
        }
        
    }

    private void addLopRecommendationFilter(String lopFilter,
            String lang) {
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_FI, lopFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_SV, lopFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_EN, lopFilter));
        } else {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME, lopFilter));
        }
    }
    
    private void addEducationCodeRecommendationFilter(String educationCodeFilter,
            String lang) { 
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, educationCodeFilter));
        } else {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        }
    }
    
    private void addEducationDomainFilter(String educationDomainFilter) {
        this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_DOMAIN, educationDomainFilter));
    }
}
