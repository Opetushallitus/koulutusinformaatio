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
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.DisMaxParams;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;

/**
 * Solr query for querying learning opportunity providers.
 *
 * @author Hannu Lyytikainen
 */
public class ProviderQuery extends SolrQuery {
    
    private static final List<String> FIELDS = Lists.newArrayList(
            LearningOpportunity.NAME_EN
    );

    private final static String BASE_EDUCATIONS = "requiredBaseEducations";
    private final static String AS_IDS = "asIds";
    private final static String NAME_FI = "name_fi";
    private final static String NAME_SV = "name_sv";
    private final static String NAME_EN = "name_en";
    private final static String NAME_FI_STR = "name_fi_str";
    private final static String NAME_SV_STR = "name_sv_str";
    private final static String NAME_EN_STR = "name_en_str";
    private final static String NEG_VOCATIONAL = "-vocationalAsIds";
    private final static String NEG_NON_VOCATIONAL = "-nonVocationalAsIds";

    private static String formatSearchTerm(String q, String lang, boolean prefix){
        if(q.isEmpty()){
            q = q + "*";
        } else {
            q = "(" + Joiner.on("* AND ").join(q.split(" ")) + "*)";
        }
        return Joiner.on(":").join(resolveNameField(lang, prefix), q);
    }

    public ProviderQuery(String q, String asId, List<String> baseEducations, int start, int rows, boolean vocational,
                         boolean nonVocational, String lang, boolean prefix, String type) {
        super(formatSearchTerm(q, lang, prefix));

        this.setStart(start);
        this.setRows(rows);
        this.setSort(resolveNameField(lang, prefix), ORDER.asc);

        this.addFilterQuery(String.format("type:%s", SolrUtil.TYPE_ORGANISATION));
        if (asId != null) {
            this.addFilterQuery(Joiner.on(":").join(AS_IDS, asId));

            if (!vocational) {
                this.addFilterQuery(Joiner.on(":").join(NEG_VOCATIONAL, asId));
            }
            if (!nonVocational) {
                this.addFilterQuery(Joiner.on(":").join(NEG_NON_VOCATIONAL, asId));
            }
        }
        if (baseEducations != null && !baseEducations.isEmpty()) {
            //this.addFilterQuery(Joiner.on(":").join(BASE_EDUCATIONS, baseEducation));
            this.addFilterQuery(
                    String.format("%s:(\"%s\")", BASE_EDUCATIONS, Joiner.on("\" OR \"").join(baseEducations)));
        }
        if (type != null) {
            this.addFilterQuery(Joiner.on(":").join(SolrUtil.ProviderFields.TYPE_VALUE, type));
        }
    }
    
    public ProviderQuery(String q, String lang, List<String> facetFilters, List<String> cities, int start, int rows, String sort, String order) {
        super(preprocess(q, lang));
        this.addFilterQuery("type:ORGANISAATIO");
        for (String curFilter : facetFilters) {
            this.addFilterQuery(curFilter);
        }
        
        if (cities != null && !cities.isEmpty()) {
            this.addFilterQuery(
                    String.format("%s:(\"%s\")", LearningOpportunity.LOP_HOMEPLACE, Joiner.on("\" OR \"").join(cities))
                    );
        }
        
        this.setStart(start);
        this.setRows(rows);
        
        this.setParam("q.op", "AND");
        
        
        if (sort != null) {
            this.addSort(resolveNameField(lang, true), order.equals("asc") ? ORDER.asc : ORDER.desc);
        }
        this.setParam(DisMaxParams.QF, Joiner.on(" ").join(FIELDS));
        this.setFacet(true);
        this.addFacetField("oltype_ffm");
    }

    private static String preprocess(String q, String lang) {
        if ("".equals(q)) {
            return q;
        }
        if ("*".equals(q)) {
            return Joiner.on(":").join(resolveNameField(lang, false), q);
        } else {
            return String.format("%s OR %s OR %s", Joiner.on(":").join(resolveNameField("fi", false), ClientUtils.escapeQueryChars(q) + '*'),
                    Joiner.on(":").join(resolveNameField("sv", false), ClientUtils.escapeQueryChars(q) + '*'),
                    Joiner.on(":").join(resolveNameField("en", false), ClientUtils.escapeQueryChars(q) + '*'));
        }
    }

    private static String resolveNameField(String lang, boolean prefix) {
        if (lang.equalsIgnoreCase("en") && !prefix) {
            return NAME_EN;
        } else if (lang.equalsIgnoreCase("sv") && !prefix) {
            return NAME_SV;
        } else if (!prefix) {
            return NAME_FI;
        } else if (lang.equals("en") && prefix) {
            return NAME_EN_STR;
        } else if (lang.equals("sv") && prefix) {
            return NAME_SV_STR;
        } else {
            return NAME_FI_STR;
        }
    }
}
