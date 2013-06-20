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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.ApplicationOptionToSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.converter.ApplicationOptionsToBasketItemDTOs;
import fi.vm.sade.koulutusinformaatio.converter.ChildLOToDTO;
import fi.vm.sade.koulutusinformaatio.converter.ParentLOToDTO;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class LearningOpportunityServiceImpl implements LearningOpportunityService {

    private EducationDataQueryService educationDataQueryService;
    private ModelMapper modelMapper;
    private static final String LANG_FI = "fi";

    @Autowired
    public LearningOpportunityServiceImpl(EducationDataQueryService educationDataQueryService, ModelMapper modelMapper) {
        this.educationDataQueryService = educationDataQueryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException {
        ParentLO parentLO = educationDataQueryService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLO);
        return ParentLOToDTO.convert(parentLO, lang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang) throws ResourceNotFoundException {
        ParentLO parentLO = educationDataQueryService.getParentLearningOpportunity(parentId);
        return ParentLOToDTO.convert(parentLO, lang);
    }

    @Override
    public ChildLearningOpportunityDTO getChildLearningOpportunity(String cloId) throws ResourceNotFoundException {
        ChildLO childLO = educationDataQueryService.getChildLearningOpportunity(cloId);
        String lang = resolveDefaultLanguage(childLO);
        return ChildLOToDTO.convert(childLO, lang);
    }

    @Override
    public ChildLearningOpportunityDTO getChildLearningOpportunity(String cloId, String lang) throws ResourceNotFoundException {
        ChildLO childLO = educationDataQueryService.getChildLearningOpportunity(cloId);
        return ChildLOToDTO.convert(childLO, lang);
    }

    @Override
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, String lopId, String baseEducation) {
        List<ApplicationOption> applicationOptions = educationDataQueryService.findApplicationOptions(asId, lopId, baseEducation);
        return Lists.transform(applicationOptions, new Function<ApplicationOption, ApplicationOptionSearchResultDTO>() {
            @Override
            public ApplicationOptionSearchResultDTO apply(ApplicationOption applicationOption) {
                return ApplicationOptionToSearchResultDTO.convert(applicationOption, LANG_FI);
            }
        });
    }

    @Override
    public List<BasketItemDTO> getBasketItems(List<String> aoId, String lang) {
        List<ApplicationOption> applicationOptions = educationDataQueryService.getApplicationOptions(aoId);
        return ApplicationOptionsToBasketItemDTOs.convert(applicationOptions, lang);
    }

    @Override
    public Date getLastDataUpdated() {
        return educationDataQueryService.getLastUpdated();
    }

    @Override
    public PictureDTO getPicture(String id) throws ResourceNotFoundException {
        Picture pic = educationDataQueryService.getPicture(id);
        return modelMapper.map(pic, PictureDTO.class);
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
}
