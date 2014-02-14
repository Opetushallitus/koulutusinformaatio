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
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @Autowired
    public EducationDataQueryServiceImpl(ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
                                         ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
                                         ChildLearningOpportunityDAO childLearningOpportunityDAO,
                                         DataStatusDAO dataStatusDAO, PictureDAO pictureDAO,
                                         UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO,
                                         SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO, 
                                         HigherEducationLOSDAO higherEducationLOSDAO, 
                                         LearningOpportunityProviderDAO learningOpportunityProviderDAO) {
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.upperSecondaryLearningOpportunitySpecificationDAO = upperSecondaryLearningOpportunitySpecificationDAO;
        this.specialLearningOpportunitySpecificationDAO = specialLearningOpportunitySpecificationDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
    }

    @Override
    public ParentLOS getParentLearningOpportunity(String oid) throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationEntity entity = parentLearningOpportunitySpecificationDAO.getFromSecondary(oid);
        if (entity != null) {
            return modelMapper.map(entity, ParentLOS.class);
        } else {
            throw new ResourceNotFoundException("Parent learning opportunity not found: " + oid);
        }
    }

    @Override
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId, String baseEducation,
                                                          boolean vocational, boolean nonVocational) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(asId, lopId, baseEducation,
                vocational, nonVocational);
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
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(aoIds);
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
    public DataStatus getLatestDataStatus() {
        DataStatusEntity status = dataStatusDAO.getLatest();
        if (status != null) {
            return modelMapper.map(status, DataStatus.class);
        } else {
            return null;
        }
    }

    @Override
    public Picture getPicture(String id) throws ResourceNotFoundException {
        PictureEntity picture = pictureDAO.getFromSecondary(id);
        if (picture != null) {
            return modelMapper.map(picture, Picture.class);
        } else {
            throw new ResourceNotFoundException("Picture not found: " + id);
        }
    }

    @Override
    public UpperSecondaryLOS getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        UpperSecondaryLearningOpportunitySpecificationEntity entity =
                upperSecondaryLearningOpportunitySpecificationDAO.getFromSecondary(id);
        if (entity != null) {
            return modelMapper.map(entity, UpperSecondaryLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Upper secondary learning opportunity specification not found: %s", id));
        }
    }

    @Override
    public SpecialLOS getSpecialLearningOpportunity(String id) throws ResourceNotFoundException {
        SpecialLearningOpportunitySpecificationEntity entity =
                specialLearningOpportunitySpecificationDAO.getFromSecondary(id);
        if (entity != null) {
            return modelMapper.map(entity, SpecialLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Special learning opportunity specification not found: %s", id));
        }
    }
    
	@Override
	public HigherEducationLOS getHigherEducationLearningOpportunity(String oid) throws ResourceNotFoundException {
		HigherEducationLOSEntity entity = this.higherEducationLOSDAO.get(oid);
		if (entity != null) {
			return modelMapper.map(entity, HigherEducationLOS.class);
		} else {
			throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
		}
	}

    @Override
    public Provider getProvider(String id) throws ResourceNotFoundException {
        LearningOpportunityProviderEntity entity = learningOpportunityProviderDAO.get(id);
        if (entity != null) {
            return modelMapper.map(entity, Provider.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Learning opportunity provider not found: %s", id));
        }
    }

    @Override
    public List<LOS> findLearningOpportunitiesByProviderId(String providerId) {
        List<LOS> results = Lists.newArrayList();
        List<ParentLearningOpportunitySpecificationEntity> parentEntites =
                parentLearningOpportunitySpecificationDAO.findByProviderId(providerId);
        List<ParentLOS> parents = Lists.transform(
                parentEntites,
                new Function<ParentLearningOpportunitySpecificationEntity, ParentLOS>() {
                    @Override
                    public ParentLOS apply(ParentLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, ParentLOS.class);
                    }
                }
        );
        List<ChildLOS> children = Lists.newArrayList();
        for (ParentLearningOpportunitySpecificationEntity parentEntity : parentEntites) {
            children.addAll(Lists.transform(
                    parentEntity.getChildren(),
                    new Function<ChildLearningOpportunitySpecificationEntity, ChildLOS>() {
                        @Override
                        public ChildLOS apply(ChildLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, ChildLOS.class);
                        }
                    }
            ));
        }
        List<UpperSecondaryLOS> upsecs = Lists.transform(
                upperSecondaryLearningOpportunitySpecificationDAO.findByProviderId(providerId),
                new Function<UpperSecondaryLearningOpportunitySpecificationEntity, UpperSecondaryLOS>() {
                    @Override
                    public UpperSecondaryLOS apply(UpperSecondaryLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, UpperSecondaryLOS.class);
                    }
                }
        );
        List<SpecialLOS> specials = Lists.transform(
                specialLearningOpportunitySpecificationDAO.findByProviderId(providerId),
                new Function<SpecialLearningOpportunitySpecificationEntity, SpecialLOS>() {
                    @Override
                    public SpecialLOS apply(SpecialLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, SpecialLOS.class);
                    }
                }
        );
        results.addAll(parents);
        results.addAll(children);
        results.addAll(upsecs);
        results.addAll(specials);
        return results;
    }

    private ChildLearningOpportunitySpecificationEntity getChildLO(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clo = childLearningOpportunityDAO.getFromSecondary(childLoId);
        if (clo == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLoId);
        }
        return clo;
    }


}
