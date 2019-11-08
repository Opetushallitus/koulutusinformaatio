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

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;

/**
 * @author Hannu Lyytikainen
 */
public class TutkintoLOSToSolrInputDocument implements Converter<TutkintoLOS, List<SolrInputDocument>> {


    public List<SolrInputDocument> convert(TutkintoLOS tutkinto) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();
        docs.addAll(createTutkintoDocsByPrerequisites(tutkinto));
        docs.addAll(fIndexer.createFacetsDocs(tutkinto));
        return docs;
    }

    private List<SolrInputDocument> createTutkintoDocsByPrerequisites(TutkintoLOS tutkinto) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        Map<String,Code> prerequisitesMap = new HashMap<String,Code>();
        for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
            for (Code prereq : koulutus.getPrerequisites()) {
                String prereqStr = prereq.getValue();
                prerequisitesMap.put(prereqStr, prereq);
            }
            // make one, if the prequisite does not exist;
            if (koulutus.getToteutustyyppi() != null && koulutus.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018)) {
                docs.add(createAmmatillinenTutkintoDoc(tutkinto, koulutus.getAmmatillinenPrerequisites()));
            }
        }
        for (Code curPrereq : prerequisitesMap.values()) {
            docs.add(createTutkintoDoc(tutkinto, curPrereq));
        }

        return docs;
    }

    private SolrInputDocument createTutkintoDoc(TutkintoLOS tutkinto, Code prerequisite) {
        return createTutkintoDoc(tutkinto, prerequisite, null);
    }

    private SolrInputDocument createAmmatillinenTutkintoDoc(TutkintoLOS tutkinto, Set<String> ammatillinenPrerequisites) {
        return createTutkintoDoc(tutkinto, null, ammatillinenPrerequisites);
    }

    private SolrInputDocument createTutkintoDoc(TutkintoLOS tutkinto, Code prerequisite, Set<String> ammatillinenPrerequisites) {
        SolrInputDocument doc = new SolrInputDocument();
        Provider provider = tutkinto.getProvider();
        doc.addField(LearningOpportunity.TYPE, tutkinto.getType());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        List<Code> languages = Lists.newArrayList(tutkinto.getTeachingLanguages());

        if (prerequisite != null) {
            doc.addField(LearningOpportunity.ID, String.format("%s#%s", tutkinto.getId(), prerequisite.getValue()));

            doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(prerequisite.getValue())
                    ? SolrConstants.PK : prerequisite.getValue());
            String prerequisiteText = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    languages, prerequisite.getName().getTranslations()
            );

            doc.setField(LearningOpportunity.PREREQUISITE, prerequisiteText);
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY, prerequisiteText);
            doc.addField(LearningOpportunity.PREREQUISITE_CODE, prerequisite.getValue());
        } else {
            doc.addField(LearningOpportunity.ID, tutkinto.getId());
            if (null != ammatillinenPrerequisites) {
                doc.setField(LearningOpportunity.PREREQUISITES, ammatillinenPrerequisites);
            }
        }

        String teachLang = tutkinto.getTeachingLanguages().isEmpty() ? "EXC" : tutkinto.getTeachingLanguages().iterator().next().getValue().toLowerCase();

        String parentName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                languages,
                tutkinto.getName().getTranslations()
        );
        doc.setField(LearningOpportunity.NAME, parentName);


        try {
            KoulutusLOS latest = tutkinto.getLatestLoi();
            String cv = latest.getCreditValue();
            Map<String, String> cu = latest.getCreditUnit().getTranslations();
            doc.addField(
                    LearningOpportunity.CREDITS,
                    String.format("%s %s", cv, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                            languages, cu
                    ))
            );
        } catch (Exception e) {
            doc.addField(
                    LearningOpportunity.CREDITS,
                    String.format("%s %s", tutkinto.getCreditValue(),
                            SolrUtil.resolveTranslationInTeachingLangUseFallback(languages, tutkinto.getCreditUnit().getTranslations())));
        }

        doc.addField(LearningOpportunity.NAME_FI_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_SV_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_EN_SORT, parentName.toLowerCase().trim());
        doc.addField(LearningOpportunity.NAME_SORT, parentName.toLowerCase().trim());

        if (teachLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", tutkinto.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", tutkinto.getName().getTranslations()));
        } else if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", tutkinto.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", tutkinto.getName().getTranslations()));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", tutkinto.getName().getTranslations()));
            doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", tutkinto.getName().getTranslations()));
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
        if (tutkinto.getGoals() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, tutkinto.getGoals().getTranslations().get("sv"));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, tutkinto.getGoals().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, tutkinto.getGoals().getTranslations().get("fi"));
            }
        }

        Set<ApplicationOption> applicationOptions = Sets.newHashSet();
        String aoNameFi = "";
        String aoNameSv = "";
        String aoNameEn = "";
        for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
            if(koulutus.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018)){
                // add all
                applicationOptions.addAll(koulutus.getApplicationOptions());
                for (ApplicationOption ao : koulutus.getApplicationOptions()) {
                    if (ao.getApplicationSystem() != null) {
                        ApplicationSystem curAs = ao.getApplicationSystem();
                        doc.addField(LearningOpportunity.AS_NAME_FI, curAs.getName().getTranslations().get("fi"));
                        doc.addField(LearningOpportunity.AS_NAME_SV, curAs.getName().getTranslations().get("sv"));
                        doc.addField(LearningOpportunity.AS_NAME_EN, curAs.getName().getTranslations().get("en"));

                        if (curAs.isShownAsFacet()) {
                            doc.addField(LearningOpportunity.AS_FACET, curAs.getId());
                        }
                        doc.addField(LearningOpportunity.AS_ID, curAs.getId());
                        if (curAs.isSiirtohaku())
                            doc.addField(LearningOpportunity.SIIRTOHAKU, true);
                    }
                    if (ao.getName() != null) {
                        aoNameFi = String.format("%s %s", aoNameFi, SolrUtil.resolveTextWithFallback("fi", ao.getName().getTranslations()));
                        aoNameSv = String.format("%s %s", aoNameSv, SolrUtil.resolveTextWithFallback("sv", ao.getName().getTranslations()));
                        aoNameEn = String.format("%s %s", aoNameEn, SolrUtil.resolveTextWithFallback("en", ao.getName().getTranslations()));
                    }
                }
            } else if (null != prerequisite) {
                for (Code prereq : koulutus.getPrerequisites()) {
                    if (prereq.getValue().equals(prerequisite.getValue())) {
                        applicationOptions.addAll(koulutus.getApplicationOptions());
                        for (ApplicationOption ao : koulutus.getApplicationOptions()) {
                            if (ao.getApplicationSystem() != null) {
                                ApplicationSystem curAs = ao.getApplicationSystem();
                                doc.addField(LearningOpportunity.AS_NAME_FI, curAs.getName().getTranslations().get("fi"));
                                doc.addField(LearningOpportunity.AS_NAME_SV, curAs.getName().getTranslations().get("sv"));
                                doc.addField(LearningOpportunity.AS_NAME_EN, curAs.getName().getTranslations().get("en"));

                                if (curAs.isShownAsFacet()) {
                                    doc.addField(LearningOpportunity.AS_FACET, curAs.getId());
                                }
                                doc.addField(LearningOpportunity.AS_ID, curAs.getId());
                                if (curAs.isSiirtohaku())
                                    doc.addField(LearningOpportunity.SIIRTOHAKU, true);

                            }
                            if (ao.getName() != null) {
                                aoNameFi = String.format("%s %s", aoNameFi, SolrUtil.resolveTextWithFallback("fi", ao.getName().getTranslations()));
                                aoNameSv = String.format("%s %s", aoNameSv, SolrUtil.resolveTextWithFallback("sv", ao.getName().getTranslations()));
                                aoNameEn = String.format("%s %s", aoNameEn, SolrUtil.resolveTextWithFallback("en", ao.getName().getTranslations()));
                            }
                        }
                    }
                }
            }
        }
        doc.addField(LearningOpportunity.AO_NAME_FI, aoNameFi);
        doc.addField(LearningOpportunity.AO_NAME_SV, aoNameSv);
        doc.addField(LearningOpportunity.AO_NAME_EN, aoNameEn);
        SolrUtil.addApplicationDates(doc, applicationOptions);

        Date earliest = null;

        for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
                if (koulutus.getStartDate() != null) {
                    if (earliest == null || earliest.after(koulutus.getStartDate())) {
                        earliest = koulutus.getStartDate();
                    }
                }
            }
        doc.setField(LearningOpportunity.START_DATE_SORT, earliest);

        String preValue = "";
        if(prerequisite != null){
            preValue = prerequisite.getValue();
        } else if (!ammatillinenPrerequisites.isEmpty()) {
            preValue = ammatillinenPrerequisites.stream().collect(Collectors.joining(", "));
        }
        indexFacetFields(tutkinto, doc, preValue, teachLang);
        if(prerequisite == null){
            for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
                indexChildFields(doc, koulutus, teachLang);
            }
        } else {
            for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
                for (Code prereq : koulutus.getPrerequisites()) {
                    if (prereq.getValue().equals(prerequisite.getValue())) {
                        indexChildFields(doc, koulutus, teachLang);
                    }
                }
            }
        }

        return doc;
    }






    private void indexChildFields(SolrInputDocument doc, KoulutusLOS koulutusLOS, String teachLang) {

        if (koulutusLOS.getShortTitle() != null) {
            String childName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    koulutusLOS.getTeachingLanguages(), koulutusLOS.getShortTitle().getTranslations());
            doc.setField(LearningOpportunity.CHILD_NAME, childName);
        }

        if (koulutusLOS.getName() != null) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CHILD_NAME_SV, SolrUtil.resolveTextWithFallback("sv", koulutusLOS.getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CHILD_NAME_EN, SolrUtil.resolveTextWithFallback("en", koulutusLOS.getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CHILD_NAME_FI, SolrUtil.resolveTextWithFallback("fi", koulutusLOS.getName().getTranslations()));
            }
        }

        if (koulutusLOS.getProfessionalTitles() != null) {
            for (I18nText i18n : koulutusLOS.getProfessionalTitles()) {

                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV,  SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN,  SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else{
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI,  SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }
        if (koulutusLOS.getDegreeTitle() != null) {
            I18nText i18n = koulutusLOS.getDegreeTitle();
            if (teachLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
            }
        }

        if (koulutusLOS.getDegreeTitles() != null) {
            for (I18nText i18n : koulutusLOS.getDegreeTitles()) {
                if (teachLang.equals("sv")) {
                    doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else {
                    doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }

        if (koulutusLOS.getQualifications() != null) {
            for (I18nText i18n : koulutusLOS.getQualifications()) {
                if (teachLang.equals("sv")) {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv", i18n.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en", i18n.getTranslations()));
                } else {
                    doc.addField(SolrUtil.LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi", i18n.getTranslations()));
                }
            }
        }
        if (koulutusLOS.getGoals() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv", koulutusLOS.getGoals().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en", koulutusLOS.getGoals().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi", koulutusLOS.getGoals().getTranslations()));
            }


        }
        if (koulutusLOS.getContent() != null) {

            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv", koulutusLOS.getContent().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en", koulutusLOS.getContent().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi", koulutusLOS.getContent().getTranslations()));
            }
        }

        if (koulutusLOS.getKoulutuslaji() != null && koulutusLOS.getKoulutuslaji().getName() != null) {
            if (teachLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv", koulutusLOS.getKoulutuslaji().getName().getTranslations()));
            } else if (teachLang.equals("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en", koulutusLOS.getKoulutuslaji().getName().getTranslations()));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi", koulutusLOS.getKoulutuslaji().getName().getTranslations()));
            }
        }

        for (ApplicationOption ao : koulutusLOS.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                ApplicationSystem curAs = ao.getApplicationSystem();
                doc.addField(LearningOpportunity.AS_NAME_FI, curAs.getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, curAs.getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, curAs.getName().getTranslations().get("en"));

                if (curAs.isShownAsFacet()) {
                    doc.addField(LearningOpportunity.AS_FACET, curAs.getId());
                }
                if(curAs.isSiirtohaku())
                    doc.addField(LearningOpportunity.SIIRTOHAKU, true);

            }
        }
    }

    /*
     * Indexes fields used in facet search for ParentLOS learning opportunities
     */
    private void indexFacetFields(TutkintoLOS tutkinto, SolrInputDocument doc, String prerequisite, String teachLang) {
        List<String> usedVals = new ArrayList<String>();
        for (KoulutusLOS koulutus : tutkinto.getChildEducations()) {
            String teachingLang = koulutus.getTeachingLanguages().get(0).getValue();
            if (!usedVals.contains(teachingLang)) {
                doc.addField(LearningOpportunity.TEACHING_LANGUAGE, teachingLang);
                usedVals.add(teachingLang);
            }
            if (!usedVals.contains(koulutus.getEducationType())) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, koulutus.getEducationType());
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
                usedVals.add(koulutus.getEducationType());
            }

            boolean isKaksoistutkinto = false;
            for (ApplicationOption ao : koulutus.getApplicationOptions()) {
                if (ao.isKaksoistutkinto()) {
                    isKaksoistutkinto = true;
                }
            }

            if (prerequisite.toLowerCase().contains(SolrConstants.PK)
                    && isKaksoistutkinto
                    && !usedVals.contains(SolrConstants.ED_TYPE_KAKSOIS)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
                usedVals.add(SolrConstants.ED_TYPE_KAKSOIS);
            }

            if(prerequisite.equals("")){
                setAdditionalFacets(koulutus, usedVals, doc);
            } else {
                for (Code prereq : koulutus.getPrerequisites()) {
                    if (prereq.getValue().equals(prerequisite)) {
                        setAdditionalFacets(koulutus, usedVals, doc);
                    }
                }
            }
        }

        if (tutkinto.getTopics() != null) {
            for (Code curTopic : tutkinto.getTopics()) {
                doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                } else {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                }
            }
        }

        if (tutkinto.getThemes() != null) {
            for (Code curTopic : tutkinto.getThemes()) {
                doc.addField(LearningOpportunity.THEME, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (teachLang.equals("sv")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                } else if (teachLang.equals("en")) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                } else {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                }
            }
        }

    }

    private void setAdditionalFacets(KoulutusLOS koulutus, List<String> usedVals, SolrInputDocument doc) {
        if (koulutus.getFotFacet() != null) {
            for (Code curCode : koulutus.getFotFacet()) {
                if (!usedVals.contains(curCode.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_TEACHING, curCode.getUri());
                    usedVals.add(curCode.getUri());
                }
            }
        }

        if (koulutus.getTimeOfTeachingFacet() != null) {
            for (Code curCode : koulutus.getTimeOfTeachingFacet()) {
                if (!usedVals.contains(curCode.getUri())) {
                    doc.addField(LearningOpportunity.TIME_OF_TEACHING, curCode.getUri());
                    usedVals.add(curCode.getUri());
                }
            }
        }
        if (koulutus.getFormOfStudyFacet() != null) {
            for (Code curCode : koulutus.getFormOfStudyFacet()) {
                if (!usedVals.contains(curCode.getUri())) {
                    doc.addField(LearningOpportunity.FORM_OF_STUDY, curCode.getUri());
                    usedVals.add(curCode.getUri());
                }
            }
        }
        if (koulutus.getKoulutuslaji() != null
                && !usedVals.contains(koulutus.getKoulutuslaji().getUri())) {
            SolrUtil.addKindOfEducationFields(koulutus, doc);
            usedVals.add(koulutus.getKoulutuslaji().getUri());
        }
    }

}
