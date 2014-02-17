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
import com.google.common.collect.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    public List<ChildLOI> createChildLOIs(List<KomotoDTO> childKomotos,
                                          String losId, I18nText losName, String educationCodeUri) throws KoodistoException {
        List<ChildLOI> childLOIs = Lists.newArrayList();
        for (KomotoDTO childKomoto : childKomotos) {
            String childKomotoOid = childKomoto.getOid();
            LOG.debug(Joiner.on(" ").join("Resolving child learning opportunity:", childKomotoOid));

            if (!CreatorUtil.komotoPublished.apply(childKomoto)) {
                LOG.debug(String.format("Skipping child non published child komoto %s", childKomoto.getOid()));
                continue;
            }

            ChildLOI childLOI = createChildLOI(childKomoto, losId, losName, educationCodeUri);
            if (!childLOI.getApplicationOptions().isEmpty()) {
                childLOIs.add(childLOI);
            }
        }
        return filter(childLOIs);
    }

    public ChildLOI createChildLOI(KomotoDTO childKomoto, String losId, I18nText losName, String educationCodeUri) throws KoodistoException {
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
        childLOI.setWorkingLifePlacement(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
        childLOI.setInternationalization(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN)));
        childLOI.setCooperation(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
        childLOI.setContent(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.SISALTO)));
        childLOI.setPlannedDuration(childKomoto.getSuunniteltuKestoArvo());
        childLOI.setPlannedDurationUnit(koodistoService.searchFirst(childKomoto.getSuunniteltuKestoYksikkoUri()));
        childLOI.setPduCodeUri(childKomoto.getLaajuusYksikkoUri());

        if (childKomoto.getYhteyshenkilos() != null) {
            for (YhteyshenkiloRDTO yhteyshenkiloRDTO : childKomoto.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getEmail(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                childLOI.getContactPersons().add(contactPerson);
            }
        }

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        Set<String> availableLanguagaes = Sets.newHashSet();
        if (childLOI.getContent() != null) {
            availableLanguagaes.addAll(childLOI.getContent().getTranslations().keySet());
        }
        if (childLOI.getInternationalization() != null) {
            availableLanguagaes.addAll(childLOI.getInternationalization().getTranslations().keySet());
        }
        if (childLOI.getCooperation() != null) {
            availableLanguagaes.addAll(childLOI.getCooperation().getTranslations().keySet());
        }
        for (Code teachingLanguage : childLOI.getTeachingLanguages()) {
            availableLanguagaes.add(teachingLanguage.getValue().toLowerCase());
        }

        childLOI.setAvailableTranslationLanguages(Lists.newArrayList(availableLanguagaes));

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
                    applicationOptionCreator.createVocationalApplicationOption(hakukohdeDTO, hakuDTO, childKomoto, childLOI.getPrerequisite(), educationCodeUri));
            if (hakukohdeDTO.isKaksoisTutkinto()) {
                kaksoistutkinto = true;
            }
        }

        childLOI.setApplicationOptions(applicationOptions);
        childLOI.setKaksoistutkinto(kaksoistutkinto);

        return childLOI;
    }

    public List<UpperSecondaryLOI> createUpperSecondaryLOIs(List<KomotoDTO> komotos, String losId, I18nText losName, String educationCodeUri) throws KoodistoException {
        List<UpperSecondaryLOI> lois = Lists.newArrayList();
        for (KomotoDTO komoto : komotos) {
            if (CreatorUtil.komotoPublished.apply(komoto)) {
                lois.add(createUpperSecondaryLOI(komoto, losId, losName, educationCodeUri));
            }
        }
        return filter(lois);
    }

    public UpperSecondaryLOI createUpperSecondaryLOI(KomotoDTO komoto, String losId, I18nText losName, String educationCodeUri) throws KoodistoException {
        UpperSecondaryLOI loi = new UpperSecondaryLOI();

        loi.setName(losName);
        loi.setId(komoto.getOid());
        loi.setStartDate(komoto.getKoulutuksenAlkamisDate());
        loi.setFormOfEducation(koodistoService.searchMultiple(komoto.getKoulutuslajiUris()));
        loi.setTeachingLanguages(koodistoService.searchCodesMultiple(komoto.getOpetuskieletUris()));
        loi.setFormOfTeaching(koodistoService.searchMultiple(komoto.getOpetusmuodotUris()));
        loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
        loi.setInternationalization(getI18nText(komoto.getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN)));
        loi.setCooperation(getI18nText(komoto.getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
        loi.setContent(getI18nText(komoto.getTekstit().get(KomotoTeksti.SISALTO)));

        loi.setPlannedDuration(komoto.getSuunniteltuKestoArvo());
        loi.setPlannedDurationUnit(koodistoService.searchFirst(komoto.getSuunniteltuKestoYksikkoUri()));
        loi.setPduCodeUri(komoto.getLaajuusYksikkoUri());

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        Set<String> availableLanguagaes = Sets.newHashSet();
        if (loi.getContent() != null) {
            availableLanguagaes.addAll(loi.getContent().getTranslations().keySet());
        }
        if (loi.getInternationalization() != null) {
            availableLanguagaes.addAll(loi.getInternationalization().getTranslations().keySet());
        }
        if (loi.getCooperation() != null) {
            availableLanguagaes.addAll(loi.getCooperation().getTranslations().keySet());
        }
        for (Code teachingLanguage : loi.getTeachingLanguages()) {
            availableLanguagaes.add(teachingLanguage.getValue().toLowerCase());
        }

        loi.setAvailableTranslationLanguages(Lists.newArrayList(availableLanguagaes));
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

            for (Map.Entry<String, List<String>> oppiaine : kielivalikoimat.entrySet()) {
                List<I18nText> languages = Lists.newArrayList();
                for (String kieliKoodi : oppiaine.getValue()) {
                    languages.add(koodistoService.searchFirst(kieliKoodi));
                }
                languageSelection.add(new LanguageSelection(oppiaine.getKey(), languages));
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
                    applicationOptionCreator.createUpperSecondaryApplicationOption(hakukohdeDTO, hakuDTO, komoto, loi, educationCodeUri));

            if (hakukohdeDTO.isKaksoisTutkinto()) {
                kaksoistutkinto = true;
            }
        }

        loi.setApplicationOptions(applicationOptions);
        loi.setKaksoistutkinto(kaksoistutkinto);

        return loi;
    }


    private <T extends LOI> List<T> filter(List<T> lois) {
        List<T> applicationOptionsFiltered = filterApplicationOptions(lois);
        return Lists.newArrayList(Collections2.filter(applicationOptionsFiltered, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return !input.getApplicationOptions().isEmpty();
            }
        }));

    }

    /**
     * Filters out old application options if newer ones are available.
     *
     * @param lois unfiltered list
     * @return filtered list
     */
    private <T extends LOI> List<T> filterApplicationOptions(List<T> lois) {
        // list application options by prerequisite
        Multimap<String, ApplicationOption> applicationOptions = HashMultimap.create();
        for (LOI loi : lois) {
            applicationOptions.putAll(loi.getPrerequisite().getValue(), loi.getApplicationOptions());
        }
        final List<ApplicationOption> filteredApplicationOptions = Lists.newArrayList();
        for (Map.Entry<String, Collection<ApplicationOption>> entry : applicationOptions.asMap().entrySet()) {
            filteredApplicationOptions.addAll(reduceApplicationOptions(new ArrayList(entry.getValue()), new ArrayList<ApplicationOption>()));
        }
        for (T loi : lois) {
            loi.setApplicationOptions(
                    Lists.newArrayList(Collections2.filter(loi.getApplicationOptions(), new Predicate<ApplicationOption>() {
                        @Override
                        public boolean apply(ApplicationOption input) {
                            return filteredApplicationOptions.contains(input);
                        }
                    }))
            );
        }

        return Lists.newArrayList(lois);
    }

    private List<ApplicationOption> reduceApplicationOptions(List<ApplicationOption> unfiltered, List<ApplicationOption> filtered) {
        if (unfiltered.isEmpty()) {
            // no more application options to process, return filtered
            return filtered;
        } else if (filtered.isEmpty()) {
            // filtered is empty, add head
            filtered.add(head(unfiltered));
            return reduceApplicationOptions(tail(unfiltered), Lists.newArrayList(filtered));
        } else {
            ApplicationOption unfilteredHead = head(unfiltered);
            ApplicationOption filteredHead = head(filtered);
            if (isCurrentOrFuture(unfilteredHead)) {
                // unfiltered head is future/current, add to filtered
                if (!isFuture(filteredHead) && !isCurrent(filteredHead)) {
                    // remove past head from filtered list
                    filtered.remove(0);
                }
                filtered.add(unfilteredHead);
                return reduceApplicationOptions(tail(unfiltered), Lists.newArrayList(filtered));
            } else {
                // unfiltered head is in the past
                if (isCurrentOrFuture(filteredHead)) {
                    // if filtered head is current/future -> pass
                    return reduceApplicationOptions(tail(unfiltered), Lists.newArrayList(filtered));
                } else {
                    // filtered and unfiltered heads are in the past -> compare and replace if needed
                    if (isAfter(unfilteredHead, filteredHead)) {
                        // unfiltered head is later -> replace
                        return reduceApplicationOptions(tail(unfiltered), Lists.newArrayList(unfilteredHead));
                    } else {
                        // filtered head is the latest
                        return reduceApplicationOptions(tail(unfiltered), Lists.newArrayList(filtered));
                    }
                }
            }
        }
    }

    /**
     * Checks if thisAO is after thatAO, ie. if thisAO is the latest of the two.
     *
     * @param thisAO
     * @param thatAO
     * @return
     */
    private boolean isAfter(ApplicationOption thisAO, ApplicationOption thatAO) {
        return thisAO.lastApplicationDate().after(thatAO.lastApplicationDate());
    }

    private ApplicationOption head(List<ApplicationOption> applicationOptions) {
        if (!applicationOptions.isEmpty()) {
            return applicationOptions.get(0);
        } else {
            return null;
        }
    }

    private List<ApplicationOption> tail(List<ApplicationOption> applicationOptions) {
        if (!applicationOptions.isEmpty()) {
            return applicationOptions.subList(1, applicationOptions.size());
        } else {
            return applicationOptions;
        }
    }

    private boolean isCurrentOrFuture(ApplicationOption ao) {
        return isCurrent(ao) || isFuture(ao);
    }

    private boolean isCurrent(ApplicationOption ao) {
        Date now = new Date();
        for (DateRange dr : ao.getApplicationDates()) {
            if (dr.getStartDate().before(now) && dr.getEndDate().after(now)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFuture(ApplicationOption ao) {
        Date now = new Date();
        for (DateRange dr : ao.getApplicationDates()) {
            if (dr.getStartDate().before(now)) {
                return false;
            }
        }
        return true;
    }



}