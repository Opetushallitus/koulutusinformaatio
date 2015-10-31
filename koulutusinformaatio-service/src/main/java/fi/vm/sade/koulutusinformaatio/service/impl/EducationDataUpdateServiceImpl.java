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

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoInternalException;

import fi.vm.sade.koulutusinformaatio.dao.AdultUpperSecondaryLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.AdultUpperSecondaryLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CompetenceBasedQualificationParentLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.PictureEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.SpecialLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.TutkintoLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.service.EducationDataUpdateService;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataUpdateServiceImpl implements EducationDataUpdateService {

    private ModelMapper modelMapper;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private PictureDAO pictureTransactionDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO;
    private DataStatusDAO dataStatusDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO;
    private HigherEducationLOSDAO higherEducationLOSTransactionDAO;
    private AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSTransactionDAO;
    private KoulutusLOSDAO koulutusLOSTransactionDAO;
    private TutkintoLOSDAO tutkintoLOSTransactionDAO;
    private AdultVocationalLOSDAO adultVocationalLOSTransactionDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(EducationDataUpdateServiceImpl.class);


    @Autowired
    public EducationDataUpdateServiceImpl(ModelMapper modelMapper,
            ApplicationOptionDAO applicationOptionTransactionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
            PictureDAO pictureTransactionDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO,
            DataStatusDAO dataStatusDAO, SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO,
            HigherEducationLOSDAO higherEducationLOSTransactionDAO,
            AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSTransactionDAO,
            KoulutusLOSDAO koulutusLOSTransactionDAO,
            TutkintoLOSDAO tutkintoLOSTransactionDAO,
            AdultVocationalLOSDAO adultVocationalLOSTransactionDAO) {
        this.modelMapper = modelMapper;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.upperSecondaryLOSTransactionDAO = upperSecondaryLOSTransactionDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.specialLOSTransactionDAO = specialLOSTransactionDAO;
        this.higherEducationLOSTransactionDAO = higherEducationLOSTransactionDAO;
        this.adultUpperSecondaryLOSTransactionDAO = adultUpperSecondaryLOSTransactionDAO;
        this.koulutusLOSTransactionDAO = koulutusLOSTransactionDAO;
        this.tutkintoLOSTransactionDAO = tutkintoLOSTransactionDAO;
        this.adultVocationalLOSTransactionDAO = adultVocationalLOSTransactionDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void save(LOS learningOpportunitySpecification) {
        if (learningOpportunitySpecification instanceof UpperSecondaryLOS) {
            save((UpperSecondaryLOS) learningOpportunitySpecification);
        }
        else if (learningOpportunitySpecification instanceof SpecialLOS) {
            save((SpecialLOS) learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof HigherEducationLOS) {
            saveHigherEducationLOS((HigherEducationLOS)learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof AdultUpperSecondaryLOS) {
            saveAdultUpperSecondaryLOS((AdultUpperSecondaryLOS)learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof KoulutusLOS) {
            saveKoulutusLOS((KoulutusLOS)learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof TutkintoLOS) {
            saveTutkintoLOS((TutkintoLOS) learningOpportunitySpecification);
        }
        else if (learningOpportunitySpecification instanceof CompetenceBasedQualificationParentLOS) {
            saveAdultVocationalLOS((CompetenceBasedQualificationParentLOS)learningOpportunitySpecification);
        }
    }

    private void saveAdultVocationalLOS(
            CompetenceBasedQualificationParentLOS learningOpportunitySpecification) {
        
        if (learningOpportunitySpecification != null) {
            CompetenceBasedQualificationParentLOSEntity entity =
                    modelMapper.map(learningOpportunitySpecification, CompetenceBasedQualificationParentLOSEntity.class);

            save(entity.getProvider());

                for (ApplicationOptionEntity ao : entity.getApplicationOptions()) {
                    save(ao);
                }
            this.adultVocationalLOSTransactionDAO.save(entity);
        }
        
    }

    private void saveAdultUpperSecondaryLOS(
            AdultUpperSecondaryLOS learningOpportunitySpecification) {
        
        if (learningOpportunitySpecification != null) {
            AdultUpperSecondaryLOSEntity entity =
                    modelMapper.map(learningOpportunitySpecification, AdultUpperSecondaryLOSEntity.class);

            save(entity.getProvider());
            
            for (LearningOpportunityProviderEntity addProv : entity.getAdditionalProviders()) {
                save(addProv);
            }

                for (ApplicationOptionEntity ao : entity.getApplicationOptions()) {
                    save(ao);
                }
            this.adultUpperSecondaryLOSTransactionDAO.save(entity);
        }
        
    }

    private void saveKoulutusLOS(
            KoulutusLOS learningOpportunitySpecification) {

        if (learningOpportunitySpecification != null) {
            KoulutusLOSEntity entity =
                    modelMapper.map(learningOpportunitySpecification, KoulutusLOSEntity.class);

            save(entity.getProvider());

            for (LearningOpportunityProviderEntity addProv : entity.getAdditionalProviders()) {
                save(addProv);
            }

            for (ApplicationOptionEntity ao : entity.getApplicationOptions()) {
                save(ao);
            }
            this.koulutusLOSTransactionDAO.save(entity);
        }

    }

    private void saveTutkintoLOS(
            TutkintoLOS learningOpportunitySpecification) {

        if (learningOpportunitySpecification != null) {
            TutkintoLOSEntity entity =
                    modelMapper.map(learningOpportunitySpecification, TutkintoLOSEntity.class);

            save(entity.getProvider());

            for (LearningOpportunityProviderEntity addProv : entity.getAdditionalProviders()) {
                save(addProv);
            }

            this.tutkintoLOSTransactionDAO.save(entity);
        }

    }

    @Override
    public void save(DataStatus dataStatus) {
        if (dataStatus != null) {
            dataStatusDAO.save(modelMapper.map(dataStatus, DataStatusEntity.class));
        }
    }

    private void save(SpecialLOS specialLOS) {
        if (specialLOS != null) {
            SpecialLearningOpportunitySpecificationEntity entity =
                    modelMapper.map(specialLOS, SpecialLearningOpportunitySpecificationEntity.class);

            save(entity.getProvider());
            
            for (LearningOpportunityProviderEntity addProv : entity.getAdditionalProviders()) {
                save(addProv);
            }

            for (ChildLearningOpportunityInstanceEntity loi : entity.getLois()) {
                for (ApplicationOptionEntity ao : loi.getApplicationOptions()) {
                    save(ao);
                }
            }
            specialLOSTransactionDAO.save(entity);
        }
    }

    private void save(UpperSecondaryLOS upperSecondaryLOS) {
        if (upperSecondaryLOS != null) {
            UpperSecondaryLearningOpportunitySpecificationEntity entity =
                    modelMapper.map(upperSecondaryLOS, UpperSecondaryLearningOpportunitySpecificationEntity.class);

            save(entity.getProvider());
            
            for (LearningOpportunityProviderEntity addProv : entity.getAdditionalProviders()) {
                save(addProv);
            }

            for (UpperSecondaryLearningOpportunityInstanceEntity loi : entity.getLois()) {
                for (ApplicationOptionEntity ao : loi.getApplicationOptions()) {
                    save(ao);
                }
            }
            try {
                upperSecondaryLOSTransactionDAO.save(entity);
            } catch (MongoInternalException e) {
                LOGGER.error("Saving los {} failed to mongo exception!", upperSecondaryLOS.getId(), e);
            }
        }
    }

    private void save(final LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            save(learningOpportunityProvider.getPicture());

            LearningOpportunityProviderEntity old = learningOpportunityProviderTransactionDAO.get(learningOpportunityProvider.getId());
            if (old != null && old.getApplicationSystemIds() != null) {
                learningOpportunityProvider.getApplicationSystemIds().addAll(old.getApplicationSystemIds());
            }

            learningOpportunityProviderTransactionDAO.save(learningOpportunityProvider);
        }
    }
    
    public void save(Provider provider) {
        LearningOpportunityProviderEntity provE 
            = modelMapper.map(provider, LearningOpportunityProviderEntity.class);
        save(provE);
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

    private void saveHigherEducationLOS(HigherEducationLOS los) {

        if (los != null) {

            for (HigherEducationLOS curChild : los.getChildren()) {
                saveHigherEducationLOS(curChild);
            }
            HigherEducationLOSEntity plos =
                    modelMapper.map(los, HigherEducationLOSEntity.class);

            save(plos.getProvider());
            
            for (LearningOpportunityProviderEntity addProv : plos.getAdditionalProviders()) {
                save(addProv);
            }
            
            
            if (plos.getStructureImage() != null 
                    && plos.getStructureImage().getPictureTranslations() != null 
                    && plos.getStructureImage().getPictureTranslations() != null) {
                for (PictureEntity curPict : plos.getStructureImage().getPictureTranslations().values()) {
                    save(curPict);
                }
            }


            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    save(ao);
                }
            }

            this.higherEducationLOSTransactionDAO.save(plos);
        }
    }
}
