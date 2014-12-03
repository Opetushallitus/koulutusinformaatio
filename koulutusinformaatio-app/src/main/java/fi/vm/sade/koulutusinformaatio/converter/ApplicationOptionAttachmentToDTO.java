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
import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ApplicationOptionAttachmentToDTO {
    
    private static final String FALLBACK_LANG_DEFAULT = "fi";

    private ApplicationOptionAttachmentToDTO() {
    }

    public static ApplicationOptionAttachmentDTO convert(final ApplicationOptionAttachment aoa, final String lang) {
        if (aoa != null) {
            ApplicationOptionAttachmentDTO dto = new ApplicationOptionAttachmentDTO();
            dto.setDueDate(aoa.getDueDate());
            dto.setType(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getType(), lang));
            dto.setDescreption(ConverterUtil.getTextByLanguageUseFallbackLang(aoa.getDescreption(), lang));
            dto.setAddress(AddressToDTO.convert(aoa.getAddress(), lang));
            dto.setEmailAddr(aoa.getEmailAddr());
            return dto;
        }
        return null;
    }

    public static List<ApplicationOptionAttachmentDTO> convertAll(final List<ApplicationOptionAttachment> aoas, final String lang, boolean isVaadinAo) {
        if (aoas != null && !aoas.isEmpty() && lang != null && !lang.isEmpty()) {
            String keyLang = lang.toLowerCase();
            List<ApplicationOptionAttachmentDTO> aoaDTOs = new ArrayList<ApplicationOptionAttachmentDTO>();
            for (ApplicationOptionAttachment curAttachment : aoas) {
                
                if (curAttachment != null 
                        && curAttachment.getType() != null 
                        && curAttachment.getType().getTranslations().containsKey(keyLang)) {
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
    
    public static List<ApplicationOptionAttachmentDTO> convertAllHigherEducation(final List<ApplicationOptionAttachment> aoas, final String lang) {
        if (aoas == null) {
            return null;
        }
        else {
            
            // get attachemnts with requested language
            List<ApplicationOptionAttachmentDTO> convertedAttachments = getApplicationOptionsByLang(aoas, lang);
            if (convertedAttachments == null || convertedAttachments.isEmpty() ) {
                // fallback to language fi
                convertedAttachments = getApplicationOptionsByLang(aoas, FALLBACK_LANG_DEFAULT);
                if (convertedAttachments == null || convertedAttachments.isEmpty() ) {
                    // fallback to any existing language
                    convertedAttachments = getApplicationOptionsByLang(aoas, aoas.get(0).getType().getTranslations().keySet().iterator().next());
                }
            }
            
            return !convertedAttachments.isEmpty() ? convertedAttachments : null;
        }
    }
    
    /**
     * Returns attachemnts with specified language 
     * 
     * @param aoas
     * @param lang
     * @return
     */
    private static List<ApplicationOptionAttachmentDTO> getApplicationOptionsByLang(final List<ApplicationOptionAttachment> aoas, final String lang) {
        if (aoas == null) {
            return null;
        } else {

            List<ApplicationOptionAttachmentDTO> convertedAttachments = new ArrayList<ApplicationOptionAttachmentDTO>();
            String keyLang = lang.toLowerCase();
            
            for (ApplicationOptionAttachment curAttachment : aoas) {
                ApplicationOptionAttachmentDTO attachment = null;
                if (curAttachment != null
                        && curAttachment.getType() != null
                        && curAttachment.getType().getTranslations().containsKey(keyLang) ) {
                    attachment = convert(curAttachment, lang);
                }
                
                if (attachment != null) {
                    convertedAttachments.add(attachment);
                }
            }
            
            return convertedAttachments;
        }
    }
}