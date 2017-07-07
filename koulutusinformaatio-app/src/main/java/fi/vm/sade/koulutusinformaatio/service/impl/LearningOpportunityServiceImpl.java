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

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.exception.ApplicatioOptionNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.converter.AdultVocationalParentLOSToDTO;
import fi.vm.sade.koulutusinformaatio.converter.ApplicationOptionToBasketItemDTO;
import fi.vm.sade.koulutusinformaatio.converter.ApplicationOptionToDTO;
import fi.vm.sade.koulutusinformaatio.converter.ApplicationOptionToSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.converter.ConverterUtil;
import fi.vm.sade.koulutusinformaatio.converter.HigherEducationLOSToDTO;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusLOSToDTO;
import fi.vm.sade.koulutusinformaatio.converter.LOSToSearchResult;
import fi.vm.sade.koulutusinformaatio.converter.PictureToThumbnail;
import fi.vm.sade.koulutusinformaatio.converter.ProviderToDTO;
import fi.vm.sade.koulutusinformaatio.converter.TutkintoLOSToDTO;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.AdultVocationalParentLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.KoulutusLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.StandaloneLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.TutkintoLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.LearningOpportunityService;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;

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
    public TutkintoLOSDTO getTutkintoLearningOpportunity(String id, String lang, String uiLang, String prerequisite)
            throws ResourceNotFoundException {
        TutkintoLOS tutkintoLOS = educationDataQueryService.getTutkintoLearningOpportunity(id);
        if (lang == null) {
            lang = uiLang;
        }
        return TutkintoLOSToDTO.convert(tutkintoLOS, lang, uiLang, LANG_FI, prerequisite);
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
    public ApplicationOptionDTO getApplicationOption(String aoId, String lang, String uiLang) throws ApplicatioOptionNotFoundException {
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
    public StandaloneLOSDTO previewKoulutusLearningOpportunity(
            String oid, String lang, String uiLang)
            throws ResourceNotFoundException {
        KoulutusLOS los = this.previewService.previewKoulutusLearningOpportunity(oid);
        StandaloneLOSDTO dto = null;
        if (lang != null && !lang.isEmpty()) {
            dto = KoulutusLOSToDTO.convert(los, lang, uiLang);
        } else {
            dto = KoulutusLOSToDTO.convert(los, uiLang, uiLang);
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
    public KoulutusLOSDTO getKoulutusLearningOpportunity(
            String id) throws ResourceNotFoundException {
        KoulutusLOS los = educationDataQueryService.getKoulutusLearningOpportunity(id);
        String lang = (los.getTeachingLanguages() != null && !los.getTeachingLanguages().isEmpty())
                ? los.getTeachingLanguages().get(0).getValue().toLowerCase() : LANG_FI;
        return KoulutusLOSToDTO.convert(los, lang, lang);
    }

    @Override
    public KoulutusLOSDTO getKoulutusLearningOpportunity(
            String id, String uiLang) throws ResourceNotFoundException {
        KoulutusLOS los = educationDataQueryService.getKoulutusLearningOpportunity(id);
        return KoulutusLOSToDTO.convert(los, uiLang, uiLang);
    }

    @Override
    public KoulutusLOSDTO getKoulutusLearningOpportunity(
            String id, String lang, String uiLang)
                    throws ResourceNotFoundException {
        KoulutusLOS los = educationDataQueryService.getKoulutusLearningOpportunity(id);
        return KoulutusLOSToDTO.convert(los, lang, uiLang);
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
