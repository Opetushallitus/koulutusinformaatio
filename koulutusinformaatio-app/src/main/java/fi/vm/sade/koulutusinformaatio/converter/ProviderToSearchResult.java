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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResultDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class ProviderToSearchResult {

    private ProviderToSearchResult() {
    }

    private static ProviderSearchResultDTO convert(final Provider p, final String lang) {
        if (p != null) {
            ProviderSearchResultDTO result = new ProviderSearchResultDTO();
            result.setId(p.getId());
            result.setName(ConverterUtil.getTextByLanguageUseFallbackLang(p.getName(), lang));
            return result;
        }
        return null;
    }

    public static List<ProviderSearchResultDTO> convertAll(List<Provider> providers, final String lang) {
        if (providers != null) {
            return Lists.transform(providers, new Function<Provider, ProviderSearchResultDTO>() {
                @Override
                public ProviderSearchResultDTO apply(Provider input) {
                    return convert(input, lang);
                }
            });
        }
        return new ArrayList<ProviderSearchResultDTO>();
    }
}
