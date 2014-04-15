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

package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.ExamEvent;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamEventDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ExamEventToDTO {

    private ExamEventToDTO() {
    }

    public static ExamEventDTO convert(final ExamEvent event, final String lang) {
        if (event != null) {
            ExamEventDTO dto = new ExamEventDTO();
            dto.setAddress(AddressToDTO.convert(event.getAddress(), lang));
            dto.setDescription(event.getDescription());
            dto.setStart(event.getStart());
            dto.setEnd(event.getEnd());
            return dto;
        } else {
            return null;
        }
    }

    public static List<ExamEventDTO> convertAll(final List<ExamEvent> events, final String lang) {
        if (events != null) {
            return Lists.transform(events, new Function<ExamEvent, ExamEventDTO>() {
                @Override
                public ExamEventDTO apply(ExamEvent input) {
                    return convert(input, lang);
                }
            });
        } else {
            return null;
        }
    }
}
