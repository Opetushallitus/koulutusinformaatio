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
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.SolrConstants;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
public class ParentLOSToSolrInputDocument implements Converter<ParentLOS, List<SolrInputDocument>> {
    

    public List<SolrInputDocument> convert(ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();
        docs.add(createParentDoc(parent));
        docs.addAll(fIndexer.createFacetsDocs(parent));

        return docs;
    }

    private SolrInputDocument createParentDoc(ParentLOS parent) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = parent.getProvider();
        doc.addField(LearningOpportunity.TYPE, parent.getType());
        doc.addField(LearningOpportunity.ID, parent.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());

        //doc.setField(LearningOpportunity.NAME, parent.getName().getTranslations().get("fi"));
        
        String teachLang = parent.getTeachingLanguages().isEmpty() ? "EXC" : parent.getTeachingLanguages().get(0).getValue().toLowerCase();
        
        String parentName = SolrUtil.resolveTranslationInTeachingLangUseFallback(parent.getTeachingLanguages(), parent.getName().getTranslations());
        doc.setField(LearningOpportunity.NAME, parentName);
        
        if (parent.getCreditValue() != null) {
            doc.addField(LearningOpportunity.CREDITS, String.format("%s %s", parent.getCreditValue(), 
                        parent.getCreditUnit().getTranslationsShortName().get("fi")));
        }
        
        doc.addField(LearningOpportunity.NAME_SORT, parentName);
        
        if (teachLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, parent.getName().getTranslations().get("fi"));
        } else if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, parent.getName().getTranslations().get("sv"));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, parent.getName().getTranslations().get("en"));
        } else {
            doc.addField(LearningOpportunity.NAME_FI, parentName);
        }
        
        indexLopName(doc, provider, teachLang);

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
            
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, parent.getGoals().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, parent.getGoals().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, parent.getGoals().getTranslations().get("fi"));
            }
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
        SolrUtil.addApplicationDates(doc, applicationOptions);

        Set<String> prerequisites = Sets.newHashSet();
        Date earliest = null;
        int minDuration = Integer.MAX_VALUE;
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                addPrerequisite(prerequisites, childLOI);
                if (earliest == null || earliest.after(childLOI.getStartDate())) {
                    earliest = childLOI.getStartDate();
                }
                int curDuration = SolrUtil.getDuration(childLOI);
                minDuration = curDuration < minDuration ? curDuration : minDuration;
            }
        }
        doc.setField(LearningOpportunity.PREREQUISITES, prerequisites);
        doc.setField(LearningOpportunity.START_DATE_SORT, earliest);
        doc.setField(LearningOpportunity.DURATION_SORT, minDuration);
        
        indexFacetFields(parent, doc);
        for (ChildLOS childLOS : parent.getChildren()) {
        for (ChildLOI childLOI : childLOS.getLois()) {
            //docs.add(createChildDoc(childLOS, childLOI, parent));
            indexChildFields(doc, childLOS, childLOI, teachLang);
        }
        }
        

        return doc;
    }

    private void addPrerequisite(Set<String> prerequisites, ChildLOI childLOI) {
        String prereq = SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(childLOI.getPrerequisite().getValue()) ? SolrConstants.PK : childLOI.getPrerequisite().getValue();
        if (!prerequisites.contains(prereq)) {
            prerequisites.add(prereq);
        }
    }

    private void indexLopName(SolrInputDocument doc, Provider provider, String teachLang) {
        
        String nameFi = provider.getName().getTranslations().get("fi");
        String nameSv = provider.getName().getTranslations().get("sv");
        String nameEn = provider.getName().getTranslations().get("en");
        
        //Setting the lop name to be finnish, if no finnish name, fallback to swedish or english
        String name = nameFi != null ? nameFi : nameSv;
        name = name == null ? nameEn : name;
        
        doc.setField(LearningOpportunity.LOP_NAME, name);
        doc.addField("lopNames", name);
        if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv",provider.getName().getTranslations()));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en",provider.getName().getTranslations()));
        } else {
            doc.addField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi",provider.getName().getTranslations()));
        }
        
    }
    
    
    
    
    private void indexChildFields(SolrInputDocument doc, ChildLOS childLOS, ChildLOI childLOI, String teachLang) {

        doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(childLOI.getPrerequisite().getValue()) 
                                ? SolrConstants.PK : childLOI.getPrerequisite().getValue());

        
        String childName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOS.getName().getTranslationsShortName());
        doc.setField(LearningOpportunity.CHILD_NAME, childName);
        
        if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.CHILD_NAME_SV, childLOS.getName().getTranslations().get("sv"));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.CHILD_NAME_EN, childLOS.getName().getTranslations().get("en"));
        } else {
            doc.addField(LearningOpportunity.CHILD_NAME_FI, childLOS.getName().getTranslations().get("fi"));
        }
        
        

        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {
                
                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, i18n.getTranslations().get("sv"));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, i18n.getTranslations().get("en"));
                } else{
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, i18n.getTranslations().get("fi"));
                }
            }
        }
        if (childLOS.getQualification() != null) {
            
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.QUALIFICATION_SV, childLOS.getQualification().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.QUALIFICATION_EN, childLOS.getQualification().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.QUALIFICATION_FI, childLOS.getQualification().getTranslations().get("fi"));
            }
            
        }
        if (childLOS.getGoals() != null) {
            
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, childLOS.getGoals().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, childLOS.getGoals().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, childLOS.getGoals().getTranslations().get("fi"));
            }
            
            
        }
        if (childLOI.getContent() != null) {
            
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, childLOI.getContent().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, childLOI.getContent().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI, childLOI.getContent().getTranslations().get("fi"));
            }
            
            
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        //SolrUtil.addApplicationDates(doc, childLOI.getApplicationOptions());
        
        //indexFacetFields(childLOS, childLOI, doc);
    }

    /*
     * Indexes fields used in facet search for ParentLOS learning opportunities
     */
    private void indexFacetFields(ParentLOS parent, SolrInputDocument doc) {
        List<String> usedVals = new ArrayList<String>();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                String teachingLang = childLOI.getTeachingLanguages().get(0).getValue();
                if (!usedVals.contains(teachingLang)) {
                    doc.addField(LearningOpportunity.TEACHING_LANGUAGE, teachingLang);
                    usedVals.add(teachingLang);
                }
                if (!usedVals.contains(SolrConstants.ED_TYPE_AMMATILLINEN)) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLINEN);
                    usedVals.add(SolrConstants.ED_TYPE_AMMATILLINEN);
                }
                if (childLOI.isKaksoistutkinto() && !usedVals.contains(SolrConstants.ED_TYPE_KAKSOIS)) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
                    usedVals.add(SolrConstants.ED_TYPE_KAKSOIS);
                }
            }
        }
        
        for (Code curTopic : parent.getTopics()) {
            doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
        }
        
        for (Code curTopic : parent.getThemes()) {
            doc.addField(LearningOpportunity.THEME, curTopic.getUri());
        }
        
    }
    
}
