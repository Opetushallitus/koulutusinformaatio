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

package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLOSToSolrInputDocument implements Converter<UpperSecondaryLOS, List<SolrInputDocument>> {
    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_UPSEC = "LUKIO";

    @Override
    public List<SolrInputDocument> convert(UpperSecondaryLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();

        for (UpperSecondaryLOI loi : los.getLois()) {
            docs.add(createDoc(los, loi));
        }

        return docs;
    }

    private SolrInputDocument createDoc(UpperSecondaryLOS los, UpperSecondaryLOI loi) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("type", TYPE_UPSEC);
        Provider provider = los.getProvider();
        doc.addField("id", loi.getId());
        doc.addField("losId", los.getId());
        doc.addField("lopId", provider.getId());
        doc.addField("prerequisites", loi.getPrerequisite().getValue());

        doc.setField("prerequisite", resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.addField("prerequisiteCode", loi.getPrerequisite().getValue());

        doc.setField("name", resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), los.getName().getTranslationsShortName()));
        doc.addField("name_fi", los.getName().getTranslations().get("fi"));
        doc.addField("name_sv", los.getName().getTranslations().get("sv"));
        doc.addField("name_en", los.getName().getTranslations().get("en"));

        doc.setField("lopName", resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), provider.getName().getTranslations()));
        doc.addField("lopName_fi", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_sv", provider.getName().getTranslations().get("sv"));
        doc.addField("lopName_en", provider.getName().getTranslations().get("en"));

        doc.addField("lopHomeplace", provider.getHomePlace().getTranslations().values());

        if (provider.getVisitingAddress() != null) {
            doc.addField("lopAddress_fi", provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField("lopDescription_fi", provider.getDescription().getTranslations().get("fi"));
            doc.addField("lopDescription_sv", provider.getDescription().getTranslations().get("sv"));
            doc.addField("lopDescription_en", provider.getDescription().getTranslations().get("en"));
        }
        if (los.getQualification() != null) {
            doc.addField("qualification_fi", los.getQualification().getTranslations().get("fi"));
            doc.addField("qualification_sv", los.getQualification().getTranslations().get("sv"));
            doc.addField("qualification_en", los.getQualification().getTranslations().get("en"));
        }
        if (los.getGoals() != null) {
            doc.addField("goals_fi", los.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", los.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", los.getGoals().getTranslations().get("en"));
        }
        if (loi.getContent() != null) {
            doc.addField("content_fi", loi.getContent().getTranslations().get("fi"));
            doc.addField("content_sv", loi.getContent().getTranslations().get("sv"));
            doc.addField("content_en", loi.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : loi.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField("asName_fi", ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField("asName_sv", ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField("asName_en", ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        addApplicationDates(doc, loi.getApplicationOptions());

        return doc;
    }

    private String resolveTranslationInTeachingLangUseFallback(List<Code> teachingLanguages, Map<String, String> translations) {
        String translation = null;
        for (Code teachingLanguage : teachingLanguages) {
            for (String key : translations.keySet()) {
                if (teachingLanguage.getValue().equalsIgnoreCase(key)) {
                    translation = translations.get(key);
                }
            }
        }
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null) {
            translation = translations.values().iterator().next();
        }

        return translation;
    }


    private void addApplicationDates(SolrInputDocument doc, List<ApplicationOption> applicationOptions) {
        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : applicationOptions) {
            if (ao.isSpecificApplicationDates()) {
                doc.addField(new StringBuilder().append("asStart").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationStartDate());
                doc.addField(new StringBuilder().append("asEnd").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationEndDate());
                parentApplicationDateRangeIndex++;
            } else {
                for (DateRange dr : ao.getApplicationSystem().getApplicationDates()) {
                    doc.addField(new StringBuilder().append("asStart").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
                    doc.addField(new StringBuilder().append("asEnd").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());
                    parentApplicationDateRangeIndex++;
                }
            }
        }
    }

}
