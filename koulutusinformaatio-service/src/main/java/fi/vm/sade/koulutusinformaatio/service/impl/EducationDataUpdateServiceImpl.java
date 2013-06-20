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

import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataUpdateServiceImpl implements EducationDataUpdateService {

    private ModelMapper modelMapper;
    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private ChildLearningOpportunityDAO childLOTransactionDAO;
    private KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder;
    private PictureDAO pictureTransactionDAO;

    @Autowired
    public EducationDataUpdateServiceImpl(ModelMapper modelMapper, ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
                                          ApplicationOptionDAO applicationOptionTransactionDAO,
                                          LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
                                          ChildLearningOpportunityDAO childLOTransactionDAO,
                                          KoulutusinformaatioObjectBuilder koulutusinformaatioObjectBuilder,
                                          PictureDAO pictureTransactionDAO) {
        this.modelMapper = modelMapper;
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.childLOTransactionDAO = childLOTransactionDAO;
        this.koulutusinformaatioObjectBuilder = koulutusinformaatioObjectBuilder;
        this.pictureTransactionDAO = pictureTransactionDAO;
    }

    @Override
    public void save(final ParentLOS parentLOS) {
        if (parentLOS != null) {
            ParentLearningOpportunitySpecificationEntity plos =
                    modelMapper.map(parentLOS, ParentLearningOpportunitySpecificationEntity.class);
            save(plos.getProvider());

            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    save(ao);
                }
            }

            if (plos.getLois() != null) {
                ParentLOSRefEntity parentRef = modelMapper.map(plos, ParentLOSRefEntity.class);
                for (ParentLearningOpportunityInstanceEntity ploi : plos.getLois()) {
                    if (ploi.getChildren() != null) {
                        for (ChildLearningOpportunityEntity cLO : ploi.getChildren()) {
                            cLO.setParent(parentRef);
                            ChildLORefEntity childRef = save(cLO, ploi);
                            ploi.getChildRefs().add(childRef);
                        }
                    }
                }
            }

            parentLOSTransactionDAO.save(plos);
        }
    }

    private ChildLORefEntity save(final ChildLearningOpportunityEntity childLearningOpportunity,
                                        final ParentLearningOpportunityInstanceEntity parentLOI) {
        if (childLearningOpportunity != null && parentLOI != null) {
            childLearningOpportunity.setRelated(new ArrayList<ChildLORefEntity>());
            for (ChildLearningOpportunityEntity childLO : parentLOI.getChildren()) {
                if (!childLearningOpportunity.getId().equals(childLO.getId())) {
                    ChildLORefEntity cRef = koulutusinformaatioObjectBuilder.buildChildLORef(childLO);
                    if (cRef != null) {
                        childLearningOpportunity.getRelated().add(cRef);
                    }
                }
            }
            if (childLearningOpportunity.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : childLearningOpportunity.getApplicationOptions()) {
                    save(ao);
                }
            }
            childLOTransactionDAO.save(childLearningOpportunity);
            return koulutusinformaatioObjectBuilder.buildChildLORef(childLearningOpportunity);
        }
        return null;
    }

    private void save(final LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            save(learningOpportunityProvider.getPicture());
            learningOpportunityProviderTransactionDAO.save(learningOpportunityProvider);
        }
    }

    private void save(final ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionTransactionDAO.save(applicationOption);
        }
    }

    private void save(final PictureEntity picture) {
        if (picture != null) {
            pictureTransactionDAO.save(picture);
        }
    }
}
