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

import com.google.common.base.Objects;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
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
public class EducationDataUpdateServiceImpl implements EducationDataUpdateService {

    private ModelMapper modelMapper;
    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private ChildLearningOpportunitySpecificationDAO childLOSTransactionDAO;
    private ChildLearningOpportunityInstanceDAO childLOITransactionDAO;
    private KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder;

    @Autowired
    public EducationDataUpdateServiceImpl(ModelMapper modelMapper, ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
                                          ApplicationOptionDAO applicationOptionTransactionDAO,
                                          LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
                                          ChildLearningOpportunitySpecificationDAO childLOSTransactionDAO,
                                          ChildLearningOpportunityInstanceDAO childLOITransactionDAO,
                                          KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder) {
        this.modelMapper = modelMapper;
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.childLOSTransactionDAO = childLOSTransactionDAO;
        this.childLOITransactionDAO = childLOITransactionDAO;
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
                ParentLOSRefEntity parentRef = modelMapper.map(plo, ParentLOSRefEntity.class);
                for (ChildLearningOpportunitySpecificationEntity clo : plo.getChildren()) {
                    clo.setParent(parentRef);
                    List<ChildLORefEntity> childRefs = save(clo, plo);
                    if (childRefs != null && !childRefs.isEmpty()) {
                        plo.getChildRefs().addAll(childRefs);
                    }
                }
            }
            for (ApplicationOptionEntity ao : aos.values()) {
                save(ao);
            }
            parentLOSTransactionDAO.save(plo);
        }
    }

    private List<ChildLORefEntity> save(final ChildLearningOpportunitySpecificationEntity childLearningOpportunitySpecification,
                                        final ParentLearningOpportunitySpecificationEntity parentLOS) {
        List<ChildLORefEntity> childLORefs = new ArrayList<ChildLORefEntity>();
        if (childLearningOpportunitySpecification != null) {
            if (childLearningOpportunitySpecification.getChildLOIs() != null) {
                for (ChildLearningOpportunityInstanceEntity childLOI : childLearningOpportunitySpecification.getChildLOIs()) {
                    ChildLORefEntity childLORef = save(childLOI, childLearningOpportunitySpecification, parentLOS);
                    if (childLORef != null) {
                        childLORefs.add(childLORef);
                    }
                }
            }

            childLOSTransactionDAO.save(childLearningOpportunitySpecification);
        }
        return childLORefs;
    }

    private ChildLORefEntity save(final ChildLearningOpportunityInstanceEntity childLearningOpportunityInstance,
                                  final ChildLearningOpportunitySpecificationEntity childLearningOpportunitySpecification,
                                  final ParentLearningOpportunitySpecificationEntity parentLOS) {
        if (childLearningOpportunityInstance != null && parentLOS != null) {
            childLearningOpportunityInstance.setRelated(new ArrayList<ChildLORefEntity>());
            for (ChildLearningOpportunitySpecificationEntity childLOS : parentLOS.getChildren()) {
                for (ChildLearningOpportunityInstanceEntity clo : childLOS.getChildLOIs()) {
                    if (!clo.getId().equals(childLearningOpportunityInstance.getId()) &&
                            Objects.equal(clo.getApplicationSystemId(), childLearningOpportunityInstance.getApplicationSystemId())) {
                        ChildLORefEntity cRef = koulutusinformaatioObjectBuilder.buildChildLORef(childLOS, clo);
                        if (cRef != null) {
                            childLearningOpportunityInstance.getRelated().add(cRef);
                        }
                    }
                }
            }

            if (childLearningOpportunityInstance.getApplicationOption() != null) {
                save(childLearningOpportunityInstance.getApplicationOption());
            }
            childLOITransactionDAO.save(childLearningOpportunityInstance);
            return koulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunitySpecification, childLearningOpportunityInstance);
        }
        return null;
    }

    private void save(final LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            learningOpportunityProviderTransactionDAO.save(learningOpportunityProvider);
        }
    }

    private void save(final ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionTransactionDAO.save(applicationOption);
        }
    }
}
