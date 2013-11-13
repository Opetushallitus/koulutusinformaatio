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
import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class ExamToDTO {

    public static ExamDTO convert(Exam exam, String lang) {
        if (exam != null)  {
            ExamDTO dto = new ExamDTO();
            dto.setType(ConverterUtil.getTextByLanguageUseFallbackLang(exam.getType(), lang));
            dto.setDescription(ConverterUtil.getTextByLanguageUseFallbackLang(exam.getDescription(), lang));
            dto.setExamEvents(ExamEventToDTO.convertAll(exam.getExamEvents(), lang));
            dto.setScoreLimit(ScoreLimitToDTO.convert(exam.getScoreLimit()));
            return dto;
        } else {
            return null;
        }
    }

    public static List<ExamDTO> convertAll(final List<Exam> exams, final String lang) {
        if (exams == null) {
            return null;
        }
        else {
            return Lists.transform(exams, new Function<Exam, ExamDTO>() {
            @Override
            public ExamDTO apply(Exam input) {
                return convert(input, lang);
            }
        });
        }
    }
}
