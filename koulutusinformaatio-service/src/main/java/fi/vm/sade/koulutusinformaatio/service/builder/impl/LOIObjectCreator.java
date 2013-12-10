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
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LOIObjectCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(LOIObjectCreator.class);

    KoodistoService koodistoService;
    TarjontaRawService tarjontaRawService;
    ApplicationOptionCreator applicationOptionCreator;

    public LOIObjectCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.tarjontaRawService = tarjontaRawService;
        this.applicationOptionCreator = new ApplicationOptionCreator(koodistoService, tarjontaRawService);
    }

    public ChildLOI createChildLOI(KomotoDTO childKomoto, String losId, I18nText losName) throws KoodistoException {
        ChildLOI childLOI = new ChildLOI();
        childLOI.setName(losName);
        childLOI.setId(childKomoto.getOid());
        childLOI.setLosId(losId);
        childLOI.setParentLOIId(childKomoto.getParentKomotoOid());
        childLOI.setStartDate(childKomoto.getKoulutuksenAlkamisDate());
        childLOI.setFormOfEducation(koodistoService.searchMultiple(childKomoto.getKoulutuslajiUris()));
        childLOI.setWebLinks(childKomoto.getWebLinkkis());
        childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
        childLOI.setFormOfTeaching(koodistoService.searchMultiple(childKomoto.getOpetusmuodotUris()));
        childLOI.setPrerequisite(koodistoService.searchFirstCode(childKomoto.getPohjakoulutusVaatimusUri()));
        childLOI.setProfessionalTitles(koodistoService.searchMultiple(childKomoto.getAmmattinimikeUris()));
        childLOI.setWorkingLifePlacement(getI18nText(childKomoto.getSijoittuminenTyoelamaan()));
        childLOI.setInternationalization(getI18nText(childKomoto.getKansainvalistyminen()));
        childLOI.setCooperation(getI18nText(childKomoto.getYhteistyoMuidenToimijoidenKanssa()));
        childLOI.setContent(getI18nText(childKomoto.getSisalto()));
        childLOI.setPlannedDuration(childKomoto.getLaajuusArvo());
        childLOI.setPlannedDurationUnit(koodistoService.searchFirst(childKomoto.getLaajuusYksikkoUri()));
        childLOI.setPduCodeUri(childKomoto.getLaajuusYksikkoUri());

        if (childKomoto.getYhteyshenkilos() != null) {
            for (YhteyshenkiloRDTO yhteyshenkiloRDTO : childKomoto.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getEmail(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                childLOI.getContactPersons().add(contactPerson);
            }
        }

        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        boolean kaksoistutkinto = false;
        List<OidRDTO> aoIdDTOs = tarjontaRawService.getHakukohdesByKomoto(childKomoto.getOid());
        for (OidRDTO aoIdDTO : aoIdDTOs) {
            LOG.debug(Joiner.on(" ").join("Adding application options (",
                    aoIdDTOs.size(), ") to child learning opportunity"));

            // application option
            String aoId = aoIdDTO.getOid();
            HakukohdeDTO hakukohdeDTO = tarjontaRawService.getHakukohde(aoId);
            HakuDTO hakuDTO = tarjontaRawService.getHakuByHakukohde(aoId);

            if (!CreatorUtil.hakukohdePublished.apply(hakukohdeDTO)) {
                LOG.debug(String.format("Application option %s skipped due to incorrect state", hakukohdeDTO.getOid()));
                continue;
            }

            if (!CreatorUtil.hakuPublished.apply(hakuDTO)) {
                LOG.debug(String.format("Application option %s skipped due to incorrect state of application system %s",
                        hakukohdeDTO.getOid(), hakuDTO.getOid()));
                continue;
            }

            applicationOptions.add(
                    applicationOptionCreator.createVocationalApplicationOption(hakukohdeDTO, hakuDTO, childKomoto, childLOI.getPrerequisite()));
            if (hakukohdeDTO.isKaksoisTutkinto()) {
                kaksoistutkinto = true;
            }
        }

        childLOI.setApplicationOptions(applicationOptions);
        childLOI.setKaksoistutkinto(kaksoistutkinto);

        return childLOI;
    }
}
