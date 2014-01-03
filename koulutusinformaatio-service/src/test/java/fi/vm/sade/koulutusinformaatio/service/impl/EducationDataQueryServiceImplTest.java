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
import com.mongodb.DBCollection;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class EducationDataQueryServiceImplTest {

    private final static String NOTFOUND = "notfound";

    private EducationDataQueryServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private PictureDAO pictureDAO;
    private DataStatusDAO dataStatusDAO;
    private Date lastDataUpdate;

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
        service = new EducationDataQueryServiceImpl(parentLearningOpportunitySpecificationDAO,
                applicationOptionDAO, modelMapper, childLearningOpportunityDAO,
                dataStatusDAO, pictureDAO, upperSecondaryLearningOpportunitySpecificationDAO,
                specialLearningOpportunitySpecificationDAO);
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

    private SpecialLearningOpportunitySpecificationDAO mockSpecialDAO() {
        SpecialLearningOpportunitySpecificationEntity entity = new SpecialLearningOpportunitySpecificationEntity();
        entity.setId("specialid");
        SpecialLearningOpportunitySpecificationDAO dao = mock(SpecialLearningOpportunitySpecificationDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    private UpperSecondaryLearningOpportunitySpecificationDAO mockUpSecDAO() {
        UpperSecondaryLearningOpportunitySpecificationEntity entity = new UpperSecondaryLearningOpportunitySpecificationEntity();
        entity.setId("upsecid");
        UpperSecondaryLearningOpportunitySpecificationDAO dao = mock(UpperSecondaryLearningOpportunitySpecificationDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    private PictureDAO mockPictureDAO() {
        PictureEntity entity = new PictureEntity();
        entity.setId("pictureid");
        entity.setPictureEncoded("encoded");
        PictureDAO dao = mock(PictureDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    private DataStatusDAO mockDataStatudDAO() {
        DataStatusEntity entity = new DataStatusEntity();
        entity.setLastUpdateOutcome("SUCCESS");
        entity.setLastUpdateFinished(lastDataUpdate);
        entity.setLastUpdateDuration(1000L);
        DataStatusDAO dao = mock(DataStatusDAO.class);
        when(dao.getLatest()).thenReturn(entity);
        return dao;
    }

    private ChildLearningOpportunityDAO mockChildDAO() {
        ChildLearningOpportunitySpecificationEntity entity = new ChildLearningOpportunitySpecificationEntity();
        entity.setId("childid");
        ChildLearningOpportunityDAO dao = mock(ChildLearningOpportunityDAO.class);
        when(dao.get(entity.getId())).thenReturn(entity);
        when(dao.get(NOTFOUND)).thenReturn(null);
        return dao;
    }

    private ParentLearningOpportunitySpecificationDAO mockParentDAO() {
        ParentLearningOpportunitySpecificationDAO parentDAO = mock(ParentLearningOpportunitySpecificationDAO.class);
        DBCollection ploCollection = mock(DBCollection.class);
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentDAO.getCollection()).thenReturn(ploCollection);
        when(parentDAO.get(eq("1.2.3"))).thenReturn(plo);
        return parentDAO;
    }

    private ApplicationOptionDAO mockApplicationOptionDAO() {
        ApplicationOptionDAO aoDAO = mock(ApplicationOptionDAO.class);
        DBCollection aoCollection = mock(DBCollection.class);
        when(aoDAO.getCollection()).thenReturn(aoCollection);
        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("8.9.0");
        aos.add(ao);
        when(aoDAO.find(eq("1.1.1"), eq("9.9.9"), eq("1"), eq(true), eq(true))).thenReturn(aos);
        when(aoDAO.find(eq(Lists.newArrayList("8.9.0")))).thenReturn(aos);
        return aoDAO;
    }
}
