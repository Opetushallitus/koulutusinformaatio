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
import fi.vm.sade.koulutusinformaatio.converter.KomoDTOToParentLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */

public class TarjontaServiceImplTest {

    TarjontaService tarjontaService;
    ConversionService conversionService;
    KomoResource komoResource;
    HakukohdeResource hakukohdeResource;
    KomotoResource komotoResource;
    OrganisaatioService organisaatioService;

    final String ID_1 = "1.2.3";
    final String ID_2 = "1.2.4";
    final String ID_3 = "1.2.5";
    final List<String> IDS = Lists.newArrayList(ID_1, ID_2, ID_3);

    @Before
    public void setUp() {
        conversionService = mock(ConversionService.class);
        KomoDTO komoDTO = new KomoDTO();
        komoDTO.setOid(ID_1);
        komoDTO.setNimi(TestUtil.createI18nText("parent_fi", "parent_sv", "parent_en").getTranslations());

        KomoDTOToParentLearningOpportunity converter = new KomoDTOToParentLearningOpportunity(conversionService);
        when(conversionService.convert(any(KomoDTO.class), eq(ParentLOS.class))).thenReturn(converter.convert(komoDTO));

        komoResource = mock(KomoResource.class);
        when(komoResource.search(null, 0, 0, null, null)).thenReturn(IDS);

        when(komoResource.getByOID(ID_1)).thenReturn(komoDTO);

        hakukohdeResource = mock(HakukohdeResource.class);
        when(hakukohdeResource.search(null, 0, 0, null, null)).thenReturn(IDS);

        komotoResource = mock(KomotoResource.class);

        organisaatioService = mock(OrganisaatioService.class);

        tarjontaService = new TarjontaServiceImpl(komoResource, komotoResource, hakukohdeResource,
                organisaatioService, conversionService);
    }

    @Test
    @Ignore
    public void testListParentLOOids() {
        List<String> oids = tarjontaService.listParentLearnignOpportunityOids();
        assertEquals(3, oids.size());
    }

    @Test
    public void testListAOOids() {
        List<String> oids = tarjontaService.listApplicationOptionOids();
        assertEquals(3, oids.size());
    }

    @Test
    @Ignore
    public void testFindParentLearningOpportunityByOid() throws TarjontaParseException, KoodistoException {
        ParentLOS parent = tarjontaService.findParentLearningOpportunity(ID_1);
        assertNotNull(parent);
        assertEquals(ID_1, parent.getId());
    }

}
