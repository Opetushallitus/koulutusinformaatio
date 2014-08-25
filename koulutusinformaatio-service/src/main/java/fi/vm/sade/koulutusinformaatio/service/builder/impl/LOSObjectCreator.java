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

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiValikoimaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
    private OrganisaatioRawService organisaatioRawService;
    private LOIObjectCreator loiCreator;
    private TarjontaRawService tarjontaRawService;

    public LOSObjectCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService,
            ProviderService providerService, OrganisaatioRawService organisaatioRawService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.organisaatioRawService = organisaatioRawService;
        this.tarjontaRawService = tarjontaRawService;
        this.loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService, organisaatioRawService);
        
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
        Code name = koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri());
        parentLOS.setName(name.getName());
        parentLOS.setShortTitle(name.getShortName());
        parentLOS.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        parentLOS.setCreditValue(parentKomo.getLaajuusArvo());
        parentLOS.setCreditUnit(koodistoService.searchFirstShortName(parentKomo.getLaajuusYksikkoUri()));

        parentLOS.setEducationDomain(koodistoService.searchFirstShortName(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirstName(parentKomo.getOpintoalaUri()));
        parentLOS.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        parentLOS.setThemes(getThemes(parentLOS));

        parentLOS.setKotitalousopetus(parentKomo.getKoulutusKoodiUri() != null 
                && parentKomo.getKoulutusKoodiUri().contains(TarjontaConstants.KOTITALOUSKOODI));

        List<ParentLOI> lois = Lists.newArrayList();

        for (KomotoDTO komoto : parentKomotos) {
            ParentLOI loi = new ParentLOI();
            loi.setId(komoto.getOid());
            loi.setSelectingDegreeProgram(getI18nText(komoto.getTekstit().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA)));
            loi.setPrerequisite(koodistoService.searchFirst(komoto.getPohjakoulutusVaatimusUri()));
            lois.add(loi);
        }
        parentLOS.setLois(lois);
        return parentLOS;
    }

    public ChildLOS createChildLOS(KomoDTO childKomo, String childLOSId, List<KomotoDTO> childKomotos) throws KoodistoException, TarjontaParseException {
        ChildLOS childLOS = createInstantiatedLOS(ChildLOS.class, childKomo);
        childLOS.setType(TarjontaConstants.TYPE_CHILD);
        childLOS.setId(childLOSId);
        Code name = koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri());
        childLOS.setName(name.getName());
        childLOS.setShortTitle(name.getShortName());
        childLOS.setQualification(koodistoService.searchFirstName(childKomo.getTutkintonimikeUri()));
        childLOS.setGoals(getI18nText(childKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        childLOS.setLois(loiCreator.createChildLOIs(childKomotos, childLOS.getId(), childLOS.getName(), educationCodeUri, SolrConstants.ED_TYPE_AMMATILLINEN_SHORT));

        for (ChildLOI curChild : childLOS.getLois()) {
            //curChild.get
        }

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
            los.setName(new I18nText(nameTranslations));
            los.setShortTitle(new I18nText(nameTranslations));
        }
        los.setCreditValue(childKomoto.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirstShortName(childKomoto.getLaajuusYksikkoUri()));
        los.setQualification(koodistoService.searchFirstName(childKomo.getTutkintonimikeUri()));
        los.setEducationDomain(koodistoService.searchFirstShortName(parentKomo.getKoulutusAlaUri()));
        los.setParent(new ParentLOSRef(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerOid),
                koodistoService.searchFirstName(parentKomo.getKoulutusKoodiUri())));
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
            ChildLOI loi = loiCreator.createChildLOI(childKomoto, specialLOSId, los.getName(), educationCodeUri, resolveEducationType(los));
            for (ApplicationOption curAo : loi.getApplicationOptions()) {
                if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)) {
                    curAo.setVocational(false);
                }
            }
            lois.add(loi);
        }
        if (!lois.isEmpty() && los.getType().equals(TarjontaConstants.TYPE_PREP)) {
            createNameForLos(lois, los);
        }
        los.setLois(lois);
        List<String> aoIds = new ArrayList<String>();
        if (los.getLois() != null) {
            for (ChildLOI curLoi : los.getLois()) {
                if (curLoi.getApplicationOptions() != null) {
                    for (ApplicationOption ao : curLoi.getApplicationOptions()) {
                        if (!aoIds.contains(ao.getId())) {
                            aoIds.add(ao.getId());
                        }
                    }
                }
            }
        }
        los.setAoIds(aoIds);
        return los;
    }

    public String resolveEducationType(SpecialLOS los) {
        if (los.getType().equals(TarjontaConstants.TYPE_REHAB)) {
            return SolrConstants.ED_TYPE_VALMENTAVA_SHORT;

        } else if (los.getType().equals(TarjontaConstants.TYPE_PREP)) {
            if (los.getEducationTypeUri().equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE)) {
                return SolrConstants.ED_TYPE_VOC_PREP;
            } else if (los.getEducationTypeUri().equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)) {
                return SolrConstants.ED_TYPE_TENTH_GRADE;
            } else if (los.getEducationTypeUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)) {
                return SolrConstants.ED_TYPE_IMM_UPSEC;
            } else if (los.getEducationTypeUri().equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)) {
                return SolrConstants.ED_TYPE_IMM_VOC;
            } else if (los.getEducationTypeUri().equals(TarjontaConstants.KANSANOPISTO_TYPE)) {
                return SolrConstants.ED_TYPE_KANSANOPISTO;
            } 
        }
        else {
            return SolrConstants.ED_TYPE_AMM_ER_SHORT;
        }
        return null;
    }

    private void createNameForLos(List<ChildLOI> lois, SpecialLOS los) {
        ChildLOI loi = lois.get(0);
        if (!loi.getApplicationOptions().isEmpty()) {
            ApplicationOption ao = loi.getApplicationOptions().get(0);
            los.setName(ao.getName());
            los.setShortTitle(ao.getName());
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
        Code name = koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri());
        los.setName(name.getName());
        los.setShortTitle(name.getShortName());
        los.setCreditValue(parentKomo.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirstShortName(parentKomo.getLaajuusYksikkoUri()));
        los.setQualification(koodistoService.searchFirstName(childKomo.getTutkintonimikeUri()));
        los.setEducationDomain(koodistoService.searchFirstShortName(parentKomo.getKoulutusAlaUri()));
        los.setParent(new ParentLOSRef(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerOid),
                koodistoService.searchFirstName(parentKomo.getKoulutusKoodiUri())));
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));

        if (childKomo.getTavoitteet() == null) {
            los.setGoals(getI18nText(parentKomo.getTavoitteet()));
        } else {
            los.setGoals(getI18nText(childKomo.getTavoitteet()));
        }

        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        los.setLois(loiCreator.createChildLOIs(childKomotos, specialLOSId, los.getName(), educationCodeUri, this.resolveEducationType(los)));
        List<String> aoIds = new ArrayList<String>();
        if (los.getLois() != null) {
            for (ChildLOI curLoi : los.getLois()) {
                if (curLoi.getApplicationOptions() != null) {
                    for (ApplicationOption ao : curLoi.getApplicationOptions()) {
                        if (!aoIds.contains(ao.getId())) {
                            aoIds.add(ao.getId());
                        }
                    }
                }
            }
        }
        los.setAoIds(aoIds);
        return los;
    }

    public UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, KomoDTO parentKomo, List<KomotoDTO> komotos, 
            String losID, String providerOid) 
                    throws KoodistoException, TarjontaParseException {
        UpperSecondaryLOS los = createBasicLOS(UpperSecondaryLOS.class, komo, providerOid);
        los.setType(TarjontaConstants.TYPE_UPSEC);
        los.setId(losID);
        Code name = koodistoService.searchFirst(komo.getLukiolinjaUri());
        los.setName(name.getName());
        los.setShortTitle(name.getShortName());
        los.setTopics(getTopics(parentKomo.getOpintoalaUri()));
        los.setThemes(getThemes(los));
        los.setQualification(koodistoService.searchFirstName(komo.getTutkintonimikeUri()));

        I18nText creditUnit = koodistoService.searchFirstShortName(parentKomo.getLaajuusYksikkoUri());
        if (creditUnit != null) {
            los.setCreditValue(parentKomo.getLaajuusArvo());
            los.setCreditUnit(creditUnit);
        }

        Map<String, String> komoTavoitteet = komo.getTekstit().get(KomoTeksti.TAVOITTEET);
        if (komoTavoitteet == null) {
            los.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        } else {
            los.setGoals(getI18nText(komoTavoitteet));
        }
        // strip version out of education code uri
        String educationCodeUri = komo.getKoulutusKoodiUri().split("#")[0];
        los.setLois(loiCreator.createUpperSecondaryLOIs(komotos, losID, los.getName(), educationCodeUri, SolrConstants.ED_TYPE_LUKIO_SHORT));
        return los;
    }

    public HigherEducationLOS createHigherEducationLOS(KoulutusKorkeakouluV1RDTO koulutus, boolean checkStatus)
            throws TarjontaParseException, KoodistoException, ResourceNotFoundException {

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
            rawTranslCodes.addAll(koodistoService.searchMultiple(
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
        if (koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS).getTekstis().containsKey(UNDEFINED)) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)))); 
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
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
        LOG.debug("Koulutusohjelma for " + koulutus.getOid() + ": " + koulutus.getKoulutusohjelma());
        los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        LOG.debug("Short title: " + los.getShortTitle());
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        los.setEducationType(getEducationType(koulutus.getKoulutusaste().getUri()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualifications(getQualifications(koulutus));
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
        los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));
        los.setChargeable(koulutus.getOpintojenMaksullisuus()); 
        

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        los.setTopics(createCodes(koulutus.getAihees()));
        los.setThemes(getThemes(los));

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));

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
                ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_KK);
            }
        }

        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));

        return los;
    }
    
    public AdultUpperSecondaryLOS createAdultUpperSeconcaryLOS(KoulutusLukioV1RDTO koulutus, boolean checkStatus) 
            throws TarjontaParseException, KoodistoException {

        AdultUpperSecondaryLOS los = new AdultUpperSecondaryLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_UPSEC);
        los.setId(koulutus.getOid());
        los.setKomoOid(koulutus.getKomoOid());

        //Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String,Code> availableLanguagesMap = new HashMap<String,Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();

        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));            
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)))); 
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)))); 
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }
        
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT) != null
                &&!koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT).getTekstis().containsKey("UNDEFINED")) {
            los.setSubjectsAndCourses(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT)));
        }
        
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA) != null
                &&!koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA).getTekstis().containsKey("UNDEFINED")) {
            los.setTargetGroup(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA)));
        }
        
        if (koulutus.getKielivalikoima() != null) {//komoto.getTarjotutKielet() != null) {
            
            //Map<String, List<String>> kielivalikoimat = komoto.getTarjotutKielet();
            List<LanguageSelection> languageSelection = Lists.newArrayList();
            KoodiValikoimaV1RDTO kielivalikoima = koulutus.getKielivalikoima();
            //kielivalikoima.

            for (Map.Entry<String,KoodiUrisV1RDTO> oppiaine : kielivalikoima.entrySet()) {
                List<I18nText> languages = getI18nTextMultiple(oppiaine.getValue());//Lists.newArrayList();
                languageSelection.add(new LanguageSelection(oppiaine.getKey(), languages));
            }
            los.setLanguageSelection(languageSelection);
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
        
        if (koulutus.getLukiodiplomit() != null) {
            los.setDiplomas(getI18nTextMultiple(koulutus.getLukiodiplomit()));
        }

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
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
        LOG.debug("Koulutusohjelma for " + koulutus.getOid() + ": " + koulutus.getKoulutusohjelma());
        los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
        LOG.debug("Short title: " + los.getShortTitle());
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        los.setEducationType(getEducationType(koulutus.getKoulutusaste().getUri()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualifications(Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));
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
        los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        if (koulutus.getOpintoala() != null) {
            los.setTopics(getTopics(koulutus.getOpintoala().getUri()));
            los.setThemes(getThemes(los));
        }

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));        

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
                //ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_ADULT_UPSEC);
            }
        }

        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));

        return los;
    }
    
    private String getEducationType(String uri) {
        if (uri.contains(TarjontaConstants.ED_DEGREE_URI_AMK)) {
            return SolrConstants.ED_TYPE_AMK;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_YLEMPI_AMK)) {
            return SolrConstants.ED_TYPE_YLEMPI_AMK;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_KANDI)) {
            return SolrConstants.ED_TYPE_KANDIDAATTI;
        } else if (uri.contains(TarjontaConstants.ED_DEGREE_URI_MAISTERI)) {
            return SolrConstants.ED_TYPE_MAISTERI;
        }
        return null;
    }

    //tutkintonimike
    private List<I18nText> getQualifications(KoulutusKorkeakouluV1RDTO koulutus) throws KoodistoException {

        List<I18nText> qualifications = new ArrayList<I18nText>();

        KoodiV1RDTO kandKoul = koulutus.getKandidaatinKoulutuskoodi();

        List<Code> kandQuals = new ArrayList<Code>();

        if (kandKoul != null && kandKoul.getUri() != null) {

            kandQuals = this.koodistoService.searchSubCodes(kandKoul.getUri(), TarjontaConstants.TUTKINTONIMIKE_KK_KOODISTO_URI);
        }

        if (!kandQuals.isEmpty() && kandQuals.get(0).getName() != null) {
            qualifications.add(kandQuals.get(0).getName());
        }

        qualifications.addAll(getI18nTextMultiple(koulutus.getTutkintonimikes()));

        return qualifications;
    }



    private ParentLOSRef createParentLosRef(HigherEducationLOS los) {
        ParentLOSRef educationRef = new ParentLOSRef();
        educationRef.setId(los.getId());
        educationRef.setName(los.getName());
        educationRef.setLosType(TarjontaConstants.TYPE_KK);
        return educationRef;
    }
    
    private boolean fetchHakukohdeData(StandaloneLOS los, boolean checkStatus) throws KoodistoException {
        ResultV1RDTO<List<NimiJaOidRDTO>> hakukohteet = loiCreator.tarjontaRawService.getHakukohdesByEducationOid(los.getId());

        if (hakukohteet == null 
                || hakukohteet.getResult() == null 
                || hakukohteet.getResult().isEmpty()) {
            return false;
        }

        List<ApplicationOption> aos = new ArrayList<ApplicationOption>();

        for (NimiJaOidRDTO curHakukoh : hakukohteet.getResult()) {
            String aoId = curHakukoh.getOid();

            ResultV1RDTO<HakukohdeV1RDTO> hakukohdeRes = loiCreator.tarjontaRawService.getV1EducationHakukohode(aoId);
            HakukohdeV1RDTO hakukohdeDTO = hakukohdeRes.getResult();



            if (checkStatus && !hakukohdeDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                continue;
            }

            ResultV1RDTO<HakuV1RDTO> hakuRes = loiCreator.tarjontaRawService.getV1EducationHakuByOid(hakukohdeDTO.getHakuOid());

            HakuV1RDTO hakuDTO = hakuRes.getResult();

            if (checkStatus && !hakuDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                continue;
            }
            
            
            try {

                ApplicationOption ao = loiCreator.applicationOptionCreator.createV1EducationApplicationOption(los, hakukohdeDTO, hakuRes.getResult());
                //If fetching for preview, the status of the application option is added
                if (!checkStatus) {
                    ao.setStatus(hakukohdeDTO.getTila());
                    ao.getApplicationSystem().setStatus(hakuDTO.getTila());
                }
                aos.add(ao);
                
            } catch (Exception ex) {
                LOG.debug("Problem fetching ao: " + ex.getMessage());
            }

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
    
    public CompetenceBasedQualificationParentLOS createCBQPLOS(String parentKomoOid, List<String> komotoOids, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        
        CompetenceBasedQualificationParentLOS los = new CompetenceBasedQualificationParentLOS();
        
        List<Code> rawTranslCodes = new ArrayList<Code>();
        
        for (String curKomotoOid : komotoOids) {
            LOG.debug("Cur standalone competence komoto oid: " + curKomotoOid);
            ResultV1RDTO<AmmattitutkintoV1RDTO> res = this.tarjontaRawService.getAdultVocationalLearningOpportunity(curKomotoOid);
            NayttotutkintoV1RDTO dto = res.getResult();
            
            LOG.debug("Got dto ");
            
            if (dto == null || dto.getToteutustyyppi() == null || !dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name())) {
                LOG.debug("Unfitting komoto, continuing");
                continue;
            }
            LOG.debug("Toteutustyyppi: " + dto.getToteutustyyppi().name());
            LOG.debug("Ok, creating it");
            try {
                AdultVocationalLOS newLos = createAdultVocationalLOS(dto, checkStatus);
                
                if (los.getName() == null) {
                    //los.setName(newLos.getName());
                    los.setName(getI18nTextEnriched(dto.getKoulutuskoodi().getMeta()));
                }
                if (los.getGoals() == null
                        && dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null  
                                && !dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
                    los.setGoals(getI18nTextEnriched(dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
                    rawTranslCodes.addAll(koodistoService.searchMultiple(
                            this.getTranslationUris(dto.getKuvausKomo().get(KomoTeksti.TAVOITTEET))));
                    
                }
                if (los.getAccessToFurtherStudies() == null
                        && dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null  
                        && !dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
                    los.setAccessToFurtherStudies(getI18nTextEnriched(dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
                    rawTranslCodes.addAll(koodistoService.searchMultiple(
                            this.getTranslationUris(dto.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET))));
                }
                if (los.getChoosingCompetence() == null
                        && dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA) != null  
                        && !dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA).getTekstis().containsKey(UNDEFINED)) {
                    los.setChoosingCompetence(getI18nTextEnriched(dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA)));
                    rawTranslCodes.addAll(koodistoService.searchMultiple(
                            this.getTranslationUris(dto.getKuvausKomoto().get(KomotoTeksti.OSAAMISALAN_VALINTA))));
                }
                if (los.getDegreeCompletion() == null
                        && dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN) != null  
                        && !dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN).getTekstis().containsKey(UNDEFINED)) {
                    los.setDegreeCompletion(getI18nTextEnriched(dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN)));
                    rawTranslCodes.addAll(koodistoService.searchMultiple(
                            this.getTranslationUris(dto.getKuvausKomoto().get(KomotoTeksti.NAYTTOTUTKINNON_SUORITTAMINEN))));
                }
                if (los.getProvider() == null) {
                    los.setProvider(newLos.getProvider());
                }
                if (los.getId() == null) {
                    los.setId(String.format("%s_%s", parentKomoOid, los.getProvider().getId()));
                }
                if (los.getEducationDomain() == null) {
                    los.setEducationDomain(newLos.getEducationDomain());
                }
                if (los.getEducationKind() == null) {
                    los.setEducationKind(getI18nTextEnriched(dto.getKoulutuslaji().getMeta()));
                }
                
                newLos.setParent(new ParentLOSRef(los.getId(), los.getName()));
                if (los.getChildren() == null) {
                    los.setChildren(new ArrayList<AdultVocationalLOS>());
                }
                los.getChildren().add(newLos);
            } catch (TarjontaParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        
        if (los == null || los.getChildren() == null || los.getChildren().isEmpty()) {
            return null;
        }
        
        Map<String,Code> availableLanguagesMap = new HashMap<String,Code>();
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }
        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));
        
        Map<String,ApplicationOption> aoMap = new HashMap<String,ApplicationOption>();
        
        for (AdultVocationalLOS curChild: los.getChildren()) {
            if (curChild.getApplicationOptions() != null) { 
                for (ApplicationOption ao : curChild.getApplicationOptions()) {
                    aoMap.put(ao.getId(), ao);
                }
            }
        }
        
        
        if (!aoMap.isEmpty()) {
            los.setApplicationOptions(new ArrayList<ApplicationOption>(aoMap.values()));
        }
        
        return los;
        
    }

    public AdultVocationalLOS createAdultVocationalLOS(
            NayttotutkintoV1RDTO koulutus, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        
        LOG.debug("Creating adult vocational los: " + koulutus.getOid());
        
        
        
        AdultVocationalLOS los = new AdultVocationalLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);//TarjontaConstants.TYPE_ADULT_UPSEC);
        los.setId(koulutus.getOid());
        los.setKomoOid(koulutus.getKomoOid());
        los.setValmistavaKoulutus(koulutus.getValmistavaKoulutus() != null);
        

        //Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String,Code> availableLanguagesMap = new HashMap<String,Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();

        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));            
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)))); 
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)))); 
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null  
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }
        
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)))); 
        }
        
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null  
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)))); 
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
        los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
        LOG.debug("Koulutusohjelma for " + koulutus.getOid() + ": " + koulutus.getKoulutusohjelma());
        los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
        LOG.debug("Short title: " + los.getShortTitle());
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        los.setEducationType(getEducationType(koulutus.getKoulutusaste().getUri()));
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualifications(Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));
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
            
        if (koulutus.getValmistavaKoulutus() != null) {

            los.setPlannedDuration(koulutus.getValmistavaKoulutus().getSuunniteltuKestoArvo());
            los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getSuunniteltuKestoTyyppi().getMeta()));
            los.setPduCodeUri(koulutus.getValmistavaKoulutus().getSuunniteltuKestoTyyppi().getUri());
            los.setChargeable(koulutus.getValmistavaKoulutus().getOpintojenMaksullisuus());
            if (koulutus.getValmistavaKoulutus().getHinta() != null) {
                los.setCharge(koulutus.getValmistavaKoulutus().getHinta());
            }
            
            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA) != null  
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA).getTekstis().containsKey(UNDEFINED)) {
                los.setTargetGroup(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KOHDERYHMA)))); 
            }
            
            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN) != null  
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN).getTekstis().containsKey(UNDEFINED)) {
                los.setPersonalization(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.OPISKELUN_HENKILOKOHTAISTAMINEN)))); 
            }
            
            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO) != null  
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
                los.setContent(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO)))); 
            }
            
            if (koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN) != null  
                    && !koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
                los.setInternationalization(getI18nTextEnriched(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN)));
                rawTranslCodes.addAll(koodistoService.searchMultiple(
                        this.getTranslationUris(koulutus.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.KANSAINVALISTYMINEN)))); 
            }
            
            if (koulutus.getValmistavaKoulutus().getYhteyshenkilos() != null 
                    && !koulutus.getValmistavaKoulutus().getYhteyshenkilos().isEmpty()) {
                List<ContactPerson> persons = new ArrayList<ContactPerson>();
                for (YhteyshenkiloTyyppi yhteyshenkiloRDTO : koulutus.getValmistavaKoulutus().getYhteyshenkilos()) {
                    ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                            yhteyshenkiloRDTO.getSahkoposti(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                    if (yhteyshenkiloRDTO.getHenkiloTyyppi() != null) {
                        contactPerson.setType(yhteyshenkiloRDTO.getHenkiloTyyppi().name());
                    }
                    persons.add(contactPerson);
                }
                los.setPreparatoryContactPersons(persons);
            }
            
            
        }
        
        if (koulutus.getAmmattinimikkeet() != null) {
            los.setProfessionalTitles(getI18nTextMultiple(koulutus.getAmmattinimikkeet()));//getAmmattinimikeUris()));
        }
        
        
        los.setCreditValue(koulutus.getOpintojenLaajuusarvo().getArvo());
        los.setCreditUnit(getI18nTextEnriched(koulutus.getOpintojenLaajuusyksikko().getMeta()));
        

        try {
            Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
            los.setProvider(provider);
        } catch (Exception ex) {
            throw new KoodistoException("Problem reading organisaatio: " + ex.getMessage());
        }

        if (koulutus.getOpintoala() != null) {
            los.setTopics(getTopics(koulutus.getOpintoala().getUri()));
            los.setThemes(getThemes(los));
        }

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));        

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
                //ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_ADULT_UPSEC);
            }
        }

        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));

        return los;

    }

}
