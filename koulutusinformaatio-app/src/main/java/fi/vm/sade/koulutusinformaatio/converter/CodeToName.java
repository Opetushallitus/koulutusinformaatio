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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Code;

/**
 * @author Hannu Lyytikainen
 */
public class CodeToName {


    public static String convert(final Code code, final String lang) {
        return ConverterUtil.getTextByLanguageUseFallbackLang(code.getName(), lang);
    }

    public static List<String> convertAll(final List<Code> codes, final String lang) {
        if (codes != null) {
            return Lists.transform(codes, new Function<Code, String>() {
                @Override
                public String apply(Code input) {
                    return convert(input, lang);
                }
            });
        } else {
            return null;
        }
    }

}
