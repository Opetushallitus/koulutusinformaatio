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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class EducationObjectCreator extends ObjectCreator {

    private KoodistoService koodistoService;

    protected EducationObjectCreator(KoodistoService koodistoService) {
        super(koodistoService);
        this.koodistoService = koodistoService;
    }

    public List<Exam> createVocationalExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        if (valintakoes != null) {
            List<Exam> exams = Lists.newArrayList();
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe != null && valintakoe.getKuvaus() != null && valintakoe.getTyyppiUri() != null
                        && valintakoe.getValintakoeAjankohtas() != null
                        && !valintakoe.getValintakoeAjankohtas().isEmpty()) {
                    Exam exam = new Exam();
                    exam.setType(koodistoService.searchFirst(valintakoe.getTyyppiUri()));
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
                    exams.add(exam);
                }
            }
            return exams;
        } else {
            return null;
        }
    }

    public List<Exam> createUpperSecondaryExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
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

    public AdditionalProof createAdditionalProof(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
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

    public ScoreLimit resolvePointLimit(ValintakoeRDTO valintakoe, String type) {
        for (ValintakoePisterajaRDTO valintakoePisteraja : valintakoe.getValintakoePisterajas()) {
            if (valintakoePisteraja.getTyyppi().equals(type)) {
                return new ScoreLimit(valintakoePisteraja.getAlinPistemaara(),
                        valintakoePisteraja.getAlinHyvaksyttyPistemaara(), valintakoePisteraja.getYlinPistemaara());
            }
        }
        return null;
    }



    public Address createAddress(OsoiteRDTO osoite) throws KoodistoException {
        if (osoite != null) {
            Address attachmentDeliveryAddress = new Address();
            attachmentDeliveryAddress.setStreetAddress(osoite.getOsoiterivi1());
            attachmentDeliveryAddress.setStreetAddress2(osoite.getOsoiterivi2());
            attachmentDeliveryAddress.setPostalCode(koodistoService.searchFirstCodeValue(osoite.getPostinumero()));
            attachmentDeliveryAddress.setPostOffice(osoite.getPostitoimipaikka());
            return attachmentDeliveryAddress;
        } else {
            return null;
        }
    }


}