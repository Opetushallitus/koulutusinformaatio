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
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLOIRefDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public final class ChildLOIRefToDTO {

    private ChildLOIRefToDTO() {
    }

    public static List<ChildLOIRefDTO> convert(final List<ChildLOIRef> refs, final String lang) {
        List<ChildLOIRefDTO> children = new ArrayList<ChildLOIRefDTO>();
        if (refs != null) {
            for (ChildLOIRef ref : refs) {
                ChildLOIRefDTO child = convert(ref, lang);
                children.add(child);
            }
        }
        Collections.sort(children);
        return children;
    }

    public static ChildLOIRefDTO convert(final ChildLOIRef ref, final String lang) {
        ChildLOIRefDTO child = new ChildLOIRefDTO();
        child.setId(ref.getId());
        child.setLosId(ref.getLosId());
        child.setPrerequisite(CodeToDTO.convert(ref.getPrerequisite(), lang));
        child.setQualification(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getQualification(), lang));
        child.setQualifications(ConverterUtil.getTextsByLanguage(ref.getQualifications(), lang));
        if (!Strings.isNullOrEmpty(ref.getNameByTeachingLang())) {
            child.setName(ref.getNameByTeachingLang());
        } else {
            child.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getName(), lang));
        }
        return child;
    }

    public static List<I18nText> convert(final List<ChildLOIRef> refs) {
        if (refs != null) {
            return Lists.transform(refs, new Function<ChildLOIRef, I18nText>() {
                @Override
                public I18nText apply(ChildLOIRef childLOIRef) {
                    return (childLOIRef != null) ? childLOIRef.getName() : null;
                }
            });
        } else {
            return null;
        }
    }
}
