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

import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.domain.ChildLORef;
import fi.vm.sade.koulutusinformaatio.domain.ParentLORef;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public ChildLORefEntity buildChildLORef(final ChildLearningOpportunitySpecificationEntity childLOS, final ChildLearningOpportunityInstanceEntity childLOI) {
        if (childLOI != null && childLOS != null) {
            ChildLORefEntity ref = new ChildLORefEntity();
            ref.setLosId(childLOS.getId());
            ref.setName(childLOS.getName());
            ref.setNameByTeachingLang(getTextByEducationLanguage(childLOS.getName(), childLOI.getTeachingLanguages()));
            ref.setLoiId(childLOI.getId());
            ref.setAsId(childLOI.getApplicationSystemId());
            return ref;
        }
        return null;
    }


    public ChildLO buildChildLO(final ChildLearningOpportunitySpecificationEntity childLOS,
                                  final ChildLearningOpportunityInstanceEntity childLOI) {
        if (childLOI != null && childLOS != null) {
            ChildLO clo = new ChildLO();
            clo.setLoiId(childLOI.getId());
            clo.setLosId(childLOS.getId());
            clo.setApplicationOption(convert(childLOI.getApplicationOption(), ApplicationOption.class));
            clo.setParent(convert(childLOS.getParent(), ParentLORef.class));
            clo.setRelated(convert(childLOI.getRelated(), ChildLORef.class));
            clo.setName(convert(childLOS.getName(), I18nText.class));
            clo.setDegreeTitle(convert(childLOS.getDegreeTitle(), I18nText.class));
            clo.setQualification(convert(childLOS.getQualification(), I18nText.class));
            clo.setStartDate(childLOI.getStartDate());
            clo.setTeachingLanguages(convert(childLOI.getTeachingLanguages(), Code.class));
            clo.setWebLinks(childLOI.getWebLinks());
            clo.setFormOfEducation(convert(childLOI.getFormOfEducation(), I18nText.class));
            clo.setPrerequisite(convert(childLOI.getPrerequisite(), I18nText.class));
            clo.setFormOfTeaching(convert(childLOI.getFormOfTeaching(), I18nText.class));
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
