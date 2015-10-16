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

import java.math.BigDecimal;

/**
 * @author Hannu Lyytikainen
 */
public class ScoreLimit {

    private double lowestScore;
    private double lowestAcceptedScore;
    private double highestScore;

    /**
     * Null safe constructor
     * */
    public ScoreLimit(BigDecimal alinPistemaara, BigDecimal alinHyvaksyttyPistemaara, BigDecimal ylinPistemaara) {
        this.lowestScore = alinPistemaara != null ? alinPistemaara.doubleValue() : 0;
        this.lowestAcceptedScore = alinHyvaksyttyPistemaara != null ? alinHyvaksyttyPistemaara.doubleValue() : 0;
        this.highestScore = ylinPistemaara != null ? ylinPistemaara.doubleValue() : 0;
    }

    public ScoreLimit() {
    }

    public double getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(double lowestScore) {
        this.lowestScore = lowestScore;
    }

    public double getLowestAcceptedScore() {
        return lowestAcceptedScore;
    }

    public void setLowestAcceptedScore(double lowestAcceptedScore) {
        this.lowestAcceptedScore = lowestAcceptedScore;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }
}
