///*
//* Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
//*
//* This program is free software:  Licensed under the EUPL, Version 1.1 or - as
//* soon as they will be approved by the European Commission - subsequent versions
//* of the EUPL (the "Licence");
//*
//* You may not use this work except in compliance with the Licence.
//* You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//*
//* This program is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* European Union Public Licence for more details.
//*/
//
//package fi.vm.sade.koulutusinformaatio.converter;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import fi.vm.sade.koulutusinformaatio.domain.*;
//import fi.vm.sade.koulutusinformaatio.util.TestUtil;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
//* @author Mikko Majapuro
//*/
//public class KoulutusinformaatioObjectBuilderTest {
//
//    private ChildLOS childLearningOpportunity;
//    private ApplicationOption ao;
//    private Provider provider;
//
//    @Before
//    public void setUp() {
//        childLearningOpportunity = new ChildLOS();
//        childLearningOpportunity.setId("childLOS123");
//        childLearningOpportunity.setName(TestUtil.createI18nText("child los fi", "child los sv", "child los en"));
//        childLearningOpportunity.setDegreeTitle(TestUtil.createI18nText("DegreeTitle fi", "DegreeTitle sv", "DegreeTitle en"));
//        childLearningOpportunity.setQualification(TestUtil.createI18nText("Qualification fi", "Qualification sv", "Qualification en"));
//        ParentLOSRef p = new ParentLOSRef();
//        p.setId("p12345");
//        p.setName(TestUtil.createI18nText("p fi", "p sv", "p en"));
//        childLearningOpportunity.setParent(p);
//
//        ChildLOIRef ref1 = new ChildLOIRef();
//        ref1.setNameByTeachingLang("ref1");
//        ref1.setAsIds(Lists.newArrayList("as123"));
//        ref1.setChildLOId("childLOS123");
//        ChildLOIRef ref2 = new ChildLOIRef();
//        ref2.setNameByTeachingLang("ref2");
//        ref2.setAsIds(Lists.newArrayList("as123"));
//        ref2.setChildLOId("childLOI125");
//
//        childLearningOpportunity.setRelated(Lists.newArrayList(ref1, ref2));
//        childLearningOpportunity.setFormOfEducation(Lists.newArrayList(TestUtil.createI18nText("FormOfEducation fi", "FormOfEducation sv", "FormOfEducation en"),
//                TestUtil.createI18nText("FormOfEducation2 fi", "FormOfEducation2 sv", "FormOfEducation2 en")));
//        childLearningOpportunity.setFormOfTeaching(Lists.newArrayList(TestUtil.createI18nText("FormOfTeaching fi", "FormOfTeaching sv", "FormOfTeaching en"),
//                TestUtil.createI18nText("FormOfTeaching2 fi", "FormOfTeaching2 sv", "FormOfTeaching2 en")));
//        Code prerequisite = new Code();
//        prerequisite.setValue("PK");
//        prerequisite.setDescription(TestUtil.createI18nText("Prerequisite fi", "Prerequisite sv", "Prerequisite en"));
//        childLearningOpportunity.setPrerequisite(prerequisite);
//        childLearningOpportunity.setStartDate(new Date());
//        Code c = new Code();
//        c.setValue("fi");
//        c.setDescription(TestUtil.createI18nText("suomi fi", "suomi sv", "suomi en"));
//        childLearningOpportunity.setTeachingLanguages(Lists.newArrayList(c));
//
//        Map<String, String> weblinks = new HashMap<String, String>();
//        weblinks.put("link1", "link1");
//        weblinks.put("link2", "link2");
//        childLearningOpportunity.setWebLinks(weblinks);
//
//        ao = new ApplicationOption();
//        ao.setId("ao123");
//        ao.setName(TestUtil.createI18nText("ao fi", "ao sv", "ao en"));
//        ApplicationSystem as = new ApplicationSystem();
//        as.setId("as123");
//        ao.setApplicationSystem(as);
//        ao.setAttachmentDeliveryDeadline(new Date());
//        ao.setEducationDegree("23");
//        ao.setLastYearApplicantCount(100);
//        ao.setLowestAcceptedAverage(66.7);
//        ao.setLowestAcceptedScore(78);
//        ao.setStartingQuota(98);
//        ao.setChildLOIRefs(Lists.newArrayList(TestUtil.createChildLORef("c1", as.getId(), childLearningOpportunity.getId()),
//                TestUtil.createChildLORef("c2", as.getId(), childLearningOpportunity.getId())));
//
//        provider = new Provider();
//        provider.setId("provider123");
//        provider.setName(TestUtil.createI18nText("pr fi", "pr sv", "pr en"));
//        provider.setApplicationSystemIDs(Sets.newHashSet("as123", "as124"));
//        childLearningOpportunity.setApplicationSystemIds(Lists.newArrayList("as123", "as124"));
//        ao.setProvider(provider);
//
//        childLearningOpportunity.setApplicationOptions(Lists.newArrayList(ao));
//    }
//
//    @Test
//    public void testBuildChildLORef() throws Exception {
//        ChildLOIRef ref = KoulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunity);
//        assertNotNull(ref);
//        assertEquals(childLearningOpportunity.getApplicationSystemIds().get(0), ref.getAsIds().get(0));
//        assertEquals(childLearningOpportunity.getId(), ref.getChildLOId());
//        assertEquals(childLearningOpportunity.getId(), ref.getChildLOId());
//        assertEquals(childLearningOpportunity.getName().getTranslations().get("fi"), ref.getNameByTeachingLang());
//    }
//
//}
