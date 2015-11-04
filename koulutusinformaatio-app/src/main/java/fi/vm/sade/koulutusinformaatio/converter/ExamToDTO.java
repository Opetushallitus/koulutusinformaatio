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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Exam;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class ExamToDTO {

    private ExamToDTO() {
    }

    public static ExamDTO convert(Exam exam, String lang) {
        if (exam != null && I18nText.hasTranslationForLanguage(exam.getDescription(), lang))  {
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
    
    public static List<ExamDTO> convertAllHigherEducation(final List<Exam> exams, final String lang) {
        if (exams == null || exams.isEmpty()) {
            return null;
        }
        else {
            
            List<ExamDTO> convertedExams = convertHigherEdExamsByLang(lang, exams);
            
            if (convertedExams == null || convertedExams.isEmpty()) {
                convertedExams = convertHigherEdExamsByLang(ConverterUtil.FALLBACK_LANG, exams);
            }
            if (convertedExams == null || convertedExams.isEmpty()) {
                convertedExams = convertHigherEdExamsByLang(exams.get(0).getType().getTranslations().keySet().iterator().next(), exams);
            }
            
            return convertedExams != null && !convertedExams.isEmpty() ? convertedExams : null;

        }
    }
    
    private static List<ExamDTO> convertHigherEdExamsByLang(String lang, final List<Exam> exams) {
        List<ExamDTO> convertedExams = new ArrayList<ExamDTO>();
        for (Exam curExam : exams) {
            ExamDTO exam = null;
            if (curExam != null) {
                exam = convert(curExam, lang);
            }
            if (exam != null) {
                convertedExams.add(exam);
            }
        }
        
        return !convertedExams.isEmpty() ? convertedExams : null;
    }
}
