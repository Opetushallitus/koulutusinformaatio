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
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
        los.setCreditValue(parentKomo.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));

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
}