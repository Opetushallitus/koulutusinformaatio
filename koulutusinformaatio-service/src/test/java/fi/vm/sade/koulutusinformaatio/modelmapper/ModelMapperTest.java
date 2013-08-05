///*
// * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
// *
// * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// * soon as they will be approved by the European Commission - subsequent versions
// * of the EUPL (the "Licence");
// *
// * You may not use this work except in compliance with the Licence.
// * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * European Union Public Licence for more details.
// */
//
//package fi.vm.sade.koulutusinformaatio.modelmapper;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import fi.vm.sade.koulutusinformaatio.dao.entity.*;
//import fi.vm.sade.koulutusinformaatio.domain.*;
//import fi.vm.sade.koulutusinformaatio.util.TestUtil;
//import org.junit.Test;
//import org.modelmapper.ModelMapper;
//
//import java.util.*;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * @author Mikko Majapuro
// */
//public class ModelMapperTest {
//
//    private ModelMapper modelMapper = new ModelMapper();
//
//    @Test
//    public void testMapParentLearningOpportunityToEntity() {
//        ParentLOS parent = new ParentLOS();
//        parent.setId("123");
//        parent.setName(TestUtil.createI18nText("parent name fi", "parent name sv", "parent name en"));
//
//        ParentLOI parentLOI = new ParentLOI();
//        parentLOI.setId("9.8.7.6");
//        parentLOI.setPrerequisite(new Code("PK", TestUtil.createI18nText("Peruskoulu", "Peruskoulu", "Peruskoulu")));
//
//        ApplicationOption ao = new ApplicationOption();
//        ao.setId("8.8.8");
//        ApplicationSystem as = new ApplicationSystem();
//        as.setId("3.4.3");
//        ao.setApplicationSystem(as);
//        ao.setEducationDegree("e degree");
//        ao.setName(TestUtil.createI18nText("ao name", "ao name", "ao name"));
//        Exam exam = new Exam();
//        exam.setDescription(TestUtil.createI18nText("Entrance exam fi", "Entrance exam sv", "Entrance exam en"));
//        exam.setType(TestUtil.createI18nText("EXAM TYPE fi", "EXAM TYPE sv", "EXAM TYPE en"));
//        ExamEvent examEvent = new ExamEvent();
//        Address address = new Address();
//        address.setPostalCode("00100");
//        address.setPostOffice("Helsinki");
//        address.setStreetAddress("Exam street address");
//        examEvent.setAddress(address);
//        examEvent.setDescription("Exam event description");
//        examEvent.setStart(new Date());
//        examEvent.setEnd(new Date());
//        exam.setExamEvents(Lists.newArrayList(examEvent));
//        ao.setExams(Lists.newArrayList(exam));
//        parent.setApplicationOptions(Sets.newHashSet(ao));
//
//        ChildLOS childLO = new ChildLOS("111", TestUtil.createI18nText("child1Name", "child1Name", "child1Name"));
//        childLO.setId("7789");
//        childLO.setApplicationOptions(Lists.newArrayList(ao));
//        childLO.setApplicationSystemIds(Lists.newArrayList("1.2.3.4.5"));
//
//        ChildLOS childLO2 = new ChildLOS("222", TestUtil.createI18nText("child2Name", "child2Name", "child2Name"));
//        childLO2.setId("7733");
//        childLO2.setApplicationOptions(Lists.newArrayList(ao));
//        childLO2.setApplicationSystemIds(Lists.newArrayList("1.2.3.4.5"));
//
//        List<ChildLOS> children = Lists.newArrayList(childLO, childLO2);
//        parentLOI.setChildren(children);
//        parent.setLois(Lists.newArrayList(parentLOI));
//
//        ParentLearningOpportunitySpecificationEntity entity = modelMapper.map(parent, ParentLearningOpportunitySpecificationEntity.class);
//
//        assertNotNull(entity);
//        assertEquals(parent.getId(), entity.getId());
//        assertEquals(parent.getName().getTranslations().get("fi"), entity.getName().getTranslations().get("fi"));
//        assertNotNull(entity.getLois().get(0).getChildren());
//        assertEquals(2, entity.getLois().get(0).getChildren().size());
//        assertEquals(childLO.getId(), entity.getLois().get(0).getChildren().get(0).getId());
//        assertEquals(childLO.getName().getTranslations().get("fi"), entity.getLois().get(0).getChildren().get(0).getName().getTranslations().get("fi"));
//        assertEquals(childLO2.getId(), entity.getLois().get(0).getChildren().get(1).getId());
//        assertEquals(childLO2.getName().getTranslations().get("fi"), entity.getLois().get(0).getChildren().get(1).getName().getTranslations().get("fi"));
//        assertNotNull(entity.getApplicationOptions());
//        assertEquals(1, entity.getApplicationOptions().size());
//        ApplicationOptionEntity aoe = entity.getApplicationOptions().iterator().next();
//
//        assertEquals(ao.getId(), aoe.getId());
//        assertEquals(ao.getId(), entity.getLois().get(0).getChildren().get(0).getApplicationOptions().get(0).getId());
//        assertEquals(ao.getId(), entity.getLois().get(0).getChildren().get(1).getApplicationOptions().get(0).getId());
//        assertEquals(ao.getExams().size(), aoe.getExams().size());
//        assertEquals(ao.getExams().get(0).getExamEvents().size(), aoe.getExams().get(0).getExamEvents().size());
//        assertEquals(ao.getExams().get(0).getType().getTranslations().get("fi"), aoe.getExams().get(0).getType().getTranslations().get("fi"));
//        assertEquals(ao.getExams().get(0).getDescription().getTranslations().get("fi"), aoe.getExams().get(0).getDescription().getTranslations().get("fi"));
//    }
//
//    @Test
//    public void testMapParentLearningOpportunityEntityToDomainObject() {
//        ParentLearningOpportunitySpecificationEntity entity = new ParentLearningOpportunitySpecificationEntity();
//        entity.setId("999");
//        entity.setName(TestUtil.createI18nTextEntity("entityName", "entityName", "entityName"));
//        ParentLearningOpportunityInstanceEntity parenLOIEntity = new ParentLearningOpportunityInstanceEntity();
//        parenLOIEntity.setId("112");
//        entity.setLois(Lists.newArrayList(parenLOIEntity));
//
//        Set<ApplicationOptionEntity> aos = new HashSet<ApplicationOptionEntity>();
//        ApplicationOptionEntity ao = new ApplicationOptionEntity();
//        ao.setId("900");
//        ao.setName(TestUtil.createI18nTextEntity("ao name", "ao name", "ao name"));
//        ApplicationSystemEntity as = new ApplicationSystemEntity();
//        as.setId("54543");
//        ao.setApplicationSystem(as);
//        ao.setEducationDegree("degree");
//        aos.add(ao);
//        entity.setApplicationOptions(aos);
//        List<ChildLearningOpportunityEntity> children = new ArrayList<ChildLearningOpportunityEntity>();
//        ChildLearningOpportunityEntity childLO = new ChildLearningOpportunityEntity();
//        childLO.setId("444");
//        childLO.setName(TestUtil.createI18nTextEntity("child1EntityName", "child1EntityName", "child1EntityName"));
//
//
//        ChildLearningOpportunityEntity childLOI = new ChildLearningOpportunityEntity();
//        childLOI.setId("9898989");
//        childLO.setApplicationSystemIds(Lists.newArrayList("1.2.3.4.5"));
//        childLO.setApplicationOptions(Lists.newArrayList(ao));
//        children.add(childLO);
//
//        ChildLearningOpportunityEntity childLO2 = new ChildLearningOpportunityEntity();
//        childLO2.setId("555");
//        childLO2.setName(TestUtil.createI18nTextEntity("child2EntityName", "child2EntityName", "child2EntityName"));
//        childLO2.setApplicationSystemIds(Lists.newArrayList("1.2.3.4.5"));
//        childLO2.setApplicationOptions(Lists.newArrayList(ao));
//        children.add(childLO2);
//
//        parenLOIEntity.setChildren(children);
//
//        ParentLOS domain = modelMapper.map(entity, ParentLOS.class);
//        assertNotNull(domain);
//        assertEquals(entity.getId(), domain.getId());
//        assertEquals(entity.getName().getTranslations().get("fi"), domain.getName().getTranslations().get("fi"));
//        assertNotNull(domain.getLois().get(0).getChildren());
//        assertEquals(2, domain.getLois().get(0).getChildren().size());
//        assertEquals(childLO.getId(), domain.getLois().get(0).getChildren().get(0).getId());
//        assertEquals(childLO.getName().getTranslations().get("fi"), domain.getLois().get(0).getChildren().get(0).getName().getTranslations().get("fi"));
//        assertEquals(childLO2.getId(), domain.getLois().get(0).getChildren().get(1).getId());
//        assertEquals(childLO2.getName().getTranslations().get("fi"), domain.getLois().get(0).getChildren().get(1).getName().getTranslations().get("fi"));
//        assertNotNull(domain.getApplicationOptions());
//        assertEquals(1, domain.getApplicationOptions().size());
//        //assertEquals(ao.getId(), domain.getApplicationOptions().get(0).getId());
////        assertEquals(ao.getId(), domain.getLois().get(0).getChildren().get(0).getChildLOIs().get(0).getApplicationOptions.getId());
////        assertEquals(ao.getId(), domain.getChildren().get(1).getChildLOIs().get(0).getApplicationOption().getId());
//    }
//}
