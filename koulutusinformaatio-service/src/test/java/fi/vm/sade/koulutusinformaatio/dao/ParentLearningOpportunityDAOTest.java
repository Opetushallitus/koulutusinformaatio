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

package fi.vm.sade.koulutusinformaatio.dao;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ParentLearningOpportunityDAOTest {

    @Autowired
    private ParentLearningOpportunityDAO parentLearningOpportunityDAO;
    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @After
    public void removeTestData() {
        parentLearningOpportunityDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
    }

    @Test
    public void testSave() {
        assertEquals(0, parentLearningOpportunityDAO.count());
        assertEquals(0, applicationOptionDAO.count());
        assertEquals(0, learningOpportunityProviderDAO.count());
        ParentLearningOpportunityEntity entity = new ParentLearningOpportunityEntity();
        entity.setId("1.2.3.4.5");
        entity.setName("parent name");
        entity.setEducationDegree("degree");
        List<ChildLearningOpportunityEntity> children = new ArrayList<ChildLearningOpportunityEntity>();
        ChildLearningOpportunityEntity child = new ChildLearningOpportunityEntity();
        child.setId("2.2.2");
        child.setName("child name");
        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("7.7.7");
        ao.setApplicationSystemId("sysId");
        ao.setEducationDegree("degree");
        ao.setName("ao name");
        aos.add(ao);
        child.setApplicationOptions(aos);
        entity.setApplicationOptions(aos);
        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId("5.5.5");
        provider.setName("provider name");
        children.add(child);
        entity.setChildren(children);
        entity.setProvider(provider);
        applicationOptionDAO.save(ao);
        parentLearningOpportunityDAO.save(entity);
        learningOpportunityProviderDAO.save(provider);
        assertEquals(1, applicationOptionDAO.count());
        assertEquals(1, parentLearningOpportunityDAO.count());
        assertEquals(1, learningOpportunityProviderDAO.count());
        ParentLearningOpportunityEntity fromDB = parentLearningOpportunityDAO.get("1.2.3.4.5");
        assertNotNull(fromDB);
        assertNotNull(fromDB.getChildren());
        assertNotNull(fromDB.getApplicationOptions());
        assertEquals(1, fromDB.getChildren().size());
        assertEquals(1, fromDB.getApplicationOptions().size());
        assertNotNull(fromDB.getChildren().get(0).getApplicationOptions());
        assertEquals(1, fromDB.getChildren().get(0).getApplicationOptions().size());
        assertEquals(entity.getId(), fromDB.getId());
        assertEquals(entity.getChildren().get(0).getId(), fromDB.getChildren().get(0).getId());
        assertEquals(entity.getApplicationOptions().get(0).getId(), fromDB.getApplicationOptions().get(0).getId());
        assertEquals(entity.getChildren().get(0).getApplicationOptions().get(0).getId(),
                fromDB.getChildren().get(0).getApplicationOptions().get(0).getId());
        assertNotNull(fromDB.getProvider());
        assertEquals(provider.getId(), fromDB.getProvider().getId());
    }

    @Test
    public void testGetParentLearningOpportunity() {
        String oid = "1.2.3";
        ParentLearningOpportunityEntity plo = new ParentLearningOpportunityEntity();
        plo.setId(oid);
        parentLearningOpportunityDAO.save(plo);
        ParentLearningOpportunityEntity fromDB = parentLearningOpportunityDAO.get(oid);
        assertNotNull(fromDB);
        assertEquals(oid, fromDB.getId());
    }

    @Test
    public void testGetParentLearningOpportunityNotFound() {
        ParentLearningOpportunityEntity entity = parentLearningOpportunityDAO.get("1.1.1");
        assertNull(entity);
    }
}
