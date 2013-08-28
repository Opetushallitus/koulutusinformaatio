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

import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.BasketResource;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Component
public class BasketResourceImpl implements BasketResource {

    private LearningOpportunityService learningOpportunityService;

    @Autowired
    public BasketResourceImpl(LearningOpportunityService learningOpportunityService) {
        this.learningOpportunityService = learningOpportunityService;
    }

    @Override
    public List<BasketItemDTO> getBasketItems(List<String> aoId, String uiLang) {
        try {
            return learningOpportunityService.getBasketItems(aoId, uiLang);
        } catch (Exception e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
