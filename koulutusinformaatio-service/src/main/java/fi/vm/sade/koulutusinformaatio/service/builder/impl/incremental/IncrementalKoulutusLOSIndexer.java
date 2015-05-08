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
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.StandaloneLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalKoulutusLOSIndexer {

    public static final Logger LOG = LoggerFactory.getLogger(IncrementalKoulutusLOSIndexer.class);

    private TarjontaRawService tarjontaRawService;
    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private EducationIncrementalDataQueryService dataQueryService;
    private IndexerService indexerService;

    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    public IncrementalKoulutusLOSIndexer(TarjontaRawService tarjontaRawService, 
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

    public void indexKoulutusKomo(String curKomoOid) throws Exception {
        if (this.indexerService.hasAlreadyProcessedKomo(curKomoOid)) {
            return;
        }
        this.indexerService.addProcessedKomo(curKomoOid);

        LOG.debug("Indexing adult upper secondary ed komo: " + curKomoOid);

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> koulutusRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);

        if (koulutusRes != null 
                && koulutusRes.getResult() != null 
                && koulutusRes.getResult().getTulokset() != null 
                && !koulutusRes.getResult().getTulokset().isEmpty()) {


            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> tarjResult :  koulutusRes.getResult().getTulokset()) {
                if (tarjResult.getTulokset() !=  null && !tarjResult.getTulokset().isEmpty()) {
                    for (KoulutusHakutulosV1RDTO curKoul : tarjResult.getTulokset()) {

                        LOG.debug("Now indexing koulutus education: " + curKoul.getOid());

                        StandaloneLOS createdLos = null;

                        try {
                            createdLos = this.tarjontaService.createKoulutusLOS(curKoul.getOid(), true);
                        } catch (TarjontaParseException tpe) {
                            createdLos = null;
                        }

                        LOG.debug("Created los");

                        if (createdLos == null) {
                            LOG.debug("Created los is to be removed");
                            removeKoulutusLOS(curKoul.getOid());
                            continue;
                        } else {
                            this.indexToSolr(createdLos);
                            this.dataUpdateService.updateKoulutusLos(createdLos);
                        }
                    }
                }
            }
        }
    }

    private void indexToSolr(StandaloneLOS createdLos) throws IOException, SolrServerException {
        LOG.debug("Indexing adult upper secondary ed: " + createdLos.getId());
        LOG.debug("Indexing adult upper secondary ed: " + createdLos.getShortTitle());
        this.indexerService.removeLos(createdLos, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(createdLos, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    public void removeKoulutusLOS(String oid)  throws Exception {
        loHttpSolrServer.deleteById(oid);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        StandaloneLOS toDeleteLos = new StandaloneLOS();
        toDeleteLos.setId(oid);
        this.dataUpdateService.deleteLos(toDeleteLos);
    }


    public void indexKoulutusKomoto(String curKomotoOid) throws Exception {
        LOG.debug("Indexing koulutus ed komoto: " + curKomotoOid);

        StandaloneLOS createdLos = null;

        try {
            createdLos = this.tarjontaService.createKoulutusLOS(curKomotoOid, true);
        } catch (TarjontaParseException tpe) {
            createdLos = null;
        }

        LOG.debug("Created los");

        if (createdLos == null) {
            LOG.debug("Created los is to be removed");
            removeKoulutusLOS(curKomotoOid);
        } else {
            this.indexToSolr(createdLos);
            this.dataUpdateService.updateKoulutusLos(createdLos);
        }


    }


}
