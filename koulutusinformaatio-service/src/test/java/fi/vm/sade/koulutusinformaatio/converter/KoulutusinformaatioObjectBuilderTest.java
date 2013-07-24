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
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class KoulutusinformaatioObjectBuilderTest {

    private ChildLearningOpportunityEntity childLearningOpportunity;
    private ApplicationOptionEntity ao;
    private LearningOpportunityProviderEntity provider;

    @Before
    public void setUp() {
        childLearningOpportunity = new ChildLearningOpportunityEntity();
        childLearningOpportunity.setId("childLOS123");
        childLearningOpportunity.setName(TestUtil.createI18nTextEntity("child los fi", "child los sv", "child los en"));
        childLearningOpportunity.setDegreeTitle(TestUtil.createI18nTextEntity("DegreeTitle fi", "DegreeTitle sv", "DegreeTitle en"));
        childLearningOpportunity.setQualification(TestUtil.createI18nTextEntity("Qualification fi", "Qualification sv", "Qualification en"));
        ParentLOSRefEntity p = new ParentLOSRefEntity();
        p.setId("p12345");
        p.setName(TestUtil.createI18nTextEntity("p fi", "p sv", "p en"));
        childLearningOpportunity.setParent(p);

        ChildLORefEntity ref1 = new ChildLORefEntity();
        ref1.setNameByTeachingLang("ref1");
        ref1.setAsIds(Lists.newArrayList("as123"));
        ref1.setChildLOId("childLOS123");
        ChildLORefEntity ref2 = new ChildLORefEntity();
        ref2.setNameByTeachingLang("ref2");
        ref2.setAsIds(Lists.newArrayList("as123"));
        ref2.setChildLOId("childLOI125");

        childLearningOpportunity.setRelated(Lists.newArrayList(ref1, ref2));
        childLearningOpportunity.setFormOfEducation(Lists.newArrayList(TestUtil.createI18nTextEntity("FormOfEducation fi", "FormOfEducation sv", "FormOfEducation en"),
                TestUtil.createI18nTextEntity("FormOfEducation2 fi", "FormOfEducation2 sv", "FormOfEducation2 en")));
        childLearningOpportunity.setFormOfTeaching(Lists.newArrayList(TestUtil.createI18nTextEntity("FormOfTeaching fi", "FormOfTeaching sv", "FormOfTeaching en"),
                TestUtil.createI18nTextEntity("FormOfTeaching2 fi", "FormOfTeaching2 sv", "FormOfTeaching2 en")));
        CodeEntity prerequisite = new CodeEntity();
        prerequisite.setValue("PK");
        prerequisite.setDescription(TestUtil.createI18nTextEntity("Prerequisite fi", "Prerequisite sv", "Prerequisite en"));
        childLearningOpportunity.setPrerequisite(prerequisite);
        childLearningOpportunity.setStartDate(new Date());
        CodeEntity c = new CodeEntity();
        c.setValue("fi");
        c.setDescription(TestUtil.createI18nTextEntity("suomi fi", "suomi sv", "suomi en"));
        childLearningOpportunity.setTeachingLanguages(Lists.newArrayList(c));

        Map<String, String> weblinks = new HashMap<String, String>();
        weblinks.put("link1", "link1");
        weblinks.put("link2", "link2");
        childLearningOpportunity.setWebLinks(weblinks);

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
        ao.setChildLORefs(Lists.newArrayList(TestUtil.createChildLORef("c1", as.getId(), childLearningOpportunity.getId()),
                TestUtil.createChildLORef("c2", as.getId(), childLearningOpportunity.getId())));

        provider = new LearningOpportunityProviderEntity();
        provider.setId("provider123");
        provider.setName(TestUtil.createI18nTextEntity("pr fi", "pr sv", "pr en"));
        provider.setApplicationSystemIds(Sets.newHashSet("as123", "as124"));
        childLearningOpportunity.setApplicationSystemIds(Lists.newArrayList("as123", "as124"));
        ao.setProvider(provider);

        childLearningOpportunity.setApplicationOptions(Lists.newArrayList(ao));
    }

    @Test
    public void testBuildChildLORef() throws Exception {
        ChildLORefEntity ref = KoulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunity);
        assertNotNull(ref);
        assertEquals(childLearningOpportunity.getApplicationSystemIds().get(0), ref.getAsIds().get(0));
        assertEquals(childLearningOpportunity.getId(), ref.getChildLOId());
        assertEquals(childLearningOpportunity.getId(), ref.getChildLOId());
        assertEquals(childLearningOpportunity.getName().getTranslations().get("fi"), ref.getNameByTeachingLang());
    }

}
