package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.codehaus.jackson.JsonToken.START_OBJECT;

public class KoulutusDeserializer extends JsonDeserializer<KoulutusV1RDTO> {

    private static final String AIKUISTEN_PERUSOPETUS = "AIKUISTEN_PERUSOPETUS";
    private static final String AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS = "AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS";
    private static final String AMMATILLINEN_PERUSKOULUTUS = "AMMATILLINEN_PERUSKOULUTUS";
    private static final String AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA = "AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA";
    private static final String AMMATILLINEN_PERUSTUTKINTO = "AMMATILLINEN_PERUSTUTKINTO";
    private static final String AMMATILLINEN_PERUSTUTKINTO_ALK_2018 = "AMMATILLINEN_PERUSTUTKINTO_ALK_2018";
    private static final String AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA = "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA";
    private static final String AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS = "AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS";
    private static final String AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA";
    private static final String AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER";
    private static final String AMMATTITUTKINTO = "AMMATTITUTKINTO";
    private static final String EB_RP_ISH = "EB_RP_ISH";
    private static final String ERIKOISAMMATTITUTKINTO = "ERIKOISAMMATTITUTKINTO";
    private static final String KORKEAKOULUOPINTO = "KORKEAKOULUOPINTO";
    private static final String KORKEAKOULUTUS = "KORKEAKOULUTUS";
    private static final String LUKIOKOULUTUS = "LUKIOKOULUTUS";
    private static final String LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA = "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA";
    private static final String MAAHANM_AMM_VALMISTAVA_KOULUTUS = "MAAHANM_AMM_VALMISTAVA_KOULUTUS";
    private static final String MAAHANM_LUKIO_VALMISTAVA_KOULUTUS = "MAAHANM_LUKIO_VALMISTAVA_KOULUTUS";
    private static final String MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS = "MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS";
    private static final String MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS = "MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS";
    private static final String PELASTUSALAN_KOULUTUS = "PELASTUSALAN_KOULUTUS";
    private static final String PERUSOPETUKSEN_LISAOPETUS = "PERUSOPETUKSEN_LISAOPETUS";
    private static final String TUNTEMATON = "TUNTEMATON";
    private static final String VALMENTAVA_JA_KUNTOUTTAVA_OPETUS = "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS";
    private static final String VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS = "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS";
    private static final String VAPAAN_SIVISTYSTYON_KOULUTUS = "VAPAAN_SIVISTYSTYON_KOULUTUS";


    @Override
    public KoulutusV1RDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != START_OBJECT) {
            throw new IOException("Expected START_OBJECT, got " + jp.getCurrentToken());
        }
        JsonNode root = jp.readValueAsTree();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return createKoulutusDTO(root, mapper);

    }

    private KoulutusV1RDTO createKoulutusDTO(JsonNode root, ObjectMapper mapper) throws IOException {
        JsonNode toteutustyyppiNode = root.get("toteutustyyppi");
        String toteutustyyppi = toteutustyyppiNode != null ? toteutustyyppiNode.asText() : null;
        JsonNode moduulityyppiNode = root.get("moduulityyppi");
        String moduulityyppi = moduulityyppiNode != null ? moduulityyppiNode.asText() : null;
        KoulutusV1RDTO dto = createConcreteDTO(toteutustyyppi, moduulityyppi);

        for (Method m : dto.getClass().getMethods()) {
            if (m.getName().startsWith("set")) {
                String prop = uncapitalize(m.getName().replaceFirst("set", ""));
                if (root.has(prop)) {
                    try {
                        Type propType = m.getGenericParameterTypes()[0];
                        Object value = mapper.readValue(root.get(prop), TypeFactory.defaultInstance().constructType(propType));
                        m.invoke(dto, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Exception while creating koulutusDTO", e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return dto;
    }

    private KoulutusV1RDTO createConcreteDTO(String toteutustyyppi, String moduulityyppi) {

        if (LUKIOKOULUTUS.equals(moduulityyppi)) {
            if (LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA.equals(toteutustyyppi)) {
                return new KoulutusLukioAikuistenOppimaaraV1RDTO();
            }
            if (EB_RP_ISH.equals(toteutustyyppi)) {
                return new KoulutusEbRpIshV1RDTO();
            }
            if (LUKIOKOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusLukioV1RDTO();
            }
        }

        if (AMMATILLINEN_PERUSKOULUTUS.equals(moduulityyppi)) {
            if (AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA.equals(toteutustyyppi)) {
                return new KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO();
            }
            if (AMMATILLINEN_PERUSTUTKINTO.equals(toteutustyyppi)) {
                return new KoulutusAmmatillinenPerustutkintoV1RDTO();
            }
            if (AMMATILLINEN_PERUSTUTKINTO_ALK_2018.equals(toteutustyyppi)) {
                return new KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO();
            }
            if (AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.equals(toteutustyyppi)) {
                return new KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO();
            }
            if (PELASTUSALAN_KOULUTUS.equals(toteutustyyppi)) {
                return new PelastusalanKoulutusV1RDTO();
            }
        }

        if (TUNTEMATON.equals(moduulityyppi)) {
            if (AIKUISTEN_PERUSOPETUS.equals(toteutustyyppi)) {
                return new KoulutusAikuistenPerusopetusV1RDTO();
            }
            if (AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER.equals(toteutustyyppi)) {
                return new KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO();
            }
            if (AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA.equals(toteutustyyppi)) {
                return new KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO();
            }
        }

        if (AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS.equals(moduulityyppi)) {
            if (AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO();
            }
        }

        if (VAPAAN_SIVISTYSTYON_KOULUTUS.equals(moduulityyppi)) {
            if (VAPAAN_SIVISTYSTYON_KOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusVapaanSivistystyonV1RDTO();
            }
        }

        if (PERUSOPETUKSEN_LISAOPETUS.equals(moduulityyppi)) {
            if (PERUSOPETUKSEN_LISAOPETUS.equals(toteutustyyppi)) {
                return new KoulutusPerusopetuksenLisaopetusV1RDTO();
            }
        }

        if (MAAHANM_LUKIO_VALMISTAVA_KOULUTUS.equals(moduulityyppi)) {
            if (MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO();
            }
        }

        if (MAAHANM_AMM_VALMISTAVA_KOULUTUS.equals(moduulityyppi)) {
            if (MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO();
            }
        }

        if (VALMENTAVA_JA_KUNTOUTTAVA_OPETUS.equals(moduulityyppi)) {
            if (VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS.equals(toteutustyyppi)) {
                return new KoulutusValmentavaJaKuntouttavaV1RDTO();
            }
        }

        if (KORKEAKOULUTUS.equals(moduulityyppi)) {
            if (KORKEAKOULUTUS.equals(toteutustyyppi)) {
                return new KoulutusKorkeakouluV1RDTO();
            } if (KORKEAKOULUOPINTO.equals(toteutustyyppi)) {
                return new KorkeakouluOpintoV1RDTO();
            }
        }

        if (ERIKOISAMMATTITUTKINTO.equals(moduulityyppi)) {
            if (AMMATTITUTKINTO.equals(toteutustyyppi)) {
                return new AmmattitutkintoV1RDTO();
            }
            if (ERIKOISAMMATTITUTKINTO.equals(toteutustyyppi)) {
                return new ErikoisammattitutkintoV1RDTO();
            }
        }

        return new KoulutusGenericV1RDTO();
    }
}
