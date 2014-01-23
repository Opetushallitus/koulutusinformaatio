package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UniversityAppliedScienceLOSDTO;

public class UniversityAppliedScienceLOSToDTO {
	
	public UniversityAppliedScienceLOSToDTO() {
		
	}
	
	public static UniversityAppliedScienceLOSDTO convert(
            final UniversityAppliedScienceLOS los, final String lang, final String uiLang) {
		UniversityAppliedScienceLOSDTO dto =
                new UniversityAppliedScienceLOSDTO();
        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), lang));
        dto.setEducationDegree(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegree(), lang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), lang));
        dto.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(los.getQualification(), lang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), lang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), lang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), lang));
        //dto.setLois(UpperSecondaryLOIToDTO.convertAll(los.getLois(), lang, uiLang));
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), lang));
        dto.setTranslationLanguage(lang);
        dto.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(los.getGoals()));
        dto.setCreditValue(los.getCreditValue());
        
        //TODO, fix when unit is in rest
        dto.setCreditUnit(null);//ConverterUtil.getTextByLanguage(los.getCreditUnit(), uiLang));
        
        //DO MORE
        
        dto.setPrerequisite(CodeToDTO.convert(los.getPrerequisite(), lang));
        //TODO: --> dto.setFormOfTeaching(ConverterUtil.getTextsByLanguage(los.getFormOfTeaching(), uiLang));
        dto.setTeachingLanguages(CodeToValue.convertAll(los.getTeachingLanguages()));
        //TODO: --> dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(los.getFormOfEducation(), uiLang));
        dto.setStartDate(los.getStartDate());
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), lang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(), lang));
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), lang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(los.getContactPersons()));
        //dto.setDiplomas(ConverterUtil.getTextsByLanguage(los.getDiplomas(), lang));
        dto.setPlannedDuration(los.getPlannedDuration());
        //dto.setLanguageSelection(LanguageSelectionToDTO.convertAll(los.getLanguageSelection(), lang));
        //dto.setContactPersons(ContactPersonToDTO.convertAll(loi.getContactPersons()));
        //dto.setDiplomas(ConverterUtil.getTextsByLanguage(loi.getDiplomas(), lang));
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        
     // as based approach for UI
        SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        for (ApplicationOption ao : los.getApplicationOptions()) {
            aoByAs.put(ao.getApplicationSystem(), ao);
        }

        for (ApplicationSystem as : aoByAs.keySet()) {
            ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
            for (ApplicationOption ao : aoByAs.get(as)) {
                asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang, uiLang));
            }
            dto.getApplicationSystems().add(asDTO);
        }
        
        return dto;
    }

}
