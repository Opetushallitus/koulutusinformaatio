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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.search.SearchResult;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/123:44 PM}
 * @since 1.1
 */
public class SearchServiceTest {
    @Test
    public void testSearch() throws Exception {
        final SearchResult search = new MockSearchService().search(new LinkedMultiValueMap<String, String>().entrySet());
        assertEquals(0, search.getSize());
    }

    @Test
    public void testSearchById() throws Exception {
        final Map<String, Object> stringObjectMap = new MockSearchService().searchById("1");
        assertEquals(0, stringObjectMap.size());
    }

    @Test
    public void testGetUniqValuesByField() throws Exception {
        final Collection<String> foo = new MockSearchService().getUniqValuesByField("foo");
        assertEquals(0, foo.size());
    }

    private class MockSearchService implements SearchService {

        @Override
        public SearchResult search(Set<Map.Entry<String, List<String>>> parameters) throws SearchException {
            return new SearchResult(new ArrayList<Map<String, Collection<Object>>>());
        }

        @Override
        public Map<String, Object> searchById(String field) {
            return new HashMap<String, Object>();
        }

        @Override
        public Collection<String> getUniqValuesByField(String field) {
            return new ArrayList<String>();
        }
    }
}
