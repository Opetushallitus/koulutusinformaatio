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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunitySearchResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
// ignore until language issues have been sorted out
@Ignore
public class ModelMapperTest {

    ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testValidate() {
        modelMapper.validate();
    }

    @Test
    public void testMapLearningOpportunityResultToDTO() {
        Map<String, String> nameMap = Maps.newHashMap();
        nameMap.put("fi", "lo name fi");
        nameMap.put("sv", "lo name sv");
        nameMap.put("en", "lo name en");
        I18nText name = new I18nText(nameMap);

        Map<String, String> lopNameMap = Maps.newHashMap();
        lopNameMap.put("fi", "lop name fi");
        lopNameMap.put("sv" , "lop name sv");
        lopNameMap.put("en", "lop name en");

        I18nText lopName = new I18nText(lopNameMap);
        LearningOpportunitySearchResult result = new LearningOpportunitySearchResult("loid", name, "lopid", lopName, "parentId");

        LearningOpportunitySearchResultDTO dto = modelMapper.map(result, LearningOpportunitySearchResultDTO.class);

        assertEquals(result.getId(), dto.getId());
        assertEquals(result.getLopName(), dto.getLopName());

    }
}
