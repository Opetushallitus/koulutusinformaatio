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

package fi.vm.sade.koulutusinformaatio.modelmapper;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class ModelMapperTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testMapParentLearningOpportunityToEntity() {
        ParentLearningOpportunity parent = new ParentLearningOpportunity();
        parent.setId("123");
        parent.setName("parentName");
        parent.setEducationDegree("degree");

        List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
        ApplicationOption ao = new ApplicationOption();
        ao.setId("8.8.8");
        ao.setApplicationSystemId("3.4.3");
        ao.setEducationDegree("e degree");
        ao.setName("ao name");
        applicationOptions.add(ao);
        parent.setApplicationOptions(applicationOptions);

        ChildLearningOpportunity child = new ChildLearningOpportunity("111", "child1Name");
        child.setApplicationOptions(applicationOptions);
        ChildLearningOpportunity child2 = new ChildLearningOpportunity("222", "child2Name");
        child2.setApplicationOptions(applicationOptions);

        List<ChildLearningOpportunity> children = new ArrayList<ChildLearningOpportunity>();
        children.add(child);
        children.add(child2);
        parent.setChildren(children);

        ParentLearningOpportunityEntity entity = modelMapper.map(parent, ParentLearningOpportunityEntity.class);

        assertNotNull(entity);
        assertEquals(parent.getId(), entity.getId());
        assertEquals(parent.getName(), entity.getName());
        assertNotNull(entity.getChildren());
        assertEquals(2, entity.getChildren().size());
        assertEquals(child.getId(), entity.getChildren().get(0).getId());
        assertEquals(child.getName(), entity.getChildren().get(0).getName());
        assertEquals(child2.getId(), entity.getChildren().get(1).getId());
        assertEquals(child2.getName(), entity.getChildren().get(1).getName());
        assertNotNull(entity.getApplicationOptions());
        assertEquals(1, entity.getApplicationOptions().size());
        assertEquals(ao.getId(), entity.getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), entity.getChildren().get(0).getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), entity.getChildren().get(1).getApplicationOptions().get(0).getId());
    }

    @Test
    public void testMapParentLearningOpportunityEntityToDomainObject() {
        ParentLearningOpportunityEntity entity = new ParentLearningOpportunityEntity();
        entity.setId("999");
        entity.setName("entityName");

        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("900");
        ao.setName("ao name");
        ao.setApplicationSystemId("54543");
        ao.setEducationDegree("degree");
        aos.add(ao);
        entity.setApplicationOptions(aos);
        List<ChildLearningOpportunityEntity> children = new ArrayList<ChildLearningOpportunityEntity>();
        ChildLearningOpportunityEntity child = new ChildLearningOpportunityEntity();
        child.setId("444");
        child.setName("child1EntityName");
        child.setApplicationOptions(aos);
        children.add(child);
        ChildLearningOpportunityEntity child2 = new ChildLearningOpportunityEntity();
        child2.setId("555");
        child2.setName("child2EntityName");
        child2.setApplicationOptions(aos);
        children.add(child2);

        entity.setChildren(children);

        ParentLearningOpportunity domain = modelMapper.map(entity, ParentLearningOpportunity.class);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName(), domain.getName());
        assertNotNull(domain.getChildren());
        assertEquals(2, domain.getChildren().size());
        assertEquals(child.getId(), domain.getChildren().get(0).getId());
        assertEquals(child.getName(), domain.getChildren().get(0).getName());
        assertEquals(child2.getId(), domain.getChildren().get(1).getId());
        assertEquals(child2.getName(), domain.getChildren().get(1).getName());
        assertNotNull(domain.getApplicationOptions());
        assertEquals(1, domain.getApplicationOptions().size());
        assertEquals(ao.getId(), domain.getApplicationOptions().get(0).getId());
        assertEquals(1, domain.getChildren().get(0).getApplicationOptions().size());
        assertEquals(1, domain.getChildren().get(1).getApplicationOptions().size());
        assertEquals(ao.getId(), domain.getChildren().get(0).getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), domain.getChildren().get(1).getApplicationOptions().get(0).getId());
    }
}
