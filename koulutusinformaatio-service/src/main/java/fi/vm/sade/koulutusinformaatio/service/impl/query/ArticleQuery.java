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

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

/**
 * 
 * @author Markus
 */
public class ArticleQuery extends SolrQuery {

    private static final long serialVersionUID = 8184490200577042042L;

    public ArticleQuery(String filterQuery, String lang) {
        super("*");
        this.addFilterQuery(filterQuery);
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.ARTICLE_LANG, lang));
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TYPE,  SolrConstants.TYPE_ARTICLE));
        
        this.setParam("q.op", "AND");
        this.addSort(LearningOpportunity.NAME_SORT, ORDER.asc);
    }
    
    public ArticleQuery(String term, String lang,
                        int start, int rows, String sort, String order,
                        List<String> facetFilters, List<String> articleFilters) {
        super(term);
        
        this.setStart(start);
        this.setRows(rows);
        
        //leaving the facet and timestamp docs out
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.ID, SolrConstants.TIMESTAMP_DOC));
        this.addFilterQuery(String.format("-%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_FACET));
        
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TYPE, SolrConstants.TYPE_ARTICLE));
        
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase()));

        addFacetsToQuery(articleFilters);
        
        SolrUtil.setSearchFields(facetFilters, this);
        
        this.setParam("defType", "edismax");
        
        this.setParam("q.op", "AND");
        if (sort != null) {
            this.addSort(sort, order.equals("asc") ? ORDER.asc : ORDER.desc);
        }   
    }
    
    private void addFacetsToQuery(List<String> articleFilters) {
        this.setFacet(true);
        this.addFacetField(LearningOpportunity.ARTICLE_CONTENT_TYPE);
        this.setFacetSort("index");
        this.setFacetMinCount(1);
        for (String curFilter : articleFilters) {
            this.addFilterQuery(curFilter);
        }
    }


}
