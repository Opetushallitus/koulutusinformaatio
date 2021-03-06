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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.DisMaxParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public final class SolrUtil {

    public static final List<String> FIELDS = Lists.newArrayList(
            LearningOpportunity.TEXT_FI,
            LearningOpportunity.TEXT_SV,
            LearningOpportunity.TEXT_EN,
            LearningOpportunity.TEXT_FI_WHOLE,
            LearningOpportunity.TEXT_SV_WHOLE,
            LearningOpportunity.TEXT_EN_WHOLE,
            LearningOpportunity.TEXT_BOOST_FI,
            LearningOpportunity.TEXT_BOOST_SV,
            LearningOpportunity.TEXT_BOOST_EN,
            LearningOpportunity.TEXT_BOOST_FI_WHOLE,
            LearningOpportunity.TEXT_BOOST_SV_WHOLE,
            LearningOpportunity.TEXT_BOOST_EN_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.NAME_AUTO_FI,
            LearningOpportunity.NAME_AUTO_SV,
            LearningOpportunity.NAME_AUTO_EN
    );

    public static final List<String> FIELDS_FI = Lists.newArrayList(
            LearningOpportunity.TEXT_FI,
            LearningOpportunity.TEXT_FI_WHOLE,
            LearningOpportunity.TEXT_BOOST_FI,
            LearningOpportunity.TEXT_BOOST_FI_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.ARTICLE_CONTENT_FI,
            LearningOpportunity.NAME_AUTO_FI
    );

    public static final List<String> FIELDS_SV = Lists.newArrayList(
            LearningOpportunity.TEXT_SV,
            LearningOpportunity.TEXT_SV_WHOLE,
            LearningOpportunity.TEXT_BOOST_SV,
            LearningOpportunity.TEXT_BOOST_SV_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.ARTICLE_CONTENT_SV,
            LearningOpportunity.NAME_AUTO_SV
    );

    public static final List<String> FIELDS_EN = Lists.newArrayList(
            LearningOpportunity.TEXT_EN,
            LearningOpportunity.TEXT_EN_WHOLE,
            LearningOpportunity.TEXT_BOOST_EN,
            LearningOpportunity.TEXT_BOOST_EN_WHOLE,
            LearningOpportunity.AS_NAMES,
            LearningOpportunity.LOP_NAMES,
            LearningOpportunity.ARTICLE_CONTENT_EN,
            LearningOpportunity.NAME_AUTO_EN
    );

    private SolrUtil() {
    }

    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_FACET = "FASETTI";
    public static final String TYPE_ORGANISATION = "ORGANISAATIO";
    public static final String TYPE_APPLICATIONOPTION = "HAKUKOHDE";


    public static final Integer AS_COUNT = 10;
    public static final String APP_STATUS = "appStatus";
    public static final String APP_STATUS_ONGOING = "ongoing";
    public static final String APP_STATUS_UPCOMING = "upcoming";
    public static final String APP_STATUS_UPCOMING_LATER = "upcomingLater";
    public static final String QUOTED_QUERY_FORMAT = "%s:\"%s\"";

    public static String resolveTranslationInTeachingLangUseFallback(List<Code> teachingLanguages, Map<String, String> translations) {
        String translation = null;
        for (Code teachingLanguage : teachingLanguages) {
            for (Map.Entry<String, String> availableTranslation : translations.entrySet()) {
                if (teachingLanguage.getValue().equalsIgnoreCase(availableTranslation.getKey())) {
                    translation = availableTranslation.getValue();
                }
            }
        }
        if (translation == null || translation.isEmpty()) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null || translation.isEmpty()) {
            translation = translations.values().iterator().next();
        }

        return translation;
    }

    public static void addApplicationDates(SolrInputDocument doc, Set<ApplicationOption> applicationOptions) {
        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : applicationOptions) {
            doc.addField(new StringBuilder().append("asStart").append("_").append(String.valueOf(parentApplicationDateRangeIndex)).toString(),
                    ao.getApplicationStartDate());
            doc.addField(new StringBuilder().append("asEnd").append("_").append(String.valueOf(parentApplicationDateRangeIndex)).toString(),
                    ao.getApplicationEndDate());
            parentApplicationDateRangeIndex++;
        }
    }

    public static void setLopAndHomeplaceDisplaynames(SolrInputDocument doc,
            Provider provider, Code prerequisite) {

        doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_FI, provider.getName().get("fi"));
        doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_SV, provider.getName().get("sv"));
        doc.setField(LearningOpportunity.LOP_NAME_DISPLAY_EN, provider.getName().get("en"));

        doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_FI, provider.getHomePlace().get("fi"));
        doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_SV, provider.getHomePlace().get("sv"));
        doc.setField(LearningOpportunity.HOMEPLACE_DISPLAY_EN, provider.getHomePlace().get("en"));

        if (prerequisite != null) {
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY_FI, prerequisite.getName().get("fi"));
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY_SV, prerequisite.getName().get("sv"));
            doc.setField(LearningOpportunity.PREREQUISITE_DISPLAY_EN, prerequisite.getName().get("en"));
        }



    }

    /*
     * Creates a facet document for the given code, and adds to the list of docs given.
     */
    public static void indexCodeAsFacetDoc(Code code, List<SolrInputDocument> docs, boolean useValueAsId) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.ID, useValueAsId ? code.getValue().trim() : code.getUri().trim());
        doc.addField(LearningOpportunity.TYPE, TYPE_FACET);
        doc.addField(LearningOpportunity.FI_FNAME, resolveTextWithFallback("fi", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.SV_FNAME, resolveTextWithFallback("sv", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.EN_FNAME, resolveTextWithFallback("en", code.getName().getTranslations()));
        docs.add(doc);
    }

    public static String resolveTextWithFallback(String lang, Map<String, String> translations) {
        if (translations.isEmpty()) {
            return null;
        }

        List<String> langOrder = Lists.newArrayList(lang, FALLBACK_LANG, "fi", "sv", "en");

        for (String preferredLang : langOrder) {
            if (!StringUtils.isBlank(translations.get(preferredLang))) {
                return translations.get(preferredLang);
            }
        }

        return translations.values().iterator().next();
    }


    public static void setSearchFields(List<String> facetFilters, SolrQuery query) {

        List<String> searchFields = new ArrayList<String>();

        if (searchFields.isEmpty()){
            query.setParam(DisMaxParams.QF, Joiner.on(" ").join(SolrUtil.FIELDS));
        } else {
            query.setParam(DisMaxParams.QF, Joiner.on(" ").join(searchFields));
        }

    }

    private static final String SPECIAL_CHARS = ".,:;+-^()[]\"{}~?|&/";
    private static final String REPLACE_CHARS = "                    ";

    public static String fixString(String term) {

        String[] splits = term.split(" ");
        String fixed = "";
        for (String curSplit : splits) {
            if ((curSplit.length() > 1 || curSplit.equals("*")) && !curSplit.startsWith("&")) {
                fixed = String.format("%s%s ", fixed, curSplit);
            }
        }

        fixed = StringUtils.replaceChars(fixed, SPECIAL_CHARS, REPLACE_CHARS);
        fixed = fixed.trim();
        return fixed;
    }

    public static class LearningOpportunity {

        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String FI_FNAME = "fi_fname"; //finnish name of a facet value
        public static final String SV_FNAME = "sv_fname"; //swedish name of a facet value
        public static final String EN_FNAME = "en_fname"; //english name of a facet value
        public static final String TEACHING_LANGUAGE = "teachingLangCode_ffm"; //The teaching language used in the learning opportunity
        public static final String EDUCATION_TYPE = "educationType_ffm"; //The education type of the learning opportunity
        public static final String AS_FACET = "asFacet_ffm"; //The education type of the learning opportunity
        public static final String TOPIC = "topic_ffm"; //The topic of the learning opportunity
        public static final String THEME = "theme_ffm"; //The theme of the learning opportunity
        public static final String FORM_OF_TEACHING = "formOfTeaching_ffm"; //The education type of the learning opportunity
        public static final String TIME_OF_TEACHING = "timeOfTeaching_ffm"; //The education type of the learning opportunity
        public static final String FORM_OF_STUDY = "formOfStudy_ffm"; //The education type of the learning opportunity
        private static final String KIND_OF_EDUCATION = "kindOfEducation_ffm"; //The education type of the learning opportunity
        public static final String SIIRTOHAKU = "siirtohaku_ffm";
        public static final String LOP_ID = "lopId";
        public static final String NAME = "name";
        public static final String NAME_FI = "name_fi";
        public static final String NAME_SV = "name_sv";
        public static final String NAME_EN = "name_en";
        public static final String CHILD_NAME = "childName";
        public static final String CHILD_NAME_FI = "childName_fi";
        public static final String CHILD_NAME_SV = "childName_sv";
        public static final String CHILD_NAME_EN = "childName_en";
        public static final String LOP_NAME = "lopName";
        public static final String LOP_NAME_FI = "lopName_fi";
        public static final String LOP_NAME_SV = "lopName_sv";
        public static final String LOP_NAME_EN = "lopName_en";
        public static final String RESPONSIBLE_PROVIDER_FI = "responsibleProvider_fi";
        public static final String RESPONSIBLE_PROVIDER_SV = "responsibleProvider_sv";
        public static final String RESPONSIBLE_PROVIDER_EN = "responsibleProvider_en";
        public static final String LOP_HOMEPLACE = "lopHomeplace";
        public static final String LOP_ADDRESS_FI = "lopAddress_fi";
        public static final String LOP_DESCRIPTION_FI = "lopDescription_fi";
        public static final String LOP_DESCRIPTION_SV = "lopDescription_sv";
        public static final String LOP_DESCRIPTION_EN = "lopDescription_en";
        public static final String GOALS_FI = "goals_fi";
        public static final String GOALS_SV = "goals_sv";
        public static final String GOALS_EN = "goals_en";
        public static final String AS_NAME_FI = "asName_fi";
        public static final String AS_NAME_SV = "asName_sv";
        public static final String AS_NAME_EN = "asName_en";
        public static final String AO_NAME_FI = "aoName_fi";
        public static final String AO_NAME_SV = "aoName_sv";
        public static final String AO_NAME_EN = "aoName_en";
        public static final String PREREQUISITES = "prerequisites";
        public static final String LOS_ID = "losId";
        public static final String AS_ID = "asId";
        public static final String PARENT_ID = "parentId";
        public static final String PREREQUISITE = "prerequisite";
        public static final String PREREQUISITE_CODE = "prerequisiteCode";
        public static final String PROFESSIONAL_TITLES_FI = "professionalTitles_fi";
        public static final String PROFESSIONAL_TITLES_SV = "professionalTitles_sv";
        public static final String PROFESSIONAL_TITLES_EN = "professionalTitles_en";
        public static final String QUALIFICATION_FI = "qualification_fi";
        public static final String QUALIFICATION_SV = "qualification_sv";
        public static final String QUALIFICATION_EN = "qualification_en";
        public static final String CONTENT_FI = "content_fi";
        public static final String CONTENT_SV = "content_sv";
        public static final String CONTENT_EN = "content_en";
        public static final String CREDITS = "credits_ss";
        public static final String CREDITS_FI = "credits_fi_ss";
        public static final String CREDITS_SV = "credits_sv_ss";
        public static final String CREDITS_EN = "credits_en_ss";
        public static final String EDUCATION_DEGREE = "educationDegree_ss";
        public static final String EDUCATION_DEGREE_CODE = "educationDegreeCode_ss";
        public static final String EDUCATION_DEGREE_FI = "educationDegree_fi_ss";
        public static final String EDUCATION_DEGREE_SV = "educationDegree_sv_ss";
        public static final String EDUCATION_DEGREE_EN = "educationDegree_en_ss";
        public static final String NAME_DISPLAY_FI = "name_fi_ss";
        public static final String NAME_DISPLAY_SV = "name_sv_ss";
        public static final String NAME_DISPLAY_EN = "name_en_ss";
        public static final String LOP_NAME_DISPLAY_FI = "lopName_fi_str_display";
        public static final String LOP_NAME_DISPLAY_SV = "lopName_sv_str_display";
        public static final String LOP_NAME_DISPLAY_EN = "lopName_en_str_display";
        public static final String HOMEPLACE_DISPLAY_FI = "homeplace_fi_ss";
        public static final String HOMEPLACE_DISPLAY_SV = "homeplace_sv_ss";
        public static final String HOMEPLACE_DISPLAY_EN = "homeplace_en_ss";
        public static final String HOMEPLACE_DISPLAY = "homeplace_ss";
        public static final String PREREQUISITE_DISPLAY_EN = "prerequisite_en_ss";
        public static final String PREREQUISITE_DISPLAY_FI = "prerequisite_fi_ss";
        public static final String PREREQUISITE_DISPLAY_SV = "prerequisite_sv_ss";
        public static final String PREREQUISITE_DISPLAY = "prerequisite_ss";
        public static final String ADDITIONALEDUCATIONTYPE_DISPLAY = "additionaleducationtype_ss";
        public static final String ADDITIONALEDUCATIONTYPE_DISPLAY_FI = "additionaleducationtype_fi_ss";
        public static final String ADDITIONALEDUCATIONTYPE_DISPLAY_EN= "additionaleducationtype_en_ss";
        public static final String ADDITIONALEDUCATIONTYPE_DISPLAY_SV = "additionaleducationtype_sv_ss";
        public static final String DEGREE_TITLE_FI = "degreeTitles_fi";
        public static final String DEGREE_TITLE_SV = "degreeTitles_sv";
        public static final String DEGREE_TITLE_EN = "degreeTitles_en";

        public static final String EDUCATION_CODE_DISPLAY_FI = "educationCode_fi_ssort";
        public static final String EDUCATION_CODE_DISPLAY_SV = "educationCode_sv_ssort";
        public static final String EDUCATION_CODE_DISPLAY_EN = "educationCode_en_ssort";

        public static final String SUBJECT_FI = "subject_fi";
        public static final String SUBJECT_SV = "subject_sv";
        public static final String SUBJECT_EN = "subject_en";

        public static final String EDUCATION_TYPE_DISPLAY = "educationCode_en_ss";

        public static final String ARTICLE_URL = "article_url_ss";
        public static final String ARTICLE_PICTURE = "article_picture_ss";
        public static final String ARTICLE_EXCERPT = "article_excerpt_ss";

        public static final String ARTICLE_NAME_INDEX_FI = "article_name_fi_ssort";
        public static final String ARTICLE_NAME_INDEX_SV = "article_name_sv_ssort";
        public static final String ARTICLE_NAME_INDEX_EN = "article_name_en_ssort";

        public static final String ARTICLE_EDUCATION_CODE = "articleEducationCode_ffm";
        public static final String ARTICLE_LANG = "article_lang_ssort";
        public static final String ARTICLE_CONTENT_TYPE = "articleContentType_ffm";

        public static final String ARTICLE_CONTENT_FI = "articleContent_auto_fi";
        public static final String ARTICLE_CONTENT_SV = "articleContent_auto_sv";
        public static final String ARTICLE_CONTENT_EN = "articleContent_auto_en";

        //Fields for sorting
        public static final String START_DATE_SORT = "startDate_dsort";
        public static final String NAME_SORT = "name_ssort";
        public static final String NAME_FI_SORT = "name_fi_ssort";
        public static final String NAME_SV_SORT = "name_sv_ssort";
        public static final String NAME_EN_SORT = "name_en_ssort";

        //Fields for autocomplete
        public static final String NAME_AUTO = "name_auto";
        public static final String FREE_AUTO = "free_auto";


        //Text search fields
        private static final String TEXT_FI = "text_fi";
        private static final String TEXT_SV = "text_sv";
        private static final String TEXT_EN = "text_en";
        private static final String TEXT_FI_WHOLE = "text_fi_whole";
        private static final String TEXT_SV_WHOLE = "text_sv_whole";
        private static final String TEXT_EN_WHOLE = "text_en_whole";
        private static final String TEXT_BOOST_FI = "textBoost_fi^10.0";
        private static final String TEXT_BOOST_SV = "textBoost_sv^10.0";
        private static final String TEXT_BOOST_EN = "textBoost_en^10.0";
        private static final String TEXT_BOOST_FI_WHOLE = "textBoost_fi_whole^10.0";
        private static final String TEXT_BOOST_SV_WHOLE = "textBoost_sv_whole^10.0";
        private static final String TEXT_BOOST_EN_WHOLE = "textBoost_en_whole^10.0";
        private static final String AS_NAMES = "asNames";
        private static final String LOP_NAMES = "lopNames";

        private static final String NAME_AUTO_FI = "name_auto_fi";
        private static final String NAME_AUTO_SV = "name_auto_sv";
        private static final String NAME_AUTO_EN = "name_auto_en";

        // Application system fields
        public static final String AS_TARGET_GROUP_CODE = "targetGroupCode_ffm";
        public static final String AS_IS_VARSINAINEN = "isVarsinainen";
        public static final String AS_ATARU_FORM_KEY = "ataruFormKey";
    }

    public static class LocationFields {
        public static final String TYPE = "type"; //Type of area, i.e. kunta or maakunta
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String NAME_AUTO = "name_auto";
        public static final String LANG = "lang";
        public static final String CODE = "code";
        public static final String PARENT = "parent"; //The parent area of the municipality
    }

    public static class ProviderFields {
        public final static String STARTS_WITH_FI = "startsWith_fi";
        public final static String STARTS_WITH_SV = "startsWith_sv";
        public final static String STARTS_WITH_EN = "startsWith_en";
        public static final String TYPE_VALUE = "typeValue";
        public static final String TYPE_FI = "type_fi";
        public static final String TYPE_SV = "type_sv";
        public static final String TYPE_EN = "type_en";
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String NAME_FI = "name_fi";
        public static final String NAME_SV = "name_sv";
        public static final String NAME_EN = "name_en";
        public static final String TEXT_FI = "text_fi";
        public static final String TEXT_SV = "text_sv";
        public static final String TEXT_EN = "text_en";
        public static final String REQUIRED_BASE_EDUCATIONS = "requiredBaseEducations";
        public static final String AS_IDS = "asIds";
        public static final String VOCATIONAL_AS_IDS = "vocationalAsIds";
        public static final String NON_VOCATIONAL_AS_IDS = "nonVocationalAsIds";
        public static final String OL_TYPE = "oltype_ffm";
        public static final String ADDRESS_EN = "address_en_str_display";
        public static final String ADDRESS_SV = "address_sv_str_display";
        public static final String ADDRESS_FI = "address_fi_str_display";


    }

    public static class AoFields {
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String LOP_ID = "lopId";
        public static final String AS_ID = "asId";
        public static final String START_DATE = "asStart";
        public static final String END_DATE = "asEnd";
        public static final String PREREQUISITES = "prerequisites";

    }

    public static class SolrConstants {
        //value constants
        public static final String ED_TYPE_TUTKINTOON = "et01";
        public static final String ED_TYPE_LUKIO = "et01.01";
        public static final String ED_TYPE_AIKUISLUKIO = "et01.01.02";
        public static final String ED_TYPE_NUORTENLUKIO = "et01.01.01";
        public static final String ED_TYPE_KAKSOIS = "et01.03.001";
        public static final String ED_TYPE_AMMATILLISET = "et01.03";
        public static final String ED_TYPE_AMMATILLINEN = "et01.03.01";
        public static final String ED_TYPE_AMM_ER = "et01.03.02";
        public static final String ED_TYPE_AMM_TUTK = "et01.03.03";
        public static final String ED_TYPE_AMM_TUTK_ER = "et01.03.04";
        public static final String ED_TYPE_MUU_AMM_TUTK = "et01.03.05";

        public static final String ED_TYPE_AMMATILLINEN_SHORT = "et3";

        public static final String ED_TYPE_AMKS = "et01.04";
        public static final String ED_TYPE_AMK = "et01.04.01";
        public static final String ED_TYPE_YLEMPI_AMK = "et01.04.02";
        public static final String ED_TYPE_AVOIN_AMK = "et01.04.03";
        public static final String ED_TYPE_YOS = "et01.05";
        public static final String ED_TYPE_KANDIDAATTI = "et01.05.01";
        public static final String ED_TYPE_MAISTERI = "et01.05.02";
        public static final String ED_TYPE_KANDI_JA_MAISTERI = "et01.05.05";
        public static final String ED_TYPE_AVOIN_YO = "et01.05.03";
        public static final String ED_TYPE_JATKOKOULUTUS = "et01.05.04";

        public static final String ED_TYPE_MUU = "et02";
        public static final String ED_TYPE_PK_JALK = "et02.01";
        public static final String ED_TYPE_TENTH_GRADE = "et02.01.01";
        public static final String ED_TYPE_VOC_PREP = "et02.01.02";
        public static final String ED_TYPE_IMM_VOC = "et02.01.03";
        public static final String ED_TYPE_IMM_UPSEC = "et02.01.04";
        public static final String ED_TYPE_KANSANOPISTO = "et02.05";
        public static final String ED_TYPE_VALMENTAVA = "et02.02";
        public static final String ED_TYPE_AIKUISTEN_PERUSOPETUS = "et02.03";
        public static final String ED_TYPE_AMM_OPETTAJA = "et02.11";
        public static final String ED_TYPE_ERITYIS_JA_VALMENTAVA = "et02.12";
        public static final String ED_TYPE_VALMA = "et02.01.06";
        public static final String ED_TYPE_VALMA_ER = "et02.12.02";
        public static final String ED_TYPE_TELMA = "et02.12.01";

        public static final String ED_CODE_AMM_OPETTAJA = "koulutus_000001";
        public static final String ED_CODE_AMM_ER_OPETTAJA = "koulutus_000002";
        public static final String ED_CODE_AMM_OPO = "koulutus_000003";

        public static final String SPECIAL_EDUCATION = "ER";
        public static final String TIMESTAMP_DOC = "loUpdateTimestampDocument";
        public static final String TYPE_FACET = "FASETTI";
        public static final String TYPE_ARTICLE = "ARTIKKELI";
        public static final String TYPE_APPLICATION_SYSTEM = "HAKU";
        public static final String PK = "pk";
        public static final String YO = "yo";
        public static final String DISTRICT_UNKNOWN = "99";
        public static final String MUNICIPALITY_UNKNOWN = "99";

        public static final String PROVIDER_TYPE_UNKNOWN = "99";
        public static final Object ED_TYPE_AMMATILLINEN_NAYTTO = "ammatillinenperustutkintonayttona";

        public static final String AS_TARGET_GROUP_CODE_VOCATIONAL = "11";
        public static final String AS_TARGET_GROUP_CODE_HIGHERED = "12";
        public static final String AS_TARGET_GROUP_CODE_PREPARATORY = "17";

    }

    public static void indexLopName(SolrInputDocument doc, Provider provider, String teachLang) {

        String nameFi = provider.getName().getTranslations().get("fi");
        String nameSv = provider.getName().getTranslations().get("sv");
        String nameEn = provider.getName().getTranslations().get("en");

        //Setting the lop name to be finnish, if no finnish name, fallback to swedish or english
        String name = nameFi != null ? nameFi : nameSv;
        name = name == null ? nameEn : name;

        doc.setField(LearningOpportunity.LOP_NAME, name);
        doc.addField("lopNames", name);
        if (teachLang.equals("sv")) {
            doc.addField(LearningOpportunity.LOP_NAME_SV,
                    SolrUtil.resolveTextWithFallback("sv",provider.getName().getTranslations()));
        } else if (teachLang.equals("en")) {
            doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en",provider.getName().getTranslations()));
        } else {
            doc.addField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi",provider.getName().getTranslations()));
        }

    }

    public static void addKindOfEducationFields(BasicLOI loi, SolrInputDocument doc) {
        if (loi.getKoulutuslaji().getUri().startsWith(TarjontaConstants.AVOIN_KAIKILLE)) {
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.NUORTEN_KOULUTUS);
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.AIKUISKOULUTUS);
        } else {
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, loi.getKoulutuslaji().getUri());
        }
    }

    public static void addKindOfEducationFields(KoulutusLOS loi, SolrInputDocument doc) {
        if (loi.getKoulutuslaji().getUri().startsWith(TarjontaConstants.AVOIN_KAIKILLE)) {
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.NUORTEN_KOULUTUS);
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, TarjontaConstants.AIKUISKOULUTUS);
        } else {
            doc.addField(LearningOpportunity.KIND_OF_EDUCATION, loi.getKoulutuslaji().getUri());
        }
    }


}
