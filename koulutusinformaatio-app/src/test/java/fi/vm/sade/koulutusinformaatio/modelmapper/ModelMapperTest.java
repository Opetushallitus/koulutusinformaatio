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

package fi.vm.sade.koulutusinformaatio.modelmapper;

import fi.vm.sade.koulutusinformaatio.domain.LOSearchResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class ModelMapperTest {

    ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testValidate() {
        modelMapper.validate();
    }

    @Test
    public void testMapLearningOpportunityResultToDTO() {
        LOSearchResult result = new LOSearchResult("loid", "name", "lopid", "lop name", "Peruskoulu", "PK", "parentId", "losId", "TYPE", "credits", "et1", null, null, null, null, null);

        LearningOpportunitySearchResultDTO dto = modelMapper.map(result, LearningOpportunitySearchResultDTO.class);

        assertEquals(result.getId(), dto.getId());
        assertEquals(result.getName(), dto.getName());
        assertEquals(result.getLopId(), dto.getLopId());
        assertEquals(result.getLopName(), dto.getLopName());
        assertEquals(result.getPrerequisite(), dto.getPrerequisite());
        assertEquals(result.getParentId(), dto.getParentId());
        assertEquals(result.getLosId(), dto.getLosId());

    }
}
