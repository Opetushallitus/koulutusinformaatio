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
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.*;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class UpperSecondaryLearningOpportunityBuilder extends LearningOpportunityBuilder<UpperSecondaryLOS> {

    private static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    private static final String APPLICATION_OPTIONS_KOODISTO_URI = "hakukohteet";

    private TarjontaRawService tarjontaRawService;
    private ProviderService providerService;
    private KoodistoService koodistoService;

    // variables

    private KomoDTO komo;
    private KomoDTO parentKomo;

    private List<UpperSecondaryLOS> loses;

    // A helper data structure that groups KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> komotosByProviderId;

    public UpperSecondaryLearningOpportunityBuilder(TarjontaRawService tarjontaRawService,
                                                    ProviderService providerService,
                                                    KoodistoService koodistoService, KomoDTO komo) {
        this.tarjontaRawService = tarjontaRawService;
        this.providerService = providerService;
        this.koodistoService = koodistoService;
        this.komo = komo;
        komotosByProviderId = ArrayListMultimap.create();
        this.loses = Lists.newArrayList();
    }

    @Override
    public LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        parentKomo = tarjontaRawService.getKomo(komo.getYlaModuulit().get(0));
        if (!CreatorUtil.komoPublished.apply(parentKomo)) {
            throw new TarjontaParseException(String.format("Parent komo not published: %s", parentKomo.getOid()));
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        if (!CreatorUtil.komoPublished.apply(komo)) {
            throw new TarjontaParseException(String.format("Child komo not published: %s", komo.getOid()));
        }
        List<OidRDTO> komotoOids = tarjontaRawService.getKomotosByKomo(komo.getOid(), Integer.MAX_VALUE, 0);
        for (OidRDTO komotoOid : komotoOids) {
            KomotoDTO komoto = tarjontaRawService.getKomoto(komotoOid.getOid());
            komotosByProviderId.put(komoto.getTarjoajaOid(), komoto);
        }

        for (String providerId : komotosByProviderId.keySet()) {
            UpperSecondaryLOS los = createLOS(komo, parentKomo, komotosByProviderId.get(providerId),
                    resolveLOSId(komo.getOid(), providerId), providerService.getByOID(providerId));
            loses.add(los);
        }

        return this;
    }

    private boolean isChildLOSValid(ChildLOS childLOS) {
        if (childLOS.getLois() != null) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getApplicationOptions() != null && childLOI.getApplicationOptions().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (UpperSecondaryLOS los : loses) {
            for (UpperSecondaryLOI loi : los.getLois()) {
                for (ApplicationOption ao : loi.getApplicationOptions()) {
                    ao.setProvider(los.getProvider());
                    ao.setEducationDegree(los.getEducationDegree());
                    los.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
                }
            }
        }
        return this;
    }

    @Override
    public LearningOpportunityBuilder filter() {
        loses = Lists.newArrayList(Collections2.filter(loses, losValid));
        return this;
    }

    @Override
    public List<UpperSecondaryLOS> build() {
        return this.loses;
    }

    private UpperSecondaryLOS createLOS(KomoDTO komo, KomoDTO parentKomo, List<KomotoDTO> komotos, String losID, Provider provider) throws KoodistoException {
        UpperSecondaryLOS los = new UpperSecondaryLOS();

        los.setId(losID);
        los.setName(koodistoService.searchFirst(komo.getLukiolinjaUri()));
        los.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        los.setQualification(koodistoService.searchFirst(komo.getTutkintonimikeUri()));
        los.setDegreeTitle(koodistoService.searchFirst(komo.getLukiolinjaUri()));
        los.setStructure(getI18nText(parentKomo.getKoulutuksenRakenne()));
        los.setAccessToFurtherStudies(getI18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        los.setProvider(provider);
        los.setCreditValue(parentKomo.getLaajuusArvo());
        los.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));

        if (komo.getTavoitteet() == null) {
            los.setGoals(getI18nText(parentKomo.getTavoitteet()));
        } else {
            los.setGoals(getI18nText(komo.getTavoitteet()));
        }

        List<UpperSecondaryLOI> lois = Lists.newArrayList();
        for (KomotoDTO komoto : komotos) {
            if (CreatorUtil.komotoPublished.apply(komoto)) {
                lois.add(createLOI(komoto, losID, los.getName()));
            }
        }

        los.setLois(lois);

        return los;
    }

    private UpperSecondaryLOI createLOI(KomotoDTO komoto, String losId, I18nText losName) throws KoodistoException {
        UpperSecondaryLOI loi = new UpperSecondaryLOI();

        loi.setName(losName);
        loi.setId(komoto.getOid());
        loi.setStartDate(komoto.getKoulutuksenAlkamisDate());
        loi.setFormOfEducation(koodistoService.searchMultiple(komoto.getKoulutuslajiUris()));
        loi.setTeachingLanguages(koodistoService.searchCodesMultiple(komoto.getOpetuskieletUris()));
        loi.setFormOfTeaching(koodistoService.searchMultiple(komoto.getOpetusmuodotUris()));
        loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
        loi.setInternationalization(getI18nText(komoto.getKansainvalistyminen()));
        loi.setCooperation(getI18nText(komoto.getYhteistyoMuidenToimijoidenKanssa()));
        loi.setContent(getI18nText(komoto.getSisalto()));

        loi.setPlannedDuration(komoto.getLaajuusArvo());
        loi.setPlannedDurationUnit(koodistoService.searchFirst(komoto.getLaajuusYksikkoUri()));
        loi.setPduCodeUri(komoto.getLaajuusYksikkoUri());
        
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

            for (String oppiaine : kielivalikoimat.keySet()) {
                List<I18nText> languages = Lists.newArrayList();
                for (String kieliKoodi : kielivalikoimat.get(oppiaine)) {
                    languages.add(koodistoService.searchFirst(kieliKoodi));
                }
                languageSelection.add(new LanguageSelection(oppiaine, languages));
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

            try {
                validateHakukohde(hakukohdeDTO);
            } catch (TarjontaParseException e) {
                LOG.debug("Application option skipped, " + e.getMessage());
                continue;
            }
            try {
                validateHaku(hakuDTO);
            } catch (TarjontaParseException e) {
                LOG.debug("Application option skipped, " + e.getMessage());
                continue;
            }

            applicationOptions.add(
                    createApplicationOption(hakukohdeDTO, hakuDTO, komoto, loi));
            
            if (hakukohdeDTO.isKaksoisTutkinto()) {
                kaksoistutkinto = true;
            }
        }

        loi.setApplicationOptions(applicationOptions);
        loi.setKaksoistutkinto(kaksoistutkinto);

        return loi;
    }

    private ApplicationOption createApplicationOption(HakukohdeDTO hakukohdeDTO, HakuDTO hakuDTO,
                                                      KomotoDTO komoto, UpperSecondaryLOI loi) throws KoodistoException {
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
        ao.setExams(createExams(hakukohdeDTO.getValintakoes()));
        ao.setVocational(false);
        List<Code> subCodes = koodistoService.searchSubCodes(komoto.getPohjakoulutusVaatimusUri(),
                LearningOpportunityBuilder.BASE_EDUCATION_KOODISTO_URI);
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
            ao.setAttachmentDeliveryAddress(convertToAddress(addressDTO));
        }

        ao.setAdditionalProof(createAdditionalProof(hakukohdeDTO.getValintakoes()));
        if (hakukohdeDTO.getValintakoes() != null) {
            for (ValintakoeRDTO valintakoeRDTO : hakukohdeDTO.getValintakoes()) {
                ao.setOverallScoreLimit(resolvePointLimit(valintakoeRDTO, "Kokonaispisteet"));
            }
        }

        List<ApplicationOptionAttachment> attachments = Lists.newArrayList();
        if (hakukohdeDTO.getLiitteet() != null && !hakukohdeDTO.getLiitteet().isEmpty()) {
            for (HakukohdeLiiteDTO liite : hakukohdeDTO.getLiitteet()) {
                ApplicationOptionAttachment attach = new ApplicationOptionAttachment();
                attach.setDueDate(liite.getErapaiva());
                attach.setType(koodistoService.searchFirst(liite.getLiitteenTyyppiUri()));
                attach.setDescreption(getI18nText(liite.getKuvaus()));
                attach.setAddress(convertToAddress(liite.getToimitusosoite()));
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
            try {
                validateChildKomoto(k);
            } catch (TarjontaParseException e) {
                continue;
            }

            ChildLOIRef cRef = new ChildLOIRef();
            cRef.setId(s.getOid());
            cRef.setLosId(resolveLOSId(komoByKomotoOID.getOid(), komoto.getTarjoajaOid()));
            cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getKoulutusOhjelmaKoodiUri()));
            cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
            cRef.setPrerequisite(loi.getPrerequisite());
            ao.getChildLOIRefs().add(cRef);
        }

        return ao;
    }

    private Address convertToAddress(OsoiteRDTO addressDTO) throws KoodistoException {
        Address attachmentDeliveryAddress = new Address();
        attachmentDeliveryAddress.setStreetAddress(addressDTO.getOsoiterivi1());
        attachmentDeliveryAddress.setStreetAddress2(addressDTO.getOsoiterivi2());
        attachmentDeliveryAddress.setPostalCode(koodistoService.searchFirstCodeValue(addressDTO.getPostinumero()));
        attachmentDeliveryAddress.setPostOffice(addressDTO.getPostitoimipaikka());
        return attachmentDeliveryAddress;
    }

    private List<Exam> createExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        List<Exam> exams = Lists.newArrayList();
        if (valintakoes != null) {
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe.getKuvaus() != null
                        && valintakoe.getValintakoeAjankohtas() != null
                        && !valintakoe.getValintakoeAjankohtas().isEmpty()) {
                    Exam exam = new Exam();
                    exam.setDescription(getI18nText(valintakoe.getKuvaus()));
                    List<ExamEvent> examEvents = Lists.newArrayList();

                    for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                        ExamEvent examEvent = new ExamEvent();
                        Address address = new Address();
                        address.setPostalCode(koodistoService.searchFirstCodeValue(valintakoeAjankohta.getOsoite().getPostinumero()));
                        address.setPostOffice(valintakoeAjankohta.getOsoite().getPostitoimipaikka());
                        address.setStreetAddress(valintakoeAjankohta.getOsoite().getOsoiterivi1());
                        examEvent.setAddress(address);
                        examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                        examEvent.setStart(valintakoeAjankohta.getAlkaa());
                        examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                        examEvents.add(examEvent);
                    }
                    exam.setExamEvents(examEvents);
                    exam.setScoreLimit(resolvePointLimit(valintakoe, "Paasykoe"));
                    exams.add(exam);
                }
            }
        }
        return exams;
    }

    private AdditionalProof createAdditionalProof(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            AdditionalProof additionalProof = new AdditionalProof();
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe.getLisanaytot() != null) {
                    additionalProof.setDescreption(getI18nText(valintakoe.getLisanaytot()));
                    additionalProof.setScoreLimit(resolvePointLimit(valintakoe, "Lisapisteet"));
                    return additionalProof;
                }
            }
        }
        return null;
    }

    private ScoreLimit resolvePointLimit(ValintakoeRDTO valintakoe, String type) {
        for (ValintakoePisterajaRDTO valintakoePisteraja : valintakoe.getValintakoePisterajas()) {
            if (valintakoePisteraja.getTyyppi().equals(type)) {
                return new ScoreLimit(valintakoePisteraja.getAlinPistemaara(),
                        valintakoePisteraja.getAlinHyvaksyttyPistemaara(), valintakoePisteraja.getYlinPistemaara());
            }
        }
        return null;
    }


    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
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

    private static Predicate<UpperSecondaryLOS> losValid = new Predicate<UpperSecondaryLOS>() {
        @Override
        public boolean apply(UpperSecondaryLOS los) {
            if (los.getLois() != null) {
                for (UpperSecondaryLOI loi : los.getLois()) {
                    if (loi.getApplicationOptions() != null && loi.getApplicationOptions().size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

}
