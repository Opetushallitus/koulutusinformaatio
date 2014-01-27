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

import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LearningOpportunityDirector;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.RehabilitatingLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.UpperSecondaryLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.VocationalLearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaServiceImplTest {

    private static final String KOMO_ID_VOC = "vocationalKomoId";
    private static final String KOMO_ID_UPSEC = "upsecKomoId";
    private static final String KOMO_ID_REHAB = "rehabKomoId";
    private static final String KOMO_ID_INVALID = "invalid";

    ConversionService conversionService;
    KoodistoService koodistoService;
    ProviderService providerService;
    LearningOpportunityDirector loDirector;
    TarjontaRawService tarjontaRawService;
    TarjontaServiceImpl service;

    @Before
    public void setup() {
        KomoDTO vocationalKomo = new KomoDTO();
        vocationalKomo.setKoulutusTyyppiUri(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE);
        vocationalKomo.setModuuliTyyppi(TarjontaConstants.MODULE_TYPE_PARENT);

        KomoDTO upsecKomo = new KomoDTO();
        upsecKomo.setKoulutusTyyppiUri(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE);
        upsecKomo.setModuuliTyyppi(TarjontaConstants.MODULE_TYPE_CHILD);

        KomoDTO rehabKomo = new KomoDTO();
        rehabKomo.setKoulutusTyyppiUri(TarjontaConstants.REHABILITATING_EDUCATION_TYPE);
        rehabKomo.setModuuliTyyppi(TarjontaConstants.MODULE_TYPE_CHILD);

        KomoDTO invalidKomo = new KomoDTO();
        invalidKomo.setKoulutusTyyppiUri("invalid");
        invalidKomo.setModuuliTyyppi("invalid");

        tarjontaRawService = mock(TarjontaRawService.class);
        loDirector = mock(LearningOpportunityDirector.class);
        when(tarjontaRawService.getKomo(eq(KOMO_ID_VOC))).thenReturn(vocationalKomo);
        when(tarjontaRawService.getKomo(eq(KOMO_ID_UPSEC))).thenReturn(upsecKomo);
        when(tarjontaRawService.getKomo(eq(KOMO_ID_REHAB))).thenReturn(rehabKomo);
        when(tarjontaRawService.getKomo(eq(KOMO_ID_INVALID))).thenReturn(invalidKomo);

        service = new TarjontaServiceImpl(conversionService, koodistoService,
                providerService, loDirector, tarjontaRawService);
    }

    @Test
    public void testVocationalResolveBuilder() throws TarjontaParseException, KoodistoException {
        service.findParentLearningOpportunity(KOMO_ID_VOC);
        verify(loDirector).constructLearningOpportunities(isA(VocationalLearningOpportunityBuilder.class));
    }

    @Test
    public void testUpperSecondaryResolveBuilder() throws TarjontaParseException, KoodistoException {
        service.findParentLearningOpportunity(KOMO_ID_UPSEC);
        verify(loDirector).constructLearningOpportunities(isA(UpperSecondaryLearningOpportunityBuilder.class));
    }

    @Test
    public void testRehabilitatingResolveBuilder() throws TarjontaParseException, KoodistoException {
        service.findParentLearningOpportunity(KOMO_ID_REHAB);
        verify(loDirector).constructLearningOpportunities(isA(RehabilitatingLearningOpportunityBuilder.class));
    }

    @Test(expected = TarjontaParseException.class)
    public void testResolveBuilderInvalidEducationType() throws TarjontaParseException {
        service.findParentLearningOpportunity(KOMO_ID_INVALID);
    }
}
