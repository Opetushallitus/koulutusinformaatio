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

import fi.vm.sade.koulutusinformaatio.converter.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class LearningOpportunityServiceImpl implements LearningOpportunityService {

    private EducationDataQueryService educationDataQueryService;
    private PreviewService previewService;
    private ModelMapper modelMapper;
    private static final String LANG_FI = "fi";

    @Autowired
    public LearningOpportunityServiceImpl(EducationDataQueryService educationDataQueryService, PreviewService previewService, ModelMapper modelMapper) {
        this.educationDataQueryService = educationDataQueryService;
        this.previewService = previewService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId) throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLOS);
        return ParentLOSToDTO.convert(parentLOS, lang, lang, lang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String uiLang) throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        String lang = resolveDefaultLanguage(parentLOS);
        return ParentLOSToDTO.convert(parentLOS, lang, uiLang, lang);
    }

    @Override
    public ParentLearningOpportunitySpecificationDTO getParentLearningOpportunity(String parentId, String lang, String uiLang) 
            throws ResourceNotFoundException {
        ParentLOS parentLOS = educationDataQueryService.getParentLearningOpportunity(parentId);
        String defaultLang = resolveDefaultLanguage(parentLOS);
        return ParentLOSToDTO.convert(parentLOS, lang, uiLang, defaultLang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId) throws ResourceNotFoundException {
        ChildLOS childLOS = educationDataQueryService.getChildLearningOpportunity(cloId);
        String lang = resolveDefaultLanguage(childLOS.getLois().get(0), LANG_FI);
        return ChildLOSToDTO.convert(childLOS, lang, lang, lang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String uiLang) throws ResourceNotFoundException {
        ChildLOS childLOS = educationDataQueryService.getChildLearningOpportunity(cloId);
        String lang = resolveDefaultLanguage(childLOS.getLois().get(0), uiLang);
        return ChildLOSToDTO.convert(childLOS, lang, uiLang, lang);
    }

    @Override
    public ChildLearningOpportunitySpecificationDTO getChildLearningOpportunity(String cloId, String lang, String uiLang) throws ResourceNotFoundException {
        ChildLOS childLO = educationDataQueryService.getChildLearningOpportunity(cloId);
        String defaultLang = resolveDefaultLanguage(childLO.getLois().get(0), lang);
        return ChildLOSToDTO.convert(childLO, lang, uiLang, defaultLang);
    }

    @Override
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        UpperSecondaryLOS upperSecondaryLOS = educationDataQueryService.getUpperSecondaryLearningOpportunity(id);
        String lang  = resolveDefaultLanguage(upperSecondaryLOS.getLois().get(0), LANG_FI);
        return UpperSecondaryLOSToDTO.convert(upperSecondaryLOS, lang, lang, lang);
    }

    @Override
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException {
        UpperSecondaryLOS upperSecondaryLOS = educationDataQueryService.getUpperSecondaryLearningOpportunity(id);
        String lang  = resolveDefaultLanguage(upperSecondaryLOS.getLois().get(0), uiLang);
        return UpperSecondaryLOSToDTO.convert(upperSecondaryLOS, lang, uiLang, lang);
    }

    @Override
    public UpperSecondaryLearningOpportunitySpecificationDTO getUpperSecondaryLearningOpportunity(String id, String lang, String uiLang) 
            throws ResourceNotFoundException {
        UpperSecondaryLOS upperSecondaryLOS = educationDataQueryService.getUpperSecondaryLearningOpportunity(id);
        String defaultLang  = resolveDefaultLanguage(upperSecondaryLOS.getLois().get(0), lang);
        return UpperSecondaryLOSToDTO.convert(upperSecondaryLOS, lang, uiLang, defaultLang);
    }

    @Override
    public SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        SpecialLOS los = educationDataQueryService.getSpecialLearningOpportunity(id);
        String lang = resolveDefaultLanguage(los.getLois().get(0), LANG_FI);
        return SpecialLOSToDTO.convert(los, lang, lang, lang);
    }

    @Override
    public SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id, String uiLang) throws ResourceNotFoundException {
        SpecialLOS los = educationDataQueryService.getSpecialLearningOpportunity(id);
        String lang = resolveDefaultLanguage(los.getLois().get(0), uiLang);
        return SpecialLOSToDTO.convert(los, lang, uiLang, lang);
    }

    @Override
    public SpecialLearningOpportunitySpecificationDTO getSpecialSecondaryLearningOpportunity(String id, 
            String lang, 
            String uiLang) 
                    throws ResourceNotFoundException {
        SpecialLOS los = educationDataQueryService.getSpecialLearningOpportunity(id);
        String defaultLang = resolveDefaultLanguage(los.getLois().get(0), lang);
        return SpecialLOSToDTO.convert(los, lang, uiLang, defaultLang);
    }

    @Override
    public List<ApplicationOptionSearchResultDTO> searchApplicationOptions(String asId, 
            String lopId, 
            String baseEducation, 
            boolean vocational, 
            boolean nonVocational,
            boolean ongoing,
            final String uiLang) {

        List<ApplicationOption> applicationOptions = educationDataQueryService.findApplicationOptions(asId, lopId, baseEducation,
                vocational, nonVocational);
        
        List<ApplicationOptionSearchResultDTO> res = new ArrayList<ApplicationOptionSearchResultDTO>();
        for (ApplicationOption curAo : applicationOptions) {
            if (!ongoing) {
                res.add(ApplicationOptionToSearchResultDTO.convert(curAo, resolveDefaultLanguage(curAo, uiLang), uiLang));
            } else if (ongoing 
                    && curAo.getApplicationStartDate() != null 
                    && curAo.getApplicationEndDate() != null) {
                if (ConverterUtil.isOngoing(new DateRange(curAo.getApplicationStartDate(),
                        curAo.getApplicationEndDate()))) {
                    res.add(ApplicationOptionToSearchResultDTO.convert(curAo, resolveDefaultLanguage(curAo, uiLang), uiLang));
                }
            } else if (ongoing) {
                if (ConverterUtil.isOngoing(curAo.getApplicationSystem().getApplicationDates())) {
                    res.add(ApplicationOptionToSearchResultDTO.convert(curAo, resolveDefaultLanguage(curAo, uiLang), uiLang));
                }    
            }
        }
        return res;
    }

    @Override
    public ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) throws ResourceNotFoundException {
        ApplicationOption ao = educationDataQueryService.getApplicationOption(aoId);
        String defaultLang = resolveDefaultLanguage(ao, lang);
        return ApplicationOptionToDTO.convert(ao, lang, uiLang, defaultLang);
    }

    @Override
    public List<ApplicationOptionDTO> getApplicationOptions(List<String> aoId, final String lang, final String uiLang) throws InvalidParametersException {
        List<ApplicationOption> applicationOptions = educationDataQueryService.getApplicationOptions(aoId);
        return Lists.transform(applicationOptions, new Function<ApplicationOption, ApplicationOptionDTO>() {
            @Override
            public ApplicationOptionDTO apply(ApplicationOption applicationOption) {
                String defaultLang = resolveDefaultLanguage(applicationOption, lang);
                return ApplicationOptionToDTO.convert(applicationOption, lang, uiLang, defaultLang);
            }
        });
    }

    @Override
    public List<BasketItemDTO> getBasketItems(List<String> aoId, String uiLang) throws InvalidParametersException {
        List<ApplicationOption> applicationOptions = educationDataQueryService.getApplicationOptions(aoId);
        return ApplicationOptionToBasketItemDTO.convert(applicationOptions, uiLang);
    }

    @Override
    public DataStatus getLastDataStatus() {
        return educationDataQueryService.getLatestDataStatus();
    }

    @Override
    public PictureDTO getPicture(String id) throws ResourceNotFoundException {
        Picture pic = educationDataQueryService.getPicture(id);
        return modelMapper.map(pic, PictureDTO.class);
    }

    @Override
    public List<LearningOpportunitySearchResultDTO> findLearningOpportunitiesByProviderId(String providerId, String lang) {
        List<LOS> losses = educationDataQueryService.findLearningOpportunitiesByProviderId(providerId);
        return LOSToSearchResult.convert(losses, lang);
    }

    private String resolveDefaultLanguage(final ParentLOS parentLO) {
        if (parentLO.getTeachingLanguages() == null || parentLO.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            return parentLO.getTeachingLanguages().get(0).getValue().toLowerCase();
        }
    }

    private String resolveDefaultLanguage(final ChildLOI childLOI, String lang) {
        if (childLOI.getTeachingLanguages() == null || childLOI.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            for (Code code : childLOI.getTeachingLanguages()) {
                if (code.getValue().equalsIgnoreCase(lang)) {
                    return lang;
                }
            }
            for (Code code : childLOI.getTeachingLanguages()) {
                if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                    return LANG_FI;
                }
            }
            return childLOI.getTeachingLanguages().get(0).getValue().toLowerCase();
        }
    }

    private String resolveDefaultLanguage(final UpperSecondaryLOI loi, String lang) {
        if (loi.getTeachingLanguages() == null || loi.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            for (Code code : loi.getTeachingLanguages()) {
                if (code.getValue().equalsIgnoreCase(lang)) {
                    return lang;
                }
            }
            for (Code code : loi.getTeachingLanguages()) {
                if (code.getValue().equalsIgnoreCase(LANG_FI)) {
                    return LANG_FI;
                }
            }
            return loi.getTeachingLanguages().get(0).getValue().toLowerCase();
        }
    }

    private String resolveDefaultLanguage(final ApplicationOption ao, String lang) {
        if (ao.getTeachingLanguages() == null || ao.getTeachingLanguages().isEmpty()) {
            return LANG_FI;
        } else {
            for (String l : ao.getTeachingLanguages()) {
                if (l.equalsIgnoreCase(lang)) {
                    return lang;
                }
            }
            for (String l : ao.getTeachingLanguages()) {
                if (l.equalsIgnoreCase(LANG_FI)) {
                    return LANG_FI;
                }
            }
            return ao.getTeachingLanguages().get(0).toLowerCase();
        }
    }

    @Override
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(
            String id) throws ResourceNotFoundException {
        HigherEducationLOS los = educationDataQueryService.getHigherEducationLearningOpportunity(id);
        String lang = (los.getTeachingLanguages() != null && !los.getTeachingLanguages().isEmpty()) 
                ? los.getTeachingLanguages().get(0).getValue().toLowerCase() : LANG_FI;
                return HigherEducationLOSToDTO.convert(los, lang, lang);
    }

    @Override
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(
            String id, String uiLang) throws ResourceNotFoundException {
        HigherEducationLOS los = educationDataQueryService.getHigherEducationLearningOpportunity(id);
        return HigherEducationLOSToDTO.convert(los, uiLang, uiLang);
    }

    @Override
    public HigherEducationLOSDTO getHigherEducationLearningOpportunity(
            String id, String lang, String uiLang)
                    throws ResourceNotFoundException {
        HigherEducationLOS los = educationDataQueryService.getHigherEducationLearningOpportunity(id);
        return HigherEducationLOSToDTO.convert(los, lang, uiLang);
    }
    
    

    @Override
    public HigherEducationLOSDTO previewHigherEdLearningOpportunity(
            String id, String lang, String uiLang)
                    throws ResourceNotFoundException {
        HigherEducationLOS los = this.previewService.previewHigherEducationLearningOpportunity(id);
        HigherEducationLOSDTO dto = null;
        if (lang != null && !lang.isEmpty()) {
            dto = HigherEducationLOSToDTO.convert(los, lang, uiLang);
        } else {
            dto = HigherEducationLOSToDTO.convert(los, uiLang, uiLang); 
        }
        
        if (dto.getStructureImageId() != null && los.getStructureImage() != null &&  los.getStructureImage().getPictureTranslations().get(uiLang) != null) {
            dto.setStructureImage(modelMapper.map(los.getStructureImage().getPictureTranslations().get(uiLang), PictureDTO.class));
        }
        return dto;
    }
    

    @Override
    public AdultUpperSecondaryLOSDTO previewAdultUpperSecondaryLearningOpportunity(
            String oid, String lang, String uiLang)
            throws ResourceNotFoundException {
        AdultUpperSecondaryLOS los = this.previewService.previewAdultUpperSecondaryLearningOpportunity(oid);
        AdultUpperSecondaryLOSDTO dto = null;
        if (lang != null && !lang.isEmpty()) {
            dto = AdultUpperSecondaryLOSToDTO.convert(los, lang, uiLang);
        } else {
            dto = AdultUpperSecondaryLOSToDTO.convert(los, uiLang, uiLang); 
        }
        return dto;
    }
    
    @Override
    public ValmaLOSDTO previewValmaLearningOpportunity(
            String oid, String lang, String uiLang)
            throws ResourceNotFoundException {
        ValmaLOS los = this.previewService.previewValmaLearningOpportunity(oid);
        ValmaLOSDTO dto = null;
        if (lang != null && !lang.isEmpty()) {
            dto = ValmaLOSToDTO.convert(los, lang, uiLang);
        } else {
            dto = ValmaLOSToDTO.convert(los, uiLang, uiLang); 
        }
        return dto;
    }
    
    @Override
    public AdultVocationalParentLOSDTO previewAdultVocationalLearningOpportunity(
            String oid, String lang, String uiLang)
            throws ResourceNotFoundException {
        
        CompetenceBasedQualificationParentLOS los = this.previewService.previewAdultVocationaParentLearningOpportunity(oid);
        
        if (lang != null && !lang.isEmpty()) {
            return AdultVocationalParentLOSToDTO.convert(los, lang, uiLang);
        } else {
            return AdultVocationalParentLOSToDTO.convert(los, uiLang, uiLang);
        }
    }

    @Override
    public DataStatus getLastSuccesfulDataStatus() { 
       return educationDataQueryService.getLatestSuccessDataStatus();
        
    }

    @Override
    public AdultUpperSecondaryLOSDTO getAdultUpperSecondaryLearningOpportunity(
            String id) throws ResourceNotFoundException {
        AdultUpperSecondaryLOS los = educationDataQueryService.getAdultUpperSecondaryLearningOpportunity(id);
        String lang = (los.getTeachingLanguages() != null && !los.getTeachingLanguages().isEmpty()) 
                ? los.getTeachingLanguages().get(0).getValue().toLowerCase() : LANG_FI;
        return AdultUpperSecondaryLOSToDTO.convert(los, lang, lang);
    }
    
    @Override
    public AdultUpperSecondaryLOSDTO getAdultUpperSecondaryLearningOpportunity(
            String id, String uiLang) throws ResourceNotFoundException {
        AdultUpperSecondaryLOS los = educationDataQueryService.getAdultUpperSecondaryLearningOpportunity(id);
        return AdultUpperSecondaryLOSToDTO.convert(los, uiLang, uiLang);
    }

    @Override
    public AdultUpperSecondaryLOSDTO getAdultUpperSecondaryLearningOpportunity(
            String id, String lang, String uiLang)
                    throws ResourceNotFoundException {
        AdultUpperSecondaryLOS los = educationDataQueryService.getAdultUpperSecondaryLearningOpportunity(id);
        return AdultUpperSecondaryLOSToDTO.convert(los, lang, uiLang);
    }

    @Override
    public ValmaLOSDTO getValmaLearningOpportunity(
            String id) throws ResourceNotFoundException {
        ValmaLOS los = educationDataQueryService.getValmaLearningOpportunity(id);
        String lang = (los.getTeachingLanguages() != null && !los.getTeachingLanguages().isEmpty()) 
                ? los.getTeachingLanguages().get(0).getValue().toLowerCase() : LANG_FI;
        return ValmaLOSToDTO.convert(los, lang, lang);
    }
    
    @Override
    public ValmaLOSDTO getValmaLearningOpportunity(
            String id, String uiLang) throws ResourceNotFoundException {
        ValmaLOS los = educationDataQueryService.getValmaLearningOpportunity(id);
        return ValmaLOSToDTO.convert(los, uiLang, uiLang);
    }

    @Override
    public ValmaLOSDTO getValmaLearningOpportunity(
            String id, String lang, String uiLang)
                    throws ResourceNotFoundException {
        ValmaLOS los = educationDataQueryService.getValmaLearningOpportunity(id);
        return ValmaLOSToDTO.convert(los, lang, uiLang);
    }

    
    @Override
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(
            String id) throws ResourceNotFoundException {
        CompetenceBasedQualificationParentLOS los = educationDataQueryService.getAdultVocationalLearningOpportunity(id);
        String lang = (los.getChildren() != null && !los.getChildren().isEmpty() 
                && los.getChildren().get(0).getTeachingLanguages() != null && !los.getChildren().get(0).getTeachingLanguages().isEmpty())  
                ? los.getChildren().get(0).getTeachingLanguages().get(0).getValue().toLowerCase() : LANG_FI;
        return AdultVocationalParentLOSToDTO.convert(los, lang, lang);
    }

    @Override
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(
            String id, String uiLang) throws ResourceNotFoundException {
        CompetenceBasedQualificationParentLOS los = educationDataQueryService.getAdultVocationalLearningOpportunity(id);
        return AdultVocationalParentLOSToDTO.convert(los, uiLang, uiLang);
    }

    @Override
    public AdultVocationalParentLOSDTO getAdultVocationalLearningOpportunity(
            String id, String lang, String uiLang)
            throws ResourceNotFoundException {
        CompetenceBasedQualificationParentLOS los = educationDataQueryService.getAdultVocationalLearningOpportunity(id);
        return AdultVocationalParentLOSToDTO.convert(los, lang, uiLang);
    }

    @Override
    public LearningOpportunityProviderDTO getProvider(String lopId, String lang)
            throws ResourceNotFoundException {
        return ProviderToDTO.convert(this.educationDataQueryService.getProvider(lopId), lang, lang, lang) ;
    }

    @Override
    public PictureDTO getThumbnail(String lopId)
            throws ResourceNotFoundException {
        Picture pic = educationDataQueryService.getPicture(lopId);
        return PictureToThumbnail.convert(pic);
    }


}
