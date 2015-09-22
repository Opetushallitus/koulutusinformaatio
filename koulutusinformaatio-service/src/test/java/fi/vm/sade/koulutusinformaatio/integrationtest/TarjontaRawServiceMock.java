package fi.vm.sade.koulutusinformaatio.integrationtest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TarjontaRawServiceMock implements TarjontaRawService {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesByHaku(String oid) {
        return null;
    }

    @Override
    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod) {
        return getJson(Map.class, "listModifiedLearningOpportunities", Long.toString(updatePeriod));
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(String... educationType) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid, boolean onlyPublished) {
        ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> res = getJson(ResultV1RDTO.class, "findHakukohdesByEducationOid", oid, Boolean.toString(onlyPublished));
        return res;
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohde(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(String parentOid) {
        return null;
    }

    @Override
    public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(String komoOid) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(String curKomoOid) {
        return null;
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        return null;
    }

    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<List<String>> searchHakus(String hakutapaYhteishaku) {
        return null;
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        return null;
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType, String providerOid, String koulutusKoodi) {
        return getJson(ResultV1RDTO.class, "listEducations", educationType, providerOid, koulutusKoodi);
    }

    private <T> T getJson(Class<T> toClass, String ...params) {
        String filename = Joiner.on("__").join(params);
        String path = "koulutusinformaatio-service/src/test/resources/tarjontaJsonResponses/" + filename + ".json";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String json = new String(encoded, StandardCharsets.UTF_8);
            return mapper.readValue(json, toClass);
        }
        catch (JsonMappingException e) {
            throw new RuntimeException("JSON mapping failed");
        }
        catch (JsonParseException e) {
            throw new RuntimeException("JSON parsing failed");
        }
        catch (IOException e) {
            throw new RuntimeException("JSON does not exist!");
        }
    }

}
