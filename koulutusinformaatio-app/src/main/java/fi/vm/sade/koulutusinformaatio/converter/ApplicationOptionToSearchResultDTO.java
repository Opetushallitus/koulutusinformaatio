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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionToSearchResultDTO {

    public static ApplicationOptionSearchResultDTO convert(final ApplicationOption applicationOption, final String lang) {
        if (applicationOption != null) {
            ApplicationOptionSearchResultDTO dto = new ApplicationOptionSearchResultDTO();
            dto.setId(applicationOption.getId());
            dto.setName(ConverterUtil.getTextByLanguage(applicationOption.getName(), lang));
            dto.setChildLONames(ConverterUtil.getTextsByLanguage(applicationOption.getChildLONames(), lang));
            dto.setEducationDegree(applicationOption.getEducationDegree());
            dto.setSora(applicationOption.isSora());
            return dto;
        }
        return null;
    }
}
