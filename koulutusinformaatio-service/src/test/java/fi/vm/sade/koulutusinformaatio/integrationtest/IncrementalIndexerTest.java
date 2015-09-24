package fi.vm.sade.koulutusinformaatio.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.TutkintoLOSEntity;
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
    private TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private TarjontaRawServiceMock tarjontaRawServiceMock;

    @After
    public void removeTestData() {
        testHelper.removeTestData();
    }

    @Test
    public void testThatChangedKomotosAreIndexed() throws Exception {
        tarjontaRawServiceMock.setTestCase("testThatChangedKomotosAreIndexed");
        List<KoulutusLOSEntity> losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "1.2.246.562.10.34581043303", "koulutus_351301");
        assertEquals(0, losses.size());

        incrementalUpdateService.updateChangedEducationData();

        losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "1.2.246.562.10.34581043303", "koulutus_351301");
        assertEquals(2, losses.size());
    }

    @Test
    public void testBug521() throws Exception {
        tarjontaRawServiceMock.setTestCase("testBug521");
        
        TutkintoLOSEntity tutkinto1 = new TutkintoLOSEntity();
        tutkinto1.setId("1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2015_Syksy");
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.61512976061", tutkinto1));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.27586825005", tutkinto1));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.165818280810", tutkinto1));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.25298814663", tutkinto1));
        
        TutkintoLOSEntity tutkinto2 = new TutkintoLOSEntity();
        tutkinto2.setId("1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2016_Kevät");
        tutkinto2.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.61486530712", tutkinto2));
        tutkinto2.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.64285210601", tutkinto2));

        tutkintoLOSDAO.save(tutkinto1);
        tutkintoLOSDAO.save(tutkinto2);
        
        for (KoulutusLOSEntity los : tutkinto1.getChildEducations()) {
            koulutusLOSDAO.save(los);
        }
        for (KoulutusLOSEntity los : tutkinto2.getChildEducations()) {
            koulutusLOSDAO.save(los);
        }

        assertEquals(2, tutkintoLOSDAO.count());
        assertEquals(6, koulutusLOSDAO.count());

        incrementalUpdateService.updateChangedEducationData();

        assertFalse("Tee testi loppuun", true);
    }

    private KoulutusLOSEntity getkoulutusLosEntity(String oid, TutkintoLOSEntity tutkinto) {
        KoulutusLOSEntity e = new KoulutusLOSEntity();
        e.setId(oid);
        e.setTutkinto(tutkinto);
        e.setToteutustyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        return e;

    }

}

