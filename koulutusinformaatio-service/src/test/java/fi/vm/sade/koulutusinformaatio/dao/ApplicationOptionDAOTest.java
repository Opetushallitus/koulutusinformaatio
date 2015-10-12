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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationSystemEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLOIRefEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.integrationtest.TestHelper;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

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

    @Autowired
    private TestHelper testHelper;

    @After
    public void removeTestData() {
        testHelper.removeTestData();
    }

    @Test
    public void testSave() {
        assertEquals(0, applicationOptionDAO.count());
        ApplicationOptionEntity entity = new ApplicationOptionEntity();
        entity.setId("1.2.3");
        entity.setName(TestUtil.createI18nTextEntity("ao name fi", "ao name sv", "ao name en"));
        ApplicationSystemEntity as = new ApplicationSystemEntity();
        as.setId("123");
        entity.setApplicationSystem(as);
        entity.setEducationDegree("degree");
        List<ChildLOIRefEntity> childLoRefs = new ArrayList<ChildLOIRefEntity>();
        childLoRefs.add(TestUtil.createChildLORefEntity("clo 1", as.getId(), "333"));
        childLoRefs.add(TestUtil.createChildLORefEntity("clo 2", as.getId(), "444"));
        entity.setChildLOIRefs(childLoRefs);

        LearningOpportunityProviderEntity lop = new LearningOpportunityProviderEntity();
        lop.setId("3.3.3");
        lop.setName(TestUtil.createI18nTextEntity("lop name fi", "lop name sv", "lop name en"));
        entity.setProvider(lop);
        learningOpportunityProviderDAO.save(lop);

        applicationOptionDAO.save(entity);
        assertEquals(1, applicationOptionDAO.count());
        ApplicationOptionEntity fromDB = applicationOptionDAO.get("1.2.3");
        assertNotNull(fromDB);
        assertEquals(entity.getId(), fromDB.getId());
        assertEquals(entity.getName().getTranslations().get("fi"), fromDB.getName().getTranslations().get("fi"));
        assertEquals(entity.getApplicationSystem().getId(), fromDB.getApplicationSystem().getId());
        assertEquals(entity.getEducationDegree(), fromDB.getEducationDegree());
        assertNotNull(entity.getProvider());
        assertEquals(lop.getId(), entity.getProvider().getId());
        assertNotNull(entity.getChildLOIRefs());
        assertEquals(2, entity.getChildLOIRefs().size());
    }
}
