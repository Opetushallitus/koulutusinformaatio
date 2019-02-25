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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.AdditionalProof;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.ExamEvent;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.OrganizationGroup;
import fi.vm.sade.koulutusinformaatio.domain.ScoreLimit;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.OrganisaatioException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.RyhmaliitosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoePisterajaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public class EducationObjectCreator extends ObjectCreator {

    private final OrganisaatioRawService organisaatioRawService;
    private KoodistoService koodistoService;

    protected EducationObjectCreator(KoodistoService koodistoService, OrganisaatioRawService organisaatioRawService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
        this.organisaatioRawService = organisaatioRawService;
    }


    public AdditionalProof createAdditionalProof(List<ValintakoeV1RDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            AdditionalProof additionalProof = new AdditionalProof();
            for (ValintakoeV1RDTO valintakoe : valintakoes) {
                if (valintakoe.getLisanaytot() != null) {
                    additionalProof.setDescreption(getI18nText(valintakoe.getLisanaytot()));
                    additionalProof.setScoreLimit(resolvePointLimit(valintakoe, "Lisapisteet"));
                    return additionalProof;
                }
            }
        }
        return null;
    }

    public ScoreLimit resolvePointLimit(ValintakoeV1RDTO valintakoe, String type) {
        if (valintakoe != null && valintakoe.getPisterajat() != null) {
            for (ValintakoePisterajaV1RDTO valintakoePisteraja : valintakoe.getPisterajat()) {
                if (valintakoePisteraja.getPisterajatyyppi().equals(type)) {
                    return new ScoreLimit(valintakoePisteraja.getAlinPistemaara(),
                            valintakoePisteraja.getAlinHyvaksyttyPistemaara(), valintakoePisteraja.getYlinPistemaara());
                }
            }
        }
        return null;
    }


    public Address createAddress(OsoiteRDTO osoite, String kieliUri) throws KoodistoException {
        if (osoite != null) {
            String lang;
            if (kieliUri != null && kieliUri.contains("kieli_")) {
                lang = kieliUri.substring(kieliUri.length() - 2);
            } else {
                lang = "fi";
            }
            Address attachmentDeliveryAddress = new Address();

            Map<String,String> streetAddrTransls = new HashMap<String,String>();
            Map<String,String> streetAddrTransls2 = new HashMap<String,String>();
            Map<String,String> postOfficeTransls = new HashMap<String,String>();
            Map<String,String> postalCodeTransls = new HashMap<String,String>();

            if (osoite.getOsoiterivi1() != null) {
                streetAddrTransls.put(lang, osoite.getOsoiterivi1());
                attachmentDeliveryAddress.setStreetAddress(new I18nText(streetAddrTransls));
            }
            if (osoite.getOsoiterivi2() != null) {
                streetAddrTransls2.put(lang, osoite.getOsoiterivi2());
                attachmentDeliveryAddress.setSecondForeignAddr(new I18nText(streetAddrTransls2));
            }
            if (osoite.getPostitoimipaikka() != null) {
                postOfficeTransls.put(lang, osoite.getPostitoimipaikka());
                attachmentDeliveryAddress.setPostOffice(new I18nText(postOfficeTransls));
            }
            if (osoite.getPostinumero() != null) {
                String postalCode = koodistoService.searchFirstCodeValue(osoite.getPostinumero());
                if (postalCode != null) {
                    postalCodeTransls.put(lang, postalCode);
                    attachmentDeliveryAddress.setPostalCode(new I18nText(postalCodeTransls));
                }
            }
            return attachmentDeliveryAddress;
        } else {
            return new Address();
        }
    }

    public List<Exam> createEducationExams(List<ValintakoeV1RDTO> valintakokeet) throws KoodistoException {
        if (valintakokeet != null && !valintakokeet.isEmpty()) {
            List<Exam> exams = Lists.newArrayList();
            for (ValintakoeV1RDTO valintakoe : valintakokeet) {
                if (valintakoe != null) {
                    Exam exam = new Exam();

                    if (valintakoe.getValintakoetyyppi() != null) {
                        Code type = koodistoService.searchFirst(valintakoe.getValintakoetyyppi());
                        exam.setType(type.getName());
                    } else if (valintakoe.getValintakoeNimi() != null && valintakoe.getKieliUri() != null) {
                        exam.setType(getI18nText(valintakoe.getValintakoeNimi(), valintakoe.getKieliUri()));
                    }
                    if (valintakoe.getValintakokeenKuvaus() != null) {
                        exam.setDescription(getI18nTextEnriched(valintakoe.getValintakokeenKuvaus()));
                    } else if (valintakoe.getKuvaukset() != null) {
                        exam.setDescription(getI18nText(valintakoe.getKuvaukset()));
                    }
                    List<ExamEvent> examEvents = Lists.newArrayList();

                    if (valintakoe.getValintakoeAjankohtas() != null
                            && !valintakoe.getValintakoeAjankohtas().isEmpty() ) {

                        for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                            ExamEvent examEvent = new ExamEvent();
                            examEvent.setAddress(createAddress(valintakoeAjankohta.getOsoite(), valintakoe.getKieliUri()));
                            examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                            examEvent.setStart(valintakoeAjankohta.getAlkaa());
                            examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                            examEvent.setTimeIncluded(valintakoeAjankohta.isKellonaikaKaytossa());
                            examEvents.add(examEvent);
                        }
                    }
                    exam.setScoreLimit(resolvePointLimit(valintakoe, "Paasykoe"));
                    exam.setExamEvents(examEvents);
                    exams.add(exam);
                }
            }
            return exams;
        }
        return null;
    }

    private I18nText getI18nTextEnriched(TekstiRDTO valintakokeenKuvaus) {
        if (!Strings.isNullOrEmpty(valintakokeenKuvaus.getArvo()) && !Strings.isNullOrEmpty(valintakokeenKuvaus.getTeksti())) {
            Map<String, String> translations = new HashMap<String, String>();
            translations.put(valintakokeenKuvaus.getArvo().toLowerCase(), valintakokeenKuvaus.getTeksti());
            I18nText text = new I18nText();
            text.setTranslations(translations);
            return text;
        }
        return null;
    }

    public List<OrganizationGroup> createOrganizationGroups(List<RyhmaliitosV1RDTO> ryhmaliitokset, String... organisaatioRyhmaOids) throws OrganisaatioException {
        if (organisaatioRyhmaOids == null) {
            return new ArrayList<>();
        }
        List<OrganizationGroup> groups = new ArrayList<>(organisaatioRyhmaOids.length);
        for (String oid: organisaatioRyhmaOids) {
            OrganisaatioRDTO   organisaatioRDTO = organisaatioRawService.getOrganisaatio(oid);
            boolean isGroup = false;
            for (String tyyppi : organisaatioRDTO.getTyypit()) {
                if (OrganisaatioTyyppi.RYHMA.value().equals(tyyppi)) {
                    isGroup = true;
                    break;
                }
            }
            if (!isGroup) {
                throw new OrganisaatioException("Organization "+oid+" is not group");
            }

            OrganizationGroup group = new OrganizationGroup();
            group.setOid(oid);
            group.setGroupTypes(organisaatioRDTO.getRyhmatyypit());
            group.setUsageGroups(organisaatioRDTO.getKayttoryhmat());
            for(RyhmaliitosV1RDTO ryhmaliitos: ryhmaliitokset) {
                if(oid.equals(ryhmaliitos.getRyhmaOid())) {
                    group.setPrioriteetti(ryhmaliitos.getPrioriteetti());
                }
            }

            groups.add(group);
        }
        return groups;
    }
}
