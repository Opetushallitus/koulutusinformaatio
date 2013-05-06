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
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaServiceImpl implements TarjontaService {

    private KomoResource komoResource;
    private HakukohdeResource hakukohdeResource;
    private ConversionService conversionService;

    @Autowired
    public TarjontaServiceImpl(KomoResource komoResource, HakukohdeResource aoResource, ConversionService conversionService) {
        this.komoResource = komoResource;
        this.hakukohdeResource = aoResource;
        this.conversionService = conversionService;
    }

    @Override
    public LearningOpportunity findLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public ParentLOS findParentLearningOpportunity(String oid) {
        KomoDTO parentKomo = komoResource.getByOID(oid);
        return conversionService.convert(parentKomo, ParentLOS.class);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return komoResource.search(null, 0, 0, null, null);
    }

    @Override
    public List<String> listApplicationOptionOids() {
        return hakukohdeResource.search(null, 0, 0, null, null);
    }

}
