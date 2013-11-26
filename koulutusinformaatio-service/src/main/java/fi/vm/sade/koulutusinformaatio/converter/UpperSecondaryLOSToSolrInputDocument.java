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
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.SolrConstants;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
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
        FacetIndexer fIndexer = new FacetIndexer();

        for (UpperSecondaryLOI loi : los.getLois()) {
            docs.add(createDoc(los, loi));
            docs.addAll(fIndexer.createFacetDocs(loi));
        }

        return docs;
    }
    


    private SolrInputDocument createDoc(UpperSecondaryLOS los, UpperSecondaryLOI loi) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.TYPE, TYPE_UPSEC);
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, loi.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        doc.addField(LearningOpportunity.PREREQUISITES, loi.getPrerequisite().getValue());

        doc.setField(LearningOpportunity.PREREQUISITE, resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, loi.getPrerequisite().getValue());


        doc.setField(LearningOpportunity.NAME, resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), los.getName().getTranslationsShortName()));
        doc.addField(LearningOpportunity.NAME_FI, los.getName().getTranslations().get("fi"));
        
        doc.addField(LearningOpportunity.NAME_SV, los.getName().getTranslations().get("sv"));
        
        doc.addField(LearningOpportunity.NAME_EN, los.getName().getTranslations().get("en"));

        doc.setField(LearningOpportunity.LOP_NAME, resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), provider.getName().getTranslations()));
        doc.addField(LearningOpportunity.LOP_NAME_FI, provider.getName().getTranslations().get("fi"));
        doc.addField(LearningOpportunity.LOP_NAME_SV, provider.getName().getTranslations().get("sv"));
        doc.addField(LearningOpportunity.LOP_NAME_EN, provider.getName().getTranslations().get("en"));
        
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
            doc.addField(LearningOpportunity.QUALIFICATION_FI, los.getQualification().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.QUALIFICATION_SV, los.getQualification().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.QUALIFICATION_EN, los.getQualification().getTranslations().get("en"));
        }
        if (los.getGoals() != null) {
            doc.addField(LearningOpportunity.GOALS_FI, los.getGoals().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.GOALS_SV, los.getGoals().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.GOALS_EN, los.getGoals().getTranslations().get("en"));
        }
        if (loi.getContent() != null) {
            doc.addField(LearningOpportunity.CONTENT_FI, loi.getContent().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.CONTENT_SV, loi.getContent().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.CONTENT_EN, loi.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : loi.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }
        
        addApplicationDates(doc, loi.getApplicationOptions());
        
        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, loi.getStartDate());
        indexDurationField(loi, doc);
        doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
                resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), provider.getName().getTranslations()), 
                resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), los.getName().getTranslationsShortName())));
        
        
        //For faceting
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, loi.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_LUKIO);
        if (loi.isKaksoistutkinto()) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
        }

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
    
    /*
     * Indexes the duration_ssort field, used in sorting
     * results according to planned duration of the loi
     */
    private void indexDurationField(UpperSecondaryLOI loi, SolrInputDocument doc) {
        int duration = getDuration(loi);
        doc.addField(LearningOpportunity.DURATION_SORT, duration);
    }

    /*
     * Parses duration from duration string, which may contain
     * non numerical characters, e.g. 2-5. Takes the min value
     * of the numerical values. 
     * Scales values to be counted in months.
     */
    private int getDuration(UpperSecondaryLOI loi) {
        String[] numStrings = loi.getPlannedDuration().split("[^0-9]*");
        int min = Integer.MAX_VALUE;
        for (String curNumStr : numStrings) {
            if ((curNumStr != null) && !curNumStr.isEmpty()) {
                try {
                    int curInt = Integer.parseInt(curNumStr);
                    min = curInt < min ? curInt : min;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (loi.getPduCodeUri().contains(SolrConstants.KESTOTYYPPI_VUOSI) && (min < Integer.MAX_VALUE)) {
            min = min * 12;
        } 
        
        return min < Integer.MAX_VALUE ? min : -1;
    }
}
