package fi.vm.sade.koulutusinformaatio.resource.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.converter.CalendarApplicationSystemToRSS;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSChannelDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSChannelItemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.rss.RSSFeedDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.RSSResource;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

@Component
public class RSSResourceImpl implements RSSResource {
    
    private SearchService searchService;
    private ResourceBundleMessageSource messageSource;
    
    private static final String RSS_VERSION = "2.0";
    
    @Autowired
    public RSSResourceImpl(SearchService searchService, ResourceBundleMessageSource messageSource) {
        this.searchService = searchService;
        this.messageSource = messageSource;
    }

    @Override
    public RSSFeedDTO getApplicationSystemCalendarAsRss(String lang) {
        try {
            List<CalendarApplicationSystem> cas = this.searchService.findApplicationSystemsForCalendar();
            return createFeed(cas, lang);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }

    }

    @Override
    public RSSFeedDTO getApplicationSystemCalendarForHigherEducationAsRss(String lang) {
        try {
            List<CalendarApplicationSystem> cas = this.searchService.findApplicationSystemsForCalendar(SolrUtil.SolrConstants.AS_TARGET_GROUP_CODE_HIGHERED);
            return createFeed(cas, lang);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public RSSFeedDTO getApplicationSystemCalendarForVocationalEducationAsRss(String lang) {
        try {
            List<CalendarApplicationSystem> cas = this.searchService.findApplicationSystemsForCalendar(SolrUtil.SolrConstants.AS_TARGET_GROUP_CODE_VOCATIONAL);
            return createFeed(cas, lang);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public RSSFeedDTO getApplicationSystemCalendarForPreparatoryEducationAsRss(String lang) {
        try {
            List<CalendarApplicationSystem> cas = this.searchService.findApplicationSystemsForCalendar(SolrUtil.SolrConstants.AS_TARGET_GROUP_CODE_PREPARATORY);
            return createFeed(cas, lang);
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
    
    private RSSFeedDTO createFeed(List<CalendarApplicationSystem> cas, String lang) {
        List<RSSChannelItemDTO> items = CalendarApplicationSystemToRSS.convertAll(cas, lang, messageSource);
        
        RSSFeedDTO feed = new RSSFeedDTO();
        feed.setVersion(RSS_VERSION);
        
        RSSChannelDTO channel = new RSSChannelDTO();
        channel.setItems(items);
        channel.setTitle(messageSource.getMessage("rss.as.calendar.title", new Object[]{}, new Locale(lang)));
        channel.setLink(messageSource.getMessage("rss.as.calendar.link", new Object[]{}, new Locale(lang)));
        channel.setDescription(messageSource.getMessage("rss.as.calendar.description", new Object[]{}, new Locale(lang)));
        channel.setLanguage(lang);
        
        feed.setChannel(channel);
        
        return feed;
    }

}
