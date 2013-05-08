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

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface EducationDataService {

    /**
     *  Saves the parent learning opportunity data.
     *  Updates previously stored if exists
     * @param parentLOS
     */
    void save(final ParentLOS parentLOS);

    void dropAllData();

    /**
     * Gets the parent learning opportunity by oid
     * @param oid
     * @return
     */
    ParentLOS getParentLearningOpportunity(final String oid) throws ResourceNotFoundException;

    /**
     * Finds application options by the application system and learning opportunity provider
     * @param asId application system id
     * @param lopId learning opportunity provider id
     * @return list of the application options
     */
    List<ApplicationOption> findApplicationOptions(final String asId, final String lopId);
}
