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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.OrganizationGroup;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.impl.KoodistoAwareTest;
import fi.vm.sade.koulutusinformaatio.util.TestUtil;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.RyhmaliitosV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationOptionCreatorTest extends KoodistoAwareTest {

    OrganisaatioRawService organisaatioRawService;
    ApplicationOptionCreator creator;
    ParameterService parameterService;

    KomotoDTO komoto;
    Code prerequisite;

    private static final String hakukohdeOid = "1.2.3.4";
    private static final String hakukohdeNameUri = "hakukohdeNameUri";
    private static final String educationCodeUri = "educationCodeUri";
    private static final String PRIORITIZED_GROUP_OID = "group_with_prio";
    private static final String HAKUAIKA_ID_1 = "hakuaika-id-1";

    @Before
    public void init() throws KoodistoException {
        organisaatioRawService = mock(OrganisaatioRawService.class);
        parameterService = mock(ParameterService.class);
        komoto = new KomotoDTO();

        I18nText name  = TestUtil.createI18nText("hakukohdeName");
        when(koodistoService.searchFirstName(eq(hakukohdeNameUri))).thenReturn(name);
        when(koodistoService.searchNames(eq(hakukohdeNameUri))).thenReturn(Arrays.asList(name));

        String aoIdentifierAthlete = "aoIdentifier";
        when(koodistoService.searchFirstCodeValue(eq(hakukohdeNameUri))).thenReturn(aoIdentifierAthlete);
        Code aoIdentifierCode = new Code();
        aoIdentifierCode.setValue(aoIdentifierAthlete);
        when(koodistoService.searchSuperCodes(
                eq(TarjontaConstants.ATHLETE_EDUCATION_KOODISTO_URI),
                eq(TarjontaConstants.APPLICATION_OPTIONS_KOODISTO_URI)))
                .thenReturn(Lists.newArrayList(aoIdentifierCode));

        when(koodistoService.searchFirstCodeValue(eq("postinumero"))).thenReturn("postinumero");

        Code baseEducation1 = new Code();
        baseEducation1.setValue("1");
        Code baseEducation2 = new Code();
        baseEducation2.setValue("2");
        komoto.setPohjakoulutusVaatimusUri("prerequisiteUri");
        when(koodistoService.searchSubCodes(
                eq(komoto.getPohjakoulutusVaatimusUri()),
                eq(TarjontaConstants.BASE_EDUCATION_KOODISTO_URI)))
                .thenReturn(Lists.newArrayList(baseEducation1, baseEducation2));

        komoto.setOpetuskieletUris(Lists.newArrayList("fiUri"));
        when(koodistoService.searchCodeValuesMultiple(komoto.getOpetuskieletUris())).thenReturn(Lists.newArrayList("fi"));

        prerequisite = new Code();
        prerequisite.setValue("prerequisite");
        prerequisite.setName(TestUtil.createI18nText("peruskoulu"));
        creator = new ApplicationOptionCreator(koodistoService, organisaatioRawService, parameterService);

    }

    @Test
    public void testCreateVocationalApplicationOption() throws KoodistoException {
        HakukohdeDTO hakukohde = new HakukohdeDTO();
        hakukohde.setOid(hakukohdeOid);
        hakukohde.setHakukohdeNimiUri(hakukohdeNameUri);
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAlinHyvaksyttavaKeskiarvo(4);
        hakukohde.setLiitteidenToimitusPvm(new Date());
        hakukohde.setEdellisenVuodenHakijatLkm(10);
        Map<String, String> selectionCriteria = Maps.newHashMap();
        selectionCriteria.put(getFiUri(), "selectionCriteria");
        hakukohde.setValintaperustekuvaus(selectionCriteria);
        hakukohde.setKaksoisTutkinto(false);
        hakukohde.setSoraKuvausKoodiUri("soraUri");

        Map<String, String> additionalInfo = Maps.newHashMap();
        additionalInfo.put(getFiUri(), "additionalInfo");
        hakukohde.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
        hakukohde.setLisatiedot(additionalInfo);

    }

    @Test
    public void testCreateV1EducationApplicationOption() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();

        KoulutusLOS los = new KoulutusLOS();
        los.setProvider(new Provider());
        Code educationCode = new Code();
        educationCode.setUri(educationCodeUri);
        los.setEducationCode(educationCode);
        Code fi = new Code();
        fi.setValue("fi");
        los.setTeachingLanguages(Arrays.asList(fi));

        HakuV1RDTO haku = new HakuV1RDTO();
        haku.setOid("4.3.2.1");
        haku.setHakutapaUri("dummyhaku");

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);
        assertNotNull(ao);
        assertEquals(hakukohdeOid, ao.getId());
        assertEquals("aoIdentifier", ao.getAoIdentifier());
        assertEquals("hakukohdeName", ao.getName().getTranslations().get("fi"));
        assertFalse(ao.isAthleteEducation());
        assertEquals(new Integer(10), ao.getStartingQuota());
        assertEquals(new Integer(5), ao.getFirstTimerStartingQuota());
        assertEquals(new Integer(5), ao.getLowestAcceptedScore());
        assertEquals(new Double(4), ao.getLowestAcceptedAverage());
        assertEquals(hakukohde.getLiitteidenToimitusPvm(), ao.getAttachmentDeliveryDeadline());
        assertEquals(new Integer(10), ao.getLastYearApplicantCount());
        assertEquals("selectionCriteria", ao.getSelectionCriteria().getTranslations().get("fi"));
        assertFalse(ao.isKaksoistutkinto());
        assertEquals(educationCodeUri, ao.getEducationCodeUri());
        assertNotNull(ao.getRequiredBaseEducations());
        assertEquals(2, ao.getRequiredBaseEducations().size());
        assertEquals("prerequisiteUri1", ao.getRequiredBaseEducations().get(0));
        assertEquals("prerequisiteUri2", ao.getRequiredBaseEducations().get(1));
        assertNotNull(ao.getTeachingLanguages());
        assertEquals(1, ao.getTeachingLanguages().size());
        assertEquals("fi", ao.getTeachingLanguages().get(0));
        assertFalse(ao.isSpecificApplicationDates());
        assertEquals("additionalInfo", ao.getAdditionalInfo().getTranslations().get("fi"));
        assertFalse(ao.isVocational());

        List<OrganizationGroup> groups = ao.getOrganizationGroups();
        assertEquals(2, groups.size());
        for (OrganizationGroup group: groups) {
            if(group.getOid().equals(PRIORITIZED_GROUP_OID)) {
                assertEquals(1, group.getPrioriteetti().intValue());
            }
            else {
                assertNull(group.getPrioriteetti());
            }
        }

    }

    @Test
    public void testCreateV1EducationApplicationOptionWithAttachment() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        
        ObjectMapper mapper = new ObjectMapper();
        HakukohdeLiiteV1RDTO liite1 = mapper.readValue(
                new File("src/test/java/fi/vm/sade/koulutusinformaatio/service/builder/impl/HakukohdeV1RDTOLiite1.json"), HakukohdeLiiteV1RDTO.class);
        HakukohdeLiiteV1RDTO liite2 = mapper.readValue(
                new File("src/test/java/fi/vm/sade/koulutusinformaatio/service/builder/impl/HakukohdeV1RDTOLiite2.json"), HakukohdeLiiteV1RDTO.class);
        liite1.setJarjestys(1);
        liite2.setJarjestys(1);
        hakukohde.getHakukohteenLiitteet().add(liite1);
        hakukohde.getHakukohteenLiitteet().add(liite2);

        KoulutusLOS los = new KoulutusLOS();
        los.setProvider(new Provider());
        Code educationCode = new Code();
        educationCode.setUri(educationCodeUri);
        los.setEducationCode(educationCode);
        Code fi = new Code();
        fi.setValue("fi");
        los.setTeachingLanguages(Arrays.asList(fi));

        HakuV1RDTO haku = new HakuV1RDTO();
        haku.setOid("4.3.2.1");
        haku.setHakutapaUri("dummyhaku");

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);
        assertNotNull(ao);

        assertEquals(1, ao.getAttachments().size());
        assertEquals(2, ao.getAttachments().get(0).getDescreption().getTranslations().size());
    }

    @Test
    public void isVisibleWhenHakuRunning() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-1), getRelativeDateFromNow(2));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertTrue(ao.showInOpintopolku());
    }

    @Test
    public void isVisibleWhenHakuInFuture() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(1), getRelativeDateFromNow(2));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertTrue(ao.showInOpintopolku());
    }

    @Test
    public void isVisibleWhenHakuStoppedLessThan10monthsAgo() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-12), getRelativeDateFromNow(-9));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertTrue(ao.showInOpintopolku());
    }

    @Test
    public void isVisibleWhenHakuStoppedMoreThan10monthsAgoButHakuParameterAllows() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-12), getRelativeDateFromNow(-11));
        haku.setOpintopolunNayttaminenLoppuu(getRelativeDateFromNow(1));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertTrue(ao.showInOpintopolku());
    }


    /**
     * When haku param says that application option should no longer be shown,
     * but haku is still active => should still show because haku is active
     * (problem in tarjonta if this happens)
     */
    @Test
    public void isVisibleWhenHakuActiveButHakuParamConflict() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-1), getRelativeDateFromNow(2));
        haku.setOpintopolunNayttaminenLoppuu(getRelativeDateFromNow(-1));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertTrue(ao.showInOpintopolku());
    }

    @Test
    public void isNotVisibleWhenHakuStoppedMoreThan10monthsAgo() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-12), getRelativeDateFromNow(-11));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertFalse(ao.showInOpintopolku());
    }

    @Test
    public void isNotVisibleAfterHakuParam() throws Exception {
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        KoulutusLOS los = getKoulutusLOS();
        HakuV1RDTO haku = getHakuV1RDTO(getRelativeDateFromNow(-12), getRelativeDateFromNow(-2));
        haku.setOpintopolunNayttaminenLoppuu(getRelativeDateFromNow(-1));

        ApplicationOption ao = creator.createV1EducationApplicationOption(los, hakukohde, haku);

        assertFalse(ao.showInOpintopolku());
    }
    
    @Test
    public void testMergeI18nTexts() throws Exception {
        I18nText t1 = new I18nText();
        t1.setTranslations(new HashMap<String, String>());
        t1.getTranslations().put("fi", "fiValue");
        I18nText t2 = new I18nText();
        t2.setTranslations(new HashMap<String, String>());
        t2.getTranslations().put("sv", "svValue");

        I18nText result = creator.mergeI18nTexts(t1, t2);
        assertNotNull(result);
        assertEquals("fiValue", result.getTranslations().get("fi"));
        assertEquals("svValue", result.getTranslations().get("sv"));
    }

    @Test
    public void testMergeI18nTextsNullTranslation() throws Exception {
        I18nText t1 = new I18nText();
        t1.setTranslations(new HashMap<String, String>());
        t1.getTranslations().put("fi", "fiValue");
        I18nText t2 = null;

        I18nText result = creator.mergeI18nTexts(t1, t2);
        assertNotNull(result);
        assertEquals("fiValue", result.getTranslations().get("fi"));
        assertNull(result.getTranslations().get("sv"));

        I18nText result2 = creator.mergeI18nTexts(null, null);
        assertNull(result2);
    }

    @Test
    public void testBug535() throws Exception {
        KoulutusLOS koulutus = getKoulutusLOS();
        Date hakuaikaStart = getRelativeDateFromNow(-12);
        Date hakuaikaEnd = getRelativeDateFromNow(12);
        HakuV1RDTO haku = getHakuV1RDTO(hakuaikaStart, hakuaikaEnd);
        HakukohdeV1RDTO hakukohde = getHakukohdeV1RDTO();
        List<HakukohdeLiiteV1RDTO> liitteet = hakukohde.getHakukohteenLiitteet();
        HakukohdeLiiteV1RDTO liiteFi = createLiite("3203625", 0, "kieli_fi", "liitteenNimi", "liitteenKuvaukset", "osoiterivi1", "postinumero", "postinumeroArvo",
                "postitoimipaikka");
        HakukohdeLiiteV1RDTO liiteEn = createLiite("3203628", 0, "kieli_en", "liitteenNimiEn", "liitteenKuvauksetEn", "osoiterivi1En", "postinumero",
                "postinumeroArvo", "postitoimipaikka");
        liitteet.add(liiteFi);
        liitteet.add(liiteEn);
        hakukohde.setHakukohteenLiitteet(liitteet);
        ApplicationOption ao = creator.createV1EducationApplicationOption(koulutus, hakukohde, haku);

        assertNotNull(ao);

        assertEquals(1, ao.getAttachments().size());
        Address address = ao.getAttachments().get(0).getAddress();

        assertEquals("osoiterivi1", address.getStreetAddress().get("fi"));
        assertEquals("osoiterivi1En", address.getStreetAddress().get("en"));
        assertNull(address.getSecondForeignAddr());
        assertEquals("postinumero", address.getPostalCode().get("fi"));
        assertEquals("postinumero", address.getPostalCode().get("en"));
        assertEquals("postitoimipaikka", address.getPostOffice().get("fi"));
        assertEquals("postitoimipaikka", address.getPostOffice().get("en"));
    }

    private HakukohdeLiiteV1RDTO createLiite(String oid, Integer jarjestys, String kieli, String liitteenNimi, String liitteenKuvaus, String osoiterivi1,
            String postinumero, String postinumeroArvo, String postitoimipaikka) {
        HakukohdeLiiteV1RDTO liite = new HakukohdeLiiteV1RDTO();
        liite.setOid(oid);
        liite.setJarjestys(jarjestys);
        liite.setKieliUri(kieli);
        liite.setLiitteenNimi(liitteenNimi);
        Map<String, String> kuvausMap = new HashMap<String, String>();
        kuvausMap.put(kieli, liitteenKuvaus);
        liite.setLiitteenKuvaukset(kuvausMap);
        OsoiteRDTO osoite = new OsoiteRDTO();
        osoite.setOsoiterivi1(osoiterivi1);
        osoite.setPostinumero(postinumero);
        osoite.setPostinumeroArvo(postinumeroArvo);
        osoite.setPostitoimipaikka(postitoimipaikka);
        liite.setLiitteenToimitusOsoite(osoite);
        return liite;
    }

    private Date getRelativeDateFromNow(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    private KoulutusLOS getKoulutusLOS() {
        KoulutusLOS los = new KoulutusLOS();
        Code educationCode = new Code();
        educationCode.setUri(educationCodeUri);
        los.setEducationCode(educationCode);
        los.setProvider(new Provider());
        Code fi = new Code();
        fi.setValue("fi");
        los.setTeachingLanguages(Arrays.asList(fi));
        return los;
    }

    private HakuV1RDTO getHakuV1RDTO(Date hakuaikaStart, Date hakuaikaEnd) {
        HakuV1RDTO haku = new HakuV1RDTO();
        haku.setOid("4.3.2.1");
        HakuaikaV1RDTO hakuaika = new HakuaikaV1RDTO();
        hakuaika.setHakuaikaId(HAKUAIKA_ID_1);
        hakuaika.setAlkuPvm(hakuaikaStart);
        hakuaika.setLoppuPvm(hakuaikaEnd);
        haku.setHakuaikas(Lists.newArrayList(hakuaika));
        haku.setHakutapaUri("dummyhaku");

        return haku;
    }

    private HakukohdeV1RDTO getHakukohdeV1RDTO() throws Exception{
        HakukohdeV1RDTO hakukohde = new HakukohdeV1RDTO();
        hakukohde.setOid(hakukohdeOid);
        hakukohde.setHakukohteenNimiUri(hakukohdeNameUri);
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setEnsikertalaistenAloituspaikat(5);
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAlinHyvaksyttavaKeskiarvo(4);
        hakukohde.setLiitteidenToimitusPvm(new Date());
        hakukohde.setEdellisenVuodenHakijatLkm(10);
        Map<String, String> selectionCriteria = Maps.newHashMap();
        selectionCriteria.put(getFiUri(), "selectionCriteria");
        hakukohde.setValintaperusteKuvaukset(selectionCriteria);
        hakukohde.setKaksoisTutkinto(false);
        hakukohde.setSoraKuvausKoodiUri("soraUri");
        hakukohde.setHakuaikaId(HAKUAIKA_ID_1);
        hakukohde.setHakukelpoisuusvaatimusUris(Arrays.asList("prerequisiteUri1", "prerequisiteUri2"));

        RyhmaliitosV1RDTO groupWithPrio = new RyhmaliitosV1RDTO();
        groupWithPrio.setRyhmaOid(PRIORITIZED_GROUP_OID);
        groupWithPrio.setPrioriteetti(1);
        OrganisaatioRDTO groupWithPrioOrg = new OrganisaatioRDTO();
        groupWithPrioOrg.setOid(groupWithPrio.getRyhmaOid());
        groupWithPrioOrg.setTyypit(Arrays.asList(OrganisaatioTyyppi.RYHMA.value()));

        RyhmaliitosV1RDTO groupWithNoPrio = new RyhmaliitosV1RDTO();
        groupWithNoPrio.setRyhmaOid("group_with_no_prio");
        OrganisaatioRDTO groupWithNoPrioOrg = new OrganisaatioRDTO();
        groupWithNoPrioOrg.setOid(groupWithNoPrio.getRyhmaOid());
        groupWithNoPrioOrg.setTyypit(Arrays.asList(OrganisaatioTyyppi.RYHMA.value()));

        hakukohde.setOrganisaatioRyhmaOids(new String[]{groupWithPrio.getRyhmaOid(), groupWithNoPrio.getRyhmaOid()});
        hakukohde.setRyhmaliitokset(new ArrayList<RyhmaliitosV1RDTO>(Arrays.asList(groupWithPrio, groupWithNoPrio)));

        when(organisaatioRawService.getOrganisaatio(eq(groupWithPrio.getRyhmaOid()))).thenReturn(groupWithPrioOrg);
        when(organisaatioRawService.getOrganisaatio(eq(groupWithNoPrio.getRyhmaOid()))).thenReturn(groupWithNoPrioOrg);

        Map<String, String> additionalInfo = Maps.newHashMap();
        additionalInfo.put(getFiUri(), "additionalInfo");
        hakukohde.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
        hakukohde.setLisatiedot(additionalInfo);

        return hakukohde;
    }
}
