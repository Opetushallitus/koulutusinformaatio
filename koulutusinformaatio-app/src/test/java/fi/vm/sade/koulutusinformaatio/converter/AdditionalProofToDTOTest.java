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

package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.AdditionalProof;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ScoreLimit;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdditionalProofDTO;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Hannu Lyytikainen
 */
public class AdditionalProofToDTOTest {

    @Test
    public void testConvert() {
        AdditionalProof ap = new AdditionalProof();
        Map<String, String> translations = Maps.newHashMap();
        translations.put("fi", "description");
        ap.setDescreption(new I18nText(translations));
        ap.setScoreLimit(new ScoreLimit());

        AdditionalProofDTO dto = AdditionalProofToDTO.convert(ap, "fi");
        assertNotNull(dto);
        assertEquals("description", dto.getDescreption());
        assertNotNull(dto.getScoreLimit());
    }

    @Test
    public void testConvertNull() {
        assertNull(AdditionalProofToDTO.convert(null, "fi"));
    }


}
