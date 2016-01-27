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
import java.util.List;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

/**
 * 
 * @author Markus
 */
public class KoulutusLOSToSolrInputDocment implements Converter<KoulutusLOS, List<SolrInputDocument>> {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusLOSToSolrInputDocment.class);

    @Override
    public List<SolrInputDocument> convert(KoulutusLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        docs.add(createDoc(los));
        docs.addAll(fIndexer.createFacetDocs(los));

        return docs;
    }

    /*
     * Creates a higher education learning opportunity solr document.
     */
    private SolrInputDocument createDoc(KoulutusLOS los) {

        SolrInputDocument doc = new SolrInputDocument();

        doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, los.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        for (ApplicationOption ao : los.getApplicationOptions()) {
            doc.addField(LearningOpportunity.AS_ID, ao.getApplicationSystem().getId());
        }

        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        for (Provider curProv : los.getAdditionalProviders()) {
            doc.addField(LearningOpportunity.LOP_ID, curProv.getId());
        }

        if (los.getFacetPrerequisites() != null && !los.getFacetPrerequisites().isEmpty()) {
            for (Code curPrereq : los.getFacetPrerequisites()) {
                doc.addField(LearningOpportunity.PREREQUISITES, curPrereq.getValue());
            }

            Code prerequisiteCode = los.getKoulutusPrerequisite() == null ? los.getFacetPrerequisites().get(0) : los.getKoulutusPrerequisite();
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                    los.getTeachingLanguages(), prerequisiteCode.getName().getTranslations()
                    ));
            doc.setField(LearningOpportunity.PREREQUISITE_CODE, prerequisiteCode.getValue());
        }

        if (los.getCreditValue() != null && los.getCreditUnit() != null && los.getCreditUnit().getTranslations() != null
                && !los.getCreditUnit().getTranslations().isEmpty()) {

            I18nText unit = los.getCreditUnit();
            if (ToteutustyyppiEnum.KORKEAKOULUOPINTO.equals(los.getToteutustyyppi())) {
                unit = los.getCreditUnitShort();
            }

            doc.addField(LearningOpportunity.CREDITS,
                    String.format("%s %s", los.getCreditValue(),
                            SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                                    unit.getTranslations())));
        }

        String teachingLang = los.getTeachingLanguages().isEmpty() ? "EXC" : los.getTeachingLanguages().get(0).getValue().toLowerCase();

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortTitle().getTranslations());

        doc.setField(LearningOpportunity.NAME, losName);

        if (los.getEducationDegreeLang() != null) {
            doc.setField(LearningOpportunity.EDUCATION_DEGREE,
                    SolrUtil.resolveTextWithFallback(teachingLang,
                            los.getEducationDegreeLang().getTranslations()));
        }
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
            Map<String, String> names = null;
            for (ApplicationOption ao : los.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    ApplicationSystem as = ao.getApplicationSystem();
                    names = as.getName().getTranslations();
                    doc.addField(LearningOpportunity.AS_NAME_FI, SolrUtil.resolveTextWithFallback("fi", names));
                    doc.addField(LearningOpportunity.AS_NAME_SV, SolrUtil.resolveTextWithFallback("sv", names));
                    doc.addField(LearningOpportunity.AS_NAME_EN, SolrUtil.resolveTextWithFallback("en", names));

                    if (as.isShownAsFacet()) {
                        doc.addField(LearningOpportunity.AS_FACET, as.getId());
                    }

                }
                if (ao.getName() != null) {
                    aoNameFi = String.format("%s %s", aoNameFi, SolrUtil.resolveTextWithFallback("fi", ao.getName().getTranslations()));
                    aoNameSv = String.format("%s %s", aoNameSv, SolrUtil.resolveTextWithFallback("sv", ao.getName().getTranslations()));
                    aoNameEn = String.format("%s %s", aoNameEn, SolrUtil.resolveTextWithFallback("en", ao.getName().getTranslations()));

                }

            }

            doc.addField(LearningOpportunity.AO_NAME_FI, aoNameFi);
            doc.addField(LearningOpportunity.AO_NAME_SV, aoNameSv);
            doc.addField(LearningOpportunity.AO_NAME_EN, aoNameEn);

            SolrUtil.addApplicationDates(doc, los.getApplicationOptions());
        }
        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, los.getStartDate());

        setSortFields(los, doc, provider);

        //For faceting
        indexFacetFields(doc, los, teachingLang);

        return doc;
    }

    private void setSortFields(KoulutusLOS los, SolrInputDocument doc,
            Provider provider) {
        if (los.getType().equals(TarjontaConstants.TYPE_ADULT_UPSEC)) {
            String nameSort = String.format("%s, %s",
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                            provider.getName().getTranslations()).toLowerCase().trim(),
                    SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                            los.getShortTitle().getTranslations())).toLowerCase().trim();
            doc.addField(LearningOpportunity.NAME_FI_SORT, nameSort);
            doc.addField(LearningOpportunity.NAME_SV_SORT, nameSort);
            doc.addField(LearningOpportunity.NAME_EN_SORT, nameSort);
            doc.addField(LearningOpportunity.NAME_SORT, nameSort);
        } else {
            doc.addField(LearningOpportunity.NAME_FI_SORT, SolrUtil.resolveTextWithFallback("fi",
                    los.getShortTitle().getTranslations()).toLowerCase().trim());
            doc.addField(LearningOpportunity.NAME_SV_SORT, SolrUtil.resolveTextWithFallback("sv",
                    los.getShortTitle().getTranslations()).toLowerCase().trim());
            doc.addField(LearningOpportunity.NAME_EN_SORT, SolrUtil.resolveTextWithFallback("en",
                    los.getShortTitle().getTranslations()).toLowerCase().trim());
            doc.addField(LearningOpportunity.NAME_SORT, SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                    los.getShortTitle().getTranslations()).toLowerCase().trim());
        }

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
            Map<String, String> transls = provider.getDescription().getTranslations();
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_EN, SolrUtil.resolveTextWithFallback("en", transls));
        }

    }

    private void indexHomeplaceDisplay(Provider provider, KoulutusLOS los, String teachingLang, SolrInputDocument doc) {
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
    private void indexLanguageFields(KoulutusLOS los,
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
            KoulutusLOS los, SolrInputDocument doc, boolean enIndexed) {

        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getShortTitle().getTranslations());

        Provider provider = los.getProvider();
        Map<String, String> transls = los.getName().getTranslations();
        if (teachingLang.equals("fi")) {
            doc.setField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            if (los.getCreditValue() != null && los.getType() != null && los.getType().equals(TarjontaConstants.TYPE_KK)) {
                createDisplayNameForHigherEd("fi", transls, LearningOpportunity.NAME_DISPLAY_FI, doc);
            } else {
                doc.setField(LearningOpportunity.NAME_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            }

        } else if (teachingLang.equals("sv")) {
            doc.setField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            if (los.getCreditValue() != null && los.getType() != null && los.getType().equals(TarjontaConstants.TYPE_KK)) {
                createDisplayNameForHigherEd("sv", transls, LearningOpportunity.NAME_DISPLAY_SV, doc);
            } else {
                doc.setField(LearningOpportunity.NAME_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            }

        } else if (teachingLang.equals("en")) {
            doc.setField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", transls));
            if (los.getCreditValue() != null && los.getType() != null && los.getType().equals(TarjontaConstants.TYPE_KK)) {
                createDisplayNameForHigherEd("en", transls, LearningOpportunity.NAME_DISPLAY_EN, doc);
            } else {
                doc.setField(LearningOpportunity.NAME_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", transls));
            }

        } else if (!enIndexed) {
            doc.setField(LearningOpportunity.NAME_EN, losName);
        }

        if (los.getCreditValue() != null && los.getCreditUnit() != null) {
            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.CREDITS_FI,
                        String.format("%s %s", los.getCreditValue(), SolrUtil.resolveTextWithFallback("fi", los.getCreditUnit().getTranslations())));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.CREDITS_SV,
                        String.format("%s %s", los.getCreditValue(), SolrUtil.resolveTextWithFallback("sv", los.getCreditUnit().getTranslations())));
            } else if (teachingLang.equals("en")) {
                doc.setField(LearningOpportunity.CREDITS_EN,
                        String.format("%s %s", los.getCreditValue(), SolrUtil.resolveTextWithFallback("en", los.getCreditUnit().getTranslations())));
            }
        }

        List<Provider> allProviders = new ArrayList<Provider>();
        allProviders.add(provider);
        allProviders.addAll(los.getAdditionalProviders());

        String homeplaceDisplayFi = null;
        String homeplaceDisplaySv = null;
        String homeplaceDisplayEn = null;

        for (Provider curProv : allProviders) {

            transls = curProv.getName().getTranslations();
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
                transls = curProv.getHomePlace().getTranslations();
                homeplaceDisplayFi = homeplaceDisplayFi != null ? String.format("%s, %s", homeplaceDisplayFi, SolrUtil.resolveTextWithFallback("fi", transls))
                        : SolrUtil.resolveTextWithFallback("fi", transls);
                homeplaceDisplaySv = homeplaceDisplaySv != null ? String.format("%s, %s", homeplaceDisplaySv, SolrUtil.resolveTextWithFallback("sv", transls))
                        : SolrUtil.resolveTextWithFallback("sv", transls);
                homeplaceDisplayEn = homeplaceDisplayEn != null ? String.format("%s, %s", homeplaceDisplayEn, SolrUtil.resolveTextWithFallback("en", transls))
                        : SolrUtil.resolveTextWithFallback("en", transls);
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
                    doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi", transls));
                } else if (teachingLang.equals("sv")) {
                    doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv", transls));
                } else if (teachingLang.equals("en")) {
                    doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en", transls));
                } else {
                    doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                    doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                    doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                }
            }
        }

        if (los.getGoals() != null) {
            transls = los.getGoals().getTranslations();
            if (teachingLang.equals("fi")) {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            } else if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback(teachingLang, transls));
            }

        }
        if (los.getContent() != null) {

            transls = los.getContent().getTranslations();
            if (teachingLang.equals("fi")) {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            } else if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.endsWith("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else if (!enIndexed) {
                doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback(teachingLang, transls));
                doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback(teachingLang, transls));
            }
        }

        if (los.getEducationDegreeLang() != null) {
            transls = los.getEducationDegreeLang().getTranslations();

            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.endsWith("en")) {
                doc.setField(LearningOpportunity.EDUCATION_DEGREE_EN, SolrUtil.resolveTextWithFallback("en", transls));
            }
        }

        if (los.getEducationCode() != null) {
            transls = los.getEducationCode().getName().getTranslations();

            if (teachingLang.equals("fi")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            } else if (teachingLang.equals("sv")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.endsWith("en")) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else if (!enIndexed) {
                doc.setField(LearningOpportunity.EDUCATION_CODE_DISPLAY_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            }
        }

        if (los.getDegreeTitle() != null && los.getDegreeTitle().getTranslations() != null) {
            transls = los.getDegreeTitle().getTranslations();
            if (teachingLang.equals("sv")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_SV, SolrUtil.resolveTextWithFallback("sv", transls));
            } else if (teachingLang.equals("en")) {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_EN, SolrUtil.resolveTextWithFallback("en", transls));
            } else {
                doc.addField(SolrUtil.LearningOpportunity.DEGREE_TITLE_FI, SolrUtil.resolveTextWithFallback("fi", transls));
            }
            //            LOG.warn("degreeTitle added to solr document: "+ resolvedText);
        }

        if (los.getDegreeTitles() != null) {
            for (I18nText i18n : los.getDegreeTitles()) {
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

    }

    private void createDisplayNameForHigherEd(String lang, Map<String, String> nameTransls, String nameDisplayField, SolrInputDocument doc) {
        String translation = nameTransls.get(lang);

        //return translation;
        LOG.debug("Setting display name: " + nameDisplayField + ": " + translation);
        doc.setField(nameDisplayField, translation);

    }

    private void indexFacetFields(SolrInputDocument doc,
            KoulutusLOS los, String teachLang) {

        for (Code teachingLangCode : los.getTeachingLanguages()) {
            String curTeachingLang = teachingLangCode.getValue();
            doc.addField(LearningOpportunity.TEACHING_LANGUAGE, curTeachingLang);
        }

        String educationUri = los.getEducationCode() != null && los.getEducationCode().getUri() != null ? los.getEducationCode().getUri() : "";
        LOG.debug("Education code: {}", educationUri);

        boolean isKaksoistutkinto = false;
        for (ApplicationOption ao : los.getApplicationOptions()) {
            if (ao.isKaksoistutkinto()) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KAKSOIS);
                isKaksoistutkinto = true;
                break;
            }
        }

        if (los.getEducationDegree() != null) {
            if (educationUri.contains(SolrConstants.ED_CODE_AMM_OPETTAJA)
                    || educationUri.contains(SolrConstants.ED_CODE_AMM_ER_OPETTAJA) || educationUri.contains(SolrConstants.ED_CODE_AMM_OPO)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_OPETTAJA);
            } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_AMK)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMK);
            } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_YLEMPI_AMK)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMKS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YLEMPI_AMK);
            } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_KANDI)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_KANDIDAATTI);
            } else if (los.getEducationDegree().contains(TarjontaConstants.ED_DEGREE_URI_MAISTERI)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_YOS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MAISTERI);
            } else if (los.getType().equals(TarjontaConstants.TYPE_ADULT_UPSEC)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_LUKIO);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AIKUISLUKIO);
            } else if (los.getType().equals(TarjontaConstants.TYPE_ADULT_VOCATIONAL)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMM_TUTK);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMMATILLISET);
            } else if (los.getType().equals(TarjontaConstants.TYPE_ADULT_BASE)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AIKUISTEN_PERUSOPETUS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
            }
        }
        if (los.getType().equals(TarjontaConstants.TYPE_KOULUTUS)) {
            if (los.getEducationType().equals(SolrConstants.ED_TYPE_LUKIO)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_LUKIO);
                if (isKaksoistutkinto) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMMATILLISET);
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMMATILLINEN);
                }
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_AMMATILLINEN)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMMATILLISET);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMMATILLINEN);
                if (isKaksoistutkinto) {
                    doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_LUKIO);
                }
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_VALMA)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_PK_JALK);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_VALMA);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_VALMA_ER)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_ERITYIS_JA_VALMENTAVA);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_VALMA_ER);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_TELMA)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_ERITYIS_JA_VALMENTAVA);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_TELMA);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_VALMENTAVA)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_VALMENTAVA);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_KANSANOPISTO)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_KANSANOPISTO);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_TENTH_GRADE)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_PK_JALK);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_TENTH_GRADE);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_IMM_UPSEC)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_MUU);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_PK_JALK);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_IMM_UPSEC);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_AVOIN_YO)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_YOS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AVOIN_YO);
            } else if (los.getEducationType().equals(SolrConstants.ED_TYPE_AVOIN_AMK)) {
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AMKS);
                doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrUtil.SolrConstants.ED_TYPE_AVOIN_AMK);
            }
        }

        if (los.getTopics() != null) {

            for (Code curTopic : los.getTopics()) {
                doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (los.getType().equals(TarjontaConstants.TYPE_KK)) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                } else {
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

        if (los.getThemes() != null) {
            for (Code curTopic : los.getThemes()) {
                doc.addField(LearningOpportunity.THEME, curTopic.getUri());
                I18nText name = curTopic.getName();
                if (los.getType().equals(TarjontaConstants.TYPE_KK)) {
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_SV, SolrUtil.resolveTextWithFallback("sv", name.getTranslations()));
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_EN, SolrUtil.resolveTextWithFallback("en", name.getTranslations()));
                    doc.addField(LearningOpportunity.PROFESSIONAL_TITLES_FI, SolrUtil.resolveTextWithFallback("fi", name.getTranslations()));
                } else {
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

        if (los.getSubjects() != null) {
            if (los.getSubjects().get("fi") != null) {
                for (String curSubject : los.getSubjects().get("fi")) {
                    doc.addField(LearningOpportunity.SUBJECT_FI, curSubject);
                }
            }
            if (los.getSubjects().get("sv") != null) {
                for (String curSubject : los.getSubjects().get("sv")) {
                    doc.addField(LearningOpportunity.SUBJECT_SV, curSubject);
                }
            }
            if (los.getSubjects().get("en") != null) {
                for (String curSubject : los.getSubjects().get("en")) {
                    doc.addField(LearningOpportunity.SUBJECT_EN, curSubject);
                }
            }
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
            SolrUtil.addKindOfEducationFields(los, doc);
        }

    }

}
