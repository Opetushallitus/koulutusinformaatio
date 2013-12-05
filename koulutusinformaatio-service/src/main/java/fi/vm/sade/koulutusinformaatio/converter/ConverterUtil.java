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

import fi.vm.sade.koulutusinformaatio.domain.*;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class ConverterUtil {

    private static final String FALLBACK_LANG = "fi";

    public static String resolveTranslationInTeachingLangUseFallback(List<Code> teachingLanguages, Map<String, String> translations) {
        String translation = null;
        for (Code teachingLanguage : teachingLanguages) {
            for (String key : translations.keySet()) {
                if (teachingLanguage.getValue().equalsIgnoreCase(key)) {
                    translation = translations.get(key);
                }
            }
        }
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null) {
            translation = translations.values().iterator().next();
        }

        return translation;
    }

    public static void addApplicationDates(SolrInputDocument doc, List<ApplicationOption> applicationOptions) {
        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : applicationOptions) {
            if (ao.isSpecificApplicationDates()) {
                doc.addField(new StringBuilder().append("asStart").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationStartDate());
                doc.addField(new StringBuilder().append("asEnd").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationEndDate());
                parentApplicationDateRangeIndex++;
            } else {
                for (DateRange dr : ao.getApplicationSystem().getApplicationDates()) {
                    doc.addField(new StringBuilder().append("asStart").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
                    doc.addField(new StringBuilder().append("asEnd").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());
                    parentApplicationDateRangeIndex++;
                }
            }
        }
    }

    /**
     * Parses duration from duration string, which may contain
     * non numerical characters, e.g. 2-5. Takes the min value
     * of the numerical values.
     * Scales values to be counted in months.
     */
    public static int getDuration(ChildLOI childLOI) {
        String[] numStrings = childLOI.getPlannedDuration().split("[^0-9]*");
        int min = Integer.MAX_VALUE;
        for (String curNumStr : numStrings) {
            if ((curNumStr != null) && !curNumStr.isEmpty()) {
                try {
                    int curInt = Integer.parseInt(curNumStr);
                    min = curInt < min ? curInt : min;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (childLOI.getPduCodeUri().contains(SolrFields.SolrConstants.KESTOTYYPPI_VUOSI) && min < Integer.MAX_VALUE) {
            min = min * 12;
        }

        return min < Integer.MAX_VALUE ? min : -1;
    }


}
