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
package fi.vm.sade.koulutusinformaatio.service.impl.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

/**
 * 
 * @author Markus
 */
public class LocationQueryTest {
   
    @Test
    public void testLocationQueryDistricts() {
        LocationQuery q = new LocationQuery(LocationFields.TYPE, SolrConstants.DISTRICT_UNKNOWN, "fi");
        assertNotNull(q);        
        assertEquals(3, q.getFilterQueries().length);
        assertEquals(String.format("%s:%s", LocationFields.TYPE, SolrConstants.DISTRICT_UNKNOWN), q.getQuery().toString());
    }
    
    @Test
    public void testLocationQueryChildLocations() {
        List<String> values = Arrays.asList(SolrConstants.DISTRICT_UNKNOWN);
        LocationQuery q = new LocationQuery(LocationFields.TYPE,values, "fi");
        assertNotNull(q);        
        assertEquals(1, q.getFilterQueries().length);
        assertEquals(String.format("%s:(%s)", LocationFields.TYPE, Joiner.on(" OR ").join(values)), q.getQuery().toString());
    }
    
}
