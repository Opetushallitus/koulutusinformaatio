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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOffice;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class ApplicationOfficeToDTO {

    private ApplicationOfficeToDTO() {
    }

    public static ApplicationOfficeDTO convert(Provider provider, String lang) {
        ApplicationOffice applicationOffice = provider.getApplicationOffice() != null ? provider.getApplicationOffice() : new ApplicationOffice();
        ApplicationOfficeDTO dto = new ApplicationOfficeDTO();
        if (applicationOffice.getName() != null) {
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOffice.getName(), lang));
        } else {
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), lang));
        }
        if (applicationOffice.getVisitingAddress() != null) {
            dto.setVisitingAddress(AddressToDTO.convert(applicationOffice.getVisitingAddress(), lang));
        } else {
            dto.setVisitingAddress(AddressToDTO.convert(provider.getVisitingAddress(), lang));
        }
        if (applicationOffice.getPostalAddress() != null) {
            dto.setPostalAddress(AddressToDTO.convert(applicationOffice.getPostalAddress(), lang));
        } else {
            dto.setPostalAddress(AddressToDTO.convert(provider.getPostalAddress(), lang));
        }
        if (applicationOffice.getPhone() != null) {
            dto.setPhone(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOffice.getPhone(), lang));
        } else {
            dto.setPhone(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getPhone(), lang));
        }
        if (applicationOffice.getEmail() != null) {
            dto.setEmail(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOffice.getEmail(), lang));
        } else {
            dto.setEmail(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getEmail(), lang));
        } 
        if (applicationOffice.getWww() != null) {
            dto.setWww(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOffice.getWww(), lang));
        } else {
            dto.setWww(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getWebPage(), lang));
        }
        if (!isEmpty(dto)) {
            return dto;
        }
        return null;
    }
    
    private static boolean isEmpty(ApplicationOfficeDTO dto) {
        return dto.getEmail() == null && dto.getPhone() == null && dto.getPostalAddress() == null && dto.getVisitingAddress() == null && dto.getWww() == null;
    }
}
