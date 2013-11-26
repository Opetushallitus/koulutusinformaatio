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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class DataStatusDTO {

    Date lastUpdated = new Date();
    String lastUpdatedStr;
    private long lastUpdateDuration;
    private String lastUpdateDurationStr;
    private boolean running;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdateDuration() {
        return lastUpdateDuration;
    }

    public void setLastUpdateDuration(long lastUpdateDuration) {
        this.lastUpdateDuration = lastUpdateDuration;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getLastUpdatedStr() {
        return lastUpdatedStr;
    }

    public void setLastUpdatedStr(String lastUpdatedStr) {
        this.lastUpdatedStr = lastUpdatedStr;
    }

    public String getLastUpdateDurationStr() {
        return lastUpdateDurationStr;
    }

    public void setLastUpdateDurationStr(String lastUpdateDurationStr) {
        this.lastUpdateDurationStr = lastUpdateDurationStr;
    }
}
