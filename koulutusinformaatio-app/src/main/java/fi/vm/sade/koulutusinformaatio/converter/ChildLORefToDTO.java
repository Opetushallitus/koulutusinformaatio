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

import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLORef;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLORefDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ChildLORefToDTO {

    public static List<ChildLORefDTO> convert(final List<ChildLORef> refs) {
        List<ChildLORefDTO> children = new ArrayList<ChildLORefDTO>();
        if (refs != null) {
            for (ChildLORef ref : refs) {
                ChildLORefDTO child = convert(ref);
                children.add(child);
            }
        }
        return children;
    }

    public static ChildLORefDTO convert(final ChildLORef ref) {
        ChildLORefDTO child = new ChildLORefDTO();
        child.setLosId(ref.getLosId());
        child.setLoiId(ref.getLoiId());
        child.setAsId(ref.getAsId());
        child.setName(ref.getName());
        return child;
    }
}
