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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.converter.CalendarApplicationSystemToDTO;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.ApplicationSystemResource;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

/**
 * 
 * @author Markus
 *
 */
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
