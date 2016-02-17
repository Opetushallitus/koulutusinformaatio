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

package fi.vm.sade.koulutusinformaatio.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;

/**
 * @author Mikko Majapuro
 */
public class EducationDataQueryServiceImplTest extends AbstractEducationServiceTest {


    private EducationDataQueryServiceImpl service;
    private ApplicationOptionDAO applicationOptionDAO;
    private PictureDAO pictureDAO;
    private DataStatusDAO dataStatusDAO;
    private LearningOpportunityProviderDAO providerDAO;
    private HigherEducationLOSDAO higherEdDAO;
    private KoulutusLOSDAO koulutusDAO;
    private TutkintoLOSDAO tutkintoDAO;
    private AdultVocationalLOSDAO adultVocDAO;

    @Before
    public void setUp() {
        lastDataUpdate = new Date();
        ModelMapper modelMapper = new ModelMapper();
        applicationOptionDAO = mockApplicationOptionDAO();
        dataStatusDAO = mockDataStatudDAO();
        pictureDAO = mockPictureDAO();
        providerDAO = mockProviderDAO();
        higherEdDAO = mockHigherEdDAO();
        koulutusDAO = mock(KoulutusLOSDAO.class);
        tutkintoDAO = mock(TutkintoLOSDAO.class);
        adultVocDAO = mock(AdultVocationalLOSDAO.class);
        service = new EducationDataQueryServiceImpl(applicationOptionDAO, modelMapper, dataStatusDAO, pictureDAO,
                 higherEdDAO, adultVocDAO, koulutusDAO, tutkintoDAO, providerDAO);
    }
    
    /**
     * Testing education with multiple providers.
     * @throws ResourceNotFoundException
     */
    @Test
    public void testMultipleProviders() throws ResourceNotFoundException {
        HigherEducationLOS los = this.service.getHigherEducationLearningOpportunity("higherEdId");
        assertEquals("mainProvider", los.getProvider().getId());
        assertEquals(1, los.getAdditionalProviders().size());
        assertEquals("additionalProvider", los.getAdditionalProviders().get(0).getId());
    }

    @Test
    public void testFindApplicationOptions() {
        List<ApplicationOption> result = service.findApplicationOptions("1.1.1", "9.9.9", "1", true, true);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("8.9.0", result.get(0).getId());
    }

    @Test
    public void testGetApplicationOptions() throws Exception {
        List<ApplicationOption> result = service.getApplicationOptions(Lists.newArrayList("8.9.0"));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetApplicationsInvalidNullParams() throws InvalidParametersException {
        service.getApplicationOptions(null);
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetApplicationsInvalidEmptyParams() throws InvalidParametersException {
        service.getApplicationOptions(new ArrayList<String>());
    }

    @Test
    public void testGetLatestDataStatus() {
        DataStatus latest = service.getLatestDataStatus();
        assertEquals(lastDataUpdate, latest.getLastUpdateFinished());
        assertEquals(latest.getLastUpdateDuration(), 1000);
        assertEquals(latest.getLastUpdateOutcome(), "SUCCESS");
    }

    @Test
    public void testPicture() throws ResourceNotFoundException {
        Picture picture = service.getPicture("pictureid");
        assertEquals("encoded", picture.getPictureEncoded());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testPictureNotFound() throws ResourceNotFoundException {
        service.getPicture(NOTFOUND);
    }

    @Test
    public void testGetProvider() throws ResourceNotFoundException {
        Provider provider = service.getProvider("providerid");
        assertNotNull(provider);
        assertEquals("providerid", provider.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProviderNotFound() throws ResourceNotFoundException {
       service.getProvider(NOTFOUND);
    }
    
    @Test
    public void testGetHigherEducation() throws ResourceNotFoundException {
        HigherEducationLOS lo = service.getHigherEducationLearningOpportunity("higherEdId");
        assertNotNull(lo);
        assertEquals("higherEdId", lo.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetHigherEdNotFound() throws ResourceNotFoundException {
    	service.getHigherEducationLearningOpportunity(NOTFOUND);
    }

}
