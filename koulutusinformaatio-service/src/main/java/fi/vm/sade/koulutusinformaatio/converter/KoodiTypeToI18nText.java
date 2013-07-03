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

package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class KoodiTypeToI18nText implements Converter<KoodiType, I18nText> {

    @Override
    public I18nText convert(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> translations = new HashMap<String, String>();
        Map<String, String> translationsShortName = new HashMap<String, String>();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
            translationsShortName.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getLyhytNimi());
        }
        return new I18nText(translations, translationsShortName);
    }
}
