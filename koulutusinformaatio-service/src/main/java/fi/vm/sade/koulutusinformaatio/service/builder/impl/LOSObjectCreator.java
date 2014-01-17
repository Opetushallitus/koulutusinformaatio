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

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
            loi.setSelectingDegreeProgram(getI18nText(komoto.getKoulutusohjelmanValinta()));
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
        List<ChildLOI> childLOIs = Lists.newArrayList();

        for (KomotoDTO childKomoto : childKomotos) {
            String childKomotoOid = childKomoto.getOid();
            LOG.debug(Joiner.on(" ").join("Resolving child learning opportunity:", childKomotoOid));

            if (!CreatorUtil.komotoPublished.apply(childKomoto)) {
                LOG.debug(String.format("Skipping child non published child komoto %s", childKomoto.getOid()));
                continue;
            }

            // strip version out of education code uri
            String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];
            ChildLOI childLOI = loiCreator.createChildLOI(childKomoto, childLOS.getId(), childLOS.getName(), educationCodeUri);
            if (!childLOI.getApplicationOptions().isEmpty()) {
                childLOIs.add(childLOI);
            }
        }
        childLOS.setLois(childLOIs);
        return childLOS;
    }

    public SpecialLOS createSpecialLOS(KomoDTO childKomo, KomoDTO parentKomo, String specialLOSId,
                                       List<KomotoDTO> childKomotos, String providerOid) throws KoodistoException {
        SpecialLOS los = new SpecialLOS();
        if (childKomo.getKoulutusTyyppiUri().equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE)) {
            los.setType(TarjontaConstants.TYPE_REHAB);
        }
        else {
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

        List<ChildLOI> lois = Lists.newArrayList();

        // strip version out of education code uri
        String educationCodeUri = childKomo.getKoulutusKoodiUri().split("#")[0];

        for (KomotoDTO komoto : childKomotos) {
            if (CreatorUtil.komotoPublished.apply(komoto)) {
                ChildLOI loi = loiCreator.createChildLOI(komoto,specialLOSId, los.getName(), educationCodeUri);
                lois.add(loi);
            }
        }
        los.setLois(lois);
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

        Map<String,String> komoTavoitteet = komo.getTekstit().get(KomoTeksti.TAVOITTEET);
        if (komoTavoitteet == null) {
            los.setGoals(getI18nText(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        } else {
            los.setGoals(getI18nText(komoTavoitteet));
        }

        // strip version out of education code uri
        String educationCodeUri = komo.getKoulutusKoodiUri().split("#")[0];

        List<UpperSecondaryLOI> lois = Lists.newArrayList();
        for (KomotoDTO komoto : komotos) {
            if (CreatorUtil.komotoPublished.apply(komoto)) {
                lois.add(loiCreator.createUpperSecondaryLOI(komoto, losID, los.getName(), educationCodeUri));
            }
        }

        los.setLois(lois);

        return los;
    }
    
    public UniversityAppliedScienceLOS createUasLOS(KoulutusKorkeakouluV1RDTO koulutus) throws KoodistoException {
    	
    	UniversityAppliedScienceLOS los = new UniversityAppliedScienceLOS();
    	los.setId(koulutus.getOid());
    	los.setInfoAboutTeachingLangs(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA)));
    	los.setGoals(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.TAVOITTEET)));
    	los.setContent(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SISALTO)));
    	los.setMajorSelection(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.PAAAINEEN_VALINTA)));
    	los.setStructure(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.KOULUTUKSEN_RAKENNE)));
    	los.setFinalExam(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.LOPPUKOEVAATIMUKSET)));
    	los.setCareerOpportunities(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN)));
    	los.setCompetence(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.PATEVYYS)));
    	los.setInternationalization(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.KANSAINVALISTYMINEN)));
    	los.setCooperation(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA)));
    	los.setResearchFocus(getI18nTextEnriched(koulutus.getKuvausKomoto().get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET)));
    	los.setAccessToFurtherStudies(getI18nTextEnriched(koulutus.getKuvausKomo().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET)));
    	
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
        los.setEducationDegree(getI18nTextEnriched(koulutus.getKoulutusala().getMeta()));//getTutkinto().getMeta()));
        los.setDegreeTitle(getI18nTextEnriched(koulutus.getKoulutusohjelma()));
        los.setQualification(getI18nTextEnriched(koulutus.getTutkintonimike().getMeta()));
        los.setDegree(getI18nTextEnriched(koulutus.getTutkinto().getMeta()));
        
        los.setStartDate(koulutus.getKoulutuksenAlkamisPvms().iterator().next());
        //los.setFormOfEducation(getMultiI18nTexts(koulutus.getOpetusmuodos()));
        los.setPlannedDuration(koulutus.getSuunniteltuKestoArvo());
        los.setPlannedDurationUnit(getI18nTextEnriched(koulutus.getSuunniteltuKestoTyyppi().getMeta()));
        los.setPduCodeUri(koulutus.getSuunniteltuKestoTyyppi().getUri());//childKomoto.getLaajuusYksikkoUri());
        los.setCreditValue(koulutus.getOpintojenLaajuus().getArvo());
        los.setChargeable(koulutus.getOpintojenMaksullisuus()); 
        
        los.setTeachingLanguages(createCodes(koulutus.getOpetuskielis()));//koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
        //childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
        Provider provider = providerService.getByOID(koulutus.getOrganisaatio().getOid());
        los.setProvider(provider);
        
        fetchHakukohdeData(los);
        //educationDegree = koulutusaste Ok
        //qualification = tutkintonimike Ok
        //degreeTitle = koulutusohjelma Ok  
        
        //Puuttuu vielä (datan rikastus pielessä)
        //formOfEducation = opetusmuoto
        //setProfessionalTitles = ammattinimikkeet
        
        
        
    	return los;
    }


    private List<Code> createCodes(KoodiUrisV1RDTO opetuskielis) throws KoodistoException {
    	List<Code> codes = new ArrayList<Code>();
    	if (opetuskielis != null && opetuskielis.getMeta() != null) {
			
			for (KoodiV1RDTO curKoodi : opetuskielis.getMeta().values()) {
				codes.addAll(koodistoService.searchCodes(curKoodi.getUri()));
			}
			
			
		}
		return codes;
	}

	private void fetchHakukohdeData(UniversityAppliedScienceLOS los) throws KoodistoException {
		 ResultV1RDTO<List<NimiJaOidRDTO>> hakukohteet = loiCreator.tarjontaRawService.getHakukohdesByHigherEducation(los.getId());
		
		 if (hakukohteet == null 
				 || hakukohteet.getResult() == null 
				 || hakukohteet.getResult().isEmpty()) {
			 return;
		 }
		 
		 
		 for (NimiJaOidRDTO curHakukoh : hakukohteet.getResult()) {
			    String aoId = curHakukoh.getOid();
			    
			    ResultV1RDTO<HakukohdeV1RDTO> hakukohdeRes = loiCreator.tarjontaRawService.getHigherEducationHakukohode(aoId);
			    HakukohdeV1RDTO hakukohde = hakukohdeRes.getResult();
			    ResultV1RDTO<HakuV1RDTO> hakuRes = loiCreator.tarjontaRawService.getHigherEducationHakuByOid(hakukohde.getHakuOid());
			    ApplicationOption ao = loiCreator.applicationOptionCreator.createHigherEducationApplicationOption(los, hakukohde, hakuRes.getResult());
	            /*HakukohdeDTO hakukohdeDTO = tarjontaRawService.getHakukohde(aoId);
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
	            }*/
		 }
		 
		 
	}



}
