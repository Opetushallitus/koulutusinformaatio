package fi.vm.sade.koulutusinformaatio.service.impl.query;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.SolrConstants;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.DisMaxParams;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQuery extends SolrQuery {

    private final static List<String> FIELDS = Lists.newArrayList(
            "text_fi",
            "text_sv",
            "text_en",
            "text_fi_whole",
            "text_sv_whole",
            "text_en_whole",
            "textBoost_fi^10.0",
            "textBoost_sv^10.0",
            "textBoost_en^10.0",
            "textBoost_fi_whole^10.0",
            "textBoost_sv_whole^10.0",
            "textBoost_en_whole^10.0",
            "asNames",
            "lopNames"
    );

    private final static Integer AS_COUNT = 10;
    public final static String APP_STATUS = "appStatus";
    public final static String APP_STATUS_ONGOING = "ongoing";
    public final static String APP_STATUS_UPCOMING = "upcoming";

    public LearningOpportunityQuery(String term, String prerequisite,
            List<String> cities, List<String> facetFilters, String lang, boolean ongoing, boolean upcoming, int start, int rows, String sort, String order) {
        super(term);
        if (prerequisite != null) {
            this.addFilterQuery(String.format("%s:%s", LearningOpportunity.PREREQUISITES, prerequisite));
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty()) {
            this.addFilterQuery(
                    String.format("%s:(%s)", LearningOpportunity.LOP_HOMEPLACE, Joiner.on(" OR ").join(cities))
                    );
        }
        
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
            upcomingFQ.append(String.format("(asStart_%d:[NOW TO *])", i, i));
            if (i != AS_COUNT-1) {
                upcomingFQ.append(" OR ");
            }
        }
        if (upcoming) {
            this.addFilterQuery(upcomingFQ.toString());
        }
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        
        addFacetsToQuery(lang, facetFilters, ongoingFQ.toString(), upcomingFQ.toString());
        
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
        this.setParam("q.op", "AND");
        if (sort != null) {
            this.addSort(sort, order.equals("asc") ? ORDER.asc : ORDER.desc);
        }
    }
    
    /*
     * For querying suggested terms (autocomplete)
     */
    public LearningOpportunityQuery(String term, String lang) {
        super("*");
        this.setRows(0);
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        
        addSuggestedTermsFacetToQuery(term);
        
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
        this.setParam("q.op", "AND");
        
    }
    
    private void addSuggestedTermsFacetToQuery(String term) {
        this.setFacet(true);
        this.setFacetPrefix(term);
        this.addFacetField(LearningOpportunity.NAME_AUTO);
        this.setFacetMinCount(1);
    }

    private void addFacetsToQuery(String lang, List<String> facetFilters, String ongoingFQ, String upcomingFQ) {
        this.setFacet(true);
        this.addFacetField(LearningOpportunity.TEACHING_LANGUAGE);
        this.addFacetField(LearningOpportunity.EDUCATION_TYPE);
        this.addFacetField(LearningOpportunity.PREREQUISITES);
        
        this.addFacetQuery(ongoingFQ);
        this.addFacetQuery(upcomingFQ);
        for (String curFilter : facetFilters) {
            this.addFilterQuery(curFilter);
        }
    }
}
