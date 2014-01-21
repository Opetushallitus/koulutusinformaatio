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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.dto.ContactPersonDTO;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public final class ContactPersonToDTO {

    private ContactPersonToDTO() {
    }

    public static ContactPersonDTO convert(final ContactPerson contactPerson) {
        if (contactPerson == null) {
            return null;
        } else {
            ContactPersonDTO cp = new ContactPersonDTO();
            cp.setFirstNames(contactPerson.getFirstNames());
            cp.setLastName(contactPerson.getLastName());
            cp.setEmail(contactPerson.getEmail());
            cp.setPhone(contactPerson.getPhone());
            cp.setTitle(contactPerson.getTitle());
            return cp;
        }
    }

    public static List<ContactPersonDTO> convertAll(List<ContactPerson> contactPersonList) {
        if (contactPersonList != null) {
            return Lists.transform(contactPersonList, new Function<ContactPerson, ContactPersonDTO>() {
                @Override
                public ContactPersonDTO apply(ContactPerson contactPerson) {
                    return convert(contactPerson);
                }
            });
        } else {
            return null;
        }
    }
}
