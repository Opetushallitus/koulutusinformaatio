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

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.dao.AdultUpperSecondaryLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.AdultUpperSecondaryLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.PictureEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.SpecialLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunityInstanceEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.UpperSecondaryLearningOpportunitySpecificationEntity;
import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;

/**
 * 
 * @author Markus
 *
 */
@Service
public class EducationIncrementalDataUpdateServiceImpl implements
        EducationIncrementalDataUpdateService {
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

    @Autowired
    public EducationIncrementalDataUpdateServiceImpl(ModelMapper modelMapper, ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
            ApplicationOptionDAO applicationOptionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            ChildLearningOpportunityDAO childLearningOpportunityDAO,
            PictureDAO pictureDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO,
            DataStatusDAO dataStatusDAO, SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO,
            HigherEducationLOSDAO higherEducationLOSDAO,
            AdultUpperSecondaryLOSDAO adultUpperSecondaryLOSDAO) {
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
        }
        
    }

    @Override
    public void deleteAo(ApplicationOption ao) {
        
        this.applicationOptionDAO.deleteById(ao.getId());
        
    }
    
    @Override
    public void clearHigherEducations(IndexerService indexerService, HttpSolrServer loHttpSolrServer) throws IOException, SolrServerException {
        List<HigherEducationLOSEntity> higherEds = higherEducationLOSDAO.findAllHigherEds();
        for (HigherEducationLOSEntity curHigherEd : higherEds) {
            HigherEducationLOS curLos = modelMapper.map(curHigherEd, HigherEducationLOS.class);
            this.deleteLos(curLos);
            indexerService.removeLos(curLos, loHttpSolrServer);
            for (ApplicationOption curAo : curLos.getApplicationOptions()) {
                this.deleteAo(curAo);
            }
            
        }
        
    }

    @Override
    public void updateHigherEdLos(HigherEducationLOS los) {
        if (los != null) {

            for (HigherEducationLOS curChild : los.getChildren()) {
                updateHigherEdLos(curChild);
            }
            HigherEducationLOSEntity plos =
                    modelMapper.map(los, HigherEducationLOSEntity.class);

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

            AdultUpperSecondaryLOSEntity plos =
                    modelMapper.map(los, AdultUpperSecondaryLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());


            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }

            this.adultUpperSecondaryLOSDAO.deleteById(plos.getId());
            this.adultUpperSecondaryLOSDAO.save(plos);
        }
        
        
    }

}
