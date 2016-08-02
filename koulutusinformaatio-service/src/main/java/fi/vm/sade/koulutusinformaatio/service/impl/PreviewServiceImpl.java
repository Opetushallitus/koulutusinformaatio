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

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.*;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Markus
 */
@Service
public class PreviewServiceImpl implements PreviewService {

    private TarjontaService tarjontaService;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewServiceImpl.class);

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
        } catch (TarjontaParseException | KoodistoException | OrganisaatioException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        } catch (NoValidApplicationOptionsException e) {
            LOG.error("preview failed due to missing hakukohdes. This should no happen.");
            throw new ResourceNotFoundException("Resource: " + oid + " not found");

        }
    }

    @Override
    public CompetenceBasedQualificationParentLOS previewAdultVocationaParentLearningOpportunity(String oid)
            throws ResourceNotFoundException {
        try {
            return this.tarjontaService.createCBQPLOS(oid, false);
        } catch (TarjontaParseException | KoodistoException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        }
    }
    
    @Override
    public KoulutusLOS previewKoulutusLearningOpportunity(String oid) throws ResourceNotFoundException {
        try {
            KoulutusLOS los = this.tarjontaService.createKoulutusLOS(oid, false);
            if (SolrConstants.ED_TYPE_AMMATILLINEN.equals(los.getEducationType())) {
                TutkintoLOS tutkinto = new TutkintoLOS();
                tutkinto.setName(los.getEducationCode().getName());
                los.setTutkinto(tutkinto);
            }
            return los;
        } catch (KIException e) {
            LOG.warn("Resource: " + oid + " not found", e);
            throw new ResourceNotFoundException("Resource: " + oid + " not found");
        }
    }

}
