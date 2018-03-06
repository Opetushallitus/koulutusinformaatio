package fi.vm.sade.koulutusinformaatio.dao.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class KoulutusLOSEntityTest {

    @Test
    public void prePersist() {
        KoulutusLOSEntity koulutusLOSEntity = new KoulutusLOSEntity();
        List<CodeEntity> codes = new ArrayList<>();
        codes.add(new CodeEntity());
        codes.add(new CodeEntity());
        codes.add(new CodeEntity());
        Map<String, List<CodeEntity>> baseeds = new HashMap<>();
        baseeds.put("1.2.3.4.5.6.7.8.9", codes);
        koulutusLOSEntity.setAoToRequiredBaseEdCode(baseeds);

        assertTrue(koulutusLOSEntity.getAoToRequiredBaseEdCode().containsKey("1.2.3.4.5.6.7.8.9"));
        koulutusLOSEntity.prePersist();
        assertTrue(koulutusLOSEntity.getAoToRequiredBaseEdCode().containsKey("1_2_3_4_5_6_7_8_9"));


    }

    @Test
    public void postLoad() {
        KoulutusLOSEntity koulutusLOSEntity = new KoulutusLOSEntity();
        List<CodeEntity> codes = new ArrayList<>();
        codes.add(new CodeEntity());
        codes.add(new CodeEntity());
        codes.add(new CodeEntity());
        Map<String, List<CodeEntity>> baseeds = new HashMap<>();
        baseeds.put("1_2_3_4_5_6_7_8_9", codes);
        koulutusLOSEntity.setAoToRequiredBaseEdCode(baseeds);

        assertTrue(koulutusLOSEntity.getAoToRequiredBaseEdCode().containsKey("1_2_3_4_5_6_7_8_9"));
        koulutusLOSEntity.postLoad();
        assertTrue(koulutusLOSEntity.getAoToRequiredBaseEdCode().containsKey("1.2.3.4.5.6.7.8.9"));
    }
}