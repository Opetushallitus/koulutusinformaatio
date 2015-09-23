package fi.vm.sade.koulutusinformaatio.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class IncrementalIndexerTest {

    @Autowired
    private IncrementalUpdateService incrementalUpdateService;

    @Autowired
    private KoulutusLOSDAO koulutusLOSDAO;

    @Autowired
    private TestHelper testHelper;

    @After
    public void removeTestData() {
        testHelper.removeTestData();
    }

    @Test
    public void testThatChangedKomotosAreIndexed() throws Exception {
        List<KoulutusLOSEntity> losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "1.2.246.562.10.34581043303", "koulutus_351301");
        assertEquals(0, losses.size());

        incrementalUpdateService.updateChangedEducationData();

        losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "1.2.246.562.10.34581043303", "koulutus_351301");
        assertEquals(2, losses.size());
    }

}

