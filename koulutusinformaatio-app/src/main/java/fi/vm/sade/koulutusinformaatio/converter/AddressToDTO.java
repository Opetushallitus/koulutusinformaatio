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

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;

/**
 * @author Mikko Majapuro
 */
public final class AddressToDTO {

    private static final String FALLBACK_LANG = "fi";

    private AddressToDTO() {
    }

    public static AddressDTO convert(final Address address, String lang) {
        AddressDTO addrs = getAddressByLang(address, lang);
        if (addrs == null)
            addrs = getAddressByLang(address, FALLBACK_LANG);
        if (addrs == null)
            addrs = getAddressByLang(address, getAnyLanguageCode(address));

        return addrs;
    }

    private static AddressDTO getAddressByLang(Address address, String lang) {
        if (addressContainsLang(address, lang)) {
            AddressDTO addr = new AddressDTO();
            addr.setStreetAddress(ConverterUtil.getTextByLanguage(address.getStreetAddress(), lang));
            addr.setStreetAddress2(ConverterUtil.getTextByLanguage(address.getSecondForeignAddr(), lang));
            addr.setPostalCode(ConverterUtil.getTextByLanguage(address.getPostalCode(), lang));
            addr.setPostOffice(ConverterUtil.getTextByLanguage(address.getPostOffice(), lang));
            return addr;
        }
        return null;
    }

    private static boolean addressContainsLang(Address address, String lang) {
        return address != null && (
                address.getPostalCode() != null && address.getPostalCode().getTranslations().containsKey(lang)
                        || address.getPostOffice() != null && address.getPostOffice().getTranslations().containsKey(lang)
                        || address.getSecondForeignAddr() != null && address.getSecondForeignAddr().getTranslations().containsKey(lang)
                        || address.getStreetAddress() != null && address.getStreetAddress().getTranslations().containsKey(lang));
    }

    private static String getAnyLanguageCode(Address address) {
        if (address != null && address.getStreetAddress() != null && address.getStreetAddress().getTranslations() != null
                && !address.getStreetAddress().getTranslations().isEmpty()) {
            return address.getStreetAddress().getTranslations().keySet().iterator().next();
        }
        return null;
    }

}
