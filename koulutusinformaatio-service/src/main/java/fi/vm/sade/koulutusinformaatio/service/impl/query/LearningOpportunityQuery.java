package fi.vm.sade.koulutusinformaatio.service.impl.query;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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

    public static final List<String> FIELDS = Lists.newArrayList(
            LearningOpportunity.TEXT_FI,
            LearningOpportunity.TEXT_SV,
            LearningOpportunity.TEXT_EN,
            LearningOpportunity.TEXT_FI_WHOLE,
            LearningOpportunity.TEXT_SV_WHOLE,
            LearningOpportunity.TEXT_EN_WHOLE,
            LearningOpportunity.TEXT_BOOST_FI,
            LearningOpportunity.TEXT_BOOST_SV,
            LearningOpportunity.TEXT_BOOST_EN,
            LearningOpportunity.TEXT_BOOST_FI_WHOLE,
            LearningOpportunity.TEXT_BOOST_SV_WHOLE,
            LearningOpportunity.TEXT_BOOST_EN_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.NAME_AUTO_FI,
            LearningOpportunity.NAME_AUTO_SV,
            LearningOpportunity.NAME_AUTO_EN
    );
    
    public static final List<String> FIELDS_FI = Lists.newArrayList(
            LearningOpportunity.TEXT_FI,
            LearningOpportunity.TEXT_FI_WHOLE,
            LearningOpportunity.TEXT_BOOST_FI,
            LearningOpportunity.TEXT_BOOST_FI_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.NAME_AUTO_FI
    );
    
    public static final List<String> FIELDS_SV = Lists.newArrayList(
            LearningOpportunity.TEXT_SV,
            LearningOpportunity.TEXT_SV_WHOLE,
            LearningOpportunity.TEXT_BOOST_SV,
            LearningOpportunity.TEXT_BOOST_SV_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.NAME_AUTO_SV
    );
    
    public static final List<String> FIELDS_EN = Lists.newArrayList(
            LearningOpportunity.TEXT_EN,
            LearningOpportunity.TEXT_EN_WHOLE,
            LearningOpportunity.TEXT_BOOST_EN,
            LearningOpportunity.TEXT_BOOST_EN_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.NAME_AUTO_EN
    );

    private static final Integer AS_COUNT = 10;
    public static final String APP_STATUS = "appStatus";
    public static final String APP_STATUS_ONGOING = "ongoing";
    public static final String APP_STATUS_UPCOMING = "upcoming";
    public static final String APP_STATUS_UPCOMING_LATER = "upcomingLater";
    public static final String QUOTED_QUERY_FORMAT = "%s:\"%s\"";

    public LearningOpportunityQuery(String term, String prerequisite,
            List<String> cities, List<String> facetFilters, List<String> articleFilters,  String lang, 
            boolean ongoing, boolean upcoming, boolean upcomingLater,
            int start, int rows, String sort, String order, 
            String lopFilter, String educationCodeFilter, List<String> excludes, 
            SearchType searchType, String upcomingDate, String upcomingLaterDate) {
        super(term);
        LOG.debug(String.format("Query term: (%s)", term));
        
        if (prerequisite != null && SearchType.LO.equals(searchType)) {
            this.addFilterQuery(String.format("%s:%s", LearningOpportunity.PREREQUISITES, prerequisite));
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty() && SearchType.LO.equals(searchType)) {
            this.addFilterQuery(
                    String.format("%s:(\"%s\")", LearningOpportunity.LOP_HOMEPLACE, Joiner.on("\" OR \"").join(cities))
                    );
        }
        
        if (SearchType.LO.equals(searchType)) {
            setApplicationStatusFilters(ongoing, upcoming, upcomingLater, upcomingDate, upcomingLaterDate);
        }
        
        if (lopFilter != null) {
            addLopRecommendationFilter(lopFilter, lang);
        }
        
        if (educationCodeFilter != null) {
            addEducationCodeRecommendationFilter(educationCodeFilter, lang);
        }
        
        if (excludes != null) {
            for (String curExclude : excludes) {
                String[] parts = curExclude.split(":");
                if (parts.length == 2) {
                    this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, parts[0], parts[1]));
                }
            }
        }
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        if (!SearchType.ARTICLE.equals(searchType)) {
            this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_ARTICLE));
        } else {
            this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_ARTICLE));
        }
        
        if (SearchType.LO.equals(searchType)) {
            addFacetsToQuery(facetFilters);
        } else if (SearchType.ARTICLE.equals(searchType)) {
            this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase()));
            this.setFacet(true);
            this.addFacetField(LearningOpportunity.ARTICLE_CONTENT_TYPE);
            this.setFacetSort("index");
            this.setFacetMinCount(1);
            for (String curFilter : articleFilters) {
                this.addFilterQuery(curFilter);
            }
        }
        
        this.setParam("defType", "edismax");
        
        setSearchFields(facetFilters);
        
        this.setParam("q.op", "AND");
        if (sort != null) {
            this.addSort(sort, order.equals("asc") ? ORDER.asc : ORDER.desc);
        }
    }
    


    private void setApplicationStatusFilters(boolean ongoing, boolean upcoming, boolean upcomingLater, String upcomingLimit, String upcomingLaterLimit) {
        StringBuilder ongoingFQ = new StringBuilder();
        for (int i = 0; i < AS_COUNT; i++) {
            ongoingFQ.append(String.format("(asStart_%d:[* TO NOW] AND asEnd_%d:[NOW TO *])", i, i));
            if (i != AS_COUNT-1) {
                ongoingFQ.append(" OR ");
            }
        }
        if (ongoing) {
            this.addFilterQuery(ongoingFQ.toString());
        }
        
        StringBuilder upcomingFQ = new StringBuilder();
        for (int i = 0; i < AS_COUNT; i++) {
            upcomingFQ.append(String.format("(asStart_%d:[NOW TO %s])", i, upcomingLimit));
            if (i != AS_COUNT-1) {
                upcomingFQ.append(" OR ");
            }
        }
        if (upcoming) {
            this.addFilterQuery(upcomingFQ.toString());
        }
        
        
        
        StringBuilder upcomingLaterFQ = new StringBuilder();
        for (int i = 0; i < AS_COUNT; i++) {
            upcomingLaterFQ.append(String.format("(asStart_%d:[NOW TO %s])", i, upcomingLaterLimit));
            if (i != AS_COUNT-1) {
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



    private void setSearchFields(List<String> facetFilters) {
        
        List<String> teachingLangs = getTeachingLangs(facetFilters);
        
        List<String> searchFields = new ArrayList<String>();
        
        if (teachingLangs.contains("fi")) {
            searchFields.addAll(FIELDS_FI);
        } 
        
        if (teachingLangs.contains("sv")) {
            searchFields.addAll(FIELDS_SV);
        } 
        
        if (teachingLangs.contains("en")) {
            searchFields.addAll(FIELDS_EN);
        } 
        
        if (searchFields.isEmpty() 
                && !teachingLangs.isEmpty()) {
            searchFields.addAll(FIELDS_FI);
        }
        
        if (searchFields.isEmpty()){
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
        } else {
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(searchFields));
        }
        
    }



    private List<String> getTeachingLangs(List<String> facetFilters) {
        List<String> teachinglangs = new ArrayList<String>();
        for (String curFilt : facetFilters) {
            if (curFilt.startsWith(LearningOpportunity.TEACHING_LANGUAGE)) {
                String theLang = curFilt.substring(curFilt.length() - 2).toLowerCase();
                teachinglangs.add(theLang);
            }
        }
        return teachinglangs;
    }



    private void addFacetsToQuery(List<String> facetFilters) {
        this.setFacet(true);
        this.addFacetField(LearningOpportunity.TEACHING_LANGUAGE);
        this.addFacetField(LearningOpportunity.EDUCATION_TYPE);
        this.addFacetField(LearningOpportunity.PREREQUISITES);
        this.addFacetField(LearningOpportunity.TOPIC);
        this.addFacetField(LearningOpportunity.THEME);
        this.setFacetSort("index");
        
        for (String curFilter : facetFilters) {
            this.addFilterQuery(curFilter);
        }
        
    }

    private void addLopRecommendationFilter(String lopFilter,
            String lang) {
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_FI, lopFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_SV, lopFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME_DISPLAY_EN, lopFilter));
        } else {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.LOP_NAME, lopFilter));
        }
    }
    
    private void addEducationCodeRecommendationFilter(String educationCodeFilter,
            String lang) { 
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, educationCodeFilter));
        } else {
            this.addFilterQuery(String.format(QUOTED_QUERY_FORMAT, LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        }
        
        
       
    }
}
