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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ProviderSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ProviderToSearchResult {

    private ProviderToSearchResult() {
    }

    public static ProviderSearchResult convert(Provider p) {
        if (p != null) {
            ProviderSearchResult result = new ProviderSearchResult();
            result.setId(p.getId());
            result.setName(p.getName().getTranslations().get("fi"));
            return result;
        }
        return null;
    }

    public static List<ProviderSearchResult> convertAll(List<Provider> providers) {
        if (providers != null) {
            return Lists.transform(providers, new Function<Provider, ProviderSearchResult>() {
                @Override
                public ProviderSearchResult apply(Provider input) {
                    return convert(input);
                }
            });
        }
        return new ArrayList<ProviderSearchResult>();
    }
}
