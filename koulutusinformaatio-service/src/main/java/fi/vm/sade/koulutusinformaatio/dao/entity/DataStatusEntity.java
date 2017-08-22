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

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;

/**
 * @author Mikko Majapuro
 */
@Entity("dataStatus")
public class DataStatusEntity {

    @Id
    private org.bson.types.ObjectId id;
    private Date lastUpdateFinished = new Date();
    private Date lastSEOIndexingUpdateFinished = new Date();
    private long lastUpdateDuration;
    private String lastUpdateOutcome;
    private long progressCounter;

    public DataStatusEntity() {}

    @PrePersist
    void prePersist() {
        lastUpdateFinished = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public Date getLastSEOIndexingUpdateFinished() {
        return lastSEOIndexingUpdateFinished;
    }

    public void setLastSEOIndexingUpdateFinished(Date lastSEOIndexingUpdateFinished) {
        this.lastSEOIndexingUpdateFinished = lastSEOIndexingUpdateFinished;
    }
}
