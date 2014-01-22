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

import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.stereotype.Service;

/**
 * @author Hannu Lyytikainen
 */
@Service("conversionService")
public class KoulutusinformaatioConversionServiceFactoryBean extends ConversionServiceFactoryBean {

    @Autowired
    KoodistoService koodistoService;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        ConversionService conversionService = getObject();
        ConverterRegistry registry = (ConverterRegistry) conversionService;
        registry.addConverter(new OrganisaatioRDTOToProvider(koodistoService));
        registry.addConverter(new KoodiTypeToI18nText());
        registry.addConverter(new KoodiTypeToCode());
        registry.addConverter(new CodeUriAndVersionToKoodiUriAndVersionType());
        registry.addConverter(new OidRDTOToString());
        registry.addConverter(new ParentLOSToSolrInputDocument());
        registry.addConverter(new UpperSecondaryLOSToSolrInputDocument());
        registry.addConverter(new SpecialLOSToSolrInputDocument());
        registry.addConverter(new UasLOSToSolrInputDocment());
    }
}
