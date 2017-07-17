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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Mikko Majapuro
 */
public class I18nText {
    private final static Logger LOGGER = LoggerFactory.getLogger(I18nText.class);

    private Map<String, String> translations;

    public I18nText() {
        this.translations = Maps.newHashMap();
    }

    public I18nText(final Map<String, String> translations) {
        try {
            this.translations = ImmutableMap.copyOf(translations);
        } catch (NullPointerException np) {
            LOGGER.error("Constructing I18nText failed: " + translations, np);
        }
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    /**
     * Get text with language key.
     *
     * @param key language code
     * @return internationalized text
     */
    public String get(String key) {
        return this.translations.get(key);
    }

    /**
     * Set internationalized text value.
     *
     * @param key language code
     * @param value internationalized text
     */
    public void put(String key, String value) {
        this.translations.put(key, value);
    }

    public static boolean hasTranslationForLanguage(I18nText text, String lang) {
        lang = lang.replace("kieli_", "");
        return text != null && text.getTranslations() != null
                && (text.getTranslations().containsKey(lang) || text.getTranslations().containsKey("kieli_" + lang));
    }

    @Override
    public String toString() {
        return translations.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        I18nText i18nText = (I18nText) o;

        return translations.equals(i18nText.translations);
    }

    @Override
    public int hashCode() {
        return translations.hashCode();
    }
}
