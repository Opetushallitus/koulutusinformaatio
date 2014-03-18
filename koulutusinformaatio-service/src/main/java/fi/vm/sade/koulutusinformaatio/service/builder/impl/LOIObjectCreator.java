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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.BasicLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.LOI;
import fi.vm.sade.koulutusinformaatio.domain.LanguageSelection;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

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

    private <T extends LOI> T createLOI(Class<T> type, KomotoDTO komoto) throws  TarjontaParseException, KoodistoException {
        T loi = null;
        try {
            loi = type.newInstance();
        } catch (IllegalAccessException e) {
            throw new TarjontaParseException(String.format("Could not create new child LOI, komoto oid: %s, error message: %s", komoto.getOid(), e.getMessage()));
        } catch (InstantiationException e) {
            throw new TarjontaParseException(String.format("Could not create new child LOI, komoto oid: %s, error message: %s", komoto.getOid(), e.getMessage()));
        }
        loi.setId(komoto.getOid());
        loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
        return loi;

    }
    private <T extends BasicLOI> T createBasicLOI(Class<T> type, KomotoDTO komoto) throws TarjontaParseException, KoodistoException {
        T basicLOI = createLOI(type, komoto);
        Map<String,Code> availableLanguagesMap = new HashMap<String,Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();
        basicLOI.setFormOfTeaching(koodistoService.searchMultiple(komoto.getOpetusmuodotUris()));
        basicLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(komoto.getOpetuskieletUris()));
        basicLOI.setStartDate(komoto.getKoulutuksenAlkamisDate());
        basicLOI.setFormOfEducation(koodistoService.searchMultiple(komoto.getKoulutuslajiUris()));
        basicLOI.setInternationalization(getI18nText(komoto.getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN)));
        basicLOI.setCooperation(getI18nText(komoto.getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
        basicLOI.setContent(getI18nText(komoto.getTekstit().get(KomotoTeksti.SISALTO)));
        basicLOI.setPlannedDuration(komoto.getSuunniteltuKestoArvo());
        basicLOI.setPlannedDurationUnit(koodistoService.searchFirst(komoto.getSuunniteltuKestoYksikkoUri()));
        basicLOI.setPduCodeUri(komoto.getLaajuusYksikkoUri());
        if (basicLOI.getContent() != null) {
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(new ArrayList<String>(komoto.getTekstit().get(KomotoTeksti.SISALTO).keySet())));
        }
        if (basicLOI.getInternationalization() != null) {
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(new ArrayList<String>(komoto.getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN).keySet())));
        }
        if (basicLOI.getCooperation() != null) {
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(new ArrayList<String>(komoto.getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).keySet())));
        }
        for (Code teachingLanguage : basicLOI.getTeachingLanguages()) {
            availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage);
        }
        for (Code curCode: rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }
        
        basicLOI.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));
        return basicLOI;
    }

    public List<ChildLOI> createChildLOIs(List<KomotoDTO> childKomotos,
                                          String losId, I18nText losName, String educationCodeUri) throws KoodistoException, TarjontaParseException {
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

    public ChildLOI createChildLOI(KomotoDTO childKomoto, String losId, I18nText losName, String educationCodeUri) throws KoodistoException, TarjontaParseException {
        ChildLOI childLOI = createBasicLOI(ChildLOI.class, childKomoto);
        childLOI.setName(losName);
        childLOI.setLosId(losId);
        childLOI.setParentLOIId(childKomoto.getParentKomotoOid());
        childLOI.setWebLinks(childKomoto.getWebLinkkis());
        childLOI.setProfessionalTitles(koodistoService.searchMultiple(childKomoto.getAmmattinimikeUris()));
        childLOI.setWorkingLifePlacement(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));

        if (childKomoto.getYhteyshenkilos() != null) {
            for (YhteyshenkiloRDTO yhteyshenkiloRDTO : childKomoto.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getEmail(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                childLOI.getContactPersons().add(contactPerson);
            }
        }

        List<OidRDTO> hakukohdeOidDTOs = tarjontaRawService.getHakukohdesByKomoto(childKomoto.getOid());
        List<String> hakukohdeOids = Lists.transform(hakukohdeOidDTOs, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return input.getOid();
            }
        });

        childLOI.setApplicationOptions(applicationOptionCreator.createVocationalApplicationOptions(hakukohdeOids, childKomoto, childLOI.getPrerequisite(), educationCodeUri));
        boolean kaksoistutkinto = false;
        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.isKaksoistutkinto()) {
                kaksoistutkinto = true;
                break;
            }
        }
        childLOI.setKaksoistutkinto(kaksoistutkinto);

        return childLOI;
    }

    public List<UpperSecondaryLOI> createUpperSecondaryLOIs(List<KomotoDTO> komotos, String losId, I18nText losName, String educationCodeUri) throws KoodistoException, TarjontaParseException {
        List<UpperSecondaryLOI> lois = Lists.newArrayList();
        for (KomotoDTO komoto : komotos) {
            if (CreatorUtil.komotoPublished.apply(komoto)) {
                lois.add(createUpperSecondaryLOI(komoto, losId, losName, educationCodeUri));
            }
        }
        return filter(lois);
    }

    public UpperSecondaryLOI createUpperSecondaryLOI(KomotoDTO komoto, String losId, I18nText losName, String educationCodeUri) throws TarjontaParseException, KoodistoException {
        UpperSecondaryLOI loi = createBasicLOI(UpperSecondaryLOI.class, komoto);
        loi.setName(losName);

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

        List<OidRDTO> hakukohdeOidDTOs = tarjontaRawService.getHakukohdesByKomoto(komoto.getOid());
        List<String> hakukohdeOids = Lists.transform(hakukohdeOidDTOs, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return input.getOid();
            }
        });
        loi.setApplicationOptions(applicationOptionCreator.createUpperSecondaryApplicationOptions(hakukohdeOids, komoto,
                loi.getPrerequisite(), educationCodeUri));
        boolean kaksoistutkinto = false;
        for (ApplicationOption ao : loi.getApplicationOptions()) {
            if (ao.isKaksoistutkinto()) {
                kaksoistutkinto = true;
                break;
            }
        }
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
            Date endDate = dr.getEndDate();
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.add(Calendar.MONTH, 4);
            endDate = endCal.getTime();
            if (dr.getStartDate().before(now) && endDate.after(now)) {
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
