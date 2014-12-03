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
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class SpecialLOSToSolrInputDocument implements Converter<SpecialLOS, List<SolrInputDocument>> {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialLOSToSolrInputDocument.class);

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
        
        if (specialLOS.getEducationTypeUri() != null 
                && specialLOS.getEducationTypeUri().equals(TarjontaConstants.KANSANOPISTO_TYPE)) {
            doc.addField(SolrUtil.LearningOpportunity.PREREQUISITES, childLOI.getPrerequisite().getValue());
        } else {
            doc.addField(SolrUtil.LearningOpportunity.PREREQUISITES, SolrConstants.PK);
        }

        if (specialLOS.getCreditValue() != null
                && specialLOS.getCreditUnit() != null) {
            doc.addField(SolrUtil.LearningOpportunity.CREDITS, String.format("%s %s", specialLOS.getCreditValue(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(childLOI.getTeachingLanguages(),
                            specialLOS.getCreditUnit().getTranslations())));
        }

        doc.setField(SolrUtil.LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.setField(SolrUtil.LearningOpportunity.PREREQUISITE_DISPLAY, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField(SolrUtil.LearningOpportunity.PREREQUISITE_CODE, childLOI.getPrerequisite().getValue());

        String teachingLang = childLOI.getTeachingLanguages().isEmpty() ? "EXC" : childLOI.getTeachingLanguages().get(0).getValue().toLowerCase();
        String losName = null;
        if (specialLOS.getType().equals(TarjontaConstants.TYPE_SPECIAL)) {
            losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(childLOI.getTeachingLanguages(), 
                    specialLOS.getName().getTranslations());
        } else {
            losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), specialLOS.getShortTitle().getTranslations());
        }


        doc.setField(SolrUtil.LearningOpportunity.NAME, losName);
        doc.addField(SolrUtil.LearningOpportunity.NAME_FI_SORT, losName.toLowerCase().trim());
        doc.addField(SolrUtil.LearningOpportunity.NAME_SV_SORT, losName.toLowerCase().trim());
        doc.addField(SolrUtil.LearningOpportunity.NAME_EN_SORT, losName.toLowerCase().trim());
        doc.addField(SolrUtil.LearningOpportunity.NAME_SORT, losName.toLowerCase().trim());

        if (teachingLang.equals("fi")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", specialLOS.getName().getTranslations()));
        } else if (teachingLang.equals("sv")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", specialLOS.getName().getTranslations()));
        } else if (teachingLang.equals("en")) {
            doc.addField(SolrUtil.LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", specialLOS.getName().getTranslations()));
        } else {
            doc.addField(SolrUtil.LearningOpportunity.NAME_FI, losName);
        }
        
        
        if (provider.getHomePlace() != null) { 
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                    SolrUtil.resolveTextWithFallback(teachingLang,
                            provider.getHomePlace().getTranslations()));
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
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_SV, 
                             SolrUtil.resolveTextWithFallback("sv", specialLOS.getQualification().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_EN, 
                             SolrUtil.resolveTextWithFallback("en", specialLOS.getQualification().getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_FI, 
                             SolrUtil.resolveTextWithFallback("fi", specialLOS.getQualification().getTranslations()));
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
        
        if (childLOI.getKoulutuslaji() != null && childLOI.getKoulutuslaji().getName() != null) {
            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", childLOI.getKoulutuslaji().getName().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en",  childLOI.getKoulutuslaji().getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", childLOI.getKoulutuslaji().getName().getTranslations()));
            }
        }

        String aoNameFi = "";
        String aoNameSv = "";
        String aoNameEn = "";
        
        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(SolrUtil.LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
            if (ao.getName() != null) {
                aoNameFi = String.format("%s %s", aoNameFi,  SolrUtil.resolveTextWithFallback("fi", ao.getName().getTranslations()));
                aoNameSv = String.format("%s %s", aoNameSv,  SolrUtil.resolveTextWithFallback("sv", ao.getName().getTranslations()));
                aoNameEn = String.format("%s %s", aoNameEn,  SolrUtil.resolveTextWithFallback("en", ao.getName().getTranslations()));
            }
        }
        
        doc.addField(LearningOpportunity.AO_NAME_FI, aoNameFi);
        doc.addField(LearningOpportunity.AO_NAME_SV, aoNameSv);
        doc.addField(LearningOpportunity.AO_NAME_EN, aoNameEn);

        SolrUtil.addApplicationDates(doc, childLOI.getApplicationOptions());

        doc.addField(SolrUtil.LearningOpportunity.START_DATE_SORT, childLOI.getStartDate());
        indexFacetFields(childLOI, specialLOS, doc, teachingLang);
        SolrUtil.setLopAndHomeplaceDisplaynames(doc, provider, childLOI.getPrerequisite());

        return doc;
    }

    /*
     * Indexes fields used in facet search for ChildLOS
     */
    private void indexFacetFields(ChildLOI childLOI, SpecialLOS specialLOS, SolrInputDocument doc, String teachLang) {
        doc.addField(SolrUtil.LearningOpportunity.TEACHING_LANGUAGE, childLOI.getTeachingLanguages().get(0).getValue());
       
        if (specialLOS.getType().equals(TarjontaConstants.TYPE_REHAB)) {
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_VALMENTAVA);
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
        } else if (specialLOS.getType().equals(TarjontaConstants.TYPE_PREP)) {
            if (specialLOS.getEducationTypeUri().equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE)) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_VOC_PREP);
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_VOC_PREP);
            } else if (specialLOS.getEducationTypeUri().equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_TENTH_GRADE);
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_TENTH_GRADE);
            } else if (specialLOS.getEducationTypeUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_IMM_UPSEC);
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_IMM_UPSEC);
            } else if (specialLOS.getEducationTypeUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_IMM_VOC);
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_IMM_VOC);
            } else if (specialLOS.getEducationTypeUri().equals(TarjontaConstants.KANSANOPISTO_TYPE)) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_KANSANOPISTO);
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrUtil.SolrConstants.ED_TYPE_KANSANOPISTO);
            } 
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
            if (!(specialLOS.getEducationTypeUri().equals(TarjontaConstants.KANSANOPISTO_TYPE))) {
                doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_PK_JALK);
            }
        }
        else {
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMM_ER);
            doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMMATILLISET);
            //doc.addField(SolrUtil.LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_TUTKINTOON);
        }
        
        List<String> usedVals = new ArrayList<String>();
        if (childLOI.getFotFacet() != null) {
            for (Code curFOT : childLOI.getFotFacet()) {
                if (!usedVals.contains(curFOT.getUri())) {
                    doc.addField(SolrUtil.LearningOpportunity.FORM_OF_TEACHING, curFOT.getUri());
                    usedVals.add(curFOT.getUri());
                }
            }
        }
        
        if (childLOI.getTimeOfTeachingFacet() != null) {
            for (Code curTimeOfTeaching : childLOI.getTimeOfTeachingFacet()) {
                if (!usedVals.contains(curTimeOfTeaching.getUri())) {
                    doc.addField(SolrUtil.LearningOpportunity.TIME_OF_TEACHING, curTimeOfTeaching.getUri());
                    usedVals.add(curTimeOfTeaching.getUri());
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
        
        if (specialLOS.getTopics() != null) {
            for (Code curTopic : specialLOS.getTopics()) {
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

        if (specialLOS.getThemes() != null) {
            for (Code curTopic : specialLOS.getThemes()) {
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
        
        if (childLOI.getKoulutuslaji() != null 
                && !usedVals.contains(childLOI.getKoulutuslaji().getUri())) {
            
            if (childLOI.getKoulutuslaji().getUri().startsWith(TarjontaConstants.AVOIN_KAIKILLE)) {
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.NUORTEN_KOULUTUS);
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.AIKUISKOULUTUS);
            } else {
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, childLOI.getKoulutuslaji().getUri());
                usedVals.add(childLOI.getKoulutuslaji().getUri());
            }
        } 
        
       
        
    }


}
