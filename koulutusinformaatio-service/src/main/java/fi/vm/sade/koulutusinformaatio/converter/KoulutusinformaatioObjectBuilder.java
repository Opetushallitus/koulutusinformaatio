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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLORef;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ParentLORef;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;

/**
 * @author Mikko Majapuro
 */
@Component
public class KoulutusinformaatioObjectBuilder {

    private ModelMapper modelMapper;
    private static final String LANG_FI = "fi";

    @Autowired
    public KoulutusinformaatioObjectBuilder(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ChildLORefEntity buildChildLORef(final ChildLearningOpportunityEntity childLO) {
        if (childLO != null) {
            ChildLORefEntity ref = new ChildLORefEntity();
            ref.setChildLOId(childLO.getId());
            ref.setName(childLO.getName());
            ref.setNameByTeachingLang(getTextByEducationLanguage(childLO.getName(), childLO.getTeachingLanguages()));
            ref.setAsIds(childLO.getApplicationSystemIds());
            return ref;
        }
        return null;
    }

    public ChildLO buildChildLO(final ChildLearningOpportunityEntity childLO) {
        if (childLO != null) {
            ChildLO clo = new ChildLO();
            clo.setId(childLO.getId());
            clo.setApplicationOptions(convert(childLO.getApplicationOptions(), ApplicationOption.class));
            clo.setParent(convert(childLO.getParent(), ParentLORef.class));
            clo.setRelated(convert(childLO.getRelated(), ChildLORef.class));
            clo.setName(convert(childLO.getName(), I18nText.class));
            clo.setDegreeTitle(convert(childLO.getDegreeTitle(), I18nText.class));
            clo.setQualification(convert(childLO.getQualification(), I18nText.class));
            clo.setStartDate(childLO.getStartDate());
            clo.setTeachingLanguages(convert(childLO.getTeachingLanguages(), Code.class));
            clo.setWebLinks(childLO.getWebLinks());
            clo.setFormOfEducation(convert(childLO.getFormOfEducation(), I18nText.class));
            clo.setPrerequisite(convert(childLO.getPrerequisite(), I18nText.class));
            clo.setFormOfTeaching(convert(childLO.getFormOfTeaching(), I18nText.class));
            clo.setProfessionalTitles(convert(childLO.getProfessionalTitles(), I18nText.class));
            clo.setWorkingLifePlacement(convert(childLO.getWorkingLifePlacement(), I18nText.class));
            clo.setInternationalization(convert(childLO.getInternationalization(), I18nText.class));
            clo.setCooperation(convert(childLO.getCooperation(), I18nText.class));
            clo.setDegreeGoal(convert(childLO.getDegreeGoal(), I18nText.class));
            return clo;
        }
        return null;
    }

    private String getTextByEducationLanguage(final I18nTextEntity text, List<CodeEntity> languages) {
        if (text != null && text.getTranslations() != null && !text.getTranslations().isEmpty()) {
            if (languages != null && !languages.isEmpty()) {
                for (CodeEntity code : languages) {
                    if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                        return text.getTranslations().get(LANG_FI);
                    }
                }
                String val = text.getTranslations().get(languages.get(0).getValue().toLowerCase());
                if (val != null) {
                    return val;
                }
            }
            return text.getTranslations().values().iterator().next();
        }
        return null;
    }

    private <E> List<E> convert(List<?> objects, Class<E> eClass) {
        if (objects != null) {
            List<E> list = new ArrayList<E>();
            for (Object obj : objects) {
                list.add(convert(obj, eClass));
            }
            return list;
        } else {
            return null;
        }
    }

    private <E> E convert(Object obj, Class<E> eClass) {
        if (obj != null) {
           return modelMapper.map(obj, eClass);
        } else {
            return null;
        }
    }
}
