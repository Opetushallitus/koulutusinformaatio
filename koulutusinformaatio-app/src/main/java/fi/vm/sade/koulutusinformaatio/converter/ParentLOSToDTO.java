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
public final class ParentLOSToDTO {

    private ParentLOSToDTO() {
    }

    public static ParentLearningOpportunitySpecificationDTO convert(final ParentLOS parentLOS, 
                                                                    final String lang, 
                                                                    final String uiLang, 
                                                                    final String defaultLang) {
        
        ParentLearningOpportunitySpecificationDTO parent = new ParentLearningOpportunitySpecificationDTO();
        parent.setId(parentLOS.getId());
        parent.setName(ConverterUtil.getTextByLanguage(parentLOS.getName(), defaultLang));
        parent.setEducationDegree(parentLOS.getEducationDegree());
        parent.setProvider(ProviderToDTO.convert(parentLOS.getProvider(), lang, defaultLang, uiLang));
        parent.setStructure(ConverterUtil.getTextByLanguage(parentLOS.getStructure(), lang));
        parent.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(parentLOS.getAccessToFurtherStudies(), lang));
        parent.setGoals(ConverterUtil.getTextByLanguage(parentLOS.getGoals(), lang));
        parent.setEducationDomain(ConverterUtil.getTextByLanguage(parentLOS.getEducationDomain(), uiLang));
        parent.setStydyDomain(ConverterUtil.getTextByLanguage(parentLOS.getStydyDomain(), uiLang));
        parent.setTranslationLanguage(lang);
        parent.setCreditValue(parentLOS.getCreditValue());
        parent.setCreditUnit(ConverterUtil.getTextByLanguage(parentLOS.getCreditUnit(), uiLang));

        if (parentLOS.getLois() != null) {
            for (ParentLOI loi : parentLOS.getLois()) {
                parent.getLois().add(ParentLOIToDTO.convert(loi, lang, uiLang, defaultLang));
            }
        }
        
        if (parentLOS.getThemes() != null) {
            parent.setThemes(CodeToDTO.convertCodesDistinct(parentLOS.getThemes(), uiLang));
        }
        if (parentLOS.getTopics() != null) {
            parent.setTopics(CodeToDTO.convertAll(parentLOS.getTopics(), uiLang));
        }
        
        parent.setContainsPseudChildLOS(true);
        
        return parent;
    }
}
