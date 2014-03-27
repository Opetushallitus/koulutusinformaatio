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
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLOSToSolrInputDocument implements Converter<UpperSecondaryLOS, List<SolrInputDocument>> {

    @Override
    public List<SolrInputDocument> convert(UpperSecondaryLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        for (UpperSecondaryLOI loi : los.getLois()) {
            docs.add(createDoc(los, loi));
            docs.addAll(fIndexer.createFacetDocs(loi, los));
        }

        return docs;
    }



    private SolrInputDocument createDoc(UpperSecondaryLOS los, UpperSecondaryLOI loi) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, loi.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());

        doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(loi.getPrerequisite().getValue()) 
                ? SolrConstants.PK : loi.getPrerequisite().getValue());

        doc.setField(LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, loi.getPrerequisite().getValue());

        if (los.getCreditValue() != null 
                && los.getCreditUnit() != null 
                && los.getCreditUnit().getTranslationsShortName() != null
                && !los.getCreditUnit().getTranslationsShortName().isEmpty()) {
            doc.addField(LearningOpportunity.CREDITS, String.format("%s %s", los.getCreditValue(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(),
                            los.getCreditUnit().getTranslationsShortName())));
        }

        String teachingLang = loi.getTeachingLanguages().isEmpty() ? "EXC" : loi.getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), los.getName().getTranslationsShortName());


        doc.setField(LearningOpportunity.NAME, losName);


        if (teachingLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi",  los.getName().getTranslations()));
        } else if (teachingLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", los.getName().getTranslations()));
        } else if (teachingLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", los.getName().getTranslations()));
        } else {
            doc.addField(LearningOpportunity.NAME_FI, losName);
        }
        
        if (provider.getHomePlace() != null) { 
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                    SolrUtil.resolveTextWithFallback(teachingLang,
                            provider.getHomePlace().getTranslations()));
        }

        doc.setField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), provider.getName().getTranslations()));

        doc.setField("lopNames", SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), provider.getName().getTranslations()));

        if (teachingLang.equals("sv")) {
            doc.addField(LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv", provider.getName().getTranslations()));
        } else if (teachingLang.equals("en")) {
            doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en", provider.getName().getTranslations()));
        } else {
            doc.addField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi",  provider.getName().getTranslations()));
        }

        if (provider.getHomeDistrict() != null) {

            List<String> locVals = new ArrayList<String>();
            locVals.addAll(provider.getHomeDistrict().getTranslations().values());
            locVals.addAll(provider.getHomePlace().getTranslations().values());
            doc.addField(LearningOpportunity.LOP_HOMEPLACE, locVals);
        } else {
            doc.addField(LearningOpportunity.LOP_HOMEPLACE, provider.getHomePlace().getTranslations().values());
        }

        if (provider.getVisitingAddress() != null) {
            doc.addField(LearningOpportunity.LOP_ADDRESS_FI, provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_FI, provider.getDescription().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_SV, provider.getDescription().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_EN, provider.getDescription().getTranslations().get("en"));
        }
        if (los.getQualification() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv", los.getQualification().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en", los.getQualification().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi", los.getQualification().getTranslations()));
            }

        }
        if (los.getGoals() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv", los.getGoals().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en", los.getGoals().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi", los.getGoals().getTranslations()));
            }

        }
        if (loi.getContent() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv", loi.getContent().getTranslations()));
            } else if (teachingLang.endsWith("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en", loi.getContent().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi", loi.getContent().getTranslations()));
            }
        }

        for (ApplicationOption ao : loi.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        SolrUtil.addApplicationDates(doc, loi.getApplicationOptions());

        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, loi.getStartDate());
        doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
                SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), 
                        provider.getName().getTranslations()).toLowerCase().trim(),
                SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), 
                        los.getName().getTranslationsShortName())).toLowerCase().trim());


        //For faceting
        indexFacetFields(doc, los, loi);

        return doc;
    }

    private void indexFacetFields(SolrInputDocument doc, UpperSecondaryLOS los,  UpperSecondaryLOI loi) {
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, loi.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_LUKIO);
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        if (loi.isKaksoistutkinto()) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
        }
        for (Code curTopic : los.getTopics()) {
            doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
        }

        for (Code curTopic : los.getThemes()) {
            doc.addField(LearningOpportunity.THEME, curTopic.getUri());
        }
    }
}
