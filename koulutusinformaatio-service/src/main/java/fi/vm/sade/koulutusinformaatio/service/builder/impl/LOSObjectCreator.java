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
import com.google.common.collect.Sets;

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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class LOSObjectCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(LOSObjectCreator.class);

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

    public ParentLOS createParentLOS(KomoDTO parentKomo, String providerId, List<KomotoDTO> parentKomotos) throws KoodistoException {
        LOG.debug(Joiner.on(" ").join("Creating provider specific parent LOS from komo: ", parentKomo.getOid()));

        ParentLOS parentLOS = new ParentLOS();
        parentLOS.setType(TarjontaConstants.TYPE_PARENT);

        // parent info
        parentLOS.setId(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerId));
        parentLOS.setName(koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri()));
        parentLOS.setStructure(getI18nText(parentKomo.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        parentLOS.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        parentLOS.setThemes(getThemes(parentLOS));
        parentLOS.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        parentLOS.setCreditValue(parentKomo.getLaajuusArvo());
        parentLOS.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));

        Provider provider = providerService.getByOID(providerId);
        parentLOS.setProvider(provider);

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

    public ChildLOS createChildLOS(KomoDTO childKomo, String childLOSId, List<KomotoDTO> childKomotos) throws KoodistoException {
        ChildLOS childLOS = new ChildLOS();
        childLOS.setType(TarjontaConstants.TYPE_CHILD);
        childLOS.setId(childLOSId);
        childLOS.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        childLOS.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        childLOS.setDegreeTitle(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        childLOS.setGoals(getI18nText(childKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        childLOS.setLois(loiCreator.createChildLOIs(childKomotos, childLOS.getId(), childLOS.getName(), educationCodeUri));
        return childLOS;
    }

    public SpecialLOS createRehabLOS(KomoDTO childKomo, KomoDTO parentKomo, String specialLOSId,
            KomotoDTO childKomoto, String providerOid) throws KoodistoException {
        SpecialLOS los = new SpecialLOS();
        if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_REHAB);
        } else {
            los.setType(TarjontaConstants.TYPE_SPECIAL);
        }

        los.setId(specialLOSId);
        String teachingLang = koodistoService.searchFirstCodeValue(childKomoto.getOpetuskieletUris().get(0)).toLowerCase();
        Map<String, String> nameTranslations = Maps.newHashMap();
        nameTranslations.put(teachingLang, childKomoto.getKoulutusohjelmanNimi());
        los.setName(new I18nText(nameTranslations, nameTranslations));
        los.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        los.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        los.setDegreeTitle(koodistoService.searchFirst(childKomo.getLukiolinjaUri()));
        los.setStructure(getI18nText(parentKomo.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        los.setAccessToFurtherStudies(getI18nText(parentKomo.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        los.setProvider(providerService.getByOID(providerOid));
        los.setCreditValue(childKomoto.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(childKomoto.getLaajuusYksikkoUri()));
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
        los.setLois(lois);

        return los;
    }

    public SpecialLOS createSpecialLOS(KomoDTO childKomo, KomoDTO parentKomo, String specialLOSId,
            List<KomotoDTO> childKomotos, String providerOid) throws KoodistoException {
        SpecialLOS los = new SpecialLOS();
        if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_REHAB);
        } else {
            los.setType(TarjontaConstants.TYPE_SPECIAL);
        }

        los.setId(specialLOSId);
        los.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        los.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        los.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        los.setDegreeTitle(koodistoService.searchFirst(childKomo.getLukiolinjaUri()));
        los.setStructure(getI18nText(parentKomo.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        los.setAccessToFurtherStudies(getI18nText(parentKomo.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        los.setProvider(providerService.getByOID(providerOid));
        los.setCreditValue(parentKomo.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));
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

    public UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, KomoDTO parentKomo, List<KomotoDTO> komotos, String losID, Provider provider) throws KoodistoException {
        UpperSecondaryLOS los = new UpperSecondaryLOS();
        los.setType(TarjontaConstants.TYPE_UPSEC);
        los.setId(losID);
        los.setName(koodistoService.searchFirst(komo.getLukiolinjaUri()));
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));
        los.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        los.setQualification(koodistoService.searchFirst(komo.getTutkintonimikeUri()));
        los.setDegreeTitle(koodistoService.searchFirst(komo.getLukiolinjaUri()));
        los.setStructure(getI18nText(parentKomo.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        los.setAccessToFurtherStudies(getI18nText(parentKomo.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        los.setProvider(provider);
        
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

    public HigherEducationLOS createHigherEducationLOS(KoulutusKorkeakouluV1RDTO koulutus, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        HigherEducationLOS los = new HigherEducationLOS();

        los.setType(TarjontaConstants.TYPE_KK);
        los.setId(koulutus.getOid());
        los.setKomoOid(koulutus.getKomoOid());

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstis().containsKey("undefined")) {
            los.setInfoAboutTeachingLangs(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey("undefined")) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey("undefined")) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA).getTekstis().containsKey("undefined")) {
            los.setMajorSelection(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey("undefined")) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET).getTekstis().containsKey("undefined")) {
            los.setFinalExam(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey("undefined")) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey("undefined")) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey("undefined")) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey("undefined")) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET).getTekstis().containsKey("undefined")) {
            los.setResearchFocus(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET)));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey("undefined")) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey("undefined")) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));//koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        Set<String> availableLanguagaes = Sets.newHashSet();
        if (los.getContent() != null) {
            availableLanguagaes.addAll(los.getContent().getTranslations().keySet());
        }
        if (los.getInternationalization() != null) {
            availableLanguagaes.addAll(los.getInternationalization().getTranslations().keySet());
        }
        if (los.getCooperation() != null) {
            availableLanguagaes.addAll(los.getCooperation().getTranslations().keySet());
        }
        for (Code teachingLanguage : los.getTeachingLanguages()) {
            availableLanguagaes.add(teachingLanguage.getValue().toLowerCase());
        }

        los.setAvailableTranslationLanguages(new ArrayList<String>(availableLanguagaes));

        if (koulutus.getYhteyshenkilos() != null) {
            for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getYhteyshenkilos()) {
                ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                        yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                los.getContactPersons().add(contactPerson);
            }
        }

        los.setEducationDomain(getI18nTextEnriched(koulutus.getKoulutusala().getMeta()));
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koulutus.getKoulutuskoodi().getUri());
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());//getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));//getTutkinto().getMeta()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        //los.setEducationType(getI18nTextEnriched(koulutus.get.getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualification(getI18nTextEnrichedFirst(koulutus.getTutkintonimikes()));//getTutkintonimike().getMeta()));
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
        Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
        los.setProvider(provider);

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

            }
        }
        
        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));
        
        return los;
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

}
