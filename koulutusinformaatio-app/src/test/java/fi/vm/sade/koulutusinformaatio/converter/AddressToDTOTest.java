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
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Hannu Lyytikainen
 */
public class AddressToDTOTest {

    @Test
    public void testConvert() {
        Address address = new Address();
        address.setPostalCode("00100");
        address.setPostOffice("Helsinki");
        address.setStreetAddress("street one");
        address.setStreetAddress2("street two");

        AddressDTO dto = AddressToDTO.convert(address);
        assertNotNull(dto);
        assertEquals("00100", dto.getPostalCode());
        assertEquals("Helsinki", dto.getPostOffice());
        assertEquals("street one", dto.getStreetAddress());
        assertEquals("street two", dto.getStreetAddress2());
    }

    @Test
    public void testConvertNull() {
        assertNull(AddressToDTO.convert(null));
    }

}
