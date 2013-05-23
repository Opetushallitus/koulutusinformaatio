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
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionToDTO {

    public static ApplicationOptionDTO convert(final ApplicationOption applicationOption, final String lang) {
        if (applicationOption != null) {
            ApplicationOptionDTO ao = new ApplicationOptionDTO();
            ao.setId(applicationOption.getId());
            ao.setApplicationSystemId(applicationOption.getApplicationSystemId());
            ao.setName(ConverterUtil.getTextByLanguage(applicationOption.getName(), lang));
            ao.setAttachmentDeliveryDeadline(applicationOption.getAttachmentDeliveryDeadline());
            ao.setLastYearApplicantCount(applicationOption.getLastYearApplicantCount());
            ao.setLowestAcceptedAverage(applicationOption.getLowestAcceptedAverage());
            ao.setLowestAcceptedScore(applicationOption.getLowestAcceptedScore());
            ao.setStartingQuota(applicationOption.getStartingQuota());
            ao.setSora(applicationOption.isSora());
            return ao;
        }
        return null;
    }
}
