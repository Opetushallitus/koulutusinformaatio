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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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

    public SpecialLOI createSpecialLOI(KomotoDTO komoto, String losId, I18nText losName) throws KoodistoException {
        SpecialLOI loi = new SpecialLOI();
        loi.setName(losName);
        loi.setId(komoto.getOid());
        loi.setStartDate(komoto.getKoulutuksenAlkamisDate());
        loi.setFormOfEducation(koodistoService.searchMultiple(komoto.getKoulutuslajiUris()));
        loi.setTeachingLanguages(koodistoService.searchCodesMultiple(komoto.getOpetuskieletUris()));
        loi.setFormOfTeaching(koodistoService.searchMultiple(komoto.getOpetusmuodotUris()));
        loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
        loi.setInternationalization(getI18nText(komoto.getKansainvalistyminen()));
        loi.setCooperation(getI18nText(komoto.getYhteistyoMuidenToimijoidenKanssa()));
        loi.setContent(getI18nText(komoto.getSisalto()));

        loi.setPlannedDuration(komoto.getLaajuusArvo());
        loi.setPlannedDurationUnit(koodistoService.searchFirst(komoto.getLaajuusYksikkoUri()));
        loi.setPduCodeUri(komoto.getLaajuusYksikkoUri());

        for (String d : komoto.getLukiodiplomitUris()) {
            loi.getDiplomas().add(koodistoService.searchFirst(d));
        }

        if (komoto.getYhteyshenkilos() != null) {
            for (YhteyshenkiloRDTO yhteyshenkiloRDTO : komoto.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getEmail(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                loi.getContactPersons().add(contactPerson);
            }
        }

        if (komoto.getTarjotutKielet() != null) {
            Map<String, List<String>> kielivalikoimat = komoto.getTarjotutKielet();
            List<LanguageSelection> languageSelection = Lists.newArrayList();

            for (String oppiaine : kielivalikoimat.keySet()) {
                List<I18nText> languages = Lists.newArrayList();
                for (String kieliKoodi : kielivalikoimat.get(oppiaine)) {
                    languages.add(koodistoService.searchFirst(kieliKoodi));
                }
                languageSelection.add(new LanguageSelection(oppiaine, languages));
            }
            loi.setLanguageSelection(languageSelection);
        }

        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        List<OidRDTO> aoIdDTOs = tarjontaRawService.getHakukohdesByKomoto(komoto.getOid());
        boolean kaksoistutkinto = false;
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
                    applicationOptionCreator.createVocationalApplicationOption(hakukohdeDTO, hakuDTO, komoto, loi.getPrerequisite()));

            if (hakukohdeDTO.isKaksoisTutkinto()) {
                kaksoistutkinto = true;
            }
        }
        loi.setKaksoistutkinto(kaksoistutkinto);
        loi.setApplicationOptions(applicationOptions);

        return loi;
    }
}
