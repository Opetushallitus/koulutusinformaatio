package fi.vm.sade.koulutusinformaatio.converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSChannelItemDTO;

/*
 * Converts CalendarApplicationSystem to an RSS feed channel item
 */
public class CalendarApplicationSystemToRSS {
    
    /*
     * Converts a list of calendar application systems to RSS channel items. Channel items are sorted by application period start date.
     */
    public static List<RSSChannelItemDTO> convertAll(final List<CalendarApplicationSystem> applicationSystems, final String lang, final ResourceBundleMessageSource messageSource) {
        
        List<RSSChannelItemDTO> dtos = new ArrayList<RSSChannelItemDTO>();
        if (applicationSystems != null) {
            for (CalendarApplicationSystem cas : applicationSystems) {
                // include only application systems with English name if requested language is English
                if ("en".equalsIgnoreCase(lang) 
                        && (cas.getName() == null 
                            || cas.getName().get("en") == null)) {
                    continue;
                }
                List<RSSChannelItemDTO> curDtos = convert(cas, lang, messageSource);
                dtos.addAll(curDtos);
            }
        }
        
        // order items by application period start date
        Collections.sort(dtos, new Comparator<RSSChannelItemDTO>() {
            @Override
            public int compare(RSSChannelItemDTO item1, RSSChannelItemDTO  item2)
            {
                return  item1.getTimestamp().compareTo(item2.getTimestamp());
            }
        });
        
        return dtos;
    }
    
    /*
     * Converts a single CalendarApplicationSystem to a list of RSS channel items. For each application period a new channel item gets created.
     */
    private static List<RSSChannelItemDTO> convert(CalendarApplicationSystem cas, String lang, final ResourceBundleMessageSource messageSource) {
        List<RSSChannelItemDTO> items = new ArrayList<RSSChannelItemDTO>();
        if (cas != null) {
            for (ApplicationPeriod ap : cas.getApplicationPeriods()) {
                RSSChannelItemDTO rssItem = new RSSChannelItemDTO();
                
                // set title
                StringBuilder builder = new StringBuilder( ConverterUtil.getTextByLanguageUseFallbackLang(cas.getName(), lang) );
                String apName = ConverterUtil.getTextByLanguageUseFallbackLang(ap.getName(), lang);
                if (apName != null) {
                    builder.append(", ");
                    builder.append( apName );
                }
                rssItem.setTitle( builder.toString() );
                
                // set description
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy '" + messageSource.getMessage("rss.as.calendar.time.abbreviation", new Object[]{}, new Locale(lang)) + "' HH:mm");
                builder = new StringBuilder( messageSource.getMessage("rss.as.calendar.applicationtime", new Object[]{}, new Locale(lang)) + ": " );
                builder.append( sdf.format( ap.getDateRange().getStartDate() ) );
                builder.append(" - ");
                builder.append( sdf.format( ap.getDateRange().getEndDate() ) );
                rssItem.setDescription( builder.toString() );
             
                // set timestamp for ordering
                rssItem.setTimestamp( ap.getDateRange().getStartDate() );
                    
                items.add(rssItem);
            }
        }
        
        return items;
    }

}
