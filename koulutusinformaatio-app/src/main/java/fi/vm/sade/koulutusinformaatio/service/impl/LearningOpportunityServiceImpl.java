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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
@Service
public class LearningOpportunityServiceImpl implements LearningOpportunityService {

    private EducationDataService educationDataService;
    private static final String LANG_FI = "fi";

    @Autowired
    public LearningOpportunityServiceImpl(EducationDataService educationDataService) {
        this.educationDataService = educationDataService;
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException {
        ParentLO parentLO = educationDataService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLO);
        return convert(parentLO, lang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang) throws ResourceNotFoundException {
        ParentLO parentLO = educationDataService.getParentLearningOpportunity(parentId);
        return convert(parentLO, lang);
    }

    @Override
    public ChildLearningOpportunityDTO getChildLearningOpportunity(String parentId, String closId, String cloiId) throws ResourceNotFoundException {
        ChildLO childLO = educationDataService.getChildLearningOpportunity(closId, cloiId);
        String lang = resolveDefaultLanguage(childLO);
        return convert(childLO, lang);
    }

    @Override
    public ChildLearningOpportunityDTO getChildLearningOpportunity(String parentId, String closId, String cloiId, String lang) throws ResourceNotFoundException {
        ChildLO childLO = educationDataService.getChildLearningOpportunity(closId, cloiId);
        return convert(childLO, lang);
    }

    private ParentLearningOpportunitySpecificationDTO convert(final ParentLO parentLO, final String lang) {
        ParentLearningOpportunitySpecificationDTO parent = new ParentLearningOpportunitySpecificationDTO();
        parent.setId(parentLO.getId());
        parent.setName(getTextByLanguage(parentLO.getName(), lang));
        parent.setEducationDegree(getTextByLanguage(parentLO.getEducationDegree(), lang));
        parent.setAvailableTranslationLanguages(getAvailableTranslationLanguages(parentLO.getName()));
        parent.setChildren(convert(parentLO.getChildRefs()));
        parent.setProvider(convert(parentLO.getProvider(), lang));
        parent.setStructureDiagram(getTextByLanguage(parentLO.getStructureDiagram(), lang));
        parent.setAccessToFurtherStudies(getTextByLanguage(parentLO.getAccessToFurtherStudies(), lang));
        parent.setGoals(getTextByLanguage(parentLO.getGoals(), lang));
        parent.setEducationDomain(getTextByLanguage(parentLO.getEducationDomain(), lang));
        parent.setStydyDomain(getTextByLanguage(parentLO.getStydyDomain(), lang));
        parent.setTranslationLanguage(lang);
        parent.setAvailableTranslationLanguages(getAvailableTranslationLanguages(parentLO.getName()));

        if (parentLO.getApplicationOptions() != null) {
            for (ApplicationOption ao : parentLO.getApplicationOptions()) {
                parent.getApplicationOptions().add(convert(ao, lang));
            }
        }
        return parent;
    }

    private ChildLearningOpportunityDTO convert(final ChildLO childLO, final String lang) {
        ChildLearningOpportunityDTO child = new ChildLearningOpportunityDTO();
        child.setLosId(childLO.getLosId());
        child.setLoiId(childLO.getLoiId());
        child.setName(getTextByLanguage(childLO.getName(), lang));
        child.setDegreeTitle(getTextByLanguage(childLO.getDegreeTitle(), lang));
        child.setQualification(getTextByLanguage(childLO.getQualification(), lang));
        child.setAvailableTranslationLanguages(getAvailableTranslationLanguages(childLO.getName()));
        child.setApplicationOption(convert(childLO.getApplicationOption(), lang));
        child.setStartDate(childLO.getStartDate());
        if (childLO.getTeachingLanguages() != null) {
            for (Code code : childLO.getTeachingLanguages()) {
                child.getTeachingLanguages().add(code.getValue());
            }
        }
        child.setRelated(convert(childLO.getRelated()));
        if (childLO.getParent() != null) {
            ParentLOSRefDTO parent = new ParentLOSRefDTO();
            parent.setId(childLO.getParent().getId());
            parent.setName(getTextByLanguage(childLO.getParent().getName(), lang));
            child.setParent(parent);
        }
        child.setFormOfTeaching(getTextsByLanguage(childLO.getFormOfTeaching(), lang));
        child.setWebLinks(childLO.getWebLinks());
        child.setFormOfEducation(getTextsByLanguage(childLO.getFormOfEducation(), lang));
        child.setPrerequisite(getTextByLanguage(childLO.getPrerequisite(), lang));
        child.setTranslationLanguage(lang);
        return child;
    }

    private ApplicationOptionDTO convert(final ApplicationOption applicationOption, final String lang) {
        if (applicationOption != null) {
            ApplicationOptionDTO ao = new ApplicationOptionDTO();
            ao.setId(applicationOption.getId());
            ao.setApplicationSystemId(applicationOption.getApplicationSystemId());
            ao.setName(getTextByLanguage(applicationOption.getName(), lang));
            ao.setAttachmentDeliveryDeadline(applicationOption.getAttachmentDeliveryDeadline());
            ao.setLastYearApplicantCount(applicationOption.getLastYearApplicantCount());
            ao.setLowestAcceptedAverage(applicationOption.getLowestAcceptedAverage());
            ao.setLowestAcceptedScore(applicationOption.getLowestAcceptedScore());
            ao.setStartingQuota(applicationOption.getStartingQuota());
            return ao;
        }
        return null;
    }

    private List<ChildLORefDTO> convert(final List<ChildLORef> refs) {
        List<ChildLORefDTO> childs = new ArrayList<ChildLORefDTO>();
        if (refs != null) {
            for (ChildLORef ref : refs) {
                ChildLORefDTO child = new ChildLORefDTO();
                child.setLosId(ref.getLosId());
                child.setLoiId(ref.getLoiId());
                child.setAsId(ref.getAsId());
                child.setName(ref.getName());
                childs.add(child);
            }
        }
        return childs;
    }

    private LearningOpportunityProviderDTO convert(final Provider provider, final String lang) {
        if (provider != null) {
            LearningOpportunityProviderDTO p = new LearningOpportunityProviderDTO();
            p.setId(provider.getId());
            p.setName(getTextByLanguage(provider.getName(), lang));
            p.setApplicationSystemIds(provider.getApplicationSystemIDs());
        }
        return null;
    }

    private String resolveDefaultLanguage(final ParentLO parentLO) {
        if (parentLO.getName() == null || parentLO.getName().getTranslations() == null || parentLO.getName().getTranslations().containsKey(LANG_FI)) {
            return LANG_FI;
        } else {
            return parentLO.getName().getTranslations().keySet().iterator().next();
        }
    }

    private String resolveDefaultLanguage(final ChildLO childLO) {
        if (childLO.getTeachingLanguages() == null || childLO.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            for (Code code : childLO.getTeachingLanguages()) {
                 if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                     return LANG_FI;
                 }
            }
            return childLO.getTeachingLanguages().get(0).getValue().toLowerCase();
        }
    }

    private List<String> getTextsByLanguage(final List<I18nText> list, final String lang) {
        List<String> texts = new ArrayList<String>();
        if (list != null) {
            for (I18nText text : list) {
                String value = getTextByLanguage(text, lang);
                if (value != null) {
                    texts.add(value);
                }
            }
        }
        return texts;
    }

    private String getTextByLanguage(final I18nText text, final String lang) {
        if (text != null && text.getTranslations() != null && text.getTranslations().containsKey(lang)) {
            return text.getTranslations().get(lang);
        } else {
            return null;
        }
    }

    private Set<String> getAvailableTranslationLanguages(final I18nText text) {
        if (text != null && text.getTranslations() != null) {
            return text.getTranslations().keySet();
        } else {
            return null;
        }
    }
}
