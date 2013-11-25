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
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.*;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Date;
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
        FacetIndexer fIndexer = new FacetIndexer();
        docs.add(createParentDoc(parent));
        docs.addAll(fIndexer.createFacetsDocs(parent));

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
        doc.addField(LearningOpportunity.TYPE, TYPE_PARENT);
        doc.addField(LearningOpportunity.ID, parent.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());

        doc.setField(LearningOpportunity.NAME, parent.getName().getTranslations().get("fi"));
        
        doc.addField(LearningOpportunity.NAME_FI, parent.getName().getTranslations().get("fi"));
        doc.addField(LearningOpportunity.NAME_SORT, parent.getName().getTranslations().get("fi"));
        
        doc.addField(LearningOpportunity.NAME_SV, parent.getName().getTranslations().get("sv"));
        
        doc.addField(LearningOpportunity.NAME_EN, parent.getName().getTranslations().get("en"));

        doc.setField(LearningOpportunity.LOP_NAME, provider.getName().getTranslations().get("fi"));
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
        if (parent.getGoals() != null) {
            doc.addField(LearningOpportunity.GOALS_FI, parent.getGoals().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.GOALS_SV, parent.getGoals().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.GOALS_EN, parent.getGoals().getTranslations().get("en"));
        }

        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        for (ParentLOI parentLOI : parent.getLois()) {
            applicationOptions.addAll(parentLOI.getApplicationOptions());
            for (ApplicationOption ao : parentLOI.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                    doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                    doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
                }
            }

        }
        addApplicationDates(doc, applicationOptions);

        Set<String> prerequisites = Sets.newHashSet();
        Date earliest = null;
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                prerequisites.add(childLOI.getPrerequisite().getValue());
                if (earliest == null || earliest.after(childLOI.getStartDate())) {
                    earliest = childLOI.getStartDate();
                }
            }
        }
        doc.setField(LearningOpportunity.PREREQUISITES, prerequisites);
        doc.setField(LearningOpportunity.START_DATE_SORT, earliest);
        
        indexFacetFields(parent, doc);

        return doc;
    }



    private SolrInputDocument createChildDoc(ChildLOS childLOS, ChildLOI childLOI, ParentLOS parent) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = parent.getProvider();
        doc.addField(LearningOpportunity.TYPE, TYPE_CHILD);
        doc.addField(LearningOpportunity.ID, childLOI.getId());
        doc.addField(LearningOpportunity.LOS_ID, childLOS.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        doc.addField(LearningOpportunity.PARENT_ID, parent.getId());
        doc.addField(LearningOpportunity.PREREQUISITES, childLOI.getPrerequisite().getValue());
        
        

        doc.setField(LearningOpportunity.PREREQUISITE, resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, childLOI.getPrerequisite().getValue());

        doc.setField(LearningOpportunity.NAME, resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOS.getName().getTranslationsShortName()));
        doc.addField(LearningOpportunity.NAME_FI, childLOS.getName().getTranslations().get("fi"));
        
        doc.addField(LearningOpportunity.NAME_SORT, resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOS.getName().getTranslationsShortName()));
        
        doc.addField(LearningOpportunity.NAME_SV, childLOS.getName().getTranslations().get("sv"));
        
        doc.addField(LearningOpportunity.NAME_EN, childLOS.getName().getTranslations().get("en"));

        doc.setField(LearningOpportunity.LOP_NAME, resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));
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
        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {
                doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, i18n.getTranslations().get("fi"));
                doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, i18n.getTranslations().get("sv"));
                doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, i18n.getTranslations().get("en"));
            }
        }
        if (childLOS.getQualification() != null) {
            doc.addField(LearningOpportunity.QUALIFICATION_FI, childLOS.getQualification().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.QUALIFICATION_SV, childLOS.getQualification().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.QUALIFICATION_EN, childLOS.getQualification().getTranslations().get("en"));
        }
        if (childLOS.getGoals() != null) {
            doc.addField(LearningOpportunity.GOALS_FI, childLOS.getGoals().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.GOALS_SV, childLOS.getGoals().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.GOALS_EN, childLOS.getGoals().getTranslations().get("en"));
        }
        if (childLOI.getContent() != null) {
            doc.addField(LearningOpportunity.CONTENT_FI, childLOI.getContent().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.CONTENT_SV, childLOI.getContent().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.CONTENT_EN, childLOI.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        addApplicationDates(doc, childLOI.getApplicationOptions());
        
        doc.addField(LearningOpportunity.START_DATE_SORT, childLOI.getStartDate());
        
        indexFacetFields(childLOS, childLOI, parent, doc);

        return doc;
    }
    

    /*
     * Indexes fields used in facet search for ParentLOS learning opportunities
     */
    private void indexFacetFields(ParentLOS parent, SolrInputDocument doc) {
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                doc.addField(LearningOpportunity.TEACHING_LANGUAGE, childLOI.getTeachingLanguages().get(0).getValue());
                if (childLOI.getPrerequisite().equals(SolrConstants.SPECIAL_EDUCATION)) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_ER);
                } else {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLINEN);
                }
                if (childLOI.isKaksoistutkinto()) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
                }
            }
        }
        
    }
    
    /*
     * Indexes fields used in facet search for ChildLOS
     */
    private void indexFacetFields(ChildLOS childLOS, ChildLOI childLOI, ParentLOS parent, SolrInputDocument doc) {
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, childLOI.getTeachingLanguages().get(0).getValue());
        if (childLOI.getPrerequisite().getValue().equals(SolrConstants.SPECIAL_EDUCATION)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_ER);
        } else {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLINEN);
        }
        if (childLOI.isKaksoistutkinto()) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
        }
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
