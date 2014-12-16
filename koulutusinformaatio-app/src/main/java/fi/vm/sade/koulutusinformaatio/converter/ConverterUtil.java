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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.DateRangeDTO;

/**
 * @author Mikko Majapuro
 */
public final class ConverterUtil {

    private ConverterUtil() {
    }

    public static String FALLBACK_LANG = "fi";

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
    
    public static List<String> getTextsByLanguageUseFallbackLang(final List<I18nText> list, String lang) {
        lang = lang.toLowerCase();
        List<String> texts = new ArrayList<String>();
        if (list != null) {
            for (I18nText text : list) {
                String value = getTextByLanguageUseFallbackLang(text, lang);
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
    
    public static boolean isCalendarApplicationsystemOngoing(List<ApplicationPeriod> applicationPeriods) {
        for (ApplicationPeriod ap : applicationPeriods) {
            if (isOngoing(ap.getDateRange())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOngoingDTO(List<DateRangeDTO> dateRanges) {
        for (DateRangeDTO dr : dateRanges) {
            if (isOngoingDTO(dr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOngoing(DateRange dateRange) {
        Date now = new Date();
        if (dateRange != null ) {
            if (dateRange.getStartDate() != null && dateRange.getEndDate() !=null && dateRange.getStartDate().before(now) && now.before(dateRange.getEndDate())) {
                return true;
            } else if (dateRange.getStartDate() != null && dateRange.getEndDate() == null && dateRange.getStartDate().before(now)) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public static boolean isOngoingDTO(DateRangeDTO dateRange) {
        Date now = new Date();
        if (dateRange != null ) {
            if (dateRange.getStartDate() != null && dateRange.getEndDate() !=null && dateRange.getStartDate().before(now) && now.before(dateRange.getEndDate())) {
                return true;
            } else if (dateRange.getStartDate() != null && dateRange.getEndDate() ==null && dateRange.getStartDate().before(now)) {
                return true;
            }
            return false;
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

    /**
     * Sort application systems for ui.
     * <p/>
     * Algorithm:
     * <p/>
     * 1. Ongoing on top.
     * 3. Sort by date, latest starting first.
     *
     * @return new sorted list
     */
    public static List<ApplicationSystemDTO> sortApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {

        Ordering<ApplicationSystemDTO> firstOngoingThenStartDate = Ordering.natural().reverse()
                .onResultOf(new Function<ApplicationSystemDTO, Boolean>() {
                    @Override
                    public Boolean apply(ApplicationSystemDTO input) {
                        return (input != null) ? isOngoingDTO(input.getApplicationDates()) : false;
                    }
                }).compound(
                        Ordering.natural().reverse()
                                .onResultOf(
                                        new Function<ApplicationSystemDTO, Date>() {
                                            @Override
                                            public Date apply(ApplicationSystemDTO input) {
                                                return (input != null) ? input.getApplicationDates().get(0).getStartDate() : null;
                                            }
                                        }
                                )
                );

        Collections.sort(applicationSystems, firstOngoingThenStartDate);
        return applicationSystems;
    }
}
