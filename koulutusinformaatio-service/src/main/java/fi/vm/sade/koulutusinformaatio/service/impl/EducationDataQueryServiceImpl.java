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

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataQueryServiceImpl implements EducationDataQueryService {

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO;
    private ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO;
    private DataStatusDAO dataStatusDAO;
    private ModelMapper modelMapper;
    private KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder;
    private PictureDAO pictureDAO;

    @Autowired
    public EducationDataQueryServiceImpl(ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
                                         ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
                                         ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO,
                                         ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO,
                                         KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder,
                                         DataStatusDAO dataStatusDAO, PictureDAO pictureDAO) {
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunitySpecificationDAO = childLearningOpportunitySpecificationDAO;
        this.childLearningOpportunityInstanceDAO = childLearningOpportunityInstanceDAO;
        this.koulutusinformaatioObjectBuilder = koulutusinformaatioObjectBuilder;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
    }

    @Override
    public ParentLO getParentLearningOpportunity(String oid) throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationEntity entity = parentLearningOpportunitySpecificationDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, ParentLO.class);
        } else {
            throw new ResourceNotFoundException("Parent learning opportunity not found: " + oid);
        }
    }

    @Override
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId, String baseEducation) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.find(asId, lopId, baseEducation);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public List<ApplicationOption> getApplicationOptions(List<String> aoIds) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.find(aoIds);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ChildLO getChildLearningOpportunity(String childLosId, String childLoiId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity childLOS = getChildLOS(childLosId);
        ChildLearningOpportunityEntity childLOI = getChildLOI(childLoiId);
        return koulutusinformaatioObjectBuilder.buildChildLO(childLOS, childLOI);
    }

    @Override
    public Date getLastUpdated() {
        DataStatusEntity status = dataStatusDAO.getLatest();
        if (status != null) {
            return status.getLastUpdated();
        } else {
            return null;
        }
    }

    @Override
    public Picture getPicture(String id) throws ResourceNotFoundException {
        PictureEntity picture = pictureDAO.get(id);
        if (picture != null) {
            return modelMapper.map(picture, Picture.class);
        } else {
            throw new ResourceNotFoundException("Picture not found: " + id);
        }
    }

    private ChildLearningOpportunitySpecificationEntity getChildLOS(String childLosId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clos = childLearningOpportunitySpecificationDAO.get(childLosId);
        if (clos == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLosId);
        }
        return clos;
    }

    private ChildLearningOpportunityEntity getChildLOI(String childLoiId) throws ResourceNotFoundException {
        ChildLearningOpportunityEntity cloi = childLearningOpportunityInstanceDAO.get(childLoiId);
        if (cloi == null) {
            throw new ResourceNotFoundException("Child learning opportunity instance not found: " + childLoiId);
        }
        return cloi;
    }
}
