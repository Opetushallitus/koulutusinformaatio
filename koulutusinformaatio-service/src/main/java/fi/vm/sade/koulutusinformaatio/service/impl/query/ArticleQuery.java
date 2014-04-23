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

}
