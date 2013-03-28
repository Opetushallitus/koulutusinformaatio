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

package fi.vm.sade.koulutusinformaatio.service.impl.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapEntryUtil {
    public static Map.Entry<String, List<String>> createEntryWithValue(final String... values) {
        return new Map.Entry<String, List<String>>() {

            @Override
            public String getKey() {
                return "test";
            }

            @Override
            public List<String> getValue() {
                return Arrays.asList(values);
            }

            @Override
            public List<String> setValue(List<String> value) {
                return null;
            }
        };
    }
}
