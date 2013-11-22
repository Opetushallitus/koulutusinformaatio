package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.LOSearchResultList;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LOSearchResultListDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.HTTPException;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

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

        LOSearchResult result1 = new LOSearchResult("1.2.3", "term hakutulos", "2.3.4", "oppilaitos nimi", "peruskoulu", "PK", "3.4.5", "4.5.6", "TUTKINTO");
        LOSearchResultList resultList = new LOSearchResultList();
        resultList.setResults(Lists.newArrayList(result1));
        resultList.setTotalCount(1);
        when(searchService.searchLearningOpportunities(eq("term"), eq("PK"), eq(Lists.newArrayList("Helsinki")), eq(new ArrayList<String>()), eq("fi"), eq(false), eq(false), eq(0), eq(30))).thenReturn(resultList);
        when(searchService.searchLearningOpportunities(eq(INVALID_TERM), anyString(), anyList(), anyList(), anyString(),  anyBoolean(), anyBoolean(), anyInt(), anyInt())).thenThrow(SearchException.class);

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

        resource = new LearningOpportunityResourceImpl(searchService, modelMapper, learningOpportunityService);

    }

    @Test
    public void testSearchLearningOpportunities() throws SearchException {
        LOSearchResultListDTO result = resource.searchLearningOpportunities("term", "PK", Lists.newArrayList("Helsinki"), new ArrayList<String>(), "fi", false, false, 0, 30);
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals("1.2.3", result.getResults().get(0).getId());
        assertEquals("term hakutulos", result.getResults().get(0).getName());
        assertEquals("2.3.4", result.getResults().get(0).getLopId());
        assertEquals("oppilaitos nimi", result.getResults().get(0).getLopName());
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
        resource.searchLearningOpportunities(INVALID_TERM, "", new ArrayList<String>(), new ArrayList<String>(), "", false, false, 0, 0);
    }

}
