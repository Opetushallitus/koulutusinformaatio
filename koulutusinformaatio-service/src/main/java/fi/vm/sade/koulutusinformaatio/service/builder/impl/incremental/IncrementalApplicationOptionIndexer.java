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
package fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.CreatorUtil;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalApplicationOptionIndexer {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalApplicationOptionIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    private IncrementalLOSIndexer losIndexer;
    
    public IncrementalApplicationOptionIndexer(TarjontaRawService tarjontaRawService, EducationIncrementalDataQueryService dataQueryService, EducationIncrementalDataUpdateService dataUpdateService, IncrementalLOSIndexer losIndexer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.losIndexer = losIndexer;
        
    }
    
    public void indexApplicationOptionData(HakukohdeDTO aoDto, HakuDTO asDto) throws Exception {


        if (CreatorUtil.isSecondaryAS(asDto)) {
            LOG.debug("Indexing  secondary ao");
            if (!TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila())) {
                removeApplicationOption(aoDto.getOid());
            } else {
                try {
                    ApplicationOption ao = this.dataQueryService.getApplicationOption(aoDto.getOid());
                    updateApplicationOption(ao, aoDto);
                } catch (ResourceNotFoundException ex) {
                    LOG.debug(ex.getMessage());
                    addApplicationOption(aoDto);
                }
            }
        } else if (CreatorUtil.isAdultUpperSecondaryAS(asDto)) {
            LOG.debug("Indexing  adult uppersecondary ao");
            this.indexAdultUpsecEdAo(aoDto.getOid(), !TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila()));
        } else {
            LOG.debug("Indexing  higher education ao");
            this.indexHigherEdAo(aoDto.getOid(), !TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila()));
        }

    }
    
    public void indexAdultUpsecEdAo(String aoOid, boolean toRemove) throws Exception {
       
        LOG.debug("Indexing adultupsec ed ao: " + aoOid);
        ResultV1RDTO<HakukohdeV1RDTO> aoRes = this.tarjontaRawService.getV1EducationHakukohode(aoOid);
        if (aoRes != null) {
            HakukohdeV1RDTO curAo = aoRes.getResult();
            if (curAo != null) {

                    ResultV1RDTO<List<NimiJaOidRDTO>> koulutusOidRes = this.tarjontaRawService.getHigherEducationByHakukohode(curAo.getOid());

                    if (koulutusOidRes != null && koulutusOidRes.getResult() != null) {
                        for (NimiJaOidRDTO curKoulOid : koulutusOidRes.getResult()) {
                            ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulOid.getOid());
                            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                                if (!toRemove) {
                                    LOG.debug("Indexing adult upsec komo: " + koulutusRes.getResult().getKomoOid());
                                    //if (koulutusRe)
                                    if (this.losIndexer.isAdultUpsecKomo(koulutusRes.getResult().getKomoOid())) {
                                        this.losIndexer.indexAdultUpsecKomo(koulutusRes.getResult().getKomoOid());
                                    } else {
                                        this.losIndexer.indexAdultVocationalKomoto(curKoulOid.getOid());
                                    }
                                } else {
                                    if (this.losIndexer.isAdultUpsecKomo(koulutusRes.getResult().getKomoOid())) {
                                        this.losIndexer.removeAdultUpsecEd(curKoulOid.getOid(), koulutusRes.getResult().getKomoOid());
                                    } else {
                                        this.losIndexer.removeAdultVocationalEd(curKoulOid.getOid(), koulutusRes.getResult().getKomoOid());
                                    }
                                }
                            }

                        }
                    }
                    
                    if (!curAo.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || toRemove) {
                        LOG.debug("Removing ao: " + curAo.getOid() + " with tila: " + curAo.getTila());
                        try {
                        ApplicationOption ao = this.dataQueryService.getApplicationOption(aoOid);
                        if (ao != null) {
                            this.dataUpdateService.deleteAo(ao);
                        }
                        } catch (ResourceNotFoundException ex) {
                            LOG.debug("Ao not found in mongo not doing anything");
                        }

                    }
                    
                }

            }
    }

    private void updateApplicationOption(ApplicationOption ao, HakukohdeDTO aoDto) throws KoodistoException, TarjontaParseException, SolrServerException, IOException {

        removeApplicationOption(ao.getId());
        addApplicationOption(aoDto);


    }
    
  //when adding an application option, the lois that are connected to it
    //need to be added or updated.
    private void addApplicationOption(HakukohdeDTO aoDto) throws KoodistoException {


        try {

            List<String> koulutusOids = aoDto.getHakukohdeKoulutusOids();
            for (String curKoulutusOid : koulutusOids) {

                KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(curKoulutusOid);

                //if komoto (loi) is in state published it needs to be added or updated
                if (TarjontaConstants.STATE_PUBLISHED.equals(komotoDto.getTila().name())) {
                    LOG.debug("komoto to add is published");
                    losIndexer.handleSecondaryLoiAddition(komotoDto);

                }

            }
        }
        catch (Exception ex) {
            LOG.error(String.format("Problem indexing application option: %s. %s", aoDto.getOid(), ex.getMessage()));
            throw new KoodistoException(ex.getMessage());
        }

    }
    
  //Handling removal of application option and related data
    private void removeApplicationOption(String oid) throws KoodistoException, TarjontaParseException, SolrServerException, IOException {

        LOG.debug("In remove application option");
        try {

            //Getting ao from mongo
            ApplicationOption ao = this.dataQueryService.getApplicationOption(oid);

            //Getting parent ref
            ParentLOSRef parentRef = ao.getParent();

            //If there is a parent ref, handling removal of parent data
            if (parentRef != null && parentRef.getId() != null) {
                LOG.debug("There is a parent ref in ao");
                LOS los  = this.dataQueryService.getLos(parentRef.getId());

                if (los != null && los instanceof ParentLOS) {
                    LOG.debug("The referenced los is ParentLOS");
                    this.losIndexer.handleParentLOSReferenceRemoval((ParentLOS)los);
                } else if (los != null && los instanceof SpecialLOS) {
                    LOG.debug("The referenced los is SpecialLOS");
                    this.losIndexer.handleSpecialLOSReferenceRemoval((SpecialLOS)los);
                } 
            }


            //If there are child loi refs, handling removal based on child references
            if (ao.getChildLOIRefs() != null
                    && !ao.getChildLOIRefs().isEmpty()) {
                LOG.debug("Handling childLOIRefs for ao");
                this.losIndexer.handleLoiRefRemoval(ao);
            }


            //If application option is not in mongo, nothing needs to be done    
        } catch (ResourceNotFoundException notFoundEx) {
            LOG.debug(notFoundEx.getMessage());
        }
    }
    
    public void indexHigherEdAo(String aoOid, boolean toRemove) throws Exception {

        ResultV1RDTO<HakukohdeV1RDTO> aoRes = this.tarjontaRawService.getV1EducationHakukohode(aoOid);
        if (aoRes != null) {
            HakukohdeV1RDTO curAo = aoRes.getResult();
            if (curAo != null) {

                    ResultV1RDTO<List<NimiJaOidRDTO>> koulutusOidRes = this.tarjontaRawService.getHigherEducationByHakukohode(curAo.getOid());

                    if (koulutusOidRes != null && koulutusOidRes.getResult() != null) {
                        for (NimiJaOidRDTO curKoulOid : koulutusOidRes.getResult()) {
                            ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulOid.getOid());
                            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                                if (!toRemove) {
                                    this.losIndexer.indexHigherEdKomo(koulutusRes.getResult().getKomoOid());
                                } else {
                                    this.losIndexer.removeHigherEd(curKoulOid.getOid(), koulutusRes.getResult().getKomoOid());
                                }
                            }

                        }
                    }
                    
                    if (!curAo.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || toRemove) {
                        LOG.debug("Removing ao: " + curAo.getOid() + " with tila: " + curAo.getTila());
                        try {
                        ApplicationOption ao = this.dataQueryService.getApplicationOption(aoOid);
                        if (ao != null) {
                            this.dataUpdateService.deleteAo(ao);
                        }
                        } catch (ResourceNotFoundException ex) {
                            LOG.debug("Ao not found in mongo not doing anything");
                        }

                    }
                    
                }

            }

    }
}
