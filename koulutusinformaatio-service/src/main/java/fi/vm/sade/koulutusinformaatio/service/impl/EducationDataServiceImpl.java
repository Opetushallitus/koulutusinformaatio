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
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLO;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataServiceImpl implements EducationDataService {

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO;
    private ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO;
    private ModelMapper modelMapper;

    @Autowired
    public EducationDataServiceImpl(ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
            ApplicationOptionDAO applicationOptionDAO, LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            ModelMapper modelMapper, ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO,
            ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO) {
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunitySpecificationDAO = childLearningOpportunitySpecificationDAO;
        this.childLearningOpportunityInstanceDAO = childLearningOpportunityInstanceDAO;
    }

    @Override
    public void save(final ParentLOS parentLOS) {
        if (parentLOS != null) {
            ParentLearningOpportunitySpecificationEntity plo =
                    modelMapper.map(parentLOS, ParentLearningOpportunitySpecificationEntity.class);
            Map<String, ApplicationOptionEntity> aos = new HashMap<String, ApplicationOptionEntity>();
            save(plo.getProvider());

            if (plo.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plo.getApplicationOptions()) {
                    aos.put(ao.getId(), ao);
                }
            }
            if (plo.getChildren() != null) {
                for (ChildLearningOpportunitySpecificationEntity clo : plo.getChildren()) {
                    save(clo);
                }
            }
            for (ApplicationOptionEntity ao : aos.values()) {
                save(ao);
            }
            parentLearningOpportunitySpecificationDAO.save(plo);
        }
    }

    private void save(ChildLearningOpportunitySpecificationEntity childLearningOpportunitySpecification) {
        if (childLearningOpportunitySpecification != null) {
            if (childLearningOpportunitySpecification.getChildLOIs() != null) {
               for (ChildLearningOpportunityInstanceEntity childLOI : childLearningOpportunitySpecification.getChildLOIs()) {
                   save(childLOI);
               }
            }
            childLearningOpportunitySpecificationDAO.save(childLearningOpportunitySpecification);
        }
    }

    private void save(ChildLearningOpportunityInstanceEntity childLearningOpportunityInstance) {
        if (childLearningOpportunityInstance != null) {
            if (childLearningOpportunityInstance.getApplicationOption() != null) {
                save(childLearningOpportunityInstance.getApplicationOption());
            }
            childLearningOpportunityInstanceDAO.save(childLearningOpportunityInstance);
        }
    }

    private void save(LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            learningOpportunityProviderDAO.save(learningOpportunityProvider);
        }
    }

    private void save(ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionDAO.save(applicationOption);
        }
    }

    @Override
    public void dropAllData() {
        //drop current data
        applicationOptionDAO.getCollection().drop();
        parentLearningOpportunitySpecificationDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        childLearningOpportunitySpecificationDAO.getCollection().drop();
        childLearningOpportunityInstanceDAO.getCollection().drop();
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
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.find(asId, lopId);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ChildLO getChildLearningOpportunity(String childLosId, String childLoiId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
