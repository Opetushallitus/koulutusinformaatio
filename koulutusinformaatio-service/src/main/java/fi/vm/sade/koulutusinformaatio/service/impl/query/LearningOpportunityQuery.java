package fi.vm.sade.koulutusinformaatio.service.impl.query;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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

    private final static String LOP_HOMEPLACE = "lopHomeplace";
    private final static String PREREQUISITES = "prerequisites";
    private final static String ID = "id";
    private final static String TIMESTAMP_DOC = "loUpdateTimestampDocument";
    private final static String TYPE = "type";
    private final static String TYPE_FACET = "FASETTI";
    public final static String TEACHING_LANG = "teachingLangCode_ffm";

    public LearningOpportunityQuery(String term, String prerequisite,
                                    List<String> cities, List<String> facetFilters, String lang, boolean ongoing, int start, int rows) {
        super(term);
        if (prerequisite != null) {
            this.addFilterQuery(String.format("%s:%s", PREREQUISITES, prerequisite));
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty()) {
            this.addFilterQuery(
                    String.format("%s:(%s)", LOP_HOMEPLACE, Joiner.on(" OR ").join(cities))
            );
        }
        if (ongoing) {
            StringBuilder ongoingFQ = new StringBuilder();
            for (int i = 0; i < AS_COUNT; i++) {
                ongoingFQ.append(String.format("(asStart_%d:[* TO NOW] AND asEnd_%d:[NOW TO *])", i, i));
                if (i != AS_COUNT-1) {
                    ongoingFQ.append(" OR ");
                }
            }
            this.addFilterQuery(ongoingFQ.toString());
        }
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", ID, TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", TYPE, TYPE_FACET));
        
        addFacetsToQuery(lang, facetFilters);
      
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
        this.setParam("q.op", "AND");
    }

    private void addFacetsToQuery(String lang, List<String> facetFilters) {
        this.setFacet(true);
        this.addFacetField(TEACHING_LANG);
        for (String curFilter : facetFilters) {
            this.addFilterQuery(curFilter);
        }
    }
}
