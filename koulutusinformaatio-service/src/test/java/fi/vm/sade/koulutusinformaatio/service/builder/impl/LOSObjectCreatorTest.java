package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * Created by alexis on 7.5.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class LOSObjectCreatorTest extends TestCase {

    @Mock
    private ApplicationOptionCreator aoCreator;

    @Mock
    private KoodistoService koodistoService;

    @Mock
    private ProviderService providerService;

    @Mock
    private TarjontaRawService tarjontaRawService;

    @InjectMocks
    private LOSObjectCreator creator;

    @Before
    public void init() throws Exception {
        when(koodistoService.search(any(String.class))).thenReturn(new ArrayList<Code>());
        Code mockedCode = new Code(){{
            this.setShortTitle(new I18nText(ImmutableMap.of("fi", "shortTitle_fi")));
        }};
        when(koodistoService.searchFirst(any(String.class))).thenReturn(mockedCode);
        when(tarjontaRawService.findHakukohdesByEducationOid(any(String.class), anyBoolean())).thenReturn(null);
    }

    @Test
    public void createsKansanopistoLOSWithoutCheckingStatus() throws Exception {
        KoulutusLOS los = creator.createKansanopistoLOS(givenValmistavaKoulutus(), false);
        assertNotNull(los);
    }

    @Test (expected = TarjontaParseException.class)
    public void doesNotCreateKansanopistoLOSWhenStatusCheckFails() throws Exception {
        creator.createKansanopistoLOS(givenValmistavaKoulutus(), true);
    }

    @Test
    public void createHigherEducationLOSWithSubjects() throws Exception{
        KoulutusKorkeakouluV1RDTO dto = givenKorkeakouluKoulutus();
        Set<OppiaineV1RDTO> oppiaineet = new HashSet<OppiaineV1RDTO>();
        oppiaineet.add(givenOppiaine("kieli_fi", "oppiaine1"));
        oppiaineet.add(givenOppiaine("kieli_sv", "oppiaine2"));
        oppiaineet.add(givenOppiaine("kieli_sv", "oppiaine3"));
        dto.setOppiaineet(oppiaineet);
        KoulutusLOS los = creator.createHigherEducationLOS(dto, false);
        assertEquals(1, los.getSubjects().get("fi").size());
        assertEquals(2, los.getSubjects().get("sv").size());
        assertNotNull(los);
    }

    private OppiaineV1RDTO givenOppiaine(String kieli, String arvo) {
        OppiaineV1RDTO e = new OppiaineV1RDTO();
        e.setKieliKoodi(kieli);
        e.setOppiaine(arvo);
        return e;
    }


    private KoulutusKorkeakouluV1RDTO givenKorkeakouluKoulutus() {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        dto.setKoulutusala(givenKoodiV1RDTOWithMeta());
        dto.setKoulutuskoodi(givenKoodiV1RDTOWithMeta());
        dto.setKoulutusaste(givenKoodiV1RDTOWithMeta());
        dto.setTutkinto(givenKoodiV1RDTOWithMeta());
        dto.setSuunniteltuKestoTyyppi(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusarvo(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusyksikko(givenKoodiV1RDTOWithMeta());
        dto.setOppiaineet(new HashSet<OppiaineV1RDTO>());
        dto.setKuvausKomo(new KuvausV1RDTO<KomoTeksti>());
        dto.setKuvausKomoto(new KuvausV1RDTO<KomotoTeksti>());
        dto.setOrganisaatio(new OrganisaatioV1RDTO("orgOid"));
        dto.setOpetusTarjoajat(Sets.<String>newHashSet());
        return dto;
    }

    @Test
    public void setsNameForKansanopistoLOSFromApplicationOptionWhenHakukohteenNimiKannassaIsNull() throws Exception {
        ValmistavaKoulutusV1RDTO koulutus = givenValmistavaKoulutus();
        koulutus.setKoulutusohjelmanNimiKannassa(null);
        when(tarjontaRawService.findHakukohdesByEducationOid(any(String.class), anyBoolean())).thenReturn(givenHakukohdeResult());
        when(tarjontaRawService.getV1EducationHakukohde(any(String.class))).thenReturn(givenV1Hakukohde());
        when(tarjontaRawService.getV1EducationHakuByOid(any(String.class))).thenReturn(givenV1Haku());
        when(providerService.getByOID(any(String.class))).thenReturn(new Provider());
        when(aoCreator.createV1EducationApplicationOption(any(KoulutusLOS.class), any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class)))
                .thenReturn(givenApplicationOption());
        when(koodistoService.searchNames(any(String.class))).thenReturn(Collections.singletonList(new I18nText(ImmutableMap.of("kieli_fi", "Hakukohde"))));
        KoulutusLOS los = creator.createKansanopistoLOS(koulutus, false);
        assertEquals(los.getName(), los.getApplicationOptions().get(0).getName());
        assertEquals(los.getShortTitle(), los.getApplicationOptions().get(0).getName());
    }

    @Test
    public void createKorkeakouluopintoLOSOverwritesSeasonIfExtraParams() throws Exception{
        when(providerService.getOppilaitosTyyppiByOID(any(String.class))).thenReturn(TarjontaConstants.OPPILAITOSTYYPPI_AMK);
        when(tarjontaRawService.getV1EducationHakukohde(any(String.class))).thenReturn(givenV1Hakukohde());
        when(tarjontaRawService.findHakukohdesByEducationOid(any(String.class), anyBoolean())).thenReturn(givenHakukohdeResult());
        when(tarjontaRawService.getV1EducationHakuByOid(any(String.class))).thenReturn(givenV1Haku());
        when(aoCreator.createV1EducationApplicationOption(any(KoulutusLOS.class), any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class)))
                .thenReturn(givenApplicationOption());
        when(providerService.getByOID(any(String.class))).thenReturn(new Provider());

        Map<String, String> kesaMap = ImmutableMap.of(
                "kieli_fi", "Kesä",
                "kieli_sv", "Sommar",
                "kieli_en", "Summer"
        );

        I18nText I18NKesa = new I18nText(ImmutableMap.of(
                "fi", "Kesä",
                "sv", "Sommar",
                "en", "Summer"
        ));

        I18nText I18NKevat = new I18nText(ImmutableMap.of(
                "fi", "Kevät",
                "sv", "Vår",
                "en", "Spring"
        ));

        KoodiV1RDTO syksy = new KoodiV1RDTO("syksyUri", 1, "Syksy");
        KoodiV1RDTO kevat = givenKevatKoodiV1RDTOWithMeta();

        KorkeakouluOpintoV1RDTO dto = givenKorkeakouluOpintoDTO("921.00.123.12");
        KorkeakouluOpintoV1RDTO dto2 = givenKorkeakouluOpintoDTO("921.00.123.13");

        // LO with starting season overwrite using extraParams
        dto.setOpintopolkuAlkamiskausi(kesaMap);
        dto.setKoulutuksenAlkamiskausi(syksy);
        dto.setExtraParams(ImmutableMap.of("opintopolkuKesaKausi", "true"));

        // Regular LO with Spring starting season
        dto2.setKoulutuksenAlkamiskausi(kevat);

        /**
         * First los should have startSeason as 'Summer', because of extraParams.
         * Second los should have startSeason as 'Spring'.
         */
        KoulutusLOS los = creator.createKorkeakouluopinto(dto, false);
        KoulutusLOS los2 = creator.createKorkeakouluopinto(dto2, false);

        assertEquals(I18NKesa.get("fi"), los.getStartSeason().get("fi"));
        assertEquals(I18NKesa.get("sv"), los.getStartSeason().get("sv"));
        assertEquals(I18NKesa.get("en"), los.getStartSeason().get("en"));
        assertEquals(I18NKevat.get("fi"), los2.getStartSeason().get("fi"));
        assertEquals(I18NKevat.get("sv"), los2.getStartSeason().get("sv"));
        assertEquals(I18NKevat.get("en"), los2.getStartSeason().get("en"));
    }


    private ApplicationOption givenApplicationOption() {
        ApplicationOption ao = new ApplicationOption();
        ao.setName(new I18nText(givenCodeMap("Hakukohde", "FI")));
        ao.setApplicationSystem(new ApplicationSystem());
        ao.setApplicationStartDate(new Date());
        ao.setApplicationEndDate(new Date());
        return ao;
    }

    private ResultV1RDTO<HakuV1RDTO> givenV1Haku() {
        return new ResultV1RDTO<HakuV1RDTO>(new HakuV1RDTO());
    }

    private ResultV1RDTO<HakukohdeV1RDTO> givenV1Hakukohde() {
        HakukohdeV1RDTO hakukohde = new HakukohdeV1RDTO();
        hakukohde.setOid("392.12.345.231");
        hakukohde.setTila(TarjontaTila.JULKAISTU);
        hakukohde.setHakukohteenNimiUri("Hakukohde");
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAlinHyvaksyttavaKeskiarvo(4.0);
        hakukohde.setLiitteidenToimitusPvm(new Date());
        hakukohde.setEdellisenVuodenHakijatLkm(10);
        hakukohde.setKaksoisTutkinto(false);
        hakukohde.setSoraKuvausKoodiUri("soraUri");
        hakukohde.setHakukelpoisuusvaatimusUris(Arrays.asList("prerequisiteUri1","prerequisiteUri2"));
        return new ResultV1RDTO<HakukohdeV1RDTO>(hakukohde);
    }

    private ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> givenHakukohdeResult() {
        HakukohdeHakutulosV1RDTO hakukohdeHakutulosV1RDTO = new HakukohdeHakutulosV1RDTO();
        hakukohdeHakutulosV1RDTO.setNimi(givenCodeMap("Hakukohde", "fi"));
        hakukohdeHakutulosV1RDTO.setOid("125.244.5552.23432.50303");

        HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO> hakutuloksetV1RDTO = new HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>();
        TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> tarjoajaHakutulosV1RDTO = new TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>();

        tarjoajaHakutulosV1RDTO.setTulokset(Arrays.asList(hakukohdeHakutulosV1RDTO));
        hakutuloksetV1RDTO.setTulokset(Arrays.asList(tarjoajaHakutulosV1RDTO));
        return new ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>(hakutuloksetV1RDTO);
    }

    private ValmistavaKoulutusV1RDTO givenValmistavaKoulutus() {
        ValmistavaKoulutusV1RDTO dto = new ValmistavaKoulutusV1RDTO();
        dto.setCreated(new Date());
        dto.setCreatedBy("Teppo Testaaja");
        dto.setKomoOid("123.123.234.123");
        dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setModified(new Date());
        dto.setModifiedBy("Teppo Testaaja");
        dto.setOid("921.00.123.12");
        dto.setOrganisaatio(new OrganisaatioV1RDTO("orgOid"));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));
        dto.setKuvausKomo(new KuvausV1RDTO<KomoTeksti>());
        dto.setKuvausKomoto(new KuvausV1RDTO<KomotoTeksti>());
        dto.setToteutustyyppi(ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
        dto.setKoulutuskoodi(givenKoodiV1RDTOWithMeta());
        dto.setKoulutusohjelmanNimiKannassa(givenCodeMap("KoulutusOhjelmanNimiKannassa", "fi"));
        dto.setKoulutusala(givenKoodiV1RDTOWithMeta());
        dto.setKoulutusaste(givenKoodiV1RDTOWithMeta());
        dto.setTutkinto(givenKoodiV1RDTOWithMeta());
        dto.setSuunniteltuKestoTyyppi(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusarvo(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusyksikko(givenKoodiV1RDTOWithMeta());
        dto.setPohjakoulutusvaatimus(givenKoodiV1RDTOWithMeta());
        return dto;
    }

    private KoodiV1RDTO givenKoodiV1RDTOWithMeta() {
        KoodiV1RDTO koodi = new KoodiV1RDTO("uri", 1, "arvo", "KoulutuksenNimi");
        koodi.setMeta(givenKoodiMeta());
        return koodi;
    }

    private KoodiV1RDTO givenKevatKoodiV1RDTOWithMeta() {
        KoodiV1RDTO kevat = new KoodiV1RDTO("kevatUri", 1, "Kevät");

        KoodiV1RDTO kevatFi = new KoodiV1RDTO("kevatUri", 1, "Kevät");
        kevatFi.setKieliArvo("fi");
        kevatFi.setNimi("Kevät");

        KoodiV1RDTO kevatSv = new KoodiV1RDTO("vorUri", 1, "Vår");
        kevatSv.setKieliArvo("sv");
        kevatSv.setNimi("Vår");

        KoodiV1RDTO kevatEn = new KoodiV1RDTO("sprUri", 1, "Spring");
        kevatEn.setKieliArvo("en");
        kevatEn.setNimi("Spring");

        kevat.setMeta(ImmutableMap.of(
                "fi", kevatFi,
                "sv", kevatSv,
                "en", kevatEn
        ));

        return kevat;
    }

    private Map<String, KoodiV1RDTO> givenKoodiMeta() {
        Map<String, KoodiV1RDTO> map = new HashMap<String, KoodiV1RDTO>();
        KoodiV1RDTO koodi = new KoodiV1RDTO("uri", 1, "arvo", "KoulutuksenNimi");
        koodi.setKieliArvo("arvo");
        koodi.setKieliUri("uri_fi");
        koodi.setKieliKaannos("KoulutuksenNimi");
        map.put("fi", koodi);
        return map;
    }

    private Map<String, String> givenCodeMap(String name, String language) {
        Map<String, String> codeMap = new HashMap<String, String>();
        codeMap.put(name, language);
        return codeMap;
    }

    private KorkeakouluOpintoV1RDTO givenKorkeakouluOpintoDTO(String oid) {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();

        dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
        dto.setCreated(new Date());
        dto.setCreatedBy("Teppo Testaaja");
        dto.setKomoOid("123.123.234.123");
        dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
        dto.setModified(new Date());
        dto.setModifiedBy("Teppo Testaaja");
        dto.setOid(oid);
        dto.setOrganisaatio(new OrganisaatioV1RDTO("orgOid"));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));
        dto.setKuvausKomo(new KuvausV1RDTO<KomoTeksti>());
        dto.setKuvausKomoto(new KuvausV1RDTO<KomotoTeksti>());
        dto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUOPINTO);
        dto.setKoulutuskoodi(givenKoodiV1RDTOWithMeta());
        dto.setKoulutusala(givenKoodiV1RDTOWithMeta());
        dto.setKoulutusaste(givenKoodiV1RDTOWithMeta());
        dto.setTutkinto(givenKoodiV1RDTOWithMeta());
        dto.setSuunniteltuKestoTyyppi(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusarvo(givenKoodiV1RDTOWithMeta());
        dto.setOpintojenLaajuusyksikko(givenKoodiV1RDTOWithMeta());
        dto.setOppiaineet(new HashSet<OppiaineV1RDTO>());
        dto.setOpetusTarjoajat(Sets.<String>newHashSet());
        return dto;
    }

}
