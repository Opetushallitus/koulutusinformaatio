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
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;

/**
 * 
 * @author Markus
 *
 */
@Component
public class IncrementalApplicationSystemIndexer {
    
    private EducationIncrementalDataQueryService dataQueryService;
    
    private IncrementalLOSIndexer losIndexer;
    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    @Autowired
    public IncrementalApplicationSystemIndexer(TarjontaService tarjontaService,
                                                EducationIncrementalDataQueryService dataQueryService, 
                                                IncrementalLOSIndexer losIndexer,
                                                IndexerService indexerService,
                                                HttpSolrServer loHttpSolrServer,
                                                HttpSolrServer lopHttpSolrServer,
                                                HttpSolrServer locationHttpSolrServer) {
        this.dataQueryService = dataQueryService;
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
            if (!tarjontaService.hasAlreadyProcessedOid(string))
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
    
}
