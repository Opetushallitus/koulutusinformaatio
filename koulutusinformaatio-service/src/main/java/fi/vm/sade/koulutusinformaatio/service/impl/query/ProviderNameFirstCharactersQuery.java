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

import com.google.common.base.Joiner;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderNameFirstCharactersQuery extends SolrQuery {

    public ProviderNameFirstCharactersQuery(String lang) {
        super(Joiner.on(":").join(resolveStartsWithFieldName(lang), "*"));
        this.setParam(GroupParams.GROUP, true);
        this.setParam(GroupParams.GROUP_FIELD, resolveStartsWithFieldName(lang));
        this.setParam(GroupParams.GROUP_LIMIT, "0");
        this.setRows(Integer.MAX_VALUE);
    }

    private static String resolveStartsWithFieldName(String lang) {
        if (lang.equalsIgnoreCase("en")) {
            return SolrUtil.ProviderFields.STARTS_WITH_EN;
        } else if (lang.equalsIgnoreCase("sv")) {
            return SolrUtil.ProviderFields.STARTS_WITH_SV;
        } else {
            return SolrUtil.ProviderFields.STARTS_WITH_FI;
        }
    }
}
