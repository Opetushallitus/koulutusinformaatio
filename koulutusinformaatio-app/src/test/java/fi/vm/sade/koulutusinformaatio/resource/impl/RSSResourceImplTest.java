package fi.vm.sade.koulutusinformaatio.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.support.ResourceBundleMessageSource;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSFeedDTO;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

public class RSSResourceImplTest {
    
    private RSSResourceImpl RSSResource;
    
    @Before
    public void setUp() throws Exception {
        
        SearchService searchService = mock(SearchService.class);
        ResourceBundleMessageSource messageSource = mock(ResourceBundleMessageSource.class, Mockito.RETURNS_SMART_NULLS);
        
        List<CalendarApplicationSystem> cals = new ArrayList<CalendarApplicationSystem>();
        CalendarApplicationSystem cal = new CalendarApplicationSystem();
        cal.setId("cal1.1.1.1");
        
        I18nText periodName = new I18nText();
        periodName.put("fi", "period name fi");
        periodName.put("sv", "period name sv");
        periodName.put("en", "period name en");
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, 1);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);
        ApplicationPeriod period = createApplicationPeriod(periodName, start.getTime(), end.getTime());
        
        I18nText periodName2 = new I18nText();
        periodName2.put("fi", "period2 name fi");
        periodName2.put("sv", "period2 name sv");
        periodName2.put("en", "period2 name en");
        Calendar start2 = Calendar.getInstance();
        start2.add(Calendar.DATE, 2);
        ApplicationPeriod period2 = createApplicationPeriod(periodName2, start2.getTime(), end.getTime());
        
        cal.setApplicationPeriods(Arrays.asList(period, period2));
        I18nText asName = new I18nText();
        asName.put("fi", "application system name fi");
        asName.put("sv", "application system name sv");
        asName.put("en", "application system name en");
        cal.setName(asName);
        cals.add(cal);
        
        when(searchService.findApplicationSystemsForCalendar()).thenReturn(cals);
        
        RSSResource = new RSSResourceImpl(searchService, messageSource);
        
    }
    
    @Test
    public void testGetApplicationSystemCalendarAsRss() {
        RSSFeedDTO feed = RSSResource.getApplicationSystemCalendarAsRss("fi");
        assertEquals("2.0", feed.getVersion());
        assertEquals(2, feed.getChannel().getItems().size());
        assertEquals("application system name fi, period2 name fi", feed.getChannel().getItems().get(0).getTitle());
        assertEquals("application system name fi, period name fi", feed.getChannel().getItems().get(1).getTitle());
    }
    
    private ApplicationPeriod createApplicationPeriod(I18nText name, Date start, Date end) {
        ApplicationPeriod period = new ApplicationPeriod();
        period.setName(name);
        DateRange range = new DateRange();
        range.setStartDate(start);
        range.setEndDate(end);
        period.setDateRange(range);
        
        return period;
    }

}
