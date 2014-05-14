package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;

@Service
public class EducationIncrementalDataQueryServiceImpl implements
        EducationIncrementalDataQueryService {

    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private ChildLearningOpportunityDAO childLOTransactionDAO;
    private DataStatusDAO dataStatusTransactionDAO;
    private ModelMapper modelMapper;
    private PictureDAO pictureTransactionDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO;
    private HigherEducationLOSDAO higherEducationLOSTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;


    @Autowired
    public EducationIncrementalDataQueryServiceImpl(ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
            ApplicationOptionDAO applicationOptionTransactionDAO, ModelMapper modelMapper,
            ChildLearningOpportunityDAO childLOTransactionDAO,
            DataStatusDAO dataStatusTransactionDAO, PictureDAO pictureTransactionDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO,
            SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO, 
            HigherEducationLOSDAO higherEducationLOSTransactionDAO, 
            LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
            IndexerService indexerService) {
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.modelMapper = modelMapper;
        this.childLOTransactionDAO = childLOTransactionDAO;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.upperSecondaryLOSTransactionDAO = upperSecondaryLOSTransactionDAO;
        this.specialLOSTransactionDAO = specialLOSTransactionDAO;
        this.higherEducationLOSTransactionDAO = higherEducationLOSTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        
    }

    @Override
    public ParentLOS getParentLearningOpportunity(String oid) throws ResourceNotFoundException {
        ParentLearningOpportunitySpecificationEntity entity = parentLOSTransactionDAO.getFromSecondary(oid);
        if (entity != null) {
            return modelMapper.map(entity, ParentLOS.class);
        } else {
            throw new ResourceNotFoundException("Parent learning opportunity not found: " + oid);
        }
    }

    @Override
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId, String baseEducation,
            boolean vocational, boolean nonVocational) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionTransactionDAO.findFromSecondary(asId, lopId, baseEducation,
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
        List<ApplicationOptionEntity> applicationOptions = applicationOptionTransactionDAO.findFromSecondary(aoIds);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ApplicationOption getApplicationOption(String aoId) throws ResourceNotFoundException {
        ApplicationOptionEntity ao = applicationOptionTransactionDAO.get(aoId);
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
        DataStatusEntity status = dataStatusTransactionDAO.getLatest();
        if (status != null) {
            return modelMapper.map(status, DataStatus.class);
        } else {
            return null;
        }
    }

    @Override
    public Picture getPicture(String id) throws ResourceNotFoundException {
        PictureEntity picture = pictureTransactionDAO.getFromSecondary(id);
        if (picture != null) {
            return modelMapper.map(picture, Picture.class);
        } else {
            throw new ResourceNotFoundException("Picture not found: " + id);
        }
    }

    @Override
    public UpperSecondaryLOS getUpperSecondaryLearningOpportunity(String id) throws ResourceNotFoundException {
        
        UpperSecondaryLearningOpportunitySpecificationEntity entity =
                upperSecondaryLOSTransactionDAO.getFromSecondary(id);
        
        
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
                specialLOSTransactionDAO.getFromSecondary(id);
        if (entity != null) {
            return modelMapper.map(entity, SpecialLOS.class);
        }
        else {
            throw new ResourceNotFoundException(String.format("Special learning opportunity specification not found: %s", id));
        }
    }

    @Override
    public HigherEducationLOS getHigherEducationLearningOpportunity(String oid) throws ResourceNotFoundException {
        HigherEducationLOSEntity entity = this.higherEducationLOSTransactionDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, HigherEducationLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }

    @Override
    public Provider getProvider(String id) throws ResourceNotFoundException {
        LearningOpportunityProviderEntity entity = learningOpportunityProviderTransactionDAO.get(id);
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
                parentLOSTransactionDAO.findByProviderId(providerId);
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
                upperSecondaryLOSTransactionDAO.findByProviderId(providerId),
                new Function<UpperSecondaryLearningOpportunitySpecificationEntity, UpperSecondaryLOS>() {
                    @Override
                    public UpperSecondaryLOS apply(UpperSecondaryLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, UpperSecondaryLOS.class);
                    }
                }
                );
        List<SpecialLOS> specials = Lists.transform(
                specialLOSTransactionDAO.findByProviderId(providerId),
                new Function<SpecialLearningOpportunitySpecificationEntity, SpecialLOS>() {
                    @Override
                    public SpecialLOS apply(SpecialLearningOpportunitySpecificationEntity input) {
                        return modelMapper.map(input, SpecialLOS.class);
                    }
                }
                );

        List<HigherEducationLOS> higherEds = Lists.transform(higherEducationLOSTransactionDAO.findByProviderId(providerId),
                new Function<HigherEducationLOSEntity, HigherEducationLOS>() {
                    @Override
                    public HigherEducationLOS apply(HigherEducationLOSEntity input) {
                        return modelMapper.map(input, HigherEducationLOS.class);
                    }
                });

        results.addAll(parents);
        results.addAll(children);
        results.addAll(upsecs);
        results.addAll(specials);
        results.addAll(higherEds);
        return results;
    }

    private ChildLearningOpportunitySpecificationEntity getChildLO(String childLoId) throws ResourceNotFoundException {
        ChildLearningOpportunitySpecificationEntity clo = childLOTransactionDAO.getFromSecondary(childLoId);
        if (clo == null) {
            throw new ResourceNotFoundException("Child learning opportunity specification not found: " + childLoId);
        }
        return clo;
    }

    @Override
    public LOS getLos(String losId) {
        
        ParentLearningOpportunitySpecificationEntity losE = this.parentLOSTransactionDAO.get(losId);
        if (losE != null) {
            return modelMapper.map(losE, ParentLOS.class);
        }
        ChildLearningOpportunitySpecificationEntity childE = this.childLOTransactionDAO.get(losId);
        if (childE != null) {
            return modelMapper.map(childE, ChildLOS.class);
        }
        UpperSecondaryLearningOpportunitySpecificationEntity upsecE = this.upperSecondaryLOSTransactionDAO.get(losId);
        if (upsecE != null) {
            return modelMapper.map(upsecE, UpperSecondaryLOS.class);
        }
        SpecialLearningOpportunitySpecificationEntity specialLosE = this.specialLOSTransactionDAO.get(losId);
        if (specialLosE != null) {
            return modelMapper.map(specialLosE, SpecialLOS.class);
        }
        
        HigherEducationLOSEntity higherEdE = this.higherEducationLOSTransactionDAO.get(losId);
        if (higherEdE != null) {
            return modelMapper.map(higherEdE, HigherEducationLOS.class);
        }
        
        return null;
    }

    @Override
    public List<LOS> findLearningOpportunitiesByLoiId(String loiId) {
        
        System.out.println("Finding learning opportunities");
        
        List<ChildLearningOpportunitySpecificationEntity> childrenE = this.childLOTransactionDAO.findByLoiId(loiId);
        if (childrenE != null) {
            
            System.out.println("There are entities: " + childrenE.size());
            
            return Lists.transform(
                    childrenE,
                    new Function<ChildLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(ChildLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, ChildLOS.class);
                        }
                    }
                    );
            
        }
        
        List<SpecialLearningOpportunitySpecificationEntity> specialsE = this.specialLOSTransactionDAO.findByLoiId(loiId);
        if (specialsE != null) {
            return Lists.transform(
                    specialsE,
                    new Function<SpecialLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(SpecialLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, SpecialLOS.class);
                        }
                    }
                    );
        }
        
        List<UpperSecondaryLearningOpportunitySpecificationEntity> upsecsE = this.upperSecondaryLOSTransactionDAO.findByLoiId(loiId);
        if (upsecsE != null) {
            return Lists.transform(
                    upsecsE,
                    new Function<UpperSecondaryLearningOpportunitySpecificationEntity, LOS>() {
                        @Override
                        public LOS apply(UpperSecondaryLearningOpportunitySpecificationEntity input) {
                            return modelMapper.map(input, UpperSecondaryLOS.class);
                        }
                    }
                    );
        }
        
        HigherEducationLOSEntity higheredE = this.higherEducationLOSTransactionDAO.get(loiId);
        if (higheredE != null) {
            List<LOS> losses = new ArrayList<LOS>();
            losses.add(modelMapper.map(higheredE, HigherEducationLOS.class));
            return losses;
        }
        
        
        
        return null;
    }

    @Override
    public DataStatus getLatestSuccessDataStatus() {
        
        DataStatusEntity dataStatusE = this.dataStatusTransactionDAO.getLatestSuccess();
        if (dataStatusE != null) {
            return modelMapper.map(dataStatusE, DataStatus.class);
        } else {
            return null;
        }
    }

}
