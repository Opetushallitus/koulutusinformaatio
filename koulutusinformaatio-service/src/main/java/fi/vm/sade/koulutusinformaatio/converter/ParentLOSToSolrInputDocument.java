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
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class ParentLOSToSolrInputDocument implements Converter<ParentLOS, List<SolrInputDocument>> {
    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_PARENT = "TUTKINTO";
    private static final String TYPE_CHILD = "KOULUTUSOHJELMA";

    public List<SolrInputDocument> convert(ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        docs.add(createParentDoc(parent));

        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                docs.add(createChildDoc(childLOS, childLOI, parent));
            }
        }

        return docs;
    }


    private SolrInputDocument createParentDoc(ParentLOS parent) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = parent.getProvider();
        doc.addField("type", TYPE_PARENT);
        doc.addField("id", parent.getId());
        doc.addField("lopId", provider.getId());

        doc.setField("name", parent.getName().getTranslations().get("fi"));
        doc.addField("name_fi", parent.getName().getTranslations().get("fi"));
        doc.addField("name_sv", parent.getName().getTranslations().get("sv"));
        doc.addField("name_en", parent.getName().getTranslations().get("en"));

        doc.setField("lopName", provider.getName().getTranslations().get("fi"));
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
        if (parent.getGoals() != null) {
            doc.addField("goals_fi", parent.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", parent.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", parent.getGoals().getTranslations().get("en"));
        }

        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        for (ParentLOI parentLOI : parent.getLois()) {
            applicationOptions.addAll(parentLOI.getApplicationOptions());
            for (ApplicationOption ao : parentLOI.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    doc.addField("asName_fi", ao.getApplicationSystem().getName().getTranslations().get("fi"));
                    doc.addField("asName_sv", ao.getApplicationSystem().getName().getTranslations().get("sv"));
                    doc.addField("asName_en", ao.getApplicationSystem().getName().getTranslations().get("en"));
                }
            }

        }
        addApplicationDates(doc, applicationOptions);

        Set<String> prerequisites = Sets.newHashSet();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                prerequisites.add(childLOI.getPrerequisite().getValue());
            }
        }
        doc.setField("prerequisites", prerequisites);

        return doc;
    }

    private SolrInputDocument createChildDoc(ChildLOS childLOS, ChildLOI childLOI, ParentLOS parent) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = parent.getProvider();
        doc.addField("type", TYPE_CHILD);
        doc.addField("id", childLOI.getId());
        doc.addField("losId", childLOS.getId());
        doc.addField("lopId", provider.getId());
        doc.addField("parentId", parent.getId());
        doc.addField("prerequisites", childLOI.getPrerequisite().getValue());

        doc.setField("prerequisite", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField("prerequisiteCode", childLOI.getPrerequisite().getValue());

        doc.setField("name", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOS.getName().getTranslationsShortName()));
        doc.addField("name_fi", childLOS.getName().getTranslations().get("fi"));
        doc.addField("name_sv", childLOS.getName().getTranslations().get("sv"));
        doc.addField("name_en", childLOS.getName().getTranslations().get("en"));

        doc.setField("lopName", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));
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
        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {
                doc.addField("professionalTitles_fi", i18n.getTranslations().get("fi"));
                doc.addField("professionalTitles_sv", i18n.getTranslations().get("sv"));
                doc.addField("professionalTitles_en", i18n.getTranslations().get("en"));
            }
        }
        if (childLOS.getQualification() != null) {
            doc.addField("qualification_fi", childLOS.getQualification().getTranslations().get("fi"));
            doc.addField("qualification_sv", childLOS.getQualification().getTranslations().get("sv"));
            doc.addField("qualification_en", childLOS.getQualification().getTranslations().get("en"));
        }
        if (childLOS.getGoals() != null) {
            doc.addField("goals_fi", childLOS.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", childLOS.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", childLOS.getGoals().getTranslations().get("en"));
        }
        if (childLOI.getContent() != null) {
            doc.addField("content_fi", childLOI.getContent().getTranslations().get("fi"));
            doc.addField("content_sv", childLOI.getContent().getTranslations().get("sv"));
            doc.addField("content_en", childLOI.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField("asName_fi", ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField("asName_sv", ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField("asName_en", ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        addApplicationDates(doc, childLOI.getApplicationOptions());

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
