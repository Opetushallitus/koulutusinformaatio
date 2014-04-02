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

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class LocationQuery extends SolrQuery {

	private static final long serialVersionUID = -4254747245152049594L;
	
	private final static String NAME = "name_auto";
    private final static String LANG = "lang";
    private final static String CODE = "code";
    

    public LocationQuery(final String q, final String lang) {
        super(Joiner.on(":").join(NAME, q));
        if (lang != null && !lang.isEmpty()) {
        	this.addFilterQuery(Joiner.on(":").join(LANG, lang));
        }
    }

    public LocationQuery(final List<String> codes, final String lang) {
        super(String.format("%s:(%s)", CODE, Joiner.on(" OR ").join(codes)));
        if (lang != null)
        this.addFilterQuery(Joiner.on(":").join(LANG, lang));
        this.setRows(1000);
    }
    
    public LocationQuery(String field, String value, final String lang) {
        super(String.format("%s:%s", field, value));
        this.addFilterQuery(String.format("%s:%s", LANG, lang));
        //Filter out the unknown district
        this.addFilterQuery(String.format("-%s:%s%s", LocationFields.ID, lang, SolrConstants.DISTRICT_UNKNOWN));
        this.addFilterQuery(String.format("-%s:%s%s", LocationFields.ID, lang, SolrConstants.MUNICIPALITY_UNKNOWN));
        this.setRows(1000);
    }
    
    public LocationQuery(String field, List<String> values, final String lang) {
        super(String.format("%s:(%s)", field, Joiner.on(" OR ").join(values)));
        this.addFilterQuery(String.format("%s:%s", LANG, lang));
        this.setRows(1000);
    }
}
