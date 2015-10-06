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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.DateRange;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.ApplicationSystemCreator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

/**
 * 
 * @author Markus
 *
 */
@Component
public class IncrementalApplicationSystemIndexer {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalApplicationSystemIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private KoodistoService koodistoService;
    private ParameterService parameterService;
    
    private IncrementalLOSIndexer losIndexer;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    @Autowired
    public IncrementalApplicationSystemIndexer(TarjontaRawService tarjontaRawService, 
                                                TarjontaService tarjontaService,
                                                EducationIncrementalDataQueryService dataQueryService, 
                                                KoodistoService koodistoService,
                                                ParameterService parameterService,
                                                IncrementalLOSIndexer losIndexer,
                                                IndexerService indexerService,
                                                HttpSolrServer loHttpSolrServer,
                                                HttpSolrServer lopHttpSolrServer,
                                                HttpSolrServer locationHttpSolrServer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.koodistoService = koodistoService;
        this.parameterService = parameterService;
        this.losIndexer = losIndexer;
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;
    }
    
    /**
     * Main method for indexing data based on application system changes
     */
    public void indexApplicationSystemData(String asOid) throws Exception {
        Set<String> koulutusToBeUpdated = Sets.newHashSet();
        koulutusToBeUpdated.addAll(tarjontaService.findKoulutusOidsByHaku(asOid)); // julkaistu koulutus tarjonnasta
        koulutusToBeUpdated.addAll(dataQueryService.getLearningOpportunityIdsByAS(asOid)); // jo valmiiksi indeksoitu koulutus

        for (String string : koulutusToBeUpdated) {
            losIndexer.indexKoulutusLos(string);
        }
        indexApplicationSystemForCalendar(asOid);
    }

    public void indexApplicationSystemForCalendar(String asOid) throws KoodistoException, SolrServerException, IOException {
        CalendarApplicationSystem calAS = this.tarjontaService.createCalendarApplicationSystem(asOid);
        loHttpSolrServer.deleteById(asOid);
        if (calAS != null) {
            this.indexerService.indexASToSolr(calAS, this.loHttpSolrServer);
        }
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }
    
    private void indexAdultUpsecAsData(String asOid) throws Exception {
        LOG.debug("Indexing higher education application system");
        ResultV1RDTO<HakuV1RDTO> hakuRes = this.tarjontaRawService.getV1EducationHakuByOid(asOid);
        
        if (hakuRes != null) {
            HakuV1RDTO asDto = hakuRes.getResult();
            List<String> lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());
            
            LOG.debug("Higher education loss in application system: {}", lossesInAS.size());
            
            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

                ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService, parameterService);
                ApplicationSystem as = asCreator.createApplicationSystem(asDto);
                
                for (String curLosId : lossesInAS) {
                    AdultUpperSecondaryLOS curLos = null;
                    try {
                        curLos = this.dataQueryService.getAdultUpsecLearningOpportunity(curLosId);
                    } catch (ResourceNotFoundException ex) {
                        LOG.warn("higher education los not found");
                    }
                    if (curLos != null) {
                        this.reIndexAsDataForKoulutusLOS(curLos, asDto, as);
                        this.losIndexer.updateAdultUpsecLos(curLos);;
                    }
                }
                
                if (lossesInAS.isEmpty()) {
                    
                    for (String curHakukohde : asDto.getHakukohdeOids()) {
                        HakukohdeV1RDTO aoDto = this.tarjontaRawService.getV1EducationHakukohde(curHakukohde).getResult();
                        for (String koulutusOid : aoDto.getHakukohdeKoulutusOids()) {
                            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
                                losIndexer.indexKoulutusLos(koulutusOid);
                            } else {
                                losIndexer.removeKoulutus(koulutusOid);
                            }
                        }
                    }
                }
                
            } else {
                
                for (String curLosId : lossesInAS) {                
                    handleAsRemovalFromHigherEdLOS(curLosId, asDto);
                }
                
            }
        }
    }
    
    private void indexHigherEducationAsData(String asOid) throws Exception {
        
        LOG.debug("Indexing higher education application system");
        ResultV1RDTO<HakuV1RDTO> hakuRes = this.tarjontaRawService.getV1EducationHakuByOid(asOid);
        
        if (hakuRes != null) {
            HakuV1RDTO asDto = hakuRes.getResult();
            List<String> lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());
            
            LOG.debug("Higher education loss in application system: {}", lossesInAS.size());
            
            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

                ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService, parameterService);
                ApplicationSystem as = asCreator.createApplicationSystem(asDto);
                
                for (String curLosId : lossesInAS) {
                    HigherEducationLOS curLos = null;
                    try {
                        curLos = this.dataQueryService.getHigherEducationLearningOpportunity(curLosId);
                    } catch (ResourceNotFoundException ex) {
                        LOG.warn("higher education los not found");
                    }
                    if (curLos != null) {
                        this.reIndexAsDataForKoulutusLOS(curLos, asDto, as);
                        this.losIndexer.updateHigherEdLos(curLos);
                    }
                }
                
                if (lossesInAS.isEmpty()) {
                    
                    for (String curHakukohde : asDto.getHakukohdeOids()) {
                        HakukohdeV1RDTO aoDto = this.tarjontaRawService.getV1EducationHakukohde(curHakukohde).getResult();
                        for (String koulutusOid : aoDto.getHakukohdeKoulutusOids()) {
                            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
                                losIndexer.indexKoulutusLos(koulutusOid);
                            } else {
                                losIndexer.removeKoulutus(koulutusOid);
                            }
                        }
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
        LOG.debug("Removing from higher ed: {}", curLosId);
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
                    LOG.debug("There was other application system: {}", curAo.getApplicationSystem().getId());
                    wasOtherAs = true;
                    aos.add(curAo);
                }
            }
        
            curLos.setApplicationOptions(aos);
            if (wasOtherAs) {
                LOG.debug("Updating higher ed los: {}", curLos.getId());
                this.losIndexer.updateHigherEdLos(curLos);
            } else {
                LOG.debug("Removing higher ed los: {}", curLos.getId());
                this.losIndexer.removeHigherEd(curLos.getId(), curLos.getKomoOid());
                LOG.debug("Higher ed los: " + curLos.getId() + " removed!");
            }
        }
    }

    private void indexSecondaryEducationAsData(HakuV1RDTO asDto) throws Exception {
        
        //Indexing application options connected to the changed application system

        List<String> lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());

        if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

            ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService, parameterService);
            ApplicationSystem as = asCreator.createApplicationSystem(asDto);
            
            for (String curLosId : lossesInAS) {
                handleAsChangesInSeondaryLos(curLosId, as, asDto);
            }
            if (lossesInAS.isEmpty()) {
                List<OidV1RDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asDto.getOid()).getResult();
                if (hakukohdeOids != null && !hakukohdeOids.isEmpty()) {
                    for (OidV1RDTO curOid : hakukohdeOids) {
                        HakukohdeV1RDTO aoDto = this.tarjontaRawService.getV1EducationHakukohde(curOid.getOid()).getResult();
                        boolean toRemove = !TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila());
                        for (String koulutusOid : aoDto.getHakukohdeKoulutusOids()) {
                            if (!toRemove) {
                                losIndexer.indexKoulutusLos(koulutusOid);
                            } else {
                                losIndexer.removeKoulutus(koulutusOid);
                            }
                        }
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
            ApplicationSystem as, HakuV1RDTO asDto) throws ResourceNotFoundException, KoodistoException, IOException, SolrServerException {
        
        LOS curLos = this.dataQueryService.getLos(curLosId);
        if (curLos instanceof ChildLOS) {
            // TODO: ammatillinen V1
        } else if (curLos instanceof SpecialLOS) {
            reIndexAsDataForSpecialLOS((SpecialLOS)curLos, asDto, as);
            this.losIndexer.updateSpecialLos((SpecialLOS) curLos);
        } else if (curLos instanceof UpperSecondaryLOS) {
            reIndexAsDataForUpsecLOS((UpperSecondaryLOS)curLos, asDto, as);
            this.losIndexer.updateUpsecLos((UpperSecondaryLOS) curLos);
        }
        
    }

    private void handleAsRemovalFromSecondaryLOS(String curLosId, HakuV1RDTO asDto) throws ResourceNotFoundException, TarjontaParseException, KoodistoException, IOException, SolrServerException {
        LOS curLos = this.dataQueryService.getLos(curLosId);
        if (curLos instanceof ChildLOS) {
            // TODO: ammatillinen V1
        } else if (curLos instanceof SpecialLOS) {
            reIndexSpecialLOSForRemovedAs((SpecialLOS)curLos, asDto);
        } else if (curLos instanceof UpperSecondaryLOS) {
            reIndexUpsecLOSForRemovedAs((UpperSecondaryLOS)curLos, asDto);
        }
    }

    private void reIndexUpsecLOSForRemovedAs(UpperSecondaryLOS curLos,
            HakuV1RDTO asDto) throws IOException, SolrServerException {
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

    private void reIndexSpecialLOSForRemovedAs(SpecialLOS curLos, HakuV1RDTO asDto) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
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

    private void reIndexAsDataForKoulutusLOS(KoulutusLOS curLos,
            HakuV1RDTO asDto, ApplicationSystem as) throws KoodistoException {
        
     for (ApplicationOption curAo : curLos.getApplicationOptions()) {
         if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
             curAo.setApplicationSystem(as);
             this.reIndexHakuaikaForKoulutusLOS(curAo, as, asDto);
         }
     }
        
    }
    
    private void reIndexHakuaikaForKoulutusLOS(ApplicationOption ao, ApplicationSystem as, HakuV1RDTO haku) throws KoodistoException {
        HakuaikaV1RDTO aoHakuaika = null;
    
        if (haku.getHakuaikas() != null) {
            for (HakuaikaV1RDTO ha  : haku.getHakuaikas()) {
                DateRange range = new DateRange();
                range.setStartDate(ha.getAlkuPvm());
                range.setEndDate(ha.getLoppuPvm());
                as.getApplicationDates().add(range);
            
                if (ha.getHakuaikaId().equals(ao.getInternalASDateRef())) {
                    aoHakuaika = ha;
                }
            
            }
        }
        // TODO: Yksinkertaistetaan ja mergetään tavalliseen indeksointiin.
        if (!ao.isSpecificApplicationDates() && (aoHakuaika != null)) {
            ao.setApplicationStartDate(aoHakuaika.getAlkuPvm());
            ao.setApplicationEndDate(aoHakuaika.getLoppuPvm());
            ao.setInternalASDateRef(aoHakuaika.getHakuaikaId());
            ao.setApplicationPeriodName(this.getI18nText(aoHakuaika.getNimet()));
        } else if (!ao.isSpecificApplicationDates() && haku.getHakuaikas() != null && !haku.getHakuaikas().isEmpty()) {
            ao.setApplicationStartDate(haku.getHakuaikas().get(0).getAlkuPvm());
            ao.setApplicationEndDate(haku.getHakuaikas().get(0).getLoppuPvm());
            ao.setInternalASDateRef(haku.getHakuaikas().get(0).getHakuaikaId());
        }
    }

    
    private void reIndexAsDataForUpsecLOS(UpperSecondaryLOS curLos,
            HakuV1RDTO asDto, ApplicationSystem as) throws KoodistoException {
        for (UpperSecondaryLOI curUpsecLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curUpsecLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                    this.reIndexHakuaikaForSecondaryLOS(curAo, asDto, as);
                }
            }
        }

    }

    private void reIndexAsDataForSpecialLOS(SpecialLOS curLos, HakuV1RDTO asDto,
            ApplicationSystem as) throws KoodistoException {

        for (ChildLOI curChildLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                    this.reIndexHakuaikaForSecondaryLOS(curAo, asDto, as);
                }
            }
        }

    }

    private void reIndexHakuaikaForSecondaryLOS(ApplicationOption ao, HakuV1RDTO asDto, ApplicationSystem as) {
        if (!ao.isSpecificApplicationDates()
                && asDto != null 
                && asDto.getHakuaikas() != null 
                && !asDto.getHakuaikas().isEmpty()) {
            HakuaikaV1RDTO aoHakuaika =  asDto.getHakuaikas().get(0);
            ao.setApplicationStartDate(aoHakuaika.getAlkuPvm());
            ao.setApplicationEndDate(aoHakuaika.getLoppuPvm());
            ao.setApplicationPeriodName(new I18nText(aoHakuaika.getNimet()));
        }
    }
    
    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

}
