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

package fi.vm.sade.koulutusinformaatio.util;

import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLORefEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class TestUtil {

    public static I18nText createI18nText(String fi, String sv, String en) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        values.put("sv", sv);
        values.put("en", en);
        return new I18nText(values);
    }

    public static I18nTextEntity createI18nTextEntity(String fi, String sv, String en) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        values.put("sv", sv);
        values.put("en", en);
        I18nTextEntity entity = new I18nTextEntity();
        entity.setTranslations(values);
        return entity;
    }

    public static ChildLORefEntity createChildLORef(String name, String asId, String losId, String loiId) {
        ChildLORefEntity ref = new ChildLORefEntity();
        ref.setNameByTeachingLang(name + " fi");
        ref.setName(createI18nTextEntity(name + " fi", name + " sv", name + " en"));
        ref.setAsId(asId);
        ref.setLosId(losId);
        ref.setLoiId(loiId);
        return ref;
    }
}
