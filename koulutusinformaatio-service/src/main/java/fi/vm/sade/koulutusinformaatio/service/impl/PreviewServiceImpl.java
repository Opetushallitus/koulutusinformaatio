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

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Markus
 */
@Service
public class PreviewServiceImpl implements PreviewService {

    private TarjontaService tarjontaService;

    @Autowired
    public PreviewServiceImpl (TarjontaService tarjontaService) {
        this.tarjontaService = tarjontaService;
    }

    @Override
    public HigherEducationLOS previewHigherEducationLearningOpportunity(
            String oid) throws ResourceNotFoundException {
        try {
            HigherEducationLOS los = this.tarjontaService.findHigherEducationLearningOpportunity(oid);
            if (los == null) {
                throw new ResourceNotFoundException("Resource: " + oid + " not found");
            }
            return los;
        } catch (TarjontaParseException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        } catch (KoodistoException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        }
    }

    @Override
    public AdultUpperSecondaryLOS previewAdultUpperSecondaryLearningOpportunity(
            String oid) throws ResourceNotFoundException {
        
        try {
            AdultUpperSecondaryLOS los = this.tarjontaService.createAdultUpperSecondaryLOS(oid, false);
            if (los == null) {
                throw new ResourceNotFoundException("Resource: " + oid + " not found");
            }
            return los;
        } catch (TarjontaParseException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        } catch (KoodistoException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        }
    }

}
