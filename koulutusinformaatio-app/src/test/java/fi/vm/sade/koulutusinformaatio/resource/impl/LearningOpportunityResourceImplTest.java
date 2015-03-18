package fi.vm.sade.koulutusinformaatio.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.SuggestedTermsResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SearchType;
import fi.vm.sade.koulutusinformaatio.domain.dto.SpecialLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.SuggestedTermsResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.HTTPException;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(MockitoJUnitRunner.class)
public class LearningOpportunityResourceImplTest {

    @Mock private SearchService searchService;
    @Mock private LearningOpportunityService learningOpportunityService;
    private ModelMapper modelMapper;

    private LearningOpportunityResourceImpl resource;

    private static final String INVALID_TERM = "invalidterm";

    @Before
    public void init() throws ResourceNotFoundException, SearchException {
        modelMapper = new ModelMapper();

        LOSearchResult result1 = new LOSearchResult("1.2.3", "term hakutulos", Arrays.asList("2.3.4"), Arrays.asList("oppilaitos nimi"), "peruskoulu", "PK", "3.4.5", "4.5.6", "TUTKINTO", "120 ov", "et1", "amk", "code_amk", "homeplace", "childname");
        LOSearchResultList resultList = new LOSearchResultList();
        resultList.setResults(Lists.newArrayList(result1));
        resultList.setTotalCount(1);
        when(searchService.searchLearningOpportunities(eq("term"), eq("PK"), eq(Lists.newArrayList("Helsinki")), eq(new ArrayList<String>()), eq(new ArrayList<String>()), eq(new ArrayList<String>()), eq("fi"), eq(false), eq(false), eq(false), eq(0), eq(30), eq("0"), eq("asc"), eq(""), eq(""), eq(new ArrayList<String>()), eq(SearchType.LO))).thenReturn(resultList);
        when(searchService.searchLearningOpportunities(eq(INVALID_TERM), anyString(), anyList(), anyList(), anyList(), anyList(), anyString(),  anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyList(), eq(SearchType.LO))).thenThrow(SearchException.class);


        ParentLearningOpportunitySpecificationDTO parentDTO = new ParentLearningOpportunitySpecificationDTO();
        parentDTO.setId("parentLOSId");
        parentDTO.setName("parent name");

        when(learningOpportunityService.getParentLearningOpportunity(eq("parentlosid"), eq("fi"), eq("fi"))).thenReturn(parentDTO);
        when(learningOpportunityService.getParentLearningOpportunity(eq("parentlosid"))).thenReturn(parentDTO);

        ChildLearningOpportunitySpecificationDTO childDTO = new ChildLearningOpportunitySpecificationDTO();
        childDTO.setId("childid");
        childDTO.setName("childName");

        when(learningOpportunityService.getChildLearningOpportunity(eq("childid"), eq("fi"), eq("fi"))).thenReturn(childDTO);
        when(learningOpportunityService.getChildLearningOpportunity(eq("childid"))).thenReturn(childDTO);
        
        SuggestedTermsResult suggestions = new SuggestedTermsResult();
        List<String> terms = Arrays.asList("term1", "term2");
        suggestions.setLoNames(terms);
        when(searchService.searchSuggestedTerms(anyString(), anyString())).thenReturn(suggestions);
        
        HigherEducationLOSDTO higherLos = new HigherEducationLOSDTO();
        higherLos.setId("1.2.3.34");
        LearningOpportunityProviderDTO mainProvider = new LearningOpportunityProviderDTO();
        mainProvider.setId("mainProvider");
        higherLos.setProvider(mainProvider);
        LearningOpportunityProviderDTO aditionalProvider = new LearningOpportunityProviderDTO();
        aditionalProvider.setId("additionalProvider");
        higherLos.setAdditionalProviders(Arrays.asList(aditionalProvider));
        
        
        when(learningOpportunityService.getHigherEducationLearningOpportunity(anyString())).thenReturn(higherLos);
        when(learningOpportunityService.previewHigherEdLearningOpportunity(anyString(), anyString(), anyString())).thenReturn(higherLos);
        
        
        SpecialLearningOpportunitySpecificationDTO specialLos = new SpecialLearningOpportunitySpecificationDTO();
        specialLos.setId("specialLosId");
        
        when(learningOpportunityService.getSpecialSecondaryLearningOpportunity(anyString())).thenReturn(specialLos);
        
        UpperSecondaryLearningOpportunitySpecificationDTO upperSecLos = new UpperSecondaryLearningOpportunitySpecificationDTO();
        upperSecLos.setId("upperSecLosId");
        
        when(learningOpportunityService.getUpperSecondaryLearningOpportunity(anyString())).thenReturn(upperSecLos);
        
        resource = new LearningOpportunityResourceImpl(searchService, modelMapper, learningOpportunityService);

    }

    @Test
    public void testSearchLearningOpportunities() throws SearchException {
        LOSearchResultListDTO result = resource.searchLearningOpportunities("term", "PK", Lists.newArrayList("Helsinki"), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),  "fi", false, false, false, 0, 30, "0", "asc", "", "", new ArrayList<String>(), SearchType.LO);
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals("1.2.3", result.getResults().get(0).getId());
        assertEquals("term hakutulos", result.getResults().get(0).getName());
        assertEquals("2.3.4", result.getResults().get(0).getLopIds().get(0));
        assertEquals("oppilaitos nimi", result.getResults().get(0).getLopNames().get(0));
        assertEquals("peruskoulu", result.getResults().get(0).getPrerequisite());
        assertEquals("PK", result.getResults().get(0).getPrerequisiteCode());
        assertEquals("3.4.5", result.getResults().get(0).getParentId());
        assertEquals("4.5.6", result.getResults().get(0).getLosId());
    }

    @Test
    public void testGetParentLearningOpportunity() throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationDTO dto1 = resource.getParentLearningOpportunity("parentlosid", "fi", "fi");
        assertNotNull(dto1);
        ParentLearningOpportunitySpecificationDTO dto2 = resource.getParentLearningOpportunity("parentlosid", null, null);
        assertNotNull(dto2);
    }

    @Test
    public void testGetChildLearningOpportunity() {
        ChildLearningOpportunitySpecificationDTO dto1 = resource.getChildLearningOpportunity("childid", "fi", "fi");
        assertNotNull(dto1);
        ChildLearningOpportunitySpecificationDTO dto2 = resource.getChildLearningOpportunity("childid", null, null);
        assertNotNull(dto2);
    }

    @Test(expected = HTTPException.class)
    public void testSearchException() {
        resource.searchLearningOpportunities(INVALID_TERM, "", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "", false, false, false, 0, 0, "0", "asc", "", "", new ArrayList<String>(), SearchType.LO);
    }
    
    @Test
    public void testSuggestedTermsSearch() {
        SuggestedTermsResultDTO strDTO = this.resource.getSuggestedTerms("term1", "fi");
        assertEquals(2, strDTO.getLoNames().size());
    }
    
    @Test
    public void testGetHigherEducationLearningOpportunity() {
    	HigherEducationLOSDTO dto = resource.getHigherEducationLearningOpportunity("1.2.3.34", null, null);
    	assertEquals("1.2.3.34", dto.getId());
    }
    
    /**
     * Testing learning opportunity with multiple providers
     */
    @Test
    public void testMultipleProviderLearningOpportunity() {
        HigherEducationLOSDTO dto = resource.getHigherEducationLearningOpportunity("1.2.3.34", null, null);
        assertEquals("mainProvider", dto.getProvider().getId());
        assertEquals("additionalProvider", dto.getAdditionalProviders().get(0).getId());
    }
    
    @Test
    public void testPreviewLearningOpportunity() {
    	HigherEducationLOSDTO dto = (HigherEducationLOSDTO)(resource.previewLearningOpportunity("1.2.3.34", "fi", "fi", "korkeakoulu"));
    	assertEquals("1.2.3.34", dto.getId());
    }
    
    @Test
    public void testGetSpecialLearningOpportunity() {
    	SpecialLearningOpportunitySpecificationDTO specialLos = resource.getSpecialLearningOpportunity("specialLosId", null, null);
    	assertEquals("specialLosId", specialLos.getId());
    }
    
    @Test
    public void testGetUpperSecondaryLearningOpportunity() {
    	UpperSecondaryLearningOpportunitySpecificationDTO upperSecLos = resource.getUpperSecondaryLearningOpportunity("upperSecLosId", null, null);
    	assertEquals("upperSecLosId", upperSecLos.getId());
    }

}
