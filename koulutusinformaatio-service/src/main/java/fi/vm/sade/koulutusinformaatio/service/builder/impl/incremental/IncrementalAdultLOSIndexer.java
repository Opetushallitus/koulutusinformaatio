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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.koulutusinformaatio.domain.exception.KISolrException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.CompetenceBasedQualificationParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalAdultLOSIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(IncrementalAdultLOSIndexer.class);

    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private IndexerService indexerService;

    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    public IncrementalAdultLOSIndexer(TarjontaService tarjontaService,
            EducationIncrementalDataUpdateService dataUpdateService,
            IndexerService indexerService,
            HttpSolrServer loHttpSolrServer,
            HttpSolrServer lopHttpSolrServer,
            HttpSolrServer locationHttpSolrServer) {

        this.tarjontaService = tarjontaService;
        this.dataUpdateService = dataUpdateService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;

    }

    private void indexToSolr(CompetenceBasedQualificationParentLOS curLOS) throws KISolrException {
        LOG.debug("Indexing adult vocational ed: {}", curLOS.getId());
        LOG.debug("Indexing adult vocational ed: {}", curLOS.getShortTitle());
        this.indexerService.removeLos(curLOS, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(curLOS, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    private void removeAdultVocationalEd(String oid) throws IOException, SolrServerException, KISolrException {
        loHttpSolrServer.deleteById(oid);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        CompetenceBasedQualificationParentLOS toDeleteLos = new CompetenceBasedQualificationParentLOS();
        toDeleteLos.setId(oid);
        this.dataUpdateService.deleteLos(toDeleteLos);

    }

    public void indexAdultVocationalKomoto(String curKomoOid) throws KISolrException {
        LOG.debug("Indexing adult vocational ed komo: {}", curKomoOid);

        CompetenceBasedQualificationParentLOS createdLos = null;

        try {
            createdLos = this.tarjontaService.createCBQPLOS(curKomoOid, true);//createKoulutusLOS(curKoul.getOid(), true);//createHigherEducationLearningOpportunityTree(curKoul.getOid());

        } catch (TarjontaParseException | ResourceNotFoundException | KoodistoException tpe) {
            createdLos = null;
        }

        LOG.debug("Created los");

        if (createdLos == null) {
            LOG.debug("Created los is to be removed");
            try {
                removeAdultVocationalEd(curKomoOid);
            } catch (IOException | SolrServerException | KISolrException e) {
                throw new KISolrException(e);
            }
        } else {

            for (ApplicationOption curAo : createdLos.getApplicationOptions()) {
                Set<HigherEducationLOSRef> refs = new HashSet<>();
                refs.add(tarjontaService.createAdultVocationalLosRef(createdLos, curAo));
                curAo.setHigherEdLOSRefs(refs);
            }

            this.indexToSolr(createdLos);
            this.dataUpdateService.updateAdultVocationalLos(createdLos);//updateAdultUpsecLos(createdLos);
        }


    }


}
