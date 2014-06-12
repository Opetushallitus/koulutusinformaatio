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
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.ApplicationSystemCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.CreatorUtil;
import fi.vm.sade.koulutusinformaatio.service.impl.IncrementalUpdateServiceImpl;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalApplicationSystemIndexer {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalApplicationSystemIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private KoodistoService koodistoService;
    
    private IncrementalApplicationOptionIndexer aoIndexer;
    private IncrementalLOSIndexer losIndexer;
    
    public IncrementalApplicationSystemIndexer(TarjontaRawService tarjontaRawService, 
                                                EducationIncrementalDataQueryService dataQueryService, 
                                                KoodistoService koodistoService,
                                                IncrementalApplicationOptionIndexer aoIndexer,
                                                IncrementalLOSIndexer losIndexer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.koodistoService = koodistoService;
        this.aoIndexer = aoIndexer;
        this.losIndexer = losIndexer;
    }
    
    /**
     * Main method for indexing data based on application system changes
     */
    public void indexApplicationSystemData(String asOid) throws Exception {
        HakuDTO asDto = this.tarjontaRawService.getHaku(asOid);
        if (CreatorUtil.isSecondaryAS(asDto)) {
            indexSecondaryEducationAsData(asDto);
        } else {
            indexHigherEducationAsData(asOid);
        }
    }
    
    private void indexHigherEducationAsData(String asOid) throws Exception {
        LOG.debug("Indexing higher education application system");
        ResultV1RDTO<HakuV1RDTO> hakuRes = this.tarjontaRawService.getHigherEducationHakuByOid(asOid);
        if (hakuRes != null) {
            HakuV1RDTO asDto = hakuRes.getResult();
            List<String> lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());
            
            LOG.debug("Higher education loss in application system: " + lossesInAS.size());
            
            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

                ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService);
                ApplicationSystem as = asCreator.createHigherEdApplicationSystem(asDto);
                
                for (String curLosId : lossesInAS) {
                    HigherEducationLOS curLos = null;
                    try {
                        curLos = this.dataQueryService.getHigherEducationLearningOpportunity(curLosId);
                    } catch (ResourceNotFoundException ex) {
                        LOG.warn("higher education los not found");
                    }
                    if (curLos != null) {
                        this.reIndexAsDataForHigherEdLOS(curLos, asDto, as);
                        this.losIndexer.updateHigherEdLos(curLos);
                    }
                }
                
                if (lossesInAS.isEmpty()) {
                    
                    for (String curHakukohde : asDto.getHakukohdeOids()) {
                        this.aoIndexer.indexHigherEdAo(curHakukohde, !asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED));
                    }
                }
                
            } else {
                
                for (String curLosId : lossesInAS) {                
                    handleAsRemovalFromHigherEdLOS(curLosId, asDto);
                }
                
            }
        }
    }


    private void handleAsRemovalFromHigherEdLOS(String curLosId,
            HakuV1RDTO asDto) throws Exception {
        LOG.debug("Removing from higher ed: " + curLosId);
        HigherEducationLOS curLos = null;
        try {
            curLos = this.dataQueryService.getHigherEducationLearningOpportunity(curLosId);
            LOG.debug("Found los");
        } catch (ResourceNotFoundException ex) {
            LOG.warn("Los: " + curLosId + " not found");
            return;
        }
        if (curLos != null) {
            List<ApplicationOption> aos = new ArrayList<ApplicationOption>();
            boolean wasOtherAs = false;
            for (ApplicationOption curAo : curLos.getApplicationOptions()) {
                if (!curAo.getApplicationSystem().getId().equals(asDto.getOid())) {
                    LOG.debug("There was other application system: " + curAo.getApplicationSystem().getId());
                    wasOtherAs = true;
                    aos.add(curAo);
                }
            }
        
            curLos.setApplicationOptions(aos);
            if (wasOtherAs) {
                LOG.debug("Updating higher ed los: " + curLos.getId());
                this.losIndexer.updateHigherEdLos(curLos);
            } else {
                LOG.debug("Removing higher ed los: " + curLos.getId());
                this.losIndexer.removeHigherEd(curLos.getId(), curLos.getKomoOid());
                LOG.debug("Higher ed los: " + curLos.getId() + " removed!");
            }
        }
    }

    private void indexSecondaryEducationAsData(HakuDTO asDto) throws Exception {
        
        //Indexing application options connected to the changed application system

        List<String> lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());

        if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

            ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService);
            ApplicationSystem as = asCreator.createApplicationSystem(asDto);
            
            for (String curLosId : lossesInAS) {
                handleAsChangesInSeondaryLos(curLosId, as, asDto);
            }
            if (lossesInAS.isEmpty()) {
                List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asDto.getOid());
                if (hakukohdeOids != null && !hakukohdeOids.isEmpty()) {
                    for (OidRDTO curOid : hakukohdeOids) {
                        HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid.getOid());
                        this.aoIndexer.indexApplicationOptionData(aoDto, asDto);
                    }
                }
            }
        } else {
            for (String curLosId : lossesInAS) {                
                handleAsRemovalFromSecondaryLOS(curLosId, asDto);
            }
        }
    }
    
    private void handleAsChangesInSeondaryLos(String curLosId,
            ApplicationSystem as, HakuDTO asDto) throws ResourceNotFoundException, KoodistoException, IOException, SolrServerException {
        
        LOS curLos = this.dataQueryService.getLos(curLosId);
        if (curLos instanceof ChildLOS) {
            ParentLOS parent = this.dataQueryService.getParentLearningOpportunity(((ChildLOS) curLos).getParent().getId());
            reIndexAsDataForParentLOS(parent, asDto, as);
            this.losIndexer.updateParentLos(parent);

        } else if (curLos instanceof SpecialLOS) {
            reIndexAsDataForSpecialLOS((SpecialLOS)curLos, asDto, as);
            this.losIndexer.updateSpecialLos((SpecialLOS)curLos);
        } else if (curLos instanceof UpperSecondaryLOS) {
            reIndexAsDataForUpsecLOS((UpperSecondaryLOS)curLos, asDto, as);
            this.losIndexer.updateUpsecLos((UpperSecondaryLOS)curLos);
        }
        
    }

    private void handleAsRemovalFromSecondaryLOS(String curLosId, HakuDTO asDto) throws ResourceNotFoundException, TarjontaParseException, KoodistoException, IOException, SolrServerException {
        LOS curLos = this.dataQueryService.getLos(curLosId);
        if (curLos instanceof ChildLOS) {
            ParentLOS parent = this.dataQueryService.getParentLearningOpportunity(((ChildLOS) curLos).getParent().getId());
            reIndexParentLOSForRemovedAs(parent, asDto);
        } else if (curLos instanceof SpecialLOS) {
            reIndexSpecialLOSForRemovedAs((SpecialLOS)curLos, asDto);
        } else if (curLos instanceof UpperSecondaryLOS) {
            reIndexUpsecLOSForRemovedAs((UpperSecondaryLOS)curLos, asDto);
        }
    }

    private void reIndexUpsecLOSForRemovedAs(UpperSecondaryLOS curLos,
            HakuDTO asDto) throws IOException, SolrServerException {
        boolean wasOtherAs = false;
        List<UpperSecondaryLOI> lois = new ArrayList<UpperSecondaryLOI>();
        for (UpperSecondaryLOI curUpsecLoi : curLos.getLois()) {
            List<ApplicationOption> aos = new ArrayList<ApplicationOption>();
            for (ApplicationOption curAo : curUpsecLoi.getApplicationOptions()) {
                if (!curAo.getApplicationSystem().getId().equals(asDto.getOid())) {
                    wasOtherAs = true;
                    aos.add(curAo);
                }
            }
            if (!aos.isEmpty()) {
                curUpsecLoi.setApplicationOptions(aos);
                lois.add(curUpsecLoi);
            }
        }
        
        
        if (wasOtherAs) {
            curLos.setLois(lois);
            this.losIndexer.updateUpsecLos(curLos);
        } else {
            this.losIndexer.removeUpperSecondaryLOS(curLos);
        }
    }

    private void reIndexSpecialLOSForRemovedAs(SpecialLOS curLos, HakuDTO asDto) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        boolean wasOtherAs = false;
        //String komotoOid = null;
        List<ChildLOI> childLois = new ArrayList<ChildLOI>();
        for (ChildLOI curChildLoi : curLos.getLois()) {
            List<ApplicationOption> aos = new ArrayList<ApplicationOption>();
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (!curAo.getApplicationSystem().getId().equals(asDto.getOid())) {
                    wasOtherAs = true;
                    aos.add(curAo);
                }
            }
            if (!aos.isEmpty()) {
                curChildLoi.setApplicationOptions(aos);
                childLois.add(curChildLoi);
            }
        }
        
        if (wasOtherAs) {
            curLos.setLois(childLois);
            this.losIndexer.updateSpecialLos(curLos);
        } else {
            this.losIndexer.removeSpecialLOS(curLos);
        }
    }

    private void reIndexParentLOSForRemovedAs(ParentLOS parent, HakuDTO asDto) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        boolean wasOtherAs = false;
        
        List<ParentLOI> parentLois = new ArrayList<ParentLOI>();
        for (ParentLOI curLoi : parent.getLois()) {
            List<ApplicationOption> aos = new ArrayList<ApplicationOption>();
            for (ApplicationOption curAo : curLoi.getApplicationOptions()) {
                if (!curAo.getApplicationSystem().getId().equals(asDto.getOid())) {
                    wasOtherAs = true;
                    aos.add(curAo);
                }
            }
            if (!aos.isEmpty()) {
                curLoi.setApplicationOptions(aos);
                parentLois.add(curLoi);
            }
        }
       
        
        List<ChildLOS> children = new ArrayList<ChildLOS>();
        for (ChildLOS curChild : parent.getChildren()) {
            List<ChildLOI> childLois = new ArrayList<ChildLOI>();
            for (ChildLOI curChildLoi : curChild.getLois()) {
                List<ApplicationOption> childAos = new ArrayList<ApplicationOption>();
                for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                    if (!curAo.getId().equals(asDto.getOid())) {
                        wasOtherAs = true;
                        childAos.add(curAo);
                    }
                }
                if (!childAos.isEmpty()) {
                    curChildLoi.setApplicationOptions(childAos);
                    childLois.add(curChildLoi);
                }
            }
            if (!childLois.isEmpty()) {
                curChild.setLois(childLois);
                children.add(curChild);
            }
        }
        
        if (wasOtherAs) {
            parent.setLois(parentLois);
            parent.setChildren(children);
            this.losIndexer.updateParentLos(parent);
        } else {
            this.losIndexer.removeParentLOS(parent);
        }
    }

    private void reIndexAsDataForHigherEdLOS(HigherEducationLOS curLos,
            HakuV1RDTO asDto, ApplicationSystem as) {
        
     for (ApplicationOption curAo : curLos.getApplicationOptions()) {
         if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
             curAo.setApplicationSystem(as);
         }
     }
        
    }
    
    private void reIndexAsDataForUpsecLOS(UpperSecondaryLOS curLos,
            HakuDTO asDto, ApplicationSystem as) throws KoodistoException {
        for (UpperSecondaryLOI curUpsecLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curUpsecLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

    private void reIndexAsDataForSpecialLOS(SpecialLOS curLos, HakuDTO asDto,
            ApplicationSystem as) throws KoodistoException {

        for (ChildLOI curChildLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

    private void reIndexAsDataForParentLOS(ParentLOS parent, HakuDTO hakuDTO, ApplicationSystem as) throws KoodistoException {
        for (ParentLOI parentLoi : parent.getLois()) {
            for (ApplicationOption curAo : parentLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

}
