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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.AdultVocationalLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalChildLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;

/**
 * 
 * @author Markus
 *
 */
public final class AdultVocationalChildLOSToDTO {
    
    public AdultVocationalChildLOSToDTO() {
     
    }
    
    public static AdultVocationalChildLOSDTO convert(final AdultVocationalLOS los, final String lang, final String uiLang) {
        
        AdultVocationalChildLOSDTO dto =
                new AdultVocationalChildLOSDTO();
        
        dto.setId(los.getId());
        
        String descriptionLang = HigherEducationLOSToDTO.getDescriptionLang(lang, los.getAvailableTranslationLanguages());
        dto.setTranslationLanguage(descriptionLang);
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setEducationDegreeName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegreeLang(), uiLang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), uiLang));
        dto.setQualifications(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getQualifications(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), descriptionLang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), descriptionLang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), descriptionLang));
        dto.setInfoAboutCharge(ConverterUtil.getTextByLanguage(los.getInfoAboutCharge(), descriptionLang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(), descriptionLang));
        dto.setCareerOpportunities(ConverterUtil.getTextByLanguage(los.getCareerOpportunities(), descriptionLang));
        
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), descriptionLang));
        dto.setTargetGroup(ConverterUtil.getTextByLanguage(los.getTargetGroup(), descriptionLang));
        dto.setPersonalization(ConverterUtil.getTextByLanguage(los.getPersonalization(), descriptionLang));
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), descriptionLang));
        
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), uiLang, "fi", uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(los.getAvailableTranslationLanguages(), uiLang));
        dto.setCreditValue(los.getCreditValue());

        dto.setCreditUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getCreditUnit(), uiLang));

        //DO MORE
        dto.setPrerequisites(CodeToDTO.convertAll(los.getPrerequisites(), uiLang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getFormOfTeaching(), uiLang));
        //dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(los.getProfessionalTitles(), descriptionLang));
        dto.setTeachingTimes(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getTeachingTimes(), uiLang));
        dto.setTeachingPlaces(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getTeachingPlaces(), uiLang));
        dto.setTeachingLanguages(CodeToName.convertAll(los.getTeachingLanguages(), uiLang));
        //TODO: --> dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(los.getFormOfEducation(), uiLang));
        if (los.getStartDate() != null) {
            dto.setStartDate(los.getStartDate());  
        } else {
            dto.setStartYear(los.getStartYear());
            dto.setStartSeason(ConverterUtil.getTextByLanguageUseFallbackLang(los.getStartSeason(), uiLang));
        }
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), descriptionLang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(), descriptionLang));
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), descriptionLang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(los.getContactPersons()));
        if (los.getPreparatoryContactPersons() != null) {
            dto.setPreparatoryContactPersons(ContactPersonToDTO.convertAll(los.getPreparatoryContactPersons()));
        }
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), uiLang));
        dto.setValmistavaKoulutus(los.isValmistavaKoulutus());

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
        
        dto.setStatus(los.getStatus());
        if (los.getEducationCode() != null) {
            dto.setEducationCode(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationCode().getName(), uiLang));
            dto.setKoulutuskoodi(los.getEducationCode().getUri());
        }
        if (los.getThemes() != null) {
            dto.setThemes(CodeToDTO.convertAll(los.getThemes(), uiLang));
        }
        if (los.getTopics() != null) {
            dto.setTopics(CodeToDTO.convertAll(los.getTopics(), uiLang));
        }
        dto.setEducationType(los.getEducationType());
        
        dto.setChargeable(los.isChargeable());
        dto.setCharge(los.getCharge());
        dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(los.getProfessionalTitles(), lang));
        dto.setDegreeOrganizer(ConverterUtil.getTextByLanguageUseFallbackLang(los.getOrganizer(), uiLang));
        
        return dto;
        
        //return null;
        
    }

}
