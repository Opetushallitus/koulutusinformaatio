package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityInstanceDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class ChildLOIToDTO {

    public static ChildLearningOpportunityInstanceDTO convert(ChildLOI childLOI, String lang) {
        ChildLearningOpportunityInstanceDTO dto = new ChildLearningOpportunityInstanceDTO();
        dto.setId(childLOI.getId());
        dto.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(childLOI.getName()));
        dto.setStartDate(childLOI.getStartDate());
        if (childLOI.getTeachingLanguages() != null) {
            for (Code code : childLOI.getTeachingLanguages()) {
                dto.getTeachingLanguages().add(code.getValue());
            }
        }
        dto.setRelated(ChildLOIRefToDTO.convert(childLOI.getRelated(), lang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguage(childLOI.getFormOfTeaching(), lang));
        dto.setWebLinks(childLOI.getWebLinks());
        dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(childLOI.getFormOfEducation(), lang));
        dto.setPrerequisite(CodeToDTO.convert(childLOI.getPrerequisite(), lang));
        dto.setTranslationLanguage(lang);
        dto.setProfessionalTitles(ConverterUtil.getTextsByLanguage(childLOI.getProfessionalTitles(), lang));
        dto.setWorkingLifePlacement(ConverterUtil.getTextByLanguage(childLOI.getWorkingLifePlacement(), lang));
        dto.setInternationalization(ConverterUtil.getTextByLanguage(childLOI.getInternationalization(), lang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(childLOI.getCooperation(), lang));
        dto.setContent(ConverterUtil.getTextByLanguage(childLOI.getContent(), lang));

        // as based approach for UI
        SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            aoByAs.put(ao.getApplicationSystem(), ao);
        }

        for (ApplicationSystem as : aoByAs.keySet()) {
            ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, lang);
            for (ApplicationOption ao : aoByAs.get(as)) {
                asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang));
            }
            dto.getApplicationSystems().add(asDTO);
        }

        return dto;
    }

    public static List<ChildLearningOpportunityInstanceDTO> convert(final List<ChildLOI> childLOIs, final String lang) {

        return Lists.transform(childLOIs, new Function<ChildLOI, ChildLearningOpportunityInstanceDTO>() {
            @Override
            public ChildLearningOpportunityInstanceDTO apply(fi.vm.sade.koulutusinformaatio.domain.ChildLOI input) {
                return convert(input, lang);
            }
        });

    }

}
