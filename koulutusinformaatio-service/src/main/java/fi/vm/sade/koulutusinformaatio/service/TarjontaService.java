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

import java.util.List;
import java.util.Set;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public interface TarjontaService {

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
            String oid) throws TarjontaParseException, KoodistoException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException;

    public List<KoulutusHakutulosV1RDTO> findKorkeakouluOpinnot();

    public List<Code> getEdTypeCodes() throws KoodistoException;

    HigherEducationLOS createHigherEducationLearningOpportunityTree(String oid)
            throws TarjontaParseException, KoodistoException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException;

    public List<KoulutusLOS> findAdultUpperSecondariesAndBaseEducation() throws KoodistoException, TarjontaParseException, OrganisaatioException;
    
    public List<CompetenceBasedQualificationParentLOS> findAdultVocationals() throws KoodistoException;

    List<KoulutusLOS> findValmistavaKoulutusEducations() throws KoodistoException;

    public CompetenceBasedQualificationParentLOS createCBQPLOS(String oid, boolean checkStatus) throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    public HigherEducationLOSRef createAdultVocationalLosRef(CompetenceBasedQualificationParentLOS los, ApplicationOption curAo);

    public List<CalendarApplicationSystem> findApplicationSystemsForCalendar() throws KoodistoException;
    
    public CalendarApplicationSystem createCalendarApplicationSystem(String hakuOid) throws KoodistoException;
    
    List<Code> getEdBaseEducationCodes() throws KoodistoException;

    List<KoulutusHakutulosV1RDTO> findAmmatillinenKoulutusDTOs() throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    /**
     * Creates a KoulutusLOS from given DTO. Additionally creates all siblingLOSes.
     * 
     * @param curDTO
     * @return List of created LOSes
     * @throws TarjontaParseException
     * @throws KoodistoException
     */
    public List<KoulutusLOS> createAmmatillinenKoulutusLOS(KoulutusHakutulosV1RDTO curDTO) throws KoodistoException, TarjontaParseException;

    /**
     * Helper method to reset duplicate sibling creation in createAmmatillinenKoulutusLOS
     */
    void clearProcessedLists();

    boolean hasAlreadyProcessedOid(String oid);

    void addProcessedOid(String komoOid);

    List<KoulutusHakutulosV1RDTO> findLukioKoulutusDTOs() throws TarjontaParseException, KoodistoException, ResourceNotFoundException;

    public KoulutusLOS createLukioKoulutusLOS(KoulutusHakutulosV1RDTO curDTO);

    List<KoulutusHakutulosV1RDTO> findKoulutus(String toteutusTyyppi, String providerOid, String koulutusKoodi) throws TarjontaParseException,
            KoodistoException, ResourceNotFoundException;

    public Set<String> findKoulutusOidsByHaku(String asOid);

    public Set<String> findKoulutusOidsByAo(String aoOid);

    /**
     * Luo korkeakouluopinnon annetusta DTOsta. Jos dto on opintojakso, luo opintokokonaisuuden ja kaikki sen opintojaksot.
     * @param dto
     * @return
     * @throws KIException 
     */
    public KoulutusLOS createKorkeakouluopinto(KoulutusHakutulosV1RDTO dto);

    KoulutusLOS createKoulutusLOS(String oid, boolean checkStatus) throws KoodistoException, TarjontaParseException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException;

}
