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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.solr.client.solrj.SolrQuery;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

/**
 * 
 * @author Markus
 *
 */
public class ApplicationSystemQuery extends SolrQuery {

    private static final long serialVersionUID = -6959594348300284342L;

    public ApplicationSystemQuery() {
        super("*");
        
        this.addFilterQuery(String.format("%s:%s", LearningOpportunity.TYPE,  SolrConstants.TYPE_APPLICATION_SYSTEM));
        this.setApplicationStatusFilters();
        
        this.setParam("q.op", "AND");
        this.addSort(LearningOpportunity.NAME_SORT, ORDER.asc);
    }
    
    public ApplicationSystemQuery(String targetGroup) {
        this();
        
        this.addFilterQuery(String.format("%s:%s", SolrUtil.LearningOpportunity.AS_TARGET_GROUP_CODE, targetGroup));
    }
    
    private String getDateLimitStr() {
        Calendar limit = Calendar.getInstance();
        
        limit.add(Calendar.MONTH, 6);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return dateFormat.format(limit.getTime());

    }
    
    private void setApplicationStatusFilters() {
        String sixMonthsFromNow = getDateLimitStr();
        StringBuilder ongoingFQ = new StringBuilder();
        for (int i = 0; i < SolrUtil.AS_COUNT; i++) {
            ongoingFQ.append(String.format("(asStart_%d:[* TO %s] AND asEnd_%d:[NOW TO *])", i, sixMonthsFromNow, i));
            ongoingFQ.append(String.format("OR (asStart_%d:[* TO %s] AND -asEnd_%d:[* TO *])", i, sixMonthsFromNow, i)); // jatkuvalla haulla ei välttämättä ole päättymisaikaa
            if (i != SolrUtil.AS_COUNT-1) {
                ongoingFQ.append(" OR ");
            }
        }
        this.addFilterQuery(ongoingFQ.toString());
        
    }

}
