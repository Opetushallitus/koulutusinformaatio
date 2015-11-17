/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.domain.Provider;

/**
 * Solr query for querying learning opportunity providers.
 *
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionQuery extends SolrQuery {

    public ApplicationOptionQuery(String asId, List<Provider> learningOpportunityProviders, List<String> baseEducations) {
        super(String.format("%s:%s", SolrUtil.AoFields.TYPE, SolrUtil.TYPE_APPLICATIONOPTION));

        this.setStart(0);
        this.setRows(Integer.MAX_VALUE);

        if (asId != null) {
            this.addFilterQuery(String.format("%s:%s", SolrUtil.AoFields.AS_ID, asId));
        }
        if (learningOpportunityProviders != null) {
            this.addFilterQuery(String.format("%s:(\"%s\")", SolrUtil.AoFields.LOP_ID, Joiner.on("\" OR \"")
                    .join(getProviderOids(learningOpportunityProviders))));
        }
        if (baseEducations != null && !baseEducations.isEmpty()) {
            this.addFilterQuery(String.format("%s:(\"%s\")", SolrUtil.AoFields.PREREQUISITES, Joiner.on("\" OR \"").join(baseEducations)));
        }
        StringBuilder ongoingFQ = new StringBuilder();
        ongoingFQ.append(String.format("(%s:[* TO NOW] AND %s:[NOW TO *])", SolrUtil.AoFields.START_DATE, SolrUtil.AoFields.END_DATE));
        ongoingFQ.append(String.format("OR (%s:[* TO NOW] AND -%s:[* TO *])", SolrUtil.AoFields.START_DATE, SolrUtil.AoFields.END_DATE)); // jatkuvalla haulla ei välttämättä ole päättymisaikaa
        this.addFilterQuery(ongoingFQ.toString());
    }

    private List<String> getProviderOids(List<Provider> learningOpportunityProviders) {
        return Lists.transform(learningOpportunityProviders, new Function<Provider, String>() {
            @Override
            public String apply(Provider input) {
                return input.getId();
            }
        });
    }
}
