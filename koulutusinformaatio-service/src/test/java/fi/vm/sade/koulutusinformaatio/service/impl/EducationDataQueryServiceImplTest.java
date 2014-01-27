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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class EducationDataQueryServiceImplTest extends AbstractEducationServiceTest {


    private EducationDataQueryServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private PictureDAO pictureDAO;
    private DataStatusDAO dataStatusDAO;
    private LearningOpportunityProviderDAO providerDAO;

    @Before
    public void setUp() {
        lastDataUpdate = new Date();
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunitySpecificationDAO = mockParentDAO();
        applicationOptionDAO = mockApplicationOptionDAO();
        childLearningOpportunityDAO = mockChildDAO();
        dataStatusDAO = mockDataStatudDAO();
        pictureDAO = mockPictureDAO();
        upperSecondaryLearningOpportunitySpecificationDAO = mockUpSecDAO();
        specialLearningOpportunitySpecificationDAO = mockSpecialDAO();
        providerDAO = mockProviderDAO();
        service = new EducationDataQueryServiceImpl(parentLearningOpportunitySpecificationDAO,
                applicationOptionDAO, modelMapper, childLearningOpportunityDAO,
                dataStatusDAO, pictureDAO, upperSecondaryLearningOpportunitySpecificationDAO,
                specialLearningOpportunitySpecificationDAO, providerDAO);
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLOS plo = service.getParentLearningOpportunity("1.2.3");
        assertNotNull(plo);
        assertEquals("1.2.3", plo.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetParentLearningOpportunityNotExists() throws ResourceNotFoundException {
        service.getParentLearningOpportunity("1.1.1");
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
    public void testGetChildLearningOpportunity() throws ResourceNotFoundException {
        ChildLOS child = service.getChildLearningOpportunity("childid");
        assertNotNull(child);
        assertEquals("childid", child.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetChildLearningOpportunityNotFound() throws ResourceNotFoundException {
        service.getChildLearningOpportunity(NOTFOUND);
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
    public void testGetUpperSecondaryLearningOpportunity() throws ResourceNotFoundException {
        UpperSecondaryLOS los  = service.getUpperSecondaryLearningOpportunity("upsecid");
        assertNotNull(los);
        assertEquals("upsecid", los.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetUpperSecondaryLearningOpportunityNotFound() throws ResourceNotFoundException {
        service.getUpperSecondaryLearningOpportunity(NOTFOUND);
    }

    @Test
    public void testGetSpecialLearningOpportunity() throws ResourceNotFoundException {
        SpecialLOS los = service.getSpecialLearningOpportunity("specialid");
        assertNotNull(los);
        assertEquals("specialid", los.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetSpecialLearningOpportunityNotFound() throws ResourceNotFoundException {
        service.getSpecialLearningOpportunity(NOTFOUND);
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

}
