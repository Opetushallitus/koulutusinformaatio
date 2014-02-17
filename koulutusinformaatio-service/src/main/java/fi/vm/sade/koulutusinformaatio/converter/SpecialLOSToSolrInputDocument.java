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
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class SpecialLOSToSolrInputDocument implements Converter<SpecialLOS, List<SolrInputDocument>> {



    @Override
    public List<SolrInputDocument> convert(SpecialLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        for (ChildLOI loi : los.getLois()) {
            docs.add(createDoc(los, loi));
            docs.addAll(fIndexer.createFacetDocs(loi, los));
        }
        return docs;
    }

    private SolrInputDocument createDoc(SpecialLOS specialLOS, ChildLOI childLOI) {

        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = specialLOS.getProvider();
        doc.addField(SolrUtil.LearningOpportunity.TYPE, specialLOS.getType());
        doc.addField(SolrUtil.LearningOpportunity.ID, childLOI.getId());
        doc.addField(SolrUtil.LearningOpportunity.LOS_ID, specialLOS.getId());
        doc.addField(SolrUtil.LearningOpportunity.LOP_ID, provider.getId());
        doc.addField(SolrUtil.LearningOpportunity.PREREQUISITES, SolrConstants.PK);

        if (specialLOS.getCreditValue() != null) {
            doc.addField(SolrUtil.LearningOpportunity.CREDITS, String.format("%s %s", specialLOS.getCreditValue(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(childLOI.getTeachingLanguages(),
                            specialLOS.getCreditUnit().getTranslationsShortName())));
        }

        doc.setField(SolrUtil.LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField(SolrUtil.LearningOpportunity.PREREQUISITE_CODE, childLOI.getPrerequisite().getValue());

        String teachingLang = childLOI.getTeachingLanguages().isEmpty() ? "EXC" : childLOI.getTeachingLanguages().get(0).getValue().toLowerCase();
        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), specialLOS.getName().getTranslationsShortName());


        doc.setField(SolrUtil.LearningOpportunity.NAME, losName);
        doc.addField(SolrUtil.LearningOpportunity.NAME_SORT, losName);

        if (teachingLang.equals("fi")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", specialLOS.getName().getTranslations()));
        } else if (teachingLang.equals("sv")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", specialLOS.getName().getTranslations()));
        } else if (teachingLang.equals("en")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", specialLOS.getName().getTranslations()));
        } else {
            doc.addField(SolrUtil.LearningOpportunity.NAME_FI, losName);
        }

        doc.setField(SolrUtil.LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));

        doc.addField("lopNames", SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));

        if (teachingLang.equals("sv")) {
            doc.addField(SolrUtil.LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv", provider.getName().getTranslations()));
        } else if (teachingLang.equals("en")) {
            doc.addField(SolrUtil.LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en", provider.getName().getTranslations()));
        } else {
            doc.addField(SolrUtil.LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi", provider.getName().getTranslations()));
        }

        if (provider.getHomeDistrict() != null) {
            List<String> locVals = new ArrayList<String>();
            locVals.addAll(provider.getHomeDistrict().getTranslations().values());
            locVals.addAll(provider.getHomePlace().getTranslations().values());
            doc.addField(SolrUtil.LearningOpportunity.LOP_HOMEPLACE, locVals);
        } else {
            doc.addField(SolrUtil.LearningOpportunity.LOP_HOMEPLACE, provider.getHomePlace().getTranslations().values());
        }

        if (provider.getVisitingAddress() != null) {
            doc.addField(SolrUtil.LearningOpportunity.LOP_ADDRESS_FI, provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField(SolrUtil.LearningOpportunity.LOP_DESCRIPTION_FI, provider.getDescription().getTranslations().get("fi"));
            doc.addField(SolrUtil.LearningOpportunity.LOP_DESCRIPTION_SV, provider.getDescription().getTranslations().get("sv"));
            doc.addField(SolrUtil.LearningOpportunity.LOP_DESCRIPTION_EN, provider.getDescription().getTranslations().get("en"));
        }
        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {

                if (teachingLang.equals("sv")) {
                    doc.addField(SolrUtil.LearningOpportunity.PROFESSIONAL_TITLES_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachingLang.equals("en")) {
                    doc.addField(SolrUtil.LearningOpportunity.PROFESSIONAL_TITLES_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else {
                    doc.addField(SolrUtil.LearningOpportunity.PROFESSIONAL_TITLES_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }
        if (specialLOS.getQualification() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv", specialLOS.getQualification().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en", specialLOS.getQualification().getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi", specialLOS.getQualification().getTranslations()));
            }
        }
        if (specialLOS.getGoals() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv", specialLOS.getGoals().getTranslations()));
            } else if  (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en", specialLOS.getGoals().getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi", specialLOS.getGoals().getTranslations()));
            }
        }
        if (childLOI.getContent() != null) {

            if (teachingLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv", childLOI.getContent().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en", childLOI.getContent().getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi", childLOI.getContent().getTranslations()));
            }
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        SolrUtil.addApplicationDates(doc, childLOI.getApplicationOptions());

        doc.addField(SolrUtil.LearningOpportunity.START_DATE_SORT, childLOI.getStartDate());
        indexFacetFields(childLOI, specialLOS, doc);

        return doc;
    }

    /*
     * Indexes fields used in facet search for ChildLOS
     */
    private void indexFacetFields(ChildLOI childLOI, SpecialLOS specialLOS, SolrInputDocument doc) {
        doc.addField(SolrUtil.LearningOpportunity.TEACHING_LANGUAGE, childLOI.getTeachingLanguages().get(0).getValue());

        if (specialLOS.getType().equals(TarjontaConstants.TYPE_REHAB)) {
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_VALMENTAVA);
        } else {
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMM_ER);
        }
    }


}
