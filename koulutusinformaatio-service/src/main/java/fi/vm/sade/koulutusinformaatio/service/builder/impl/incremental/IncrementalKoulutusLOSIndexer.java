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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalKoulutusLOSIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(IncrementalKoulutusLOSIndexer.class);

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

    private void indexToSolr(LOS createdLos) throws IOException, SolrServerException {
        LOG.debug("Indexing los: {}", createdLos.getId());
        LOG.debug("Indexing los: {}", createdLos.getName().get("fi"));
        this.indexerService.removeLos(createdLos, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(createdLos, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    private void removeKoulutusLOS(String oid) throws Exception {
        loHttpSolrServer.deleteById(oid);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        KoulutusLOS toDeleteLos = new KoulutusLOS();
        toDeleteLos.setId(oid);
        this.dataUpdateService.deleteLos(toDeleteLos);
    }

    private void removeTutkintoLOS(String oid) throws Exception {
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
        ToteutustyyppiEnum toteutusTyyppi = dto.getToteutustyyppiEnum();
        String tarjoaja = dto.getTarjoajat().get(0);
        String koulutusKoodi = dto.getKoulutuskoodi().split("#")[0];
        List<KoulutusHakutulosV1RDTO> dtosToBeUpdated = tarjontaService.findKoulutus(toteutusTyyppi.name(), tarjoaja, koulutusKoodi);
        List<KoulutusLOS> losses = new ArrayList<KoulutusLOS>();
        for (KoulutusHakutulosV1RDTO koulutusHakutulosV1RDTO : dtosToBeUpdated) {
            List<KoulutusLOS> result = tarjontaService.createAmmatillinenKoulutusLOS(koulutusHakutulosV1RDTO);
            losses.addAll(result);
        }

        Set<String> tutkintoOidsToBeRemoved = new HashSet<String>();
        Set<String> koulutusOidsToBeRemoved = new HashSet<String>();
        List<KoulutusLOS> lossesToBeRemoved = dataQueryService.getKoulutusLos(toteutusTyyppi, tarjoaja, koulutusKoodi);
        for (KoulutusLOS los : lossesToBeRemoved) {
            if (los.getTutkinto() != null) {
                tutkintoOidsToBeRemoved.add(los.getTutkinto().getId());
            } else {
                koulutusOidsToBeRemoved.add(los.getId());
            }
        }
        for (String oid : tutkintoOidsToBeRemoved) {
            removeTutkintoLOS(oid);
        }
        for (String oid : koulutusOidsToBeRemoved) {
            removeKoulutusLOS(oid);
        }

        Set<String> updatedTutkintos = Sets.newHashSet();
        for (KoulutusLOS los : losses) {
            this.dataUpdateService.updateKoulutusLos(los);
            if (los.getTutkinto() == null) {
                indexToSolr(los);
            } else if (!updatedTutkintos.contains(los.getTutkinto().getId())) {
                indexToSolr(los.getTutkinto());
                dataUpdateService.updateTutkintoLos(los.getTutkinto());
                updatedTutkintos.add(los.getTutkinto().getId());
            }
        }
    }

    public void indexSingleKoulutusWithoutRelations(KoulutusHakutulosV1RDTO dto) throws Exception {
        KoulutusLOS los = tarjontaService.createKoulutusLOS(dto.getOid(), true);
        if (los == null) {
            removeKoulutusLOS(dto.getOid());
        } else {
            this.indexToSolr(los);
            this.dataUpdateService.updateKoulutusLos(los);
        }
    }

    public void indexKorkeakouluopintoKomoto(KoulutusHakutulosV1RDTO dto) throws Exception {
        KoulutusLOS los = tarjontaService.createKorkeakouluopinto(dto);

        KoulutusLOS losToRemove = (KoulutusLOS) dataQueryService.getLos(dto.getOid());
        if (losToRemove != null) {
            removeKorkeakouluOpintoAndRelatives(losToRemove);
        }

        this.indexToSolr(los);
        this.dataUpdateService.updateKoulutusLos(los);

        for (KoulutusLOS child : los.getOpintojaksos()) {
            this.indexToSolr(child);
            this.dataUpdateService.updateKoulutusLos(child);
        }
    }

    private void removeKorkeakouluOpintoAndRelatives(KoulutusLOS los) {
        if (los.getOpintokokonaisuus() != null) {
            removeKorkeakouluOpintoAndRelatives(los.getOpintokokonaisuus());
        } else {
            for (KoulutusLOS child : los.getOpintojaksos()) {
                dataUpdateService.deleteLos(child);
            }
            dataUpdateService.deleteLos(los);
        }

    }
}
