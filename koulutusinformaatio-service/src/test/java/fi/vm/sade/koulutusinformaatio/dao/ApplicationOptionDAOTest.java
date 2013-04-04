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
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
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

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationOptionDAOTest {

    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @After
    public void removeTestData() {
        applicationOptionDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
    }

    @Test
    public void testSave() {
        assertEquals(0, applicationOptionDAO.count());
        ApplicationOptionEntity entity = new ApplicationOptionEntity();
        entity.setId("1.2.3");
        entity.setName("ao name");
        entity.setApplicationSystemId("123");
        entity.setEducationDegree("degree");
        List<String> childLoNames = new ArrayList<String>();
        childLoNames.add("clo name");
        childLoNames.add("clo name 2");
        entity.setChildLONames(childLoNames);

        LearningOpportunityProviderEntity lop = new LearningOpportunityProviderEntity();
        lop.setId("3.3.3");
        lop.setName("lop name");
        entity.setProvider(lop);
        learningOpportunityProviderDAO.save(lop);

        applicationOptionDAO.save(entity);
        assertEquals(1, applicationOptionDAO.count());
        ApplicationOptionEntity fromDB = applicationOptionDAO.get("1.2.3");
        assertNotNull(fromDB);
        assertEquals(entity.getId(), fromDB.getId());
        assertEquals(entity.getName(), fromDB.getName());
        assertEquals(entity.getApplicationSystemId(), fromDB.getApplicationSystemId());
        assertEquals(entity.getEducationDegree(), fromDB.getEducationDegree());
        assertNotNull(entity.getProvider());
        assertEquals(lop.getId(), entity.getProvider().getId());
        assertNotNull(entity.getChildLONames());
        assertEquals(2, entity.getChildLONames().size());
    }
}
