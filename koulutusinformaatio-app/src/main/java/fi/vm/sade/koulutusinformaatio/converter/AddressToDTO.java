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

    private AddressToDTO() {
    }

    public static AddressDTO convert(final Address address, String lang) {
        if (address != null) {
            AddressDTO addrs = new AddressDTO();
            addrs.setStreetAddress(ConverterUtil.getTextByLanguageUseFallbackLang(address.getStreetAddress(), lang));
            addrs.setStreetAddress2(ConverterUtil.getTextByLanguageUseFallbackLang(address.getStreetAddress2(), lang));
            addrs.setPostalCode(address.getPostalCode());
            addrs.setPostOffice(ConverterUtil.getTextByLanguageUseFallbackLang(address.getPostOffice(), lang));
            return addrs;
        } else {
            return null;
        }
    }
}
