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

import fi.vm.sade.koulutusinformaatio.domain.ChildLORef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class KoulutusinformaatioObjectBuilder {

    private static final String LANG_FI = "fi";

    public static ChildLORef buildChildLORef(final ChildLOS childLO) {
        if (childLO != null) {
            ChildLORef ref = new ChildLORef();
            ref.setChildLOId(childLO.getId());
            ref.setName(childLO.getName());
            ref.setNameByTeachingLang(getTextByEducationLanguage(childLO.getName(), childLO.getTeachingLanguages()));
            ref.setAsIds(childLO.getApplicationSystemIds());
            ref.setPrerequisite(childLO.getPrerequisite());
            return ref;
        }
        return null;
    }

    private static String getTextByEducationLanguage(final I18nText text, List<Code> languages) {
        if (text != null && text.getTranslationsShortName() != null && !text.getTranslationsShortName().isEmpty()) {
            if (languages != null && !languages.isEmpty()) {
                for (Code code : languages) {
                    if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                        return text.getTranslationsShortName().get(LANG_FI);
                    }
                }
                String val = text.getTranslationsShortName().get(languages.get(0).getValue().toLowerCase());
                if (val != null) {
                    return val;
                }
            }
            return text.getTranslationsShortName().values().iterator().next();
        }
        return null;
    }
}
