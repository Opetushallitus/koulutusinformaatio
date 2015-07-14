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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLearningOpportunityBuilder extends LearningOpportunityBuilder<UpperSecondaryLOS> {

    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator losObjectCreator;

    private KomoDTO komo;
    private KomoDTO parentKomo;
    private List<UpperSecondaryLOS> loses;
    // A helper data structure that groups KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> komotosByProviderId;

    public UpperSecondaryLearningOpportunityBuilder(TarjontaRawService tarjontaRawService,
                                                    ProviderService providerService,
                                                    KoodistoService koodistoService, KomoDTO komo,
                                                    OrganisaatioRawService organisaatioRawService,
                                                    ParameterService parameterService) {
        this.tarjontaRawService = tarjontaRawService;
        this.komo = komo;
        komotosByProviderId = ArrayListMultimap.create();
        this.loses = Lists.newArrayList();
        this.losObjectCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService,
                organisaatioRawService, parameterService);
    }

    @Override
    public LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        parentKomo = tarjontaRawService.getKomo(komo.getYlaModuulit().get(0));
        if (!CreatorUtil.komoPublished.apply(parentKomo)) {
            throw new TarjontaParseException(String.format("Parent komo not published: %s", parentKomo.getOid()));
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        if (!CreatorUtil.komoPublished.apply(komo)) {
            throw new TarjontaParseException(String.format("Child komo not published: %s", komo.getOid()));
        }
        List<OidRDTO> komotoOids = tarjontaRawService.getKomotosByKomo(komo.getOid(), Integer.MAX_VALUE, 0);
        for (OidRDTO komotoOid : komotoOids) {
            KomotoDTO komoto = tarjontaRawService.getKomoto(komotoOid.getOid());
            
            if (isNuortenKoulutus(komoto)) {
                komotosByProviderId.put(komoto.getTarjoajaOid(), komoto);
            }
        }

        for (String providerId : komotosByProviderId.keySet()) {
            UpperSecondaryLOS los = losObjectCreator.createUpperSecondaryLOS(komo, parentKomo, komotosByProviderId.get(providerId),
                    resolveLOSId(komo.getOid(), providerId), providerId);
            loses.add(los);
        }

        return this;
    }

    @Override
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (UpperSecondaryLOS los : loses) {
            for (UpperSecondaryLOI loi : los.getLois()) {
                for (ApplicationOption ao : loi.getApplicationOptions()) {
                    ao.setProvider(los.getProvider());
                    ao.setEducationDegree(los.getEducationDegree());
                    ao.setType(TarjontaConstants.TYPE_UPSEC);
                    los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                }
            }
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder filter() {
        loses = Lists.newArrayList(Collections2.filter(loses, losValid));
        return this;
    }

    @Override
    public List<UpperSecondaryLOS> build() {
        return this.loses;
    }

    private static Predicate<UpperSecondaryLOS> losValid = new Predicate<UpperSecondaryLOS>() {
        @Override
        public boolean apply(UpperSecondaryLOS los) {
            if (los != null
                && los.getLois() != null) {
                for (UpperSecondaryLOI loi : los.getLois()) {
                    if (loi.getApplicationOptions() != null && loi.getApplicationOptions().size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

}
