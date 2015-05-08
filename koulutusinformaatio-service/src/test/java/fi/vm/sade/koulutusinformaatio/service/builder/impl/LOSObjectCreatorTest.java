package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.StandaloneLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import static org.mockito.Matchers.any;

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
    private LOIObjectCreator loiCreator;

    @Mock
    private TarjontaRawService tarjontaRawService;

    @InjectMocks
    private LOSObjectCreator creator;

    @Before
    public void init() throws Exception {
        loiCreator.tarjontaRawService = tarjontaRawService;
        loiCreator.applicationOptionCreator = aoCreator;
        when(koodistoService.search(any(String.class))).thenReturn(new ArrayList<Code>());
        when(tarjontaRawService.getHakukohdesByEducationOid(any(String.class))).thenReturn(null);
    }
    
    @Test
    public void createsKansanopistoLOSWithoutCheckingStatus() throws Exception {
        StandaloneLOS los = creator.createKansanopistoLOS(givenValmistavaKoulutus(), false);
        assertNotNull(los);
    }

    @Test (expected = TarjontaParseException.class)
    public void doesNotCreateKansanopistoLOSWhenStatusCheckFails() throws Exception {
        creator.createKansanopistoLOS(givenValmistavaKoulutus(), true);
    }
    
    @Test
    public void setsNameForKansanopistoLOSFromApplicationOptionWhenHakukohteenNimiKannassaIsNull() throws Exception {
        ValmistavaKoulutusV1RDTO koulutus = givenValmistavaKoulutus();
        koulutus.setKoulutusohjelmanNimiKannassa(null);
        when(tarjontaRawService.getHakukohdesByEducationOid(any(String.class))).thenReturn(givenHakukohdeResult());
        when(tarjontaRawService.getV1EducationHakukohode(any(String.class))).thenReturn(givenV1Hakukohde());
        when(tarjontaRawService.getV1EducationHakuByOid(any(String.class))).thenReturn(givenV1Haku());
        when(providerService.getByOID(any(String.class))).thenReturn(new Provider());
        when(aoCreator.createV1EducationApplicationOption(any(StandaloneLOS.class), any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class)))
                .thenReturn(givenApplicationOption());
        StandaloneLOS los = creator.createKansanopistoLOS(koulutus, false);
        assertEquals(los.getName(), los.getApplicationOptions().get(0).getName());
        assertEquals(los.getShortTitle(), los.getApplicationOptions().get(0).getName());
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
        hakukohde.setHakukohteenNimiUri("Hakukohde");
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAlinHyvaksyttavaKeskiarvo(4);
        hakukohde.setLiitteidenToimitusPvm(new Date());
        hakukohde.setEdellisenVuodenHakijatLkm(10);
        hakukohde.setKaksoisTutkinto(false);
        hakukohde.setSoraKuvausKoodiUri("soraUri");
        hakukohde.setHakukelpoisuusvaatimusUris(Arrays.asList("prerequisiteUri1","prerequisiteUri2"));
        return new ResultV1RDTO<HakukohdeV1RDTO>(hakukohde);
    }

    private ResultV1RDTO<List<NimiJaOidRDTO>> givenHakukohdeResult() {
        NimiJaOidRDTO rdto = new NimiJaOidRDTO(givenCodeMap("Hakukohde", "fi"), "125.244.5552.23432.50303");
        return new ResultV1RDTO<List<NimiJaOidRDTO>>(Arrays.asList(rdto));
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
        dto.setTila(TarjontaTila.JULKAISTU);
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

}