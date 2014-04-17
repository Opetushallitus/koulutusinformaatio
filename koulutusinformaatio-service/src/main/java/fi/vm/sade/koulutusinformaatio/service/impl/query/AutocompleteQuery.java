/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.impl.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.DisMaxParams;

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;


/**
 * 
 * @author Markus
 */
public class AutocompleteQuery extends SolrQuery {
    
    private static final long serialVersionUID = 2017117732915843350L;

    /*
     * For querying suggested terms (autocomplete)
     */
    public AutocompleteQuery(String term, String lang) {
        super("*");
        this.setRows(0);
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase()));
        
        addSuggestedTermsFacetToQuery(term, lang);
        
        this.setParam("defType", "edismax");
        
        if ("fi".equalsIgnoreCase(lang)) {
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(SolrUtil.FIELDS_FI));
        } else if ("sv".equalsIgnoreCase(lang)) {
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(SolrUtil.FIELDS_SV));
        } else if ("en".equalsIgnoreCase(lang)) {
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(SolrUtil.FIELDS_EN));
        } else {
            this.setParam(DisMaxParams.QF, Joiner.on(" ").join(SolrUtil.FIELDS));
        }
        
        this.setParam("q.op", "AND");
        
    }
    
    private void addSuggestedTermsFacetToQuery(String term, String lang) {
        this.setFacet(true);
        if (term != null) {
            this.setFacetPrefix(term.toLowerCase());
        }
        this.addFacetField(String.format("%s_%s", LearningOpportunity.NAME_AUTO, lang.toLowerCase()));
        this.addFacetField(String.format("%s_%s", LearningOpportunity.FREE_AUTO, lang.toLowerCase()));
        this.setFacetMinCount(1);
        this.setFacetLimit(5);
    }
    
}
