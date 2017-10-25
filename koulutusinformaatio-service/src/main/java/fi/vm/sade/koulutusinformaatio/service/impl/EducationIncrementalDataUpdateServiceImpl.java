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
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author Markus
 *
 */
@Service
public class EducationIncrementalDataUpdateServiceImpl implements
        EducationIncrementalDataUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(EducationIncrementalDataUpdateServiceImpl.class);

    private ModelMapper modelMapper;
    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private PictureDAO pictureDAO;
    private DataStatusDAO dataStatusDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;

    @Autowired
    public EducationIncrementalDataUpdateServiceImpl(ModelMapper modelMapper,
            ApplicationOptionDAO applicationOptionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            PictureDAO pictureDAO,
            DataStatusDAO dataStatusDAO,
            HigherEducationLOSDAO higherEducationLOSDAO,
            AdultVocationalLOSDAO adultVocationalLOSDAO,
            KoulutusLOSDAO koulutusLOSDAO,
            TutkintoLOSDAO tutkintoLOSDAO) {
        this.modelMapper = modelMapper;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.pictureDAO = pictureDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void save(DataStatus dataStatus) {
        if (dataStatus != null) {
            dataStatusDAO.save(modelMapper.map(dataStatus, DataStatusEntity.class));
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
            LOG.debug("Saved hakukohde: {}", applicationOption.getId());
            LOG.trace("Name: {}", applicationOption.getName().getTranslations().toString());
        }
    }

    private void save(final PictureEntity picture) {
        if (picture != null) {
            pictureDAO.save(picture);
        }
    }

    @Override
    public void deleteLos(LOS los) {
        String type = los.getToteutustyyppi() != null ? los.getToteutustyyppi().name() : los.getType();
        LOG.debug("Deleted {} koulutus: {}", type != null ? type : "by oid", los.getId());
        if (los instanceof HigherEducationLOS) {
            this.higherEducationLOSDAO.deleteById(los.getId());
        } else if (los instanceof CompetenceBasedQualificationParentLOS) {
            this.adultVocationalLOSDAO.deleteById(los.getId());
        } else if (los instanceof KoulutusLOS) {
            this.koulutusLOSDAO.deleteById(los.getId());
        } else if (los instanceof TutkintoLOS) {
            this.tutkintoLOSDAO.deleteById(los.getId());
        }

    }


    @Override
    public void updateHigherEdLos(HigherEducationLOS los) {
        if (los != null) {
            LOG.trace("updateHigherEdLos {}, {}",los.getId(), los.getName().getTranslations().toString());
            for (HigherEducationLOS curChild : los.getChildren()) {
                updateHigherEdLos(curChild);
            }

            updateProviderReferences(los);

            HigherEducationLOSEntity plos = modelMapper.map(los, HigherEducationLOSEntity.class);
            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());

            if (plos.getStructureImage() != null
                    && plos.getStructureImage().getPictureTranslations() != null
                    && plos.getStructureImage().getPictureTranslations() != null) {
                for (PictureEntity curPict : plos.getStructureImage().getPictureTranslations().values()) {
                    save(curPict);
                }
            }

            updateLosRefs(plos);

            LOG.debug("Updated {} koulutus: {}", los.getToteutustyyppi() != null ? los.getToteutustyyppi() : los.getType(), los.getId());
            this.higherEducationLOSDAO.deleteById(plos.getId());
            this.higherEducationLOSDAO.save(plos);
        }

    }

    @Override
    public void updateAdultVocationalLos(
            CompetenceBasedQualificationParentLOS los) {

        if (los != null) {

            updateProviderReferences(los);

            CompetenceBasedQualificationParentLOSEntity cplos =
                    modelMapper.map(los, CompetenceBasedQualificationParentLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(cplos.getProvider().getId());
            save(cplos.getProvider());

            updateLosRefs(cplos);

            LOG.debug("Updated {} koulutus: {}", los.getToteutustyyppi() != null ? los.getToteutustyyppi() : los.getType(), los.getId());
            this.adultVocationalLOSDAO.deleteById(cplos.getId());
            this.adultVocationalLOSDAO.save(cplos);
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

    @Override
    public void updateKoulutusLos(KoulutusLOS los) {
        if (los != null) {

            updateProviderReferences(los);

            KoulutusLOSEntity plos = modelMapper.map(los, KoulutusLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());

            if (plos.getApplicationOptions() != null) {
                for (ApplicationOptionEntity ao : plos.getApplicationOptions()) {
                    this.applicationOptionDAO.deleteById(ao.getId());
                    save(ao);
                }
            }
            LOG.debug("Updated {} koulutus: {}", los.getToteutustyyppi() != null ? los.getToteutustyyppi() : los.getType(), los.getId());
            this.koulutusLOSDAO.deleteById(plos.getId());
            this.koulutusLOSDAO.save(plos);
        }
    }

    @Override
    public void updateTutkintoLos(TutkintoLOS los) {
        if (los != null) {

            updateProviderReferences(los);

            TutkintoLOSEntity plos = modelMapper.map(los, TutkintoLOSEntity.class);

            this.learningOpportunityProviderDAO.deleteById(plos.getProvider().getId());
            save(plos.getProvider());

            LOG.debug("Updated {} tutkinto: {}", los.getToteutustyyppi() != null ? los.getToteutustyyppi() : los.getType(), los.getId());
            this.tutkintoLOSDAO.deleteById(plos.getId());
            this.tutkintoLOSDAO.save(plos);
        }
    }

    private void updateLosRefs(CompetenceBasedQualificationParentLOSEntity cplos) {
        updateLosRefs(cplos.getApplicationOptions(), cplos.getId());
    }

    private void updateLosRefs(List<ApplicationOptionEntity> ents, String losId) {
        LOG.trace("Updating los {} refs", losId);
        if(ents == null) {
            LOG.trace("ents null, returning");
            return;
        }
        for (ApplicationOptionEntity ao : ents) {
            try {
                ApplicationOptionEntity exAo = this.getAo(ao.getId());
                for (HigherEducationLOSRefEntity curRef : ao.getHigherEdLOSRefs()) {
                    if (this.adultVocationalLOSDAO.get(curRef.getId()) != null && !curRef.getId().equals(losId)) {
                        ao.getHigherEdLOSRefs().add(curRef);
                    }
                }
            } catch (ResourceNotFoundException ex) {
                LOG.debug("No existing ao");
            }

            this.applicationOptionDAO.deleteById(ao.getId());
            save(ao);
        }
    }

    private void updateLosRefs(HigherEducationLOSEntity cplos) {
        updateLosRefs(cplos.getApplicationOptions(), cplos.getId());
    }

    /**
     * Update LOS provider application system id references
     * @param los
     * @return true if success, false if not
     */
    private boolean updateProviderReferences(LOS los) {
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
            return false;
        }
        return true;
    }
}
