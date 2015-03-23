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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.BasicLOS;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ContactPerson;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.InstantiatedLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.LanguageSelection;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.StandaloneLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusGenericV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusValmentavaJaKuntouttavaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * @author Hannu Lyytikainen
 */
public class LOSObjectCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(LOSObjectCreator.class);

    private static final String UNDEFINED = "undefined";

    private KoodistoService koodistoService;
    private ProviderService providerService;
    private LOIObjectCreator loiCreator;
    private TarjontaRawService tarjontaRawService;

    public LOSObjectCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService,
                            ProviderService providerService, OrganisaatioRawService organisaatioRawService, ParameterService parameterService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaRawService = tarjontaRawService;
        this.loiCreator = new LOIObjectCreator(koodistoService, tarjontaRawService, organisaatioRawService, parameterService);

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
        LOG.debug("Creating provider specific parent (" + providerId + ") LOS from komo: " + parentKomo.getOid());

        ParentLOS parentLOS = createBasicLOS(ParentLOS.class, parentKomo, providerId);
        parentLOS.setType(TarjontaConstants.TYPE_PARENT);

        // los info
        parentLOS.setId(CreatorUtil.resolveLOSId(parentKomo.getOid(), providerId));
        Code name = koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri());
        parentLOS.setName(name.getName());
        parentLOS.setShortTitle(name.getShortTitle());
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
        Code name = getNameFromKomotoDTOs(childKomo.getKoulutusOhjelmaKoodiUri(), childKomo.getKoulutusKoodiUri(), childKomotos);
        if (name != null) {
            childLOS.setName(name.getName());
            childLOS.setShortTitle(name.getShortTitle());
        }
        childLOS.setQualification(koodistoService.searchFirstName(childKomo.getTutkintonimikeUri()));
        childLOS.setQualifications(getQualificationsFromKomotoDTOs(childKomotos));
        childLOS.setGoals(getI18nText(childKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        childLOS.setPseudo(childKomo.isPseudo());

        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
        childLOS.setLois(loiCreator.createChildLOIs(childKomotos, childLOS.getId(), childLOS.getName(), educationCodeUri, SolrConstants.ED_TYPE_AMMATILLINEN_SHORT));

        return childLOS;
    }

    // If childkomo contains mixed komotos with koulutusohjelmauri and osaamisalauri, the childkomo has only koulutusohjelmauri.
    // Therefore we must check if any of the childkomotos have a osaamisalauri that can be used as name
    private Code getNameFromKomotoDTOs(String komoKoulutusOhjelmakoodiUri, String komoKoulutusKoodiUri, List<KomotoDTO> childKomotos) throws KoodistoException {
        Code name = null;
        for (KomotoDTO koulutus : childKomotos) {
            if (koulutus.getKoulutusohjelmaUri() != null && koulutus.getKoulutusohjelmaUri().contains("osaamisala")) {
                if (name == null)
                    name = koodistoService.searchFirst(koulutus.getKoulutusohjelmaUri());
            }
        }
        if (name == null)
            name = koodistoService.searchFirst(komoKoulutusOhjelmakoodiUri);
        if (name == null)
            name = koodistoService.searchFirst(komoKoulutusKoodiUri);
        return name;
    }

    private List<I18nText> getQualificationsFromKomotoDTOs(List<KomotoDTO> komotoDTOs) throws KoodistoException {
        List<I18nText> qualifications = new ArrayList<I18nText>();
        for (String tutkintonimikeUri : getTutkintonimikeUris(komotoDTOs)) {
            qualifications.add(koodistoService.searchFirstName(tutkintonimikeUri));
        }
        return qualifications;
    }

    private Set<String> getTutkintonimikeUris(List<KomotoDTO> komotoDTOs) {
        Set<String> tutkintonimikeUris = new HashSet<String>();
        for (KomotoDTO komotoDTO : komotoDTOs) {
            if (CreatorUtil.komotoPublished.apply(komotoDTO)) {
                tutkintonimikeUris.addAll(new HashSet<String>(komotoDTO.getTutkintonimikeUris()));
            }
        }
        return tutkintonimikeUris;
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
        if ((!los.getType().equals(TarjontaConstants.TYPE_PREP) && !los.getType().equals(TarjontaConstants.TYPE_SPECIAL))
                || (TarjontaConstants.KANSANOPISTO_TYPE.equals(los.getEducationTypeUri()) && childKomoto.getKoulutusohjelmanNimi() != null)) {
            if (childKomoto.getKoulutusohjelmanNimi() != null) {
                Map<String, String> nameTranslations = Maps.newHashMap();
                nameTranslations.put(teachingLang, childKomoto.getKoulutusohjelmanNimi());
                los.setName(new I18nText(nameTranslations));
                los.setShortTitle(new I18nText(nameTranslations));
            } else {
                Code name = koodistoService.searchFirst(childKomoto.getKoulutusKoodiUri());
                los.setName(name.getName());
                los.setShortTitle(name.getShortTitle());
            }
        } else if (los.getType().equals(TarjontaConstants.TYPE_SPECIAL)) {
            Code name = koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri());
            los.setName(name.getName());
            los.setShortTitle(name.getShortTitle());
            Code subName = koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri());
            los.setSubName(subName.getName());
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

            if (childKomoto.getKoulutuslajiUris() != null
                    && !childKomoto.getKoulutuslajiUris().isEmpty()
                    && childKomoto.getKoulutuslajiUris().get(0).contains(TarjontaConstants.AIKUISKOULUTUS)) {

                loi.setTargetGroup(getI18nText(childKomoto.getTekstit().get(KomotoTeksti.KOHDERYHMA)));

            }

            lois.add(loi);
        }
        if (!lois.isEmpty()
                && los.getType().equals(TarjontaConstants.TYPE_PREP)
                && (!TarjontaConstants.KANSANOPISTO_TYPE.equals(los.getEducationTypeUri())
                || (TarjontaConstants.KANSANOPISTO_TYPE.equals(los.getEducationTypeUri())
                && childKomoto.getKoulutusohjelmanNimi() == null))) {
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
        } else {
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

        Code name = los.getType().equals(TarjontaConstants.TYPE_SPECIAL)
                ? koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri())
                : koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri());
        
        los.setName(name.getName());
        los.setShortTitle(name.getShortTitle());
        if (los.getType().equals(TarjontaConstants.TYPE_SPECIAL)) {
            if(childKomo.getKoulutusOhjelmaKoodiUri() == null || childKomo.getKoulutusOhjelmaKoodiUri().isEmpty()){
                LOG.debug("ChildKomo " + childKomo.getOid() + " contained empty koulutusOhjelmaKoodiUri. Subname not added.");
            } else {
                Code subName = getNameFromKomotoDTOs(childKomo.getKoulutusOhjelmaKoodiUri(), childKomo.getKoulutusKoodiUri(), childKomotos);
                los.setSubName(subName.getName());
            }
        }
        los.setQualification(koodistoService.searchFirstName(childKomo.getTutkintonimikeUri()));
        los.setQualifications(getQualificationsFromKomotoDTOs(childKomotos));
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
        ChildLOI latesChild = los.getLatestLoi();
        if(latesChild != null){
            los.setCreditValue(latesChild.getCreditValue());
            los.setCreditUnit(latesChild.getCreditUnit());
        } else {
            los.setCreditValue(parentKomo.getLaajuusArvo());
            los.setCreditUnit(koodistoService.searchFirstShortName(parentKomo.getLaajuusYksikkoUri()));
        }
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
        los.setShortTitle(name.getShortTitle());
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
        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutTeachingLangs(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET))));
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
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET).getTekstis().containsKey(UNDEFINED)) {
            los.setFinalExam(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstis().containsKey(UNDEFINED)) {
            los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN))));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS).getTekstis().containsKey(UNDEFINED)) {
            los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS))));
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
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET))));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS).getTekstis().containsKey(UNDEFINED)) {
            los.setInfoAboutCharge(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(
                    this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.MAKSULLISUUS))));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));//koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

        // fields used to resolve available translation languages
        // content, internationalization, cooperation
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }

        /*for (Code teachingLanguage : los.getTeachingLanguages()) {
            availableLanguagesMap.put(teachingLanguage.getUri(), teachingLanguage);
        }*/

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
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkinto().getMeta())); // muutos: oli koulutus.getKoulutusohjelma()
        los.setDegreeTitles(getI18nTextMultiple(koulutus.getTutkintonimikes())); // uusi
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

            for (String curTarjoaja : koulutus.getOpetusTarjoajat()) {
                if (!curTarjoaja.equals(provider.getId())) {
                    los.getAdditionalProviders().add(providerService.getByOID(curTarjoaja));
                }
            }
            
            /*
            if (koulutus.getOpetusTarjoajat() != null) {
                
            }*/

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
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_KK);
            }
        }

        los.setFacetPrerequisites(getFacetPrequisites(los.getPrerequisites()));

        return los;
    }

    public AdultUpperSecondaryLOS createAdultUpperSeconcaryLOS(KoulutusLukioV1RDTO koulutus, boolean checkStatus)
            throws TarjontaParseException, KoodistoException {

        AdultUpperSecondaryLOS los = new AdultUpperSecondaryLOS();

        los.setType(TarjontaConstants.TYPE_ADULT_UPSEC);
        los.setEducationType(SolrConstants.ED_TYPE_AIKUISLUKIO);
        addLOSFields(koulutus, los);
        addStandaloneLOSFields(koulutus, los, checkStatus, TarjontaConstants.TYPE_ADULT_UPSEC);

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT).getTekstis().containsKey("UNDEFINED")) {
            los.setSubjectsAndCourses(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.OPPIAINEET_JA_KURSSIT)));
        }

        if (koulutus.getKielivalikoima() != null) {//komoto.getTarjotutKielet() != null) {

            //Map<String, List<String>> kielivalikoimat = komoto.getTarjotutKielet();
            List<LanguageSelection> languageSelection = Lists.newArrayList();
            KoodiValikoimaV1RDTO kielivalikoima = koulutus.getKielivalikoima();
            //kielivalikoima.

            for (Map.Entry<String, KoodiUrisV1RDTO> oppiaine : kielivalikoima.entrySet()) {
                List<I18nText> languages = getI18nTextMultiple(oppiaine.getValue());//Lists.newArrayList();
                languageSelection.add(new LanguageSelection(oppiaine.getKey(), languages));
            }
            los.setLanguageSelection(languageSelection);
        }

        if (koulutus.getLukiodiplomit() != null) {
            los.setDiplomas(getI18nTextMultiple(koulutus.getLukiodiplomit()));
        }

        los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta()));
        los.setQualifications(Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));

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

        if (kandKoul != null
                && kandKoul.getUri() != null
                && kandKoul.getArvo() != null
                && !kandKoul.getArvo().equals(TarjontaConstants.KANDI_TUNTEMATON)) {

            kandQuals = this.koodistoService.searchSubCodes(kandKoul.getUri(), TarjontaConstants.TUTKINTONIMIKE_KK_KOODISTO_URI);
        }

        if (!kandQuals.isEmpty() && kandQuals.get(0).getName() != null) {
            qualifications.add(kandQuals.get(0).getName());
        }

        qualifications.addAll(getI18nTextMultiple(koulutus.getTutkintonimikes()));

        return qualifications;
    }

    //tutkintonimike
    private List<I18nText> getQualificationsForAikuAmm(NayttotutkintoV1RDTO koulutus) throws KoodistoException {

        List<I18nText> qualifications = new ArrayList<I18nText>();

        String osaamisalalUri = koulutus.getKoulutusohjelma().getUri();//getKandidaatinKoulutuskoodi();

        List<Code> quals = new ArrayList<Code>();

        if (osaamisalalUri != null) {

            quals = this.koodistoService.searchSubCodes(osaamisalalUri, TarjontaConstants.TUTKINTONIMIKEET_KOODISTO_URI);
        }

        if (quals != null && !quals.isEmpty()) {
            for (Code curQual : quals) {
                qualifications.add(curQual.getName());
            }
        } else if (koulutus.getTutkintonimike() != null && koulutus.getTutkintonimike().getMeta() != null) {
            qualifications.add(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta()));
        }
        return qualifications;
    }


    private <S extends StandaloneLOS> ParentLOSRef createParentLosRef(S los) {
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


            if (checkStatus && (hakukohdeDTO == null || hakukohdeDTO.getTila() == null || !hakukohdeDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString()))) {
                continue;
            }

            ResultV1RDTO<HakuV1RDTO> hakuRes = loiCreator.tarjontaRawService.getV1EducationHakuByOid(hakukohdeDTO.getHakuOid());

            HakuV1RDTO hakuDTO = hakuRes.getResult();

            if (checkStatus && (hakuDTO == null || hakuDTO.getTila() == null || !hakuDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString()))) {
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

        los.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);
        List<Code> rawTranslCodes = new ArrayList<Code>();

        for (String curKomotoOid : komotoOids) {
            LOG.debug("Cur standalone competence komoto oid: " + curKomotoOid);
            ResultV1RDTO<AmmattitutkintoV1RDTO> res = this.tarjontaRawService.getAdultVocationalLearningOpportunity(curKomotoOid);
            NayttotutkintoV1RDTO dto = res.getResult();

            LOG.debug("Got dto ");

            if (dto == null || dto.getToteutustyyppi() == null || !isAikuAmm(dto)) {
                LOG.debug("Unfitting komoto, continuing");
                try {
                    LOG.debug("Toteutustyyppi: " + dto.getToteutustyyppi().name());
                } catch (Exception ex) {
                    LOG.debug("Could not get toteutustyyppi: ");
                }
                continue;
            }
            LOG.debug("Toteutustyyppi: " + dto.getToteutustyyppi().name());
            LOG.debug("Ok, creating it");
            try {

                AdultVocationalLOS newLos = createAdultVocationalLOS(dto, checkStatus);

                LOG.debug("Updating parnet los data with dto: " + dto.getOid());

                updateParentLosData(los, rawTranslCodes, dto, parentKomoOid, newLos);
                if (los.getChildren() == null) {
                    los.setChildren(new ArrayList<AdultVocationalLOS>());
                }
                los.getChildren().add(newLos);

                newLos.setParent(new ParentLOSRef(los.getId(), los.getName()));


            } catch (TarjontaParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (los == null || los.getChildren() == null || los.getChildren().isEmpty()) {
            if (checkStatus) {
                throw new TarjontaParseException("No valid children for parent adult vocational: " + parentKomoOid);
            }
            return null;
        }

        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        for (Code curCode : rawTranslCodes) {
            availableLanguagesMap.put(curCode.getUri(), curCode);
        }
        los.setAvailableTranslationLanguages(new ArrayList<Code>(availableLanguagesMap.values()));

        Map<String, ApplicationOption> aoMap = new HashMap<String, ApplicationOption>();
        Map<String, Code> topicMap = new HashMap<String, Code>();
        Map<String, Code> themeMap = new HashMap<String, Code>();

        for (AdultVocationalLOS curChild : los.getChildren()) {
            if (curChild.getApplicationOptions() != null) {
                for (ApplicationOption ao : curChild.getApplicationOptions()) {
                    aoMap.put(ao.getId(), ao);
                }
            }
            for (Code curTopic : curChild.getTopics()) {
                topicMap.put(curTopic.getUri(), curTopic);
            }
            for (Code curTheme : curChild.getThemes()) {
                themeMap.put(curTheme.getUri(), curTheme);
            }
        }

        los.setTopics(new ArrayList<Code>(topicMap.values()));
        los.setThemes(new ArrayList<Code>(themeMap.values()));

        if (!aoMap.isEmpty()) {
            los.setApplicationOptions(new ArrayList<ApplicationOption>(aoMap.values()));
        }

        return los;
    }

    private void updateParentLosData(CompetenceBasedQualificationParentLOS los,
                                     List<Code> rawTranslCodes, NayttotutkintoV1RDTO dto,
                                     String parentKomoOid, AdultVocationalLOS newLos) throws KoodistoException {
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
            los.setId(dto.getOid());
        }
        if (los.getEducationDomain() == null) {
            los.setEducationDomain(newLos.getEducationDomain());
        }
        if (los.getEducationKind() == null) {
            los.setEducationKind(getI18nTextEnriched(dto.getKoulutuslaji().getMeta()));
        }
        if (los.getEducationType() == null) {
            los.setEducationType(getI18nTextEnriched(dto.getKoulutustyyppi().getMeta()));
            los.setEdtUri(dto.getKoulutustyyppi().getUri());
        }

        LOG.debug("setting charge with los: " + los.getId() + " and dto hinta: " + dto.getHinta());
        if (dto.getHinta() != null) {
            los.setCharge(dto.getHinta());
        }
        los.setChargeable(dto.getOpintojenMaksullisuus());
        los.setOsaamisala(!dto.getKoulutusmoduuliTyyppi().name().equals(KoulutusmoduuliTyyppi.TUTKINTO.name()));
        los.setDeterminer(dto.getTarkenne());

    }


    private boolean isAikuAmm(NayttotutkintoV1RDTO dto) {
        return (dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name())
                || dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.AMMATTITUTKINTO.name())
                || dto.getToteutustyyppi().name().startsWith(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO.name()))
                && dto.getKoulutuslaji().getUri().startsWith("koulutuslaji_a");
    }

    public StandaloneLOS createValmaLOS(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Valma los: " + koulutusDTO.getOid());
        StandaloneLOS los = new StandaloneLOS();
        los.setType(TarjontaConstants.TYPE_KOULUTUS);
        los.setEducationType(SolrConstants.ED_TYPE_VALMA);
        addLOSFields(koulutusDTO, los);
        addStandaloneLOSFields(koulutusDTO, los, checkStatus, TarjontaConstants.TYPE_KOULUTUS);
        addDatabaseValuesForNamesAndCreditValue(koulutusDTO, los);
        return los;
    }

    public StandaloneLOS createValmaLOSEr(ValmistavaKoulutusV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Valma Er los: " + koulutusDTO.getOid());
        StandaloneLOS los = new StandaloneLOS();
        los.setType(TarjontaConstants.TYPE_KOULUTUS);
        los.setEducationType(SolrConstants.ED_TYPE_VALMA_ER);
        addLOSFields(koulutusDTO, los);
        addStandaloneLOSFields(koulutusDTO, los, checkStatus, TarjontaConstants.TYPE_KOULUTUS);
        addDatabaseValuesForNamesAndCreditValue(koulutusDTO, los);
        return los;
    }

    public StandaloneLOS createTelmaLOS(KoulutusValmentavaJaKuntouttavaV1RDTO koulutusDTO, boolean checkStatus) throws TarjontaParseException, KoodistoException {
        LOG.debug("Creating Telma los: " + koulutusDTO.getOid());
        StandaloneLOS los = new StandaloneLOS();
        los.setType(TarjontaConstants.TYPE_KOULUTUS);
        los.setEducationType(SolrConstants.ED_TYPE_TELMA);
        addLOSFields(koulutusDTO, los);
        addStandaloneLOSFields(koulutusDTO, los, checkStatus, TarjontaConstants.TYPE_KOULUTUS);
        addDatabaseValuesForNamesAndCreditValue(koulutusDTO, los);
        return los;
    }

    private void addDatabaseValuesForNamesAndCreditValue(ValmistavaKoulutusV1RDTO koulutusDTO, StandaloneLOS los) {
        if (koulutusDTO.getKoulutusohjelmanNimiKannassa() != null) {
            los.setName(new I18nText(koulutusDTO.getKoulutusohjelmanNimiKannassa()));
            los.setShortTitle(new I18nText(koulutusDTO.getKoulutusohjelmanNimiKannassa()));
        }
        if (koulutusDTO.getOpintojenLaajuusarvoKannassa() != null) {
            los.setCreditValue(koulutusDTO.getOpintojenLaajuusarvoKannassa());
        }
    }
    
    private <S extends KoulutusV1RDTO, T extends LOS> void addLOSFields(S koulutus, T los) throws KoodistoException {
        los.setId(koulutus.getOid());
        if (koulutus instanceof KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO) { // Valma
            los.setName(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
            los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        } else {
            los.setName(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
            los.setShortTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma().getMeta()));
        }
        if (koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().containsKey(UNDEFINED)) {
            los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
        }
        if (koulutus.getOpintoala() != null) {
            los.setTopics(getTopics(koulutus.getOpintoala().getUri()));
            los.setThemes(getThemes(los));
        }
    }

    private <S extends KoulutusGenericV1RDTO, T extends StandaloneLOS> void addStandaloneLOSFields(S koulutus, T los, boolean checkStatus, String aoType)
            throws KoodistoException, TarjontaParseException {
        los.setKomoOid(koulutus.getKomoOid());

        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();

        List<Code> rawTranslCodes = new ArrayList<Code>();

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO).getTekstis().containsKey(UNDEFINED)) {
            los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstis().containsKey(UNDEFINED)) {
            los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN).getTekstis().containsKey(UNDEFINED)) {
            los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN))));
        }
        if (koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTekstis().containsKey(UNDEFINED)) {
            los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
            rawTranslCodes.addAll(koodistoService.searchMultiple(this.getTranslationUris(koulutus.getKuvausKomoto().get(
                    KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA))));
        }

        if (koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET) != null
                && !koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstis().containsKey(UNDEFINED)) {
            los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
        }

        if (koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA) != null
                && !koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA).getTekstis().containsKey(UNDEFINED)) {
            los.setTargetGroup(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KOHDERYHMA)));
        }

        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));// koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));

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
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());
        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
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

        los.setFormOfTeaching(getI18nTextMultiple(koulutus.getOpetusmuodos()));
        los.setFotFacet(this.createCodes(koulutus.getOpetusPaikkas()));
        los.setTimeOfTeachingFacet(this.createCodes(koulutus.getOpetusAikas()));
        los.setFormOfStudyFacet(this.createCodes(koulutus.getOpetusmuodos()));

        los.setTeachingTimes(getI18nTextMultiple(koulutus.getOpetusAikas()));
        los.setTeachingPlaces(getI18nTextMultiple(koulutus.getOpetusPaikkas()));

        // If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && !fetchHakukohdeData(los, checkStatus)) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setParent(createParentLosRef(los));
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                ao.setType(aoType);
            }
        }
        los.getPrerequisites().addAll(koodistoService.search(koulutus.getPohjakoulutusvaatimus().getUri()));
        List<Code> facetPrequisites = this.getFacetPrequisites(los.getPrerequisites());
        los.setFacetPrerequisites(facetPrequisites);
        los.setStartDates(Lists.newArrayList(koulutus.getKoulutuksenAlkamisPvms()));
    }
    
    public AdultVocationalLOS createAdultVocationalLOS(NayttotutkintoV1RDTO koulutus, boolean checkStatus) throws TarjontaParseException, KoodistoException {

        LOG.debug("Creating adult vocational los: " + koulutus.getOid());

        AdultVocationalLOS los = new AdultVocationalLOS();

        addLOSFields(koulutus, los);
        
        los.setStatus(koulutus.getTila().toString());

        los.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);//TarjontaConstants.TYPE_ADULT_UPSEC);
        los.setKomoOid(koulutus.getKomoOid());
        los.setValmistavaKoulutus(koulutus.getValmistavaKoulutus() != null);
        if (koulutus.getToteutustyyppi().name().equals(ToteutustyyppiEnum.AMMATTITUTKINTO.name())) {
            los.setEducationType(SolrConstants.ED_TYPE_AMM_TUTK);
        } else if (koulutus.getToteutustyyppi().name().equals(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO.name())) {
            los.setEducationType(SolrConstants.ED_TYPE_AMM_ER);
        } else {
            los.setEducationType(SolrConstants.ED_TYPE_AMMATILLINEN);
        }

        //Set<Code> availableLanguagaes = Sets.newHashSet();
        Map<String, Code> availableLanguagesMap = new HashMap<String, Code>();
        List<Code> rawTranslCodes = new ArrayList<Code>();

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
        los.setKoulutuskoodi(getI18nTextEnriched(koulutus.getKoulutuskoodi().getMeta()));
        los.setEducationCode(koodistoService.searchFirst(koulutus.getKoulutuskoodi().getUri()));
        los.setEducationDegree(koulutus.getKoulutusaste().getUri());

        los.setEducationDegreeLang(getI18nTextEnriched(koulutus.getKoulutusaste().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())); // muutos: oli koulutus.getKoulutusohjelma()
//      los.setDegreeTitles(getI18nTextMultiple(koulutus.getTutkintonimikes())); // ei löydy NayttotutkintoV1RDTO:lle
        los.setQualifications(getQualificationsForAikuAmm(koulutus));//Arrays.asList(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta())));


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

            los.setFormOfTeaching(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusmuodos()));
            los.setFotFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusPaikkas()));
            los.setTimeOfTeachingFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusAikas()));
            los.setFormOfStudyFacet(this.createCodes(koulutus.getValmistavaKoulutus().getOpetusmuodos()));
            los.setTeachingTimes(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusAikas()));
            los.setTeachingPlaces(getI18nTextMultiple(koulutus.getValmistavaKoulutus().getOpetusPaikkas()));

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

        try {
            Provider organizer = providerService.getByOID(koulutus.getJarjestavaOrganisaatio().getOid());
            los.setOrganizer(organizer.getName());


        } catch (Exception ex) {
            throw new KoodistoException("Problem reading jarjestava organisaatio: " + ex.getMessage());
        }


        boolean existsValidHakukohde = fetchHakukohdeData(los, checkStatus);

        //If we are not fetching for preview, an exception is thrown if no valid application options exist
        if (checkStatus && !existsValidHakukohde) {
            throw new TarjontaParseException("No valid application options for education: " + los.getId());
        }
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption ao : los.getApplicationOptions()) {
                ao.setProvider(los.getProvider());
                ao.setEducationDegree(los.getEducationDegree());
                los.getProvider().getApplicationSystemIds().add(ao.getApplicationSystem().getId());
                //ao.setParent(createParentLosRef(los));
                ao.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);//TarjontaConstants.TYPE_ADULT_UPSEC);
            }
        }

        if (koulutus.getKoulutuslaji() != null) {
            los.setKoulutuslaji(this.koodistoService.searchFirst(koulutus.getKoulutuslaji().getUri()));
        }

        los.setFacetPrerequisites(this.getFacetPrequisites(los.getPrerequisites()));

        return los;

    }

    public CalendarApplicationSystem createApplicationSystemForCalendar(HakuV1RDTO hakuDTO, boolean shownInCalendar) throws KoodistoException {
        return this.loiCreator.applicationOptionCreator.getApplicationSystemCreator().createApplicationSystemForCalendar(hakuDTO, shownInCalendar);
    }

}
