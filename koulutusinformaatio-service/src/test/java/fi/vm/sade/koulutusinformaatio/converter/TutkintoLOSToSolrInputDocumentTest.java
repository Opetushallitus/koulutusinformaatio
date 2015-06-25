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
package fi.vm.sade.koulutusinformaatio.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * 
 * @author Markus
 */
public class TutkintoLOSToSolrInputDocumentTest {

    private TutkintoLOS los;
    private TutkintoLOSToSolrInputDocument converter;
    private Code prerequisite;
    private Code lang;
    private Provider provider;
    private ApplicationOption ao;
    private List<I18nText> tutkintonimikkeet;

    @Before
    public void setUp() {
        los = new TutkintoLOS();
        los.setType("parentLos");
        los.setId("parentId");
        los.setName(TestUtil.createI18nText("parent name fi", "parent name sv", "parent name en"));

        lang = new Code();
        lang.setName(TestUtil.createI18nText("Suomi", "Finska", "Finnish"));
        lang.setValue("FI");
        lang.setUri("fi_uri");
        los.setTeachingLanguages(Arrays.asList(lang));

        los.setEducationDomain(TestUtil.createI18nText("parent domain fi", "parent domain sv", "parent name end"));
        los.setStydyDomain(TestUtil.createI18nText("study domain fi", "study domain sv", "study domain en"));

        provider = new Provider();
        provider.setId("provId");
        provider.setName(TestUtil.createI18nText("prov fi", "prov sv", "prov en"));
        provider.setHomeDistrict(TestUtil.createI18nText("Uusimaa fi", "Uusimaa sv", "Uusimaa en"));
        provider.setHomePlace(TestUtil.createI18nText("Hki fi", "Hki sv", "Hki en"));
        Address addr = new Address();
        addr.setPostalCode(TestUtil.createI18nText("04620", "04620", "04620"));
        provider.setVisitingAddress(addr);
        provider.setDescription(TestUtil.createI18nText("prov descr fi", "prov descr sv", "prov descr en"));
        los.setProvider(provider);

        los.setCreditValue("80");
        los.setCreditUnit(TestUtil.createI18nText("ov fi", "ov sv", "ov en"));
        los.setGoals(TestUtil.createI18nText("Goals fi", "Goals sv", "Goals en"));

        ParentLOI parentLoi = new ParentLOI();
        ApplicationSystem as = new ApplicationSystem();
        as.setName(TestUtil.createI18nText("Haku fi", "Haku sv", "Haku en"));
        Date asStart = new Date();
        Date asEnd = new Date();
        DateRange dr = new DateRange();
        dr.setStartDate(asStart);
        dr.setEndDate(asEnd);
        as.setApplicationDates(Arrays.asList(dr));
        ao = new ApplicationOption();
        ao.setSpecificApplicationDates(false);
        ao.setApplicationSystem(as);
        ao.setKaksoistutkinto(false);
        parentLoi.setApplicationOptions(Arrays.asList(ao));
        los.setLois(Arrays.asList(parentLoi));

        List<Code> topics = new ArrayList<Code>();
        Code topic1 = new Code();
        topic1.setName(TestUtil.createI18nText("topiikii 1 fi", "topiikki 1 sv", "topiikki 1 en"));
        topic1.setValue("top1");
        topic1.setUri("top1_uri");
        topics.add(topic1);

        Code topic2 = new Code();
        topic2.setName(TestUtil.createI18nText("topiikii 2 fi", "topiikki 2 sv", "topiikki 2 en"));
        topic2.setValue("top2");
        topic2.setUri("top2_uri");
        topics.add(topic2);
        los.setTopics(topics);

        List<Code> themes = new ArrayList<Code>();
        Code theme1 = new Code();
        theme1.setName(TestUtil.createI18nText("theme 1 fi", "theme 1 sv", "theme 1 en"));
        theme1.setValue("theme1");
        theme1.setUri("theme1_uri");
        themes.add(theme1);

        Code theme2 = new Code();
        theme2.setName(TestUtil.createI18nText("theme 2 fi", "theme 2 sv", "theme 2 en"));
        theme2.setValue("theme2");
        theme2.setUri("theme2_uri");
        themes.add(theme2);
        los.setThemes(themes);

        KoulutusLOS koulutus = new KoulutusLOS();
        koulutus.setName(TestUtil.createI18nText("child los name fi", "child los name sv", "child los name en"));
        koulutus.setShortTitle(TestUtil.createI18nText("child los short name fi", "child los short name sv", "child los short name en"));
        koulutus.setQualifications(Arrays.asList(TestUtil.createI18nText("quali fi", "quali sv", "quali en")));
        koulutus.setGoals(TestUtil.createI18nText("Goals child fi", "Goals child sv", "Goals child en"));

        koulutus.setStartDate(new Date());

        prerequisite = new Code();
        prerequisite.setName(TestUtil.createI18nText("Peruskoulu", "Peruskoulu sv", "Peruskoulu en"));
        prerequisite.setValue("pk");
        prerequisite.setUri("pk_uri");
        koulutus.setPrerequisites(Arrays.asList(prerequisite));
        koulutus.setTeachingLanguages(Arrays.asList(lang));
        koulutus.setProfessionalTitles(Arrays.asList(TestUtil.createI18nText("profession fi", "profession sv", "profession en")));
        koulutus.setContent(TestUtil.createI18nText("Content fi", "Content sv", "Content en"));
        koulutus.setApplicationOptions(Arrays.asList(ao));
        koulutus.setDegreeTitle(TestUtil.createI18nText("tutkintonimike1"));
        tutkintonimikkeet = new ArrayList<I18nText>();
        tutkintonimikkeet.add(TestUtil.createI18nText("tutkintonimike1"));
        tutkintonimikkeet.add(TestUtil.createI18nText("tutkintonimike2"));
        koulutus.setDegreeTitles(tutkintonimikkeet);
        ArrayList<KoulutusLOS> losses = new ArrayList<KoulutusLOS>();
        losses.add(koulutus);
        los.setChildEducations(losses);

        converter = new TutkintoLOSToSolrInputDocument();
    }

    @Test
    public void testConvert() {

        List<SolrInputDocument> docs = converter.convert(los);
        assertEquals(7, docs.size());
        SolrInputDocument doc = docs.get(0);
        assertEquals(los.getId() + "#" + prerequisite.getValue(), doc.get(LearningOpportunity.ID).getValue().toString());
        assertEquals(prerequisite.getValue(), doc.get(LearningOpportunity.PREREQUISITES).getValues().iterator().next().toString());
        assertEquals("80 ov fi", doc.get(LearningOpportunity.CREDITS).getValue().toString());
        assertEquals(provider.getName().getTranslations().get("fi"), doc.get(LearningOpportunity.LOP_NAME).getValue().toString());
        assertEquals(tutkintonimikkeet.size() + 1, doc.get(LearningOpportunity.DEGREE_TITLE_FI).getValues().size());
        Collection<Object> nimikkeet = doc.get(LearningOpportunity.DEGREE_TITLE_FI).getValues();
        assertTrue(nimikkeet.contains(tutkintonimikkeet.get(0).get("fi")));
        // assertEquals(SolrConstants.ED_TYPE_AMMATILLINEN, doc.get(LearningOpportunity.EDUCATION_TYPE).getValue().toString());

    }

    @Test
    public void testConvertKaksoistutkinto() {
        los.getChildEducations().get(0).getApplicationOptions().get(0).setKaksoistutkinto(true);
        List<SolrInputDocument> docs = converter.convert(los);
        assertEquals(7, docs.size());
        SolrInputDocument doc = docs.get(0);
        assertEquals(los.getId() + "#" + prerequisite.getValue(), doc.get(LearningOpportunity.ID).getValue().toString());
        assertEquals(prerequisite.getValue(), doc.get(LearningOpportunity.PREREQUISITES).getValues().iterator().next().toString());
        assertEquals("80 ov fi", doc.get(LearningOpportunity.CREDITS).getValue().toString());
        assertEquals(provider.getName().getTranslations().get("fi"), doc.get(LearningOpportunity.LOP_NAME).getValue().toString());
        assertTrue(doc.get(LearningOpportunity.EDUCATION_TYPE).getValue().toString().contains(SolrUtil.SolrConstants.ED_TYPE_KAKSOIS));
    }

    @Test
    public void testMultiplePrerequisites() {
        KoulutusLOS koulutus = new KoulutusLOS();
        koulutus.setStartDate(new Date());

        Code prerequisite1 = new Code();
        prerequisite1.setName(TestUtil.createI18nText("Ylioppilas fi", "Ylioppilas sv", "Ylioppilas en"));
        prerequisite1.setValue("yo");
        prerequisite1.setUri("yo_uri");
        koulutus.setPrerequisites(Arrays.asList(prerequisite1));
        koulutus.setTeachingLanguages(Arrays.asList(lang));
        koulutus.setProfessionalTitles(Arrays.asList(TestUtil.createI18nText("profession1 fi", "profession1 sv", "profession1 en")));
        koulutus.setContent(TestUtil.createI18nText("Content1 fi", "Content1 sv", "Content1 en"));
        koulutus.setApplicationOptions(Arrays.asList(ao));
        los.getChildEducations().add(koulutus);
        List<SolrInputDocument> docs = converter.convert(los);

        assertEquals(10, docs.size());

        SolrInputDocument doc1 = docs.get(0);
        SolrInputDocument doc2 = docs.get(1);

        assertTrue(doc1.get(LearningOpportunity.ID).getValue().toString() != doc2.get(LearningOpportunity.ID).getValue().toString());
        assertTrue(validatePrerequisites(doc1, doc2, prerequisite1));
    }

    private boolean validatePrerequisites(SolrInputDocument doc1, SolrInputDocument doc2, Code prerequisite1) {

        String id1 = los.getId() + "#" + prerequisite1.getValue();
        String id2 = los.getId() + "#" + prerequisite.getValue();

        if (id1.equals(doc1.get(LearningOpportunity.ID).getValue().toString())) {
            return id2.equals(doc2.get(LearningOpportunity.ID).getValue().toString());
        } else {
            return id2.equals(doc1.get(LearningOpportunity.ID).getValue().toString())
                    && id1.equals(doc2.get(LearningOpportunity.ID).getValue().toString());
        }
    }

}
