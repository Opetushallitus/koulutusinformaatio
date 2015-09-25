package fi.vm.sade.koulutusinformaatio.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
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
    private ApplicationOptionDAO applicationOptionDAO;

    @Autowired
    private TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

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

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId("1.2.246.562.10.10779357598");
        
        TutkintoLOSEntity tutkinto1 = new TutkintoLOSEntity();
        tutkinto1.setId("1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2015_Syksy");
        tutkinto1.setProvider(provider);
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.61512976061", tutkinto1, "koulutus_321101", provider));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.27586825005", tutkinto1, "koulutus_321101", provider));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.165818280810", tutkinto1, "koulutus_321101", provider));
        tutkinto1.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.25298814663", tutkinto1, "koulutus_321101", provider));
        
        TutkintoLOSEntity tutkinto2 = new TutkintoLOSEntity();
        tutkinto2.setId("1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2016_Kevät");
        tutkinto2.setProvider(provider);
        tutkinto2.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.61486530712", tutkinto2, "koulutus_321101", provider));
        tutkinto2.getChildEducations().add(getkoulutusLosEntity("1.2.246.562.17.64285210601", tutkinto2, "koulutus_321101", provider));

        tutkintoLOSDAO.save(tutkinto1);
        tutkintoLOSDAO.save(tutkinto2);
        
        learningOpportunityProviderDAO.save(provider);

        for (KoulutusLOSEntity los : tutkinto1.getChildEducations()) {
            koulutusLOSDAO.save(los);
        }
        for (KoulutusLOSEntity los : tutkinto2.getChildEducations()) {
            koulutusLOSDAO.save(los);
        }

        assertEquals(2, tutkintoLOSDAO.count());
        assertEquals(6, koulutusLOSDAO.count());
        assertEquals(0, applicationOptionDAO.count());

        incrementalUpdateService.updateChangedEducationData();

        List<TutkintoLOSEntity> tutkintos = tutkintoLOSDAO.find().asList();
        List<KoulutusLOSEntity> losses = koulutusLOSDAO.find().asList();
        List<ApplicationOptionEntity> aos = applicationOptionDAO.find().asList();
        assertEquals(2, tutkintos.size());
        assertEquals(6, losses.size());
        assertEquals(6, aos.size());

        Set<String> usedAoOids = new HashSet<String>();
        Set<String> usedKoulutusOids = new HashSet<String>();

        for (TutkintoLOSEntity tutkinto : tutkintos) {
            validateTutkinto(tutkinto);
            for (KoulutusLOSEntity koulutus : tutkinto.getChildEducations()) {
                usedKoulutusOids.add(koulutus.getId());
            }
            for (ApplicationOptionEntity ao : tutkinto.getApplicationOptions()) {
                usedAoOids.add(ao.getId());
            }
        }
        for (KoulutusLOSEntity koulutus : losses) {
            validateKoulutus(koulutus);
            usedKoulutusOids.remove(koulutus.getId());
            for (ApplicationOptionEntity ao : koulutus.getApplicationOptions()) {
                usedAoOids.add(ao.getId());
            }
        }
        for (ApplicationOptionEntity ao : aos) {
            validateAo(ao);
            usedAoOids.remove(ao.getId());
        }
        assertEquals("There were orphaned koulutus: " + usedKoulutusOids, 0, usedKoulutusOids.size());
        assertEquals("There were orphaned aos: " + usedAoOids, 0, usedAoOids.size());
    }

    private void validateTutkinto(TutkintoLOSEntity tutkinto) {
        assertTrue("Tutkinto " + tutkinto.getId() + " has no application options.", tutkinto.getApplicationOptions().size() > 0);
        assertTrue("Tutkinto " + tutkinto.getId() + " has no koulutus.", tutkinto.getChildEducations().size() > 0);
    }

    private void validateKoulutus(KoulutusLOSEntity koulutus) {
        assertTrue("Koulutus " + koulutus.getId() + " has no application options.", koulutus.getApplicationOptions().size() > 0);
        assertNotNull("Koulutus " + koulutus.getId() + " has no tutkinto.", koulutus.getTutkinto());
    }

    private void validateAo(ApplicationOptionEntity ao) {
        assertNotNull(ao.getApplicationSystem());
    }

    private KoulutusLOSEntity getkoulutusLosEntity(String oid, TutkintoLOSEntity tutkinto, String koulutuskoodi, LearningOpportunityProviderEntity provider) {
        KoulutusLOSEntity e = new KoulutusLOSEntity();
        e.setId(oid);
        e.setTutkinto(tutkinto);
        CodeEntity koulutusPrerequisite = new CodeEntity();
        koulutusPrerequisite.setUri(koulutuskoodi);
        e.setEducationCode(koulutusPrerequisite);
        e.setToteutustyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        e.setProvider(provider);
        return e;
    }

}

