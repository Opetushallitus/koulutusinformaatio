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

import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalKoulutusLOSIndexer {

    public static final Logger LOG = LoggerFactory.getLogger(IncrementalKoulutusLOSIndexer.class);

    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private IndexerService indexerService;

    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    private EducationIncrementalDataQueryService dataQueryService;

    public IncrementalKoulutusLOSIndexer(TarjontaService tarjontaService,
            EducationIncrementalDataUpdateService dataUpdateService,
            EducationIncrementalDataQueryService dataQueryService,
            IndexerService indexerService,
            HttpSolrServer loHttpSolrServer,
            HttpSolrServer lopHttpSolrServer,
            HttpSolrServer locationHttpSolrServer) {

        this.tarjontaService = tarjontaService;
        this.dataUpdateService = dataUpdateService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.dataQueryService = dataQueryService;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;

    }

    public void indexKoulutusLOS(String koulutusOid) throws Exception {
        KoulutusLOS createdLos = null;

        try {
            createdLos = this.tarjontaService.createKoulutusLOS(koulutusOid, true);
        } catch (TarjontaParseException tpe) {
            createdLos = null;
        }

        LOG.debug("Created los");

        if (createdLos == null) {
            LOG.debug("Created los is to be removed");
            removeKoulutusLOS(koulutusOid);
        } else {
            this.indexToSolr(createdLos);
            this.dataUpdateService.updateKoulutusLos(createdLos);
        }
    }

    private void indexToSolr(LOS createdLos) throws IOException, SolrServerException {
        LOG.debug("Indexing los: {}", createdLos.getId());
        LOG.debug("Indexing los: {}", createdLos.getName().get("fi"));
        this.indexerService.removeLos(createdLos, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(createdLos, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    public void removeKoulutusLOS(String oid)  throws Exception {
        loHttpSolrServer.deleteById(oid);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        KoulutusLOS toDeleteLos = new KoulutusLOS();
        toDeleteLos.setId(oid);
        this.dataUpdateService.deleteLos(toDeleteLos);
    }

    public void removeTutkintoLOS(String oid) throws Exception {
        try {
            TutkintoLOS curLos = this.dataQueryService.getTutkinto(oid);
            curLos.getChildEducations();
            for (KoulutusLOS komoto : curLos.getChildEducations()) {
                removeKoulutusLOS(komoto.getId());
            }
            TutkintoLOS toDeleteLos = new TutkintoLOS();
            toDeleteLos.setId(oid);
            this.dataUpdateService.deleteLos(toDeleteLos);
        } catch (ResourceNotFoundException e) {
            LOG.debug("There was no existing Tutkinto to be removed: {}", oid);
        }
    }

    public void indexAmmatillinenKoulutusKomoto(KoulutusHakutulosV1RDTO dto) throws Exception {
        List<KoulutusLOS> loses = tarjontaService.createAmmatillinenKoulutusLOS(dto);
        removeTutkintoLOS(dto.getKomoOid());
        if (!loses.isEmpty()) {
            for (KoulutusLOS los : loses) {
                if (los.isOsaamisalaton()) {
                    LOG.debug("Updated osaamisalaton los {}", los.getId());
                    this.indexToSolr(los);
                    this.dataUpdateService.updateKoulutusLos(los);
                } else {
                    LOG.debug("Updated los {}", los.getId());
                    this.dataUpdateService.updateKoulutusLos(los);
                }
            }
            if (loses.get(0).getTutkinto() != null) {
                this.indexToSolr(loses.get(0).getTutkinto());
                this.dataUpdateService.updateTutkintoLos(loses.get(0).getTutkinto());
            }
        }
    }

    public void indexLukioKoulutusKomoto(KoulutusHakutulosV1RDTO dto) throws Exception {
        KoulutusLOS los = tarjontaService.createLukioKoulutusLOS(dto);
        if (los == null) {
            LOG.debug("Created los is to be removed");
            removeKoulutusLOS(dto.getOid());
        } else {
            LOG.debug("Updated los {}", los.getId());
            this.indexToSolr(los);
            this.dataUpdateService.updateKoulutusLos(los);
        }
    }

    public void indexValmistavaKoulutusKomoto(String curKomotoOid) throws Exception {
        LOG.debug("Indexing koulutus ed komoto: {}", curKomotoOid);
        KoulutusLOS createdLos = null;
        try {
            createdLos = this.tarjontaService.createKoulutusLOS(curKomotoOid, true);
        } catch (TarjontaParseException tpe) {
            createdLos = null;
        }
        if (createdLos == null) {
            LOG.debug("Created los is to be removed");
            removeKoulutusLOS(curKomotoOid);
        } else {
            LOG.debug("Updated los {}", createdLos.getId());
            this.indexToSolr(createdLos);
            this.dataUpdateService.updateKoulutusLos(createdLos);
        }


    }


}
