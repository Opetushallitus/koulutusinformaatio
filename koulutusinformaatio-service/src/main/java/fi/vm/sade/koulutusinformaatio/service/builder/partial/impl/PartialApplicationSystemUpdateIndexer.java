/**
 * Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.builder.partial.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationOptionIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.partial.PartialUpdateIndexer;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;

/**
 * @author risal1
 *
 */
@Component
public class PartialApplicationSystemUpdateIndexer implements PartialUpdateIndexer {
    
    private static Logger LOGGER = LoggerFactory.getLogger(PartialApplicationSystemUpdateIndexer.class);
    
    @Autowired
    private TarjontaRawService tarjontaService;
    
    @Autowired
    private IncrementalApplicationSystemIndexer asIndexer; 
    
    @Autowired
    private IncrementalApplicationOptionIndexer aoIndexer;
    
    @Override
    public void update(String oid) throws Exception {        
        asIndexer.indexApplicationSystemData(oid);
        HakuV1RDTO hakuDto = tarjontaService.getV1EducationHakuByOid(oid).getResult();
        for (OidRDTO hakuKohde : tarjontaService.getHakukohdesByHaku(oid)) {
            updateApplicationOptions(hakuDto, hakuKohde.getOid());
        }        
    }

    private void updateApplicationOptions(HakuV1RDTO hakuDto, String hakuKohdeOid) throws Exception {
        LOGGER.debug("Indexing + application option: " + hakuKohdeOid);
        aoIndexer.indexApplicationOptionData(tarjontaService.getV1EducationHakukohode(hakuKohdeOid).getResult(), hakuDto);
    }

}
