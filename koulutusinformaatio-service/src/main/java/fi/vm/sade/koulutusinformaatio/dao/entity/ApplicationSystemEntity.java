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

import com.google.code.morphia.annotations.Embedded;

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
}
