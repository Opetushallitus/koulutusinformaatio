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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionCreator extends ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationOptionCreator.class);

    private KoodistoService koodistoService;
    private TarjontaRawService tarjontaRawService;
    private EducationObjectCreator educationObjectCreator;

    protected ApplicationOptionCreator(KoodistoService koodistoService, TarjontaRawService tarjontaRawService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.tarjontaRawService = tarjontaRawService;
        this.educationObjectCreator = new EducationObjectCreator(koodistoService);

    }

    public ApplicationOption createVocationalApplicationOption(HakukohdeDTO hakukohdeDTO, HakuDTO hakuDTO,
                                                               KomotoDTO childKomoto, Code prerequisite, String educationCodeUri) throws KoodistoException {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(hakukohdeDTO.getOid());
        ao.setName(koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
        ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
        ao.setAthleteEducation(isAthleteEducation(ao.getAoIdentifier()));
        ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
        ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
        ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
        ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
        ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
        ao.setSelectionCriteria(getI18nText(hakukohdeDTO.getValintaperustekuvaus()));
        ao.setExams(educationObjectCreator.createVocationalExams(hakukohdeDTO.getValintakoes()));
        ao.setKaksoistutkinto(hakukohdeDTO.isKaksoisTutkinto());
        ao.setVocational(true);
        ao.setEducationCodeUri(educationCodeUri);
        List<Code> subCodes = koodistoService.searchSubCodes(childKomoto.getPohjakoulutusVaatimusUri(),
                TarjontaConstants.BASE_EDUCATION_KOODISTO_URI);
        List<String> baseEducations = Lists.transform(subCodes, new Function<Code, String>() {
            @Override
            public String apply(Code code) {
                return code.getValue();
            }
        });
        ao.setRequiredBaseEducations(baseEducations);

        ApplicationSystem as = new ApplicationSystem();
        as.setId(hakuDTO.getOid());
        as.setName(getI18nText(hakuDTO.getNimi()));
        if (hakuDTO.getHakuaikas() != null) {
            for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);
            }
        }
        ao.setApplicationSystem(as);
        if (!Strings.isNullOrEmpty(hakukohdeDTO.getSoraKuvausKoodiUri())) {
            ao.setSora(true);
        }

        ao.setTeachingLanguages(koodistoService.searchCodeValuesMultiple(childKomoto.getOpetuskieletUris()));
        ao.setPrerequisite(prerequisite);
        ao.setSpecificApplicationDates(hakukohdeDTO.isKaytetaanHakukohdekohtaistaHakuaikaa());
        if (ao.isSpecificApplicationDates()) {
            ao.setApplicationStartDate(hakukohdeDTO.getHakuaikaAlkuPvm());
            ao.setApplicationEndDate(hakukohdeDTO.getHakuaikaLoppuPvm());
        }

        ao.setAttachmentDeliveryAddress(educationObjectCreator.createAddress(hakukohdeDTO.getLiitteidenToimitusosoite()));

        List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
        if (hakukohdeDTO.getLiitteet() != null && !hakukohdeDTO.getLiitteet().isEmpty()) {
            for (HakukohdeLiiteDTO liite : hakukohdeDTO.getLiitteet()) {
                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getErapaiva());
                attach.setType(koodistoService.searchFirst(liite.getLiitteenTyyppiUri()));
                attach.setDescreption(getI18nText(liite.getKuvaus()));
                attach.setAddress(educationObjectCreator.createAddress(liite.getToimitusosoite()));
                attachments.add(attach);
            }
        }
        ao.setAttachments(attachments);
        ao.setAdditionalInfo(getI18nText(hakukohdeDTO.getLisatiedot()));

        // set child loi names to application option
        List<OidRDTO> komotosByHakukohdeOID = tarjontaRawService.getKomotosByHakukohde(hakukohdeDTO.getOid());
        for (OidRDTO s : komotosByHakukohdeOID) {
            KomoDTO komoByKomotoOID = tarjontaRawService.getKomoByKomoto(s.getOid());

            if (not(
                    and(
                            CreatorUtil.komoPublished,
                            CreatorUtil.komoHasKoulutusohjelmaKoodi,
                            CreatorUtil.komoHasTutkintonimike
                    )
            ).apply(komoByKomotoOID)) {
                LOG.debug(String.format("Skipping invalid child komo %s", komoByKomotoOID.getOid()));
                continue;
            }

            KomotoDTO k = tarjontaRawService.getKomoto(s.getOid());
            if (not(CreatorUtil.komotoPublished).apply(k)) {
                LOG.debug(String.format("Skipping invalid child komoto %s", k.getOid()));
                continue;
            }

            ChildLOIRef cRef = new ChildLOIRef();
            cRef.setId(s.getOid());
            cRef.setLosId(CreatorUtil.resolveLOSId(komoByKomotoOID.getOid(), childKomoto.getTarjoajaOid()));
            cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getKoulutusOhjelmaKoodiUri()));
            cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
            cRef.setPrerequisite(prerequisite);
            ao.getChildLOIRefs().add(cRef);
        }
        return ao;
    }

    private boolean isAthleteEducation(final String aoIdentifier) {
        if (!Strings.isNullOrEmpty(aoIdentifier)) {
            List<Code> superCodes = null;
            try {
                superCodes = koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                        APPLICATION_OPTIONS_KOODISTO_URI);
            } catch (KoodistoException e) {
                throw new KIConversionException("Conversion failed - " + e.getMessage());
            }
            if (superCodes != null) {
                for (Code code : superCodes) {
                    if (aoIdentifier.equals(code.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ApplicationOption createUpperSecondaryApplicationOption(HakukohdeDTO hakukohdeDTO, HakuDTO hakuDTO,
                                                      KomotoDTO komoto, UpperSecondaryLOI loi, String educationCodeUri) throws KoodistoException {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(hakukohdeDTO.getOid());
        ao.setName(koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
        ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
        ao.setAthleteEducation(isAthleteEducation(ao.getAoIdentifier()));
        ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
        ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
        ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
        ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
        ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
        ao.setSelectionCriteria(getI18nText(hakukohdeDTO.getValintaperustekuvaus()));
        ao.setKaksoistutkinto(hakukohdeDTO.isKaksoisTutkinto());
        ao.setExams(educationObjectCreator.createUpperSecondaryExams(hakukohdeDTO.getValintakoes()));
        ao.setVocational(false);
        ao.setEducationCodeUri(educationCodeUri);
        List<Code> subCodes = koodistoService.searchSubCodes(komoto.getPohjakoulutusVaatimusUri(),
                TarjontaConstants.BASE_EDUCATION_KOODISTO_URI);
        List<String> baseEducations = Lists.transform(subCodes, new Function<Code, String>() {
            @Override
            public String apply(Code code) {
                return code.getValue();
            }
        });
        ao.setRequiredBaseEducations(baseEducations);

        ApplicationSystem as = new ApplicationSystem();
        as.setId(hakuDTO.getOid());
        as.setName(getI18nText(hakuDTO.getNimi()));
        if (hakuDTO.getHakuaikas() != null) {
            for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);
            }
        }
        ao.setApplicationSystem(as);
        if (!Strings.isNullOrEmpty(hakukohdeDTO.getSoraKuvausKoodiUri())) {
            ao.setSora(true);
        }

        ao.setTeachingLanguages(koodistoService.searchCodeValuesMultiple(komoto.getOpetuskieletUris()));
        ao.setPrerequisite(loi.getPrerequisite());
        ao.setSpecificApplicationDates(hakukohdeDTO.isKaytetaanHakukohdekohtaistaHakuaikaa());
        if (ao.isSpecificApplicationDates()) {
            ao.setApplicationStartDate(hakukohdeDTO.getHakuaikaAlkuPvm());
            ao.setApplicationEndDate(hakukohdeDTO.getHakuaikaLoppuPvm());
        }

        if (hakukohdeDTO.getLiitteidenToimitusosoite() != null) {
            OsoiteRDTO addressDTO = hakukohdeDTO.getLiitteidenToimitusosoite();
            ao.setAttachmentDeliveryAddress(educationObjectCreator.createAddress(addressDTO));
        }

        ao.setAdditionalProof(educationObjectCreator.createAdditionalProof(hakukohdeDTO.getValintakoes()));
        if (hakukohdeDTO.getValintakoes() != null) {
            for (ValintakoeRDTO valintakoeRDTO : hakukohdeDTO.getValintakoes()) {
                ao.setOverallScoreLimit(educationObjectCreator.resolvePointLimit(valintakoeRDTO, "Kokonaispisteet"));
            }
        }

        List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
        if (hakukohdeDTO.getLiitteet() != null && !hakukohdeDTO.getLiitteet().isEmpty()) {
            for (HakukohdeLiiteDTO liite : hakukohdeDTO.getLiitteet()) {
                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getErapaiva());
                attach.setType(koodistoService.searchFirst(liite.getLiitteenTyyppiUri()));
                attach.setDescreption(getI18nText(liite.getKuvaus()));
                attach.setAddress(educationObjectCreator.createAddress(liite.getToimitusosoite()));
                attachments.add(attach);
            }
        }
        ao.setAttachments(attachments);

        if (hakukohdeDTO.getPainotettavatOppiaineet() != null) {
            List<EmphasizedSubject> emphasizedSubjects = Lists.newArrayList();
            List<List<String>> painotettavat = hakukohdeDTO.getPainotettavatOppiaineet();
            for (List<String> painotettava : painotettavat) {
                emphasizedSubjects.add(
                        new EmphasizedSubject(koodistoService.searchFirst(painotettava.get(0)), painotettava.get(1)));
            }
            ao.setEmphasizedSubjects(emphasizedSubjects);
        }

        ao.setAdditionalInfo(getI18nText(hakukohdeDTO.getLisatiedot()));

        // set child loi names to application option
        List<OidRDTO> komotosByHakukohdeOID = tarjontaRawService.getKomotosByHakukohde(hakukohdeDTO.getOid());
        for (OidRDTO s : komotosByHakukohdeOID) {
            KomoDTO komoByKomotoOID = tarjontaRawService.getKomoByKomoto(s.getOid());

            if (!CreatorUtil.komoPublished.apply(komoByKomotoOID)) {
                continue;
            }

            KomotoDTO k = tarjontaRawService.getKomoto(s.getOid());
            if (not(CreatorUtil.komotoPublished).apply(k)) {
                LOG.debug(String.format("Skipping invalid child komoto %s", k.getOid()));
                continue;
            }

            ChildLOIRef cRef = new ChildLOIRef();
            cRef.setId(s.getOid());
            cRef.setLosId(CreatorUtil.resolveLOSId(komoByKomotoOID.getOid(), komoto.getTarjoajaOid()));
            cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getLukiolinjaUri()));
            cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
            cRef.setPrerequisite(loi.getPrerequisite());
            ao.getChildLOIRefs().add(cRef);
        }

        return ao;
    }
    
    public ApplicationOption createHigherEducationApplicationOption(UniversityAppliedScienceLOS los, HakukohdeV1RDTO hakukohde, HakuV1RDTO haku) throws KoodistoException {
    	ApplicationOption ao = new ApplicationOption();
        ao.setId(hakukohde.getOid());
        ao.setName(super.getI18nText(hakukohde.getHakukohteenNimet())); //koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
        //ao.setAoIdentifier(//koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
        ao.setAthleteEducation(false);
        ao.setStartingQuota(hakukohde.getAloituspaikatLkm());
        ao.setLowestAcceptedScore(hakukohde.getAlinValintaPistemaara());
        ao.setLowestAcceptedAverage(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        ao.setAttachmentDeliveryDeadline(hakukohde.getLiitteidenToimitusPvm());
        ao.setLastYearApplicantCount(hakukohde.getEdellisenVuodenHakijatLkm());
        ao.setSelectionCriteria(getI18nText(hakukohde.getValintaperusteKuvaukset()));
        ao.setSoraDescription(getI18nText(hakukohde.getSoraKuvaukset()));
        ao.setExams(educationObjectCreator.createExamsHigherEducation(hakukohde.getValintakokeet()));
        ao.setKaksoistutkinto(false);
        ao.setVocational(false);
        ao.setEducationCodeUri(los.getEducationCode());

        ao.setRequiredBaseEducations(hakukohde.getHakukelpoisuusvaatimusUris());
        los.setPrerequisites(koodistoService.searchCodesMultiple(hakukohde.getHakukelpoisuusvaatimusUris()));
        //haku.getNimi().
        ApplicationSystem as = new ApplicationSystem();
        as.setId(haku.getOid());
        as.setName(getI18nText(haku.getNimi()));
        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha : haku.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);
            }
        }
        ao.setApplicationSystem(as);
        if (!Strings.isNullOrEmpty(hakukohde.getSoraKuvausKoodiUri())) {
            ao.setSora(true);
        }

        ao.setTeachingLanguages(extractCodeVales(los.getTeachingLanguages()));
        
        //ao.setPrerequisite(prerequisite);
        ao.setSpecificApplicationDates(hakukohde.isKaytetaanHakukohdekohtaistaHakuaikaa());
        if (ao.isSpecificApplicationDates()) {
            ao.setApplicationStartDate(hakukohde.getHakuaikaAlkuPvm());
            ao.setApplicationEndDate(hakukohde.getHakuaikaLoppuPvm());
        }

        ao.setAttachmentDeliveryAddress(educationObjectCreator.createAddress(hakukohde.getLiitteidenToimitusOsoite()));

        List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
        if (hakukohde.getHakukohteenLiitteet() != null && !hakukohde.getHakukohteenLiitteet().isEmpty()) {
            for (HakukohdeLiiteV1RDTO liite : hakukohde.getHakukohteenLiitteet()) {
                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getToimitettavaMennessa());
                //attach.setType(koodistoService.searchFirst(liite.getLiitteenTyyppiUri()));
                attach.setDescreption(getI18nText(liite.getLiitteenKuvaukset()));
                attach.setAddress(educationObjectCreator.createAddress(liite.getLiitteenToimitusOsoite()));
                attachments.add(attach);
            }
        }
        ao.setAttachments(attachments);
        ao.setAdditionalInfo(getI18nText(hakukohde.getLisatiedot()));

        //TODO TARVITAANKO N�IT�???
        // set child loi names to application option
        /*List<OidRDTO> komotosByHakukohdeOID = tarjontaRawService.getKomotosByHakukohde(hakukohdeDTO.getOid());
        for (OidRDTO s : komotosByHakukohdeOID) {
            KomoDTO komoByKomotoOID = tarjontaRawService.getKomoByKomoto(s.getOid());

            if (not(
                    and(
                            CreatorUtil.komoPublished,
                            CreatorUtil.komoHasKoulutusohjelmaKoodi,
                            CreatorUtil.komoHasTutkintonimike
                    )
            ).apply(komoByKomotoOID)) {
                LOG.debug(String.format("Skipping invalid child komo %s", komoByKomotoOID.getOid()));
                continue;
            }

            KomotoDTO k = tarjontaRawService.getKomoto(s.getOid());
            if (not(CreatorUtil.komotoPublished).apply(k)) {
                LOG.debug(String.format("Skipping invalid child komoto %s", k.getOid()));
                continue;
            }

            ChildLOIRef cRef = new ChildLOIRef();
            cRef.setId(s.getOid());
            cRef.setLosId(CreatorUtil.resolveLOSId(komoByKomotoOID.getOid(), childKomoto.getTarjoajaOid()));
            cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getKoulutusOhjelmaKoodiUri()));
            cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
            cRef.setPrerequisite(prerequisite);
            ao.getChildLOIRefs().add(cRef);
        }*/
        return ao;
    }

	private List<String> extractCodeVales(List<Code> teachingLanguages) {
		List<String> vals = new ArrayList<String>();
		for (Code curCode : teachingLanguages) {
			vals.add(curCode.getValue());
		}
		return vals;
	}




}
