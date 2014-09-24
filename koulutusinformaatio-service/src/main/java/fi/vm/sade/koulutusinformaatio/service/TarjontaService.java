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

import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public interface TarjontaService {

    /**
     * Finds a parent learning opportunity by oid.
     *
     *
     * @param oid
     * @return
     */
    public List<LOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException;

    /**
     * Returns a list of all parent learning opportunity oids.
     *
     * @return list of oids
     */
    public List<String> listParentLearnignOpportunityOids();

    /**
     * Returns a list of parent learning opportunity oids
     * @param count maximum count of results
     * @param startIndex start index of results
     * @return list of oids
     */
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex);
    
    
    /**
     * Returns a list of root-level higher education learning opportunities.
     * The children of each learning opportunity is contained in the object.
     * @return list of root-level higher education learning opportunities.
     * @throws KoodistoException
     */
    public List<HigherEducationLOS> findHigherEducations() throws KoodistoException, ResourceNotFoundException;

    /**
     * Retrieves (from tarjonta) and returns one higher level learning opportunity. This
     * method is intended to be used in preview.
     * 
     * @param oid the oid of the learning opportunity to retrieve. 
     * @return the retrieved higher education learning opportunity
     * @throws TarjontaParseException
     * @throws KoodistoException
     */
	public HigherEducationLOS findHigherEducationLearningOpportunity(
			String oid) throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    public List<Code> getEdTypeCodes() throws KoodistoException;

    HigherEducationLOS createHigherEducationLearningOpportunityTree(String oid)
            throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    public List<AdultUpperSecondaryLOS> findAdultUpperSecondaries() throws KoodistoException;
    
    public List<CompetenceBasedQualificationParentLOS> findAdultVocationals() throws KoodistoException;

    public AdultUpperSecondaryLOS createAdultUpperSecondaryLOS(String oid, boolean checkStatus) throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    public CompetenceBasedQualificationParentLOS createCBQPLOS(String oid, boolean checkStatus) throws TarjontaParseException, KoodistoException, ResourceNotFoundException;
    
    public HigherEducationLOSRef createAdultVocationalLosRef(CompetenceBasedQualificationParentLOS los, ApplicationOption curAo);
            
}
