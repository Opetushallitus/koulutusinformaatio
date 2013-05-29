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

import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    public static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private EducationDataService educationDataService;
    private static final int MAX_RESULTS = 100;


    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService,
                             IndexerService indexerService, EducationDataService educationDataService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataService = educationDataService;
    }

    @Override
    public void updateAllEducationData() throws Exception {
        LOG.info("Starting full education data update");
        // drop db
        // drop index
        this.educationDataService.dropAllData();
        this.indexerService.dropLOPs();
        this.indexerService.dropLOs();

        int count = MAX_RESULTS;
        int index = 0;

        while(count >= MAX_RESULTS) {
            LOG.debug("Searching parent learning opportunity oids count: " + count + ", start index: " + index);
            List<OidRDTO> parentOids = tarjontaService.listParentLearnignOpportunityOids(count, index);
            count = parentOids.size();
            index += count;

            for (OidRDTO parentOid : parentOids) {
                ParentLOS parent = null;

                try {
                    parent = tarjontaService.findParentLearningOpportunity(parentOid.getOid());
                } catch (TarjontaParseException e) {
                    LOG.warn("Exception while updating parent learning opportunity, oid: " + parentOid + ", Message: " + e.getMessage());
                    continue;
                }
                catch (KoodistoException e) {
                    LOG.warn("Exception while updating parent learning opportunity, oid: " + parentOid + ", Message: " + e.getMessage());
                    continue;
                }

                this.indexerService.addParentLearningOpportunity(parent);
                this.educationDataService.save(parent);
            }
        }

        this.indexerService.commitLOChnages();

        LOG.info("Education data update successfully finished");
    }
}
