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
public class UpperSecondaryLOS extends BasicLOS<UpperSecondaryLOI> {

    private List<UpperSecondaryLOI> lois;
    private I18nText qualification;
    private I18nText degreeTitle;				// tutkintonimike
    private List<I18nText> degreeTitles;		// tutkintonimikkeet

    public List<UpperSecondaryLOI> getLois() {
        return lois;
    }

    public void setLois(List<UpperSecondaryLOI> lois) {
        this.lois = lois;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

	public I18nText getDegreeTitle() {
		return degreeTitle;
	}

	public void setDegreeTitle(I18nText degreeTitle) {
		this.degreeTitle = degreeTitle;
	}

	public List<I18nText> getDegreeTitles() {
		return degreeTitles;
	}

	public void setDegreeTitles(List<I18nText> degreeTitles) {
		this.degreeTitles = degreeTitles;
	}
}