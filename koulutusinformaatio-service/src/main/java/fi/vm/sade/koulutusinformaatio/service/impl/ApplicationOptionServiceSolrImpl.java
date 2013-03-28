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

import fi.vm.sade.koulutusinformaatio.domain.search.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.search.Organization;
import fi.vm.sade.koulutusinformaatio.domain.search.SearchResult;
import fi.vm.sade.koulutusinformaatio.service.ApplicationOptionService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Service("applicationOptionServiceSolrImpl")
public class ApplicationOptionServiceSolrImpl implements ApplicationOptionService {

    private final SearchService service;

    @Autowired
    public ApplicationOptionServiceSolrImpl(SearchService service) {
        this.service = service;
    }

    @Override
    public List<Organization> searchOrganisaatio(final String hakuId, final String term, final String prerequisite, final String vocational) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(3);
        Set<Organization> organizations = new HashSet<Organization>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            parameters.put("LOPInstitutionInfoName", createParameter(term + "*"));
            parameters.put("ASId", createParameter(hakuId));
            parameters = addPrerequisite(parameters, prerequisite, vocational);
            SearchResult search = service.search(parameters.entrySet());
            List<Map<String, Object>> items = search.getItems();
            for (Map<String, Object> item : items) {
                organizations.add(new Organization((String) item.get("LOPId"), (String) item.get("LOPInstitutionInfoName")));
            }
        }
        return new ArrayList<Organization>(organizations);
    }

    @Override
    public List<ApplicationOption> searchHakukohde(final String hakuId, final String organisaatioId, final String prerequisite, final String vocational) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(3);
        parameters.put("ASId", createParameter(hakuId));
        parameters.put("LOPId", createParameter(organisaatioId));
        parameters = addPrerequisite(parameters, prerequisite, vocational);
        SearchResult search = service.search(parameters.entrySet());

        List<ApplicationOption> hakukohteet = new ArrayList<ApplicationOption>(search.getSize());
        List<Map<String, Object>> items = search.getItems();
        for (Map<String, Object> item : items) {
            hakukohteet.add(new ApplicationOption((String) item.get("AOId"), (String) item.get("AOTitle"), (String) item.get("AOEducationDegree")));
        }
        return hakukohteet;
    }

    private MultiValueMap<String, String> addPrerequisite(MultiValueMap<String, String> parameters, String prerequisite, String vocational) {
        String realPrerequisite = prerequisite;
        if (realPrerequisite.equals("KESKEYTYNYT") || realPrerequisite.equals("ULKOMAINEN_TUTKINTO")) {
            return parameters; // Ei suodatusta
        }
        if (realPrerequisite.equals("YLIOPPILAS")) {
            parameters.put("LOIPrerequisite", createParameter("(5 OR 9)"));
        } else if (realPrerequisite.equals("PERUSKOULU")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5)"));
        } else if (realPrerequisite.equals("OSITTAIN_YKSILOLLISTETTY")
                || realPrerequisite.equals("ERITYISOPETUKSEN_YKSILOLLISTETTY")
                || realPrerequisite.equals("YKSILOLLISTETTY")) {
            parameters.put("LOIPrerequisite", createParameter("(1 OR 2 OR 4 OR 5 OR 6)"));
        }
        boolean hasVocational = Boolean.parseBoolean(vocational);
        if (hasVocational) {
            parameters.put("AOEducationDegree", createParameter("(NOT 32)"));
        }
        return parameters;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }
}
