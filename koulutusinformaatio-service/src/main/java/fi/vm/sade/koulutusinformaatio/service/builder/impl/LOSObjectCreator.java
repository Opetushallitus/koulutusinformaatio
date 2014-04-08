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
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class LOSObjectCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(LOSObjectCreator.class);

    private static final String UNDEFINED = "undefined";

    private KoodistoService koodistoService;
    private ProviderService providerService;
    private LOIObjectCreator loiCreator;

    public LOSObjectCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService,
            ProviderService providerService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService);
    }

    private <T extends LOS> T createLOS(Class<T> type) throws TarjontaParseException {
        T los;
        try {
            los = type.newInstance();
        } catch (InstantiationException e) {
            throw new TarjontaParseException(String.format("Failed to instantiate new %s object: %s", type.getName(), e.getMessage()));
        } catch (IllegalAccessException e) {
            throw new TarjontaParseException(String.format("Failed to instantiate new %s object: %s", type.getName(), e.getMessage()));
        }

        return los;
    }

    @SuppressWarnings("rawtypes")
    private <T extends InstantiatedLOS> T createInstantiatedLOS(Class<T> type, KomoDTO komo) throws TarjontaParseException {
        return createLOS(type);
    }

    @SuppressWarnings("rawtypes")
    private <T extends BasicLOS> T createBasicLOS(Class<T> type, KomoDTO komo, String providerId) throws TarjontaParseException, KoodistoException {
        T basicLOS = createLOS(type);
        basicLOS.setStructure(getI18nText(komo.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        try {
            basicLOS.setProvider(providerService.getByOID(providerId));
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }
        basicLOS.setAccessToFurtherStudies(getI18nText(komo.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        basicLOS.setEducationDegree(koodistoService.searchFirstCodeValue(komo.getKoulutusAsteUri()));
        return basicLOS;
    }

    public ParentLOS createParentLOS(KomoDTO parentKomo, String providerId, List<KomotoDTO> parentKomotos) throws KoodistoException, TarjontaParseException {
        LOG.debug(Joiner.on(" ").join("Creating provider specific parent LOS from komo: ", parentKomo.getOid()));

        ParentLOS parentLOS = createBasicLOS(ParentLOS.class, parentKomo, providerId);
        parentLOS.setType(TarjontaConstants.TYPE_PARENT);

        // los info
        parentLOS.setId(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerId));
        parentLOS.setName(koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri()));
        parentLOS.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        parentLOS.setCreditValue(parentKomo.getLaajuusArvo());
        parentLOS.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));

        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        parentLOS.setThemes(getThemes(parentLOS));

        List<ParentLOI> lois = Lists.newArrayList();

        for (KomotoDTO komoto : parentKomotos) {
            ParentLOI loi = new ParentLOI();
            loi.setId(komoto.getOid());
            loi.setSelectingDegreeProgram(getI18nText(komoto.getTekstit().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA)));
            loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
            lois.add(loi);
        }
        parentLOS.setLois(lois);
        return parentLOS;
    }

    public ChildLOS createChildLOS(KomoDTO childKomo, String childLOSId, List<KomotoDTO> childKomotos) throws KoodistoException, TarjontaParseException {
        ChildLOS childLOS = createInstantiatedLOS(ChildLOS.class, childKomo);
        childLOS.setType(TarjontaConstants.TYPE_CHILD);
        childLOS.setId(childLOSId);
        childLOS.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        childLOS.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        childLOS.setGoals(getI18nText(childKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        childLOS.setLois(loiCreator.createChildLOIs(childKomotos, childLOS.getId(), childLOS.getName(), educationCodeUri));
        return childLOS;
    }

    public SpecialLOS createRehabLOS(KomoDTO childKomo, KomoDTO parentKomo, String specialLOSId,
            KomotoDTO childKomoto, String providerOid) throws KoodistoException, TarjontaParseException {
        SpecialLOS los = createBasicLOS(SpecialLOS.class, parentKomo, providerOid);
        if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_REHAB);
        } else if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE) 
                || childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)
                || childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)
                || childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)
                || childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.KANSANOPISTO_TYPE)) {
            los.setType(TarjontaConstants.TYPE_PREP);
            los.setEducationTypeUri(childKomo.getKoulutusTyyppiUri());
        } else {
            los.setType(TarjontaConstants.TYPE_SPECIAL);
        }

        los.setId(specialLOSId);
        String teachingLang = koodistoService.searchFirstCodeValue(childKomoto.getOpetuskieletUris().get(0)).toLowerCase();
        if (!los.getType().equals(TarjontaConstants.TYPE_PREP)) {
            Map<String, String> nameTranslations = Maps.newHashMap();
            nameTranslations.put(teachingLang, childKomoto.getKoulutusohjelmanNimi());
            los.setName(new I18nText(nameTranslations, nameTranslations));
        }
        los.setCreditValue(childKomoto.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(childKomoto.getLaajuusYksikkoUri()));
        los.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        los.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        los.setParent(new ParentLOSRef(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerOid),
                koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri())));
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));

        if (childKomo.getTavoitteet() == null) {
            los.setGoals(getI18nText(parentKomo.getTavoitteet()));
        } else {
            los.setGoals(getI18nText(childKomo.getTavoitteet()));
        }

        List<ChildLOI> lois = Lists.newArrayList();

        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];

        if (CreatorUtil.komotoPublished.apply(childKomoto)) {
            ChildLOI loi = loiCreator.createChildLOI(childKomoto, specialLOSId, los.getName(), educationCodeUri);
            lois.add(loi);
        }
        if (!lois.isEmpty() && los.getType().equals(TarjontaConstants.TYPE_PREP)) {
            createNameForLos(lois, los);
        }
        los.setLois(lois);

        return los;
    }

    private void createNameForLos(List<ChildLOI> lois, SpecialLOS los) {
        ChildLOI loi = lois.get(0);
        if (!loi.getApplicationOptions().isEmpty()) {
            ApplicationOption ao = loi.getApplicationOptions().get(0);
            los.setName(ao.getName());
        }
    }

    public SpecialLOS createSpecialLOS(KomoDTO childKomo, KomoDTO parentKomo, String specialLOSId,
            List<KomotoDTO> childKomotos, String providerOid) throws KoodistoException, TarjontaParseException {
        SpecialLOS los = createBasicLOS(SpecialLOS.class, parentKomo, providerOid);
        if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_REHAB);
        } else if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_PREP);
        } else {
            los.setType(TarjontaConstants.TYPE_SPECIAL);
        }

        los.setId(specialLOSId);
        los.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        los.setCreditValue(parentKomo.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));
        los.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        los.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        los.setParent(new ParentLOSRef(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerOid),
                koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri())));
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));

        if (childKomo.getTavoitteet() == null) {
            los.setGoals(getI18nText(parentKomo.getTavoitteet()));
        } else {
            los.setGoals(getI18nText(childKomo.getTavoitteet()));
        }

        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        los.setLois(loiCreator.createChildLOIs(childKomotos, specialLOSId, los.getName(), educationCodeUri));
        return los;
    }

    public UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, KomoDTO parentKomo, List<KomotoDTO> komotos, 
            String losID, String providerOid) 
                    throws KoodistoException, TarjontaParseException {
        UpperSecondaryLOS los = createBasicLOS(UpperSecondaryLOS.class, komo, providerOid);
        los.setType(TarjontaConstants.TYPE_UPSEC);
        los.setId(losID);
        los.setName(koodistoService.searchFirst(komo.getLukiolinjaUri()));
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));
        los.setQualification(koodistoService.searchFirst(komo.getTutkintonimikeUri()));

        I18nText laajuusyksikko = koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri());
        if (laajuusyksikko != null) {
            los.setCreditValue(parentKomo.getLaajuusArvo());
            los.setCreditUnit(laajuusyksikko);
        }

        Map<String, String> komoTavoitteet = komo.getTekstit().get(KomoTeksti.TAVOITTEET);
        if (komoTavoitteet == null) {
            los.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        } else {
            los.setGoals(getI18nText(komoTavoitteet));
        }
        // strip version out of education code uri
        String educationCodeUri = komo.getKoulutusKoodiUri().split("#")[0];
        los.setLois(loiCreator.createUpperSecondaryLOIs(komotos, losID, los.getName(), educationCodeUri));
        return los;
    }

    public HigherEducationLOS createHigherEducationLOS(KoulutusKorkeakouluV1RDTO koulutus, boolean checkStatus) 
            throws TarjontaParseException, KoodistoException {

        HigherEducationLOS los = new HigherEducationLOS();

        los.setType(TarjontaConstants.TYPE_KK);
        los.setId(koulutus.getOid());
        los.setKomoOid(koulutus.getKomoOid());


        //Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String,Code> availableLanguagesMap = new HashMap<String,Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutTeachingLangs(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            //availableLanguagaes.add(koodistoService.searchCodesMultiple(koodiUri))
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));            
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA).getTekstis().containsKey(UNDEFINED)) {
            los.setMajorSelection(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET).getTekstis().containsKey(UNDEFINED)) {
            los.setFinalExam(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)))); 
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchCodesMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)))); 
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setResearchFocus(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET)));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));//koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }

        for (Code teachingLanguage : los.getTeachingLanguages()) {
            availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage);
        }

        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        if (koulutus.getYhteyshenkilos() != null) {
            for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                    contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                }
                los.getContactPersons().add(contactPerson);
            }
        }

        los.setEducationDomain(getI18nTextEnriched(koulutus.getKoulutusala().getMeta()));
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirstCode(koulutus.getKoulutuskoodi().getUri()));//koulutus.getKoulutuskoodi().getUri());
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());//getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));//getTutkinto().getMeta()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        //los.setEducationType(getI18nTextEnriched(koulutus.get.getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualifications(getQualifications(koulutus));//getTutkintonimike().getMeta()));
        los.setDegree(getI18nTextEnriched(koulutus.getTutkinto().getMeta()));
        if (koulutus.getKoulutuksenAlkamisPvms() != null && !koulutus.getKoulutuksenAlkamisPvms().isEmpty()) {
            los.setStartDate(koulutus.getKoulutuksenAlkamisPvms().iterator().next());
        }
        if (koulutus.getKoulutuksenAlkamisvuosi() != null) {
            los.setStartYear(koulutus.getKoulutuksenAlkamisvuosi());
        }
        if (koulutus.getKoulutuksenAlkamiskausi() != null) {	
            los.setStartSeason(getI18nTextEnriched(koulutus.getKoulutuksenAlkamiskausi().getMeta()));
        }

        los.setPlannedDuration(koulutus.getSuunniteltuKestoArvo());
        los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getSuunniteltuKestoTyyppi().getMeta()));
        los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());//childKomoto.getLaajuusYksikkoUri());
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());//getOpintojenLaajuus().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));
        los.setChargeable(koulutus.getOpintojenMaksullisuus()); 


        //childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        los.setTopics(createCodes(koulutus.getAihees()));
        los.setThemes(getThemes(los));

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setProfessionalTitles(getI18nTextMultiple(koulutus.getAmmattinimikkeet()));

        los.setTeachingTimes(getI18nTextMultiple(koulutus.getOpetusAikas()));
        los.setTeachingPlaces(getI18nTextMultiple(koulutus.getOpetusPaikkas()));

        boolean existsValidHakukohde = fetchHakukohdeData(los, checkStatus);


        //If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && !existsValidHakukohde) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
                ao.setParent(createParetLosRef(los));
                ao.setType(TarjontaConstants.TYPE_KK);

            }

        }

        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));

        return los;
    }
    
    //tutkintonimike
    private List<I18nText> getQualifications(KoulutusKorkeakouluV1RDTO koulutus) throws KoodistoException {
        
        List<I18nText> qualifications = new ArrayList<I18nText>();
        
        KoodiV1RDTO kandKoul = koulutus.getKandidaatinKoulutuskoodi();
        
        List<Code> kandQuals = new ArrayList<Code>();
        
        if (kandKoul != null && kandKoul.getUri() != null) {
            
            kandQuals = this.koodistoService.searchSubCodes(kandKoul.getUri(), TarjontaConstants.TUTKINTONIMIKE_KK_KOODISTO_URI);
        }
        
        if (!kandQuals.isEmpty()) {
            qualifications.add(kandQuals.get(0).getName());
        }
        
        qualifications.addAll(getI18nTextMultiple(koulutus.getTutkintonimikes()));
        
        return qualifications;
    }



    private ParentLOSRef createParetLosRef(HigherEducationLOS los) {
        ParentLOSRef educationRef = new ParentLOSRef();
        educationRef.setId(los.getId());
        educationRef.setName(los.getName());
        educationRef.setLosType(TarjontaConstants.TYPE_KK);
        return educationRef;
    }

    private boolean fetchHakukohdeData(HigherEducationLOS los, boolean checkStatus) throws KoodistoException {
        ResultV1RDTO<List<NimiJaOidRDTO>> hakukohteet = loiCreator.tarjontaRawService.getHakukohdesByHigherEducation(los.getId());

        if (hakukohteet == null 
                || hakukohteet.getResult() == null 
                || hakukohteet.getResult().isEmpty()) {
            return false;
        }

        List<ApplicationOption> aos = new ArrayList<ApplicationOption>();

        for (NimiJaOidRDTO curHakukoh : hakukohteet.getResult()) {
            String aoId = curHakukoh.getOid();

            ResultV1RDTO<HakukohdeV1RDTO> hakukohdeRes = loiCreator.tarjontaRawService.getHigherEducationHakukohode(aoId);
            HakukohdeV1RDTO hakukohdeDTO = hakukohdeRes.getResult();

            if (checkStatus && !hakukohdeDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                continue;
            }

            ResultV1RDTO<HakuV1RDTO> hakuRes = loiCreator.tarjontaRawService.getHigherEducationHakuByOid(hakukohdeDTO.getHakuOid());

            HakuV1RDTO hakuDTO = hakuRes.getResult();

            if (checkStatus && !hakuDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                continue;
            }

            ApplicationOption ao = loiCreator.applicationOptionCreator.createHigherEducationApplicationOption(los, hakukohdeDTO, hakuRes.getResult());
            //If fetching for preview, the status of the application option is added
            if (!checkStatus) {
                ao.setStatus(hakukohdeDTO.getTila());
                ao.getApplicationSystem().setStatus(hakuDTO.getTila());
            }
            aos.add(ao);

        }

        los.setApplicationOptions(aos);

        return !aos.isEmpty();
    }

    public HigherEducationLOS createHigherEducationLOSReference(
            KoulutusKorkeakouluV1RDTO koulutusDTO, boolean b) throws KoodistoException {
        HigherEducationLOS los = new HigherEducationLOS();
        los.setId(koulutusDTO.getOid());
        los.setName(getI18nTextEnriched(koulutusDTO.getKoulutusohjelma()));
        los.setEducationDegree(koulutusDTO.getKoulutusaste().getUri());
        if (!b) {
            los.setStatus(koulutusDTO.getTila().toString());
        }
        return los;
    }

    public HigherEducationLOSRef createHigherEducationLOSRef(
            KoulutusKorkeakouluV1RDTO koulutusDTO, boolean b, ApplicationOption ao) throws TarjontaParseException, KoodistoException {

        HigherEducationLOSRef losRef = new HigherEducationLOSRef();

        losRef.setId(koulutusDTO.getOid());
        losRef.setName(getI18nTextEnriched(koulutusDTO.getKoulutusohjelma()));
        losRef.setQualifications(getI18nTextMultiple(koulutusDTO.getTutkintonimikes()));
        losRef.setPrerequisite(ao.getPrerequisite());

        return losRef;
    }

}
