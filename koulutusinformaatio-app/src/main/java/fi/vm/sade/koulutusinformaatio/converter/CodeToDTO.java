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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.dto.CodeDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class CodeToDTO {

    private CodeToDTO() {
    }

    public static CodeDTO convert(Code code, String lang) {
        if (code != null) {
            CodeDTO dto = new CodeDTO();
            dto.setValue(code.getValue());
            dto.setUri(code.getUri());
            dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(code.getName(), lang));
            dto.setShortName(ConverterUtil.getTextByLanguageUseFallbackLang(code.getShortName(), lang));
            dto.setDescription(ConverterUtil.getTextByLanguageUseFallbackLang(code.getDescription(), lang));
            return dto;
        } else {
            return null;
        }
    }
    
    public static List<CodeDTO> convertAll(List<Code> codes, final String lang) {
        if (codes != null) {
            return Lists.transform(codes, new Function<Code, CodeDTO>() {
                @Override
                public CodeDTO apply(Code code) {
                    return convert(code, lang);
                }
            });
        } else {
            return null;
        }
    }

}
