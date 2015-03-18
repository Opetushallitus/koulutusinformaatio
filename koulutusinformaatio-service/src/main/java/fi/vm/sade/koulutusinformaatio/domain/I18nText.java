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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Mikko Majapuro
 */
public class I18nText {

    private Map<String, String> translations;

    public I18nText() {
        this.translations = Maps.newHashMap();
    }

    public I18nText(final Map<String, String> translations) {
        this.translations = ImmutableMap.copyOf(translations);
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
}
