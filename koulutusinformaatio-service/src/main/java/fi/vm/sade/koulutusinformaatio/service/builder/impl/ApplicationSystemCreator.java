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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationSystemCreator extends ObjectCreator {

    public ApplicationSystemCreator(KoodistoService koodistoService) {
        super(koodistoService);
    }

    public ApplicationSystem createApplicationSystem(HakuDTO hakuDTO) throws KoodistoException {
        if (hakuDTO != null) {
            ApplicationSystem as = new ApplicationSystem();
            as.setId(hakuDTO.getOid());
            as.setMaxApplications(hakuDTO.getMaxHakukohdes());
            as.setName(getI18nText(hakuDTO.getNimi()));
            as.setApplicationFormLink( hakuDTO.getHakulomakeUrl() );
            as.setHakutapaUri( koodistoService.searchFirstCodeValue(hakuDTO.getHakutapaUri()) );
            as.setHakutyyppiUri( koodistoService.searchFirstCodeValue(hakuDTO.getHakutyyppiUri()));
            if (hakuDTO.getHakuaikas() != null) {
                for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                    DateRange range = new DateRange();
                    range.setStartDate(ha.getAlkuPvm());
                    range.setEndDate(ha.getLoppuPvm());
                    as.getApplicationDates().add(range);
                }
            }
            return as;
        } else {
            return null;
        }
    }
    
    public ApplicationSystem createHigherEdApplicationSystem(HakuV1RDTO haku) throws KoodistoException {
        ApplicationSystem as = new ApplicationSystem();
        as.setId(haku.getOid());
        as.setMaxApplications(haku.getMaxHakukohdes());
        as.setName(getI18nText(haku.getNimi()));
        as.setApplicationFormLink( haku.getHakulomakeUri());
        as.setHakutapaUri(koodistoService.searchFirstCodeValue(haku.getHakutapaUri()));
        as.setHakutyyppiUri(koodistoService.searchFirstCodeValue(haku.getHakutyyppiUri()));
        
        return as;
    }
    
    public CalendarApplicationSystem createApplicationSystemForCalendar(HakuV1RDTO haku) throws KoodistoException {
        CalendarApplicationSystem as = new CalendarApplicationSystem();
        as.setId(haku.getOid());
        //as.setMaxApplications(haku.getMaxHakukohdes());
        as.setName(getI18nText(haku.getNimi()));
        //as.setApplicationFormLink( haku.getHakulomakeUri());
        //as.setHakutapaUri(koodistoService.searchFirstCodeValue(haku.getHakutapaUri()));
        //as.setHakutyyppiUri(koodistoService.searchFirstCodeValue(haku.getHakutyyppiUri()));
        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : haku.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                
                ApplicationPeriod ap = new ApplicationPeriod();
                ap.setDateRange(range);
                ap.setName(getI18nText(ha.getNimet()));
                
                as.getApplicationPeriods().add(ap);
                //as.getApplicationDates().add(range);
            }
        }
        
        return as;
    }
    
    /*
    public ApplicationSystem createHigherEdApplicationSystemWithDates(HakuV1RDTO haku) throws KoodistoException {
        ApplicationSystem as = new ApplicationSystem();
        as.setId(haku.getOid());
        as.setMaxApplications(haku.getMaxHakukohdes());
        as.setName(getI18nText(haku.getNimi()));
        as.setApplicationFormLink( haku.getHakulomakeUri());
        as.setHakutapaUri(koodistoService.searchFirstCodeValue(haku.getHakutapaUri()));
        as.setHakutyyppiUri(koodistoService.searchFirstCodeValue(haku.getHakutyyppiUri()));
        if (haku.getHakuaikas() != null) {
            for () {
                
            }
        }
        
        return as;
    }*/
}
