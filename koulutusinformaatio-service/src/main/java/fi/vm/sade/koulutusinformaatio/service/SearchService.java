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

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

import java.util.List;

public interface SearchService {

    List<Provider> searchLearningOpportunityProviders(
            final String term, final String asId, final String baseEducation, final boolean vocational,
            final boolean nonVocational, int start, int rows, String lang, boolean prefix) throws SearchException;

    List<Provider> searchLearningOpportunityProviders(final String term, String lang, boolean prefix) throws SearchException;

    LOSearchResultList searchLearningOpportunities(final String term, final String prerequisite,
                                                   List<String> cities, List<String> facetFilters, 
                                                   String lang, boolean ongoing, boolean upcoming, 
                                                   int start, int rows, String sort, String order) throws SearchException;

    List<LOSearchResult> searchLearningOpportunitiesByProvider(String lopId, String lang) throws SearchException;

    List<Location> searchLocations(final String term, final String lang) throws SearchException;
    List<Location> getLocations(List<String> codes, final String lang) throws SearchException;
    List<Location> getDistricts(final String lang) throws SearchException;
    List<Location> getChildLocations(List<String> districts, final String lang) throws SearchException;
    SuggestedTermsResult searchSuggestedTerms(String term, String lang) throws SearchException;
}
