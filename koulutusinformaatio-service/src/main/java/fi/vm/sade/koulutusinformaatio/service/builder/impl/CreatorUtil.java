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

import javax.validation.constraints.NotNull;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * @author Hannu Lyytikainen
 */
public final class CreatorUtil {

    private CreatorUtil() {
    }

    protected static Predicate<KomoDTO> komoPublished = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(@NotNull KomoDTO komo) {
            Preconditions.checkNotNull(komo);
            return komo.getTila().equals(TarjontaTila.JULKAISTU);
        }
    };

    protected static Predicate<KomotoDTO> komotoPublished = new Predicate<KomotoDTO>() {
        @Override
        public boolean apply(@NotNull KomotoDTO komoto) {
            Preconditions.checkNotNull(komoto);
            return komoto.getTila().equals(TarjontaTila.JULKAISTU);
        }
    };

    protected static Predicate<HakukohdeDTO> hakukohdePublished = new Predicate<HakukohdeDTO>() {
        @Override
        public boolean apply(@NotNull HakukohdeDTO hakukohde) {
            Preconditions.checkNotNull(hakukohde);
            return hakukohde.getTila().equals(TarjontaConstants.STATE_PUBLISHED);
        }
    };

    protected static Predicate<HakuDTO> hakuPublished = new Predicate<HakuDTO>() {
        @Override
        public boolean apply(@NotNull HakuDTO haku) {
            Preconditions.checkNotNull(haku);
            return haku.getTila().equals(TarjontaConstants.STATE_PUBLISHED);
        }
    };

    protected static Predicate<KomoDTO> komoHasKoulutusohjelmaKoodi = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(@NotNull KomoDTO komo) {
            Preconditions.checkNotNull(komo);
            return komo.getKoulutusOhjelmaKoodiUri() != null;
        }
    };

    protected static Predicate<KomoDTO> komoHasTutkintonimike = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(@NotNull KomoDTO komo) {
            Preconditions.checkNotNull(komo);
            return komo.getTutkintonimikeUri() != null;
        }
    };

    protected static Predicate<SpecialLOS> specialLOSValid = new Predicate<SpecialLOS>() {
        @Override
        public boolean apply(@NotNull SpecialLOS los) {
            Preconditions.checkNotNull(los);
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

    protected static String resolveLOSId(String komoId, String providerId) {
        return Joiner.on("_").join(komoId, providerId);
    }
}
