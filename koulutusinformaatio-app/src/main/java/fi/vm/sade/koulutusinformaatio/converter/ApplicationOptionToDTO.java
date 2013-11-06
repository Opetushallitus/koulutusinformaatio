/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

import java.util.Date;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionToDTO {

    public static ApplicationOptionDTO convert(final ApplicationOption applicationOption, final String lang, final String uiLang) {
        if (applicationOption != null) {
            ApplicationOptionDTO ao = new ApplicationOptionDTO();
            ao.setId(applicationOption.getId());
            ao.setName(ConverterUtil.getTextByLanguage(applicationOption.getName(), lang));
            ao.setAoIdentifier(applicationOption.getAoIdentifier());
            ao.setAttachmentDeliveryDeadline(applicationOption.getAttachmentDeliveryDeadline());
            ao.setAttachmentDeliveryAddress(AddressToDTO.convert(applicationOption.getAttachmentDeliveryAddress()));
            ao.setLastYearApplicantCount(applicationOption.getLastYearApplicantCount());
            ao.setLowestAcceptedAverage(applicationOption.getLowestAcceptedAverage());
            ao.setLowestAcceptedScore(applicationOption.getLowestAcceptedScore());
            ao.setStartingQuota(applicationOption.getStartingQuota());
            ao.setSora(applicationOption.isSora());
            ao.setEducationDegree(applicationOption.getEducationDegree());
            ao.setTeachingLanguages(applicationOption.getTeachingLanguages());
            ao.setSelectionCriteria(ConverterUtil.getTextByLanguage(applicationOption.getSelectionCriteria(), uiLang));
            ao.setPrerequisite(CodeToDTO.convert(applicationOption.getPrerequisite(), lang));
            ao.setExams(ExamToDTO.convertAll(applicationOption.getExams(), lang));
            ao.setProvider(ProviderToDTO.convert(applicationOption.getProvider(), lang));
            ao.setChildRefs(ChildLOIRefToDTO.convert(applicationOption.getChildLOIRefs(), lang));
            ao.setSpecificApplicationDates(applicationOption.isSpecificApplicationDates());
            ao.setApplicationStartDate(applicationOption.getApplicationStartDate());
            ao.setApplicationEndDate(applicationOption.getApplicationEndDate());
            ao.setRequiredBaseEducations(applicationOption.getRequiredBaseEducations());
            if (applicationOption.isSpecificApplicationDates()) {
                ao.setCanBeApplied(ConverterUtil.isOngoing(new DateRange(applicationOption.getApplicationStartDate(),
                        applicationOption.getApplicationEndDate())));
                if (applicationOption.getApplicationStartDate().after(new Date())) {
                    ao.setNextApplicationPeriodStarts(applicationOption.getApplicationStartDate());
                }
            }

            if (applicationOption.getAttachments() != null) {
                ao.setAttachments(ApplicationOptionAttachmentToDTO.convertAll(applicationOption.getAttachments(), uiLang));
            }
            if (applicationOption.getEmphasizedSubjects() != null) {
                ao.setEmphasizedSubjects(EmphasizedSubjectToDTO.convertAll(applicationOption.getEmphasizedSubjects(), uiLang));
            }
            if (applicationOption.getAdditionalInfo() != null) {
                ao.setAdditionalInfo(ConverterUtil.getTextByLanguage(applicationOption.getAdditionalInfo(), uiLang));
            }
            if (applicationOption.getAdditionalProof() != null) {
                ao.setAdditionalProof(ConverterUtil.getTextByLanguage(applicationOption.getAdditionalProof(), lang));
            }

            return ao;
        }
        return null;
    }
}
