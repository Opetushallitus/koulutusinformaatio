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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationSystemDTO {

    private String id;
    private String name;
    private List<DateRangeDTO> applicationDates = new ArrayList<DateRangeDTO>();
    private List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
    private boolean asOngoing;
    private Date nextApplicationPeriodStarts;
    private String status;
    private String applicationFormLink;
    private String hakutapa;
    private String hakutyyppi;
    private boolean useSystemApplicationForm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DateRangeDTO> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRangeDTO> applicationDates) {
        this.applicationDates = applicationDates;
    }

    public List<ApplicationOptionDTO> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionDTO> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public boolean isAsOngoing() {
        return asOngoing;
    }

    public void setAsOngoing(boolean asOngoing) {
        this.asOngoing = asOngoing;
    }

    public Date getNextApplicationPeriodStarts() {
        return nextApplicationPeriodStarts;
    }

    public void setNextApplicationPeriodStarts(Date nextApplicationPeriodStarts) {
        this.nextApplicationPeriodStarts = nextApplicationPeriodStarts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }

    public String getHakutapa() {
        return hakutapa;
    }

    public void setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
    }

    public String getHakutyyppi() {
        return hakutyyppi;
    }

    public void setHakutyyppi(String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    public boolean isUseSystemApplicationForm() {
        return useSystemApplicationForm;
    }

    public void setUseSystemApplicationForm(boolean useSystemApplicationForm) {
        this.useSystemApplicationForm = useSystemApplicationForm;
    }
}
