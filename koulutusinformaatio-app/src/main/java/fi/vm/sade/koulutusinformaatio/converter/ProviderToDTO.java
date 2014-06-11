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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public final class ProviderToDTO {

    private ProviderToDTO() {
    }

    public static LearningOpportunityProviderDTO convert(final Provider provider, final String lang, final String defaultLang, String uiLang) {
        if (provider != null) {
            LearningOpportunityProviderDTO p = new LearningOpportunityProviderDTO();
            p.setId(provider.getId());
            p.setName(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), uiLang));
            p.setApplicationSystemIds(provider.getApplicationSystemIDs());
            p.setPostalAddress(AddressToDTO.convert(provider.getPostalAddress(), uiLang));
            p.setVisitingAddress(AddressToDTO.convert(provider.getVisitingAddress(), uiLang));
            p.setEmail(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getEmail(), uiLang));
            p.setWebPage(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getWebPage(), uiLang));
            p.setPhone(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getPhone(), uiLang));
            p.setFax(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getFax(), uiLang));
            p.setDescription(ConverterUtil.getTextByLanguage(provider.getDescription(), lang));
            p.setAccessibility(ConverterUtil.getTextByLanguage(provider.getAccessibility(), lang));
            p.setLearningEnvironment(ConverterUtil.getTextByLanguage(provider.getLearningEnvironment(), lang));
            p.setLivingExpenses(ConverterUtil.getTextByLanguage(provider.getLivingExpenses(), lang));
            p.setLiving(ConverterUtil.getTextByLanguage(provider.getLiving(), lang));
            
            p.setYearClock(ConverterUtil.getTextByLanguage(provider.getYearClock(), lang));
            p.setFinancingStudies(ConverterUtil.getTextByLanguage(provider.getFinancingStudies(), lang));
            p.setInsurances(ConverterUtil.getTextByLanguage(provider.getInsurances(), lang));
            p.setLeisureServices(ConverterUtil.getTextByLanguage(provider.getLeisureServices(), lang));
            
            p.setDining(ConverterUtil.getTextByLanguage(provider.getDining(), lang));
            p.setHealthcare(ConverterUtil.getTextByLanguage(provider.getHealthcare(), lang));
            p.setSocial(SocialToDTO.convert(provider.getSocial()));
            p.setPictureFound(provider.getPicture() != null);
            p.setAthleteEducation(provider.isAthleteEducation());
            p.setApplicationOffice(ApplicationOfficeToDTO.convert(provider, lang));
            return p;
        }
        return null;
    }

    public static List<LearningOpportunityProviderDTO> convertAll(final List<Provider> providers, final String lang,
                                                                  final String defaultLang, final String uiLang) {
        return Lists.transform(providers, new Function<Provider, LearningOpportunityProviderDTO>() {
            @Override
            public LearningOpportunityProviderDTO apply(Provider input) {
                return convert(input, lang, defaultLang, uiLang);
            }
        });
    }
}
