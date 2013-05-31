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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;

/**
 * @author Mikko Majapuro
 */
public class ParentLOToDTO {

    public static ParentLearningOpportunitySpecificationDTO convert(final ParentLO parentLO, final String lang) {
        ParentLearningOpportunitySpecificationDTO parent = new ParentLearningOpportunitySpecificationDTO();
        parent.setId(parentLO.getId());
        parent.setName(ConverterUtil.getTextByLanguage(parentLO.getName(), lang));
        parent.setEducationDegree(ConverterUtil.getTextByLanguage(parentLO.getEducationDegree(), lang));
        parent.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(parentLO.getName()));
        parent.setChildren(ChildLORefToDTO.convert(parentLO.getChildRefs(), lang));
        parent.setProvider(ProviderToDTO.convert(parentLO.getProvider(), lang));
        parent.setStructureDiagram(ConverterUtil.getTextByLanguage(parentLO.getStructureDiagram(), lang));
        parent.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(parentLO.getAccessToFurtherStudies(), lang));
        parent.setGoals(ConverterUtil.getTextByLanguage(parentLO.getGoals(), lang));
        parent.setEducationDomain(ConverterUtil.getTextByLanguage(parentLO.getEducationDomain(), lang));
        parent.setStydyDomain(ConverterUtil.getTextByLanguage(parentLO.getStydyDomain(), lang));
        parent.setTranslationLanguage(lang);
        parent.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(parentLO.getName()));

        if (parentLO.getApplicationOptions() != null) {
            for (ApplicationOption ao : parentLO.getApplicationOptions()) {
                parent.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang));
            }
        }
        if (parentLO.getLois() != null) {
            for (ParentLOI loi : parentLO.getLois()) {
                parent.getLois().add(ParentLOIToDTO.convert(loi, lang));
            }
        }
        return parent;
    }
}
