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

package fi.vm.sade.koulutusinformaatio.domain;

import fi.vm.sade.koulutusinformaatio.domain.search.Organization;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganisaatioTest {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final Organization ORGANIZATION_1 = new Organization(ID, NAME);
    public static final Organization ORGANIZATION_NULL_ID = new Organization(null, NAME);

    @Test
    public void testEqualsSameObject() throws Exception {
        assertTrue(ORGANIZATION_1.equals(ORGANIZATION_1));
    }

    @Test
    public void testEqualsSameId() throws Exception {
        assertTrue(ORGANIZATION_1.equals(new Organization(ORGANIZATION_1.getId(), "N")));
    }

    @Test
    public void testEqualsFalse() throws Exception {
        assertFalse(ORGANIZATION_1.equals(new Organization(ID + NAME, NAME + ID)));
    }

    @Test
    public void testEqualsNull() throws Exception {
        assertFalse(ORGANIZATION_1.equals(null));
    }

    @Test
    public void testEqualsFalseIdNull() throws Exception {
        assertFalse(ORGANIZATION_NULL_ID.equals(null));
    }

    @Test
    public void testEqualsFalseIdsNull() throws Exception {
        assertTrue(ORGANIZATION_NULL_ID.equals(new Organization(null, NAME)));
    }
}
