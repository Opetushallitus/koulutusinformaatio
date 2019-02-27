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
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.OrganisaatioException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderServiceImpl.class);

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
    public Provider getByOID(String oid) throws OrganisaatioException {
        try {
            LOG.debug("Fetching provider with oid {}", oid);

            Provider cachedProvider = providerMap.get(oid);
            if (cachedProvider != null) {
                LOG.debug("Returning provider from cache");
                return cachedProvider;
            }
            OrganisaatioRDTO organisaatioRDTO = organisaatioRawService.getOrganisaatio(oid);

            Provider provider = conversionService.convert(organisaatioRDTO, Provider.class);
            if (!validate(provider) && !Strings.isNullOrEmpty(organisaatioRDTO.getParentOid())) {

                LOG.debug("Enriching provider {} with parent provider {}", organisaatioRDTO.getOid(), organisaatioRDTO.getParentOid());

                Provider parent = getByOID(organisaatioRDTO.getParentOid());
                LOG.debug("Got parent provider");

                provider = inheritMetadata(provider, parent);
            }

            if (provider.getType() == null) {
                inheritOlTypes(provider, organisaatioRDTO);
            }
            if (provider.getType() != null) {
                provider.getOlTypes().add(provider.getType());
            }
            if (provider.getOlTypes() != null) {
                for (Code curOlType : provider.getOlTypes()) {
                    List<Code> olFacets = null;
                        olFacets = this.koodistoService.searchSuperCodes(curOlType.getUri(), KOODISTO_OPPILAITOSTYYPPIFASETTI);
                    provider.getOlTypeFacets().addAll(olFacets);
                }
            }
            if (organisaatioRDTO.getTyypit() != null && organisaatioRDTO.getTyypit().contains(ORG_TYPE_OPPISOPIMUSTOIMIPISTE)) {
                Code opSopToim = koodistoService.searchFirst(OPPILAITOSTYYPPIFASETT_OPPISOPIMUS);
                provider.getOlTypeFacets().add(opSopToim);
            }


            providerMap.put(oid, provider);

            return provider;
        } catch (KoodistoException e) {
            throw new OrganisaatioException("Koodisto failed during organisaatio search", e);
        }
    }
    
    

    private void inheritOlTypes(Provider provider, OrganisaatioRDTO rawProvider) throws OrganisaatioException {
        try{
            if (rawProvider.getTyypit().contains(ORG_TYPE_OPPILAITOS)) {
                Code olTyyppi = koodistoService.searchFirst(rawProvider.getOppilaitosTyyppiUri());
                if (olTyyppi != null) {
                    provider.getOlTypes().add(olTyyppi);
                }
            }
            else if (rawProvider.getTyypit().contains(ORG_TYPE_TOIMIPISTE)) {
                OrganisaatioRDTO inheritableOrg = this.organisaatioRawService.getOrganisaatio(rawProvider.getParentOid());
                inheritOlTypes(provider, inheritableOrg);
            }
        } catch (KoodistoException e) {
            throw new OrganisaatioException("Koodisto failed during organisaatio search", e);
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

    @Override
    public List<OrganisaatioPerustieto> fetchOpplaitokset() {
        List<OrganisaatioPerustieto> resOrgs = new ArrayList<OrganisaatioPerustieto>();
        
        OrganisaatioHakutulos result = this.organisaatioRawService.fetchOrganisaatiosByType(ORG_TYPE_OPPILAITOS);
        if (result != null && result.getOrganisaatiot() != null) {
            
            for (OrganisaatioPerustieto curOrg : result.getOrganisaatiot()) {
                String olTyyppi = curOrg.getOppilaitostyyppi();
                if (olTyyppi != null) {
                    try {
                        List<Code> olFacets = this.koodistoService.searchSuperCodes(olTyyppi, KOODISTO_OPPILAITOSTYYPPIFASETTI);
                        if (olFacets != null && !olFacets.isEmpty()) {
                            resOrgs.add(curOrg);
                        }
                    } catch (KoodistoException ex) {
                        LOG.warn("Problem checking oppilaitostyyppifasetti for: " + curOrg.getOid(), ex);
                        continue;
                    }
                }
            }
            
        }
        return resOrgs;
    }

    
    @Override
    public List<OrganisaatioPerustieto> fetchToimipisteet() {
        OrganisaatioHakutulos result = this.organisaatioRawService.fetchOrganisaatiosByType(ORG_TYPE_TOIMIPISTE);
        if (result != null && result.getOrganisaatiot() != null) {
            return result.getOrganisaatiot();
        }
        return new ArrayList<>();
    }

    @Override
    public List<OrganisaatioPerustieto> fetchOppisopimusToimipisteet() {
        OrganisaatioHakutulos result = this.organisaatioRawService.fetchOrganisaatiosByType(ORG_TYPE_OPPISOPIMUSTOIMIPISTE);
        if (result != null && result.getOrganisaatiot() != null) {
            return result.getOrganisaatiot();
        }
        return new ArrayList<>();
    }

    @Override
    public String getOppilaitosTyyppiByOID(String oid) throws OrganisaatioException {

        List<OrganisaatioPerustieto> organisaatiot = organisaatioRawService.findOrganisaatio(oid).getOrganisaatiot();
        if (CollectionUtils.isEmpty(organisaatiot))
            throw new OrganisaatioException("Organisaatiota " + oid + " ei löytynyt!");
        OrganisaatioPerustieto tulos = organisaatiot.get(0);
        String oppilaitosTyyppi = getOppilaitosTyyppi(tulos);
        if (oppilaitosTyyppi != null)
            return oppilaitosTyyppi;
        throw new OrganisaatioException("Organisaatiolla " + oid + " ei ollut oppilaitostyyppiä!");
    }

    private Set<String> validOppilaitosTyyppis = Sets.newHashSet(OPPILAITOSTYYPPI_AMK, OPPILAITOSTYYPPI_YLIOPISTO, OPPILAITOSTYYPPI_SOTILASKK);
    private String getOppilaitosTyyppi(OrganisaatioPerustieto tulos) {
        String oppilaitostyyppiUri = Strings.nullToEmpty(tulos.getOppilaitostyyppi()).split("#")[0];
        if (validOppilaitosTyyppis.contains(oppilaitostyyppiUri)) {
            return oppilaitostyyppiUri;
        }
        for (OrganisaatioPerustieto organisaatioPerustieto : tulos.getChildren()) {
            String childsOppilaitosTyyppi = getOppilaitosTyyppi(organisaatioPerustieto);
            if (childsOppilaitosTyyppi != null)
                return childsOppilaitosTyyppi;
        }
        return null;
    }

}
