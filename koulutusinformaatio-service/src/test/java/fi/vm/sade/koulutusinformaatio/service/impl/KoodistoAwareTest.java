/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Super class for tests that test classes that require koodisto.
 * Mocks common koodisto requests like language key request.
 *
 * @author Hannu Lyytikainen
 */
public abstract class KoodistoAwareTest {

    protected KoodistoService koodistoService;

    private static final String FI_URI = "fiUri";
    private static final String FI = "fi";
    private static final String SV_URI = "svUri";
    private static final String SV = "sv";
    private static final String EN_URI = "enUri";
    private static final String EN = "en";
    @Before
    public void setupKoodisto() throws KoodistoException {
        koodistoService = mock(KoodistoService.class);
        when(koodistoService.searchFirstCodeValue(FI_URI)).thenReturn(FI);
        when(koodistoService.searchFirstCodeValue(SV_URI)).thenReturn(SV);
        when(koodistoService.searchFirstCodeValue(EN_URI)).thenReturn(EN);

    }

    public static String getFi() {
        return FI;
    }

    public static String getSv() {
        return SV;
    }

    public static String getEn() {
        return EN;
    }

    public String getFiUri() {
        return FI_URI;
    }

    public static String getSvUri() {
        return SV_URI;
    }

    public static String getEnUri() {
        return EN_URI;
    }
}
