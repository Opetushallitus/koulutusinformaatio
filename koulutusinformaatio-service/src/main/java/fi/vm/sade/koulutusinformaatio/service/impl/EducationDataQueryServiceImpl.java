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
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.ApplicationOptionNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.InvalidParametersException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationDataQueryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataQueryServiceImpl implements EducationDataQueryService {

    private ApplicationOptionDAO applicationOptionDAO;
    private DataStatusDAO dataStatusDAO;
    private ModelMapper modelMapper;
    private PictureDAO pictureDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;

    @Autowired
    public EducationDataQueryServiceImpl(ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
            DataStatusDAO dataStatusDAO, PictureDAO pictureDAO,
            HigherEducationLOSDAO higherEducationLOSDAO,
            AdultVocationalLOSDAO adultVocationalLOSDAO,
            KoulutusLOSDAO koulutusLOSDAO,
            TutkintoLOSDAO tutkintoLOSDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO) {
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<ApplicationOption> findApplicationOptions(String asId, String lopId, String baseEducation,
            boolean vocational, boolean nonVocational) {
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(asId, lopId, baseEducation,
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
        List<ApplicationOptionEntity> applicationOptions = applicationOptionDAO.findFromSecondary(aoIds);
        return Lists.transform(applicationOptions, new Function<ApplicationOptionEntity, ApplicationOption>() {
            @Override
            public ApplicationOption apply(ApplicationOptionEntity applicationOptionEntity) {
                return modelMapper.map(applicationOptionEntity, ApplicationOption.class);
            }
        });
    }

    @Override
    public ApplicationOption getApplicationOption(String aoId) throws ApplicationOptionNotFoundException {
        ApplicationOptionEntity ao = applicationOptionDAO.get(aoId);
        if (ao != null) {
            return modelMapper.map(ao, ApplicationOption.class);
        } else {
            throw new ApplicationOptionNotFoundException("Application option not found: " + aoId);
        }
    }

    @Override
    public DataStatus getLatestDataStatus() {
        DataStatusEntity status = dataStatusDAO.getLatest();
        if (status != null) {
            return modelMapper.map(status, DataStatus.class);
        } else {
            return null;
        }
    }

    @Override
    public Picture getPicture(String id) throws ResourceNotFoundException {
        PictureEntity picture = pictureDAO.getFromSecondary(id);
        if (picture != null) {
            return modelMapper.map(picture, Picture.class);
        } else {
            throw new ResourceNotFoundException("Picture not found: " + id);
        }
    }

    @Override
    public HigherEducationLOS getHigherEducationLearningOpportunity(String oid) throws ResourceNotFoundException {
        HigherEducationLOSEntity entity = this.higherEducationLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, HigherEducationLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }
    
    @Override
    public KoulutusLOS getKoulutusLearningOpportunity(String oid) throws ResourceNotFoundException {
        KoulutusLOSEntity entity = this.koulutusLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, KoulutusLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("Koulutus learning opportunity specification not found: %s", oid));
        }
    }
    
    @Override
    public TutkintoLOS getTutkintoLearningOpportunity(String oid) throws ResourceNotFoundException {
        TutkintoLOSEntity entity = this.tutkintoLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, TutkintoLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("Tutkinto learning opportunity specification not found: %s", oid));
        }
    }

    @Override
    public CompetenceBasedQualificationParentLOS getAdultVocationalLearningOpportunity(String oid) throws ResourceNotFoundException {
        CompetenceBasedQualificationParentLOSEntity entity = this.adultVocationalLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, CompetenceBasedQualificationParentLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("University of applied science learning opportunity specification not found: %s", oid));
        }
    }

    @Override
    public Provider getProvider(String id) throws ResourceNotFoundException {
        LearningOpportunityProviderEntity entity = learningOpportunityProviderDAO.get(id);
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

        List<KoulutusLOSEntity> entities = koulutusLOSDAO.findByProviderId(providerId);
        List<KoulutusLOS> losses = Lists.transform(entities,
                new Function<KoulutusLOSEntity, KoulutusLOS>() {
                    @Override
                    public KoulutusLOS apply(KoulutusLOSEntity input) {
                        return modelMapper.map(input, KoulutusLOS.class);
                    }
                }
        );

        List<HigherEducationLOS> higherEds = Lists.transform(higherEducationLOSDAO.findByProviderId(providerId),
                new Function<HigherEducationLOSEntity, HigherEducationLOS>() {
                    @Override
                    public HigherEducationLOS apply(HigherEducationLOSEntity input) {
                        return modelMapper.map(input, HigherEducationLOS.class);
                    }
                });

        List<CompetenceBasedQualificationParentLOS> adultVocationalLOSes = Lists.transform(adultVocationalLOSDAO.findByProviderId(providerId),
                new Function<CompetenceBasedQualificationParentLOSEntity, CompetenceBasedQualificationParentLOS>() {
                    @Override
                    public CompetenceBasedQualificationParentLOS apply(CompetenceBasedQualificationParentLOSEntity input) {
                        return modelMapper.map(input, CompetenceBasedQualificationParentLOS.class);
                    }
                });

        List<TutkintoLOS> tutkintoLOSes = Lists.transform(tutkintoLOSDAO.findByProviderId(providerId),
                new Function<TutkintoLOSEntity, TutkintoLOS>() {
                    @Override
                    public TutkintoLOS apply(TutkintoLOSEntity input) {
                        return modelMapper.map(input, TutkintoLOS.class);
                    }
                });

        results.addAll(adultVocationalLOSes);
        results.addAll(tutkintoLOSes);
        results.addAll(higherEds);
        results.addAll(losses);

        return results;
    }

    @Override
    public DataStatus getLatestSuccessDataStatus() {
        
        DataStatusEntity dataStatusE = this.dataStatusDAO.getLatestSuccess();
        if (dataStatusE != null) {
            return modelMapper.map(dataStatusE, DataStatus.class);
        } else {
            return null;
        }
    }


}
