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

package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ApplicationSystem {

    private String id;
    private I18nText name;
    private List<DateRange> applicationDates = new ArrayList<DateRange>();
	private String status;
	private int maxApplications;
	

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public List<DateRange> getApplicationDates() {
        return applicationDates;
    }

    public void setApplicationDates(List<DateRange> applicationDates) {
        this.applicationDates = applicationDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationSystem)) return false;

        ApplicationSystem that = (ApplicationSystem) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

	public void setStatus(String tila) {
		this.status = tila;	
	}

    public String getStatus() {
		return status;
	}

	public int getMaxApplications() {
		return maxApplications;
	}

	public void setMaxApplications(int maxApplications) {
		this.maxApplications = maxApplications;
	}
}
