package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationChildLosReferenceDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;

import java.util.ArrayList;
import java.util.List;

public class HigherEducationLOSToDTO {

    public HigherEducationLOSToDTO() {

    }

    public static HigherEducationLOSDTO convert(
            final HigherEducationLOS los, final String lang, final String uiLang) {
        HigherEducationLOSDTO dto =
                new HigherEducationLOSDTO();
        dto.setTranslationLanguage(lang);
        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setEducationDegreeName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegreeLang(), uiLang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), uiLang));
        dto.setQualifications(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getQualifications(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), lang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), lang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), lang));
        dto.setInfoAboutTeachingLangs(ConverterUtil.getTextByLanguage(los.getInfoAboutTeachingLangs(), lang));
        dto.setMajorSelection(ConverterUtil.getTextByLanguage(los.getMajorSelection(), lang));
        dto.setInfoAboutCharge(ConverterUtil.getTextByLanguage(los.getInfoAboutCharge(), lang));
        //dto.setInfoAboutCharge(ConverterUtil.getTextByLanguageUseFallbackLang(los.getInfoAboutCharge(), uiLang));
        dto.setFinalExam(ConverterUtil.getTextByLanguage(los.getFinalExam(), lang));
        dto.setCareerOpportunities(ConverterUtil.getTextByLanguage(los.getCareerOpportunities(), lang));
        dto.setCompetence(ConverterUtil.getTextByLanguage(los.getCompetence(), lang));
        dto.setResearchFocus(ConverterUtil.getTextByLanguage(los.getResearchFocus(), lang));

        //dto.setLois(UpperSecondaryLOIToDTO.convertAll(los.getLois(), uiLang, uiLang));
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), uiLang, "fi", uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(los.getAvailableTranslationLanguages(), uiLang));
        dto.setCreditValue(los.getCreditValue());

        dto.setCreditUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getCreditUnit(), uiLang));

        //DO MORE
        dto.setPrerequisites(CodeToDTO.convertAll(los.getPrerequisites(), uiLang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getFormOfTeaching(), uiLang));
        dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(los.getProfessionalTitles(), lang));
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
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), lang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(),lang));
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), lang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(los.getContactPersons()));
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), uiLang));

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
        dto.setChargeable(los.getChargeable());
        dto.setChildren(convertReferences(los.getChildren(), uiLang));
        dto.setParents(convertReferences(los.getParents(), uiLang));
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
        return dto;
    }

    private static List<HigherEducationChildLosReferenceDTO> convertReferences(
            List<HigherEducationLOS> children, String lang) {
        List<HigherEducationChildLosReferenceDTO> results = new ArrayList<HigherEducationChildLosReferenceDTO>();
        for (HigherEducationLOS curChild : children) {
            HigherEducationChildLosReferenceDTO childDto = new HigherEducationChildLosReferenceDTO();
            childDto.setId(curChild.getId());
            childDto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(curChild.getName(), lang));
            childDto.setEducationDegree(curChild.getEducationDegree());
            childDto.setStatus(curChild.getStatus());
            results.add(childDto);
        }

        return results;
    }

}
