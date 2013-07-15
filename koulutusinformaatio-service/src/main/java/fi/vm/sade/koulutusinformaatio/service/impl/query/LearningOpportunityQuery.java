package fi.vm.sade.koulutusinformaatio.service.impl.query;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityQuery extends SolrQuery {

    private final static String TEXT  = "text";
    private final static String LOP_CITY  = "lopCity";

    public LearningOpportunityQuery(String term, String prerequisite,
                                    List<String> cities, int start, int rows) {
        super(new StringBuilder().append('+').append(TEXT).append(":(").append(term).append(")").toString());
        this.addFilterQuery("prerequisites" + ":" + prerequisite);
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


    }
}
