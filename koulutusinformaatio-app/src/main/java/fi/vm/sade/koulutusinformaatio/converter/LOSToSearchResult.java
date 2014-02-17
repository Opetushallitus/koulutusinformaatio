/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class LOSToSearchResult {
    private LOSToSearchResult() {
    }

    public static List<LearningOpportunitySearchResultDTO> convert(final List<LOS> losses, final String lang) {
        return Lists.transform(losses, new Function<LOS, LearningOpportunitySearchResultDTO>() {
            @Override
            public LearningOpportunitySearchResultDTO apply(LOS input) {
                return (input != null) ? convert(input, lang) : null;
            }
        });
    }

    private static LearningOpportunitySearchResultDTO convert(LOS los, String lang) {
        LearningOpportunitySearchResultDTO dto = new LearningOpportunitySearchResultDTO();
        if (los instanceof ParentLOS) {
            dto.setId(((ParentLOS) los).getId());
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(
                ((ParentLOS) los).getName(), lang));
            dto.setType(TarjontaConstants.TYPE_PARENT.toLowerCase());
        }
        else if (los instanceof ChildLOS) {
            dto.setId(((ChildLOS) los).getId());
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(
                    ((ChildLOS) los).getName(), lang));
            dto.setType(TarjontaConstants.TYPE_CHILD.toLowerCase());
        }
        else if (los instanceof UpperSecondaryLOS) {
            dto.setId(((UpperSecondaryLOS) los).getId());
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(
                    ((UpperSecondaryLOS) los).getName(), lang));
            dto.setType(TarjontaConstants.TYPE_UPSEC.toLowerCase());
        }
        else {
            dto.setId(((SpecialLOS) los).getId());
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(
                    ((SpecialLOS) los).getName(), lang));
            dto.setType(TarjontaConstants.TYPE_SPECIAL.toLowerCase());
        }
        return dto;
    }
}
