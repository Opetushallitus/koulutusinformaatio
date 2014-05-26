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

package fi.vm.sade.koulutusinformaatio.service.builder;

import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;

import java.util.List;

/**
 * Builds learning opportunity instances.
 *
 * @author Hannu Lyytikainen
 */
public abstract class LearningOpportunityBuilder<T extends LOS> {

    public static final Logger LOG = LoggerFactory.getLogger(LearningOpportunityBuilder.class);
    private static final String NOT_IN_STATE = " not in state ";


    protected String resolveLOSId(String komoId, String providerId) {
        return Joiner.on("_").join(komoId, providerId);
    }

    protected void validateParentKomo(KomoDTO komo) throws TarjontaParseException {
        // parent check
        if (!komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("Komo not of type " + TarjontaConstants.MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Parent komo state not " + TarjontaTila.JULKAISTU.toString());
        }
    }

    protected void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komo " + komo.getOid() + NOT_IN_STATE + TarjontaTila.JULKAISTU.toString());
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("Child KomoDTO koulutusOhjelmaKoodiUri (name) is null");
        }
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("Child KomoDTO tutkinto nimike uri is null");
        }
    }

    protected void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komoto " + komoto.getOid() + NOT_IN_STATE + TarjontaTila.JULKAISTU.toString());
        }

    }

    protected void validateHakukohde(HakukohdeDTO hakukohde) throws TarjontaParseException {
        if (!hakukohde.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application option " + hakukohde.getOid() + NOT_IN_STATE + TarjontaConstants.STATE_PUBLISHED);
        }
    }

    protected void validateHaku(HakuDTO haku) throws TarjontaParseException {
        if (!haku.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application system " + haku.getOid() + NOT_IN_STATE + TarjontaConstants.STATE_PUBLISHED);
        }
    }
    
    
    protected boolean isNuortenKoulutus(KomotoDTO komotoDto) {
        return (komotoDto.getKoulutuslajiUris() != null 
                && !komotoDto.getKoulutuslajiUris().isEmpty() 
                && komotoDto.getKoulutuslajiUris().get(0).contains(TarjontaConstants.NUORTEN_KOULUTUS))
                || ((komotoDto.getKoulutuslajiUris() == null) || komotoDto.getKoulutuslajiUris().isEmpty());
    }

    public abstract LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder filter();
    public abstract List<T> build();
}