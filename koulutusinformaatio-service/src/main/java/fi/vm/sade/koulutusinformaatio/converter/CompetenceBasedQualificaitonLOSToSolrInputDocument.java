package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;

public class CompetenceBasedQualificaitonLOSToSolrInputDocument implements Converter<CompetenceBasedQualificationParentLOS, List<SolrInputDocument>>{

    @Override
    public List<SolrInputDocument> convert(CompetenceBasedQualificationParentLOS los) {
        
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();
        docs.add(createParentDoc(los));
        docs.addAll(fIndexer.createFacetsDocs(los));

        return docs;
    }

    private SolrInputDocument createParentDoc(
            CompetenceBasedQualificationParentLOS los) {
        
        SolrInputDocument doc = new SolrInputDocument();
        
        doc.addField(LearningOpportunity.ID, los.getId());
        doc.addField(LearningOpportunity.TYPE, los.getType());
        
        Provider provider = null;
        
        for (AdultVocationalLOS curChild : los.getChildren()) {
            provider = curChild.getProvider();
        }
        
        if (provider != null) {
            doc.addField(LearningOpportunity.LOP_ID, provider.getId());
            
        }
        
        String teachLang = los.getChildren().get(0).getTeachingLanguages().isEmpty() ? "EXC" : los.getChildren().get(0).getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = String.format("%s, %s", SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getChildren().get(0).getTeachingLanguages(), 
                los.getName().getTranslations()), SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getChildren().get(0).getTeachingLanguages(), 
                        los.getEducationKind().getTranslations()).toLowerCase());
        losName = (los.getDeterminer() != null) && !los.isOsaamisala() ? String.format("%s, %s" , losName, los.getDeterminer()) : losName;
        doc.setField(LearningOpportunity.NAME, losName);
        doc.addField(LearningOpportunity.NAME_SORT, losName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_FI_SORT, losName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_SV_SORT, losName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_EN_SORT, losName.toLowerCase().trim());
        if (teachLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, String.format("%s, %s", SolrUtil.resolveTextWithFallback("fi", los.getName().getTranslations()), SolrUtil.resolveTextWithFallback("fi", los.getEducationKind().getTranslations()).toLowerCase()));
        } else if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, String.format("%s, %s", SolrUtil.resolveTextWithFallback("sv", los.getName().getTranslations()), SolrUtil.resolveTextWithFallback("sv", los.getEducationKind().getTranslations())).toLowerCase());
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, String.format("%s, %s", SolrUtil.resolveTextWithFallback("en", los.getName().getTranslations()), SolrUtil.resolveTextWithFallback("en", los.getEducationKind().getTranslations()).toLowerCase()));
        } else {
            doc.addField(LearningOpportunity.NAME_FI, losName);
        }
        
        if (provider.getHomePlace() != null) { 
            doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY, 
                    SolrUtil.resolveTextWithFallback(teachLang,
                            provider.getHomePlace().getTranslations()));
        }

        SolrUtil.setLopAndHomeplaceDisplaynames(doc, provider, null);

        
        SolrUtil.indexLopName(doc, provider, teachLang);
        

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
        if (los.getGoals() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, los.getGoals().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, los.getGoals().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, los.getGoals().getTranslations().get("fi"));
            }
        }
        
        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        
        for (AdultVocationalLOS curChild : los.getChildren()) {
            applicationOptions.addAll(curChild.getApplicationOptions());
            for (ApplicationOption ao : curChild.getApplicationOptions()) {
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
        }
            
        
        SolrUtil.addApplicationDates(doc, applicationOptions);

        Date earliest = null;

        for (AdultVocationalLOS curChild : los.getChildren()) {
            if (earliest == null || curChild.getStartDate() == null || earliest.after(curChild.getStartDate())) {
                earliest = curChild.getStartDate();
            }    
        }
        doc.setField(LearningOpportunity.START_DATE_SORT, earliest);
        
        indexFacetFields(los, doc, teachLang);
        if (los.getChildren() != null) {
            for (AdultVocationalLOS curChild : los.getChildren()) {
            //for (ChildLOI childLOI : childLOS.getLois()) {
            //    if (childLOI.getPrerequisite() != null && childLOI.getPrerequisite().getValue().equals(prerequisite.getValue())) {
                if (curChild != null) {
                    indexChildFields(doc, curChild, teachLang, los.getDeterminer());
                }
                //}
            //}
            }
        }
        
        
        
        return doc;
    }
    
    private void indexChildFields(SolrInputDocument doc,
            AdultVocationalLOS curChild, String teachLang, String determiner) {
        if (curChild.getShortTitle() != null 
                && curChild.getShortTitle().getTranslations() != null 
                && !curChild.getShortTitle().getTranslations().isEmpty()) {
            String childName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    curChild.getTeachingLanguages(), curChild.getShortTitle().getTranslations());
            childName = (determiner != null) ? String.format("%s, %s", childName, determiner) : childName;
            doc.setField(LearningOpportunity.CHILD_NAME, childName);
        }
        
        if (curChild.getEducationDegreeLang() != null 
                && curChild.getEducationDegreeLang().getTranslations() != null 
                && !curChild.getEducationDegreeLang().getTranslations().isEmpty()) {
            doc.setField(LearningOpportunity.EDUCATION_DEGREE, 
                    SolrUtil.resolveTextWithFallback(teachLang,  
                            curChild.getEducationDegreeLang().getTranslations()));
        }
        
        
        if (curChild.getEducationDegree() != null) {        
            doc.addField(LearningOpportunity.EDUCATION_DEGREE_CODE, curChild.getEducationDegree());
        }


        if (curChild.getName() != null 
                && curChild.getName().getTranslations() != null 
                && !curChild.getName().getTranslations().isEmpty()) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CHILD_NAME_SV, SolrUtil.resolveTextWithFallback("sv", curChild.getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CHILD_NAME_EN, SolrUtil.resolveTextWithFallback("en", curChild.getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CHILD_NAME_FI, SolrUtil.resolveTextWithFallback("fi", curChild.getName().getTranslations()));
            }
        }

        if (curChild.getContent() != null
                && curChild.getContent().getTranslations() != null
                && !curChild.getContent().getTranslations().isEmpty()) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", curChild.getContent().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en", curChild.getContent().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", curChild.getContent().getTranslations()));
            }
        }

       
        
        if (curChild.getApplicationOptions() != null) {
            String aoNameFi = "";
            String aoNameSv = "";
            String aoNameEn = "";
            
            for (ApplicationOption ao : curChild.getApplicationOptions()) {
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
            
        }
        
        if (curChild.getKoulutuslaji() != null && curChild.getKoulutuslaji().getName() != null) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV,  SolrUtil.resolveTextWithFallback("sv", curChild.getKoulutuslaji().getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN,  SolrUtil.resolveTextWithFallback("en",  curChild.getKoulutuslaji().getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI,  SolrUtil.resolveTextWithFallback("fi", curChild.getKoulutuslaji().getName().getTranslations()));
            }
        }
        
    }

    /*
     * Indexes fields used in facet search for ParentLOS learning opportunities
     */
    private void indexFacetFields(CompetenceBasedQualificationParentLOS los, SolrInputDocument doc, String teachLang) {
        List<String> usedVals = new ArrayList<String>();
            for (AdultVocationalLOS curChild : los.getChildren()) {
                String teachingLang = curChild.getTeachingLanguages().get(0).getValue();
                if (!usedVals.contains(teachingLang)) {
                    doc.addField(LearningOpportunity.TEACHING_LANGUAGE, teachingLang);
                    usedVals.add(teachingLang);
                }
                
                

                
                    if (curChild.getFotFacet() != null) {
                        for (Code curCode : curChild.getFotFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.FORM_OF_TEACHING, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }

                    if (curChild.getTimeOfTeachingFacet() != null) {
                        for (Code curCode : curChild.getTimeOfTeachingFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.TIME_OF_TEACHING, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }
                    if (curChild.getFormOfStudyFacet() != null) {
                        for (Code curCode : curChild.getFormOfStudyFacet()) {
                            if (!usedVals.contains(curCode.getUri())) {
                                doc.addField(LearningOpportunity.FORM_OF_STUDY, curCode.getUri());
                                usedVals.add(curCode.getUri());
                            }
                        }
                    }
                    if (curChild.getKoulutuslaji() != null 
                            && !usedVals.contains(curChild.getKoulutuslaji().getUri())) {
                        doc.addField(LearningOpportunity.KIND_OF_EDUCATION, curChild.getKoulutuslaji().getUri());
                        usedVals.add(curChild.getKoulutuslaji().getUri());
                    }
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
        
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
        
        if (los.getEdtUri().contains("koulutustyyppi_11")) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_TUTK);
            doc.addField(LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrConstants.ED_TYPE_AMM_TUTK);
        } else if (los.getEdtUri().contains("koulutustyyppi_12")) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_TUTK_ER);
            doc.addField(LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrConstants.ED_TYPE_AMM_TUTK_ER);
        } else if (los.getEdtUri().contains("koulutustyyppi_13")) {
            doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLINEN);
            doc.addField(LearningOpportunity.EDUCATION_TYPE_DISPLAY, SolrConstants.ED_TYPE_AMMATILLINEN_NAYTTO);
        }

    }

}
