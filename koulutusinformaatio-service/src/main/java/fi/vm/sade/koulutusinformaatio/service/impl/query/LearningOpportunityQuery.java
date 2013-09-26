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
            "textBoost_fi^10.0",
            "textBoost_sv^10.0",
            "textBoost_en^10.0",
            "asNames",
            "lopNames"
    );

    private final static Integer AS_COUNT = 10;

    private final static String LOP_HOMEPLACE = "lopHomeplace";
    private final static String PREREQUISITES = "prerequisites";

    public LearningOpportunityQuery(String term, String prerequisite,
                                    List<String> cities, boolean ongoing, int start, int rows) {
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
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
    }
}
