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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

/**
 * @author Hannu Lyytikainen
 */
public class ParentLOSToSolrInputDocument implements Converter<ParentLOS, List<SolrInputDocument>> {


    public List<SolrInputDocument> convert(ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();
        docs.addAll(createParentDocsByPrerequisites(parent));
        docs.addAll(fIndexer.createFacetsDocs(parent));

        return docs;
    }

    private List<SolrInputDocument> createParentDocsByPrerequisites(ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        Map<String,Code> prerequisitesMap = new HashMap<String,Code>();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                String prereq = SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(childLOI.getPrerequisite().getValue())
                        ? SolrConstants.PK
                                : childLOI.getPrerequisite().getValue();
                prerequisitesMap.put(prereq, childLOI.getPrerequisite());
            }
        }

        for (Code curPrereq : prerequisitesMap.values()) {
            docs.add(createParentDoc(parent, curPrereq));
        }

        return docs;
    }

    private SolrInputDocument createParentDoc(ParentLOS parent, Code prerequisite) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = parent.getProvider();
        String prereqVal = SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(prerequisite.getValue())
                ? SolrConstants.PK : prerequisite.getValue();
        doc.addField(LearningOpportunity.TYPE, parent.getType());
        doc.addField(LearningOpportunity.ID, String.format("%s#%s", parent.getId(), prereqVal));
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        
        doc.addField(LearningOpportunity.PREREQUISITES, prereqVal);

        doc.setField(LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                parent.getTeachingLanguages(), prerequisite.getName().getTranslations()));
        doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                parent.getTeachingLanguages(), prerequisite.getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, prereqVal);

        //doc.setField(LearningOpportunity.NAME, parent.getName().getTranslations().get("fi"));

        String teachLang = parent.getTeachingLanguages().isEmpty() ? "EXC" : parent.getTeachingLanguages().get(0).getValue().toLowerCase();

        String parentName = SolrUtil.resolveTranslationInTeachingLangUseFallback(parent.getTeachingLanguages(),
                parent.getName().getTranslations());
        doc.setField(LearningOpportunity.NAME, parentName);

        
        try {
            ChildLOI latest = parent.getLatestLoi();
            String cv = latest.getCreditValue();
            Map<String, String> cu = latest.getCreditUnit().getTranslations();
            doc.addField(LearningOpportunity.CREDITS,
                    String.format("%s %s", cv, SolrUtil.resolveTranslationInTeachingLangUseFallback(parent.getTeachingLanguages(), cu)));
        } catch (Exception e) {
            doc.addField(
                    LearningOpportunity.CREDITS,
                    String.format("%s %s", parent.getCreditValue(),
                            SolrUtil.resolveTranslationInTeachingLangUseFallback(parent.getTeachingLanguages(), parent.getCreditUnit().getTranslations())));
        }

        doc.addField(LearningOpportunity.NAME_FI_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_SV_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_EN_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_SORT, parentName.toLowerCase().trim());

        if (teachLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", parent.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi",  parent.getName().getTranslations()));
        } else if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", parent.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv",  parent.getName().getTranslations()));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", parent.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en",  parent.getName().getTranslations()));
        } else {
            doc.addField(LearningOpportunity.NAME_FI, parentName);
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, parentName);
        }
        

        SolrUtil.indexLopName(doc, provider, teachLang);

        if (provider.getHomePlace() != null) {
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY,
                    SolrUtil.resolveTextWithFallback(teachLang,
                            provider.getHomePlace().getTranslations()));
        }

        SolrUtil.setLopAndHomeplaceDisplaynames(doc, provider, prerequisite);

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
        String aoNameFi = "";
        String aoNameSv = "";
        String aoNameEn = "";
        for (ParentLOI parentLOI : parent.getLois()) {
            if (parentLOI.getPrerequisite() != null && parentLOI.getPrerequisite().getValue().equals(prerequisite.getValue())) {
                applicationOptions.addAll(parentLOI.getApplicationOptions());



                for (ApplicationOption ao : parentLOI.getApplicationOptions()) {
                    if (ao.getApplicationSystem() != null) {
                        ApplicationSystem curAs = ao.getApplicationSystem();
                        doc.addField(LearningOpportunity.AS_NAME_FI, curAs.getName().getTranslations().get("fi"));
                        doc.addField(LearningOpportunity.AS_NAME_SV, curAs.getName().getTranslations().get("sv"));
                        doc.addField(LearningOpportunity.AS_NAME_EN, curAs.getName().getTranslations().get("en"));

                        if (curAs.isShownAsFacet()) {
                            doc.addField(LearningOpportunity.AS_FACET, curAs.getId());
                        }
                    }
                    if (ao.getName() != null) {
                        aoNameFi = String.format("%s %s", aoNameFi,  SolrUtil.resolveTextWithFallback("fi", ao.getName().getTranslations()));
                        aoNameSv = String.format("%s %s", aoNameSv,  SolrUtil.resolveTextWithFallback("sv", ao.getName().getTranslations()));
                        aoNameEn = String.format("%s %s", aoNameEn,  SolrUtil.resolveTextWithFallback("en", ao.getName().getTranslations()));
                    }
                }


            }

        }
        doc.addField(LearningOpportunity.AO_NAME_FI, aoNameFi);
        doc.addField(LearningOpportunity.AO_NAME_SV, aoNameSv);
        doc.addField(LearningOpportunity.AO_NAME_EN, aoNameEn);
        SolrUtil.addApplicationDates(doc, applicationOptions);

        Date earliest = null;

        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getStartDate() != null) {
                    if (earliest == null || earliest.after(childLOI.getStartDate())) {
                        earliest = childLOI.getStartDate();
                    }
                }
            }
        }
        doc.setField(LearningOpportunity.START_DATE_SORT, earliest);
        
        indexFacetFields(parent, doc, prereqVal, teachLang);
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getPrerequisite() != null && childLOI.getPrerequisite().getValue().equals(prerequisite.getValue())) {
                    indexChildFields(doc, childLOS, childLOI, teachLang);
                }
            }
        }


        return doc;
    }






    private void indexChildFields(SolrInputDocument doc, ChildLOS childLOS, ChildLOI childLOI, String teachLang) {

        /*doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(childLOI.getPrerequisite().getValue()) 
                ? SolrConstants.PK : childLOI.getPrerequisite().getValue());*/

        if(childLOS.getShortTitle() != null) {
            String childName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    childLOI.getTeachingLanguages(), childLOS.getShortTitle().getTranslations());
            doc.setField(LearningOpportunity.CHILD_NAME, childName);
        }

        if(childLOS.getName() != null) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CHILD_NAME_SV, SolrUtil.resolveTextWithFallback("sv", childLOS.getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CHILD_NAME_EN, SolrUtil.resolveTextWithFallback("en", childLOS.getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CHILD_NAME_FI, SolrUtil.resolveTextWithFallback("fi", childLOS.getName().getTranslations()));
            }
        }

        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {

                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV,  SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN,  SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else{
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI,  SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }
        
        if (childLOI.getDegreeTitle() != null) {
        	I18nText i18n = childLOI.getDegreeTitle();
            if (teachLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
            }
//            LOG.warn("degreeTitle added to solr document: "+ resolvedText);
        }

        if (childLOI.getDegreeTitles() != null) {
            for (I18nText i18n : childLOI.getDegreeTitles()) {
	            if (teachLang.equals("sv")) {
	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
	            } else if (teachLang.equals("en")) {
	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
	            } else {
	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
	            }
//	            LOG.warn("degreeTitles added to solr document: "+ resolvedText);
            }
        }

        if (childLOS.getQualification() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.QUALIFICATION_SV,  SolrUtil.resolveTextWithFallback("sv", childLOS.getQualification().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.QUALIFICATION_EN,  SolrUtil.resolveTextWithFallback("en", childLOS.getQualification().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.QUALIFICATION_FI,  SolrUtil.resolveTextWithFallback("fi", childLOS.getQualification().getTranslations()));
            }

        }
        if (childLOS.getQualifications() != null) {
            for (I18nText i18n : childLOS.getQualifications()) {
                if (teachLang.equals("sv")) {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }
        if (childLOS.getGoals() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV,  SolrUtil.resolveTextWithFallback("sv", childLOS.getGoals().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN,  SolrUtil.resolveTextWithFallback("en", childLOS.getGoals().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI,  SolrUtil.resolveTextWithFallback("fi", childLOS.getGoals().getTranslations()));
            }


        }
        if (childLOI.getContent() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", childLOI.getContent().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en", childLOI.getContent().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", childLOI.getContent().getTranslations()));
            }
        }

        if (childLOI.getKoulutuslaji() != null && childLOI.getKoulutuslaji().getName() != null) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", childLOI.getKoulutuslaji().getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en",  childLOI.getKoulutuslaji().getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", childLOI.getKoulutuslaji().getName().getTranslations()));
            }
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                ApplicationSystem curAs = ao.getApplicationSystem();
                doc.addField(LearningOpportunity.AS_NAME_FI, curAs.getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, curAs.getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, curAs.getName().getTranslations().get("en"));

                if (curAs.isShownAsFacet()) {
                    doc.addField(LearningOpportunity.AS_FACET, curAs.getId());
                }
            }
        }

        //SolrUtil.addApplicationDates(doc, childLOI.getApplicationOptions());

        //indexFacetFields(childLOS, childLOI, doc);
    }

    /*
     * Indexes fields used in facet search for ParentLOS learning opportunities
     */
    private void indexFacetFields(ParentLOS parent, SolrInputDocument doc, String prereqVal, String teachLang) {
        List<String> usedVals = new ArrayList<String>();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                String teachingLang = childLOI.getTeachingLanguages().get(0).getValue();
                if (!usedVals.contains(teachingLang)) {
                    doc.addField(LearningOpportunity.TEACHING_LANGUAGE, teachingLang);
                    usedVals.add(teachingLang);
                }
                if (!parent.isKotitalousopetus()
                        && !usedVals.contains(SolrConstants.ED_TYPE_AMMATILLINEN)) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLINEN);
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
                    //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
                    usedVals.add(SolrConstants.ED_TYPE_AMMATILLINEN);
                }
                if ( !parent.isKotitalousopetus()
                        && SolrConstants.PK.equalsIgnoreCase(prereqVal)
                        && childLOI.isKaksoistutkinto()
                        && !usedVals.contains(SolrConstants.ED_TYPE_KAKSOIS)) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
                    usedVals.add(SolrConstants.ED_TYPE_KAKSOIS);
                }
                if (parent.isKotitalousopetus() && !usedVals.contains(SolrConstants.ED_TYPE_MUU)) {
                    usedVals.add(SolrConstants.ED_TYPE_MUU);
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KOTITALOUS);
                    doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_KOTITALOUS_DISPLAY);
                }

                if (childLOI.getPrerequisite().getValue().equals(prereqVal)) {
                    if (childLOI.getFotFacet() != null) {
                        for (Code curCode : childLOI.getFotFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.FORM_OF_TEACHING, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }

                    if (childLOI.getTimeOfTeachingFacet() != null) {
                        for (Code curCode : childLOI.getTimeOfTeachingFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.TIME_OF_TEACHING, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }
                    if (childLOI.getFormOfStudyFacet() != null) {
                        for (Code curCode : childLOI.getFormOfStudyFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.FORM_OF_STUDY, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }
                    if (childLOI.getKoulutuslaji() != null
                            && !usedVals.contains(childLOI.getKoulutuslaji().getUri())) {
                        SolrUtil.addKindOfEducationFields(childLOI, doc);
                        usedVals.add(childLOI.getKoulutuslaji().getUri());
                    }
                }

            }
        }

        if (parent.getTopics() != null) {
            for (Code curTopic : parent.getTopics()) {
                doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV,  SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN,  SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                } else{
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI,  SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                }
            }
        }

        if (parent.getThemes() != null) {
            for (Code curTopic : parent.getThemes()) {
                doc.addField(LearningOpportunity.THEME, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV,  SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN,  SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                } else{
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI,  SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                }
            }
        }

    }

}
