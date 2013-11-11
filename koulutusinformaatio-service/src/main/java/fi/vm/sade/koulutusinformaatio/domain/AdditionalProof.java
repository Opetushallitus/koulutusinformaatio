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

/**
 * @author Hannu Lyytikainen
 */
public class AdditionalProof {

    private I18nText descreption;
    private ScoreLimit scoreLimit;

    public I18nText getDescreption() {
        return descreption;
    }

    public void setDescreption(I18nText descreption) {
        this.descreption = descreption;
    }

    public ScoreLimit getScoreLimit() {
        return scoreLimit;
    }

    public void setScoreLimit(ScoreLimit scoreLimit) {
        this.scoreLimit = scoreLimit;
    }
}
