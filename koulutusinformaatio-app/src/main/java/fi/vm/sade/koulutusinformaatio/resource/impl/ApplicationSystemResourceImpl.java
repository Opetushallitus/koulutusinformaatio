package fi.vm.sade.koulutusinformaatio.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.converter.ApplicationSystemToDTO;
import fi.vm.sade.koulutusinformaatio.converter.CalendarApplicationSystemToDTO;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.ApplicationSystemResource;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

@Component
public class ApplicationSystemResourceImpl implements ApplicationSystemResource {

    private SearchService searchService;
    
    @Autowired
    public ApplicationSystemResourceImpl(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @Override
    public List<CalendarApplicationSystemDTO> fetchApplicationSystemsForCalendar(
            String uiLang) {
        
        try {
                List<CalendarApplicationSystem> apps = this.searchService.findApplicationSystemsForCalendar();
                return CalendarApplicationSystemToDTO.convertAll(apps, uiLang);
                
            } catch (SearchException e) {
                throw KIExceptionHandler.resolveException(e);
            }
        
        
       
    }

}
