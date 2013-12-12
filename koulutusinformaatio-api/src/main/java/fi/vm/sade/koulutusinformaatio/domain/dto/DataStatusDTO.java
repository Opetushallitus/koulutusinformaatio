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

    private Date lastUpdateFinished;
    private String lastUpdateFinishedStr;
    private long lastUpdateDuration;
    private String lastUpdateDurationStr;
    private String lastUpdateOutcome;
    private boolean running;
    private Date runningSince;
    private String runningSinceStr;

    public Date getLastUpdateFinished() {
        return lastUpdateFinished;
    }

    public void setLastUpdateFinished(Date lastUpdateFinished) {
        this.lastUpdateFinished = lastUpdateFinished;
    }

    public String getLastUpdateFinishedStr() {
        return lastUpdateFinishedStr;
    }

    public void setLastUpdateFinishedStr(String lastUpdateFinishedStr) {
        this.lastUpdateFinishedStr = lastUpdateFinishedStr;
    }

    public long getLastUpdateDuration() {
        return lastUpdateDuration;
    }

    public void setLastUpdateDuration(long lastUpdateDuration) {
        this.lastUpdateDuration = lastUpdateDuration;
    }

    public String getLastUpdateDurationStr() {
        return lastUpdateDurationStr;
    }

    public void setLastUpdateDurationStr(String lastUpdateDurationStr) {
        this.lastUpdateDurationStr = lastUpdateDurationStr;
    }

    public String getLastUpdateOutcome() {
        return lastUpdateOutcome;
    }

    public void setLastUpdateOutcome(String lastUpdateOutcome) {
        this.lastUpdateOutcome = lastUpdateOutcome;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Date getRunningSince() {
        return runningSince;
    }

    public void setRunningSince(Date runningSince) {
        this.runningSince = runningSince;
    }

    public String getRunningSinceStr() {
        return runningSinceStr;
    }

    public void setRunningSinceStr(String runningSinceStr) {
        this.runningSinceStr = runningSinceStr;
    }
}
