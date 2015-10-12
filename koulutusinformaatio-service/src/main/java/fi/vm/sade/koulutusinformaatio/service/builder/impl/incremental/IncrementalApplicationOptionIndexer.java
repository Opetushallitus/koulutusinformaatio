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
package fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;

/**
 *
 * @author Markus
 *
 */
@Component
public class IncrementalApplicationOptionIndexer {

    private IncrementalLOSIndexer losIndexer;

    private TarjontaService tarjontaService;

    @Autowired
    public IncrementalApplicationOptionIndexer(IncrementalLOSIndexer losIndexer, TarjontaService tarjontaService) {
        this.losIndexer = losIndexer;
        this.tarjontaService = tarjontaService;
    }

    public void indexApplicationOptionData(HakukohdeV1RDTO aoDto, HakuV1RDTO asDto) throws Exception {
        for (String koulutusOid : aoDto.getHakukohdeKoulutusOids()) {
            if (!tarjontaService.hasAlreadyProcessedOid(koulutusOid))
                losIndexer.indexLoiData(koulutusOid);
        }
    }
}
