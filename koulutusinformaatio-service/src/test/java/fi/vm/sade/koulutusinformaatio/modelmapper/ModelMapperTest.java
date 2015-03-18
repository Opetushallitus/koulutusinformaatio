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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationSystemEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.ExamEvent;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
* @author Mikko Majapuro
*/
public class ModelMapperTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testMapParentLearningSpecificationOpportunityToEntity() {
        ParentLOS parent = new ParentLOS();
        parent.setId("123");
        parent.setName(TestUtil.createI18nText("parent name fi", "parent name sv", "parent name en"));

        ParentLOI parentLOI = new ParentLOI();
        parentLOI.setId("9.8.7.6");
        parentLOI.setPrerequisite(new Code("PK", TestUtil.createI18nText("Peruskoulu", "Peruskoulu", "Peruskoulu"),
                TestUtil.createI18nText("Peruskoulu", "Peruskoulu", "Peruskoulu")));

        ApplicationOption ao = new ApplicationOption();
        ao.setId("8.8.8");
        ApplicationSystem as = new ApplicationSystem();
        as.setId("3.4.3");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("e degree");
        ao.setName(TestUtil.createI18nText("ao name", "ao name", "ao name"));
        Exam exam = new Exam();
        exam.setDescription(TestUtil.createI18nText("Entrance exam fi", "Entrance exam sv", "Entrance exam en"));
        exam.setType(TestUtil.createI18nText("EXAM TYPE fi", "EXAM TYPE sv", "EXAM TYPE en"));
        ExamEvent examEvent = new ExamEvent();
        Address address = new Address();
        address.setPostalCode("00100");
        address.setPostOffice(TestUtil.createI18nText("Helsinki"));
        address.setStreetAddress(TestUtil.createI18nText("Exam street address"));
        examEvent.setAddress(address);
        examEvent.setDescription("Exam event description");
        examEvent.setStart(new Date());
        examEvent.setEnd(new Date());
        exam.setExamEvents(Lists.newArrayList(examEvent));
        ao.setExams(Lists.newArrayList(exam));

        ChildLOS childLOS = new ChildLOS();
        childLOS.setId("111");
        childLOS.setName(TestUtil.createI18nText("child1Name", "child1Name", "child1Name"));
        ChildLOI childLOI = new ChildLOI();
        childLOI.setId("999");
        childLOI.setLosId("111");
        childLOI.setApplicationOptions(Lists.newArrayList(ao));
        childLOS.setLois(Lists.newArrayList(childLOI));

        ChildLOS childLOS2 = new ChildLOS();
        childLOS2.setId("222");
        childLOS2.setName(TestUtil.createI18nText("child2Name", "child2Name", "child2Name"));
        childLOS2.setId("7733");
        ChildLOI childLOI2 = new ChildLOI();
        childLOI2.setId("888");
        childLOI2.setLosId("222");
        childLOI2.setApplicationOptions(Lists.newArrayList(ao));
        childLOS2.setLois(Lists.newArrayList(childLOI2));

        List<ChildLOS> children = Lists.newArrayList(childLOS, childLOS2);
        parent.setChildren(children);
        parent.setLois(Lists.newArrayList(parentLOI));
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
        ApplicationOptionEntity aoe1 = entity.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0);
        ApplicationOptionEntity aoe2 = entity.getChildren().get(1).getLois().get(0).getApplicationOptions().get(0);
        assertEquals(ao.getId(), aoe1.getId());
        assertEquals(ao.getId(), aoe2.getId());
        assertEquals(ao.getExams().size(), aoe1.getExams().size());
        assertEquals(ao.getExams().size(), aoe2.getExams().size());
        assertEquals(ao.getExams().get(0).getExamEvents().size(), aoe1.getExams().get(0).getExamEvents().size());
        assertEquals(ao.getExams().get(0).getExamEvents().size(), aoe2.getExams().get(0).getExamEvents().size());
        assertEquals(ao.getExams().get(0).getType().getTranslations().get("fi"), aoe1.getExams().get(0).getType().getTranslations().get("fi"));
        assertEquals(ao.getExams().get(0).getType().getTranslations().get("fi"), aoe2.getExams().get(0).getType().getTranslations().get("fi"));
        assertEquals(ao.getExams().get(0).getDescription().getTranslations().get("fi"), aoe1.getExams().get(0).getDescription().getTranslations().get("fi"));
        assertEquals(ao.getExams().get(0).getDescription().getTranslations().get("fi"), aoe2.getExams().get(0).getDescription().getTranslations().get("fi"));
        assertEquals(as.getId(), aoe1.getApplicationSystem().getId());
        assertEquals(as.getId(), aoe2.getApplicationSystem().getId());
    }

    @Test
    public void testMapParentLearningOpportunityEntityToDomainObject() {
        ParentLearningOpportunitySpecificationEntity entity = new ParentLearningOpportunitySpecificationEntity();
        entity.setId("999");
        entity.setName(TestUtil.createI18nTextEntity("entityName", "entityName", "entityName"));
        ParentLearningOpportunityInstanceEntity parenLOIEntity = new ParentLearningOpportunityInstanceEntity();
        parenLOIEntity.setId("112");
        entity.setLois(Lists.newArrayList(parenLOIEntity));

        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("900");
        ao.setName(TestUtil.createI18nTextEntity("ao name", "ao name", "ao name"));
        ApplicationSystemEntity as = new ApplicationSystemEntity();
        as.setId("54543");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("degree");

        List<ChildLearningOpportunitySpecificationEntity> children = new ArrayList<ChildLearningOpportunitySpecificationEntity>();
        ChildLearningOpportunitySpecificationEntity childLOS = new ChildLearningOpportunitySpecificationEntity();
        childLOS.setId("444");
        childLOS.setName(TestUtil.createI18nTextEntity("child1EntityName", "child1EntityName", "child1EntityName"));

        ChildLearningOpportunityInstanceEntity childLOI = new ChildLearningOpportunityInstanceEntity();
        childLOI.setId("9898989");
        childLOI.setApplicationOptions(Lists.newArrayList(ao));
        childLOS.setLois(Lists.newArrayList(childLOI));

        children.add(childLOS);

        ChildLearningOpportunitySpecificationEntity childLOS2 = new ChildLearningOpportunitySpecificationEntity();
        childLOS2.setId("555");
        childLOS2.setName(TestUtil.createI18nTextEntity("child2EntityName", "child2EntityName", "child2EntityName"));
        ChildLearningOpportunityInstanceEntity childLOI2 = new ChildLearningOpportunityInstanceEntity();
        childLOI2.setId("666");
        childLOI2.setApplicationOptions(Lists.newArrayList(ao));
        childLOS2.setLois(Lists.newArrayList(childLOI2));
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
        assertEquals(ao.getId(), domain.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0).getId());
        assertEquals(ao.getId(), domain.getChildren().get(1).getLois().get(0).getApplicationOptions().get(0).getId());
        assertEquals(as.getId(), domain.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0).getApplicationSystem().getId());
        assertEquals(as.getId(), domain.getChildren().get(1).getLois().get(0).getApplicationOptions().get(0).getApplicationSystem().getId());
    }
}
