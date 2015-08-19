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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOptionAttachment;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;

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
            dto.setUsedInApplicationForm(aoa.isUsedInApplicationForm());
            dto.setDescreption(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getDescreption(), lang));
            dto.setAddress(AddressToDTO.convert(aoa.getAddress(), lang));
            dto.setEmailAddr(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getEmailAddr(), lang));
            return dto;
        }
        return null;
    }

    public static List<ApplicationOptionAttachmentDTO> convertAll(final List<ApplicationOptionAttachment> aoas, final String lang, boolean isVaadinAo) {
        if (aoas != null && !aoas.isEmpty() && lang != null && !lang.isEmpty()) {
            List<ApplicationOptionAttachmentDTO> aoaDTOs = new ArrayList<ApplicationOptionAttachmentDTO>();
            for (ApplicationOptionAttachment curAttachment : aoas) {
                
                if (curAttachment != null 
                        && curAttachment.getType() != null) {
                    aoaDTOs.add(convert(curAttachment, lang));
                } else if (curAttachment != null && (curAttachment.getType() == null || isVaadinAo)) {
                    
                   aoaDTOs.add(convert(curAttachment, lang)); 
                }
            }
            return aoaDTOs;
        } else if (aoas != null && !aoas.isEmpty()) {
        
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