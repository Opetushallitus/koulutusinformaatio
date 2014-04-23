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
public class HigherEducationLOSToSolrInputDocment implements Converter<HigherEducationLOS, List<SolrInputDocument>> {

    @Override
    public List<SolrInputDocument> convert(HigherEducationLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        docs.add(createDoc(los));
        docs.addAll(fIndexer.createFacetDocs(los));

        return docs;
    }

    /*
     * Creates a higher education learning opportunity solr document.
     */
    private SolrInputDocument createDoc(HigherEducationLOS los) {

        SolrInputDocument doc = new SolrInputDocument();

        doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, los.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        if (los.getFacetPrerequisites() != null && !los.getFacetPrerequisites().isEmpty()) {
            for (Code curPrereq : los.getFacetPrerequisites()) {
                doc.addField(LearningOpportunity.PREREQUISITES, curPrereq.getValue());
            }
        }

        if (los.getCreditValue() != null) {
            doc.addField(LearningOpportunity.CREDITS, 
                    String.format("%s %s", los.getCreditValue(), 
                            SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                    los.getCreditUnit().getTranslations())));
        }

        String teachingLang = los.getTeachingLanguages().isEmpty() ? "EXC" : los.getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortName().getTranslations());


        doc.setField(LearningOpportunity.NAME, losName);

        
        doc.setField(LearningOpportunity.EDUCATION_DEGREE, 
                SolrUtil.resolveTextWithFallback(teachingLang,  
                        los.getEducationDegreeLang().getTranslations()));
        
       if (provider.getHomePlace() != null) { 
           doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                   SolrUtil.resolveTextWithFallback(teachingLang,
                           provider.getHomePlace().getTranslations()));
       }
                
        doc.addField(LearningOpportunity.EDUCATION_DEGREE_CODE, los.getEducationDegree());

        indexLanguageFields(los, doc);


        doc.setField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), provider.getName().getTranslations()));


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

        if (los.getApplicationOptions() != null) {
            Map<String,String> names = null;
            for (ApplicationOption ao : los.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    names = ao.getApplicationSystem().getName().getTranslations();
                    doc.addField(LearningOpportunity.AS_NAME_FI, SolrUtil.resolveTextWithFallback("fi",  names));
                    doc.addField(LearningOpportunity.AS_NAME_SV, SolrUtil.resolveTextWithFallback("sv",  names));
                    doc.addField(LearningOpportunity.AS_NAME_EN, SolrUtil.resolveTextWithFallback("en",  names));
                }
            }

            SolrUtil.addApplicationDates(doc, los.getApplicationOptions());
        }
        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, los.getStartDate());
        //indexDurationField(loi, doc);
        doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
                SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), 
                        provider.getName().getTranslations()).toLowerCase().trim(),
                SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), 
                        los.getShortName().getTranslations())).toLowerCase().trim());


        //For faceting
        indexFacetFields(doc, los);

        return doc;
    }

    /*
     * Indexes language specific fields according to teaching languages
     * and tries to index fi, sv, and en regardles of teaching languages
     */
    private void indexLanguageFields(HigherEducationLOS los,
            SolrInputDocument doc) {
        
        boolean fiIndexed = false;
        boolean svIndexed = false;
        boolean enIndexed = false;
        for (Code teachingLangCode : los.getTeachingLanguages()) {
            String curTeachingLang = teachingLangCode.getValue().toLowerCase();
            indexLangSpecificFields(curTeachingLang, los, doc, fiIndexed);
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
            HigherEducationLOS los, SolrInputDocument doc, boolean fiIndexed) {

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortName().getTranslations());

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
        } else if (!fiIndexed) {
            doc.setField(LearningOpportunity.NAME_FI, losName);
        }


        transls =  provider.getName().getTranslations();
        if (teachingLang.equals("fi")) {
            doc.setField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
        } else if (teachingLang.equals("sv")) {
            doc.setField(LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", transls));
        } else if (teachingLang.equals("en")) {
            doc.setField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en", transls));
            doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", transls));
        } else if (!fiIndexed) {
            doc.setField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback(teachingLang, transls));
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
            } else if (!fiIndexed){
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
            } else if (!fiIndexed) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            }
        }
        
        if (los.getProvider().getHomePlace() != null) {
            transls = los.getProvider().getHomePlace().getTranslations();
            
            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi",  transls));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv",  transls));
            } else if (teachingLang.endsWith("en")) {
                doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en",  transls));
            } 
        }
        
    }

    private void indexFacetFields(SolrInputDocument doc,
            HigherEducationLOS los) {

        for (Code teachingLangCode : los.getTeachingLanguages()) {
            String curTeachingLang = teachingLangCode.getValue();
            doc.addField(LearningOpportunity.TEACHING_LANGUAGE, curTeachingLang);
        }
        
        if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_AMK)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMK);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_YLEMPI_AMK)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YLEMPI_AMK);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_KANDI)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KANDIDAATTI);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_MAISTERI)) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MAISTERI);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TUTKINTOON);
        }

        for (Code curTopic : los.getTopics()) {
            doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
        }

        for (Code curTopic : los.getThemes()) {
            doc.addField(LearningOpportunity.THEME, curTopic.getUri());
        }

    }

}
