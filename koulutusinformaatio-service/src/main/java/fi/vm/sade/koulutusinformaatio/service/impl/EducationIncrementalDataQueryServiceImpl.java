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

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLOIRefEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.CompetenceBasedQualificationParentLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSRefEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.TutkintoLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * 
 * @author Markus
 *
 */
@Service
public class EducationIncrementalDataQueryServiceImpl implements
EducationIncrementalDataQueryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EducationIncrementalDataQueryServiceImpl.class);

    private ApplicationOptionDAO applicationOptionDAO;
    private DataStatusDAO dataStatusDAO;
    private ModelMapper modelMapper;
    private PictureDAO pictureDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;


    @Autowired
    public EducationIncrementalDataQueryServiceImpl(ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper,
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
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    }

    @Override
    public LOS getLos(String losId) {
        HigherEducationLOSEntity higherEdE = this.higherEducationLOSDAO.get(losId);
        if (higherEdE != null) {
            return modelMapper.map(higherEdE, HigherEducationLOS.class);
        }

        CompetenceBasedQualificationParentLOSEntity adultVocationalEdE = this.adultVocationalLOSDAO.get(losId);
        if (adultVocationalEdE != null) {
            return modelMapper.map(adultVocationalEdE, CompetenceBasedQualificationParentLOS.class);
        }
        
        KoulutusLOSEntity koulutusEntity = this.koulutusLOSDAO.get(losId);
        if (koulutusEntity != null) {
            return modelMapper.map(koulutusEntity, KoulutusLOS.class);
        }

        TutkintoLOSEntity tutkintoEntity = this.tutkintoLOSDAO.get(losId);
        if (tutkintoEntity != null) {
            return modelMapper.map(tutkintoEntity, TutkintoLOS.class);
        }

        return null;
    }

    @Override
    public List<LOS> findLearningOpportunitiesByLoiId(String loiId) {

        HigherEducationLOSEntity higheredE = this.higherEducationLOSDAO.get(loiId);
        if (higheredE != null) {
            List<LOS> losses = new ArrayList<LOS>();
            losses.add(modelMapper.map(higheredE, HigherEducationLOS.class));
            return losses;
        }

        return null;
    }

    @Override
    public DataStatus getLatestSuccessDataStatus() {

        DataStatusEntity dataStatusE = this.dataStatusDAO.getLatestSuccessOrIncremental();
        if (dataStatusE != null) {
            return modelMapper.map(dataStatusE, DataStatus.class);
        } else {
            return null;
        }
    }

    @Override
    public List<String> getLearningOpportunityIdsByAS(String asId) {

        List<String> loss = new ArrayList<String>();
        
        List<Key<ApplicationOptionEntity>> aosE = this.applicationOptionDAO.findByAS(asId); 
        
       for (Key<ApplicationOptionEntity> curAoE : aosE) {
           ApplicationOptionEntity aoEntity = this.applicationOptionDAO.get(curAoE.getId().toString());
           loss.addAll(this.getLearningOpportunitiesByAO(aoEntity));
       }
        
        return loss;
    }
    
    private List<String> getLearningOpportunitiesByAO(ApplicationOptionEntity aoE) {
        LOG.debug("getting los ids for application option: {}", aoE.getId());
        List<String> loss = new ArrayList<String>();
        for (ChildLOIRefEntity childLoiE :  aoE.getChildLOIRefs()) {
            List<LOS> curLoss = this.findLearningOpportunitiesByLoiId(childLoiE.getId());
            if (curLoss != null) {
                for (LOS curLos : curLoss) {
                    loss.add(curLos.getId());
                }
            }
        }
        
        List<HigherEducationLOSRefEntity> higherEdLossRefs =  aoE.getHigherEdLOSRefs();
        if (higherEdLossRefs != null) {
            LOG.debug("Higher ed los refs: {}", higherEdLossRefs.size());
            for (HigherEducationLOSRefEntity curLosRef : higherEdLossRefs) {
                loss.add(curLosRef.getId());
            }
        }

        LOG.debug("returning: " + loss.size() + " los ids.");
        return loss;
    }

    @Override
    public TutkintoLOS getTutkinto(String oid) throws ResourceNotFoundException {
        TutkintoLOSEntity entity = this.tutkintoLOSDAO.get(oid);
        if (entity != null) {
            return modelMapper.map(entity, TutkintoLOS.class);
        } else {
            throw new ResourceNotFoundException(String.format("Tutkinto specification not found: %s", oid));
        }
    }

    @Override
    public List<KoulutusLOS> getKoulutusLos(ToteutustyyppiEnum toteutusTyyppi, String tarjoaja, String koulutusKoodi) throws ResourceNotFoundException {
        List<KoulutusLOSEntity> result = koulutusLOSDAO.getKoulutusLos(toteutusTyyppi, tarjoaja, koulutusKoodi);
        return modelMapper.<List<KoulutusLOS>>map(result, new TypeToken<List<KoulutusLOS>>() {}.getType());
    }

}
