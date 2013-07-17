package fi.vm.sade.koulutusinformaatio.service.impl.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.DisMaxParams;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQuery extends SolrQuery {

    private final static String TEXT  = "text";
    private final static String TEXT_BOOST  = "textBoost";
    private final static String LOP_CITY  = "lopCity";
    private final static String PREREQUISITES  = "prerequisites";
    private final static String DISMAX = new StringBuilder(TEXT).append(" ").append(TEXT_BOOST).append("^5").toString();

    public LearningOpportunityQuery(String term, String prerequisite,
                                    List<String> cities, int start, int rows) {
        super(new StringBuilder().append(TEXT).append(":(").append(term).append(")").append(" OR ")
                .append(TEXT_BOOST).append(":(").append(term).append(")").toString());

        if (prerequisite != null) {
        this.addFilterQuery(new StringBuilder(PREREQUISITES).append(":").append(prerequisite).toString());
        }
        this.setStart(start);
        this.setRows(rows);
        if (cities != null && !cities.isEmpty()) {

            StringBuilder fq = new StringBuilder(LOP_CITY).append(":(");

            for (int i = 0; i < cities.size(); i++) {
                if (i < cities.size() -1) {
                    fq.append(cities.get(i)).append(" OR ");
                }
                else {
                    fq.append(cities.get(i));
                }
            }
            fq.append(")");
            this.addFilterQuery(fq.toString());
        }
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, DISMAX);
    }
}
