package fi.vm.sade.koulutusinformaatio.integrationtest;

import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.service.IncrementalUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class IncrementalIndexerTest {

    @Autowired
    private IncrementalUpdateService incrementalUpdateService;

    @Autowired
    private TarjontaRawService tarjontaRawService;

    @Autowired
    private KoulutusLOSDAO koulutusLOSDAO;

    @Test
    public void testThatChangedKomotosAreIndexed() throws Exception {
        assertEquals(0, koulutusLOSDAO.count());
        incrementalUpdateService.updateChangedEducationData();

        assertEquals(1, koulutusLOSDAO.count());
        //tarjontaRawService.listEducations(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.name(), "1.2.246.562.10.34581043303", "koulutus_351301");
    }

}

