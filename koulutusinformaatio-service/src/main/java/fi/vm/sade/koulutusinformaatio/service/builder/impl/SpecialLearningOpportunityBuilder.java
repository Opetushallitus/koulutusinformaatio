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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class SpecialLearningOpportunityBuilder extends LearningOpportunityBuilder<SpecialLOS> {

    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator losObjectCreator;

    // variables

    private KomoDTO komo;
    private KomoDTO parentKomo;

    private List<SpecialLOS> loses;

    // A helper data structure that groups KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> komotosByProviderId;

    public SpecialLearningOpportunityBuilder(TarjontaRawService tarjontaRawService,
                                                    ProviderService providerService,
                                                    KoodistoService koodistoService, KomoDTO komo) {
        this.tarjontaRawService = tarjontaRawService;
        this.komo = komo;
        this.komotosByProviderId = ArrayListMultimap.create();
        this.loses = Lists.newArrayList();
        this.losObjectCreator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
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
            komotosByProviderId.put(komoto.getTarjoajaOid(), komoto);
        }

        for (String providerId : komotosByProviderId.keySet()) {
            SpecialLOS los = losObjectCreator.createSpecialLOS(komo, parentKomo,
                    resolveLOSId(komo.getOid(), providerId), komotosByProviderId.get(providerId), providerId);
            loses.add(los);
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (SpecialLOS los : loses) {
            for (ChildLOI loi : los.getLois()) {
                for (ApplicationOption ao : loi.getApplicationOptions()) {
                    ao.setProvider(los.getProvider());
                    ao.setEducationDegree(los.getEducationDegree());
                    los.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
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
    public List<SpecialLOS> build() {
        return this.loses;
    }

    private static Predicate<SpecialLOS> losValid = new Predicate<SpecialLOS>() {
        @Override
        public boolean apply(SpecialLOS los) {
            if (los.getLois() != null) {
                for (ChildLOI loi : los.getLois()) {
                    if (loi.getApplicationOptions() != null && loi.getApplicationOptions().size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    };



}
