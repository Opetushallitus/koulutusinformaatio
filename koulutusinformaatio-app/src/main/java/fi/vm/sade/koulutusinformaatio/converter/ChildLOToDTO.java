/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityDTO;

/**
 * @author Mikko Majapuro
 */
public class ChildLOToDTO {

    public static ChildLearningOpportunityDTO convert(final ChildLearningOpportunity childLO, final String lang) {
        ChildLearningOpportunityDTO child = new ChildLearningOpportunityDTO();
        child.setId(childLO.getId());
        child.setName(ConverterUtil.getTextByLanguage(childLO.getName(), lang));
        child.setDegreeTitle(ConverterUtil.getTextByLanguage(childLO.getDegreeTitle(), lang));
        child.setQualification(ConverterUtil.getTextByLanguage(childLO.getQualification(), lang));
        child.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(childLO.getName()));
        child.setStartDate(childLO.getStartDate());
        if (childLO.getTeachingLanguages() != null) {
            for (Code code : childLO.getTeachingLanguages()) {
                child.getTeachingLanguages().add(code.getValue());
            }
        }
        child.setRelated(ChildLORefToDTO.convert(childLO.getRelated(), lang));
        child.setParent(ParentLOSRefToDTO.convert(childLO.getParent(), lang));
        child.setFormOfTeaching(ConverterUtil.getTextsByLanguage(childLO.getFormOfTeaching(), lang));
        child.setWebLinks(childLO.getWebLinks());
        child.setFormOfEducation(ConverterUtil.getTextsByLanguage(childLO.getFormOfEducation(), lang));
        child.setPrerequisite(CodeToDTO.convert(childLO.getPrerequisite(), lang));
        child.setTranslationLanguage(lang);
        child.setProfessionalTitles(ConverterUtil.getTextsByLanguage(childLO.getProfessionalTitles(), lang));
        child.setWorkingLifePlacement(ConverterUtil.getTextByLanguage(childLO.getWorkingLifePlacement(), lang));
        child.setInternationalization(ConverterUtil.getTextByLanguage(childLO.getInternationalization(), lang));
        child.setCooperation(ConverterUtil.getTextByLanguage(childLO.getCooperation(), lang));
        child.setDegreeGoal(ConverterUtil.getTextByLanguage(childLO.getDegreeGoal(), lang));
        child.setContent(ConverterUtil.getTextByLanguage(childLO.getContent(), lang));

        // as based approach for UI
        SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        for (ApplicationOption ao : childLO.getApplicationOptions()) {
            aoByAs.put(ao.getApplicationSystem(), ao);
        }

        for (ApplicationSystem as : aoByAs.keySet()) {
            ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, lang);
            for (ApplicationOption ao : aoByAs.get(as)) {
                asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang));
            }
            child.getApplicationSystems().add(asDTO);
        }

//        child.setApplicationOptions(Lists.transform(childLO.getApplicationOptions(), new Function<ApplicationOption, ApplicationOptionDTO>() {
//            @Override
//            public ApplicationOptionDTO apply(fi.vm.sade.koulutusinformaatio.domain.ApplicationOption input) {
//                return ApplicationOptionToDTO.convert(input, lang);
//            }
//        }));

        return child;
    }

}
