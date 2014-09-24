package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.I18nPicture;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
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
        
        dto.setId(los.getId());
        
        String descriptionLang = getDescriptionLang(lang, los.getAvailableTranslationLanguages());
        descriptionLang = (descriptionLang) == null ? lang : descriptionLang;
        dto.setTranslationLanguage(descriptionLang);
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setEducationDegreeName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegreeLang(), uiLang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), uiLang));
        dto.setQualifications(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getQualifications(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), descriptionLang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), descriptionLang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), descriptionLang));
        dto.setInfoAboutTeachingLangs(ConverterUtil.getTextByLanguage(los.getInfoAboutTeachingLangs(), descriptionLang));
        dto.setMajorSelection(ConverterUtil.getTextByLanguage(los.getMajorSelection(), descriptionLang));
        dto.setInfoAboutCharge(ConverterUtil.getTextByLanguage(los.getInfoAboutCharge(), descriptionLang));
        //dto.setInfoAboutCharge(ConverterUtil.getTextByLanguageUseFallbackLang(los.getInfoAboutCharge(), uiLang));
        dto.setFinalExam(ConverterUtil.getTextByLanguage(los.getFinalExam(), descriptionLang));
        dto.setCareerOpportunities(ConverterUtil.getTextByLanguage(los.getCareerOpportunities(), descriptionLang));
        dto.setCompetence(ConverterUtil.getTextByLanguage(los.getCompetence(), descriptionLang));
        dto.setResearchFocus(ConverterUtil.getTextByLanguage(los.getResearchFocus(), descriptionLang));

        //dto.setLois(UpperSecondaryLOIToDTO.convertAll(los.getLois(), uiLang, uiLang));
        dto.setProvider(ProviderToDTO.convert(los.getProvider(), uiLang, "fi", uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(los.getAvailableTranslationLanguages(), uiLang));
        dto.setCreditValue(los.getCreditValue());

        dto.setCreditUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getCreditUnit(), uiLang));

        //DO MORE
        dto.setPrerequisites(CodeToDTO.convertAll(los.getPrerequisites(), uiLang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getFormOfTeaching(), uiLang));
        dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(los.getProfessionalTitles(), descriptionLang));
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
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), uiLang));

        // as based approach for UI
        String defLang = los.getTeachingLanguages().size() == 1 && los.getTeachingLanguages().get(0) != null ? los.getTeachingLanguages().get(0).getValue() : uiLang;

        if (los.getApplicationOptions() != null) {
            SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
            for (ApplicationOption ao : los.getApplicationOptions()) {
                aoByAs.put(ao.getApplicationSystem(), ao);
            }

            for (ApplicationSystem as : aoByAs.keySet()) {
                ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
                asDTO.setStatus(as.getStatus());
                for (ApplicationOption ao : aoByAs.get(as)) {
                    asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convertHigherEducation(ao, lang, uiLang, defLang));
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
        dto.setStructureImageId(getStructureImageId(los, descriptionLang));
        return dto;
    }

    public static String getDescriptionLang(String lang,
            List<Code> availableTranslationLanguages) {
        
        boolean uiLangHasDescription = false;
        boolean fiHasDescription = false;
        if (availableTranslationLanguages == null) {
            return lang;
        }
        for (Code curLangCode : availableTranslationLanguages) {
            if (curLangCode != null && curLangCode.getValue().equalsIgnoreCase(lang)) {
                uiLangHasDescription = true;
            }
            
            if (curLangCode != null && curLangCode.getValue().equalsIgnoreCase("fi")) {
                fiHasDescription = true;
            }
        }
        
        if (uiLangHasDescription) {
            return lang;
        }
        if (fiHasDescription) {
            return "fi";
        }
        if (!availableTranslationLanguages.isEmpty()) {
            return availableTranslationLanguages.get(0).getValue().toLowerCase();
        }
        return lang;
    }

    private static String getStructureImageId(HigherEducationLOS los,
            String uiLang) {
        I18nPicture image = los.getStructureImage();
        Picture pict = null;
        if (image != null 
                && image.getPictureTranslations() != null 
                && !image.getPictureTranslations().isEmpty()) {
            pict = image.getPictureTranslations().get(uiLang);
        }
        return pict != null ? pict.getId() : null;
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
