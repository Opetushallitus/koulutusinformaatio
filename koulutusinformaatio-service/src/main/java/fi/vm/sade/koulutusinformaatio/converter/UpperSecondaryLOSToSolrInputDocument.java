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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLOSToSolrInputDocument implements Converter<UpperSecondaryLOS, List<SolrInputDocument>> {

    @Override
    public List<SolrInputDocument> convert(UpperSecondaryLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        UpperSecondaryLOI loi = null;
        
        for (UpperSecondaryLOI curLoi : los.getLois()) {
            if (loi == null) {
                loi = curLoi;
            }
            if (isLater(curLoi, loi)) {
                loi = curLoi;
            }
        }
        
        if (loi != null) {
            docs.add(createDoc(los, loi));
            docs.addAll(fIndexer.createFacetDocs(loi, los));
        }

        return docs;
    }


    /*
     * Checking if curLoi has a later start date than existingLoi. 
     * This is done to ensure that only the latest loi gets indexed into solr.
     */
    private boolean isLater(UpperSecondaryLOI curLoi, UpperSecondaryLOI existingLoi) {
        
        if (curLoi.getStartDate() != null && existingLoi.getStartDate() != null) {
            if (curLoi.getStartDate().after(existingLoi.getStartDate())) {
                return true;
            } else {
                return false;
            }
        } else if (curLoi.getStartDate() != null) {
            String existingLoiSeason = existingLoi.getStartSeason() != null 
                    && existingLoi.getStartSeason().getTranslations() != null 
                    ? existingLoi.getStartSeason().getTranslations().get("fi") : null;
            return isDateLaterThanYearAndSeason(curLoi.getStartDate(), existingLoi.getStartYear(), existingLoiSeason);
        } else if (existingLoi.getStartDate() != null) {
            String curLoiSeason = curLoi.getStartSeason() != null 
                    && curLoi.getStartSeason().getTranslations() != null 
                    ? curLoi.getStartSeason().getTranslations().get("fi") : null;
            return !isDateLaterThanYearAndSeason(existingLoi.getStartDate(), curLoi.getStartYear(), curLoiSeason);
        } else {
            if (curLoi.getStartYear() != existingLoi.getStartYear()) {
                return curLoi.getStartYear() > existingLoi.getStartYear();
            } else {
                String existingLoiSeason = existingLoi.getStartSeason() != null 
                        && existingLoi.getStartSeason().getTranslations() != null 
                        ? existingLoi.getStartSeason().getTranslations().get("fi") : null;
                return !SolrConstants.AUTUMN.equalsIgnoreCase(existingLoiSeason);
            }
        }
    }
    
    /*
     * Checking if a date can be considered to be later than a given year and season.
     * This is needed because some lois don't have explicit start date but only
     *  a year and season are given.
     */
    private boolean isDateLaterThanYearAndSeason(Date date, int year, String season) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int curLoiMonth = cal.get(Calendar.MONTH);
        int curLoiYear = cal.get(Calendar.YEAR);
        if (curLoiYear != year) {
            return curLoiYear > year;
        } else if (SolrConstants.AUTUMN.equalsIgnoreCase(season)
                && curLoiMonth < 7) {
            return false;
        } else {
            return true;
        }
    }



    private SolrInputDocument createDoc(UpperSecondaryLOS los, UpperSecondaryLOI loi) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, los.getId());//loi.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());

        doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(loi.getPrerequisite().getValue()) 
                ? SolrConstants.PK : loi.getPrerequisite().getValue());

        doc.setField(LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, loi.getPrerequisite().getValue());

        if (los.getCreditValue() != null 
                && los.getCreditUnit() != null) {
            doc.addField(LearningOpportunity.CREDITS, String.format("%s %s", los.getCreditValue(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(),
                            los.getCreditUnit().getTranslations())));
        }

        String teachingLang = loi.getTeachingLanguages().isEmpty() ? "EXC" : loi.getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), los.getShortTitle().getTranslations());


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
        
        if (loi.getKoulutuslaji() != null && loi.getKoulutuslaji().getName() != null) {
            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", loi.getKoulutuslaji().getName().getTranslations()));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en",  loi.getKoulutuslaji().getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", loi.getKoulutuslaji().getName().getTranslations()));
            }
        }
        
    	Map<String, String> transls = null;
        if (loi.getDegreeTitle() != null && (transls = loi.getDegreeTitle().getTranslations()) != null) {
            if (teachingLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            }
//            LOG.warn("degreeTitle added to solr document: "+ resolvedText);
        }

        if (loi.getDegreeTitles() != null) {
            for (I18nText i18n : loi.getDegreeTitles()) {
            	if ((transls = i18n.getTranslations()) != null) {
    	            if (teachingLang.equals("sv")) {
    	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", transls));
    	            } else if (teachingLang.equals("en")) {
    	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", transls));
    	            } else {
    	                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", transls));
    	            }
//    	            LOG.warn("degreeTitles added to solr document: "+ resolvedText);
				}
            }
        }

        String aoNameFi = "";
        String aoNameSv = "";
        String aoNameEn = "";

        for (ApplicationOption ao : loi.getApplicationOptions()) {
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

        doc.addField(LearningOpportunity.AO_NAME_FI, aoNameFi);
        doc.addField(LearningOpportunity.AO_NAME_SV, aoNameSv);
        doc.addField(LearningOpportunity.AO_NAME_EN, aoNameEn);
        SolrUtil.addApplicationDates(doc, loi.getApplicationOptions());

        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, loi.getStartDate());
        
        String sortName = String.format("%s, %s",
                SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), 
                        provider.getName().getTranslations()).toLowerCase().trim(),
                SolrUtil.resolveTranslationInTeachingLangUseFallback(loi.getTeachingLanguages(), 
                        los.getShortTitle().getTranslations())).toLowerCase().trim();
        
        doc.addField(LearningOpportunity.NAME_FI_SORT, sortName);
        doc.addField(LearningOpportunity.NAME_SV_SORT, sortName);
        doc.addField(LearningOpportunity.NAME_EN_SORT, sortName);
        doc.addField(LearningOpportunity.NAME_SORT, sortName);


        //For faceting
        indexFacetFields(doc, los, loi, teachingLang);
        SolrUtil.setLopAndHomeplaceDisplaynames(doc, provider, loi.getPrerequisite());

        return doc;
    }

    private void indexFacetFields(SolrInputDocument doc, UpperSecondaryLOS los,  UpperSecondaryLOI loi, String teachLang) {
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, loi.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_LUKIO);
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_NUORTENLUKIO);
        //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        if (loi.isKaksoistutkinto()) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
        }
        
        if (los.getTopics() != null) {
            for (Code curTopic : los.getTopics()) {
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

        if (los.getThemes() != null) {
            for (Code curTopic : los.getThemes()) {
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
        
        List<String> usedVals = new ArrayList<String>();
        
        if (loi.getFotFacet() != null) {
            
            
            
            for (Code curFOT : loi.getFotFacet()) {
                if (!usedVals.contains(curFOT.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_TEACHING, curFOT.getUri());
                    usedVals.add(curFOT.getUri());
                }
            }
        }
        
        if (loi.getTimeOfTeachingFacet() != null) {
            
            for (Code curTimeOfTeachinig : loi.getTimeOfTeachingFacet()) {
                if (!usedVals.contains(curTimeOfTeachinig.getUri())) {
                    doc.addField(LearningOpportunity.TIME_OF_TEACHING, curTimeOfTeachinig.getUri());
                    usedVals.add(curTimeOfTeachinig.getUri());
                }
            }
        }
        
        if (loi.getFormOfStudyFacet() != null) {
            
            for (Code curFormOfStudy : loi.getFormOfStudyFacet()) {
                if (!usedVals.contains(curFormOfStudy.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_STUDY, curFormOfStudy.getUri());
                    usedVals.add(curFormOfStudy.getUri());
                }
            }
        }
        
        if (loi.getKoulutuslaji() != null 
                && !usedVals.contains(loi.getKoulutuslaji().getUri())) {
            
            if (loi.getKoulutuslaji().getUri().startsWith(TarjontaConstants.AVOIN_KAIKILLE)) {
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.NUORTEN_KOULUTUS);
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.AIKUISKOULUTUS);
            } else {
                doc.addField(LearningOpportunity.KIND_OF_EDUCATION, loi.getKoulutuslaji().getUri());
                usedVals.add(loi.getKoulutuslaji().getUri());
            }
        } 
        
    }
}
