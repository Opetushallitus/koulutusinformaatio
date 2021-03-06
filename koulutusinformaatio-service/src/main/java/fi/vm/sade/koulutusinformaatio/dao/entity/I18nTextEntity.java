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

package fi.vm.sade.koulutusinformaatio.dao.entity;


import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class I18nTextEntity {

    private Map<String, String> translations;

    public I18nTextEntity() {}

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    @Override
    public String toString() {
        String value = "";
        if (translations != null && !translations.isEmpty())
            value = translations.get("kieli_fi") != null ? translations.get("kieli_fi") : translations.values().iterator().next();
        return value;
    }
}
