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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
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
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalHigherEducationLOSIndexer {


    public static final Logger LOG = LoggerFactory.getLogger(IncrementalHigherEducationLOSIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private EducationIncrementalDataQueryService dataQueryService;
    private IndexerService indexerService;
    
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    public IncrementalHigherEducationLOSIndexer(TarjontaRawService tarjontaRawService, 
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
    
    public void indexHigherEdKomo(String curKomoOid) throws Exception {

        LOG.debug("Indexing higher ed komo: " + curKomoOid);

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> higherEdRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);
        //higherEdRes.getResult().getTulokset().

        if (higherEdRes != null 
                && higherEdRes.getResult() != null 
                && higherEdRes.getResult().getTulokset() != null 
                && !higherEdRes.getResult().getTulokset().isEmpty()) {


            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> tarjResult :  higherEdRes.getResult().getTulokset()) {
                if (tarjResult.getTulokset() !=  null && !tarjResult.getTulokset().isEmpty()) {
                    for (KoulutusHakutulosV1RDTO curKoul : tarjResult.getTulokset()) {
                        
                        if (!curKoul.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.KORKEAKOULUTUS)) {
                            continue;
                        }
                        
                        LOG.debug("Now indexing higher education: " + curKoul.getOid());

                        HigherEducationLOS createdLos = null;
                        try {
                            createdLos = this.tarjontaService.createHigherEducationLearningOpportunityTree(curKoul.getOid());
                        } catch (TarjontaParseException tpe) {
                            createdLos = null;
                        }

                        LOG.debug("Created los");

                        if (createdLos == null) {
                            LOG.debug("Created los is to be removed");
                            removeHigherEd(curKoul.getOid(), curKomoOid);
                            continue;
                        }

                        LOG.debug("Doing family adjustements");

                        List<HigherEducationLOS> parentEds = fetchParentsOfLos(curKomoOid);


                        createdLos.setParents(parentEds);

                        for (HigherEducationLOS curParent : parentEds) {
                            boolean wasCreatedInSiblings = false;
                            List<HigherEducationLOS> siblings = curParent.getChildren();
                            if (siblings != null) {
                                for (HigherEducationLOS curSibling : siblings) {
                                    if (curSibling.getId().equals(createdLos.getId())) {
                                        wasCreatedInSiblings = true;
                                    }
                                }
                            }
                            if (!wasCreatedInSiblings) {
                                curParent.getChildren().add(createdLos);
                            }
                        }

                        List<HigherEducationLOS> orphanedChildren = getOrphanedChildren(createdLos);

                        for (HigherEducationLOS curOrphan : orphanedChildren) {
                            LOG.debug("Saving orphan: " + curOrphan.getId());
                            this.indexToSolr(curOrphan);
                            this.dataUpdateService.updateHigherEdLos(curOrphan);
                        }


                        if (parentEds != null && !parentEds.isEmpty()) {
                            for (HigherEducationLOS curParent : parentEds) {
                                LOG.debug("Saving parent: " + curParent.getId());
                                this.indexToSolr(curParent);
                                this.dataUpdateService.updateHigherEdLos(curParent);
                            }
                        } else {
                            LOG.debug("Saving actual los: " + createdLos.getId());
                            this.indexToSolr(createdLos);
                            this.dataUpdateService.updateHigherEdLos(createdLos);
                        }


                    }
                }
            }

        }   
    }
    

    public void updateHigherEdLos(HigherEducationLOS los) throws Exception {
        
        this.removeHigherEd(los.getId(), los.getKomoOid());
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        this.dataUpdateService.save(los);
        this.indexerService.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        
    }
    
    public void removeHigherEd(String educationOid, String curKomoOid) throws Exception {


        LOS existingLos = this.dataQueryService.getLos(educationOid);
        if (existingLos != null && existingLos instanceof HigherEducationLOS) {
            HigherEducationLOS existingHigherEd = (HigherEducationLOS)existingLos;
            if (existingHigherEd.getParents() != null) {
                for (HigherEducationLOS curParent : existingHigherEd.getParents()) {
                    pruneParent(curParent, educationOid);
                }
            }
            if (existingHigherEd.getChildren() != null) {
                for (HigherEducationLOS curChild : existingHigherEd.getChildren()) {
                    pruneChild(curChild, educationOid);
                }
            }
            this.indexerService.removeLos(existingHigherEd, loHttpSolrServer);
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            this.dataUpdateService.deleteLos(existingHigherEd);

        }

    }

    private void pruneChild(HigherEducationLOS curChild, String educationOid) throws Exception {

        if (curChild.getParents() != null) {
            List<HigherEducationLOS> remainingParents = new ArrayList<HigherEducationLOS>();
            for (HigherEducationLOS curParent : curChild.getParents()) {
                if (!curParent.getId().equals(educationOid)) {
                    remainingParents.add(curParent);
                }
            }
            curChild.setParents(remainingParents);
            this.indexToSolr(curChild);
            this.dataUpdateService.updateHigherEdLos(curChild);
        }

    }

    private void pruneParent(HigherEducationLOS curParent, String educationOid) throws Exception {
        if (curParent.getChildren() != null) {
            List<HigherEducationLOS> remainingChildren = new ArrayList<HigherEducationLOS>();
            for (HigherEducationLOS curChild : curParent.getChildren()) {
                if (!curChild.getId().equals(educationOid)) {
                    remainingChildren.add(curChild);
                }
            }
            curParent.setChildren(remainingChildren);
            this.indexToSolr(curParent);
            this.dataUpdateService.updateHigherEdLos(curParent);
        }

    }

    private List<HigherEducationLOS> getOrphanedChildren(
            HigherEducationLOS createdLos) {

        List<HigherEducationLOS> orphanedChildren = new ArrayList<HigherEducationLOS>();

        List<String> childOids = new ArrayList<String>();

        ResultV1RDTO<Set<String>> childRes = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(createdLos.getKomoOid());
        if (childRes != null && childRes.getResult() != null) {
            for (String curChildKomoOid : childRes.getResult()) {

                ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>  childEds = this.tarjontaRawService.getHigherEducationByKomo(curChildKomoOid);
                if (childEds != null 
                        && childEds.getResult() != null 
                        && childEds.getResult().getTulokset() != null 
                        && !childEds.getResult().getTulokset().isEmpty()) {

                    //List<String> higherEdsToIndex = new ArrayList<String>();



                    for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> childTarjResult :  childEds.getResult().getTulokset()) {
                        if (childTarjResult.getTulokset() !=  null && !childTarjResult.getTulokset().isEmpty()) {
                            for (KoulutusHakutulosV1RDTO curChildEd : childTarjResult.getTulokset()) {
                                childOids.add(curChildEd.getOid());
                            }
                        }
                    }

                }
            }
        }


        if (createdLos.getChildren()!= null) {
            for (HigherEducationLOS curChild : createdLos.getChildren()) {

                if (!childOids.contains(curChild.getId())) {

                    if (curChild.getParents() != null) {

                        List<HigherEducationLOS> remainingParents = new ArrayList<HigherEducationLOS>();
                        for (HigherEducationLOS curParent : curChild.getParents()) {
                            if (!curParent.getId().equals(createdLos.getId())) {
                                remainingParents.add(curParent);
                            }
                        }
                        curChild.setParents(remainingParents);
                    }

                    orphanedChildren.add(curChild);
                }
            }
        }


        return orphanedChildren;
    }


    private List<HigherEducationLOS> fetchParentsOfLos(String curKomoOid) {

        List<HigherEducationLOS> parents = new ArrayList<HigherEducationLOS>();

        ResultV1RDTO<Set<String>> parentRes = this.tarjontaRawService.getParentsOfHigherEducationLOS(curKomoOid);
        if (parentRes != null && parentRes.getResult() != null) {
            for (String curParentKomoOid : parentRes.getResult()) {

                ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>  parentEds = this.tarjontaRawService.getHigherEducationByKomo(curParentKomoOid);
                if (parentEds != null 
                        && parentEds.getResult() != null 
                        && parentEds.getResult().getTulokset() != null 
                        && !parentEds.getResult().getTulokset().isEmpty()) {

                    //List<String> higherEdsToIndex = new ArrayList<String>();

                    for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> parentTarjResult :  parentEds.getResult().getTulokset()) {
                        if (parentTarjResult.getTulokset() !=  null && !parentTarjResult.getTulokset().isEmpty()) {
                            for (KoulutusHakutulosV1RDTO curParentEd : parentTarjResult.getTulokset()) {
                                LOS curLos = this.dataQueryService.getLos(curParentEd.getOid());
                                if (curLos != null && curLos instanceof HigherEducationLOS) {
                                    parents.add((HigherEducationLOS)curLos);
                                }
                            }
                        }
                    }
                }

            }
        }

        return parents;
    }
    
    /*
     * Indexing of an added higher education to solr
     */
    private void indexToSolr(HigherEducationLOS curLOS,
            HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws Exception {
        this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
        for (HigherEducationLOS curChild: curLOS.getChildren()) {
            indexToSolr(curChild, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        }
    }
    
    private void indexToSolr(HigherEducationLOS curLOS) throws Exception {
        LOG.debug("Indexing higher ed: " + curLOS.getId());
        LOG.debug("Indexing higher ed: " + curLOS.getShortTitle());
        this.indexerService.removeLos(curLOS, loHttpSolrServer);
        this.indexerService.addLearningOpportunitySpecification(curLOS, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        for (HigherEducationLOS curChild: curLOS.getChildren()) {
            indexToSolr(curChild, loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer);
        }
    }

    
}
