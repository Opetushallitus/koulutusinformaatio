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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.exception.HTTPException;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionResourceImplTest {

    private ApplicationOptionResourceImpl applicationOptionResource;
    private LearningOpportunityService learningOpportunityService;
    private final String asId = "1.2.3.4";
    private final String lopId = "5.6.7.8";
    private final String baseEducation = "1";
    private final String aoId = "1.1.2";
    private final List<String> aoIds = Lists.newArrayList(aoId);
    private final String invalidAoId = "INVALID";
    private final ResourceNotFoundException notFoundException = new ResourceNotFoundException("Not found");
    private final InvalidParametersException invalidParametersException =
            new InvalidParametersException("Invalid parameters");


    @Before
    public void setUp() throws Exception {
        learningOpportunityService = mock(LearningOpportunityService.class);
        List<ApplicationOptionSearchResultDTO> aos = new ArrayList<ApplicationOptionSearchResultDTO>();

        ApplicationOptionSearchResultDTO ao = new ApplicationOptionSearchResultDTO();
        ao.setId(aoId);
        ao.setName("ao 1 fi");
        ao.setEducationDegree("degree 1");
        List<String> cloNames = new ArrayList<String>();
        cloNames.add("clo name 1 fi");
        cloNames.add("clo name 2 fi");
        ao.setChildLONames(cloNames);

        ApplicationOptionSearchResultDTO ao2 = new ApplicationOptionSearchResultDTO();
        ao2.setId("2.3.2");
        ao2.setName("ao 2 fi");
        ao2.setEducationDegree("degree 2");

        ApplicationOptionDTO aoDTO = new ApplicationOptionDTO();
        aoDTO.setId(aoId);
        aoDTO.setName("ao 1 fi");
        aoDTO.setEducationDegree("degree1");

        List<ApplicationOptionDTO> aoDTOs = Lists.newArrayList(aoDTO);

        aos.add(ao);
        aos.add(ao2);


        when(learningOpportunityService.searchApplicationOptions(eq(asId), eq(lopId), eq(baseEducation), eq(true), eq(true), eq(false), eq("fi"))).thenReturn(aos);
        when(learningOpportunityService.getApplicationOption(eq(aoId), eq("fi"), eq("fi"))).thenReturn(aoDTO);
        when(learningOpportunityService.getApplicationOption(eq(invalidAoId), eq("fi"), eq("fi"))).thenThrow(notFoundException);
        when(learningOpportunityService.getApplicationOptions(argThat(new IsValidAoIdList()), eq("fi"), eq("fi"))).thenReturn(aoDTOs);
        when(learningOpportunityService.getApplicationOptions(argThat(new IsEmptyList()), eq("fi"), eq("fi"))).thenThrow(invalidParametersException);
        applicationOptionResource = new ApplicationOptionResourceImpl(learningOpportunityService);
    }

    @Test
    public void testSearchApplicationOptions() {
        List<ApplicationOptionSearchResultDTO> result = applicationOptionResource.searchApplicationOptions(asId, lopId, baseEducation,
                true, true, false, "fi");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1.1.2", result.get(0).getId());
        assertEquals("2.3.2", result.get(1).getId());
    }

    @Test
    public void testGetApplicationOption() {
        ApplicationOptionDTO result = applicationOptionResource.getApplicationOption(aoId, "fi", "fi");
        assertNotNull(result);
        assertEquals(aoId, result.getId());
    }

    @Test(expected = HTTPException.class)
    public void testGetApplicationOptionNotFound() {
        ApplicationOptionDTO result = applicationOptionResource.getApplicationOption(invalidAoId, "fi", "fi");
    }

    @Test
    public void testGetApplicationOptions() {
        List<ApplicationOptionDTO> result = applicationOptionResource.getApplicationOptions(aoIds, "fi", "fi");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(aoId, result.iterator().next().getId());
    }

    @Test(expected = HTTPException.class)
    public void testGetApplicationsInvalidParameters() {
        List<String> params = Lists.newArrayList();
        List<ApplicationOptionDTO> result = applicationOptionResource.getApplicationOptions(params, "fi", "fi");
    }

    class IsValidAoIdList extends ArgumentMatcher<List> {
        @Override
        public boolean matches(Object list) {

            return list != null && !((List)list).isEmpty() && ((List) list).get(0).equals(aoId);
        }
    }

    class IsEmptyList extends ArgumentMatcher<List> {
        @Override
        public boolean matches(Object list) {
            return ((List) list).size() == 0;
        }
    }

}