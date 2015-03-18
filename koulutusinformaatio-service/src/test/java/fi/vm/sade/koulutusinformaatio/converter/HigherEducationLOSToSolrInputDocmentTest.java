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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * @author Markus
 *
 */
public class HigherEducationLOSToSolrInputDocmentTest {
	

	@Test
	public void testConvert() {
		
	    HigherEducationLOS los = createLos("losId", "koulutus_someRandom");
		
		HigherEducationLOSToSolrInputDocment converter = new HigherEducationLOSToSolrInputDocment(); 
		List<SolrInputDocument> docs = converter.convert(los);
		assertEquals(7, docs.size());
		SolrInputDocument doc = docs.get(0);
		assertEquals(los.getId(), doc.get(LearningOpportunity.ID).getValue().toString());
		//assertEquals(prerequisite.getValue(), doc.get(LearningOpportunity.PREREQUISITES).getValues().iterator().next().toString());
		assertEquals("80 ov fi", doc.get(LearningOpportunity.CREDITS).getValue().toString());
        assertEquals(los.getProvider().getName().getTranslations().get("fi"), doc.get(LearningOpportunity.LOP_NAME).getValue().toString());
        //assertEquals(SolrConstants.ED_TYPE_AMKS, doc.get(LearningOpportunity.EDUCATION_TYPE).getValue().toString());
        assertEquals(los.getEducationDegreeLang().getTranslations().get("fi"), doc.get(LearningOpportunity.EDUCATION_DEGREE_FI).getValue().toString());
        assertEquals(los.getEducationDegree(), doc.get(LearningOpportunity.EDUCATION_DEGREE_CODE).getValue().toString());
        assertEquals(los.getDegreeTitle().getTranslations().get("fi"), doc.get(LearningOpportunity.DEGREE_TITLE_FI).getValue().toString());
		
	}
	
    @Test
    public void testConvertAmmOpettaja() {
        HigherEducationLOS los = createLos("ammOpettajaLosId", SolrConstants.ED_CODE_AMM_OPETTAJA);
        HigherEducationLOSToSolrInputDocment converter = new HigherEducationLOSToSolrInputDocment(); 
        List<SolrInputDocument> docs = converter.convert(los);
        assertEquals(7, docs.size());
        SolrInputDocument doc = docs.get(0);
        assertEquals(los.getId(), doc.get(LearningOpportunity.ID).getValue().toString());
        assertEquals("educationType_ffm=[et02, et02.11]", doc.getField(LearningOpportunity.EDUCATION_TYPE).toString());
    }
	
	private HigherEducationLOS createLos(String id, String edCodeUri) {
	    HigherEducationLOS los = new HigherEducationLOS();
        los.setType("KORKEAKOULU");
        los.setId(id);
        
        Code edCode = new Code();
        edCode.setName(TestUtil.createI18nText(edCodeUri, edCodeUri, edCodeUri));
        edCode.setValue(edCodeUri);
        edCode.setUri(edCodeUri);
        los.setEducationCode(edCode);

        Code lang = new Code();
        lang.setName(TestUtil.createI18nText("Suomi", "Finska", "Finnish"));
        lang.setValue("FI");
        lang.setUri("fi_uri");
        los.setTeachingLanguages(Arrays.asList(lang));
        
        Code prerequisite = new Code();
        prerequisite.setName(TestUtil.createI18nText("Peruskoulu", "Peruskoulu sv", "Peruskoulu en"));
        prerequisite.setValue("pk");
        prerequisite.setUri("pk_uri");
        los.setPrerequisites(Arrays.asList(prerequisite));
        
        Provider provider = new Provider();
        provider.setId("provId");
        provider.setName(TestUtil.createI18nText("prov fi", "prov sv", "prov en"));
        provider.setHomeDistrict(TestUtil.createI18nText("Uusimaa fi", "Uusimaa sv", "Uusimaa en"));
        provider.setHomePlace(TestUtil.createI18nText("Hki fi", "Hki sv", "Hki en"));
        Address addr = new Address();
        addr.setPostalCode("04620");
        provider.setVisitingAddress(addr);
        provider.setDescription(TestUtil.createI18nText("prov descr fi", "prov descr sv", "prov descr en"));
        los.setProvider(provider);
        
        los.setCreditValue("80");
        los.setCreditUnit(TestUtil.createI18nText("ov fi", "ov sv", "ov en"));
        los.setName(TestUtil.createI18nText("los name fi", "los name sv", "los name en"));
        los.setShortTitle(TestUtil.createI18nText("los short name fi", "los short name sv", "los short name en"));
        los.setEducationDegreeLang(TestUtil.createI18nText("Alempi korkeakoulu fi", "Alempi korkeakoulu sv", "Alempi korkeakoulu en"));
        los.setEducationDegree("alempiKk_uri");
        List<I18nText> quals = new ArrayList<I18nText>();
        quals.add(TestUtil.createI18nText("quali fi", "quali sv", "quali en"));
        los.setQualifications(quals);
        los.setGoals(TestUtil.createI18nText("Goals fi", "Goals sv", "Goals en"));
        los.setContent(TestUtil.createI18nText("Content fi", "Content sv", "Content en"));
        los.setDegreeTitle(TestUtil.createI18nText("tutkintonimike1"));
        
        ApplicationSystem as = new ApplicationSystem();
        as.setName(TestUtil.createI18nText("Haku fi", "Haku sv", "Haku en"));
        Date asStart = new Date();
        Date asEnd = new Date();
        DateRange dr = new DateRange();
        dr.setStartDate(asStart);
        dr.setEndDate(asEnd);
        as.setApplicationDates(Arrays.asList(dr));
        ApplicationOption ao = new ApplicationOption();
        ao.setSpecificApplicationDates(false);
        ao.setApplicationSystem(as);
        los.setApplicationOptions(Arrays.asList(ao));
        
        Date edStart = new Date();
        los.setStartDate(edStart);
        
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
        return los;
	}
	
}
