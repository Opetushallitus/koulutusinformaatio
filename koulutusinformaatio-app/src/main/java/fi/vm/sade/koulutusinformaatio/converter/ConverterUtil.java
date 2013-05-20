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

import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
public class ConverterUtil {

    public static String getTextByLanguage(final I18nText text, final String lang) {
        if (text != null && text.getTranslations() != null && text.getTranslations().containsKey(lang)) {
            return text.getTranslations().get(lang);
        } else {
            return null;
        }
    }

    public static Set<String> getAvailableTranslationLanguages(final I18nText text) {
        if (text != null && text.getTranslations() != null) {
            return text.getTranslations().keySet();
        } else {
            return null;
        }
    }

    public static List<String> getTextsByLanguage(final List<I18nText> list, final String lang) {
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
}
