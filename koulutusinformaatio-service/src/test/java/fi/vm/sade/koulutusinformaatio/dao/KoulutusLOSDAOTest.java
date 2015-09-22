/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.WriteResult;

import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class KoulutusLOSDAOTest {

    @Autowired
    private KoulutusLOSDAO koulutusLOSDAO;

    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    private Key<KoulutusLOSEntity> saveTestKoulutus(String id, String providerOid, String educationCode, ToteutustyyppiEnum toteutustyyppi) {
        KoulutusLOSEntity los = new KoulutusLOSEntity();
        los.setId(id);

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId(providerOid);
        learningOpportunityProviderDAO.getDatastore().save(provider);
        los.setProvider(provider);

        CodeEntity code = new CodeEntity();
        code.setUri(educationCode);
        los.setEducationCode(code);
        los.setToteutustyyppi(toteutustyyppi);

        return koulutusLOSDAO.getDatastore().save(los);
    }

    @Test
    public void testGetKoulutusLos() throws ResourceNotFoundException {
        // Educations that we want to find
        saveTestKoulutus("0", "tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("1", "tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("2", "tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);

        // Educations that we don't want to find
        saveTestKoulutus("3", "tarjoaja2", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("4", "tarjoaja1", "koodi_2", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("5", "tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);

        List<KoulutusLOSEntity> losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "tarjoaja1", "koodi_1");

        assertEquals(3, losses.size());
        KoulutusLOSEntity firstLos = losses.get(0);
        assertEquals("tarjoaja1", firstLos.getProvider().getId());
        assertEquals("koodi_1", firstLos.getEducationCode().getUri());
        assertEquals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, firstLos.getToteutustyyppi());
    }

    @Test
    public void testDeleteKoulutus() {
        WriteResult result = koulutusLOSDAO.deleteById("nonexistentkoulutusoid");
        assertEquals("Non existent oid was deleted", 0, result.getLastError().get("n"));

        long count = koulutusLOSDAO.count();
        Key<KoulutusLOSEntity> saved = saveTestKoulutus("6", "tarjoaja", "koodi_2", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        WriteResult result2 = koulutusLOSDAO.deleteById((String) saved.getId());
        assertEquals("Koulutus was not deleted", 1, result2.getLastError().get("n"));
        long count2 = koulutusLOSDAO.count();

        assertEquals("Delete did not succeed", count, count2);
    }

}
