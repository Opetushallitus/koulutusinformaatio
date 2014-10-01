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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.CreatorUtil;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

/**
 * 
 * @author Markus
 *
 */
public class SingleSpecialLOSBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(SingleSpecialLOSBuilder.class);

    private LOSObjectCreator losObjectCreator;
    private TarjontaRawService tarjontaRawService;


    public SingleSpecialLOSBuilder(LOSObjectCreator losObjectCreator, TarjontaRawService tarjontaRawService) {
        this.losObjectCreator = losObjectCreator;
        this.tarjontaRawService = tarjontaRawService;
    }

    public SpecialLOS createSpecialLOS(KomoDTO komo, KomotoDTO komoto, boolean isRehab, String providerId) throws TarjontaParseException, KoodistoException {

        if (isRehab) {

            return createRehabLos(komo, komoto);

        } else {

            return createSpecialVocationalLos(komo, providerId);

        }
    }

    private SpecialLOS createSpecialVocationalLos(KomoDTO komo, String providerId) throws TarjontaParseException, KoodistoException {

        KomoDTO parentKomo = tarjontaRawService.getKomo(komo.getYlaModuulit().get(0));
        if (!CreatorUtil.komoPublished.apply(parentKomo)) {
            throw new TarjontaParseException(String.format("Parent komo not published: %s", parentKomo.getOid()));
        }

        List<KomotoDTO> childKomotos = new ArrayList<KomotoDTO>();
        List<OidRDTO> childKomotoOids = tarjontaRawService.getKomotosByKomo(komo.getOid(), Integer.MAX_VALUE, 0);
        for (OidRDTO childKomotoOid : childKomotoOids) {
            KomotoDTO childKomoto = tarjontaRawService.getKomoto(childKomotoOid.getOid());

            if (SingleParentLOSBuilder.isSpecialEdKomoto(childKomoto) && childKomoto.getTarjoajaOid().equals(providerId) && SingleParentLOSBuilder.isNuortenKoulutus(childKomoto)) {
                // ER
                LOG.warn("Here is a special ed komoto, it should definitely be here.");
                childKomotos.add(childKomoto);

            }
        }

        String specialChildLOSId = String.format("%s_%s_er", komo.getOid(), providerId);


        SpecialLOS newSpecial = losObjectCreator.createSpecialLOS(komo, parentKomo, specialChildLOSId,
                childKomotos, providerId);

        for (ChildLOI curChild : newSpecial.getLois()) {
            for (ApplicationOption curAo : curChild.getApplicationOptions()) {
                curAo.setType(TarjontaConstants.TYPE_SPECIAL);
                curAo.setParent(new ParentLOSRef(newSpecial.getId(), newSpecial.getName()));
            }
        }

        return this.specialLosIsValid(newSpecial) ? newSpecial : null;
    }

    private SpecialLOS createRehabLos(KomoDTO komo, KomotoDTO komoto) throws TarjontaParseException, KoodistoException {
        if (!CreatorUtil.komoPublished.apply(komo)) {
            throw new TarjontaParseException(String.format("Child komo not published: %s", komo.getOid()));
        }
        KomoDTO parentKomo = tarjontaRawService.getKomo(komo.getYlaModuulit().get(0));
        if (!CreatorUtil.komoPublished.apply(parentKomo)) {
            throw new TarjontaParseException(String.format("Parent komo not published: %s", parentKomo.getOid()));
        }

        String rehabId = String.format("%s_%s_%s", komo.getOid(), komoto.getTarjoajaOid(), komoto.getOid());
        SpecialLOS newSpecial = losObjectCreator.createRehabLOS(komo, parentKomo,
                rehabId, komoto, komoto.getTarjoajaOid());


        for (ChildLOI loi : newSpecial.getLois()) {
            for (ApplicationOption ao : loi.getApplicationOptions()) {
                ao.setProvider(newSpecial.getProvider());
                ao.setEducationDegree(newSpecial.getEducationDegree());
                ao.setType(newSpecial.getType());
                //ao.setEducationTypeUri();
                ao.setParent(new ParentLOSRef(newSpecial.getId(), newSpecial.getName()));
                newSpecial.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
            }
        }


        return this.specialLosIsValid(newSpecial) ? newSpecial : null;

    }

    private boolean specialLosIsValid(SpecialLOS los) {
        if (los != null 
                && los.getLois() != null) {
            for (ChildLOI loi : los.getLois()) {
                if (loi.getApplicationOptions() != null && loi.getApplicationOptions().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
