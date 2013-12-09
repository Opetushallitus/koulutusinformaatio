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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.ApplicationOptionResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Component
public class ApplicationOptionResourceImpl implements ApplicationOptionResource {

    private LearningOpportunityService learningOpportunityService;

    @Autowired
    public ApplicationOptionResourceImpl(LearningOpportunityService learningOpportunityService) {
        this.learningOpportunityService = learningOpportunityService;
    }

    @Override
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId,
                                                                           String lopId,
                                                                           String baseEducation,
                                                                           boolean vocational,
                                                                           boolean nonVocational) {
        return learningOpportunityService.searchApplicationOptions(asId, lopId, baseEducation, vocational, nonVocational);
    }

    @Override
    public ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) {
        try {
            return learningOpportunityService.getApplicationOption(aoId, lang, uiLang);
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, String lang, String uiLang) {
        try {
            return learningOpportunityService.getApplicationOptions(aoId, lang, uiLang);
        } catch (InvalidParametersException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
