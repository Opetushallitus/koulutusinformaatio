package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UniversityAppliedScienceLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UniversityChildLosReferenceDTO;

public class UniversityAppliedScienceLOSToDTO {
	
	public UniversityAppliedScienceLOSToDTO() {
		
	}
	
	public static UniversityAppliedScienceLOSDTO convert(
            final UniversityAppliedScienceLOS los, final String lang, final String uiLang) {
		UniversityAppliedScienceLOSDTO dto =
                new UniversityAppliedScienceLOSDTO();
        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), lang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setEducationDegreeName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegreeName(), lang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), lang));
        dto.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(los.getQualification(), lang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), lang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), lang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), lang));
        dto.setInfoAboutTeachingLangs(ConverterUtil.getTextByLanguage(los.getInfoAboutTeachingLangs(), lang));
        dto.setMajorSelection(ConverterUtil.getTextByLanguage(los.getMajorSelection(), lang));
        dto.setInfoAboutCharge(ConverterUtil.getTextByLanguage(los.getInfoAboutCharge(), lang));
        dto.setInfoAboutCharge(ConverterUtil.getTextByLanguage(los.getInfoAboutCharge(), lang));
        dto.setFinalExam(ConverterUtil.getTextByLanguage(los.getFinalExam(), lang));
        dto.setCareerOpportunities(ConverterUtil.getTextByLanguage(los.getCareerOpportunities(), lang));
        dto.setCompetence(ConverterUtil.getTextByLanguage(los.getCompetence(), lang));
        dto.setResearchFocus(ConverterUtil.getTextByLanguage(los.getResearchFocus(), lang));
        
        //dto.setLois(UpperSecondaryLOIToDTO.convertAll(los.getLois(), lang, uiLang));
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), lang, "fi"));
        dto.setTranslationLanguage(lang);
        dto.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(los.getGoals()));
        dto.setCreditValue(los.getCreditValue());
        
        dto.setCreditUnit(ConverterUtil.getTextByLanguage(los.getCreditUnit(), uiLang));
        
        //DO MORE
        
        dto.setPrerequisites(CodeToDTO.convertAll(los.getPrerequisites(), lang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguage(los.getFormOfTeaching(), uiLang));
        dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(los.getProfessionalTitles(), uiLang));
        dto.setTeachingTimes(ConverterUtil.getTextsByLanguage(los.getTeachingTimes(), uiLang));
        dto.setTeachingPlaces(ConverterUtil.getTextsByLanguage(los.getTeachingPlaces(), uiLang));
        dto.setTeachingLanguages(CodeToValue.convertAll(los.getTeachingLanguages()));
        //TODO: --> dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(los.getFormOfEducation(), uiLang));
        dto.setStartDate(los.getStartDate());
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), lang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(), lang));
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), lang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(los.getContactPersons()));
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), lang));
        
     // as based approach for UI
        
        if (los.getApplicationOptions() != null) {
        	SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        	for (ApplicationOption ao : los.getApplicationOptions()) {
        		aoByAs.put(ao.getApplicationSystem(), ao);
        	}

        	for (ApplicationSystem as : aoByAs.keySet()) {
        		ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
        		for (ApplicationOption ao : aoByAs.get(as)) {
        			asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang, uiLang, "fi"));
        		}
        		dto.getApplicationSystems().add(asDTO);
        	}
        }
        dto.setChargeable(los.getChargeable());
        dto.setChildren(convertReferences(los.getChildren(), lang));
        dto.setParents(convertReferences(los.getParents(), lang));
        return dto;
    }

	private static List<UniversityChildLosReferenceDTO> convertReferences(
			List<UniversityAppliedScienceLOS> children, String lang) {
		List<UniversityChildLosReferenceDTO> results = new ArrayList<UniversityChildLosReferenceDTO>();
		for (UniversityAppliedScienceLOS curChild : children) {
			UniversityChildLosReferenceDTO childDto = new UniversityChildLosReferenceDTO();
			childDto.setId(curChild.getId());
			childDto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(curChild.getName(), lang));
			childDto.setEducationDegree(curChild.getEducationDegree());
			results.add(childDto);
		}
		
		return results;
	}

}
