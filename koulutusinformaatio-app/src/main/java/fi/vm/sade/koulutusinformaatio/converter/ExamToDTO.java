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
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ExamDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public final class ExamToDTO {

    private static ExamDTO convert(final Exam exam, final String lang) {
        if (exam != null && I18nText.hasTranslationForLanguage(exam.getDescription(), lang)) {
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

    private static List<ExamDTO> convertAllForLang(final List<Exam> exams, final String lang) {
        List<ExamDTO> converted = new ArrayList<>(Lists.transform(exams, new Function<Exam, ExamDTO>() {
            @Override
            public ExamDTO apply(Exam input) {
                return convert(input, lang);
            }
        }));
        converted.removeAll(Collections.singleton((ExamDTO) null));
        return CollectionUtils.isEmpty(converted) ? null : converted;
    }

    // Jos koe on vain suomeksi tai ruotsiksi, näytetään koe kummankin kielisessä kälissä.
    // Jos koe on englanniksi, sitä ei näytetä suomen- tai ruotsinkielilissä käleissä.
    // Jos englanniksi ei löydy näytettäviä kokeita, näytetään suomenkieliset (BUG-1928)
    public static List<ExamDTO> convertAll(final List<Exam> exams, final String lang) {
        if (CollectionUtils.isEmpty(exams)) {
            return null;
        } else {
            List<ExamDTO> convertedExams = convertAllForLang(exams, lang);

            if (CollectionUtils.isEmpty(convertedExams) && "sv".equals(lang)) {
                convertedExams = convertAllForLang(exams, "fi");
            } else if(CollectionUtils.isEmpty(convertedExams) && "fi".equals(lang)){
                convertedExams = convertAllForLang(exams, "sv");
            } else if (CollectionUtils.isEmpty(convertedExams) && "en".equals(lang)) {
                convertedExams = convertAllForLang(exams, "fi");
            }

            return convertedExams;
        }
    }
}
