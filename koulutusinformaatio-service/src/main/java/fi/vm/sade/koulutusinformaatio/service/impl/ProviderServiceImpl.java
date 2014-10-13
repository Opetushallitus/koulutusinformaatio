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

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    public static final Logger LOG = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ConversionService conversionService;
    private final OrganisaatioRawService organisaatioRawService;
    private KoodistoService koodistoService;

    private static Map<String,Provider> providerMap = new HashMap<String,Provider>();

    @Autowired
    public ProviderServiceImpl(ConversionService conversionService, OrganisaatioRawService organisaatioRawService, KoodistoService koodistoService) {
        this.conversionService = conversionService;
        this.organisaatioRawService = organisaatioRawService;
        this.koodistoService = koodistoService;
    }

    @Override
    public Provider getByOID(String oid) throws KoodistoException, ResourceNotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching provider with oid " + oid);
        }
        
        if (providerMap.containsKey(oid)) {
            return providerMap.get(oid);
        }

        OrganisaatioRDTO organisaatioRDTO = organisaatioRawService.getOrganisaatio(oid);

        Provider provider = conversionService.convert(organisaatioRDTO, Provider.class);
        if (!validate(provider) && !Strings.isNullOrEmpty(organisaatioRDTO.getParentOid())) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Enriching provider " + organisaatioRDTO.getOid() + " with parent provider " + organisaatioRDTO.getParentOid());
            }
            
            Provider parent = getByOID(organisaatioRDTO.getParentOid());
            provider = inheritMetadata(provider, parent);
        }
        
        if (provider.getType() == null) {
            inheritOlTypes(provider, organisaatioRDTO);
        } else {
            provider.getOlTypes().add(provider.getType());
        }
        
        providerMap.put(oid, provider);
        return provider;
    }

    private void inheritOlTypes(Provider provider, OrganisaatioRDTO rawProvider) throws ResourceNotFoundException, KoodistoException {
        
        if (rawProvider.getTyypit().contains("Oppilaitos")) {
            Code olTyyppi = koodistoService.searchFirst(rawProvider.getOppilaitosTyyppiUri());
            if (olTyyppi != null) {
                provider.getOlTypes().add(olTyyppi);
            }
        }
        else if (rawProvider.getTyypit().contains("Toimipiste")) {
            OrganisaatioRDTO inheritableOrg = this.organisaatioRawService.getOrganisaatio(rawProvider.getParentOid());
            inheritOlTypes(provider, inheritableOrg);
        } else if (rawProvider.getTyypit().contains("Koulutustoimija")) {
            try {
            List<OrganisaatioRDTO> childrenRaws = this.organisaatioRawService.getChildren(rawProvider.getOid());
            if (childrenRaws != null) {
                for (OrganisaatioRDTO curChild : childrenRaws) {
                    inheritOlTypes(provider, curChild);
                }
            }
            } catch (Exception ex) {
                LOG.error("children resources not found:" + rawProvider.getOid());
            }
        }
    }

    private Provider inheritMetadata(Provider child, Provider parent) {
        if (child.getDescription() == null) {
            child.setDescription(parent.getDescription());
        }
        if (child.getHealthcare() == null) {
            child.setHealthcare(parent.getHealthcare());
        }
        if (child.getAccessibility() == null) {
            child.setAccessibility(parent.getAccessibility());
        }
        if (child.getLivingExpenses() == null) {
            child.setLivingExpenses(parent.getLivingExpenses());
        }
        if (child.getLearningEnvironment() == null) {
            child.setLearningEnvironment(parent.getLearningEnvironment());
        }
        if (child.getDining() == null) {
            child.setDining(parent.getDining());
        }
        if (child.getSocial() == null) {
            child.setSocial(parent.getSocial());
        }
        if (child.getType() == null) {
            child.setType(parent.getType());
        }
        return child;
    }

    private boolean validate(Provider provider) {
        boolean valid = true;
        if (provider.getDescription() == null ||
                provider.getHealthcare() == null ||
                provider.getAccessibility() == null ||
                provider.getLivingExpenses() == null ||
                provider.getLearningEnvironment() == null ||
                provider.getDining() == null ||
                provider.getSocial() == null ||
                provider.getType() == null) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void clearCache() {
        providerMap = new HashMap<String,Provider>();   
    }


}
