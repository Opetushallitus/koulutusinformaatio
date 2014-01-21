package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
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
        
        return dto;
    }

}
