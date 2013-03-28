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

package fi.vm.sade.koulutusinformaatio.domain.search;

import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchFilters {

    //private final String[] filterFields = new String[]{"koulutustyyppi", "pohjakoulutus", "koulutuksenkieli", "opetusmuoto", "oppilaitostyyppi"};
    private final String[] filterFields = new String[]{"pohjakoulutus", "koulutuksenkieli", "opetusmuoto"};

    Map<String, List<String>> filters = new HashMap<String, List<String>>();

    private final SearchService service;

    @Autowired
    public SearchFilters(SearchService service) {
        this.service = service;
        this.filters = Collections.unmodifiableMap(new HashMap<String, List<String>>());
    }

    public Map<String, List<String>> getFilters() {
        // TODO Päivitä suodattimet ainoastaan tarjonnan päivitylsen yhteydessä
        filters = fetchFilters();
        return filters;
    }

    private Map<String, List<String>> fetchFilters() {
        Map<String, List<String>> filters = new HashMap<String, List<String>>();
        for (String filterField : filterFields) {
            List<String> value = populateFilter(filterField);
            filters.put(filterField, value);
        }
        return Collections.unmodifiableMap(filters);
    }

    private List<String> populateFilter(final String field) {
        List<String> filterValues = new ArrayList<String>();
        Collection<String> values = service.getUniqValuesByField(field);
        for (String name : values) {
            filterValues.add(name);
        }
        return Collections.unmodifiableList(filterValues);
    }
}
