package fi.vm.sade.koulutusinformaatio.integrationtest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.impl.TarjontaRawServiceImpl;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAikuistenPerusopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;

public class TarjontaRawServiceMock implements TarjontaRawService {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TarjontaRawServiceImpl tarjontaRawServiceImpl;

    private String testCase = "";

    private static final String JSON_MAPPING_FAILED = "JSON mapping failed";

    public TarjontaRawServiceMock() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesByHaku(String oid) {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod) {
        return getJson(Map.class, "listModifiedLearningOpportunities", Long.toString(updatePeriod));
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(String... educationType) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid, boolean onlyPublished) {
        File jsonFile = new File(getJsonPath("findHakukohdesByEducationOid", oid, Boolean.toString(onlyPublished)));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>(){});
        } catch (Exception e) {
            try {
                ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> realResult = tarjontaRawServiceImpl.findHakukohdesByEducationOid(oid, onlyPublished);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohde(String oid) {
        File jsonFile = new File(getJsonPath("getV1EducationHakukohde", oid));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<HakukohdeV1RDTO>>() {});
        } catch (Exception e) {
            try {
                ResultV1RDTO<HakukohdeV1RDTO> realResult = tarjontaRawServiceImpl.getV1EducationHakukohde(oid);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }

    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid) {
        File jsonFile = new File(getJsonPath("getV1EducationHakuByOid", oid));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<HakuV1RDTO>>() {});
        } catch (Exception e) {
            try {
                ResultV1RDTO<HakuV1RDTO> realResult = tarjontaRawServiceImpl.getV1EducationHakuByOid(oid);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    @Override
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(String parentOid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(String komoOid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(String curKomoOid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(String oid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid) {
        File jsonFile = new File(getJsonPath("getV1Komo", oid));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<KomoV1RDTO>>() {});
        } catch (Exception e) {
            try {
                ResultV1RDTO<KomoV1RDTO> realResult = tarjontaRawServiceImpl.getV1Komo(oid);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    @Override
    public ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(String oid) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<List<String>> searchHakus(String hakutapaYhteishaku) {
        throw new NotImplementedException();
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid) {
        File jsonFile = new File(getJsonPath("getV1KoulutusLearningOpportunity", oid));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<KoulutusV1RDTO>>(){});
        } catch (Exception e) {
            try {
                ResultV1RDTO<KoulutusV1RDTO> realResult = tarjontaRawServiceImpl.getV1KoulutusLearningOpportunity(oid);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) {
        File jsonFile = new File(getJsonPath("searchEducation", oid));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>(){});
        } catch (Exception e) {
            try {
                ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> realResult = tarjontaRawServiceImpl.searchEducation(oid);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType, String providerOid, String koulutusKoodi) {
        File jsonFile = new File(getJsonPath("listEducations", educationType, providerOid, koulutusKoodi));
        try {
            return mapper.readValue(jsonFile, new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {});
        } catch (Exception e) {
            try {
                ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> realResult = tarjontaRawServiceImpl.listEducations(educationType, providerOid,
                        koulutusKoodi);
                mapper.writeValue(jsonFile, realResult);
                return realResult;
            } catch (IOException e1) {
                throw new Error(JSON_MAPPING_FAILED);
            }
        }
    }

    private String getJsonPath(String... params) {
        return "src/test/resources/tarjontaJsonResponses/" + testCase + "/" + Joiner.on("__").join(params) + ".json";
    }

    private <T> T getJson(Class<T> toClass, String ...params) {
        try {
            return mapper.readValue(new File(getJsonPath(params)), toClass);
        }
        catch (JsonMappingException e) {
            throw new RuntimeException(JSON_MAPPING_FAILED);
        }
        catch (JsonParseException e) {
            throw new RuntimeException("JSON parsing failed");
        }
        catch (IOException e) {
            throw new RuntimeException("JSON does not exist!");
        }
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

}
