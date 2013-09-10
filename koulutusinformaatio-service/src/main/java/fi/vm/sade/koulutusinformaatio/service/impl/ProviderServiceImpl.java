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

import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    private static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    private static final String PLACE_OF_BUSINESS_KOODISTO_URI = "opetuspisteet";

    private WebResource webResource;
    private KoodistoService koodistoService;
    private ConversionService conversionService;

    @Autowired
    public ProviderServiceImpl(@Value("${organisaatio.api.rest.url}") final String organisaatioResourceUrl,
                               KoodistoService koodistoService, ConversionService conversionService) {
        this.koodistoService = koodistoService;
        this.conversionService = conversionService;
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(organisaatioResourceUrl);
    }

    @Override
    public Provider getByOID(String oid) throws KoodistoException {
        WebResource oidResource = webResource.path(oid);
        OrganisaatioRDTO organisaatioRDTO = oidResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                .get(new GenericType<OrganisaatioRDTO>() {
                });

        if (organisaatioRDTO != null) {
            Provider provider = conversionService.convert(organisaatioRDTO, Provider.class);
            return updateCodeValues(provider);
        }
        return null;
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
            provider.setAthleteEducation(isAthleteEducation(provider.getPlaceOfBusinessCode()));
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
                    if (!Strings.isNullOrEmpty(key)) {
                        translations.put(key.toLowerCase(), entry.getValue());
                    }
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

    private boolean isAthleteEducation(final String placeOfBusinessCode) throws KoodistoException {
        if (!Strings.isNullOrEmpty(placeOfBusinessCode)) {
            List<Code> superCodes = koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                    PLACE_OF_BUSINESS_KOODISTO_URI);
            if (superCodes != null) {
                for (Code code : superCodes) {
                    if (placeOfBusinessCode.equals(code.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
