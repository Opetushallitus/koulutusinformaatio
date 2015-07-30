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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexerServiceImplTest {

    @Mock
    private HttpSolrServer loUpdateHttpSolrServer;
    @Mock
    private HttpSolrServer lopUpdateHttpSolrServer;
    @Mock
    private HttpSolrServer locationUpdateHttpSolrServer;
    @Mock
    private HttpSolrServer locationHttpSolrServer;
    @Mock
    private HttpSolrServer loHttpSolrServer;
    @Mock
    private HttpSolrServer lopHttpSolrServer;
    @Mock
    private ConversionService conversionService;

    private IndexerServiceImpl indexerServiceImpl;

    private Date applicationSystemStarts;
    private Date applicationSystemEnds;
    private Date applicationOptionApplicationPeriodStarts;
    private Date applicationOptionApplicationPeriodEnds;

    @Before
    public void init() throws SolrServerException {
        applicationSystemStarts = new Date();
        Calendar endCal = Calendar.getInstance();
        endCal.roll(Calendar.YEAR, 1);
        applicationSystemEnds = endCal.getTime();
        Calendar aoStartCal = Calendar.getInstance();
        aoStartCal.roll(Calendar.MONTH, 1);
        aoStartCal.set(Calendar.DATE, 10);
        applicationOptionApplicationPeriodStarts = aoStartCal.getTime();
        Calendar aoEndCal = Calendar.getInstance();
        aoEndCal.roll(Calendar.MONTH, 1);
        aoEndCal.set(Calendar.DATE, 15);
        applicationOptionApplicationPeriodEnds = aoEndCal.getTime();
        when(lopUpdateHttpSolrServer.query(any(SolrQuery.class))).thenReturn(new QueryResponse());
        
        SolrDocumentList timestamps1 = new SolrDocumentList();
        SolrDocument timestamp1 = new SolrDocument();
        timestamp1.addField(LearningOpportunity.NAME, "01.22.2013 08:43:15");
        timestamps1.add(timestamp1);
        QueryResponse timestampResp1 = mock(QueryResponse.class);
        
        when(loHttpSolrServer.query(any(SolrQuery.class))).thenReturn(timestampResp1);
        when(timestampResp1.getResults()).thenReturn(timestamps1);
        
        SolrDocumentList timestamps2 = new SolrDocumentList();
        SolrDocument timestamp2 = new SolrDocument();
        timestamp2.addField(LearningOpportunity.NAME, "01.23.2013 08:43:15");
        QueryResponse timestampResp2 = mock(QueryResponse.class);
        timestamps2.add(timestamp2);
        when(loUpdateHttpSolrServer.query(any(SolrQuery.class))).thenReturn(timestampResp2);
        when(timestampResp2.getResults()).thenReturn(timestamps2);
        
        when(loHttpSolrServer.getBaseURL()).thenReturn("lo");
        when(loUpdateHttpSolrServer.getBaseURL()).thenReturn("loUpdate");

        List<SolrInputDocument> parentDocs = new ArrayList<SolrInputDocument>();
        SolrInputDocument parentDoc1 = new SolrInputDocument();
        parentDoc1.setField("parentId", "parentId1");
        parentDoc1.setField("asStart_0", applicationOptionApplicationPeriodStarts);
        parentDoc1.setField("asEnd_0", applicationOptionApplicationPeriodEnds);
        parentDocs.add(parentDoc1);
        SolrInputDocument parentDoc2 = new SolrInputDocument();
        parentDoc2.setField("parentId", "parentId2");
        parentDoc2.setField("asStart_0", applicationOptionApplicationPeriodStarts);
        parentDoc2.setField("asEnd_0", applicationOptionApplicationPeriodEnds);
        parentDocs.add(parentDoc2);
        
        indexerServiceImpl = new IndexerServiceImpl(conversionService, loUpdateHttpSolrServer, lopUpdateHttpSolrServer, locationUpdateHttpSolrServer, loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, loUpdateHttpSolrServer, lopUpdateHttpSolrServer, locationUpdateHttpSolrServer);
    }

    @Test
    public void testCommitLOChanges() throws Exception {
        indexerServiceImpl.commitLOChanges(loUpdateHttpSolrServer, lopUpdateHttpSolrServer, locationUpdateHttpSolrServer, true);
        verify(loUpdateHttpSolrServer).commit();
        verify(lopUpdateHttpSolrServer).commit();
    }
    
    @Test
    public void getLoCollectionToUpdateTest() {
        HttpSolrServer result = this.indexerServiceImpl.getLoCollectionToUpdate();
        assertEquals(result, loHttpSolrServer);
    }
    
    @Test
    public void getLopCollectionToUpdateTest() {
        HttpSolrServer result = this.indexerServiceImpl.getLopCollectionToUpdate(loHttpSolrServer);
        assertEquals(result, lopHttpSolrServer);
    }
    
    @Test
    public void getLocationCollectionToUpdateTest() {
        HttpSolrServer result = this.indexerServiceImpl.getLocationCollectionToUpdate(loHttpSolrServer);
        assertEquals(result, locationHttpSolrServer);
    }
    
    /**
     * Tests isDocumentInIndex method with expected false result.
     */
    @Test
    public void isDocumentInIndexTest() {
        boolean res = this.indexerServiceImpl.isDocumentInIndex("doc:someId", this.lopUpdateHttpSolrServer);
        assertEquals(false, res);
    }
    
    /**
     * Tests the creation of provider docs.
     * 
     * @throws SolrServerException
     * @throws IOException
     */
    @Test
    public void createProviderDocsTest() throws SolrServerException, IOException {
        Provider prov = new Provider();
        prov.setId("1.1.1.organisation");
        I18nText name = new I18nText();
        name.put("fi", "nimi fi");
        prov.setName(name);
        I18nText homeplace = new I18nText();
        homeplace.put("fi", "Helsinki");
        prov.setHomePlace(homeplace);
        this.indexerServiceImpl.createProviderDocs(prov, this.lopUpdateHttpSolrServer, new HashSet<String>(), new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
        verify(lopUpdateHttpSolrServer).add(argThat(TestUtil.isListOfOneELement()));
    }
    
    @Test(expected=RuntimeException.class)
    public void throwsRuntimeExceptionWhenUnknownExceptionIsCaughtWhileIndexingLearningOpportunitySpecification() throws SolrServerException, IOException {
        SpecialLOS los = new SpecialLOS();
        los.setId("123.56534.4534.32");
        los.setLois(Arrays.asList(givenChildLOI(givenApplicationOption())));
        indexerServiceImpl.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
    }

    private ApplicationOption givenApplicationOption() {
        ApplicationOption ao1 = new ApplicationOption();
        ao1.setId("AO1_id");
        ao1.setPrerequisite(new Code("PK",
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en"),
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en")));
        ao1.setRequiredBaseEducations(Lists.newArrayList("1"));
        return ao1;
    }

    private ChildLOI givenChildLOI(ApplicationOption ao1) {
        ChildLOI childLOI1 = new ChildLOI();
        childLOI1.setId("childLOI1_id");
        childLOI1.setPrerequisite(new Code("PK",
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en"),
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en")));

        childLOI1.setTeachingLanguages(Lists.newArrayList(new Code("fi",
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en"),
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en"))));

        childLOI1.setProfessionalTitles(Lists.newArrayList(TestUtil.createI18nText("Professional title fi", "Professional title sv", "Professional title en")));
        childLOI1.setContent(TestUtil.createI18nText("Content fi", "Content sv", "Content en"));
        childLOI1.setApplicationOptions(Lists.newArrayList(ao1));
        return childLOI1;
    }


}
