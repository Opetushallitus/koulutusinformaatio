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

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

/**
 * 
 * @author Markus
 */
public interface PreviewService {

    /**
     * Gets the parent learning opportunity by oid
     * @param oid
     * @return
     */
    HigherEducationLOS previewHigherEducationLearningOpportunity(final String oid) throws ResourceNotFoundException;
    
    /**
     * Gets an adult upper secondary learning opportunity by oid
     * @param oid
     * @return
     */
    AdultUpperSecondaryLOS previewAdultUpperSecondaryLearningOpportunity(final String oid) throws ResourceNotFoundException;

    CompetenceBasedQualificationParentLOS previewAdultVocationaParentLearningOpportunity(final String oid) throws ResourceNotFoundException;

}
