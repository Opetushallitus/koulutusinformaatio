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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;

/**
 * 
 * @author Markus
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class HigherEducationLOSDAOTest {
	
	@Autowired
    private HigherEducationLOSDAO higherEducationDAO;
    @Autowired
    private LearningOpportunityProviderDAO providerDAO;
    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @After
    public void removeTestData() {
        higherEducationDAO.getCollection().drop();
        providerDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
    }

    @Test
    public void testSave() {
        assertEquals(0, higherEducationDAO.count());
        assertEquals(0, providerDAO.count());
        assertEquals(0, applicationOptionDAO.count());

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId("providerid");
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("aoid");
        HigherEducationLOSEntity los = new HigherEducationLOSEntity();
        los.setId("losid");
        
        HigherEducationLOSEntity child = new HigherEducationLOSEntity();
        child.setId("childid");
        
        los.setApplicationOptions(Lists.newArrayList(ao));
        List<HigherEducationLOSEntity> children = new ArrayList<HigherEducationLOSEntity>();
        children.add(child);
        los.setChildren(children);
        los.setProvider(provider);

        applicationOptionDAO.save(ao);
        higherEducationDAO.save(child);
        higherEducationDAO.save(los);
        providerDAO.save(provider);

        assertEquals(1, applicationOptionDAO.count());
        assertEquals(1, providerDAO.count());
        assertEquals(2, higherEducationDAO.count());

        HigherEducationLOSEntity fromDB = higherEducationDAO.get("losid");
        assertNotNull(fromDB);
        assertEquals(los.getId(), fromDB.getId());
        assertNotNull(fromDB.getProvider());
        assertEquals(provider.getId(), fromDB.getProvider().getId());
        assertNotNull(los.getChildren());
        assertEquals(1, los.getChildren().size());
        assertNotNull(los.getChildren().get(0));
        assertEquals(child.getId(), fromDB.getChildren().get(0).getId());
        assertNotNull(los.getApplicationOptions());
        assertEquals(1, los.getApplicationOptions().size());
        assertEquals(ao.getId(), los.getApplicationOptions().get(0).getId());
    }

    @Test
    public void testNotFound() {
        HigherEducationLOSEntity entity = higherEducationDAO.get("invalid");
        assertNull(entity);
    }

    @Test
    public void testFindByProviderId() {
        String providerId = "providerdi";
        LearningOpportunityProviderEntity provider= new LearningOpportunityProviderEntity();
        provider.setId(providerId);
        HigherEducationLOSEntity entity = new HigherEducationLOSEntity();
        entity.setId("parentId");
        entity.setProvider(provider);
        learningOpportunityProviderDAO.save(provider);
        higherEducationDAO.save(entity);
        List<HigherEducationLOSEntity> fromDB =
                higherEducationDAO.findByProviderId(providerId);
        assertNotNull(fromDB);
        assertEquals(1, fromDB.size());
        assertEquals(entity.getId(), fromDB.get(0).getId());

    }

    @Test
    public void testFindByProviderIdNotFound() {
        String providerId = "providerdi";
        LearningOpportunityProviderEntity provider= new LearningOpportunityProviderEntity();
        provider.setId(providerId);
        HigherEducationLOSEntity entity = new HigherEducationLOSEntity();
        entity.setId("parentId");
        entity.setProvider(provider);
        learningOpportunityProviderDAO.save(provider);
        higherEducationDAO.save(entity);
        List<HigherEducationLOSEntity> fromDB =
                higherEducationDAO.findByProviderId("invalid");
        assertNotNull(fromDB);
        assertEquals(0, fromDB.size());
    }

}
