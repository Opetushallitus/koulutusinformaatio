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

import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLearningOpportunitySpecificationDTO;

/**
 * @author Mikko Majapuro
 */
public final class ChildLOSToDTO {

    private ChildLOSToDTO() {
    }

    public static ChildLearningOpportunitySpecificationDTO convert(final ChildLOS childLOS, final String lang, final String uiLang, final String defaultLang) {
        if (childLOS != null) {
            ChildLearningOpportunitySpecificationDTO child = new ChildLearningOpportunitySpecificationDTO();
            child.setId(childLOS.getId());
            child.setName(ConverterUtil.getTextByLanguage(childLOS.getName(), defaultLang));
            child.setQualification(ConverterUtil.getTextByLanguage(childLOS.getQualification(), uiLang));
            child.setLois(ChildLOIToDTO.convert(childLOS.getLois(), lang, uiLang, defaultLang));
            child.setParent(ParentLOSRefToDTO.convert(childLOS.getParent(), defaultLang));
            child.setGoals(ConverterUtil.getTextByLanguage(childLOS.getGoals(), lang));
            child.setTranslationLanguage(lang);
            return child;
        } else {
            return null;
        }
    }

}
