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

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public ApplicationSystem createApplicationSystemForAo(HakuV1RDTO hakuDto, HakukohdeV1RDTO hakukohdeDto) throws KoodistoException {
        if (hakuDto == null)
            return null;

        ApplicationSystem as = new ApplicationSystem();
        as.setId(hakuDto.getOid());
        as.setMaxApplications(hakuDto.getMaxHakukohdes());
        as.setName(getI18nText(hakuDto.getNimi()));
        as.setApplicationFormLink(hakukohdeDto.getHakulomakeUrl());
        as.setHakutapaUri(koodistoService.searchFirstCodeValue(hakuDto.getHakutapaUri()));
        as.setHakutyyppiUri(koodistoService.searchFirstCodeValue(hakuDto.getHakutyyppiUri()));
        if (hakuDto.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : hakuDto.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);
            }

        }
        if (hakuDto.getHakutapaUri().contains(TarjontaConstants.HAKUTAPA_YHTEISHAKU)) {
            HandleHakuParameters(as);
        } else {
            as.setShownAsFacet(false);
        }
        as.setShowEducationsUntil(hakuDto.getOpintopolunNayttaminenLoppuu());
        as.setUseSystemApplicationForm(hakuDto.isJarjestelmanHakulomake());
        as.setAtaruFormKey(hakuDto.getAtaruLomakeAvain());
        as.setSiirtohaku(isSiirtohaku(hakuDto));

        // Demoympäristöä varten pakotetaan haku näkyviin.
        if (overriddenASOids != null && overriddenASOids.contains(hakuDto.getOid())) {
            LOG.warn("Puukotetaan demohaku {} näkyviin!", as.getId());
            as.getApplicationDates().add(getDemoRange(hakuDto));
            as.setShownAsFacet(true);
            as.setShowEducationsUntil(getModifiedDate(hakuDto.getHakuaikas().get(0).getLoppuPvm(), 12));
        }
        return as;
    }

    private boolean isSiirtohaku(HakuV1RDTO hakuDto) {
        return nullsafeCodeUriEquals(hakuDto.getHakutapaUri(), TarjontaConstants.HAKUTAPA_ERILLIS)
                && nullsafeCodeUriEquals(hakuDto.getKohdejoukkoUri(), TarjontaConstants.KOHDEJOUKKO_KORKEAKOULUTUS)
                && nullsafeCodeUriEquals(hakuDto.getKohdejoukonTarkenne(), TarjontaConstants.KOHDEJOUKONTARKENNE_SIIRTOHAKU);
    }

    // Stripts the version number from code uri and check for equality
    private boolean nullsafeCodeUriEquals(String codeUri, String value) {
        return StringUtils.equals(StringUtils.defaultString(codeUri).split("#")[0], value);
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
        as.setAtaruFormKey(haku.getAtaruLomakeAvain());

        as.setTargetGroupCode(koodistoService.searchFirstCodeValue(haku.getKohdejoukkoUri()));
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

        if (overriddenASOids != null && overriddenASOids.contains(haku.getOid())) {
            LOG.warn("Puukotetaan demohaku {} näkyviin!", as.getId());
            ApplicationPeriod ap = new ApplicationPeriod();
            ap.setDateRange(getDemoRange(haku));
            Map<String, String> translations = Maps.newHashMap();
            translations.put("fi", "Demohakuaika");
            I18nText demoName = new I18nText(translations);
            ap.setName(demoName);

            as.getApplicationPeriods().add(ap);
        } else if (!overriddenASOids.isEmpty()) { // Hakua ei haluta näyttää kalenterissa, jos sitä ei ole määritetty näkymään demoympäristössä.
            return null;
        }

        return as;
    }

    private DateRange getDemoRange(HakuV1RDTO haku) {
        DateRange demorange = new DateRange();
        demorange.setStartDate(getModifiedDate(haku.getHakuaikas().get(0).getAlkuPvm(), -12));
        demorange.setEndDate(getModifiedDate(haku.getHakuaikas().get(0).getLoppuPvm(), 12));
        return demorange;
    }

    private Date getModifiedDate(Date date, int months) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.add(Calendar.MONTH, months);
        return start.getTime();
    }
}
