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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.UpperSecondaryLearningOpportunityInstanceDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class UpperSecondaryLOIToDTO {

    private UpperSecondaryLOIToDTO() {
    }

    private static UpperSecondaryLearningOpportunityInstanceDTO convert(UpperSecondaryLOI loi, String lang, String uiLang, String defaultLang) {
        UpperSecondaryLearningOpportunityInstanceDTO dto =
                new UpperSecondaryLearningOpportunityInstanceDTO();

        dto.setId(loi.getId());
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(loi.getShortName(), defaultLang));
        dto.setPrerequisite(CodeToDTO.convert(loi.getPrerequisite(), lang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguage(loi.getFormOfTeaching(), uiLang));
        dto.setTimeOfTeaching(ConverterUtil.getTextsByLanguage(loi.getTimeOfTeaching(), uiLang));
        dto.setPlaceOfTeaching(ConverterUtil.getTextsByLanguage(loi.getPlaceOfTeaching(), uiLang));
        dto.setTeachingLanguages(CodeToName.convertAll(loi.getTeachingLanguages(), uiLang));
        dto.setFormOfEducation(ConverterUtil.getTextsByLanguage(loi.getFormOfEducation(), uiLang));
        dto.setStartDate(loi.getStartDate());
        dto.setStartYear(loi.getStartYear());
        dto.setStartSeason(ConverterUtil.getTextByLanguageUseFallbackLang(loi.getStartSeason(), uiLang));
        dto.setInternationalization(ConverterUtil.getTextByLanguage(loi.getInternationalization(), lang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(loi.getCooperation(), lang));
        dto.setContent(ConverterUtil.getTextByLanguage(loi.getContent(), lang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(loi.getContactPersons()));
        dto.setDiplomas(ConverterUtil.getTextsByLanguage(loi.getDiplomas(), lang));
        dto.setPlannedDuration(loi.getPlannedDuration());
        dto.setLanguageSelection(LanguageSelectionToDTO.convertAll(loi.getLanguageSelection(), lang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(loi.getContactPersons()));
        dto.setDiplomas(ConverterUtil.getTextsByLanguage(loi.getDiplomas(), lang));
        dto.setPlannedDuration(loi.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(loi.getPlannedDurationUnit(), uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(loi.getAvailableTranslationLanguages(), uiLang));

        // as based approach for UI
        SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        for (ApplicationOption ao : loi.getApplicationOptions()) {
            aoByAs.put(ao.getApplicationSystem(), ao);
        }

        for (ApplicationSystem as : aoByAs.keySet()) {
            ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
            for (ApplicationOption ao : aoByAs.get(as)) {
                asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convert(ao, lang, uiLang, defaultLang));
            }
            dto.getApplicationSystems().add(asDTO);
        }


        return dto;
    }

    public static List<UpperSecondaryLearningOpportunityInstanceDTO> convertAll(final List<UpperSecondaryLOI> lois,
                                                                                final String lang, final String uiLang, final String defaultLang) {
        return Lists.transform(lois, new Function<UpperSecondaryLOI, UpperSecondaryLearningOpportunityInstanceDTO>() {
            @Override
            public UpperSecondaryLearningOpportunityInstanceDTO apply(UpperSecondaryLOI loi) {
                return convert(loi, lang, uiLang, defaultLang);
            }
        });
    }
}
