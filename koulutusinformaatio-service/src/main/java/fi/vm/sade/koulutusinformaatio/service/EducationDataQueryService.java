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
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

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
    ParentLOS getParentLearningOpportunity(final String oid) throws ResourceNotFoundException;

    /**
     * Finds application options by the application system, learning opportunity provider and base education.
     * @param asId application system id
     * @param lopId learning opportunity provider id
     * @param baseEducation base education identifier from koodisto
     * @return list of the application options
     */
    List<ApplicationOption> findApplicationOptions(final String asId, final String lopId, final String baseEducation,
                                                   boolean vocational, boolean nonVocational);

    List<ApplicationOption> getApplicationOptions(final List<String> aoIds) throws InvalidParametersException;

    ApplicationOption getApplicationOption(final String aoId) throws ResourceNotFoundException;

    /**
     * Gets the child learning opportunity
     * @param childLoId child learning opportunity id
     * @return child learning opportunity
     */
    ChildLOS getChildLearningOpportunity(final String childLoId) throws ResourceNotFoundException;

    DataStatus getLatestDataStatus();

    Picture getPicture(final String id) throws ResourceNotFoundException;

    /**
     * Retrieves an upper secondary learning opportunity specification.
     *
     * @param id los id
     * @return upper secondary los
     * @throws ResourceNotFoundException
     */
    UpperSecondaryLOS getUpperSecondaryLearningOpportunity(final String id) throws ResourceNotFoundException;

    /**
     * Retrieves a special learning opportunity specification.
     *
     * @param id los id
     * @return special los
     * @throws ResourceNotFoundException
     */
    SpecialLOS getSpecialLearningOpportunity(final String id) throws ResourceNotFoundException;
    
    
    /**
     * Retrieves a university of applied science learning opportunity specification.
     *
     * @param id los id
     * @return university of applied science los
     * @throws ResourceNotFoundException
     */
    UniversityAppliedScienceLOS getUasLearningOpportunity(final String oid) throws ResourceNotFoundException;
    
    /**
     * Retrieves a learning opportunity provider.
     *
     * @param id provider id
     * @return provider
     * @throws ResourceNotFoundException no provider found with the id
     */
    Provider getProvider(final String id) throws ResourceNotFoundException;
    
    
}
