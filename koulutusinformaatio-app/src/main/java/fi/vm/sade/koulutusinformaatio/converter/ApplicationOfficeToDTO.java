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

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOffice;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class ApplicationOfficeToDTO {

    private ApplicationOfficeToDTO() {
    }

    public static ApplicationOfficeDTO convert(Provider provider, String lang) {
        return convert(null, provider, lang);
    }
    
    public static ApplicationOfficeDTO convert(ApplicationOffice aoFromApplicationOption, Provider provider, String lang) {
        ApplicationOffice ao = null;
        if(aoFromApplicationOption != null){
            ao = aoFromApplicationOption;
        } else if(provider.getApplicationOffice() != null){
            ao = provider.getApplicationOffice();
        } else {
           ao = new ApplicationOffice(); 
        }
        ApplicationOfficeDTO dto = new ApplicationOfficeDTO();
        if (ao.getName() != null) {
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getName(), lang));
        } else {
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), lang));
        }
        if (ao.getVisitingAddress() != null && !emptyAddress(ao.getVisitingAddress())) {
            dto.setVisitingAddress(AddressToDTO.convert(ao.getVisitingAddress(), lang));
        } else {
            dto.setVisitingAddress(AddressToDTO.convert(provider.getVisitingAddress(), lang));
        }
        if (ao.getPostalAddress() != null && !emptyAddress(ao.getPostalAddress())) {
            dto.setPostalAddress(AddressToDTO.convert(ao.getPostalAddress(), lang));
        } else {
            dto.setPostalAddress(AddressToDTO.convert(provider.getPostalAddress(), lang));
        }
        if (ao.getPhone() != null) {
            dto.setPhone(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getPhone(), lang));
        } else {
            dto.setPhone(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getPhone(), lang));
        }
        if (ao.getEmail() != null) {
            dto.setEmail(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getEmail(), lang));
        } else {
            dto.setEmail(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getEmail(), lang));
        } 
        if (ao.getWww() != null) {
            dto.setWww(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getWww(), lang));
        } else {
            dto.setWww(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getWebPage(), lang));
        }
        if (!isEmpty(dto)) {
            return dto;
        }
        return null;
    }
    
    private static boolean emptyAddress(Address address) {
        return address.getPostalCode() == null && emptyI18nText(address.getPostOffice())  &&  emptyI18nText(address.getStreetAddress()) && emptyI18nText(address.getSecondForeignAddr()); 
    }
    
    private static boolean emptyI18nText(I18nText text) {
        return text == null || text.getTranslations() == null || text.getTranslations().isEmpty();
    }

    private static boolean isEmpty(ApplicationOfficeDTO dto) {
        return dto.getEmail() == null && dto.getPhone() == null && dto.getPostalAddress() == null && dto.getVisitingAddress() == null && dto.getWww() == null;
    }
}
