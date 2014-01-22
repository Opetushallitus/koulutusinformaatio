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

package fi.vm.sade.koulutusinformaatio.dao;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class UpperSecondaryLearningOpportunitySpecificationDAOTest {

    @Autowired
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryDAO;
    @Autowired
    private LearningOpportunityProviderDAO providerDAO;
    @Autowired
    private ApplicationOptionDAO applicationOptionDAO;
    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @After
    public void removeTestData() {
        upperSecondaryDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        providerDAO.getCollection().drop();
    }

    @Test
    public void testFind() {
        assertEquals(0, upperSecondaryDAO.count());
        assertEquals(0, providerDAO.count());
        assertEquals(0, applicationOptionDAO.count());

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId("providerid");
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("aoid");
        UpperSecondaryLearningOpportunitySpecificationEntity los = new UpperSecondaryLearningOpportunitySpecificationEntity();
        los.setId("losid");
        UpperSecondaryLearningOpportunityInstanceEntity loi = new UpperSecondaryLearningOpportunityInstanceEntity();
        loi.setId("loiid");
        loi.setApplicationOptions(Lists.newArrayList(ao));
        los.setLois(Lists.newArrayList(loi));
        los.setProvider(provider);

        applicationOptionDAO.save(ao);
        upperSecondaryDAO.save(los);
        providerDAO.save(provider);

        assertEquals(1, applicationOptionDAO.count());
        assertEquals(1, providerDAO.count());
        assertEquals(1, upperSecondaryDAO.count());

        UpperSecondaryLearningOpportunitySpecificationEntity fromDB = upperSecondaryDAO.get("losid");
        assertNotNull(fromDB);
        assertEquals(los.getId(), fromDB.getId());
        assertNotNull(fromDB.getProvider());
        assertEquals(provider.getId(), fromDB.getProvider().getId());
        assertNotNull(los.getLois());
        assertEquals(1, los.getLois().size());
        assertNotNull(los.getLois().get(0));
        assertEquals(loi.getId(), fromDB.getLois().get(0).getId());
        assertNotNull(los.getLois().get(0).getApplicationOptions());
        assertEquals(1, los.getLois().get(0).getApplicationOptions().size());
        assertEquals(ao.getId(), los.getLois().get(0).getApplicationOptions().get(0).getId());
    }

    @Test
    public void testNotFound() {
        UpperSecondaryLearningOpportunitySpecificationEntity entity = upperSecondaryDAO.get("invalid");
        assertNull(entity);
    }

    @Test
    public void testFindByProviderId() {
        String providerId = "providerdi";
        LearningOpportunityProviderEntity provider= new LearningOpportunityProviderEntity();
        provider.setId(providerId);
        UpperSecondaryLearningOpportunitySpecificationEntity entity = new UpperSecondaryLearningOpportunitySpecificationEntity();
        entity.setId("parentId");
        entity.setProvider(provider);
        learningOpportunityProviderDAO.save(provider);
        upperSecondaryDAO.save(entity);
        List<UpperSecondaryLearningOpportunitySpecificationEntity> fromDB =
                upperSecondaryDAO.findByProviderId(providerId);
        assertNotNull(fromDB);
        assertEquals(1, fromDB.size());
        assertEquals(entity.getId(), fromDB.get(0).getId());

    }

    @Test
    public void testFindByProviderIdNotFound() {
        String providerId = "providerdi";
        LearningOpportunityProviderEntity provider= new LearningOpportunityProviderEntity();
        provider.setId(providerId);
        UpperSecondaryLearningOpportunitySpecificationEntity entity = new UpperSecondaryLearningOpportunitySpecificationEntity();
        entity.setId("parentId");
        entity.setProvider(provider);
        learningOpportunityProviderDAO.save(provider);
        upperSecondaryDAO.save(entity);
        List<UpperSecondaryLearningOpportunitySpecificationEntity> fromDB =
                upperSecondaryDAO.findByProviderId("invalid");
        assertNotNull(fromDB);
        assertEquals(0, fromDB.size());
    }
}
