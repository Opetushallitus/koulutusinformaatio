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

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    public static final String STATE_PUBLISHED = "JULKAISTU";
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";






    protected String resolveLOSId(String komoId, String providerId) {
        return Joiner.on("_").join(komoId, providerId);
    }

    protected void validateParentKomo(KomoDTO komo) throws TarjontaParseException {
        // parent check
        if (!komo.getModuuliTyyppi().equals(LearningOpportunityBuilder.MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("Komo not of type " + LearningOpportunityBuilder.MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Parent komo state not " + TarjontaTila.JULKAISTU.toString());
        }
    }

    protected void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komo " + komo.getOid() + " not in state " + TarjontaTila.JULKAISTU.toString());
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
            throw new TarjontaParseException("Child komoto " + komoto.getOid() + " not in state " + TarjontaTila.JULKAISTU.toString());
        }

    }

    protected void validateHakukohde(HakukohdeDTO hakukohde) throws TarjontaParseException {
        if (!hakukohde.getTila().equals(LearningOpportunityBuilder.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application option " + hakukohde.getOid() + " not in state " + LearningOpportunityBuilder.STATE_PUBLISHED);
        }
    }

    protected void validateHaku(HakuDTO haku) throws TarjontaParseException {
        if (!haku.getTila().equals(LearningOpportunityBuilder.STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application system " + haku.getOid() + " not in state " + LearningOpportunityBuilder.STATE_PUBLISHED);
        }
    }

    public abstract LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public abstract LearningOpportunityBuilder filter();
    public abstract List<T> build();
}