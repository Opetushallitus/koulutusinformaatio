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
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class KoodiTypeToCode implements Converter<KoodiType, Code> {
    @Override
    public Code convert(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> name = Maps.newHashMap();
        Map<String, String> shortName = Maps.newHashMap();
        Map<String, String> description = Maps.newHashMap();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            String lang = koodiMetadataType.getKieli().value().toLowerCase();
            String nameStr = koodiMetadataType.getNimi() != null ? koodiMetadataType.getNimi() : "";
            name.put(lang, nameStr);
            String shortNameStr = koodiMetadataType.getLyhytNimi() != null ? koodiMetadataType.getLyhytNimi() : "";
            shortName.put(lang, shortNameStr);
            String descrStr = koodiMetadataType.getKuvaus() != null ? koodiMetadataType.getKuvaus() : "";
            description.put(lang, descrStr);
        }
        return new Code(koodiType.getKoodiArvo(), new I18nText(name), new I18nText(shortName), new I18nText(description), koodiType.getKoodiUri());
    }
}
