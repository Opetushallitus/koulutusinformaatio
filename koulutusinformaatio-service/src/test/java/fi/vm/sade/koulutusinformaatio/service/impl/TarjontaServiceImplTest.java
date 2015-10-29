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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaServiceImplTest {

    private static final String CALENDAR_APPLICATION_SYSTEM_OID = "as1.1.1.1.oid";

    KoodistoService koodistoService;
    ProviderService providerService;
    TarjontaRawService tarjontaRawService;
    OrganisaatioRawService organisaatioRawService;
    TarjontaServiceImpl service;
    LOSObjectCreator creator;

    @Before
    public void setup() {
        tarjontaRawService = mock(TarjontaRawService.class);

        service = new TarjontaServiceImpl(koodistoService,
                providerService, tarjontaRawService, organisaatioRawService, mock(ParameterService.class), Lists.<String> newArrayList());
        
        mockHigherEdRawRes();
        this.mockCalendarApplicationSystems();
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
        when(tarjontaRawService.listEducationsByToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS.name())).thenReturn(rawRes);
    	
    	ResultV1RDTO<KoulutusV1RDTO> koulutusRes = new ResultV1RDTO<KoulutusV1RDTO>();
    	KoulutusKorkeakouluV1RDTO koulutus1 = new KoulutusKorkeakouluV1RDTO();
    	koulutus1.setOid(koulJulk.getOid());
    	koulutus1.setTila(TarjontaTila.JULKAISTU);
    	koulutusRes.setResult(koulutus1);
    	
    	
    	when(tarjontaRawService.getV1KoulutusLearningOpportunity(koulJulk.getOid())).thenReturn(koulutusRes);
    	
    	ResultV1RDTO<KoulutusV1RDTO> koulutusRes2 = new ResultV1RDTO<KoulutusV1RDTO>();
    	KoulutusKorkeakouluV1RDTO koulutus2 = new KoulutusKorkeakouluV1RDTO();
    	koulutus2.setOid(koulEiJulk.getOid());
    	koulutus2.setTila(TarjontaTila.VALMIS);
    	koulutusRes2.setResult(koulutus2);
    	
    	when(tarjontaRawService.getV1KoulutusLearningOpportunity(koulEiJulk.getOid())).thenReturn(koulutusRes2);
    	creator = mock(LOSObjectCreator.class);//new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
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
    
    private void mockCalendarApplicationSystems() {
        
        ResultV1RDTO<List<String>> rawRes = new ResultV1RDTO<List<String>>();
        //String asOid = "as1.1.1.1.oid";
        List<String> resOids = Arrays.asList(CALENDAR_APPLICATION_SYSTEM_OID);
        rawRes.setResult(resOids);
        when(this.tarjontaRawService.searchHakus(TarjontaConstants.HAKUTAPA_YHTEISHAKUV1)).thenReturn(rawRes);
        
        ResultV1RDTO<HakuV1RDTO> curHakuResult = new ResultV1RDTO<HakuV1RDTO>();//this.tarjontaRawService.getV1EducationHakuByOid(curOid);
        HakuV1RDTO curHaku = new HakuV1RDTO();
        curHaku.setOid(CALENDAR_APPLICATION_SYSTEM_OID);
        curHaku.setTila(TarjontaConstants.STATE_PUBLISHED);
        curHaku.setHakutyyppiUri(TarjontaConstants.HAKUTYYPPI_VARSINAINEN);
        curHaku.setHakutapaUri(TarjontaConstants.HAKUTAPA_YHTEISHAKU);
        
        curHakuResult.setResult(curHaku);
        when(this.tarjontaRawService.getV1EducationHakuByOid(CALENDAR_APPLICATION_SYSTEM_OID)).thenReturn(curHakuResult);
        
        CalendarApplicationSystem calendarAs = new CalendarApplicationSystem();
        calendarAs.setId(CALENDAR_APPLICATION_SYSTEM_OID);
        
        try {
            when(this.creator.createApplicationSystemForCalendar(curHaku, true)).thenReturn(calendarAs);
            when(this.creator.createApplicationSystemForCalendar(curHaku, false)).thenReturn(calendarAs);
        } catch (KoodistoException ex) {
            ex.printStackTrace();
        }
    }

    
    @Test
    public void testFindHigherEducations() throws KoodistoException, IOException, ResourceNotFoundException {
    	List<HigherEducationLOS> higherEds = service.findHigherEducations();
    	assertEquals(higherEds.size(), 1);
    }
    
    @Test
    public void testfindHigherEducationLearningOpportunity() throws TarjontaParseException, KoodistoException, IOException, ResourceNotFoundException {
    	HigherEducationLOS nonPublished = service.findHigherEducationLearningOpportunity("2.2.3.4");
    	assertEquals(nonPublished.getId(), "2.2.3.4");
    }
    
    @Test
    public void testFindApplicationSystemsForCalendar() throws KoodistoException {
        List<CalendarApplicationSystem> results = service.findApplicationSystemsForCalendar();
        CalendarApplicationSystem calAS = results.get(0);
        assertEquals(CALENDAR_APPLICATION_SYSTEM_OID, calAS.getId());
    }
    
    @Test
    public void testCreateCalendarApplicationSystem() throws KoodistoException {
        CalendarApplicationSystem calAS = this.service.createCalendarApplicationSystem(CALENDAR_APPLICATION_SYSTEM_OID);
        assertEquals(CALENDAR_APPLICATION_SYSTEM_OID, calAS.getId());
    }
    
    
    
}
