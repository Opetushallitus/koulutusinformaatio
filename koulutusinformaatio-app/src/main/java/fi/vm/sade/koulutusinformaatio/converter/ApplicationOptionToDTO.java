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

import java.util.Date;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

/**
 * @author Mikko Majapuro
 */
public final class ApplicationOptionToDTO {

    private ApplicationOptionToDTO() {
    }

    public static ApplicationOptionDTO convert(final ApplicationOption applicationOption, final String lang, final String uiLang, String defaultLang) {
        if (applicationOption != null) {
            ApplicationOptionDTO dto = new ApplicationOptionDTO();
            dto.setId(applicationOption.getId());
            dto.setType(applicationOption.getType());
            dto.setEducationTypeUri(createEducationTypeUri(applicationOption.getEducationTypeUri()));
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getName(), uiLang));
            dto.setAoIdentifier(applicationOption.getAoIdentifier());
            dto.setAttachmentDeliveryDeadline(applicationOption.getAttachmentDeliveryDeadline());
            dto.setAttachmentDeliveryAddress(AddressToDTO.convert(applicationOption.getAttachmentDeliveryAddress(), uiLang));
            dto.setLastYearApplicantCount(applicationOption.getLastYearApplicantCount());
            dto.setLowestAcceptedAverage(applicationOption.getLowestAcceptedAverage());
            dto.setLowestAcceptedScore(applicationOption.getLowestAcceptedScore());
            if (applicationOption.getStartingQuotaDescription() != null 
                    && applicationOption.getStartingQuotaDescription().getTranslations() != null 
                    && !applicationOption.getStartingQuotaDescription().getTranslations().isEmpty()) {
                dto.setStartingQuotaDescription(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getStartingQuotaDescription(), uiLang));
            }
            dto.setStartingQuota(applicationOption.getStartingQuota());
            dto.setFirstTimerStartingQuota(applicationOption.getFirstTimerStartingQuota());
            dto.setSora(applicationOption.isSora());
            dto.setEducationDegree(applicationOption.getEducationDegree());
            dto.setTeachingLanguages(applicationOption.getTeachingLanguages());
            dto.setTeachingLanguageNames(ConverterUtil.getTextsByLanguageUseFallbackLang(applicationOption.getTeachingLanguageNames(), lang));
            dto.setSelectionCriteria(ConverterUtil.getTextByLanguage(applicationOption.getSelectionCriteria(), lang));
            dto.setSoraDescription(ConverterUtil.getTextByLanguage(applicationOption.getSoraDescription(), lang));
            dto.setPrerequisite(CodeToDTO.convert(applicationOption.getPrerequisite(), lang));
            dto.setExams(ExamToDTO.convertAll(applicationOption.getExams(), lang));
            dto.setProvider(ProviderToDTO.convert(applicationOption.getProvider(), lang, defaultLang, uiLang));
            dto.setApplicationOffice(ApplicationOfficeToDTO.convert(applicationOption.getApplicationOffice(), applicationOption.getProvider(), uiLang));
            dto.setChildRefs(ChildLOIRefToDTO.convert(applicationOption.getChildLOIRefs(), defaultLang));
            dto.setHigherEdLOSRefs(HigherEducationLOSRefToDTO.convert(applicationOption.getHigherEdLOSRefs(), defaultLang));
            dto.setSpecificApplicationDates(applicationOption.isSpecificApplicationDates());
            dto.setApplicationStartDate(applicationOption.getApplicationStartDate());
            dto.setApplicationEndDate(applicationOption.getApplicationEndDate());
            dto.setRequiredBaseEducations(applicationOption.getRequiredBaseEducations());
            dto.setVocational(applicationOption.isVocational());

            if (applicationOption.getApplicationStartDate() != null && applicationOption.getApplicationEndDate() != null) {
                dto.setCanBeApplied(ConverterUtil.isOngoing(new DateRange(applicationOption.getApplicationStartDate(),
                    applicationOption.getApplicationEndDate())));
            } else {
                dto.setCanBeApplied(ConverterUtil.isOngoing(applicationOption.getApplicationSystem().getApplicationDates()));
            }
            if (applicationOption.getApplicationStartDate() != null 
                    && applicationOption.getApplicationStartDate().after(new Date())) {
                dto.setNextApplicationPeriodStarts(applicationOption.getApplicationStartDate());
            }
            if (applicationOption.getApplicationPeriodName() != null) {
                dto.setApplicationPeriodName(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getApplicationPeriodName(), defaultLang));
            }

            boolean isVaadinAo = dto.getHigherEdLOSRefs() == null || dto.getHigherEdLOSRefs().isEmpty();
            dto.setAttachments(ApplicationOptionAttachmentToDTO.convertAll(applicationOption.getAttachments(), lang, isVaadinAo));
            dto.setEmphasizedSubjects(EmphasizedSubjectToDTO.convertAll(applicationOption.getEmphasizedSubjects(), lang));
            dto.setAdditionalInfo(ConverterUtil.getTextByLanguage(applicationOption.getAdditionalInfo(), lang));
            dto.setAdditionalProof(AdditionalProofToDTO.convert(applicationOption.getAdditionalProof(), lang));
            dto.setOverallScoreLimit(ScoreLimitToDTO.convert(applicationOption.getOverallScoreLimit()));
            dto.setKaksoistutkinto(applicationOption.isKaksoistutkinto());
            dto.setAthleteEducation(applicationOption.isAthleteEducation());
            dto.setEducationCodeUri(applicationOption.getEducationCodeUri());
            dto.setStatus(applicationOption.getStatus());
            dto.setKotitalous(applicationOption.getEducationCodeUri() != null && applicationOption.getEducationCodeUri().contains(TarjontaConstants.KOTITALOUSKOODI));
            dto.setHakuaikaId(applicationOption.getInternalASDateRef());
            dto.setOrganizationGroups(OrganizationGroupToDTO.convertAll(applicationOption.getOrganizationGroups()));

            return dto;
        }
        return null;
    }

    public static ApplicationOptionDTO convertHigherEducation(final ApplicationOption applicationOption, 
            final String lang, 
            final String uiLang, 
            String defaultLang) {
        if (applicationOption == null) {
            return null;
        }
        ApplicationOptionDTO dto = convert(applicationOption, lang, uiLang, defaultLang);
        if (dto == null) {
            return null;
        }
        if (applicationOption.getEligibilityDescription() != null) {
            dto.setEligibilityDescription(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getEligibilityDescription(), lang));            
        }
        dto.setSelectionCriteria(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getSelectionCriteria(), lang));
        dto.setSoraDescription(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getSoraDescription(), lang));
        dto.setAdditionalInfo(ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getAdditionalInfo(), lang));
        dto.setExams(ExamToDTO.convertAllHigherEducation(applicationOption.getExams(), lang));
        dto.setAttachments(ApplicationOptionAttachmentToDTO.convertAllHigherEducation(applicationOption.getAttachments(), lang));
        return dto;
    }

    private static String createEducationTypeUri(String educationTypeUri) {

        if (educationTypeUri != null) {
            return educationTypeUri.replace(".", "");
        }

        return null;
    }
}
