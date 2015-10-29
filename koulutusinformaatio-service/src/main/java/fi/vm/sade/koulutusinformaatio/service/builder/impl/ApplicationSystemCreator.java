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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationPeriod;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystemParameters;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationSystemCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSystemCreator.class);

    private static final String VARSINAINEN_HAKU = "hakutyyppi_01";
    private ParameterService parameterService;

    private List<String> overriddenASOids;

    public ApplicationSystemCreator(KoodistoService koodistoService, ParameterService parameterService, List<String> overriddenASOids) {
        super(koodistoService);
        this.parameterService = parameterService;
        this.overriddenASOids = overriddenASOids;
    }

    public ApplicationSystem createApplicationSystem(HakuV1RDTO asDto) throws KoodistoException {
        if (asDto != null) {
            ApplicationSystem as = new ApplicationSystem();
            as.setId(asDto.getOid());
            as.setMaxApplications(asDto.getMaxHakukohdes());
            as.setName(getI18nText(asDto.getNimi()));
            as.setApplicationFormLink( asDto.getHakulomakeUri() );
            as.setHakutapaUri( koodistoService.searchFirstCodeValue(asDto.getHakutapaUri()) );
            as.setHakutyyppiUri( koodistoService.searchFirstCodeValue(asDto.getHakutyyppiUri()));
            if (asDto.getHakuaikas() != null) {
                for (HakuaikaV1RDTO ha : asDto.getHakuaikas()) {
                    DateRange range = new DateRange();
                    range.setStartDate(ha.getAlkuPvm());
                    range.setEndDate(ha.getLoppuPvm());
                    as.getApplicationDates().add(range);
                }

            }
            if (asDto.getHakutapaUri().contains(TarjontaConstants.HAKUTAPA_YHTEISHAKU)) {
                HandleHakuParameters(as);
            } else {
                as.setShownAsFacet(false);
            }
            as.setShowEducationsUntil(asDto.getOpintopolunNayttaminenLoppuu());
            as.setUseSystemApplicationForm(asDto.isJarjestelmanHakulomake());

            // Demoympäristöä varten pakotetaan haku näkyviin.
            if (overriddenASOids != null && overriddenASOids.contains(asDto.getOid())) {
                LOG.warn("Puukotetaan demohaku {} näkyviin!", as.getId());
                addDemoApplicationDates(as);
                as.setShownAsFacet(true);
                Calendar end = Calendar.getInstance();
                end.add(Calendar.MONTH, 16);
                as.setShowEducationsUntil(end.getTime());
            }
            return as;
        } else {
            return null;
        }
    }

    private void addDemoApplicationDates(ApplicationSystem as) {
        DateRange range = new DateRange();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, -6);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);

        range.setStartDate(start.getTime());
        range.setEndDate(end.getTime());

        as.getApplicationDates().add(range);
    }

    /*
     * Setting the date range when this application system should be shown
     * as a facet filter in faceted search.
     */
    private void HandleHakuParameters(ApplicationSystem as) {
        ApplicationSystemParameters params = this.parameterService.getParametersForHaku(as.getId());
        Date now = Calendar.getInstance().getTime();
        if (params != null
                && params.getShownInFacetedSearch() != null
                && params.getShownInFacetedSearch().getDateStart() != null
                && !params.getShownInFacetedSearch().getDateStart().after(now)
                && params.getShownInFacetedSearch().getDateEnd() != null
                && !params.getShownInFacetedSearch().getDateEnd().before(now)) {
            as.setShownAsFacet(true);
            as.setFacetRange(new DateRange(params.getShownInFacetedSearch().getDateStart(), params.getShownInFacetedSearch().getDateEnd()));
        } else {
            as.setShownAsFacet(false);
        }
    }

    public CalendarApplicationSystem createApplicationSystemForCalendar(HakuV1RDTO haku, boolean shownInCalendar) throws KoodistoException {
        CalendarApplicationSystem as = new CalendarApplicationSystem();
        as.setId(haku.getOid());
        as.setName(getI18nText(haku.getNimi()));
        as.setShownInCalendar(shownInCalendar);
        as.setVarsinainenHaku(haku.getHakutyyppiUri().contains(VARSINAINEN_HAKU));

        as.setTargetGroupCode(koodistoService.searchFirstCodeValue( haku.getKohdejoukkoUri() ));
        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : haku.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());

                ApplicationPeriod ap = new ApplicationPeriod();
                ap.setDateRange(range);
                ap.setName(getI18nText(ha.getNimet()));

                as.getApplicationPeriods().add(ap);
            }
        }

        return as;
    }
}
