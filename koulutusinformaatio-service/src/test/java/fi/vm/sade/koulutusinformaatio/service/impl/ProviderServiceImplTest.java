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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.converter.OrganisaatioRDTOToProvider;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.OrganisaatioResource;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderServiceImplTest {

    ProviderService providerService;

    public static final String ORGANISAATIO_OID = "1.2.3.4.5";

    @Before
    public void setup() {
        OrganisaatioRDTO o = new OrganisaatioRDTO();
        o.setOid(ORGANISAATIO_OID);
        Map<String, String> name = Maps.newHashMap();
        name.put("fi", "o_name_fi");
        name.put("sv", "o_name_sv");
        o.setNimi(name);
        ConversionService cs = mock(ConversionService.class);
        OrganisaatioRDTOToProvider converter = new OrganisaatioRDTOToProvider();
        when(cs.convert(eq(o), eq(Provider.class))).thenReturn(converter.convert(o));
        OrganisaatioResource or = mock(OrganisaatioResource.class);
        when(or.getOrganisaatioByOID(ORGANISAATIO_OID)).thenReturn(o);
        providerService = new ProviderServiceImpl(or, cs);
    }

    @Test
    public void testGetByOid() throws KoodistoException {
        Provider p = providerService.getByOID(ORGANISAATIO_OID);
        assertNotNull(p);
        assertEquals(p.getId(), ORGANISAATIO_OID);
        assertEquals("o_name_fi", p.getName().getTranslations().get("fi"));
        assertEquals("o_name_sv", p.getName().getTranslations().get("sv"));
    }
}
