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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class LOSToSearchResult {
    private static final Logger LOG = LoggerFactory.getLogger(LOSToSearchResult.class);
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
        dto.setId(los.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), lang));
        if (los instanceof KoulutusLOS) {
            dto.setType(TarjontaConstants.TYPE_KOULUTUS);
        } else if( los instanceof TutkintoLOS){
            dto.setType(TarjontaConstants.TYPE_PARENT);
        } else if( los instanceof HigherEducationLOS){
            dto.setType(TarjontaConstants.TYPE_KK);
        } else if( los instanceof CompetenceBasedQualificationParentLOS){
            dto.setType(TarjontaConstants.TYPE_ADULT_VOCATIONAL);
        } else {
            LOG.warn("Ei osattu päätellä tyyppiä koulutukselle: " + los.getId());
        }
        return dto;
    }
}
