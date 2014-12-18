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

package fi.vm.sade.koulutusinformaatio.dao.entity;

import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ApplicationSystemEntity {

    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private List<DateRangeEntity> applicationDates = new ArrayList<DateRangeEntity>();

    private int maxApplications;
    private String applicationFormLink;
    private String hakutapaUri;
    private String hakutyyppiUri;
    private boolean shownAsFacet;
    @Embedded
    private DateRangeEntity facetRange;
    private boolean useSystemApplicationForm;

    public ApplicationSystemEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public List<DateRangeEntity> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRangeEntity> applicationDates) {
        this.applicationDates = applicationDates;
    }

    public int getMaxApplications() {
        return maxApplications;
    }

    public void setMaxApplications(int maxApplications) {
        this.maxApplications = maxApplications;
    }

    public String getApplicationFormLink() {
        return applicationFormLink;
    }

    public void setApplicationFormLink(String applicationFormLink) {
        this.applicationFormLink = applicationFormLink;
    }

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this.hakutapaUri = hakutapaUri;
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

    public boolean isShownAsFacet() {
        return shownAsFacet;
    }

    public void setShownAsFacet(boolean shownAsFacet) {
        this.shownAsFacet = shownAsFacet;
    }

    public DateRangeEntity getFacetRange() {
        return facetRange;
    }

    public void setFacetRange(DateRangeEntity facetRange) {
        this.facetRange = facetRange;
    }

    public boolean isUseSystemApplicationForm() {
        return useSystemApplicationForm;
    }

    public void setUseSystemApplicationForm(boolean useSystemApplicationForm) {
        this.useSystemApplicationForm = useSystemApplicationForm;
    }
}
