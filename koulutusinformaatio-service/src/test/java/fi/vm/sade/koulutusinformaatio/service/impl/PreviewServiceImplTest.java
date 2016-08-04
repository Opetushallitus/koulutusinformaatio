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

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.*;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 
 * @author Markus
 */
public class PreviewServiceImplTest {
	
	private TarjontaService tarjontaService;
	
	private PreviewService service;
	
	@Before
    public void setup() throws TarjontaParseException, KoodistoException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException {
        tarjontaService = mock(TarjontaService.class);

		HigherEducationLOS heLOS = new HigherEducationLOS();
		heLOS.setId("1.2.3.4");
		
		when(tarjontaService.findHigherEducationLearningOpportunity(heLOS.getId())).thenReturn(heLOS);
		
		service = new PreviewServiceImpl(tarjontaService);
	}
	
	@Test
	public void testPreviewHigherEducationLearningOpportunity() throws ResourceNotFoundException {
		HigherEducationLOS los = service.previewHigherEducationLearningOpportunity("1.2.3.4");
		assertEquals("1.2.3.4", los.getId());
	}
	
	@Test(expected = ResourceNotFoundException.class) 
	public void testPreviewNotFound() throws ResourceNotFoundException {
		service.previewHigherEducationLearningOpportunity("NotFound");
	}

}
