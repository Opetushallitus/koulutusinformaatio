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

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

@Entity("snapshot")
public class SnapshotEntity {

    @Id
    private String oid;
    private String content;
    private Date snapshotCreated;

    public SnapshotEntity() {

    }

    public SnapshotEntity(String oid, String content, Date snapshotCreated) {
        this.oid = oid;
        this.content = content;
        this.snapshotCreated = snapshotCreated;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSnapshotCreated() {
        return snapshotCreated;
    }

    public void setSnapshotCreated(Date snapshotCreated) {
        this.snapshotCreated = snapshotCreated;
    }
}
