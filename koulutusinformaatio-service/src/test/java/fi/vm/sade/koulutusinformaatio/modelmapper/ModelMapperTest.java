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
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Ignore;
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
        ParentLOS parent = new ParentLOS();
        parent.setId("123");
        parent.setName(TestUtil.createI18nText("parent name fi", "parent name sv", "parent name en"));
        parent.setEducationDegree("degree");

        List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
        ApplicationOption ao = new ApplicationOption();
        ao.setId("8.8.8");
        ao.setApplicationSystemId("3.4.3");
        ao.setEducationDegree("e degree");
        ao.setName(TestUtil.createI18nText("ao name", "ao name", "ao name"));
        applicationOptions.add(ao);
        parent.setApplicationOptions(applicationOptions);

        ChildLOS child = new ChildLOS("111", TestUtil.createI18nText("child1Name", "child1Name", "child1Name"));
        child.setApplicationOptions(applicationOptions);
        ChildLOS child2 = new ChildLOS("222", TestUtil.createI18nText("child2Name", "child2Name", "child2Name"));
        child2.setApplicationOptions(applicationOptions);

        List<ChildLOS> children = new ArrayList<ChildLOS>();
        children.add(child);
        children.add(child2);
        parent.setChildren(children);

        ParentLearningOpportunitySpecificationEntity entity = modelMapper.map(parent, ParentLearningOpportunitySpecificationEntity.class);

        assertNotNull(entity);
        assertEquals(parent.getId(), entity.getId());
        assertEquals(parent.getName().getTranslations().get("fi"), entity.getName().getTranslations().get("fi"));
        assertNotNull(entity.getChildren());
        assertEquals(2, entity.getChildren().size());
        assertEquals(child.getId(), entity.getChildren().get(0).getId());
        assertEquals(child.getName().getTranslations().get("fi"), entity.getChildren().get(0).getName().getTranslations().get("fi"));
        assertEquals(child2.getId(), entity.getChildren().get(1).getId());
        assertEquals(child2.getName().getTranslations().get("fi"), entity.getChildren().get(1).getName().getTranslations().get("fi"));
        assertNotNull(entity.getApplicationOptions());
        assertEquals(1, entity.getApplicationOptions().size());
        assertEquals(ao.getId(), entity.getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), entity.getChildren().get(0).getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), entity.getChildren().get(1).getApplicationOptions().get(0).getId());
    }

    @Test
    public void testMapParentLearningOpportunityEntityToDomainObject() {
        ParentLearningOpportunitySpecificationEntity entity = new ParentLearningOpportunitySpecificationEntity();
        entity.setId("999");
        entity.setName(TestUtil.createI18nTextEntity("entityName", "entityName", "entityName"));

        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("900");
        ao.setName(TestUtil.createI18nTextEntity("ao name", "ao name", "ao name"));
        ao.setApplicationSystemId("54543");
        ao.setEducationDegree("degree");
        aos.add(ao);
        entity.setApplicationOptions(aos);
        List<ChildLearningOpportunitySpecificationEntity> children = new ArrayList<ChildLearningOpportunitySpecificationEntity>();
        ChildLearningOpportunitySpecificationEntity child = new ChildLearningOpportunitySpecificationEntity();
        child.setId("444");
        child.setName(TestUtil.createI18nTextEntity("child1EntityName", "child1EntityName", "child1EntityName"));
        child.setApplicationOptions(aos);
        children.add(child);
        ChildLearningOpportunitySpecificationEntity child2 = new ChildLearningOpportunitySpecificationEntity();
        child2.setId("555");
        child2.setName(TestUtil.createI18nTextEntity("child2EntityName", "child2EntityName", "child2EntityName"));
        child2.setApplicationOptions(aos);
        children.add(child2);

        entity.setChildren(children);

        ParentLOS domain = modelMapper.map(entity, ParentLOS.class);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName().getTranslations().get("fi"), domain.getName().getTranslations().get("fi"));
        assertNotNull(domain.getChildren());
        assertEquals(2, domain.getChildren().size());
        assertEquals(child.getId(), domain.getChildren().get(0).getId());
        assertEquals(child.getName().getTranslations().get("fi"), domain.getChildren().get(0).getName().getTranslations().get("fi"));
        assertEquals(child2.getId(), domain.getChildren().get(1).getId());
        assertEquals(child2.getName().getTranslations().get("fi"), domain.getChildren().get(1).getName().getTranslations().get("fi"));
        assertNotNull(domain.getApplicationOptions());
        assertEquals(1, domain.getApplicationOptions().size());
        assertEquals(ao.getId(), domain.getApplicationOptions().get(0).getId());
        assertEquals(1, domain.getChildren().get(0).getApplicationOptions().size());
        assertEquals(1, domain.getChildren().get(1).getApplicationOptions().size());
        assertEquals(ao.getId(), domain.getChildren().get(0).getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), domain.getChildren().get(1).getApplicationOptions().get(0).getId());
    }
}
