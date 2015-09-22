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

import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class IncrementalDAOTest {

    @Autowired
    private KoulutusLOSDAO koulutusLOSDAO;

    @Autowired
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    private KoulutusLOSEntity saveTestKoulutus(String providerOid, String educationCode, ToteutustyyppiEnum toteutustyyppi) {
        KoulutusLOSEntity los = new KoulutusLOSEntity();

        LearningOpportunityProviderEntity provider = new LearningOpportunityProviderEntity();
        provider.setId(providerOid);
        learningOpportunityProviderDAO.getDatastore().save(provider);
        los.setProvider(provider);

        CodeEntity code = new CodeEntity();
        code.setUri(educationCode);
        los.setEducationCode(code);
        los.setToteutustyyppi(toteutustyyppi);

        koulutusLOSDAO.getDatastore().save(los);

        return los;
    }

    @Test
    public void testGetKoulutusLos() throws ResourceNotFoundException {
        // Educations that we want to find
        saveTestKoulutus("tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);

        // Educations that we don't want to find
        saveTestKoulutus("tarjoaja2", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("tarjoaja1", "koodi_2", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        saveTestKoulutus("tarjoaja1", "koodi_1", ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);

        List<KoulutusLOSEntity> losses = koulutusLOSDAO.getKoulutusLos(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, "tarjoaja1", "koodi_1");

        assertEquals(3, losses.size());
        KoulutusLOSEntity firstLos = losses.get(0);
        assertEquals("tarjoaja1", firstLos.getProvider().getId());
        assertEquals("koodi_1", firstLos.getEducationCode().getUri());
        assertEquals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, firstLos.getToteutustyyppi());
    }
}
