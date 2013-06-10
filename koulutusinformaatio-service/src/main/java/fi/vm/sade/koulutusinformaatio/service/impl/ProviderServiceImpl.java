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

package fi.vm.sade.koulutusinformaatio.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.OrganisaatioResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.google.common.base.Strings;

/**
 * @author Hannu Lyytikainen
 */
public class ProviderServiceImpl implements ProviderService {

    private OrganisaatioResource organisaatioResource;
    private ConversionService conversionService;
    @Autowired
    private KoodistoService koodistoService;

    public ProviderServiceImpl(OrganisaatioResource organisaatioResource, ConversionService conversionService) {
        this.organisaatioResource = organisaatioResource;
        this.conversionService = conversionService;
    }

    @Override
    public Provider getByOID(String oid) throws KoodistoException {
        Provider provider = conversionService.convert(organisaatioResource.getOrganisaatioByOID(oid), Provider.class);
        return updateCodeValues(provider);
    }

    private Provider updateCodeValues(final Provider provider) throws KoodistoException {
        if (provider != null) {
            updateAddressCodeValues(provider.getPostalAddress());
            updateAddressCodeValues(provider.getVisitingAddress());
            
            provider.setDescription( getI18nText(provider.getDescription().getTranslations()) );
            provider.setAccessibility( getI18nText(provider.getAccessibility().getTranslations()) );
            provider.setHealthcare( getI18nText(provider.getHealthcare().getTranslations()) );
            provider.setLivingExpenses( getI18nText(provider.getLivingExpenses().getTranslations()) );
            provider.setLearningEnvironment( getI18nText(provider.getLearningEnvironment().getTranslations()) );
            provider.setDining( getI18nText(provider.getDining().getTranslations()) );
        }
        return provider;
    }

    private void updateAddressCodeValues(final Address addrs) throws KoodistoException {
        if (addrs != null) {
            addrs.setPostalCode(koodistoService.searchFirstCodeValue(addrs.getPostalCode()));
        }
    }
    
    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i  = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }
}
