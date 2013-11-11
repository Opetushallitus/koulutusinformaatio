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

package fi.vm.sade.koulutusinformaatio.dao.entity;


import org.mongodb.morphia.annotations.Embedded;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Embedded
public class ExamEntity {

    @Embedded
    private I18nTextEntity type;
    @Embedded
    private I18nTextEntity description;
    @Embedded
    private List<ExamEventEntity> examEvents;
    @Embedded
    private ScoreLimitEntity scoreLimit;

    public ExamEntity() {
    }

    public I18nTextEntity getType() {
        return type;
    }

    public void setType(I18nTextEntity type) {
        this.type = type;
    }

    public I18nTextEntity getDescription() {
        return description;
    }

    public void setDescription(I18nTextEntity description) {
        this.description = description;
    }

    public List<ExamEventEntity> getExamEvents() {
        return examEvents;
    }

    public void setExamEvents(List<ExamEventEntity> examEvents) {
        this.examEvents = examEvents;
    }

    public ScoreLimitEntity getScoreLimit() {
        return scoreLimit;
    }

    public void setScoreLimit(ScoreLimitEntity scoreLimit) {
        this.scoreLimit = scoreLimit;
    }
}
