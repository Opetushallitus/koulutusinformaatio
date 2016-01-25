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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOffice;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOptionAttachment;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.YhteystiedotV1RDTO;
import fi.vm.sade.tarjonta.shared.types.Osoitemuoto;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionCreator extends ObjectCreator {


    private static final String KOULUTUS_KOODISTO_URI = "koulutus";

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationOptionCreator.class);

    private static final String AMMATILLINEN_KOULUTUS_KOULUTUSASTE = "koulutusasteoph2002_32";

    private static final String EI_KYSYTA_HARKINNANVARAISIA_KOODIURI = "hakulomakkeenasetukset_eiharkinnanvaraisuutta";

    private KoodistoService koodistoService;
    private EducationObjectCreator educationObjectCreator;
    private ApplicationSystemCreator applicationSystemCreator;

    private List<String> overriddenASOids;

    protected ApplicationOptionCreator(KoodistoService koodistoService,
            OrganisaatioRawService organisaatioRawService,
            ParameterService parameterService, List<String> overriddenASOids) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.educationObjectCreator = new EducationObjectCreator(koodistoService, organisaatioRawService);
        this.overriddenASOids = overriddenASOids;
        this.applicationSystemCreator = new ApplicationSystemCreator(koodistoService, parameterService, overriddenASOids);
    }

    public ApplicationSystemCreator getApplicationSystemCreator() {
        return applicationSystemCreator;
    }

    public ApplicationOption createV1EducationApplicationOption(KoulutusLOS los, HakukohdeV1RDTO hakukohde, HakuV1RDTO haku) throws KoodistoException,
            ResourceNotFoundException {

        // Demoympäristössä halutaan näyttää vain ne hakukohteet, jotka kuuluvat hakuihin overriddenASOids listalla
        if (overriddenASOids != null && !overriddenASOids.isEmpty() && !overriddenASOids.contains(haku.getOid())) {
            return null;
        }

        ApplicationOption ao = new ApplicationOption();
        ao.setId(hakukohde.getOid());
        if (hakukohde.getHakukohteenNimet() != null) {
            ao.setName(super.getI18nText(hakukohde.getHakukohteenNimet()));
        } else if (hakukohde.getHakukohteenNimiUri() != null) {

            List<I18nText> hakKohdeNames = this.koodistoService.searchNames(hakukohde.getHakukohteenNimiUri());
            if (hakKohdeNames != null && !hakKohdeNames.isEmpty()) {
                ao.setName(hakKohdeNames.get(0));
            }
        }

        try {
            ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohde.getHakukohteenNimiUri()));
        } catch (Exception ex) {
            LOG.debug("HakukohdeNimiUri was not codeelement: " + ao.getId() + " name: " + hakukohde.getHakukohteenNimiUri());
        }
        if (ao.getAoIdentifier() == null) {
            ao.setAoIdentifier(hakukohde.getHakukohteenNimiUri());
        }

        ao.setAthleteEducation(isAthleteEducation(ao.getAoIdentifier()));
        ao.setStartingQuota(hakukohde.getAloituspaikatLkm());
        ao.setFirstTimerStartingQuota(hakukohde.getEnsikertalaistenAloituspaikat());
        ao.setStartingQuotaDescription(getI18nText(hakukohde.getAloituspaikatKuvaukset()));
        ao.setLowestAcceptedScore(hakukohde.getAlinValintaPistemaara());
        ao.setLowestAcceptedAverage(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        ao.setAttachmentDeliveryDeadline(hakukohde.getLiitteidenToimitusPvm());
        ao.setLastYearApplicantCount(hakukohde.getEdellisenVuodenHakijatLkm());
        ao.setSelectionCriteria(getI18nText(hakukohde.getValintaperusteKuvaukset()));
        ao.setSoraDescription(getI18nText(hakukohde.getSoraKuvaukset()));
        ao.setEligibilityDescription(getI18nText(hakukohde.getHakukelpoisuusVaatimusKuvaukset()));
        ao.setExams(educationObjectCreator.createEducationExams(hakukohde.getValintakokeet()));
        if (hakukohde.getValintakokeet() != null) {
            for (ValintakoeV1RDTO valintakoe : hakukohde.getValintakokeet()) {
                ao.setOverallScoreLimit(educationObjectCreator.resolvePointLimit(valintakoe, "Kokonaispisteet"));
            }
        }
        ao.setOrganizationGroups(educationObjectCreator.createOrganizationGroups(hakukohde.getRyhmaliitokset(), hakukohde.getOrganisaatioRyhmaOids()));
        ao.setKaksoistutkinto(hakukohde.getKaksoisTutkinto());
        ao.setVocational(SolrConstants.ED_TYPE_AMMATILLINEN.equals(los.getEducationType()));
        if (los.getEducationCode() != null) {
            ao.setEducationCodeUri(los.getEducationCode().getUri());
            ao.setKysytaanHarkinnanvaraiset(getKysytaankoHarkinnanvaraiset(hakukohde, los.getEducationDegree(), los.getEducationCode().getUri()));
        }
        ao.setPrerequisite(los.getKoulutusPrerequisite());
        ao.setPohjakoulutusLiitteet(hakukohde.getPohjakoulutusliitteet());
        ao.setJosYoEiMuitaLiitepyyntoja(hakukohde.isJosYoEiMuitaLiitepyyntoja());

        List<String> baseEducations = new ArrayList<String>();
        for (Code code : los.getPrerequisites()) {
            baseEducations.add(code.getUri());
        }
        baseEducations.addAll(hakukohde.getHakukelpoisuusvaatimusUris());
        if (los.getKoulutusPrerequisite() != null) {
            List<Code> subCodes = koodistoService.searchSubCodes(
                    los.getKoulutusPrerequisite().getUri(),
                    TarjontaConstants.BASE_EDUCATION_KOODISTO_URI
                    );
            for (Code subCode : subCodes) {
                baseEducations.add(subCode.getValue());
            }
        }
        ao.setRequiredBaseEducations(baseEducations);

        ApplicationSystem as = applicationSystemCreator.createApplicationSystemForAo(haku, hakukohde);

        HakuaikaV1RDTO aoHakuaika = null;

        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : haku.getHakuaikas()) {
                if (ha.getHakuaikaId().equals(hakukohde.getHakuaikaId())) {
                    aoHakuaika = ha;
                }
            }
        }

        ao.setApplicationSystem(as);
        if (!Strings.isNullOrEmpty(hakukohde.getSoraKuvausKoodiUri())) {
            ao.setSora(true);
        }

        ao.setTeachingLanguages(extractCodeVales(los.getTeachingLanguages()));

        ao.setSpecificApplicationDates(hakukohde.isKaytetaanHakukohdekohtaistaHakuaikaa());
        if (ao.isSpecificApplicationDates()) {
            ao.setApplicationStartDate(hakukohde.getHakuaikaAlkuPvm());
            ao.setApplicationEndDate(hakukohde.getHakuaikaLoppuPvm());
        } else if (aoHakuaika != null) {
            ao.setApplicationStartDate(aoHakuaika.getAlkuPvm());
            ao.setApplicationEndDate(aoHakuaika.getLoppuPvm());
            ao.setInternalASDateRef(aoHakuaika.getHakuaikaId());
        } else if (haku.getHakuaikas() != null && !haku.getHakuaikas().isEmpty()) {
            ao.setApplicationStartDate(haku.getHakuaikas().get(0).getAlkuPvm());
            ao.setApplicationEndDate(haku.getHakuaikas().get(0).getLoppuPvm());
            ao.setApplicationPeriodName(super.getI18nText(haku.getHakuaikas().get(0).getNimet()));
            ao.setInternalASDateRef(haku.getHakuaikas().get(0).getHakuaikaId());
        }

        if (overriddenASOids != null && overriddenASOids.contains(haku.getOid())) {
            setDemoApplicationDates(ao);
        }

        if (aoHakuaika != null && aoHakuaika.getNimet() != null && !aoHakuaika.getNimet().isEmpty()) {
            ao.setApplicationPeriodName(super.getI18nText(aoHakuaika.getNimet()));
        }

        ao.setAttachmentDeliveryAddress(educationObjectCreator.createAddress(hakukohde.getLiitteidenToimitusOsoite(), null));

        HashMap<Integer, ApplicationOptionAttachment> attachments = new HashMap<Integer, ApplicationOptionAttachment>();
        if (hakukohde.getHakukohteenLiitteet() != null && !hakukohde.getHakukohteenLiitteet().isEmpty()) {
            for (HakukohdeLiiteV1RDTO liite : hakukohde.getHakukohteenLiitteet()) {

                if (liite.getJarjestys() != null && attachments.containsKey(liite.getJarjestys())) { // merge existing attachments
                    ApplicationOptionAttachment attach = attachments.get(liite.getJarjestys());

                    if (!StringUtils.isEmpty(liite.getLiitteenNimi()))
                        attach.setType(mergeI18nTexts(getI18nText(liite.getLiitteenNimi(), liite.getKieliUri()), attach.getType()));
                    attach.setDescreption(mergeI18nTexts(getI18nText(liite.getLiitteenKuvaukset()), attach.getDescreption()));
                    attach.setEmailAddr(mergeI18nTexts(getI18nText(liite.getSahkoinenToimitusOsoite(), liite.getKieliUri()), attach.getEmailAddr()));

                    Address a1 = attach.getAddress();
                    Address a2 = educationObjectCreator.createAddress(liite.getLiitteenToimitusOsoite(), liite.getKieliUri());

                    Address addr = new Address();
                    addr.setPostalCode(mergeI18nTexts(a1.getPostalCode(), a2.getPostalCode()));
                    addr.setPostOffice(mergeI18nTexts(a1.getPostOffice(), a2.getPostOffice()));
                    addr.setSecondForeignAddr(mergeI18nTexts(a1.getSecondForeignAddr(), a2.getSecondForeignAddr()));
                    addr.setStreetAddress(mergeI18nTexts(a1.getStreetAddress(), a2.getStreetAddress()));
                    attach.setAddress(addr);

                    attachments.put(liite.getJarjestys(), attach);
                } else { // create new attachment
                    ApplicationOptionAttachment attach = new ApplicationOptionAttachment();

                    if (!StringUtils.isEmpty(liite.getLiitteenNimi())) {
                        attach.setType(getI18nText(liite.getLiitteenNimi(), liite.getKieliUri()));
                    } else {
                        attach.setType(koodistoService.searchFirstName(liite.getLiitteenTyyppi()));
                    }
                    attach.setDueDate(liite.getToimitettavaMennessa());
                    attach.setUsedInApplicationForm(liite.isKaytetaanHakulomakkeella());
                    attach.setDescreption(getI18nText(liite.getLiitteenKuvaukset()));
                    attach.setAddress(educationObjectCreator.createAddress(liite.getLiitteenToimitusOsoite(), liite.getKieliUri()));

                    attach.setEmailAddr(getI18nText(liite.getSahkoinenToimitusOsoite(), liite.getKieliUri()));

                    attachments.put(liite.getJarjestys(), attach);
                }
            }
        }
        ao.setAttachments(new ArrayList<ApplicationOptionAttachment>(attachments.values()));
        ao.setAdditionalInfo(getI18nText(hakukohde.getLisatiedot()));

        ao.setApplicationOffice(getApplicationOffice(hakukohde.getYhteystiedot()));

        ao.getKomotoOids().addAll(hakukohde.getHakukohdeKoulutusOids());

        ao.setAdditionalProof(educationObjectCreator.createAdditionalProof(hakukohde.getValintakokeet()));

        ao.setHakuMenettelyKuvaukset(getI18nText(hakukohde.getHakuMenettelyKuvaukset()));
        ao.setPeruutusEhdotKuvaukset(getI18nText(hakukohde.getPeruutusEhdotKuvaukset()));

        if (SolrConstants.ED_TYPE_AMMATILLINEN.equals(los.getEducationType())) {
            ao.setEducationTypeUri(SolrConstants.ED_TYPE_AMMATILLINEN_SHORT);
        }

        ao.setPaid(haku.isMaksumuuriKaytossa());

        return ao;
    }

    /*
     * Harkinnanvaraiset kysymykset kysytään ammatillisilta koulutuksilta.
     * Poikkeuksena ovat koodirelaatioilla määritellyt koulutukset, joilla harkinnanvaraiset kysymykset jätetään pois, jos niillä on valintakokeita.
     */
    private boolean getKysytaankoHarkinnanvaraiset(HakukohdeV1RDTO hakukohde, String koulutusaste, String koulutuskoodi) throws KoodistoException {
        boolean isAmmatillinen = AMMATILLINEN_KOULUTUS_KOULUTUSASTE.equals(koulutusaste);
        boolean hasExams = !CollectionUtils.isEmpty(hakukohde.getValintakokeet());
        if (isAmmatillinen && hasExams && !StringUtils.isEmpty(koulutuskoodi)) {
            try {
                List<Code> koodit = koodistoService.searchSuperCodes(EI_KYSYTA_HARKINNANVARAISIA_KOODIURI, KOULUTUS_KOODISTO_URI);
                for (Code code : koodit) {
                    if (koulutuskoodi.equals(code.getUri()))
                        return false; // Koodistossa on erikseen asetettu koodinsuhteilla että koulutukselta ei kysytä harkinnanvaraisuutta.
                }
            } catch (KoodistoException e) {
                LOG.error("Hakulomakkeen asetuskoodiston koodi {} palautti virheen: {}", EI_KYSYTA_HARKINNANVARAISIA_KOODIURI,
                        e.getMessage(), e);
                throw e;
            }
        }
        return isAmmatillinen;
    }

    private void setDemoApplicationDates(ApplicationOption ao) {
        LOG.warn("Puukotetaan demohakukohde {} näkyviin!", ao.getId());
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, -6);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 6);
        ao.setApplicationStartDate(start.getTime());
        ao.setApplicationEndDate(end.getTime());
    }

    private List<String> extractCodeVales(List<Code> teachingLanguages) {
        List<String> vals = new ArrayList<String>();
        for (Code curCode : teachingLanguages) {
            vals.add(curCode.getValue());
        }
        return vals;
    }

    private ApplicationOffice getApplicationOffice(List<YhteystiedotV1RDTO> yhteystiedot) throws KoodistoException {
        if (yhteystiedot == null || yhteystiedot.isEmpty()) {
            return null;
        } else {
            fallbacklang = null;
            langsToBeFallbacked = new ArrayList<String>(Arrays.asList(new String[] { "fi", "sv", "en" }));
            for (YhteystiedotV1RDTO yt : yhteystiedot) {
                yt.setLang(koodistoService.searchFirstCodeValue(yt.getLang()).toLowerCase());
                setFallbacklanguage(yt.getLang());
            }
            I18nText hakutoimistonNimi = getHakutoimistonNimi(yhteystiedot);
            I18nText phone = getPhoneNumber(yhteystiedot);
            I18nText email = getEmail(yhteystiedot);
            I18nText www = getWww(yhteystiedot);
            Address visitingAddress = getLocalizedVisitingAddress(yhteystiedot);
            Address postalAddress = getLocalizedAddress(yhteystiedot);
            return new ApplicationOffice(hakutoimistonNimi, phone, email, www, visitingAddress, postalAddress);
        }
    }

    private I18nText getHakutoimistonNimi(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getHakutoimistonNimi());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getWww(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getWwwOsoite());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getEmail(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getSahkopostiosoite());
        }
        return getSanitizedI18nText(map);
    }

    private I18nText getPhoneNumber(List<YhteystiedotV1RDTO> yhteystiedot) {
        Map<String, String> map = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            map.put(yt.getLang(), yt.getPuhelinnumero());
        }
        return getSanitizedI18nText(map);
    }

    private Address getLocalizedAddress(List<YhteystiedotV1RDTO> yhteystiedot) {
        Address a = new Address();
        Map<String, String> streetAddress = new HashMap<String, String>();
        Map<String, String> secondForeignAddr = new HashMap<String, String>();
        Map<String, String> postalCode = new HashMap<String, String>();
        Map<String, String> postOffice = new HashMap<String, String>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            if (Osoitemuoto.KANSAINVALINEN.equals(yt.getOsoitemuoto())) {
                streetAddress.put(yt.getLang(), yt.getKansainvalinenOsoite());
            } else {
                streetAddress.put(yt.getLang(), yt.getOsoiterivi1());
                secondForeignAddr.put(yt.getLang(), yt.getOsoiterivi2());
                postOffice.put(yt.getLang(), yt.getPostitoimipaikka());
                postalCode.put(yt.getLang(), yt.getPostinumeroArvo());
            }
        }
        a.setStreetAddress(getSanitizedI18nText(streetAddress));
        a.setSecondForeignAddr(getSanitizedI18nText(secondForeignAddr));
        a.setPostOffice(getSanitizedI18nText(postOffice));
        a.setPostalCode(getSanitizedI18nText(postalCode));
        return a;
    }

    private I18nText getSanitizedI18nText(Map<String, String> translations) {
        for (String key : translations.keySet()) {
            if (translations.get(key) == null)
                translations.put(key, "");
        }
        insertFallbackLanguageValues(translations);
        return new I18nText(translations);
    }

    private Address getLocalizedVisitingAddress(List<YhteystiedotV1RDTO> yhteystiedot) {
        List<YhteystiedotV1RDTO> visitingAddreses = new ArrayList<YhteystiedotV1RDTO>();
        for (YhteystiedotV1RDTO yt : yhteystiedot) {
            YhteystiedotV1RDTO kayntiosoite = yt.getKayntiosoite();
            kayntiosoite.setLang(yt.getLang());
            kayntiosoite.setOsoitemuoto(yt.getOsoitemuoto());
            visitingAddreses.add(kayntiosoite);
        }
        return getLocalizedAddress(visitingAddreses);
    }

    private String fallbacklang;
    private List<String> langsToBeFallbacked;

    private void setFallbacklanguage(String lang) {
        langsToBeFallbacked.remove(lang);
        if (fallbacklang == null || fallbacklang.equals("en") || lang.equals("fi")) {
            fallbacklang = lang;
        }
    }

    private void insertFallbackLanguageValues(Map<String, String> map) {
        if (map.get(fallbacklang) != null) {
            for (String lang : langsToBeFallbacked) {
                map.put(lang, map.get(fallbacklang));
            }
        }
    }

    private boolean isAthleteEducation(final String aoIdentifier) {
        if (!Strings.isNullOrEmpty(aoIdentifier)) {
            try {
                List<Code> superCodes = koodistoService.searchSuperCodes(TarjontaConstants.ATHLETE_EDUCATION_KOODISTO_URI,
                        TarjontaConstants.APPLICATION_OPTIONS_KOODISTO_URI);
                if (superCodes != null) {
                    for (Code code : superCodes) {
                        if (aoIdentifier.equals(code.getValue())) {
                            return true;
                        }
                    }
                }
            } catch (KoodistoException e) {
                throw new KIConversionException("Conversion failed - " + e.getMessage());
            }
        }
        return false;
    }

}
