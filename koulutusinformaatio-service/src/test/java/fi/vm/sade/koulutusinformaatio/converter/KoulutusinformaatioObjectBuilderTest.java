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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
* @author Mikko Majapuro
*/
public class KoulutusinformaatioObjectBuilderTest {

    private ChildLOI childLearningOpportunityInstance;
    private ApplicationOption ao;

    @Before
    public void setUp() {
        childLearningOpportunityInstance = new ChildLOI();
        childLearningOpportunityInstance.setId("childLOS123");
        childLearningOpportunityInstance.setName(TestUtil.createI18nText("child los fi", "child los sv", "child los en"));

        childLearningOpportunityInstance.setFormOfEducation(Lists.newArrayList(TestUtil.createI18nText("FormOfEducation fi", "FormOfEducation sv", "FormOfEducation en"),
                TestUtil.createI18nText("FormOfEducation2 fi", "FormOfEducation2 sv", "FormOfEducation2 en")));
        childLearningOpportunityInstance.setFormOfTeaching(Lists.newArrayList(TestUtil.createI18nText("FormOfTeaching fi", "FormOfTeaching sv", "FormOfTeaching en"),
                TestUtil.createI18nText("FormOfTeaching2 fi", "FormOfTeaching2 sv", "FormOfTeaching2 en")));
        Code prerequisite = new Code();
        prerequisite.setValue("PK");
        prerequisite.setDescription(TestUtil.createI18nText("Prerequisite fi", "Prerequisite sv", "Prerequisite en"));
        childLearningOpportunityInstance.setPrerequisite(prerequisite);
        Code c = new Code();
        c.setValue("fi");
        c.setDescription(TestUtil.createI18nText("suomi fi", "suomi sv", "suomi en"));
        childLearningOpportunityInstance.setTeachingLanguages(Lists.newArrayList(c));

        ao = new ApplicationOption();
        ao.setId("ao123");
        ao.setName(TestUtil.createI18nText("ao fi", "ao sv", "ao en"));
        ApplicationSystem as = new ApplicationSystem();
        as.setId("as123");
        ao.setChildLOIRefs(Lists.newArrayList(TestUtil.createChildLORef("c1", as.getId(), childLearningOpportunityInstance.getId()),
                TestUtil.createChildLORef("c2", as.getId(), childLearningOpportunityInstance.getId())));
        childLearningOpportunityInstance.setApplicationSystemIds(Lists.newArrayList("as123", "as124"));
        childLearningOpportunityInstance.setApplicationOptions(Lists.newArrayList(ao));
    }

    @Test
    public void testBuildChildLOIRef() throws Exception {

        ChildLOIRef ref = KoulutusinformaatioObjectBuilder.buildChildLOIRef(childLearningOpportunityInstance);
        assertNotNull(ref);
        assertEquals(childLearningOpportunityInstance.getId(), ref.getId());
        assertEquals(childLearningOpportunityInstance.getLosId(), ref.getLosId());
        assertEquals(childLearningOpportunityInstance.getApplicationSystemIds().get(0), ref.getAsIds().get(0));
        assertEquals(childLearningOpportunityInstance.getName().getTranslations().get("fi"),
                ref.getName().getTranslations().get("fi"));
        assertEquals(childLearningOpportunityInstance.getName().getTranslations().get("fi"), ref.getNameByTeachingLang());
        assertEquals(childLearningOpportunityInstance.getPrerequisite().getValue(), ref.getPrerequisite().getValue());
    }

}
