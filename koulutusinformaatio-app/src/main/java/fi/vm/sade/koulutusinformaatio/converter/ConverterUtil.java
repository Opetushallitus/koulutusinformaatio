/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

import com.google.common.base.Strings;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
public class ConverterUtil {

    private static String FALLBACK_LANG = "fi";

    public static String getTextByLanguage(final I18nText text, String lang) {
        lang = lang.toLowerCase();
        if (text != null && text.getTranslations() != null && text.getTranslations().containsKey(lang)) {
            return text.getTranslations().get(lang);
        } else {
            return null;
        }
    }

    public static String getTextByLanguageUseFallbackLang(final I18nText text, String lang) {
        String val = getTextByLanguage(text, lang);
        if (Strings.isNullOrEmpty(val) && text != null && text.getTranslations() != null &&
                !text.getTranslations().isEmpty()) {
            val = getTextByLanguage(text, FALLBACK_LANG);
            if (Strings.isNullOrEmpty(val)) {
                val = text.getTranslations().values().iterator().next();
            }
        }
        return val;
    }

    public static String getShortNameTextByLanguage(final I18nText text, String lang) {
        lang = lang.toLowerCase();
        if (text != null && text.getTranslationsShortName() != null && text.getTranslationsShortName().containsKey(lang)) {
            return text.getTranslationsShortName().get(lang);
        } else {
            return null;
        }
    }

    public static String getShortNameTextByLanguageUseFallbackLang(final I18nText text, String lang) {
        String val = getShortNameTextByLanguage(text, lang);
        if (Strings.isNullOrEmpty(val) && text != null && text.getTranslationsShortName() != null &&
                !text.getTranslationsShortName().isEmpty()) {
            val = getShortNameTextByLanguage(text, FALLBACK_LANG);
            if (Strings.isNullOrEmpty(val)) {
                val = text.getTranslationsShortName().values().iterator().next();
            }
        }
        return val;
    }

    public static Set<String> getAvailableTranslationLanguages(final I18nText text) {
        if (text != null && text.getTranslations() != null) {
            return text.getTranslations().keySet();
        } else {
            return null;
        }
    }

    public static List<String> getTextsByLanguage(final List<I18nText> list, String lang) {
        lang = lang.toLowerCase();
        List<String> texts = new ArrayList<String>();
        if (list != null) {
            for (I18nText text : list) {
                String value = getTextByLanguage(text, lang);
                if (value != null) {
                    texts.add(value);
                }
            }
        }
        return texts;
    }

    public static List<String> getShortNameTextsByLanguage(final List<I18nText> list, String lang) {
        lang = lang.toLowerCase();
        List<String> texts = new ArrayList<String>();
        if (list != null) {
            for (I18nText text : list) {
                String value = getShortNameTextByLanguage(text, lang);
                if (value != null) {
                    texts.add(value);
                }
            }
        }
        return texts;
    }

    public static boolean isOngoing(List<DateRange> dateRanges) {
        for (DateRange dr : dateRanges) {
            if (isOngoing(dr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOngoing(DateRange dateRange) {
        Date now = new Date();
        if (dateRange.getStartDate().before(now) && now.before(dateRange.getEndDate())) {
            return true;
        } else {
            return false;
        }
    }

    public static Date resolveNextDateRangeStart(List<DateRange> dateRanges) {
        Date nextStarts = null;
        Date now = new Date();
        for (DateRange dateRange : dateRanges) {
            if ((nextStarts == null && dateRange.getStartDate().after(now)) ||
                    (dateRange.getStartDate().after(now) && dateRange.getStartDate().before(nextStarts))) {
                nextStarts = dateRange.getStartDate();
            }
        }
        return nextStarts;
    }
}
