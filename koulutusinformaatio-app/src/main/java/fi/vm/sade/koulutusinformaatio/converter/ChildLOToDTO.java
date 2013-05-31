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

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunityDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLOSRefDTO;

/**
 * @author Mikko Majapuro
 */
public class ChildLOToDTO {

    public static ChildLearningOpportunityDTO convert(final ChildLO childLO, final String lang) {
        ChildLearningOpportunityDTO child = new ChildLearningOpportunityDTO();
        child.setLosId(childLO.getLosId());
        child.setLoiId(childLO.getLoiId());
        child.setName(ConverterUtil.getTextByLanguage(childLO.getName(), lang));
        child.setDegreeTitle(ConverterUtil.getTextByLanguage(childLO.getDegreeTitle(), lang));
        child.setQualification(ConverterUtil.getTextByLanguage(childLO.getQualification(), lang));
        child.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(childLO.getName()));
        child.setApplicationOption(ApplicationOptionToDTO.convert(childLO.getApplicationOption(), lang));
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
        child.setPrerequisite(ConverterUtil.getTextByLanguage(childLO.getPrerequisite(), lang));
        child.setTranslationLanguage(lang);
        return child;
    }

}
