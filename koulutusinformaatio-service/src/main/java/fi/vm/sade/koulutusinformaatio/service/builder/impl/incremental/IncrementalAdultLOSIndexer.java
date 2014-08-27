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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalAdultLOSIndexer {
    
  public static final Logger LOG = LoggerFactory.getLogger(IncrementalAdultLOSIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private EducationIncrementalDataQueryService dataQueryService;
    private IndexerService indexerService;
    
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    public IncrementalAdultLOSIndexer(TarjontaRawService tarjontaRawService, 
                                                TarjontaService tarjontaService,
                                                EducationIncrementalDataUpdateService dataUpdateService,
                                                EducationIncrementalDataQueryService dataQueryService,
                                                IndexerService indexerService,
                                                HttpSolrServer loHttpSolrServer,
                                                HttpSolrServer lopHttpSolrServer,
                                                HttpSolrServer locationHttpSolrServer) {
        
        this.tarjontaRawService = tarjontaRawService;
        this.tarjontaService = tarjontaService;
        this.dataUpdateService = dataUpdateService;
        this.dataQueryService = dataQueryService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;

    }
    
    public void indexAdultUpsecKomo(String curKomoOid) throws Exception {

        LOG.debug("Indexing adult upper secondary ed komo: " + curKomoOid);

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> adultUpsecEdRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);
        //higherEdRes.getResult().getTulokset().

        if (adultUpsecEdRes != null 
                && adultUpsecEdRes.getResult() != null 
                && adultUpsecEdRes.getResult().getTulokset() != null 
                && !adultUpsecEdRes.getResult().getTulokset().isEmpty()) {


            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> tarjResult :  adultUpsecEdRes.getResult().getTulokset()) {
                if (tarjResult.getTulokset() !=  null && !tarjResult.getTulokset().isEmpty()) {
                    for (KoulutusHakutulosV1RDTO curKoul : tarjResult.getTulokset()) {
                        
                        if (!curKoul.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS) 
                                || curKoul.getKoulutuslajiUri() == null 
                                || curKoul.getKoulutuslajiUri().contains(TarjontaConstants.NUORTEN_KOULUTUS)) {
                            continue;
                        }
                        
                        LOG.debug("Now indexing adult upper secondary education: " + curKoul.getOid());
                        
                        AdultUpperSecondaryLOS createdLos = null;
                        
                        
                        try {
                            createdLos = this.tarjontaService.createAdultUpperSecondaryLOS(curKoul.getOid(), true);//createHigherEducationLearningOpportunityTree(curKoul.getOid());
                        } catch (TarjontaParseException tpe) {
                            createdLos = null;
                        }

                        LOG.debug("Created los");

                        if (createdLos == null) {
                            LOG.debug("Created los is to be removed");
                            removeAdultUpsecEd(curKoul.getOid(), curKomoOid);
                            continue;
                        } else {
                            this.indexToSolr(createdLos);
                            this.dataUpdateService.updateAdultUpsecLos(createdLos);
                        }
                        
                        
                    }
                }
            }
        }
    }

    private void indexToSolr(AdultUpperSecondaryLOS curLOS) throws IOException, SolrServerException {
        LOG.debug("Indexing higher ed: " + curLOS.getId());
        LOG.debug("Indexing higher ed: " + curLOS.getShortTitle());
        this.indexerService.removeLos(curLOS, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(curLOS, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    public void removeAdultUpsecEd(String oid, String curKomoOid)  throws Exception {
        
        LOS existingLos = this.dataQueryService.getLos(oid);
        if (existingLos != null && existingLos instanceof AdultUpperSecondaryLOS) {
            AdultUpperSecondaryLOS existingAdultUpsecEd = (AdultUpperSecondaryLOS)existingLos;
           
            this.indexerService.removeLos(existingAdultUpsecEd, loHttpSolrServer);
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            this.dataUpdateService.deleteLos(existingAdultUpsecEd);

        }
    }

    public void updateAdultUpsecLos(AdultUpperSecondaryLOS los) throws Exception {
        
        this.removeAdultUpsecEd(los.getId(), los.getKomoOid());
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        this.dataUpdateService.save(los);
        this.indexerService.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
       
    }

}
