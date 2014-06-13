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

package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunitySpecificationDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class UpperSecondaryLOSToDTO {

    private UpperSecondaryLOSToDTO() {
    }

    public static UpperSecondaryLearningOpportunitySpecificationDTO convert(
            final UpperSecondaryLOS los, final String lang, final String uiLang, final String defaultLang) {
        UpperSecondaryLearningOpportunitySpecificationDTO dto =
                new UpperSecondaryLearningOpportunitySpecificationDTO();
        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), defaultLang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(los.getQualification(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), lang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), lang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), lang));
        dto.setLois(UpperSecondaryLOIToDTO.convertAll(los.getLois(), lang, uiLang, defaultLang));
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), lang, defaultLang, uiLang));
        dto.setTranslationLanguage(lang);
        dto.setCreditValue(los.getCreditValue());
        dto.setCreditUnit(ConverterUtil.getTextByLanguage(los.getCreditUnit(), uiLang));
        return dto;
    }
}