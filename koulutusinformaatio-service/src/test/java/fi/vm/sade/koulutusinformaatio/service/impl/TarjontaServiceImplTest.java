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

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LearningOpportunityDirector;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.RehabilitatingLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.UpperSecondaryLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.VocationalLearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import static org.junit.Assert.assertEquals;
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
        
        mockHigherEdRawRes();
    }

    private void mockHigherEdRawRes() {
    	
    	ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = new ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>();
    	HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = new HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>();
    	List<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> resSets = new ArrayList<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>>();
    	TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> resSet = new TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>();
    	List<KoulutusHakutulosV1RDTO> koulTulokset = new ArrayList<KoulutusHakutulosV1RDTO>();
    	KoulutusHakutulosV1RDTO koulJulk = new KoulutusHakutulosV1RDTO();
    	koulJulk.setOid("1.2.3.4");
    	koulJulk.setTila(TarjontaTila.JULKAISTU);
    	koulTulokset.add(koulJulk);
    	KoulutusHakutulosV1RDTO koulEiJulk = new KoulutusHakutulosV1RDTO();
    	koulEiJulk.setOid("2.2.3.4");
    	koulEiJulk.setTila(TarjontaTila.VALMIS);
    	koulTulokset.add(koulEiJulk);
    	resSet.setTulokset(koulTulokset);
    	resSets.add(resSet);
    	results.setTulokset(resSets);
    	rawRes.setResult(results);
    	when(tarjontaRawService.listHigherEducation()).thenReturn(rawRes);
    	
    	ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = new ResultV1RDTO<KoulutusKorkeakouluV1RDTO>();
    	KoulutusKorkeakouluV1RDTO koulutus1 = new KoulutusKorkeakouluV1RDTO();
    	koulutus1.setOid(koulJulk.getOid());
    	koulutus1.setTila(TarjontaTila.JULKAISTU);
    	koulutusRes.setResult(koulutus1);
    	
    	
    	when(tarjontaRawService.getHigherEducationLearningOpportunity(koulJulk.getOid())).thenReturn(koulutusRes);
    	
    	ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes2 = new ResultV1RDTO<KoulutusKorkeakouluV1RDTO>();
    	KoulutusKorkeakouluV1RDTO koulutus2 = new KoulutusKorkeakouluV1RDTO();
    	koulutus2.setOid(koulEiJulk.getOid());
    	koulutus2.setTila(TarjontaTila.VALMIS);
    	koulutusRes2.setResult(koulutus2);
    	
    	when(tarjontaRawService.getHigherEducationLearningOpportunity(koulEiJulk.getOid())).thenReturn(koulutusRes2);
    	LOSObjectCreator creator = mock(LOSObjectCreator.class);//new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
    	service.setCreator(creator);
    	
    	
    	try {
    		HigherEducationLOS los1 = new HigherEducationLOS();
        	los1.setId(koulJulk.getOid());
    		when(creator.createHigherEducationLOS(koulutus1, true)).thenReturn(los1);
    	
    		HigherEducationLOS los2 = new HigherEducationLOS();
    		los2.setId(koulEiJulk.getOid());
    		when(creator.createHigherEducationLOS(koulutus2, false)).thenReturn(los2);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	

        when(tarjontaRawService.getChildrenOfParentHigherEducationLOS(null)).thenReturn(null);
        when(tarjontaRawService.getParentsOfHigherEducationLOS(null)).thenReturn(null);
        
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
    
    @Test
    public void testFindHigherEducations() throws KoodistoException {
    	List<HigherEducationLOS> higherEds = service.findHigherEducations();
    	assertEquals(higherEds.size(), 1);
    }
    
    @Test
    public void testfindHigherEducationLearningOpportunity() throws TarjontaParseException, KoodistoException {
    	HigherEducationLOS nonPublished = service.findHigherEducationLearningOpportunity("2.2.3.4");
    	assertEquals(nonPublished.getId(), "2.2.3.4");
    }
    
}
