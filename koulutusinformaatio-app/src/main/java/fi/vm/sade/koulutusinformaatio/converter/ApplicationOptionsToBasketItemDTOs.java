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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public final class ApplicationOptionsToBasketItemDTOs {

    private ApplicationOptionsToBasketItemDTOs() {
    }

    private final static String FALLBACK_LANG = "fi";

    public static List<BasketItemDTO> convert(final List<ApplicationOption> aos, String uiLang) {
        if (aos != null) {

            Map<String, BasketItemDTO> items = Maps.newHashMap();
            for (ApplicationOption ao : aos) {

                String lang = null;
                try {
                    lang = ao.getTeachingLanguages().get(0).toLowerCase();
                } catch (Exception e) {
                    lang = FALLBACK_LANG;
                }
                if (Strings.isNullOrEmpty(lang)) {
                    lang = FALLBACK_LANG;
                }

                BasketApplicationOptionDTO aoDTO = new BasketApplicationOptionDTO();
                aoDTO.setId(ao.getId());
                aoDTO.setType(ao.getType());
                aoDTO.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getName(), lang));
                aoDTO.setEducationDegree(ao.getEducationDegree());
                aoDTO.setSora(ao.isSora());
                aoDTO.setTeachingLanguages(ao.getTeachingLanguages());
                aoDTO.setParent(ParentLOSRefToDTO.convert(ao.getParent(), lang));
                aoDTO.setChildren(ChildLOIRefToDTO.convert(ao.getChildLOIRefs(), lang));
                aoDTO.setAttachmentDeliveryDeadline(ao.getAttachmentDeliveryDeadline());
                aoDTO.setAttachments(ApplicationOptionAttachmentToDTO.convertAll(ao.getAttachments(), lang));
                aoDTO.setExams(ExamToDTO.convertAll(ao.getExams(), lang));
                aoDTO.setAoIdentifier(ao.getAoIdentifier());
                aoDTO.setKaksoistutkinto(ao.isKaksoistutkinto());
                aoDTO.setVocational(ao.isVocational());
                aoDTO.setEducationCodeUri(ao.getEducationCodeUri());
                aoDTO.setEducationTypeUri(ao.getEducationTypeUri());
                ParentLOSRef los = ao.getParent();
                if (los != null) {
                	aoDTO.setHigherEducation(TarjontaConstants.TYPE_KK.equals(los.getLosType()));
                }
                Provider provider = ao.getProvider();
                if (provider != null) {
                    aoDTO.setProviderId(provider.getId());
                    aoDTO.setAthleteEducation(provider.isAthleteEducation() || ao.isAthleteEducation());
                    aoDTO.setProviderName(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), lang));
                    if (provider.getVisitingAddress() != null) {
                        aoDTO.setProviderLocation(provider.getVisitingAddress().getPostOffice());
                    }
                }
                ApplicationSystem as = ao.getApplicationSystem();
                if (as != null && items.containsKey(as.getId())) {
                    items.get(as.getId()).getApplicationOptions().add(aoDTO);
                } else if (as != null) {
                    BasketItemDTO basketItem = new BasketItemDTO();
                    basketItem.setMaxApplicationOptions(as.getMaxApplications());
                    basketItem.setApplicationSystemId(as.getId());
                    basketItem.getApplicationOptions().add(aoDTO);
                    basketItem.setApplicationSystemName(ConverterUtil.getTextByLanguageUseFallbackLang(as.getName(), uiLang));
                    basketItem.setApplicationDates(DateRangeToDTO.convert(as.getApplicationDates()));
                    basketItem.setAsOngoing(ConverterUtil.isOngoing(as.getApplicationDates()));
                    basketItem.setNextApplicationPeriodStarts(ConverterUtil.resolveNextDateRangeStart(as.getApplicationDates()));
                    items.put(as.getId(), basketItem);
                }
            }
            return Lists.newArrayList(items.values());
        } else {
            return null;
        }
    }
}
