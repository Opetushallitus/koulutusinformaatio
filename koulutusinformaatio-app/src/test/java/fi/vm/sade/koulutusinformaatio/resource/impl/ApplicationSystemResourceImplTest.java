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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 
 * @author Markus
 *
 */
public class ApplicationSystemResourceImplTest {
    
    private ApplicationSystemResourceImpl applicationSystemResource;
    
    @Before
    public void setUp() throws Exception {
        
        SearchService searchService = mock(SearchService.class);
        
        List<CalendarApplicationSystem> cals = new ArrayList<CalendarApplicationSystem>();
        CalendarApplicationSystem cal = new CalendarApplicationSystem();
        cal.setId("cal1.1.1.1");
        ApplicationPeriod period = new ApplicationPeriod();
        I18nText periodName = new I18nText();
        periodName.put("fi", "period name fi");
        periodName.put("sv", "period name sv");
        periodName.put("en", "period name en");
        period.setName(periodName);
        DateRange range = new DateRange();
        range.setStartDate(new Date());
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);
        range.setEndDate(end.getTime());
        period.setDateRange(range);
        cal.setApplicationPeriods(Arrays.asList(period));
        I18nText asName = new I18nText();
        asName.put("fi", "application system name fi");
        asName.put("sv", "application system name sv");
        asName.put("en", "application system name en");
        cal.setName(asName);
        cals.add(cal);
        
        when(searchService.findApplicationSystemsForCalendar()).thenReturn(cals);
        
        applicationSystemResource = new ApplicationSystemResourceImpl(searchService);
        
    }
    
    @Test
    public void testFetchApplicationSystemsForCalendar() {
        List<CalendarApplicationSystemDTO> cals = applicationSystemResource.fetchApplicationSystemsForCalendar("fi");
        CalendarApplicationSystemDTO cal = cals.get(0);
        assertEquals("application system name fi", cal.getName());
        assertEquals("cal1.1.1.1", cal.getId());
    }

}
