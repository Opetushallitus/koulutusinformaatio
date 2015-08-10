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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.Date;

/**
 * @author Hannu Lyytikainen
 */
public class DataStatus {

    private Date lastUpdateFinished;
    private long lastUpdateDuration;
    private String lastUpdateOutcome;
    private long progressCounter;

    public DataStatus() {}
    
    public DataStatus(Date lastUpdateFinished, long lastUpdateDuration, String lastUpdateOutcome, long progressCounter) {
        this.lastUpdateFinished = lastUpdateFinished;
        this.lastUpdateDuration = lastUpdateDuration;
        this.lastUpdateOutcome = lastUpdateOutcome;
        this.progressCounter = progressCounter;
    }

    public DataStatus(Date lastUpdateFinished, long lastUpdateDuration, String lastUpdateOutcome) {
        this(lastUpdateFinished, lastUpdateDuration, lastUpdateOutcome, 0);
    }

    public Date getLastUpdateFinished() {
        return lastUpdateFinished;
    }

    public void setLastUpdateFinished(Date lastUpdateFinished) {
        this.lastUpdateFinished = lastUpdateFinished;
    }

    public long getLastUpdateDuration() {
        return lastUpdateDuration;
    }

    public void setLastUpdateDuration(long lastUpdateDuration) {
        this.lastUpdateDuration = lastUpdateDuration;
    }

    public String getLastUpdateOutcome() {
        return lastUpdateOutcome;
    }

    public void setLastUpdateOutcome(String lastUpdateOutcome) {
        this.lastUpdateOutcome = lastUpdateOutcome;
    }

    public long getProgressCounter() {
        return progressCounter;
    }

    public void setProgressCounter(long progressCounter) {
        this.progressCounter = progressCounter;
    }
}
