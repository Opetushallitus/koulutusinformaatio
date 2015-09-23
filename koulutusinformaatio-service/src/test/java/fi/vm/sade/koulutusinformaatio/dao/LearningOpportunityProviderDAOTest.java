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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.integrationtest.TestHelper;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class LearningOpportunityProviderDAOTest {

    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @Autowired
    private TestHelper testHelper;

    @After
    public void removeTestData() {
        testHelper.removeTestData();
    }

    @Test
    public void testSave() {
        assertEquals(0, learningOpportunityProviderDAO.count());
        LearningOpportunityProviderEntity entity = new LearningOpportunityProviderEntity();
        String oid = "1.2.3.4.5";
        entity.setId(oid);
        entity.setName(TestUtil.createI18nTextEntity("lop name fi", "lop name sv", "lop name en"));
        Set<String> asIds = new HashSet<String>();
        asIds.add("1.2.3");
        asIds.add("2.2.2");
        asIds.add("3.3.3");
        entity.setApplicationSystemIds(asIds);
        learningOpportunityProviderDAO.save(entity);
        assertEquals(1, learningOpportunityProviderDAO.count());
        LearningOpportunityProviderEntity fromDb = learningOpportunityProviderDAO.get(oid);
        assertNotNull(fromDb);
        assertEquals(oid, fromDb.getId());
        assertEquals(entity.getName().getTranslations().get("fi"), fromDb.getName().getTranslations().get("fi"));
        assertNotNull(fromDb.getApplicationSystemIds());
        assertEquals(3, fromDb.getApplicationSystemIds().size());
    }
}
