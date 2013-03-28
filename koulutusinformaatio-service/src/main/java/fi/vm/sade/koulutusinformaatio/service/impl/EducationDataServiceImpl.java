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

import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataServiceImpl implements EducationDataService {

    private ParentLearningOpportunityDAO parentLearningOpportunityDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ModelMapper modelMapper;

    @Autowired
    public EducationDataServiceImpl(ParentLearningOpportunityDAO parentLearningOpportunityDAO,
                                    ApplicationOptionDAO applicationOptionDAO, LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            ModelMapper modelMapper) {
        this.parentLearningOpportunityDAO = parentLearningOpportunityDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.modelMapper = modelMapper;
    }

    @Override
    public void save(LearningOpportunityData learningOpportunityData) {
        if (learningOpportunityData != null) {
            //drop current data
            applicationOptionDAO.getCollection().drop();
            parentLearningOpportunityDAO.getCollection().drop();
            learningOpportunityProviderDAO.getCollection().drop();

            for (ApplicationOption applicationOption: learningOpportunityData.getApplicationOptions()) {
                ApplicationOptionEntity applicationOptionEntity = modelMapper.map(applicationOption, ApplicationOptionEntity.class);
                applicationOptionDAO.save(applicationOptionEntity);
            }
            for (ParentLearningOpportunity parentLearningOpportunity :learningOpportunityData.getParentLearningOpportinities()) {
                ParentLearningOpportunityEntity parentLearningOpportunityEntity =
                        modelMapper.map(parentLearningOpportunity, ParentLearningOpportunityEntity.class);
                learningOpportunityProviderDAO.save(parentLearningOpportunityEntity.getProvider());
                parentLearningOpportunityDAO.save(parentLearningOpportunityEntity);
            }
        }
    }

    @Override
    public ParentLearningOpportunity getParentLearningOpportunity(String oid) {
        ParentLearningOpportunityEntity entity = parentLearningOpportunityDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, ParentLearningOpportunity.class);
        } else {
            //TODO should throw exception?
            return null;
        }
    }
}
