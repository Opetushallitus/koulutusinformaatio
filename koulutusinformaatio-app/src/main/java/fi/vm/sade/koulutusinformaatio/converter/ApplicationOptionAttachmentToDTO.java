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
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOptionAttachment;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ApplicationOptionAttachmentToDTO {

    private ApplicationOptionAttachmentToDTO() {
    }

    public static ApplicationOptionAttachmentDTO convert(final ApplicationOptionAttachment aoa, final String lang) {
        if (aoa != null) {
            ApplicationOptionAttachmentDTO dto = new ApplicationOptionAttachmentDTO();
            dto.setDueDate(aoa.getDueDate());
            dto.setType(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getType(), lang));
            dto.setDescreption(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getDescreption(), lang));
            dto.setAddress(AddressToDTO.convert(aoa.getAddress(), lang));
            return dto;
        }
        return null;
    }

    public static List<ApplicationOptionAttachmentDTO> convertAll(final List<ApplicationOptionAttachment> aoas, final String lang) {
        if (aoas != null) {
            return Lists.transform(aoas, new Function<ApplicationOptionAttachment, ApplicationOptionAttachmentDTO>() {
                @Override
                public ApplicationOptionAttachmentDTO apply(ApplicationOptionAttachment input) {
                    return convert(input, lang);
                }
            });
        }
        return null;
    }
}