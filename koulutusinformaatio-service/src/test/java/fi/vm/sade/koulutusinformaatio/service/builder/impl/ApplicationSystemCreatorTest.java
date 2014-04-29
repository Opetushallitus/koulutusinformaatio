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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationSystemCreatorTest extends KoodistoAwareTest {

    HakuDTO dto;
    Date start;
    Date end;
    ApplicationSystemCreator creator;

    @Before
    public void init() {
        dto = new HakuDTO();
        dto.setOid("1.2.3");
        dto.setMaxHakukohdes(10);
        Map<String, String> name = Maps.newHashMap();
        name.put(getFiUri(), "as name fi");
        dto.setNimi(name);
        HakuaikaRDTO hakuaika = new HakuaikaRDTO();
        Calendar startCal = Calendar.getInstance();
        start = startCal.getTime();
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.MONTH, 1);
        end = endCal.getTime();
        hakuaika.setAlkuPvm(start);
        hakuaika.setLoppuPvm(end);
        dto.setHakuaikas(Lists.newArrayList(hakuaika));

        creator = new ApplicationSystemCreator(koodistoService);
    }

    @Test
    public void testCreateApplicationSystem() throws KoodistoException {
        ApplicationSystem as = creator.createApplicationSystem(dto);
        assertNotNull(as);
        assertEquals("1.2.3", as.getId());
        assertEquals(10, as.getMaxApplications());
        assertEquals("as name fi", as.getName().get(getFi()));
        assertNotNull(as.getApplicationDates());
        DateRange dr = as.getApplicationDates().get(0);
        assertEquals(start, dr.getStartDate());
        assertEquals(end, dr.getEndDate());
    }

    @Test
    public void testCreateApplicationSystemWithNull() throws KoodistoException {
        dto = null;
        assertNull(creator.createApplicationSystem(dto));
    }
}
