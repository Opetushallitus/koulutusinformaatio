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

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class Exam {

    private I18nText type;
    private I18nText description;
    private List<ExamEvent> examEvents;
    private ScoreLimit scoreLimit;

    public I18nText getType() {
        return type;
    }

    public void setType(I18nText type) {
        this.type = type;
    }

    public I18nText getDescription() {
        return description;
    }

    public void setDescription(I18nText description) {
        this.description = description;
    }

    public List<ExamEvent> getExamEvents() {
        return examEvents;
    }

    public void setExamEvents(List<ExamEvent> examEvents) {
        this.examEvents = examEvents;
    }

    public ScoreLimit getScoreLimit() {
        return scoreLimit;
    }

    public void setScoreLimit(ScoreLimit scoreLimit) {
        this.scoreLimit = scoreLimit;
    }
}
