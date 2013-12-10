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
            docs.addAll(fIndexer.createFacetDocs(loi));
        }
        return docs;
    }

    private SolrInputDocument createDoc(SpecialLOS specialLOS, ChildLOI childLOI) {

        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = specialLOS.getProvider();
        doc.addField(SolrFields.LearningOpportunity.TYPE, specialLOS.getType());
        doc.addField(SolrFields.LearningOpportunity.ID, childLOI.getId());
        doc.addField(SolrFields.LearningOpportunity.LOS_ID, specialLOS.getId());
        doc.addField(SolrFields.LearningOpportunity.LOP_ID, provider.getId());
        doc.addField(SolrFields.LearningOpportunity.PREREQUISITES, childLOI.getPrerequisite().getValue());

        if (specialLOS.getCreditValue() != null) {
            doc.addField(SolrFields.LearningOpportunity.CREDITS, String.format("%s %s", specialLOS.getCreditValue(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(childLOI.getTeachingLanguages(),
                            specialLOS.getCreditUnit().getTranslationsShortName())));
        }

        doc.setField(SolrFields.LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField(SolrFields.LearningOpportunity.PREREQUISITE_CODE, childLOI.getPrerequisite().getValue());

        doc.setField(SolrFields.LearningOpportunity.NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), specialLOS.getName().getTranslationsShortName()));
        doc.addField(SolrFields.LearningOpportunity.NAME_FI, specialLOS.getName().getTranslations().get("fi"));

        doc.addField(SolrFields.LearningOpportunity.NAME_SORT, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), specialLOS.getName().getTranslationsShortName()));

        doc.addField(SolrFields.LearningOpportunity.NAME_SV, specialLOS.getName().getTranslations().get("sv"));

        doc.addField(SolrFields.LearningOpportunity.NAME_EN, specialLOS.getName().getTranslations().get("en"));

        doc.setField(SolrFields.LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));
        doc.addField(SolrFields.LearningOpportunity.LOP_NAME_FI, provider.getName().getTranslations().get("fi"));
        doc.addField(SolrFields.LearningOpportunity.LOP_NAME_SV, provider.getName().getTranslations().get("sv"));
        doc.addField(SolrFields.LearningOpportunity.LOP_NAME_EN, provider.getName().getTranslations().get("en"));

        if (provider.getHomeDistrict() != null) {
            List<String> locVals = new ArrayList<String>();
            locVals.addAll(provider.getHomeDistrict().getTranslations().values());
            locVals.addAll(provider.getHomePlace().getTranslations().values());
            doc.addField(SolrFields.LearningOpportunity.LOP_HOMEPLACE, locVals);
        } else {
            doc.addField(SolrFields.LearningOpportunity.LOP_HOMEPLACE, provider.getHomePlace().getTranslations().values());
        }

        if (provider.getVisitingAddress() != null) {
            doc.addField(SolrFields.LearningOpportunity.LOP_ADDRESS_FI, provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField(SolrFields.LearningOpportunity.LOP_DESCRIPTION_FI, provider.getDescription().getTranslations().get("fi"));
            doc.addField(SolrFields.LearningOpportunity.LOP_DESCRIPTION_SV, provider.getDescription().getTranslations().get("sv"));
            doc.addField(SolrFields.LearningOpportunity.LOP_DESCRIPTION_EN, provider.getDescription().getTranslations().get("en"));
        }
        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {
                doc.addField(SolrFields.LearningOpportunity.PROFESSIONAL_TITLES_FI, i18n.getTranslations().get("fi"));
                doc.addField(SolrFields.LearningOpportunity.PROFESSIONAL_TITLES_SV, i18n.getTranslations().get("sv"));
                doc.addField(SolrFields.LearningOpportunity.PROFESSIONAL_TITLES_EN, i18n.getTranslations().get("en"));
            }
        }
        if (specialLOS.getQualification() != null) {
            doc.addField(SolrFields.LearningOpportunity.QUALIFICATION_FI, specialLOS.getQualification().getTranslations().get("fi"));
            doc.addField(SolrFields.LearningOpportunity.QUALIFICATION_SV, specialLOS.getQualification().getTranslations().get("sv"));
            doc.addField(SolrFields.LearningOpportunity.QUALIFICATION_EN, specialLOS.getQualification().getTranslations().get("en"));
        }
        if (specialLOS.getGoals() != null) {
            doc.addField(SolrFields.LearningOpportunity.GOALS_FI, specialLOS.getGoals().getTranslations().get("fi"));
            doc.addField(SolrFields.LearningOpportunity.GOALS_SV, specialLOS.getGoals().getTranslations().get("sv"));
            doc.addField(SolrFields.LearningOpportunity.GOALS_EN, specialLOS.getGoals().getTranslations().get("en"));
        }
        if (childLOI.getContent() != null) {
            doc.addField(SolrFields.LearningOpportunity.CONTENT_FI, childLOI.getContent().getTranslations().get("fi"));
            doc.addField(SolrFields.LearningOpportunity.CONTENT_SV, childLOI.getContent().getTranslations().get("sv"));
            doc.addField(SolrFields.LearningOpportunity.CONTENT_EN, childLOI.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(SolrFields.LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(SolrFields.LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(SolrFields.LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        SolrUtil.addApplicationDates(doc, childLOI.getApplicationOptions());

        doc.addField(SolrFields.LearningOpportunity.START_DATE_SORT, childLOI.getStartDate());
        indexDurationField(childLOI, doc);

        indexFacetFields(childLOI, doc);

        return doc;
    }

    /*
     * Indexes the duration_ssort field, used in sorting
     * results according to planned duration of the loi
    */
    private void indexDurationField(ChildLOI childLOI, SolrInputDocument doc) {
        int duration = SolrUtil.getDuration(childLOI);
        doc.addField(SolrFields.LearningOpportunity.DURATION_SORT, duration);
    }

    /*
     * Indexes fields used in facet search for ChildLOS
     */
    private void indexFacetFields(ChildLOI childLOI, SolrInputDocument doc) {
        doc.addField(SolrFields.LearningOpportunity.TEACHING_LANGUAGE, childLOI.getTeachingLanguages().get(0).getValue());
        doc.addField(SolrFields.LearningOpportunity.EDUCATION_TYPE, SolrFields.SolrConstants.ED_TYPE_AMM_ER);
    }


}
