package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityInstanceDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ChildLOIToDTO {

    private ChildLOIToDTO() {
    }

    public static ChildLearningOpportunityInstanceDTO convert(ChildLOI childLOI, String lang, String uiLang, String defaultLang) {
        if (childLOI != null) {
            ChildLearningOpportunityInstanceDTO dto = new ChildLearningOpportunityInstanceDTO();
            dto.setId(childLOI.getId());
            dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(childLOI.getAvailableTranslationLanguages(), uiLang));
            dto.setStartDate(childLOI.getStartDate());
            dto.setStartDates(childLOI.getStartDates());
            dto.setStartYear(childLOI.getStartYear());
            dto.setStartSeason(ConverterUtil.getTextByLanguageUseFallbackLang(childLOI.getStartSeason(), uiLang));
            dto.setTeachingLanguages(CodeToName.convertAll(childLOI.getTeachingLanguages(), uiLang));
            dto.setRelated(ChildLOIRefToDTO.convert(childLOI.getRelated(), defaultLang));
            dto.setFormOfTeaching(ConverterUtil.getTextsByLanguage(childLOI.getFormOfTeaching(), uiLang));
            dto.setTimeOfTeaching(ConverterUtil.getTextsByLanguage(childLOI.getTimeOfTeaching(), uiLang));
            dto.setPlaceOfTeaching(ConverterUtil.getTextsByLanguage(childLOI.getPlaceOfTeaching(), uiLang));
            dto.setWebLinks(childLOI.getWebLinks());
            dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(childLOI.getFormOfEducation(), uiLang));
            dto.setPrerequisite(CodeToDTO.convert(childLOI.getPrerequisite(), uiLang));
            dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(childLOI.getProfessionalTitles(), lang));
            dto.setWorkingLifePlacement(ConverterUtil.getTextByLanguage(childLOI.getWorkingLifePlacement(), lang));
            dto.setInternationalization(ConverterUtil.getTextByLanguage(childLOI.getInternationalization(), lang));
            dto.setCooperation(ConverterUtil.getTextByLanguage(childLOI.getCooperation(), lang));
            dto.setContent(ConverterUtil.getTextByLanguage(childLOI.getContent(), lang));
            dto.setSelectingDegreeProgram(ConverterUtil.getTextByLanguage(childLOI.getSelectingDegreeProgram(), lang));
            dto.setPlannedDuration(childLOI.getPlannedDuration());
            dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(childLOI.getPlannedDurationUnit(), uiLang));

            // as based approach for UI
            SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
            for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                aoByAs.put(ao.getApplicationSystem(), ao);
            }

            for (ApplicationSystem as : aoByAs.keySet()) {
                ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
                for (ApplicationOption ao : aoByAs.get(as)) {
                    asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang, uiLang, defaultLang));
                }
                dto.getApplicationSystems().add(asDTO);
            }

            if (childLOI.getContactPersons() != null) {
                for (ContactPerson contactPerson : childLOI.getContactPersons()) {
                    dto.getContactPersons().add(ContactPersonToDTO.convert(contactPerson));
                }
            }
            
            if (childLOI.getTargetGroup() != null 
                    && childLOI.getTargetGroup().getTranslations() != null 
                    && !childLOI.getTargetGroup().getTranslations().isEmpty()) {
                dto.setTargetGroup(ConverterUtil.getTextByLanguage(childLOI.getTargetGroup(), lang));
            }

            return dto;
        }
        else {
            return null;
        }
    }

    public static List<ChildLearningOpportunityInstanceDTO> convert(final List<ChildLOI> childLOIs, 
                                                                    final String lang, 
                                                                    final String uiLang, 
                                                                    final String defaultLang) {
        if (childLOIs != null) {
            return Lists.transform(childLOIs, new Function<ChildLOI, ChildLearningOpportunityInstanceDTO>() {
                @Override
                public ChildLearningOpportunityInstanceDTO apply(fi.vm.sade.koulutusinformaatio.domain.ChildLOI input) {
                    return convert(input, lang, uiLang, defaultLang);
                }
            });
        }
        else {
            return null;
        }
    }

}
