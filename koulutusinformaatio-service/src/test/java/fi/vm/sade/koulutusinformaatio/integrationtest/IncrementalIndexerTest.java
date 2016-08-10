package fi.vm.sade.koulutusinformaatio.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CompetenceBasedQualificationParentLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
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

    @Autowired
    private DataStatusDAO dataStatusDAO;

    @Autowired
    private AdultVocationalLOSDAO adultVocationalLOSDAO;

    @After
    public void removeTestData() {
        testHelper.removeTestData();
    }

    @Before
    public void init() {
        DataStatusEntity dataStatus = new DataStatusEntity();
        dataStatus.setLastUpdateDuration(10);
        dataStatus.setLastUpdateFinished(new Date(1000));
        dataStatus.setLastUpdateOutcome("SUCCESS");
        when(dataStatusDAO.getLatestSuccessOrIncremental()).thenReturn(dataStatus);
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
        List<KoulutusLOSEntity> children1 = Lists.newArrayList(
                getkoulutusLosEntity("1.2.246.562.17.61512976061", tutkinto1, "koulutus_321101", provider),
                getkoulutusLosEntity("1.2.246.562.17.27586825005", tutkinto1, "koulutus_321101", provider),
                getkoulutusLosEntity("1.2.246.562.17.165818280810", tutkinto1, "koulutus_321101", provider),
                getkoulutusLosEntity("1.2.246.562.17.25298814663", tutkinto1, "koulutus_321101", provider)
                );
        for (KoulutusLOSEntity child : children1) {
            child.setSiblings(children1);
        }
        tutkinto1.setChildEducations(children1);

        TutkintoLOSEntity tutkinto2 = new TutkintoLOSEntity();
        tutkinto2.setId("1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2016_Kevät");
        tutkinto2.setProvider(provider);

        List<KoulutusLOSEntity> children2 = Lists.newArrayList(
                getkoulutusLosEntity("1.2.246.562.17.61486530712", tutkinto2, "koulutus_321101", provider), // <--- tämä palautuu las modified vastauksessa
                getkoulutusLosEntity("1.2.246.562.17.64285210601", tutkinto2, "koulutus_321101", provider) // <--- peruttu
                );
        for (KoulutusLOSEntity child : children2) {
            child.setSiblings(children2);
        }
        tutkinto2.setChildEducations(children2);

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

        // Run incremental indexing
        incrementalUpdateService.updateChangedEducationData();

        List<TutkintoLOSEntity> tutkintos = tutkintoLOSDAO.find().asList();
        List<KoulutusLOSEntity> losses = koulutusLOSDAO.find().asList();
        List<ApplicationOptionEntity> aos = applicationOptionDAO.find().asList();

        List<String> allowedTutkintoIDs = Lists.newArrayList(
                "1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2015_Syksy",
                "1.2.246.562.5.2013061010184190024479_1.2.246.562.10.10779357598_2016_Kevät");
        for (TutkintoLOSEntity tutkinto : tutkintos) {
            validateTutkinto(tutkinto, allowedTutkintoIDs);
        }

        List<String> allowedKoulutusIDs = Lists.newArrayList(
                "1.2.246.562.17.61512976061",
                "1.2.246.562.17.27586825005",
                "1.2.246.562.17.165818280810",
                "1.2.246.562.17.25298814663",
                "1.2.246.562.17.61486530712",
                "1.2.246.562.17.25124155778");
        for (KoulutusLOSEntity koulutus : losses) {
            validateKoulutus(koulutus, allowedKoulutusIDs);
        }

        List<String> allowedAoIDs = Lists.newArrayList(
                "1.2.246.562.20.10843418954",
                "1.2.246.562.20.26949381318",
                "1.2.246.562.20.52393151668",
                "1.2.246.562.20.72559091441",
                "1.2.246.562.20.90183207456",
                "1.2.246.562.20.96267839536");
        for (ApplicationOptionEntity ao : aos) {
            validateAo(ao, allowedAoIDs);
        }

        assertEquals(2, tutkintos.size());
        assertEquals(6, losses.size());
        assertEquals(6, aos.size());

    }

    @Test
    public void testBug451() throws Exception {
        tarjontaRawServiceMock.setTestCase("testBug451");

        assertEquals(0, tutkintoLOSDAO.count());
        assertEquals(0, koulutusLOSDAO.count());
        assertEquals(0, applicationOptionDAO.count());
        assertEquals(0, adultVocationalLOSDAO.count());

        // Run incremental indexing
        incrementalUpdateService.updateChangedEducationData();

        List<TutkintoLOSEntity> tutkintos = tutkintoLOSDAO.find().asList();
        List<KoulutusLOSEntity> losses = koulutusLOSDAO.find().asList();
        List<CompetenceBasedQualificationParentLOSEntity> adultLosses = adultVocationalLOSDAO.find().asList();
        List<ApplicationOptionEntity> aos = applicationOptionDAO.find().asList();

        List<String> allowedTutkintoIDs = Lists.newArrayList(
                "1.2.246.562.5.2013061010190957797211_1.2.246.562.10.23856923257_2015_Syksy");
        assertEquals(allowedTutkintoIDs.size(), tutkintos.size());

        List<String> allowedKoulutusIDs = Lists.newArrayList(
                "1.2.246.562.17.61286770351",
                "1.2.246.562.17.26073764842");
        assertEquals(allowedKoulutusIDs.size(), losses.size());
        assertEquals(1, adultLosses.size());
        List<String> allowedAoIDs = Lists.newArrayList(
                "1.2.246.562.20.14429222244",
                "1.2.246.562.20.70012100114");
        assertEquals(allowedAoIDs.size(), aos.size());

        for (TutkintoLOSEntity tutkinto : tutkintos) {
            validateTutkinto(tutkinto, allowedTutkintoIDs);
        }

        for (KoulutusLOSEntity koulutus : losses) {
            validateKoulutus(koulutus, allowedKoulutusIDs);
        }

        for (ApplicationOptionEntity ao : aos) {
            validateAo(ao, allowedAoIDs);
        }
        assertEquals("1.2.246.562.17.79497399471", adultLosses.get(0).getId());

    }

    @Test
    public void testThatOpintojaksoIsIndexedCorrectly() throws Exception {

        tarjontaRawServiceMock.setTestCase("testThatOpintojaksoIsIndexedCorrectly");
        String opintojaksoId = "1.2.246.562.17.28053757085";
        KoulutusLOSEntity opintojakso = koulutusLOSDAO.get(opintojaksoId);
        String opintokokonaisuusId = "1.2.246.562.17.52083499963";
        KoulutusLOSEntity opintokokonaisuus = koulutusLOSDAO.get(opintokokonaisuusId);
        assertNull(opintojakso);
        assertNull(opintokokonaisuus);

        incrementalUpdateService.updateChangedEducationData();

        opintojakso = koulutusLOSDAO.get(opintojaksoId);
        opintokokonaisuus = koulutusLOSDAO.get(opintokokonaisuusId);
        assertNotNull(opintojakso);
        assertNotNull(opintokokonaisuus);
        assertEquals(0, opintokokonaisuus.getOpintokokonaisuudet().size());
        assertEquals(1, opintokokonaisuus.getOpintojaksos().size());
        assertEquals(0, opintojakso.getOpintojaksos().size());
        assertEquals(opintokokonaisuusId, opintojakso.getOpintokokonaisuudet().iterator().next().getId());
        assertEquals(opintojaksoId, opintokokonaisuus.getOpintojaksos().get(0).getId());

    }

    @Test
    public void testKsh718() throws Exception {
        tarjontaRawServiceMock.setTestCase("testKsh718");
        String oid = "1.2.246.562.17.70879824525";
        KoulutusLOSEntity los = koulutusLOSDAO.get(oid);
        assertNull(los);

        incrementalUpdateService.updateChangedEducationData();

        los = koulutusLOSDAO.get(oid);
        assertNotNull(los);

        assertTrue("Koulutus " + los.getId() + " has no application options.", los.getApplicationOptions().size() > 0);
        // Tekstikentät kälissä
        assertNotNull(los.getGoals());
        assertNotNull(los.getContent());
        assertNotNull(los.getStructure());
        assertNotNull(los.getLanguageSelection());
        assertNotNull(los.getDiplomas());
        assertNotNull(los.getInternationalization());
        assertNotNull(los.getCooperation());
        assertNotNull(los.getAccessToFurtherStudies());
        assertNotNull(los.getContactPersons());

        // Harmaa laatikko
        assertNotNull(los.getStartDate());
        assertNotNull(los.getTeachingLanguages());
        assertNotNull(los.getPlannedDuration());
        assertNotNull(los.getPlannedDurationUnit());
        assertNotNull(los.getTeachingTimes());
        assertNotNull(los.getTeachingPlaces());
        assertNotNull(los.getFormOfTeaching());
        assertNotNull(los.getKoulutusPrerequisite());

    }

    private void validateTutkinto(TutkintoLOSEntity tutkinto, List<String> allowedTutkinToIDs) {
        assertTrue("Tutkinto " + tutkinto.getId() + " has no application options.", tutkinto.getApplicationOptions().size() > 0);
        assertTrue("Tutkinto " + tutkinto.getId() + " has no koulutus.", tutkinto.getChildEducations().size() > 0);
        assertTrue("Unknown tutkinto indexed " + tutkinto.getId(), allowedTutkinToIDs.contains(tutkinto.getId()));
    }

    private void validateKoulutus(KoulutusLOSEntity koulutus, List<String> allowedKoulutusIDs) {
        assertTrue("Koulutus " + koulutus.getId() + " has no application options.", koulutus.getApplicationOptions().size() > 0);
        assertNotNull("Koulutus " + koulutus.getId() + " has no tutkinto.", koulutus.getTutkinto());
        assertTrue("Unknown koulutus indexed " + koulutus.getId(), allowedKoulutusIDs.contains(koulutus.getId()));
    }

    private void validateAo(ApplicationOptionEntity ao, List<String> allowedAoIDs) {
        assertNotNull(ao.getApplicationSystem());
        assertTrue("Unknown hakukohde indexed " + ao.getId(), allowedAoIDs.contains(ao.getId()));
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
