package fi.vm.sade.koulutusinformaatio.service.impl.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.DisMaxParams;

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.SolrConstants;

public class AutocompleteQuery extends SolrQuery {
    
    /*
     * For querying suggested terms (autocomplete)
     */
    public AutocompleteQuery(String term, String lang) {
        super("*");
        this.setRows(0);
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        
        addSuggestedTermsFacetToQuery(term, lang);
        
        this.setParam("defType", "edismax");
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(LearningOpportunityQuery.FIELDS));
        this.setParam("q.op", "AND");
        
    }
    
    private void addSuggestedTermsFacetToQuery(String term, String lang) {
        this.setFacet(true);
        if (term != null) {
            this.setFacetPrefix(term.toLowerCase());
        }
        this.addFacetField(LearningOpportunity.NAME_AUTO);
        this.addFacetField(String.format("%s_%s", LearningOpportunity.FREE_AUTO, lang.toLowerCase()));
        this.setFacetMinCount(1);
        this.setFacetLimit(5);
    }
    
}
