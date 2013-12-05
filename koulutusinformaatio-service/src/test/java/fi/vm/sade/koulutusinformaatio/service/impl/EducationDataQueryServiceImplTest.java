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
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
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

    private EducationDataQueryServiceImpl service;
    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private PictureDAO pictureDAO;
    private DataStatusDAO dataStatusDAO;
    private DBCollection ploCollection;
    private DBCollection aoCollection;
    private DBCollection lopCollection;
    private DBCollection cloCollection;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunitySpecificationDAO = mock(ParentLearningOpportunitySpecificationDAO.class);
        ploCollection = mock(DBCollection.class);
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentLearningOpportunitySpecificationDAO.getCollection()).thenReturn(ploCollection);
        when(parentLearningOpportunitySpecificationDAO.get(eq("1.2.3"))).thenReturn(plo);
        applicationOptionDAO = mock(ApplicationOptionDAO.class);
        aoCollection = mock(DBCollection.class);
        when(applicationOptionDAO.getCollection()).thenReturn(aoCollection);

        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("8.9.0");
        aos.add(ao);
        when(applicationOptionDAO.find(eq("1.1.1"), eq("9.9.9"), eq("1"))).thenReturn(aos);
        when(applicationOptionDAO.find(eq(Lists.newArrayList("8.9.0")))).thenReturn(aos);
        learningOpportunityProviderDAO = mock(LearningOpportunityProviderDAO.class);
        lopCollection = mock(DBCollection.class);
        when(learningOpportunityProviderDAO.getCollection()).thenReturn(lopCollection);

        cloCollection = mock(DBCollection.class);
        childLearningOpportunityDAO = mock(ChildLearningOpportunityDAO.class);
        when(childLearningOpportunityDAO.getCollection()).thenReturn(cloCollection);

        dataStatusDAO = mock(DataStatusDAO.class);
        pictureDAO = mock(PictureDAO.class);
        upperSecondaryLearningOpportunitySpecificationDAO = mock(UpperSecondaryLearningOpportunitySpecificationDAO.class);
        specialLearningOpportunitySpecificationDAO = mock(SpecialLearningOpportunitySpecificationDAO.class);

        service = new EducationDataQueryServiceImpl(parentLearningOpportunitySpecificationDAO,
                applicationOptionDAO, modelMapper, childLearningOpportunityDAO,
                dataStatusDAO, pictureDAO, upperSecondaryLearningOpportunitySpecificationDAO, specialLearningOpportunitySpecificationDAO);
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLOS plo = service.getParentLearningOpportunity("1.2.3");
        assertNotNull(plo);
        assertEquals("1.2.3", plo.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetParentLearningOpportunityNotExists() throws ResourceNotFoundException {
        ParentLOS plo = service.getParentLearningOpportunity("1.1.1");
    }

    @Test
    public void testFindApplicationOptions() {
        List<ApplicationOption> result = service.findApplicationOptions("1.1.1", "9.9.9", "1");
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
}
