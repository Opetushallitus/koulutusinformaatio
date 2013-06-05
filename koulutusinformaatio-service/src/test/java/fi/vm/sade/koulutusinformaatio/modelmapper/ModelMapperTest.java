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

import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Set<ApplicationOption> applicationOptions = Sets.newHashSet();
        ApplicationOption ao = new ApplicationOption();
        ao.setId("8.8.8");
        ApplicationSystem as = new ApplicationSystem();
        as.setId("3.4.3");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("e degree");
        ao.setName(TestUtil.createI18nText("ao name", "ao name", "ao name"));
        applicationOptions.add(ao);
        parent.setApplicationOptions(applicationOptions);

        ChildLOS childLOS = new ChildLOS("111", TestUtil.createI18nText("child1Name", "child1Name", "child1Name"));
        ChildLOI childLOI = new ChildLOI();
        childLOI.setId("7789");
        childLOI.setApplicationOption(ao);
        childLOI.setApplicationSystemId("1.2.3.4.5");
        childLOS.getChildLOIs().add(childLOI);

        ChildLOS childLOS2 = new ChildLOS("222", TestUtil.createI18nText("child2Name", "child2Name", "child2Name"));
        ChildLOI childLOI2 = new ChildLOI();
        childLOI2.setId("7733");
        childLOI2.setApplicationOption(ao);
        childLOI2.setApplicationSystemId("1.2.3.4.5");
        childLOS2.getChildLOIs().add(childLOI2);

        List<ChildLOS> children = new ArrayList<ChildLOS>();
        children.add(childLOS);
        children.add(childLOS2);
        parent.setChildren(children);

        ParentLearningOpportunitySpecificationEntity entity = modelMapper.map(parent, ParentLearningOpportunitySpecificationEntity.class);

        assertNotNull(entity);
        assertEquals(parent.getId(), entity.getId());
        assertEquals(parent.getName().getTranslations().get("fi"), entity.getName().getTranslations().get("fi"));
        assertNotNull(entity.getChildren());
        assertEquals(2, entity.getChildren().size());
        assertEquals(childLOS.getId(), entity.getChildren().get(0).getId());
        assertEquals(childLOS.getName().getTranslations().get("fi"), entity.getChildren().get(0).getName().getTranslations().get("fi"));
        assertEquals(childLOS2.getId(), entity.getChildren().get(1).getId());
        assertEquals(childLOS2.getName().getTranslations().get("fi"), entity.getChildren().get(1).getName().getTranslations().get("fi"));
        assertNotNull(entity.getApplicationOptions());
        assertEquals(1, entity.getApplicationOptions().size());
        assertEquals(ao.getId(), entity.getApplicationOptions().iterator().next().getId());
        assertEquals(ao.getId(), entity.getChildren().get(0).getChildLOIs().get(0).getApplicationOption().getId());
        assertEquals(ao.getId(), entity.getChildren().get(1).getChildLOIs().get(0).getApplicationOption().getId());
    }

    @Test
    public void testMapParentLearningOpportunityEntityToDomainObject() {
        ParentLearningOpportunitySpecificationEntity entity = new ParentLearningOpportunitySpecificationEntity();
        entity.setId("999");
        entity.setName(TestUtil.createI18nTextEntity("entityName", "entityName", "entityName"));

        Set<ApplicationOptionEntity> aos = new HashSet<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("900");
        ao.setName(TestUtil.createI18nTextEntity("ao name", "ao name", "ao name"));
        ApplicationSystemEntity as = new ApplicationSystemEntity();
        as.setId("54543");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("degree");
        aos.add(ao);
        entity.setApplicationOptions(aos);
        List<ChildLearningOpportunitySpecificationEntity> children = new ArrayList<ChildLearningOpportunitySpecificationEntity>();
        ChildLearningOpportunitySpecificationEntity childLOS = new ChildLearningOpportunitySpecificationEntity();
        childLOS.setId("444");
        childLOS.setName(TestUtil.createI18nTextEntity("child1EntityName", "child1EntityName", "child1EntityName"));

        List<ChildLearningOpportunityInstanceEntity> childLOIs = new ArrayList<ChildLearningOpportunityInstanceEntity>();
        ChildLearningOpportunityInstanceEntity childLOI = new ChildLearningOpportunityInstanceEntity();
        childLOI.setId("9898989");
        childLOI.setApplicationSystemId("1.2.3.4.5");
        childLOI.setApplicationOption(ao);
        childLOIs.add(childLOI);
        childLOS.setChildLOIs(childLOIs);

        children.add(childLOS);

        ChildLearningOpportunitySpecificationEntity childLOS2 = new ChildLearningOpportunitySpecificationEntity();
        childLOS2.setId("555");
        childLOS2.setName(TestUtil.createI18nTextEntity("child2EntityName", "child2EntityName", "child2EntityName"));

        List<ChildLearningOpportunityInstanceEntity> childLOIs2 = new ArrayList<ChildLearningOpportunityInstanceEntity>();
        ChildLearningOpportunityInstanceEntity childLOI2 = new ChildLearningOpportunityInstanceEntity();
        childLOI2.setId("567567567");
        childLOI2.setApplicationSystemId("1.2.3.4.5");
        childLOI2.setApplicationOption(ao);
        childLOIs2.add(childLOI2);
        childLOS2.setChildLOIs(childLOIs2);

        children.add(childLOS2);

        entity.setChildren(children);

        ParentLOS domain = modelMapper.map(entity, ParentLOS.class);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName().getTranslations().get("fi"), domain.getName().getTranslations().get("fi"));
        assertNotNull(domain.getChildren());
        assertEquals(2, domain.getChildren().size());
        assertEquals(childLOS.getId(), domain.getChildren().get(0).getId());
        assertEquals(childLOS.getName().getTranslations().get("fi"), domain.getChildren().get(0).getName().getTranslations().get("fi"));
        assertEquals(childLOS2.getId(), domain.getChildren().get(1).getId());
        assertEquals(childLOS2.getName().getTranslations().get("fi"), domain.getChildren().get(1).getName().getTranslations().get("fi"));
        assertNotNull(domain.getApplicationOptions());
        assertEquals(1, domain.getApplicationOptions().size());
        //assertEquals(ao.getId(), domain.getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), domain.getChildren().get(0).getChildLOIs().get(0).getApplicationOption().getId());
        assertEquals(ao.getId(), domain.getChildren().get(1).getChildLOIs().get(0).getApplicationOption().getId());
    }
}
