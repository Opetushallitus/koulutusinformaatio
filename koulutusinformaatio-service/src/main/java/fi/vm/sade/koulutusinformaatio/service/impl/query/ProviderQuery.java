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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Solr query for querying learning opportunity providers.
 *
 * @author Hannu Lyytikainen
 */
public class ProviderQuery extends SolrQuery {

    private final static String BASE_EDUCATIONS = "requiredBaseEducations";
    private final static String AS_IDS = "asIds";
    private final static String NAME_FI = "name_fi";
    private final static String NAME_SV = "name_sv";
    private final static String NAME_FI_STR = "name_fi_str";
    private final static String NAME_SV_STR = "name_sv_str";
    private final static String NEG_VOCATIONAL = "-vocationalAsIds";
    private final static String NEG_NON_VOCATIONAL = "-nonVocationalAsIds";

    public ProviderQuery(String q, String asId, String baseEducation, int start, int rows, boolean vocational,
                         boolean nonVocational, String lang, boolean prefix) {
        super(Joiner.on(":").join(resolveNameField(lang, prefix), ClientUtils.escapeQueryChars(q) + "*"));
        
        this.setStart(start);
        this.setRows(rows);
        this.setSort(resolveNameField(lang, prefix), ORDER.asc);

        if (asId != null) {
            this.addFilterQuery(Joiner.on(":").join(AS_IDS, asId));

            if (!vocational) {
                this.addFilterQuery(Joiner.on(":").join(NEG_VOCATIONAL, asId));
            }
            if (!nonVocational) {
                this.addFilterQuery(Joiner.on(":").join(NEG_NON_VOCATIONAL, asId));
            }
        }
        if (baseEducation != null) {
            this.addFilterQuery(Joiner.on(":").join(BASE_EDUCATIONS, baseEducation));
        }
    }

    private static String resolveNameField(String lang, boolean prefix) {
        if (lang.equalsIgnoreCase("sv") && !prefix) {
            return NAME_SV;
        } else if (!prefix) {
            return NAME_FI;
        } else if (lang.equals("sv") && prefix) {
            return NAME_SV_STR;
        } else {
            return NAME_FI_STR;
        }
    }
}
