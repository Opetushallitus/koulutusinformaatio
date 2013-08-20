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

import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunitySpecificationDTO;

/**
 * @author Mikko Majapuro
 */
public class ParentLOSToDTO {

    public static ParentLearningOpportunitySpecificationDTO convert(final ParentLOS parentLOS, final String lang) {
        ParentLearningOpportunitySpecificationDTO parent = new ParentLearningOpportunitySpecificationDTO();
        parent.setId(parentLOS.getId());
        parent.setName(ConverterUtil.getTextByLanguage(parentLOS.getName(), lang));
        parent.setEducationDegree(parentLOS.getEducationDegree());
        parent.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(parentLOS.getName()));
        parent.setProvider(ProviderToDTO.convert(parentLOS.getProvider(), lang));
        parent.setStructureDiagram(ConverterUtil.getTextByLanguage(parentLOS.getStructureDiagram(), lang));
        parent.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(parentLOS.getAccessToFurtherStudies(), lang));
        parent.setGoals(ConverterUtil.getTextByLanguage(parentLOS.getGoals(), lang));
        parent.setEducationDomain(ConverterUtil.getTextByLanguage(parentLOS.getEducationDomain(), lang));
        parent.setStydyDomain(ConverterUtil.getTextByLanguage(parentLOS.getStydyDomain(), lang));
        parent.setTranslationLanguage(lang);
        parent.setAvailableTranslationLanguages(ConverterUtil.getAvailableTranslationLanguages(parentLOS.getName()));
        parent.setCreditValue(parentLOS.getCreditValue());
        parent.setCreditUnit(ConverterUtil.getTextByLanguage(parentLOS.getCreditUnit(), lang));

        if (parentLOS.getLois() != null) {
            for (ParentLOI loi : parentLOS.getLois()) {
                parent.getLois().add(ParentLOIToDTO.convert(loi, lang));
            }
        }
        return parent;
    }
}
