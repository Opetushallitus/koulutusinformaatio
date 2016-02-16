package fi.vm.sade.koulutusinformaatio.service.impl.query;

import com.google.common.base.Joiner;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity.*;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQuery extends SolrQuery {

    private static final long serialVersionUID = -4340177833703968140L;
    
    private static final Logger LOG = LoggerFactory.getLogger(LearningOpportunityQuery.class);


    public LearningOpportunityQuery(String term, String prerequisite,
            List<String> cities, List<String> facetFilters, String lang, 
            boolean ongoing, boolean upcoming, boolean upcomingLater,
            int start, int rows, String sort, String order, 
            String lopFilter, String educationCodeFilter, List<String> excludes, String upcomingDate, String upcomingLaterDate, String asId) {
        super(term);
        LOG.debug(String.format("Query term: (%s)", term));
        
        if (prerequisite != null) {
            this.addFilterQuery(String.format("%s:%s", PREREQUISITES, prerequisite));
        }
        if (asId != null) {
            this.addFilterQuery(String.format("%s:%s", AS_ID, asId));
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty()) {
            this.addFilterQuery(
                    String.format("%s:(\"%s\")", LOP_HOMEPLACE, Joiner.on("\" OR \"").join(cities))
                    );
        }
        
        setApplicationStatusFilters(ongoing, upcoming, upcomingLater, upcomingDate, upcomingLaterDate);

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
                    this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, parts[0], parts[1]));
                }
            }
        }
        
        //leaving the faces, timestamps and application systems docs out
        this.addFilterQuery(String.format("-%s:%s", ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", TYPE, SolrConstants.TYPE_FACET));
        this.addFilterQuery(String.format("-%s:%s", TYPE, SolrConstants.TYPE_ARTICLE));
        this.addFilterQuery(String.format("-%s:%s", TYPE, SolrConstants.TYPE_APPLICATION_SYSTEM));
        
        
        addFacetsToQuery(facetFilters);
        
        this.setParam("defType", "edismax");
        
        SolrUtil.setSearchFields(facetFilters, this);
        
        this.setParam("q.op", "AND");
        if (sort != null && "fi".equals(lang)) {
            this.addSort(NAME_FI_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null && "sv".equals(lang)) {
            this.addSort(NAME_SV_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null && "en".equals(lang)) {
            this.addSort(NAME_EN_SORT, order.equals("asc") ? ORDER.asc : ORDER.desc);
        } else if (sort != null) {
            this.addSort(sort, order.equals("asc") ? ORDER.asc : ORDER.desc);
        }
    }

    private void setApplicationStatusFilters(boolean ongoing, boolean upcoming, boolean upcomingLater, String upcomingLimit, String upcomingLaterLimit) {
        StringBuilder ongoingFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            ongoingFQ.append(String.format("(asStart_%d:[* TO NOW] AND asEnd_%d:[NOW TO *])", i, i));
            ongoingFQ.append(String.format("OR (asStart_%d:[* TO NOW] AND -asEnd_%d:[* TO *])", i, i)); // jatkuvalla haulla ei välttämättä ole päättymisaikaa
            if (i != SolrUtil.AS_COUNT - 1) {
                ongoingFQ.append(" OR ");
            }
        }
        StringBuilder upcomingFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            upcomingFQ.append(String.format("(asStart_%d:[NOW TO %s])", i, upcomingLimit));
            if (i != SolrUtil.AS_COUNT - 1) {
                upcomingFQ.append(" OR ");
            }
        }

        StringBuilder upcomingLaterFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            upcomingLaterFQ.append(String.format("(asStart_%d:[%s TO %s])", i, upcomingLimit, upcomingLaterLimit));
            if (i != SolrUtil.AS_COUNT - 1) {
                upcomingLaterFQ.append(" OR ");
            }
        }

        if (ongoing)
            this.addFilterQuery(ongoingFQ.toString());
        if (upcoming)
            this.addFilterQuery(upcomingFQ.toString());
        if (upcomingLater)
            this.addFilterQuery(upcomingLaterFQ.toString());

        this.addFacetQuery(ongoingFQ.toString());
        this.addFacetQuery(upcomingFQ.toString());
        this.addFacetQuery(upcomingLaterFQ.toString());

    }

    private void addFacetsToQuery(List<String> facetFilters) {
        this.setFacet(true);
        this.setFacetLimit(-1);
        this.addFacetField(TEACHING_LANGUAGE);
        this.addFacetField(EDUCATION_TYPE);
        this.addFacetField(PREREQUISITES);
        this.addFacetField(TOPIC);
        this.addFacetField(THEME);
        this.addFacetField(FORM_OF_TEACHING);
        this.addFacetField(TIME_OF_TEACHING);
        this.addFacetField(FORM_OF_STUDY);
        this.addFacetField(AS_FACET);
        this.addFacetField(SIIRTOHAKU);
        this.setFacetSort("index");
        this.setParam("f." + PREREQUISITES + ".facet.missing", true);
        
        for (String curFilter : facetFilters) {
            if (curFilter.startsWith(PREREQUISITES + ":")) {
                // Prerequisite should match or be empty
                this.addFilterQuery("-(-" + curFilter + " AND " + PREREQUISITES + ":[* TO *])");
            } else {
                this.addFilterQuery(curFilter);
            }
        }
        
    }

    private void addLopRecommendationFilter(String lopFilter,
            String lang) {
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LOP_NAME_DISPLAY_FI, lopFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LOP_NAME_DISPLAY_SV, lopFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LOP_NAME_DISPLAY_EN, lopFilter));
        } else {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, LOP_NAME, lopFilter));
        }
    }
    
    private void addEducationCodeRecommendationFilter(String educationCodeFilter,
            String lang) { 
        
        if (lang.equalsIgnoreCase("fi")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("sv")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, EDUCATION_CODE_DISPLAY_SV, educationCodeFilter));
        } else if (lang.equalsIgnoreCase("en")) {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, EDUCATION_CODE_DISPLAY_EN, educationCodeFilter));
        } else {
            this.addFilterQuery(String.format(SolrUtil.QUOTED_QUERY_FORMAT, EDUCATION_CODE_DISPLAY_FI, educationCodeFilter));
        }
    }
    
}
