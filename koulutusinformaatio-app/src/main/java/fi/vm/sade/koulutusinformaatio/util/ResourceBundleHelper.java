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

package fi.vm.sade.koulutusinformaatio.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.common.collect.Maps;

/**
 * @author Hannu Lyytikainen
 */
public class ResourceBundleHelper {

    private static final String FI = "fi";
    private static final String SV = "sv";
    private static final String EN = "en";
    private static final String BUNDLE_NAME = "messages";

    private Map<String, ResourceBundle> resourceBundles;

    public ResourceBundleHelper() {
        resourceBundles = Maps.newHashMap();
        resourceBundles.put(FI, ResourceBundle.getBundle(BUNDLE_NAME, new Locale(FI)));
        resourceBundles.put(SV, ResourceBundle.getBundle(BUNDLE_NAME, new Locale(SV)));
        resourceBundles.put(EN, ResourceBundle.getBundle(BUNDLE_NAME, new Locale(EN)));
    }

    public ResourceBundle getBundle(String lang) {
        if (resourceBundles.containsKey(lang)) {
            return resourceBundles.get(lang);
        }
        else {
            return resourceBundles.get(FI);
        }
    }
}
