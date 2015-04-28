/**
 * Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import fi.vm.sade.koulutusinformaatio.domain.StandaloneLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LearningOpportunityDirector;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author risal1
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TarjontaServiceValmistavaKoulutusTest {

    @Mock
    private LOSObjectCreator creator;

    @Mock
    private TarjontaRawService rawService;

    @Mock
    private ConversionService conversionService;
    
    @Mock
    private OrganisaatioRawService organisaatioRawService;
    
    @Mock
    private KoodistoService koodistoService;
    
    @Mock
    private ProviderService providerService;
    
    @Mock
    private LearningOpportunityDirector loDirector;
    
    @Mock
    private ParameterService parameterService;
    
    @InjectMocks
    private TarjontaServiceImpl service;
    
    

    private final static String CIVILIZING_OID = "234.243.243.21";
    
    @SuppressWarnings("unchecked")
    @Before
    public void init() throws Exception {
        setCreator();
        KoulutusHakutulosV1RDTO mockedEducationResult = mock(KoulutusHakutulosV1RDTO.class);
        when(mockedEducationResult.getTila()).thenReturn(TarjontaTila.JULKAISTU);
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> searchResult = mock(HakutuloksetV1RDTO.class);
        TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> providerResults = mock(TarjoajaHakutulosV1RDTO.class);
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> mockedResult = mock(ResultV1RDTO.class);
        when(mockedResult.getResult()).thenReturn(searchResult);
        when(rawService.listEducationsByToteutustyyppi(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class)))
            .thenReturn(mockedResult);
        when(searchResult.getTulokset()).thenReturn(Arrays.asList(providerResults));
        when(providerResults.getTulokset()).thenReturn(Arrays.asList(mockedEducationResult));
        when(mockedEducationResult.getOid()).thenReturn(CIVILIZING_OID);
        when(rawService.getValmistavaKoulutusLearningOpportunity(CIVILIZING_OID)).thenReturn(givenValmistavaResult());
    }

    @Test(expected = KoodistoException.class)
    public void throwsExceptionWhenFailsToCreateKoulutusLOS() throws TarjontaParseException, KoodistoException {
        when(creator.createKansanopistoLOS(any(ValmistavaKoulutusV1RDTO.class), eq(true))).thenThrow(new NullPointerException());
        service.findValmistavaKoulutusEducations();
    }
    
    @Test
    public void returnsStandAloneLOS() throws TarjontaParseException, KoodistoException {
        when(creator.createKansanopistoLOS(any(ValmistavaKoulutusV1RDTO.class), eq(true))).thenReturn(new StandaloneLOS());
        List<StandaloneLOS> losses = service.findValmistavaKoulutusEducations();
        assertEquals(1, losses.size());
    }


    private ResultV1RDTO<ValmistavaKoulutusV1RDTO> givenValmistavaResult() {
        ValmistavaKoulutusV1RDTO result = new ValmistavaKoulutusV1RDTO();
        result.setCreated(new Date());
        result.setCreatedBy("Teppo Testaaja");
        result.setKomoOid("123.123.234.123");
        result.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        result.setModified(new Date());
        result.setModifiedBy("Teppo Testaaja");
        result.setOid("921.00.123.12");
        result.setTila(TarjontaTila.JULKAISTU);
        result.setToteutustyyppi(ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
        result.setKoulutuskoodi(new KoodiV1RDTO("uri", 1, "arvo"));
        return new ResultV1RDTO<ValmistavaKoulutusV1RDTO>(result);
    }
    
    private void setCreator() throws Exception {
        Field creatorField = service.getClass().getDeclaredField("creator");
        creatorField.setAccessible(true);
        creatorField.set(service, creator);
        
    }

}
