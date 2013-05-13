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
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
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

import java.util.ArrayList;
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
    private KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder;

    @Autowired
    public EducationDataServiceImpl(ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
            ApplicationOptionDAO applicationOptionDAO, LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            ModelMapper modelMapper, ChildLearningOpportunitySpecificationDAO childLearningOpportunitySpecificationDAO,
            ChildLearningOpportunityInstanceDAO childLearningOpportunityInstanceDAO,
            KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder) {
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.modelMapper = modelMapper;
        this.childLearningOpportunitySpecificationDAO = childLearningOpportunitySpecificationDAO;
        this.childLearningOpportunityInstanceDAO = childLearningOpportunityInstanceDAO;
        this.koulutusinformaatioObjectBuilder = koulutusinformaatioObjectBuilder;
    }

    @Override
    public void save(final ParentLOS parentLOS) {
        if (parentLOS != null) {
            ParentLearningOpportunitySpecificationEntity plo =
                    modelMapper.map(parentLOS, ParentLearningOpportunitySpecificationEntity.class);
            plo.setChildRefs(new ArrayList<ChildLORefEntity>());
            Map<String, ApplicationOptionEntity> aos = new HashMap<String, ApplicationOptionEntity>();
            save(plo.getProvider());

            if (plo.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plo.getApplicationOptions()) {
                    aos.put(ao.getId(), ao);
                }
            }
            if (plo.getChildren() != null) {
                for (ChildLearningOpportunitySpecificationEntity clo : plo.getChildren()) {
                    clo.setParent(modelMapper.map(plo, ParentLOSRefEntity.class));
                    List<ChildLORefEntity> childRefs = save(clo);
                    if (childRefs != null && !childRefs.isEmpty()) {
                        plo.getChildRefs().addAll(childRefs);
                    }
                }
            }
            for (ApplicationOptionEntity ao : aos.values()) {
                save(ao);
            }
            parentLearningOpportunitySpecificationDAO.save(plo);
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
    public ChildLO getChildLearningOpportunity(String childLosId, String childLoiId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity childLOS = getChildLOS(childLosId);
        ChildLearningOpportunityInstanceEntity childLOI = getChildLOI(childLoiId);
        return koulutusinformaatioObjectBuilder.buildChildLO(childLOS, childLOI);
    }

    private ChildLearningOpportunitySpecificationEntity getChildLOS(String childLosId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clos = childLearningOpportunitySpecificationDAO.get(childLosId);
        if (clos == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLosId);
        }
        return clos;
    }

    private ChildLearningOpportunityInstanceEntity getChildLOI(String childLoiId) throws ResourceNotFoundException {
        ChildLearningOpportunityInstanceEntity cloi = childLearningOpportunityInstanceDAO.get(childLoiId);
        if (cloi == null) {
            throw new ResourceNotFoundException("Child learning opportunity instance not found: " + childLoiId);
        }
        return cloi;
    }

    private List<ChildLORefEntity> save(final ChildLearningOpportunitySpecificationEntity childLearningOpportunitySpecification) {
        List<ChildLORefEntity> childLORefs = new ArrayList<ChildLORefEntity>();
        if (childLearningOpportunitySpecification != null) {
            if (childLearningOpportunitySpecification.getChildLOIs() != null) {
               for (ChildLearningOpportunityInstanceEntity childLOI : childLearningOpportunitySpecification.getChildLOIs()) {
                   ChildLORefEntity childLORef = save(childLOI, childLearningOpportunitySpecification);
                   if (childLORef != null) {
                        childLORefs.add(childLORef);
                   }
               }
            }

            childLearningOpportunitySpecificationDAO.save(childLearningOpportunitySpecification);
        }
        return childLORefs;
    }

    private ChildLORefEntity save(final ChildLearningOpportunityInstanceEntity childLearningOpportunityInstance,
                                  final ChildLearningOpportunitySpecificationEntity childLearningOpportunitySpecification) {
        if (childLearningOpportunityInstance != null && childLearningOpportunitySpecification != null) {
            childLearningOpportunityInstance.setRelated(new ArrayList<ChildLORefEntity>());
            for (ChildLearningOpportunityInstanceEntity clo :childLearningOpportunitySpecification.getChildLOIs()) {
                if (!clo.getId().equals(childLearningOpportunityInstance.getId()) &&
                        Objects.equal(clo.getApplicationSystemId(), childLearningOpportunityInstance.getApplicationSystemId())) {
                    ChildLORefEntity cRef = koulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunitySpecification, clo);
                    if (cRef != null) {
                        childLearningOpportunityInstance.getRelated().add(cRef);
                    }
                }
            }

            if (childLearningOpportunityInstance.getApplicationOption() != null) {
                save(childLearningOpportunityInstance.getApplicationOption());
            }
            childLearningOpportunityInstanceDAO.save(childLearningOpportunityInstance);
            return koulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunitySpecification, childLearningOpportunityInstance);
        }
        return null;
    }

    private void save(final LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            learningOpportunityProviderDAO.save(learningOpportunityProvider);
        }
    }

    private void save(final ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionDAO.save(applicationOption);
        }
    }


}
