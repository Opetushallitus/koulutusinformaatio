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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalChildLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;

/**
 * 
 * @author Markus
 * 
 */
public final class AdultVocationalParentLOSToDTO {

    public AdultVocationalParentLOSToDTO() {

    }

    public static AdultVocationalParentLOSDTO convert(
            final CompetenceBasedQualificationParentLOS los, final String lang, final String uiLang) {
        
        AdultVocationalParentLOSDTO dto =
                new AdultVocationalParentLOSDTO();
        
        dto.setId(los.getId());
        
        String descriptionLang = HigherEducationLOSToDTO.getDescriptionLang(lang, los.getAvailableTranslationLanguages());
        descriptionLang = descriptionLang != null ? descriptionLang : "fi";
        dto.setTranslationLanguage(descriptionLang);
        boolean iseducationKind = los.getEducationKind() != null && los.getEducationKind().getTranslations() != null && !los.getEducationKind().getTranslations().isEmpty();
        String name = iseducationKind?  String.format("%s, %s", ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang), ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationKind(), uiLang).toLowerCase()) : ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang);
        if (los.isOsaamisala()) {
            dto.setName(name);
        } else if (los.getDeterminer() != null) {
            dto.setName(String.format("%s, %s, %s", ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang), ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationKind(), uiLang).toLowerCase(), los.getDeterminer()));
        } else {
            dto.setName(name);
        }

        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), descriptionLang));
        //dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), descriptionLang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), descriptionLang));
        dto.setDegreeCompletion(ConverterUtil.getTextByLanguage(los.getDegreeCompletion(), descriptionLang));
        dto.setChoosingCompetence(ConverterUtil.getTextByLanguage(los.getChoosingCompetence(), descriptionLang));

        dto.setProvider(ProviderToDTO.convert(los.getProvider(), uiLang, "fi", uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(los.getAvailableTranslationLanguages(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), uiLang));
        dto.setEducationKind(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationKind(), uiLang));
        if (dto.getEducationKind() != null) {
            dto.setEducationKind(dto.getEducationKind().toLowerCase());
        }
        
        dto.setChargeable(los.isChargeable());
        dto.setCharge(los.getCharge());
        dto.setOsaamisala(los.isOsaamisala());

        // as based approach for UI

        if (los.getApplicationOptions() != null) {
            SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
            for (ApplicationOption ao : los.getApplicationOptions()) {
                aoByAs.put(ao.getApplicationSystem(), ao);
            }

            for (ApplicationSystem as : aoByAs.keySet()) {
                ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
                asDTO.setStatus(as.getStatus());
                for (ApplicationOption ao : aoByAs.get(as)) {
                    asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convertHigherEducation(ao, lang, uiLang, "fi"));
                }
                dto.getApplicationSystems().add(asDTO);
            }
        }

        if (los.getThemes() != null) {
            dto.setThemes(CodeToDTO.convertAll(los.getThemes(), uiLang));
        }
        if (los.getTopics() != null) {
            dto.setTopics(CodeToDTO.convertAll(los.getTopics(), uiLang));
        }
        
        List<AdultVocationalChildLOSDTO> childList = new ArrayList<AdultVocationalChildLOSDTO>();
        for (AdultVocationalLOS curChild: los.getChildren()) {
            AdultVocationalChildLOSDTO childDto = AdultVocationalChildLOSToDTO.convert(curChild, los.getDeterminer(), lang, uiLang);
            if (childDto != null) {
                childList.add(childDto);
            }
        }
        dto.setChildren(childList);

        return dto;
        
    }
    
    
}
