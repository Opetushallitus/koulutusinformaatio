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
            "textBoost2_fi^15.0",
            "textBoost2_sv^15.0",
            "textBoost2_en^15.0",
            "asNames",
            "lopNames"
    );
    private final static String LOP_HOMEPLACE = "lopHomeplace";
    private final static String PREREQUISITES = "prerequisites";
    private final static String APPLICATION_SYSTEMS = "applicationSystems";

    public LearningOpportunityQuery(String term, String prerequisite,
                                    List<String> cities, String applicationSystemId, int start, int rows) {
        super(term);
        if (prerequisite != null) {
            this.addFilterQuery(new StringBuilder(PREREQUISITES).append(":").append(prerequisite).toString());
        }
        if (cities != null && !cities.isEmpty()) {
            StringBuilder fq = new StringBuilder(LOP_HOMEPLACE).append(":(")
                    .append(Joiner.on(" OR ").join(cities))
                    .append(")");;
            this.addFilterQuery(fq.toString());
        }
        if (applicationSystemId != null) {
            this.addFilterQuery(new StringBuilder(APPLICATION_SYSTEMS).append(":").append(applicationSystemId).toString());
        }
        this.setStart(start);
        this.setRows(rows);
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
    }
}
