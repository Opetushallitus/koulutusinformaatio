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
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

import java.util.Date;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface EducationDataQueryService {

    /**
     * Gets the parent learning opportunity by oid
     * @param oid
     * @return
     */
    ParentLO getParentLearningOpportunity(final String oid) throws ResourceNotFoundException;

    /**
     * Finds application options by the application system and learning opportunity provider
     * @param asId application system id
     * @param lopId learning opportunity provider id
     * @return list of the application options
     */
    List<ApplicationOption> findApplicationOptions(final String asId, final String lopId);

    List<ApplicationOption> getApplicationOptions(final List<String> aoIds);

    /**
     * Gets the child learning opportunity
     * @param childLosId child learning opportunity specification id
     * @param childLoiId child learning opportunity instance id
     * @return child learning opportunity
     */
    ChildLO getChildLearningOpportunity(final String childLosId, final String childLoiId) throws ResourceNotFoundException;

    Date getLastUpdated();
}
