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
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityProvider;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
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
    public void save(final ParentLearningOpportunity parentLearningOpportunity) {
        if (parentLearningOpportunity != null) {
            ParentLearningOpportunityEntity plo =
                    modelMapper.map(parentLearningOpportunity, ParentLearningOpportunityEntity.class);
            Map<String, ApplicationOptionEntity> aos = new HashMap<String, ApplicationOptionEntity>();
            save(plo.getProvider());

            if (plo.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plo.getApplicationOptions()) {
                    aos.put(ao.getId(), ao);
                }
            }
            if (plo.getChildren() != null) {
                for (ChildLearningOpportunityEntity clo : plo.getChildren()) {
                    for (ApplicationOptionEntity ao : clo.getApplicationOptions()) {
                        aos.put(ao.getId(), ao);
                    }
                }
            }
            for (ApplicationOptionEntity ao : aos.values()) {
                save(ao);
            }
            parentLearningOpportunityDAO.save(plo);
        }
    }

    @Override
    public void save(LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            learningOpportunityProviderDAO.save(learningOpportunityProvider);
        }
    }

    @Override
    public void save(ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionDAO.save(applicationOption);
        }
    }

    @Override
    public void dropAllData() {
        //drop current data
        applicationOptionDAO.getCollection().drop();
        parentLearningOpportunityDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
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
}
