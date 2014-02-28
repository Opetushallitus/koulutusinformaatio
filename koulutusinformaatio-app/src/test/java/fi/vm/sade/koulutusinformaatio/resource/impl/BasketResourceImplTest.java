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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;

/**
 * @author Markus
 */
public class BasketResourceImplTest {

	private LearningOpportunityService learningOpportunityService;
	private BasketResourceImpl resource;
	BasketItemDTO item;
	
	@Before
	public void setUp() throws InvalidParametersException {
		
		learningOpportunityService = mock(LearningOpportunityService.class);
		List<BasketItemDTO> basketItems = new ArrayList<BasketItemDTO>();
		item = new BasketItemDTO();
		item.setApplicationSystemId("asId");
		item.setMaxApplicationOptions(3);
		basketItems.add(item);
		
		when(learningOpportunityService.getBasketItems(anyList(), anyString())).thenReturn(basketItems);
		
		resource = new BasketResourceImpl(learningOpportunityService);
	}
	
	@Test
	public void testGetBasketItems() {
		List<BasketItemDTO> basketItems = resource.getBasketItems(new ArrayList<String>(), "");
		assertEquals(item.getApplicationSystemId(), basketItems.get(0).getApplicationSystemId());
		assertEquals(item.getMaxApplicationOptions(), basketItems.get(0).getMaxApplicationOptions());
	}
	

}
