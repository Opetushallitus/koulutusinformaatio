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

package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.dto.DateRangeDTO;

/**
 * @author Mikko Majapuro
 */
public final class DateRangeToDTO {

    private DateRangeToDTO() {
    }

    public static DateRangeDTO convert(final DateRange dateRange) {
        if (dateRange != null) {
            DateRangeDTO r = new DateRangeDTO();
            r.setStartDate(dateRange.getStartDate());
            r.setEndDate(dateRange.getEndDate());
            return r;
        } else {
            return null;
        }
    }

    public static List<DateRangeDTO> convert(final List<DateRange> dateRanges) {
        if (dateRanges != null) {
            List<DateRangeDTO> ranges = new ArrayList<DateRangeDTO>();
            for (DateRange dr : dateRanges) {
                ranges.add(convert(dr));
            }
            return ranges;
        } else {
            return null;
        }
    }
}
