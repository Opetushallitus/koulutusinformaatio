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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class BasketItemDTO {

    private String applicationSystemId;
    private String applicationSystemName;
    private int maxApplicationOptions;
    private String applicationFormLink;
    private List<DateRangeDTO> applicationDates = new ArrayList<DateRangeDTO>();
    private List<BasketApplicationOptionDTO> applicationOptions = new ArrayList<BasketApplicationOptionDTO>();
    private boolean asOngoing;
    private Date nextApplicationPeriodStarts;
    private boolean useSystemApplicationForm;

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getApplicationSystemName() {
        return applicationSystemName;
    }

    public void setApplicationSystemName(String applicationSystemName) {
        this.applicationSystemName = applicationSystemName;
    }

    public List<DateRangeDTO> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRangeDTO> applicationDates) {
        this.applicationDates = applicationDates;
    }

    public List<BasketApplicationOptionDTO> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<BasketApplicationOptionDTO> applicationOptions) {
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

    public int getMaxApplicationOptions() {
        return maxApplicationOptions;
    }

    public void setMaxApplicationOptions(int maxApplicationOptions) {
        this.maxApplicationOptions = maxApplicationOptions;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }

    public boolean isUseSystemApplicationForm() {
        return useSystemApplicationForm;
    }

    public void setUseSystemApplicationForm(boolean useSystemApplicationForm) {
        this.useSystemApplicationForm = useSystemApplicationForm;
    }
}
