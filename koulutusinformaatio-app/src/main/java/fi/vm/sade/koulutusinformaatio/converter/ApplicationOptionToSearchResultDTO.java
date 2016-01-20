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

import java.util.Collections;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Mikko Majapuro
 */
public final class ApplicationOptionToSearchResultDTO {

    private ApplicationOptionToSearchResultDTO() {
    }

    public static ApplicationOptionSearchResultDTO convert(final ApplicationOption applicationOption, final String lang, final String uiLang) {
        if (applicationOption != null) {
            ApplicationOptionSearchResultDTO dto = new ApplicationOptionSearchResultDTO();
            dto.setId(applicationOption.getId());
            String name = ConverterUtil.getTextByLanguage(applicationOption.getName(), lang);
            if (Strings.isNullOrEmpty(name)) {
                name = ConverterUtil.getTextByLanguageUseFallbackLang(applicationOption.getName(), uiLang);
            }
            dto.setName(name);
            dto.setAoIdentifier(applicationOption.getAoIdentifier());
            dto.setChildLONames(ConverterUtil.getTextsByLanguage(ChildLOIRefToDTO.convert(applicationOption.getChildLOIRefs()), lang));
            Collections.sort(dto.getChildLONames());
            dto.setEducationDegree(applicationOption.getEducationDegree());
            dto.setSora(applicationOption.isSora());
            dto.setTeachingLanguages(applicationOption.getTeachingLanguages());
            dto.setTeachingLanguageNames(ConverterUtil.getTextsByLanguage(applicationOption.getTeachingLanguageNames(),lang));
            dto.setKaksoistutkinto(applicationOption.isKaksoistutkinto());
            dto.setVocational(applicationOption.isVocational());
            dto.setKysytaanHarkinnanvaraiset(applicationOption.isKysytaanHarkinnanvaraiset());
            dto.setEducationCodeUri(applicationOption.getEducationCodeUri());
            dto.setOrganizationGroups(OrganizationGroupToDTO.convertAll(applicationOption.getOrganizationGroups()));
            dto.setAttachments(ApplicationOptionAttachmentToDTO.convertAll(applicationOption.getAttachments(), lang));
            dto.setRequiredBaseEducations(applicationOption.getRequiredBaseEducations());
            if (applicationOption.getProvider() != null) {
                dto.setAthleteEducation(applicationOption.getProvider().isAthleteEducation() || applicationOption.isAthleteEducation());
            } else {
                dto.setAthleteEducation(applicationOption.isAthleteEducation());
            }
            return dto;
        }
        return null;
    }
}
