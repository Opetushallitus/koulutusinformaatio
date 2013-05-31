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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionsToBasketItemDTOs {

    public static List<BasketItemDTO> convert(final List<ApplicationOption> aos, String lang) {
        if (aos != null) {
            lang = lang.toLowerCase();
            Map<String, BasketItemDTO> items = Maps.newHashMap();
            for (ApplicationOption ao : aos) {
                BasketApplicationOptionDTO aoDTO = new BasketApplicationOptionDTO();
                aoDTO.setId(ao.getId());
                aoDTO.setEducationDegree(ao.getEducationDegree());
                aoDTO.setParent(ParentLOSRefToDTO.convert(ao.getParent(), lang));
                aoDTO.setChildren(ChildLORefToDTO.convert(ao.getChildLORefs(), lang));
                Provider provider = ao.getProvider();
                if (provider != null) {
                    aoDTO.setProviderId(provider.getId());
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
                    basketItem.setApplicationSystemId(as.getId());
                    basketItem.getApplicationOptions().add(aoDTO);
                    basketItem.setApplicationSystemName(ConverterUtil.getTextByLanguageUseFallbackLang(as.getName(), lang));
                    items.put(as.getId(), basketItem);
                }
            }
            return Lists.newArrayList(items.values());
        } else {
            return null;
        }
    }
}
