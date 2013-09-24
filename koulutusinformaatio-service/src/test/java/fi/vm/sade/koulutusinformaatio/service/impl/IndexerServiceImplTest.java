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
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexerServiceImplTest {

    @Mock
    private HttpSolrServer loUpdateHttpSolrServer;
    @Mock
    private HttpSolrServer lopUpdateHttpSolrServer;

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

        indexerServiceImpl = new IndexerServiceImpl(loUpdateHttpSolrServer, lopUpdateHttpSolrServer);
    }

    @Test
    public void testAddParentLOS() throws Exception {
        ParentLOS p = createParentLOS();
        indexerServiceImpl.addParentLearningOpportunity(p);
        verify(loUpdateHttpSolrServer).add(argThat(TestUtil.isListOfTwoELements()));
        verify(lopUpdateHttpSolrServer).add(argThat(TestUtil.isListOfOneELement()));
    }

    @Test
    public void testAOSpecificApplicationDates() throws Exception {
        ParentLOS p = createParentLOSWithApplicationOptionSpecificDates();
        indexerServiceImpl.addParentLearningOpportunity(p);
        verify(loUpdateHttpSolrServer).add(argThat(new ArgumentMatcher<List<SolrInputDocument>>() {
            @Override
            public boolean matches(Object list) {
                if (list == null) return false;
                if (((List) list).size() != 2) return false;
                for (SolrInputDocument doc : (List<SolrInputDocument>) list) {
                    if (doc.get("parentId") != null) {
                        Date start = (Date) doc.get("asStart_0").getValue();
                        Date end = (Date) doc.get("asEnd_0").getValue();
                        return start.equals(applicationOptionApplicationPeriodStarts) &&
                                end.equals(applicationOptionApplicationPeriodEnds);
                    }
                }
                return false;
            }
        }));
        verify(lopUpdateHttpSolrServer).add(argThat(TestUtil.isListOfOneELement()));
    }

    @Test
    public void testCommitLOChanges() throws Exception {
        indexerServiceImpl.commitLOChanges();
        verify(loUpdateHttpSolrServer).commit();
        verify(lopUpdateHttpSolrServer).commit();
    }

    private ParentLOS createParentLOS() {
        ParentLOS p = new ParentLOS();
        p.setId("parent_id");
        p.setName(TestUtil.createI18nText("Parent LOS name fi", "Parent LOS name sv", "Parent LOS name en"));
        Provider provider = new Provider();
        provider.setId("123");
        provider.setName(TestUtil.createI18nText("LOP name fi", "LOP name sv", "LOP name en"));
        Address address = new Address();
        address.setPostOffice("Helsinki");
        provider.setVisitingAddress(address);
        provider.setDescription(TestUtil.createI18nText("LOP description fi", "LOP description sv", "LOP description en"));
        provider.setHomePlace(TestUtil.createI18nText("homeplace fi", "homeplace sv", "homeplace en"));
        p.setProvider(provider);
        p.setStructure(TestUtil.createI18nText("Parent LOS structure diagram fi", "Parent LOS structure diagram sv", "Parent LOS structure diagram en"));
        p.setGoals(TestUtil.createI18nText("Parent LOS goals fi", "Parent LOS goals sv", "Parent LOS goals en"));

        ParentLOI parentLOI1 = new ParentLOI();
        ApplicationOption ao1 = new ApplicationOption();
        ao1.setId("AO1_id");
        ao1.setPrerequisite(new Code("PK",
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en"),
                TestUtil.createI18nText("Peruskoulu fi", "Peruskoulu sv", "Peruskoulu en")));
        ao1.setRequiredBaseEducations(Lists.newArrayList("1"));
        ApplicationSystem as1 = new ApplicationSystem();
        as1.setId("AS1_id");
        as1.setName(TestUtil.createI18nText("AS name fi", "AS name sv", "AS name en"));
        Calendar endCal = Calendar.getInstance();
        endCal.roll(Calendar.YEAR, 1);
        DateRange dr = new DateRange(applicationSystemStarts, applicationSystemEnds);
        as1.setApplicationDates(Lists.newArrayList(dr));
        ao1.setApplicationSystem(as1);
        parentLOI1.setApplicationOptions(Sets.newHashSet(ao1));
        p.setLois(Lists.newArrayList(parentLOI1));

        ChildLOS childLOS1 = new ChildLOS();
        childLOS1.setId("childLOS1_id");
        childLOS1.setName(TestUtil.createI18nText("child los name fi", "child los name sv", "child los name en",
                "child los name short fi", "child los name short sv", "child los name short en"));
        childLOS1.setQualification(TestUtil.createI18nText("Qualification fi", "Qualification sv", "Qualification en"));
        childLOS1.setGoals(TestUtil.createI18nText("Degree goal fi", "Degree goal sv", "Degree goal en"));

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
        childLOS1.setLois(Lists.newArrayList(childLOI1));

        p.setChildren(Lists.newArrayList(childLOS1));
        return p;
    }

    private ParentLOS createParentLOSWithApplicationOptionSpecificDates() {
        ParentLOS p = createParentLOS();
        p.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0).setSpecificApplicationDates(true);
        p.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0).
                setApplicationStartDate(applicationOptionApplicationPeriodStarts);
        p.getChildren().get(0).getLois().get(0).getApplicationOptions().get(0).
                setApplicationEndDate(applicationOptionApplicationPeriodEnds);
        return p;
    }
}
