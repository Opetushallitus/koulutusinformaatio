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

package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class KoulutusinformaatioObjectBuilderTest {

    private KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder;
    private ModelMapper modelMapper;
    private ChildLearningOpportunitySpecificationEntity childLOS;
    private ChildLearningOpportunityEntity childLOI;
    private ApplicationOptionEntity ao;
    private LearningOpportunityProviderEntity provider;

    @Before
    public void setUp() {
        modelMapper = new ModelMapper();
        koulutusinformaatioObjectBuilder = new KoulutusinformaatioObjectBuilder(modelMapper);

        childLOS = new ChildLearningOpportunitySpecificationEntity();
        childLOS.setId("childLOS123");
        childLOS.setName(TestUtil.createI18nTextEntity("child los fi", "child los sv", "child los en"));
        childLOS.setDegreeTitle(TestUtil.createI18nTextEntity("DegreeTitle fi", "DegreeTitle sv", "DegreeTitle en"));
        childLOS.setQualification(TestUtil.createI18nTextEntity("Qualification fi", "Qualification sv", "Qualification en"));
        ParentLOSRefEntity p = new ParentLOSRefEntity();
        p.setId("p12345");
        p.setName(TestUtil.createI18nTextEntity("p fi", "p sv", "p en"));
        childLOS.setParent(p);

        childLOI = new ChildLearningOpportunityEntity();
        childLOI.setId("childLOI123");

        ChildLORefEntity ref1 = new ChildLORefEntity();
        ref1.setNameByTeachingLang("ref1");
        ref1.setAsId("as123");
        ref1.setLosId("childLOS123");
        ref1.setLoiId("childLOI124");
        ChildLORefEntity ref2 = new ChildLORefEntity();
        ref2.setNameByTeachingLang("ref2");
        ref2.setAsId("as123");
        ref2.setLosId("childLOS123");
        ref2.setLoiId("childLOI125");

        childLOI.setRelated(Lists.newArrayList(ref1, ref2));
        childLOI.setFormOfEducation(Lists.newArrayList(TestUtil.createI18nTextEntity("FormOfEducation fi", "FormOfEducation sv", "FormOfEducation en"),
                TestUtil.createI18nTextEntity("FormOfEducation2 fi", "FormOfEducation2 sv", "FormOfEducation2 en")));
        childLOI.setFormOfTeaching(Lists.newArrayList(TestUtil.createI18nTextEntity("FormOfTeaching fi", "FormOfTeaching sv", "FormOfTeaching en"),
                TestUtil.createI18nTextEntity("FormOfTeaching2 fi", "FormOfTeaching2 sv", "FormOfTeaching2 en")));
        childLOI.setPrerequisite(TestUtil.createI18nTextEntity("Prerequisite fi", "Prerequisite sv", "Prerequisite en"));
        childLOI.setStartDate(new Date());
        CodeEntity c = new CodeEntity();
        c.setValue("fi");
        c.setDescription(TestUtil.createI18nTextEntity("suomi fi", "suomi sv", "suomi en"));
        childLOI.setTeachingLanguages(Lists.newArrayList(c));

        Map<String, String> weblinks = new HashMap<String, String>();
        weblinks.put("link1", "link1");
        weblinks.put("link2", "link2");
        childLOI.setWebLinks(weblinks);

        ao = new ApplicationOptionEntity();
        ao.setId("ao123");
        ao.setName(TestUtil.createI18nTextEntity("ao fi", "ao sv", "ao en"));
        ApplicationSystemEntity as = new ApplicationSystemEntity();
        as.setId("as123");
        ao.setApplicationSystem(as);
        ao.setAttachmentDeliveryDeadline(new Date());
        ao.setEducationDegree("23");
        ao.setLastYearApplicantCount(100);
        ao.setLowestAcceptedAverage(66.7);
        ao.setLowestAcceptedScore(78);
        ao.setStartingQuota(98);
        ao.setChildLORefs(Lists.newArrayList(TestUtil.createChildLORef("c1", as.getId(), childLOS.getId(), childLOI.getId()),
                TestUtil.createChildLORef("c2", as.getId(), childLOS.getId(), childLOI.getId())));

        provider = new LearningOpportunityProviderEntity();
        provider.setId("provider123");
        provider.setName(TestUtil.createI18nTextEntity("pr fi", "pr sv", "pr en"));
        provider.setApplicationSystemIds(Sets.newHashSet("as123", "as124"));
        ao.setProvider(provider);

        childLOI.setApplicationOption(ao);

        childLOS.setChildLOIs(Lists.newArrayList(childLOI));
    }

    @Test
    public void testBuildChildLORef() throws Exception {
        ChildLORefEntity ref = koulutusinformaatioObjectBuilder.buildChildLORef(childLOS, childLOI);
        assertNotNull(ref);
        assertEquals(childLOI.getApplicationSystemId(), ref.getAsId());
        assertEquals(childLOI.getId(), ref.getLoiId());
        assertEquals(childLOS.getId(), ref.getLosId());
        assertEquals(childLOS.getName().getTranslations().get("fi"), ref.getNameByTeachingLang());
    }

    @Test
    public void testBuildChildLO() throws Exception {
        ChildLO childLO = koulutusinformaatioObjectBuilder.buildChildLO(childLOS, childLOI);
        assertNotNull(childLO);
        assertEquals(childLOS.getId(), childLO.getLosId());
        assertEquals(childLOI.getId(), childLO.getLoiId());
        assertEquals(childLOS.getName().getTranslations().get("fi"), childLO.getName().getTranslations().get("fi"));
        assertEquals(childLOI.getApplicationOption().getId(), childLO.getApplicationOption().getId());
        assertEquals(childLOS.getDegreeTitle().getTranslations().get("fi"), childLO.getDegreeTitle().getTranslations().get("fi"));
        assertEquals(childLOI.getFormOfTeaching().get(0).getTranslations().get("fi"), childLO.getFormOfTeaching().get(0).getTranslations().get("fi"));
        assertEquals(childLOI.getFormOfEducation().get(0).getTranslations().get("fi"), childLO.getFormOfEducation().get(0).getTranslations().get("fi"));
        assertEquals(childLOS.getParent().getId(), childLO.getParent().getId());
        assertEquals(childLOI.getStartDate(), childLO.getStartDate());
        assertEquals(childLOS.getQualification().getTranslations().get("fi"), childLO.getQualification().getTranslations().get("fi"));
        assertEquals(childLOI.getPrerequisite().getTranslations().get("fi"), childLO.getPrerequisite().getTranslations().get("fi"));
        assertEquals(childLOI.getRelated().get(0).getNameByTeachingLang(), childLO.getRelated().get(0).getNameByTeachingLang());
        assertEquals("link1", childLO.getWebLinks().get("link1"));
        assertEquals(childLOI.getTeachingLanguages().get(0).getValue(), childLO.getTeachingLanguages().get(0).getValue());
    }
}
