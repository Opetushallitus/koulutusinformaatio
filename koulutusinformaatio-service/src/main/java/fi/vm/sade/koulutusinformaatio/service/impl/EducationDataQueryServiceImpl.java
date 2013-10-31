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
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataQueryServiceImpl implements EducationDataQueryService {

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private DataStatusDAO dataStatusDAO;
    private ModelMapper modelMapper;
    private PictureDAO pictureDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;

    @Autowired
    public EducationDataQueryServiceImpl(ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
                                         ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
                                         ChildLearningOpportunityDAO childLearningOpportunityDAO,
                                         DataStatusDAO dataStatusDAO, PictureDAO pictureDAO, UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO) {
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.upperSecondaryLearningOpportunitySpecificationDAO = upperSecondaryLearningOpportunitySpecificationDAO;
    }

    @Override
    public ParentLOS getParentLearningOpportunity(String oid) throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationEntity entity = parentLearningOpportunitySpecificationDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, ParentLOS.class);
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
    public List<ApplicationOption> getApplicationOptions(List<String> aoIds) throws InvalidParametersException {
        if (aoIds == null || aoIds.isEmpty()) {
            throw new InvalidParametersException("Application option IDs required");
        }
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.find(aoIds);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ApplicationOption getApplicationOption(String aoId) throws ResourceNotFoundException {
        ApplicationOptionEntity ao = applicationOptionDAO.get(aoId);
        if (ao != null) {
            return modelMapper.map(ao, ApplicationOption.class);
        } else {
            throw new ResourceNotFoundException("Application option not found: " + aoId);
        }
    }

    @Override
    public ChildLOS getChildLearningOpportunity(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity childLO = getChildLO(childLoId);
        return modelMapper.map(childLO, ChildLOS.class);
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

    @Override
    public UpperSecondaryLOS getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        UpperSecondaryLearningOpportunitySpecificationEntity entity =
                upperSecondaryLearningOpportunitySpecificationDAO.get(id);
        if (entity != null) {
            return modelMapper.map(entity, UpperSecondaryLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Upper secondary learning opportunity specifiaction not found: %s", id));
        }
    }

    private ChildLearningOpportunitySpecificationEntity getChildLO(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clo = childLearningOpportunityDAO.get(childLoId);
        if (clo == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLoId);
        }
        return clo;
    }
}
