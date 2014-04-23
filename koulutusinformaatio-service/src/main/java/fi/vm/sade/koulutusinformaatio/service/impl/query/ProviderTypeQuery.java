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

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderTypeQuery extends SolrQuery {

    public ProviderTypeQuery(String firstCharacter, String lang) {
        super(String.format("%s:%s", resolveStartsWithField(lang), firstCharacter));
        this.setParam(GroupParams.GROUP, true);
        this.setParam(GroupParams.GROUP_FIELD, SolrUtil.ProviderFields.TYPE_VALUE);
        this.setParam(GroupParams.GROUP_LIMIT, "1");
        this.setRows(Integer.MAX_VALUE);
        this.setSort(SolrUtil.ProviderFields.TYPE_VALUE, ORDER.asc);
    }

    private static String resolveStartsWithField(String lang) {
        if (lang.equalsIgnoreCase("sv")) {
            return SolrUtil.ProviderFields.STARTS_WITH_SV;
        }
        else {
            return SolrUtil.ProviderFields.STARTS_WITH_FI;
        }
    }
}
