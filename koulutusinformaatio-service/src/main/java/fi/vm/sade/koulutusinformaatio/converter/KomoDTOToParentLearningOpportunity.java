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

import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Hannu Lyytikainen
 */
public class KomoDTOToParentLearningOpportunity implements Converter<KomoDTO, ParentLearningOpportunity> {

    private ConversionService conversionService;

    public KomoDTOToParentLearningOpportunity(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ParentLearningOpportunity convert(KomoDTO source) {
        ParentLearningOpportunity parent = new ParentLearningOpportunity();
        parent.setId(source.getOid());
        parent.setName(conversionService.convert(source.getNimi(), I18nText.class));
        return parent;
    }

}
