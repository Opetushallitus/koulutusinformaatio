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

import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.SpecialLearningOpportunitySpecificationDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class SpecialLOSToDTO {

    private SpecialLOSToDTO() {
    }

    public static SpecialLearningOpportunitySpecificationDTO convert(SpecialLOS los, String lang, String uiLang, String defaultLang) {
        SpecialLearningOpportunitySpecificationDTO dto = new SpecialLearningOpportunitySpecificationDTO();

        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), defaultLang));
        if (los.getSubName() != null 
                && los.getSubName().getTranslations() != null 
                && !los.getSubName().getTranslations().isEmpty()) {
            dto.setSubName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getSubName(), defaultLang));
        }
        
        dto.setEducationDegree(los.getEducationDegree());
        dto.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(los.getQualification(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), lang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), lang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), lang));
        dto.setLois(ChildLOIToDTO.convert(los.getLois(), lang, uiLang, defaultLang));
        if (los.getLois() != null && !los.getLois().isEmpty()) {
            ChildLOI curLoi = los.getLois().get(0);
            if (curLoi.getKoulutuslaji() != null) {
                dto.setEducationKind(CodeToName.convert(curLoi.getKoulutuslaji(), uiLang));
            }
        }
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), lang, defaultLang, uiLang));
        dto.setTranslationLanguage(lang);
        dto.setCreditValue(los.getCreditValue());
        dto.setCreditUnit(ConverterUtil.getTextByLanguage(los.getCreditUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguage(los.getEducationDomain(), uiLang));
        dto.setParent(ParentLOSRefToDTO.convert(los.getParent(), defaultLang));
        dto.setEducationTypeUri(los.getEducationTypeUri());
        
        if (los.getThemes() != null) {
            dto.setThemes(CodeToDTO.convertCodesDistinct(los.getThemes(), uiLang));
        }
        if (los.getTopics() != null) {
            dto.setTopics(CodeToDTO.convertAll(los.getTopics(), uiLang));
        }

        return dto;
    }


}
