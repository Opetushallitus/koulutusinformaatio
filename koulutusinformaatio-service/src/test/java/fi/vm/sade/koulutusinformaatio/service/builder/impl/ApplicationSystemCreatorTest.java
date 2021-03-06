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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationSystemCreatorTest extends KoodistoAwareTest {

    HakuV1RDTO dto;
    Date start;
    Date end;
    ApplicationSystemCreator creator;
    private final static String AO_FORM_LINK = "http//hakukohdekohtainen.com";

    @Before
    public void init() {
        dto = new HakuV1RDTO();
        dto.setOid("1.2.3");
        dto.setMaxHakukohdes(10);
        Map<String, String> name = Maps.newHashMap();
        name.put(getFiUri(), "as name fi");
        dto.setNimi(name);
        HakuaikaV1RDTO hakuaika = new HakuaikaV1RDTO();
        Calendar startCal = Calendar.getInstance();
        start = startCal.getTime();
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.MONTH, 1);
        end = endCal.getTime();
        hakuaika.setAlkuPvm(start);
        hakuaika.setLoppuPvm(end);
        dto.setHakuaikas(Lists.newArrayList(hakuaika));
        dto.setHakutapaUri(TarjontaConstants.HAKUTAPA_YHTEISHAKUV1);
        dto.setAtaruLomakeAvain("1.2.3.4");

        creator = new ApplicationSystemCreator(koodistoService, mock(ParameterService.class), Lists.<String> newArrayList());
    }

    private HakukohdeV1RDTO getHakukohdeDto() {
        HakukohdeV1RDTO dto = new HakukohdeV1RDTO();
        dto.setHakulomakeUrl(AO_FORM_LINK);
        return dto;
    }

    @Test
    public void testCreateApplicationSystem() throws KoodistoException {
        ApplicationSystem as = creator.createApplicationSystemForAo(dto, getHakukohdeDto());
        assertNotNull(as);
        assertEquals(AO_FORM_LINK, as.getApplicationFormLink());
        assertEquals("1.2.3", as.getId());
        assertEquals(10, as.getMaxApplications());
        assertEquals("as name fi", as.getName().get(getFi()));
        assertNotNull(as.getApplicationDates());
        DateRange dr = as.getApplicationDates().get(0);
        assertEquals(start, dr.getStartDate());
        assertEquals(end, dr.getEndDate());
        assertEquals("1.2.3.4", as.getAtaruFormKey());
    }

    @Test
    public void testCreateApplicationSystemWithNull() throws KoodistoException {
        dto = null;
        assertNull(creator.createApplicationSystemForAo(dto, getHakukohdeDto()));
    }
}
