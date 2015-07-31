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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
* @author Mikko Majapuro
*/
public class LearningOpportunityServiceImplTest {

    private LearningOpportunityService learningOpportunityService;
    private EducationDataQueryService educationDataQueryService;
    private ChildLOS childLOS;
    private ChildLOI childLOI;
    private ApplicationOption applicationOption;
    private PreviewService previewService;

    @Before
    public void setUp() throws ResourceNotFoundException, InvalidParametersException {
        educationDataQueryService = mock(EducationDataQueryService.class);

        Code prerequisite = new Code("PK", createI18Text("Peruskoulu"), createI18Text("Peruskoulukoodin kuvaus"));

        List<ChildLOIRef> childLOIRefs = new ArrayList<ChildLOIRef>();
        childLOIRefs.add(createChildLOIRef(createI18Text("c1"), "c1 fi", "as123", "lo123", prerequisite));
        childLOIRefs.add(createChildLOIRef(createI18Text("c2"), "c2 fi", "as123", "lo124", prerequisite));
        childLOIRefs.add(createChildLOIRef(createI18Text("c3"), "c3 fi", "as124", "lo125", prerequisite));

        Set<String> asIds = new HashSet<String>();
        asIds.add("as123");
        asIds.add("as124");
        Provider parentProvider = createProvider("p1234", createI18Text("provider1"), asIds);
        applicationOption = createApplicationOption("ao123", createI18Text("ao name"), "as123",
                parentProvider, new Date(), 100, 25, 6, 77, childLOIRefs, "32",
                prerequisite);
        List<ApplicationOption> aos = Lists.newArrayList(applicationOption);

        childLOS = new ChildLOS();
        childLOS.setId("lo123");
        childLOS.setName(createI18Text("child 1"));
        childLOS.setQualification(createI18Text("Qualification"));
        ParentLOSRef parent = new ParentLOSRef();
        parent.setId("1234");
        parent.setName(createI18Text("parent name"));
        childLOS.setParent(parent);

        Code c = new Code();
        c.setValue("fi");
        c.setDescription(createI18Text("suomi"));

        Map<String, String> links = new HashMap<String, String>();
        links.put("link1", "link1");
        links.put("link2", "link2");

        childLOI = new ChildLOI();
        childLOI.setStartDate(new Date());
        childLOI.setApplicationOptions(Lists.newArrayList(aos));
        childLOI.setFormOfEducation(Lists.newArrayList(createI18Text("FormOfEducation"), createI18Text("FormOfEducation2")));
        childLOI.setFormOfTeaching(Lists.newArrayList(createI18Text("FormOfTeaching"), createI18Text("FormOfTeaching2")));
        childLOI.setPrerequisite(new Code("PK", createI18Text("Prerequisite"), null));
        childLOI.setTeachingLanguages(Lists.newArrayList(c));
        childLOI.setWebLinks(links);
        childLOI.setRelated(childLOIRefs);
        childLOI.setPrerequisite(prerequisite);
        childLOS.setLois(Lists.newArrayList(childLOI));

        ModelMapper modelMapper = new ModelMapper();

        when(educationDataQueryService.getChildLearningOpportunity(eq("clo123"))).thenReturn(childLOS);
        when(educationDataQueryService.getApplicationOption(eq("ao123"))).thenReturn(applicationOption);
        
        previewService = mock(PreviewService.class);
        
        HigherEducationLOS heLOS = new HigherEducationLOS();
        heLOS.setId("1.3.2.4He");
        heLOS.setCreditUnit(this.createI18Text("opintoviikkoa"));
        heLOS.setAccessToFurtherStudies(createI18Text("AccessToFurtherStudies"));
        heLOS.setEducationDegree("32");
        heLOS.setName(createI18Text("name"));
        heLOS.setGoals(createI18Text("goals"));
        heLOS.setStructure(createI18Text("StructureDiagram"));
        heLOS.setEducationDomain(createI18Text("EducationDomain"));
        heLOS.setStartDate(new Date());
        heLOS.setApplicationOptions(Lists.newArrayList(aos));
        heLOS.setFormOfTeaching(Lists.newArrayList(createI18Text("FormOfTeaching"), createI18Text("FormOfTeaching2")));
        heLOS.setTeachingLanguages(Lists.newArrayList(c));
        
        Provider mainProvider = new Provider();
        mainProvider.setId("mainProvider");
        heLOS.setProvider(mainProvider);
        
        Provider additionalProvider = new Provider();
        additionalProvider.setId("additionalProvider");
        
        heLOS.setAdditionalProviders(Arrays.asList(additionalProvider));
        
        when(educationDataQueryService.getHigherEducationLearningOpportunity(heLOS.getId())).thenReturn(heLOS);
        when(previewService.previewHigherEducationLearningOpportunity(heLOS.getId())).thenReturn(heLOS);
        when(educationDataQueryService.getApplicationOptions(anyList())).thenReturn(aos);
        when(educationDataQueryService.findApplicationOptions("as123", "", "", true, true)).thenReturn(aos);
        
        List<LOS> losses = new ArrayList<LOS>();

        UpperSecondaryLOS upperLOS = new UpperSecondaryLOS();
        upperLOS.setId("2234");
        upperLOS.setAccessToFurtherStudies(createI18Text("AccessToFurtherStudies"));
        upperLOS.setEducationDegree("32");
        upperLOS.setName(createI18Text("name"));
        upperLOS.setGoals(createI18Text("goals"));
        upperLOS.setStructure(createI18Text("StructureDiagram"));
        losses.add(upperLOS);

        losses.add(childLOS);
        
        SpecialLOS specialLOS = new SpecialLOS();
        specialLOS.setId("3234");
        specialLOS.setAccessToFurtherStudies(createI18Text("AccessToFurtherStudies"));
        specialLOS.setEducationDegree("32");
        specialLOS.setName(createI18Text("name"));
        specialLOS.setGoals(createI18Text("goals"));
        specialLOS.setStructure(createI18Text("StructureDiagram"));
        losses.add(specialLOS);
        
        when(educationDataQueryService.findLearningOpportunitiesByProviderId("provId")).thenReturn(losses);

        
        Provider provider = new Provider();
        provider.setId("prov111");
        provider.setAccessibility(createI18Text("accessibility"));
        provider.setDescription(createI18Text("description"));
        provider.setEmail(createI18Text("has@email.fi"));
        provider.setHomePlace(createI18Text("Vantaa"));
        provider.setName(createI18Text("prov111"));
        
        when(educationDataQueryService.getProvider("prov111")).thenReturn(provider);
        
        learningOpportunityService = new LearningOpportunityServiceImpl(educationDataQueryService, previewService, modelMapper);
    }
    
    /**
     * Tests the fetching of provider from learning opportunity service.
     * @throws ResourceNotFoundException
     */
    @Test
    public void testGetProvider() throws ResourceNotFoundException {
        LearningOpportunityProviderDTO prov = learningOpportunityService.getProvider("prov111", "fi");
        assertEquals("prov111", prov.getId());
    }

    @Test
    public void testGetBasketItems() throws InvalidParametersException {
        List<BasketItemDTO> results = learningOpportunityService.getBasketItems(new ArrayList<String>(), "fi");
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getApplicationOptions().size(), 1);
        assertEquals(results.get(0).getApplicationOptions().get(0).getId(), "ao123");
    }
    
    @Test
    public void testSearchApplicationOptions() {
        List<ApplicationOptionSearchResultDTO> results = learningOpportunityService.searchApplicationOptions("as123", "", "", true, true, false, "fi");
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getId(), "ao123");
    }
    
    @Test
    public void testFindLearningOpportunitiesByProviderId() {
        List<LearningOpportunitySearchResultDTO> results = learningOpportunityService.findLearningOpportunitiesByProviderId("provId", "fi");
        assertEquals(results.size(), 3);
        assertTrue(results.get(0).getId().contains("23"));
    }

    @Test
    public void testGetChildLearningOpportunity() throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationDTO result = learningOpportunityService.getChildLearningOpportunity("clo123");
        checkResult("fi", "fi", result);
    }

    @Test
    public void testGetChildLearningOpportunityEn() throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationDTO result = learningOpportunityService.getChildLearningOpportunity("clo123", "en", "en");
        checkResult("en", "fi", result);
    }

    @Test
    public void testGetApplicationOption() throws ResourceNotFoundException {
        ApplicationOptionDTO result = learningOpportunityService.getApplicationOption(applicationOption.getId(), "fi", "fi");
        checkResult("fi", "fi", result);
    }

    @Test
    public void testGetApplicationOptionEn() throws ResourceNotFoundException {
        ApplicationOptionDTO result = learningOpportunityService.getApplicationOption(applicationOption.getId(), "en", "en");
        checkResult("en", "fi", result);
    }
    
    @Test
    public void testetHigherEducationLearningOpportunity() throws ResourceNotFoundException {
    	HigherEducationLOSDTO losDto = learningOpportunityService.getHigherEducationLearningOpportunity("1.3.2.4He");
    	assertEquals("1.3.2.4He", losDto.getId());
    	assertEquals("opintoviikkoa fi", losDto.getCreditUnit());
    }
    
    /**
     * Testing learnin opportunity with multiple providers
     * @throws ResourceNotFoundException
     */
    @Test
    public void testMultipleProviderLearningOpportunity() throws ResourceNotFoundException {
        HigherEducationLOSDTO losDto = learningOpportunityService.getHigherEducationLearningOpportunity("1.3.2.4He");
        assertEquals("1.3.2.4He", losDto.getId());
        assertEquals("mainProvider", losDto.getProvider().getId());
        assertEquals(1, losDto.getAdditionalProviders().size());
        assertEquals("additionalProvider", losDto.getAdditionalProviders().get(0).getId());
    }
    
    
    
    @Test
    public void testetHigherEducationLearningOpportunitySv() throws ResourceNotFoundException {
    	HigherEducationLOSDTO losDto = learningOpportunityService.getHigherEducationLearningOpportunity("1.3.2.4He", "sv", "sv");
    	assertEquals("1.3.2.4He", losDto.getId());
    	assertEquals("opintoviikkoa sv", losDto.getCreditUnit());
    }
    
    @Test
    public void testetHigherEducationLearningOpportunityEn() throws ResourceNotFoundException {
    	HigherEducationLOSDTO losDto = learningOpportunityService.getHigherEducationLearningOpportunity("1.3.2.4He", "en", "en");
    	assertEquals("1.3.2.4He", losDto.getId());
    	assertEquals("opintoviikkoa en", losDto.getCreditUnit());
    }
    
    
    
    @Test
    public void testPreviewLearningOpportunity() throws ResourceNotFoundException {
    	HigherEducationLOSDTO losDto = learningOpportunityService.previewHigherEdLearningOpportunity("1.3.2.4He", "fi", "fi");
    	assertEquals("1.3.2.4He", losDto.getId());
    	assertEquals("opintoviikkoa fi", losDto.getCreditUnit());
    	
    }
    


    private void checkResult(String lang, String defaultLang, ApplicationOptionDTO result) {
        assertNotNull(result);
        assertEquals(applicationOption.getId(), result.getId());
        assertEquals(applicationOption.getAoIdentifier(), result.getAoIdentifier());
        assertEquals(applicationOption.getName().getTranslations().get(lang), result.getName());
        assertEquals(applicationOption.getEducationDegree(), result.getEducationDegree());
        assertEquals(applicationOption.getProvider().getId(), result.getProvider().getId());
        assertEquals(applicationOption.getAttachmentDeliveryDeadline(), result.getAttachmentDeliveryDeadline());
        assertEquals(applicationOption.getPrerequisite().getValue(), result.getPrerequisite().getValue());
        assertEquals(applicationOption.getLastYearApplicantCount(), result.getLastYearApplicantCount());
        assertEquals(applicationOption.getLowestAcceptedAverage(), result.getLowestAcceptedAverage());
        assertEquals(applicationOption.getLowestAcceptedScore(), result.getLowestAcceptedScore());
        assertEquals(applicationOption.getStartingQuota(), result.getStartingQuota());
        assertEquals(applicationOption.getChildLOIRefs().size(), result.getChildRefs().size());
    }

    private void checkResult(String lang, String defaultLang, ChildLearningOpportunitySpecificationDTO result) {
        assertNotNull(result);
        assertEquals(childLOS.getId(), result.getId());
        assertEquals(childLOS.getName().getTranslations().get(defaultLang), result.getName());

        assertNotNull(result.getLois());
        assertEquals(1, result.getLois().size());

        assertEquals(childLOS.getQualification().getTranslations().get(lang), result.getQualification());
        assertEquals(childLOS.getParent().getName().getTranslations().get(defaultLang), result.getParent().getName());
        assertEquals(childLOS.getParent().getId(), result.getParent().getId());

        assertNotNull(result.getLois());
        assertEquals(1, result.getLois().size());
        ChildLearningOpportunityInstanceDTO loi = result.getLois().get(0);
        assertEquals(childLOI.getApplicationOptions().get(0).getId(),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().iterator().next().getId());
        assertEquals(childLOI.getApplicationOptions().get(0).getName().getTranslations().get(lang),
                loi.getApplicationSystems().iterator().next().getApplicationOptions().iterator().next().getName());
        assertEquals(childLOI.getPrerequisite().getValue(),
                loi.getPrerequisite().getValue());
        assertEquals(childLOI.getPrerequisite().getDescription().getTranslations().get(lang),
                loi.getPrerequisite().getDescription());

        assertEquals(childLOI.getStartDate(), loi.getStartDate());
        assertEquals(childLOI.getFormOfEducation().get(0).getTranslations().get(lang), loi.getFormOfEducation().get(0));
        assertEquals(childLOI.getFormOfTeaching().get(0).getTranslations().get(lang), loi.getFormOfTeaching().get(0));
        assertEquals(1, loi.getTeachingLanguages().size());
        assertEquals(2, loi.getWebLinks().size());
    }

    private I18nText createI18Text(String text) {
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fi", text + " fi");
        translations.put("sv", text + " sv");
        translations.put("en", text + " en");
        return new I18nText(translations);
    }

    private ChildLOIRef createChildLOIRef(I18nText name, String nameByTeachingLang, String asId, String losId, Code prerequisite) {
        ChildLOIRef ref = new ChildLOIRef();
        ref.setName(name);
        ref.setNameByTeachingLang(nameByTeachingLang);
        ref.setAsIds(Lists.newArrayList(asId));
        ref.setLosId(losId);
        ref.setPrerequisite(prerequisite);
        return ref;
    }

    private Provider createProvider(String id, I18nText name, Set<String> asIds) {
        Provider provider = new Provider();
        provider.setId(id);
        provider.setName(name);
        provider.setApplicationSystemIds(asIds);
        return provider;
    }

    private ApplicationOption createApplicationOption(String id, I18nText name, String asId, Provider provider, Date attDeadline,
                                                      int lastYearApplicantCount, double lowestAcceptedAverage,
                                                      int lowestAcceptedScore, int startingQuota, List<ChildLOIRef> childLOIRefs,
                                                      String educationDegree, Code prerequisite) {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(id);
        ao.setName(name);
        ApplicationSystem as = new ApplicationSystem();
        as.setId(asId);
        as.setName(createI18Text("Haun nimi"));
        ao.setApplicationSystem(as);
        ao.setProvider(provider);
        ao.setAttachmentDeliveryDeadline(attDeadline);
        ao.setLastYearApplicantCount(lastYearApplicantCount);
        ao.setLowestAcceptedAverage(lowestAcceptedAverage);
        ao.setLowestAcceptedScore(lowestAcceptedScore);
        ao.setStartingQuota(startingQuota);
        ao.setChildLOIRefs(childLOIRefs);
        ao.setEducationDegree(educationDegree);
        ao.setPrerequisite(prerequisite);
        Exam exam = new Exam();
        exam.setDescription(createI18Text("exam description"));
        exam.setType(createI18Text("exam type"));
        ExamEvent event = new ExamEvent();
        event.setDescription("event description");
        event.setStart(new Date());
        event.setEnd(new Date());
        Address address = new Address();
        address.setPostalCode(createI18nText("00100"));
        address.setPostOffice(createI18nText("Helsinki"));
        address.setStreetAddress(createI18nText("street address"));
        event.setAddress(address);
        exam.setExamEvents(Lists.newArrayList(event));
        ao.setExams(Lists.newArrayList(exam));
        return ao;
    }
    
    private I18nText createI18nText(String fi) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        return new I18nText(values);
    }
}
