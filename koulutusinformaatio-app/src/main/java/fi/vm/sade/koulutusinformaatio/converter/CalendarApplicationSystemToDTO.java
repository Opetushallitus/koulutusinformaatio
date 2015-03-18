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

package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationPeriodDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.CalendarApplicationSystemDTO;

/**
 * 
 * @author Markus
 *
 */
public final class CalendarApplicationSystemToDTO {
    
    private CalendarApplicationSystemToDTO() {
        
    }
    
    
    
    private static CalendarApplicationSystemDTO convert(final CalendarApplicationSystem applicationSystem, final String lang) {
        if (applicationSystem != null) {
            
            CalendarApplicationSystemDTO calendarAS = new CalendarApplicationSystemDTO();
            calendarAS.setId(applicationSystem.getId());
            calendarAS.setName(ConverterUtil.getTextByLanguageUseFallbackLang(applicationSystem.getName(), lang));
            calendarAS.setAsOngoing(ConverterUtil.isCalendarApplicationsystemOngoing(applicationSystem.getApplicationPeriods()));
            calendarAS.setApplicationPeriods(convertApplicationPeriods(applicationSystem.getApplicationPeriods(), lang));
            return calendarAS;
        } else {
            return null;
        }
    }
    
    private static List<ApplicationPeriodDTO> convertApplicationPeriods(
            List<ApplicationPeriod> applicationPeriods, final String lang) {
        
        List<ApplicationPeriodDTO> dtos = new ArrayList<ApplicationPeriodDTO>();
        
        if (applicationPeriods != null) {
            for (ApplicationPeriod curPeriod : applicationPeriods) {
                ApplicationPeriodDTO dto = convertPeriod(curPeriod, lang);
                if (dto != null) {
                    dtos.add(dto);
                }
                
            }
        }
        
        return dtos;
    }



    private static ApplicationPeriodDTO convertPeriod(
            ApplicationPeriod curPeriod, final String lang) {
        if (curPeriod != null) {
            ApplicationPeriodDTO dto = new ApplicationPeriodDTO();
            dto.setDateRange(DateRangeToDTO.convert(curPeriod.getDateRange()));
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(curPeriod.getName(), lang));
            return dto;
        }
        return null;
    }



    public static List<CalendarApplicationSystemDTO> convertAll(final List<CalendarApplicationSystem> applicationSystems, final String lang) {
        
        List<CalendarApplicationSystemDTO> dtos = new ArrayList<CalendarApplicationSystemDTO>();
        if (applicationSystems != null) {
            for (CalendarApplicationSystem curAs : applicationSystems) {
                if ("en".equalsIgnoreCase(lang) 
                        && (curAs.getName() == null 
                            || curAs.getName().get("en") == null)) {
                    continue;
                }
                CalendarApplicationSystemDTO curDto = convert(curAs, lang);
                if (curDto != null) {
                    dtos.add(curDto);
                }
            }
        }
        
        return dtos;
    }

}
