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

import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;

/**
 * @author Mikko Majapuro
 */
public class ProviderToDTO {

    public static LearningOpportunityProviderDTO convert(final Provider provider, final String lang) {
        if (provider != null) {
            LearningOpportunityProviderDTO p = new LearningOpportunityProviderDTO();
            p.setId(provider.getId());
            p.setName(ConverterUtil.getTextByLanguage(provider.getName(), lang));
            p.setApplicationSystemIds(provider.getApplicationSystemIDs());
            p.setPostalAddress(AddressToDTO.convert(provider.getPostalAddress()));
            p.setVisitingAddress(AddressToDTO.convert(provider.getVisitingAddress()));
            p.setEmail(provider.getEmail());
            p.setWebPage(provider.getWebPage());
            p.setPhone(provider.getPhone());
            p.setFax(provider.getFax());
            p.setDescription(ConverterUtil.getTextByLanguage(provider.getDescription(), lang));
            p.setAccessibility(ConverterUtil.getTextByLanguage(provider.getAccessibility(), lang));
            p.setLearningEnvironment(ConverterUtil.getTextByLanguage(provider.getLearningEnvironment(), lang));
            p.setLivingExpenses(ConverterUtil.getTextByLanguage(provider.getLivingExpenses(), lang));
            p.setDining(ConverterUtil.getTextByLanguage(provider.getDining(), lang));
            p.setHealthcare(ConverterUtil.getTextByLanguage(provider.getHealthcare(), lang));
            p.setSocial(SocialToDTO.convert(provider.getSocial()));
            p.setPictureFound(provider.getPicture() != null);
            return p;
        }
        return null;
    }
}
