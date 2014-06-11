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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
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
public class SingleUpperSecondaryLOSBuilder {

    private LOSObjectCreator losObjectCreator;
    private TarjontaRawService tarjontaRawService;

    public SingleUpperSecondaryLOSBuilder(LOSObjectCreator losObjectCreator, TarjontaRawService tarjontaRawService) {
        this.losObjectCreator = losObjectCreator;
        this.tarjontaRawService = tarjontaRawService;
    }

    public UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, String providerId) throws TarjontaParseException, KoodistoException {

        KomoDTO parentKomo = tarjontaRawService.getKomo(komo.getYlaModuulit().get(0));
        if (!CreatorUtil.komoPublished.apply(parentKomo)) {
            throw new TarjontaParseException(String.format("Parent komo not published: %s", parentKomo.getOid()));
        }

        if (!CreatorUtil.komoPublished.apply(komo)) {
            throw new TarjontaParseException(String.format("Child komo not published: %s", komo.getOid()));
        }
        List<KomotoDTO> komotos = new ArrayList<KomotoDTO>();
        List<OidRDTO> komotoOids = tarjontaRawService.getKomotosByKomo(komo.getOid(), Integer.MAX_VALUE, 0);
        for (OidRDTO komotoOid : komotoOids) {
            KomotoDTO komoto = tarjontaRawService.getKomoto(komotoOid.getOid());
            if (komoto.getTarjoajaOid().equals(providerId) && SingleParentLOSBuilder.isNuortenKoulutus(komoto)) {
                komotos.add(komoto);
            }

        }


        UpperSecondaryLOS los = losObjectCreator.createUpperSecondaryLOS(komo, parentKomo, komotos,
                String.format("%s_%s", komo.getOid(), providerId), providerId);



        for (UpperSecondaryLOI loi : los.getLois()) {
            for (ApplicationOption ao : loi.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setEducationDegree(los.getEducationDegree());
                ao.setType(TarjontaConstants.TYPE_UPSEC);
                los.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
            }
        }

        return isValid(los) ? los : null;
    }
    
    private boolean isValid(UpperSecondaryLOS los) {
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

}
