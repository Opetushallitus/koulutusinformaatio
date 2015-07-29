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

import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import org.apache.commons.lang.NotImplementedException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Markus
 *
 */
@Service
public class EducationIncrementalDataUpdateServiceImpl implements
        EducationIncrementalDataUpdateService {
    
    
    public static final Logger LOG = LoggerFactory.getLogger(EducationIncrementalDataUpdateServiceImpl.class);
    
    private ModelMapper modelMapper;
    private ParentLearningOpportunitySpecificationDAO parentLOSDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private ChildLearningOpportunityDAO childLODAO;
    private PictureDAO pictureDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSDAO;
    private DataStatusDAO dataStatusDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLOSDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    public EducationIncrementalDataUpdateServiceImpl(ModelMapper modelMapper, ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
            ApplicationOptionDAO applicationOptionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            ChildLearningOpportunityDAO childLearningOpportunityDAO,
            PictureDAO pictureDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO,
            DataStatusDAO dataStatusDAO, SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO,
            HigherEducationLOSDAO higherEducationLOSDAO,
            AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSDAO,
            AdultVocationalLOSDAO adultVocationalLOSDAO,
            KoulutusLOSDAO koulutusLOSDAO,
            TutkintoLOSDAO tutkintoLOSDAO) {
        this.modelMapper = modelMapper;
        this.parentLOSDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.childLODAO = childLearningOpportunityDAO;
        this.pictureDAO = pictureDAO;
        this.upperSecondaryLOSDAO = upperSecondaryLearningOpportunitySpecificationDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.specialLOSDAO = specialLearningOpportunitySpecificationDAO; 
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.adultUpperSecondaryLOSDAO = adultUpperSecondaryLOSDAO;
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void save(LOS learningOpportunitySpecification) {
        if (learningOpportunitySpecification instanceof ParentLOS) {
            save((ParentLOS) learningOpportunitySpecification);
        }
        else if (learningOpportunitySpecification instanceof UpperSecondaryLOS) {
            save((UpperSecondaryLOS) learningOpportunitySpecification);
        }
        else if (learningOpportunitySpecification instanceof SpecialLOS) {
            save((SpecialLOS) learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof HigherEducationLOS) {
            this.saveHigherEducationLOS((HigherEducationLOS)learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof KoulutusLOS) {
            this.saveKoulutusLOS((KoulutusLOS)learningOpportunitySpecification);
        } 
        else if (learningOpportunitySpecification instanceof TutkintoLOS) {
            this.saveTutkintoLOS((KoulutusLOS) learningOpportunitySpecification);
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
            
            try {
                Provider existingProv = this.getProvider(specialLOS.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                    for (String curAsId : existingProv.getApplicationSystemIds()) {
                        if (!specialLOS.getProvider().getApplicationSystemIds().contains(curAsId)) {
                            specialLOS.getProvider().getApplicationSystemIds().add(curAsId);
                        }
                    }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.debug("No existing provider");
            }
            
            SpecialLearningOpportunitySpecificationEntity entity =
                    modelMapper.map(specialLOS, SpecialLearningOpportunitySpecificationEntity.class);

            save(entity.getProvider());

            for (ChildLearningOpportunityInstanceEntity loi : entity.getLois()) {
                for (ApplicationOptionEntity ao : loi.getApplicationOptions()) {
                    save(ao);
                }
            }
            specialLOSDAO.save(entity);
        }
    }

    private void save(UpperSecondaryLOS upperSecondaryLOS) {
        if (upperSecondaryLOS != null) {
            
            try {
                Provider existingProv = this.getProvider(upperSecondaryLOS.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!upperSecondaryLOS.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        upperSecondaryLOS.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.debug("No existing provider");
            }
            
            UpperSecondaryLearningOpportunitySpecificationEntity entity =
                    modelMapper.map(upperSecondaryLOS, UpperSecondaryLearningOpportunitySpecificationEntity.class);

            save(entity.getProvider());

            for (UpperSecondaryLearningOpportunityInstanceEntity loi : entity.getLois()) {
                for (ApplicationOptionEntity ao : loi.getApplicationOptions()) {
                    save(ao);
                }
            }
            upperSecondaryLOSDAO.save(entity);
        }
    }

    private void save(final ParentLOS parentLOS) {
        if (parentLOS != null) {
            
            try {
                Provider existingProv = this.getProvider(parentLOS.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!parentLOS.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        parentLOS.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.debug("No existing provider");
            }
            
            ParentLearningOpportunitySpecificationEntity plos =
                    modelMapper.map(parentLOS, ParentLearningOpportunitySpecificationEntity.class);
            save(plos.getProvider());

            if (plos.getChildren() != null) {
                for (ChildLearningOpportunitySpecificationEntity cLO : plos.getChildren()) {
                    save(cLO);
                }
            }

            parentLOSDAO.save(plos);
        }
    }

    private void save(final ChildLearningOpportunitySpecificationEntity childLearningOpportunity) {
        if (childLearningOpportunity != null) {
            for (ChildLearningOpportunityInstanceEntity childLearningOpportunityInstance : childLearningOpportunity.getLois()) {
                if (childLearningOpportunityInstance.getApplicationOptions() != null) {
                    for (ApplicationOptionEntity ao : childLearningOpportunityInstance.getApplicationOptions()) {
                        save(ao);
                    }
                }
            }
            childLODAO.save(childLearningOpportunity);
        }
    }

    private void save(final LearningOpportunityProviderEntity learningOpportunityProvider) {
        if (learningOpportunityProvider != null) {
            save(learningOpportunityProvider.getPicture());

            LearningOpportunityProviderEntity old = learningOpportunityProviderDAO.get(learningOpportunityProvider.getId());
            if (old != null && old.getApplicationSystemIds() != null) {
                learningOpportunityProvider.getApplicationSystemIds().addAll(old.getApplicationSystemIds());
            }

            learningOpportunityProviderDAO.save(learningOpportunityProvider);
        }
    }

    private void save(final ApplicationOptionEntity applicationOption) {
        if (applicationOption != null) {
            save(applicationOption.getProvider());
            applicationOptionDAO.save(applicationOption);
        }
    }

    private void save(final PictureEntity picture) {
        if (picture != null) {
            pictureDAO.save(picture);
        }
    }

    private void saveHigherEducationLOS(HigherEducationLOS los) {

        if (los != null) {

            for (HigherEducationLOS curChild : los.getChildren()) {
                saveHigherEducationLOS(curChild);
            }
            
            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        los.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.debug("No existing provider");
            }
            
            HigherEducationLOSEntity plos =
                    modelMapper.map(los, HigherEducationLOSEntity.class);

            save(plos.getProvider());
            
            
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

            this.higherEducationLOSDAO.save(plos);
        }
    }

    @Override
    public void deleteLos(LOS los) {
        
        if (los instanceof ParentLOS) {
            
            for (ChildLOS curChild : ((ParentLOS) los).getChildren()) {
                this.childLODAO.deleteById(curChild.getId());
            }
            this.parentLOSDAO.deleteById(los.getId());
        } else if (los instanceof ChildLOS) {
            this.childLODAO.deleteById(los.getId());
        } else if (los instanceof SpecialLOS) {
            this.specialLOSDAO.deleteById(los.getId());
        } else if (los instanceof UpperSecondaryLOS) {
            this.upperSecondaryLOSDAO.deleteById(los.getId());
        } else if (los instanceof HigherEducationLOS) {
            this.higherEducationLOSDAO.deleteById(los.getId());
        } else if (los instanceof AdultUpperSecondaryLOS) {
            this.adultUpperSecondaryLOSDAO.deleteById(los.getId());
        } else if (los instanceof CompetenceBasedQualificationParentLOS) {
            this.adultVocationalLOSDAO.deleteById(los.getId());
        } else if (los instanceof KoulutusLOS) {
            this.koulutusLOSDAO.deleteById(los.getId());
        } else if (los instanceof TutkintoLOS) {
            this.tutkintoLOSDAO.deleteById(los.getId());
        }
        
    }

    @Override
    public void deleteAo(ApplicationOption ao) {
        this.applicationOptionDAO.deleteById(ao.getId());
    }

    @Override
    public void updateHigherEdLos(HigherEducationLOS los) {
        if (los != null) {

            for (HigherEducationLOS curChild : los.getChildren()) {
                updateHigherEdLos(curChild);
            }
            
            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        los.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.warn("Problem updating provider's application system references");
            }
            
            HigherEducationLOSEntity plos =
                    modelMapper.map(los, HigherEducationLOSEntity.class);

            //this.learningOpportunityProviderDAO.get(id)
            
            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());
            
            
            if (plos.getStructureImage() != null 
                    && plos.getStructureImage().getPictureTranslations() != null 
                    && plos.getStructureImage().getPictureTranslations() != null) {
                for (PictureEntity curPict : plos.getStructureImage().getPictureTranslations().values()) {
                    save(curPict);
                }
            }


            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    
                    try {
                        ApplicationOptionEntity exAo = this.getAo(ao.getId());
                        updateLosRefs(ao, exAo, plos.getId());
                    } catch (ResourceNotFoundException ex) {
                        LOG.debug("No existing ao");
                    }
                    
                    
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }

            this.higherEducationLOSDAO.deleteById(plos.getId());
            this.higherEducationLOSDAO.save(plos);
        }
        
    }

    @Override
    public void updateAdultUpsecLos(AdultUpperSecondaryLOS los) {
        
        if (los != null) {
            
            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        los.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.warn("Problem updating provider's application system references");
            }

            AdultUpperSecondaryLOSEntity plos =
                    modelMapper.map(los, AdultUpperSecondaryLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());


            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    
                    try {
                        ApplicationOptionEntity exAo = this.getAo(ao.getId());
                        updateLosRefs(ao, exAo, plos.getId());
                    } catch (ResourceNotFoundException ex) {
                        LOG.debug("No existing ao");
                    }
                    
                    
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }

            this.adultUpperSecondaryLOSDAO.deleteById(plos.getId());
            this.adultUpperSecondaryLOSDAO.save(plos);
        }
        
        
    }
    
    @Override
    public void updateAdultVocationalLos(
            CompetenceBasedQualificationParentLOS los) {
        
        if (los != null) {
            
            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                for (String curAsId : existingProv.getApplicationSystemIds()) {
                    if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                        los.getProvider().getApplicationSystemIds().add(curAsId);
                    }
                }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.warn("Problem updating provider's application system references");
            }

            CompetenceBasedQualificationParentLOSEntity plos =
                    modelMapper.map(los, CompetenceBasedQualificationParentLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());


            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    
                    try {
                        ApplicationOptionEntity exAo = this.getAo(ao.getId());
                        updateLosRefs(ao, exAo, plos.getId());
                    } catch (ResourceNotFoundException ex) {
                        LOG.debug("No existing ao");
                    }
                    
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }

            this.adultVocationalLOSDAO.deleteById(plos.getId());
            this.adultVocationalLOSDAO.save(plos);
        }
    }
    
    private void updateLosRefs(ApplicationOptionEntity ao, ApplicationOptionEntity existingAo, String curLosId) {
        for (HigherEducationLOSRefEntity curRef : existingAo.getHigherEdLOSRefs()) {
            if (this.adultVocationalLOSDAO.get(curRef.getId()) != null 
                    && !curRef.getId().equals(curLosId)) {
                
                ao.getHigherEdLOSRefs().add(curRef);
                
            }
        }
    }
    
    private ApplicationOptionEntity getAo(String aoId) throws ResourceNotFoundException {
        ApplicationOptionEntity aoE = this.applicationOptionDAO.get(aoId);
        if (aoE != null) {
            return aoE;
        } else {
            throw new ResourceNotFoundException(String.format("ApplicationOption not found: %s", aoId));
        }
    }
    
    private Provider getProvider(String id) throws ResourceNotFoundException {
        LearningOpportunityProviderEntity entity = learningOpportunityProviderDAO.get(id);
        if (entity != null) {
            return modelMapper.map(entity, Provider.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Learning opportunity provider not found: %s", id));
        }
    }

    private void saveKoulutusLOS(KoulutusLOS los) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    private void saveTutkintoLOS(KoulutusLOS learningOpportunitySpecification) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public void updateKoulutusLos(KoulutusLOS los) {
        if (los != null) {

            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                    for (String curAsId : existingProv.getApplicationSystemIds()) {
                        if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                            los.getProvider().getApplicationSystemIds().add(curAsId);
                        }
                    }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.warn("Problem updating provider's application system references");
            }

            KoulutusLOSEntity plos = modelMapper.map(los, KoulutusLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());
            
            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }
            this.koulutusLOSDAO.deleteById(plos.getId());
            this.koulutusLOSDAO.save(plos);
        }
    }

    @Override
    public void updateTutkintoLos(TutkintoLOS los) {
        if (los != null) {

            try {
                Provider existingProv = this.getProvider(los.getProvider().getId());
                if (existingProv != null && existingProv.getApplicationSystemIds() != null) {
                    for (String curAsId : existingProv.getApplicationSystemIds()) {
                        if (!los.getProvider().getApplicationSystemIds().contains(curAsId)) {
                            los.getProvider().getApplicationSystemIds().add(curAsId);
                        }
                    }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.warn("Problem updating provider's application system references");
            }

            TutkintoLOSEntity plos = modelMapper.map(los, TutkintoLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());

            // TODO save childeducaions?

            this.tutkintoLOSDAO.deleteById(plos.getId());
            this.tutkintoLOSDAO.save(plos);
        }
    }
}
