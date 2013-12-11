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

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.builder.BuilderConstants;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * @author Hannu Lyytikainen
 */
public class CreatorUtil {


    protected static Predicate<KomoDTO> komoPublished = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(KomoDTO komo) {
            return komo.getTila().equals(TarjontaTila.JULKAISTU);
        }
    };

    protected static Predicate<KomotoDTO> komotoPublished = new Predicate<KomotoDTO>() {
        @Override
        public boolean apply(KomotoDTO komoto) {
            return komoto.getTila().equals(TarjontaTila.JULKAISTU);
        }
    };

    protected static Predicate<HakukohdeDTO> hakukohdePublished = new Predicate<HakukohdeDTO>() {
        @Override
        public boolean apply(HakukohdeDTO hakukohde) {
            return hakukohde.getTila().equals(BuilderConstants.STATE_PUBLISHED);
        }
    };

    protected static Predicate<HakuDTO> hakuPublished = new Predicate<HakuDTO>() {
        @Override
        public boolean apply(HakuDTO haku) {
            return haku.getTila().equals(BuilderConstants.STATE_PUBLISHED);
        }
    };

    protected static Predicate<KomoDTO> komoHasKoulutusohjelmaKoodi = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(KomoDTO komo) {
            return komo.getKoulutusOhjelmaKoodiUri() != null;
        }
    };

    protected static Predicate<KomoDTO> komoHasTutkintonimike = new Predicate<KomoDTO>() {
        @Override
        public boolean apply(KomoDTO komo) {
            return komo.getTutkintonimikeUri() != null;
        }
    };

    protected static Predicate<SpecialLOS> specialLOSValid = new Predicate<SpecialLOS>() {
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

    protected static String resolveLOSId(String komoId, String providerId) {
        return Joiner.on("_").join(komoId, providerId);
    }

    @Deprecated
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

    @Deprecated
    protected void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komoto " + komoto.getOid() + " not in state " + TarjontaTila.JULKAISTU.toString());
        }

    }


}
