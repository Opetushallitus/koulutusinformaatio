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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.ChildLORef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLORefDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ChildLORefToDTO {

    public static List<ChildLORefDTO> convert(final List<ChildLORef> refs, final String lang) {
        List<ChildLORefDTO> children = new ArrayList<ChildLORefDTO>();
        if (refs != null) {
            for (ChildLORef ref : refs) {
                ChildLORefDTO child = convert(ref, lang);
                children.add(child);
            }
        }
        return children;
    }

    public static ChildLORefDTO convert(final ChildLORef ref, final String lang) {
        ChildLORefDTO child = new ChildLORefDTO();
        child.setChildLOId(ref.getChildLOId());
        child.setAsIds(ref.getAsIds());
        child.setPrerequisite(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getPrerequisite(), lang));
        child.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getQualification(), lang));
        if (!Strings.isNullOrEmpty(ref.getNameByTeachingLang())) {
            child.setName(ref.getNameByTeachingLang());
        } else {
            child.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getName(), lang));
        }
        return child;
    }

    public static List<I18nText> convert(final List<ChildLORef> refs) {
        if (refs != null) {
            return Lists.transform(refs, new Function<ChildLORef, I18nText>() {
                @Override
                public I18nText apply(ChildLORef childLORef) {
                    return childLORef.getName();
                }
            });
        } else {
            return null;
        }
    }
}
