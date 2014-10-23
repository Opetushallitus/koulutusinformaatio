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

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Markus
 */
public class HigherEducationLOSToSolrInputDocment implements Converter<StandaloneLOS, List<SolrInputDocument>> {

    @Override
    public List<SolrInputDocument> convert(StandaloneLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        docs.add(createDoc(los));
        docs.addAll(fIndexer.createFacetDocs(los));

        return docs;
    }

    /*
     * Creates a higher education learning opportunity solr document.
     */
    private SolrInputDocument createDoc(StandaloneLOS los) {

        SolrInputDocument doc = new SolrInputDocument();

        doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, los.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());

        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        for (Provider curProv : los.getAdditionalProviders()) {
            doc.addField(LearningOpportunity.LOP_ID, curProv.getId());
        }

        if (los.getFacetPrerequisites() != null && !los.getFacetPrerequisites().isEmpty()) {
            for (Code curPrereq : los.getFacetPrerequisites()) {
                doc.addField(LearningOpportunity.PREREQUISITES, curPrereq.getValue());                
            }
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY, los.getFacetPrerequisites().get(0).getValue());            
        }

        if (los.getCreditValue() != null) {
            doc.addField(LearningOpportunity.CREDITS, 
                    String.format("%s %s", los.getCreditValue(), 
                            SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                                    los.getCreditUnit().getTranslations())));
        }

        String teachingLang = los.getTeachingLanguages().isEmpty() ? "EXC" : los.getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortTitle().getTranslations());


        doc.setField(LearningOpportunity.NAME, losName);


        doc.setField(LearningOpportunity.EDUCATION_DEGREE, 
                SolrUtil.resolveTextWithFallback(teachingLang,  
                        los.getEducationDegreeLang().getTranslations()));

        indexHomeplaceDisplay(provider, los, teachingLang, doc); 



        doc.addField(LearningOpportunity.EDUCATION_DEGREE_CODE, los.getEducationDegree());

        indexLanguageFields(los, doc);


        doc.addField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), provider.getName().getTranslations()));

        for (Provider curProv : los.getAdditionalProviders()) {
            doc.addField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    los.getTeachingLanguages(), curProv.getName().getTranslations()));
        }

        indexAddresses(provider, los.getAdditionalProviders(), doc);

        if (los.getApplicationOptions() != null) {
            String aoNameFi = "";
            String aoNameSv = "";
            String aoNameEn = "";
            Map<String,String> names = null;
            for (ApplicationOption ao : los.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    names = ao.getApplicationSystem().getName().getTranslations();
                    doc.addField(LearningOpportunity.AS_NAME_FI, SolrUtil.resolveTextWithFallback("fi",  names));
                    doc.addField(LearningOpportunity.AS_NAME_SV, SolrUtil.resolveTextWithFallback("sv",  names));
                    doc.addField(LearningOpportunity.AS_NAME_EN, SolrUtil.resolveTextWithFallback("en",  names));
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

            SolrUtil.addApplicationDates(doc, los.getApplicationOptions());
        }
        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, los.getStartDate());
        //indexDurationField(loi, doc);
        doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
                SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), 
                        provider.getName().getTranslations()).toLowerCase().trim(),
                        SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), 
                                los.getShortTitle().getTranslations())).toLowerCase().trim());


        //For faceting
        indexFacetFields(doc, los);

        return doc;
    }


    private void indexAddresses(Provider provider,
            List<Provider> additionalProviders, SolrInputDocument doc) {

        indexProviderAddresses(provider, doc);
        for (Provider curProv : additionalProviders) {
            indexProviderAddresses(curProv, doc);
        }


    }

    private void indexProviderAddresses(Provider provider, SolrInputDocument doc) {
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
            Map<String,String> transls = provider.getDescription().getTranslations();
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_EN, SolrUtil.resolveTextWithFallback("en",  transls));
        }

    }

    private void indexHomeplaceDisplay(Provider provider, StandaloneLOS los, String teachingLang, SolrInputDocument doc) {
        String homePlaceDisplay = null; 
        if (provider.getHomePlace() != null) {
            homePlaceDisplay = SolrUtil.resolveTextWithFallback(teachingLang,
                    provider.getHomePlace().getTranslations());
            /*doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                    );*/
        }

        for (Provider curProv : los.getAdditionalProviders()) {
            if (curProv.getHomePlace() != null) {
                homePlaceDisplay = String.format("%s, %s", homePlaceDisplay, SolrUtil.resolveTextWithFallback(teachingLang,
                        curProv.getHomePlace().getTranslations()));
            }
        }

        if (homePlaceDisplay != null) {
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                    homePlaceDisplay);
        }

    }

    /*
     * Indexes language specific fields according to teaching languages
     * and tries to index fi, sv, and en regardles of teaching languages
     */
    private void indexLanguageFields(StandaloneLOS los,
            SolrInputDocument doc) {

        boolean fiIndexed = false;
        boolean svIndexed = false;
        boolean enIndexed = false;
        for (Code teachingLangCode : los.getTeachingLanguages()) {
            String curTeachingLang = teachingLangCode.getValue().toLowerCase();
            indexLangSpecificFields(curTeachingLang, los, doc, enIndexed);
            if (curTeachingLang.equals("fi")) {
                fiIndexed = true;
            } else if (curTeachingLang.equals("sv")) {
                svIndexed = true;
            } else if (curTeachingLang.equals("en")) {
                enIndexed = true;
            }
        }

        if (!fiIndexed) {
            indexLangSpecificFields("fi", los, doc, fiIndexed);
        }
        if (!svIndexed) {
            indexLangSpecificFields("sv", los, doc, fiIndexed);
        }
        if (!enIndexed) {
            indexLangSpecificFields("en", los, doc, fiIndexed);
        }

    }

    /*
     * Indexes language specific fields according to given teachingLang
     */
    private void indexLangSpecificFields(String teachingLang,
            StandaloneLOS los, SolrInputDocument doc, boolean enIndexed) {

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortTitle().getTranslations());

        Provider provider = los.getProvider();
        Map<String,String> transls = los.getName().getTranslations();
        if (teachingLang.equals("fi")) {
            doc.setField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            doc.setField(LearningOpportunity.NAME_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
        } else if (teachingLang.equals("sv")) {
            doc.setField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            doc.setField(LearningOpportunity.NAME_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", transls));
        } else if (teachingLang.equals("en")) {
            doc.setField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", transls));
            doc.setField(LearningOpportunity.NAME_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", transls));
        } else if (!enIndexed) {
            doc.setField(LearningOpportunity.NAME_EN, losName);
        }

        List<Provider> allProviders = new ArrayList<Provider>();
        allProviders.add(provider);
        allProviders.addAll(los.getAdditionalProviders());
        
        String homeplaceDisplayFi = null;
        String homeplaceDisplaySv = null;
        String homeplaceDisplayEn = null;

        for (Provider curProv : allProviders) {

            transls =  curProv.getName().getTranslations();
            if (teachingLang.equals("fi")) {
                doc.addField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi", transls));
                doc.addField(LearningOpportunity.LOP_NAME_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            } else if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv", transls));
                doc.addField(LearningOpportunity.LOP_NAME_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en", transls));
                doc.addField(LearningOpportunity.LOP_NAME_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else if (!enIndexed) {
                doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback(teachingLang, transls));
            }
            if (curProv.getHomePlace() != null) {
                transls = los.getProvider().getHomePlace().getTranslations();
                homeplaceDisplayFi = homeplaceDisplayFi != null ? String.format("%s, %s", homeplaceDisplayFi, SolrUtil.resolveTextWithFallback("fi",  transls)) : SolrUtil.resolveTextWithFallback("fi",  transls); 
                homeplaceDisplaySv = homeplaceDisplaySv != null ? String.format("%s, %s", homeplaceDisplaySv, SolrUtil.resolveTextWithFallback("sv",  transls)) : SolrUtil.resolveTextWithFallback("sv",  transls); 
                homeplaceDisplayEn = homeplaceDisplayEn != null ? String.format("%s, %s", homeplaceDisplayEn, SolrUtil.resolveTextWithFallback("en",  transls)) : SolrUtil.resolveTextWithFallback("en",  transls); 
            }
                
        }
        
        if (homeplaceDisplayFi != null) {
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_FI, homeplaceDisplayFi);
        }
        if (homeplaceDisplaySv != null) {
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_SV, homeplaceDisplaySv);
        }
        if (homeplaceDisplayEn != null) {
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_EN, homeplaceDisplayEn);
        }


        if (los.getQualifications() != null && !los.getQualifications().isEmpty()) {

            for (I18nText curQualification : los.getQualifications()) {

                transls = curQualification.getTranslations();

                if (teachingLang.equals("fi")) {
                    doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
                } else if (teachingLang.equals("sv")) {
                    doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
                } else if (teachingLang.equals("en")) {
                    doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en",  transls));
                } else {
                    doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
                    doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
                    doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
                }
            }
        }

        if (los.getGoals() != null) {
            transls = los.getGoals().getTranslations();
            if (teachingLang.equals("fi")) {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            } else if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en",  transls));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback(teachingLang,  transls));
            }

        }
        if (los.getContent() != null) {

            transls = los.getContent().getTranslations();
            if (teachingLang.equals("fi")) {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            } else if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            } else if (teachingLang.endsWith("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en",  transls));
            } else if (!enIndexed){
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback(teachingLang, transls));
            }
        }

        if (los.getEducationDegreeLang() != null) {
            transls = los.getEducationDegreeLang().getTranslations();

            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            } else if (teachingLang.endsWith("en")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_EN, SolrUtil.resolveTextWithFallback("en",  transls));
            } 
        }

        if (los.getEducationCode() != null) {
            transls = los.getEducationCode().getName().getTranslations();

            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            } else if (teachingLang.endsWith("en")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en",  transls));
            } else if (!enIndexed) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            }
        }

        

    }

    private void indexFacetFields(SolrInputDocument doc,
            StandaloneLOS los) {

        for (Code teachingLangCode : los.getTeachingLanguages()) {
            String curTeachingLang = teachingLangCode.getValue();
            doc.addField(LearningOpportunity.TEACHING_LANGUAGE, curTeachingLang);
        }

        if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_AMK)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMK);
            //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_YLEMPI_AMK)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YLEMPI_AMK);
            //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_KANDI)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KANDIDAATTI);
            //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_MAISTERI)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MAISTERI);
            //doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getType().equals(TarjontaConstants.TYPE_ADULT_UPSEC)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_LUKIO);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AIKUISLUKIO);
        } else if (los.getType().equals(TarjontaConstants.TYPE_ADULT_VOCATIONAL)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_TUTK);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
        }



        for (Code curTopic : los.getTopics()) {
            doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
        }

        for (Code curTopic : los.getThemes()) {
            doc.addField(LearningOpportunity.THEME, curTopic.getUri());
        }

        if (los.getFotFacet() != null) {
            List<String> usedVals = new ArrayList<String>();
            for (Code curFOT : los.getFotFacet()) {
                if (!usedVals.contains(curFOT.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_TEACHING, curFOT.getUri());
                    usedVals.add(curFOT.getUri());
                }
            }
        }

        if (los.getTimeOfTeachingFacet() != null) {
            List<String> usedVals = new ArrayList<String>();
            for (Code curTimeOfTeaching : los.getTimeOfTeachingFacet()) {
                if (!usedVals.contains(curTimeOfTeaching.getUri())) {
                    doc.addField(LearningOpportunity.TIME_OF_TEACHING, curTimeOfTeaching.getUri());
                    usedVals.add(curTimeOfTeaching.getUri());
                }
            }
        }

        if (los.getFormOfStudyFacet() != null) {
            List<String> usedVals = new ArrayList<String>();
            for (Code curFormOfStudy : los.getFormOfStudyFacet()) {
                if (!usedVals.contains(curFormOfStudy.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_STUDY, curFormOfStudy.getUri());
                    usedVals.add(curFormOfStudy.getUri());
                }
            }
        }

        if (los.getKoulutuslaji() != null) {
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, los.getKoulutuslaji().getUri());
        }

    }

}
