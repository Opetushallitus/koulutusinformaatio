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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaServiceImpl implements TarjontaService {

    private KomoResource komoResource;

    public TarjontaServiceImpl(KomoResource komoResource) {
        this.komoResource = komoResource;
    }

    @Override
    public LearningOpportunity findLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public ParentLearningOpportunity findParentLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return komoResource.search(null, 0, 0, null, null);
    }

}
