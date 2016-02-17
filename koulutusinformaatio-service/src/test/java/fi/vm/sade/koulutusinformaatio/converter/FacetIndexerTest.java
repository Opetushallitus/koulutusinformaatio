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
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;

/**
 * 
 * @author Markus
 */
public class FacetIndexerTest {
    
    List<Code> topics;
    List<Code> themes;
    Code lang;
    Code prerequisite;
    
    private FacetIndexer indexer;
    
    @Before
    public void setup() {
        
        topics = new ArrayList<Code>();
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
        
        themes = new ArrayList<Code>();
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
        
        lang = new Code();
        lang.setName(TestUtil.createI18nText("Suomi", "Finska", "Finnish"));
        lang.setValue("FI");
        lang.setUri("fi_uri");
        
        prerequisite = new Code();
        prerequisite.setName(TestUtil.createI18nText("Peruskoulu", "Peruskoulu sv", "Peruskoulu en"));
        prerequisite.setValue("pk");
        prerequisite.setUri("pk_uri");
        
        indexer = new FacetIndexer();
        
    }
    
    @Test
    public void testCreateTutkintoLOSFacetDocs() {
        KoulutusLOS koulutus = new KoulutusLOS();
        koulutus.setTeachingLanguages(Arrays.asList(lang));
        koulutus.setPrerequisites(Arrays.asList(prerequisite));
        TutkintoLOS los = new TutkintoLOS();
        los.setChildEducations(Arrays.asList(koulutus));
        los.setTopics(topics);
        los.setThemes(themes);
        List<SolrInputDocument> docs = indexer.createFacetsDocs(los);
        assertEquals(6, docs.size());
    }
    
    @Test
    public void testCreateHigherEducationFacetDocs() {
        HigherEducationLOS los = new HigherEducationLOS();
        los.setTeachingLanguages(Arrays.asList(lang));
        los.setPrerequisites(Arrays.asList(prerequisite));
        los.setTopics(topics);
        los.setThemes(themes);
        List<SolrInputDocument> docs = indexer.createFacetDocs(los);
        assertEquals(6, docs.size());
    }
}
