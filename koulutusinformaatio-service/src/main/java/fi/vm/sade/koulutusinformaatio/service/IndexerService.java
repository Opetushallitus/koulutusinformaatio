/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.CalendarApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface IndexerService {

    /**
     * Adds an learning opportunity parent and it's children into solar.
     * The data is not committed to index.
     */
    void addLearningOpportunitySpecification(LOS los,
                                             HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr) throws IOException, SolrServerException;

    /**
     * Commits learning opportunities from memory to index.
     * @param lopUpdateSolr 
     * @param loUpdateSolr 
     */
    void commitLOChanges(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr, 
                         boolean createTimestamp) throws IOException, SolrServerException;
    
    void addLocations(List<Location> locations, HttpSolrServer locationUpdateSolr) throws IOException, SolrServerException;
    
    HttpSolrServer getLoCollectionToUpdate();

	HttpSolrServer getLopCollectionToUpdate(HttpSolrServer loUpdateSolr);

	HttpSolrServer getLocationCollectionToUpdate(HttpSolrServer loUpdateSolr);

    void addEdTypeCodes(List<Code> edTypeCodes, HttpSolrServer loUpdateSolr) throws IOException, SolrServerException;
    
    void addArticles(HttpSolrServer loUpdateSolr, List<Article> articles) throws IOException, SolrServerException;

    void removeLos(LOS curLos, HttpSolrServer loHttpSolrServer) throws IOException, SolrServerException;

    void removeArticles() throws SearchException, IOException;

    void addArticles(List<Article> articles) throws IOException, SolrServerException;

    void rollbackIncrementalSolrChanges() throws SolrServerException, IOException;

    void indexASToSolr(CalendarApplicationSystem curAs, HttpSolrServer loUpdateSolr) throws SolrServerException, IOException;
    
    boolean isDocumentInIndex(String docId, HttpSolrServer server);
    
    void createProviderDocs(Provider provider, 
            HttpSolrServer lopSolr, 
            Set<String> requiredBaseEducations, 
            Set<String> vocationalAsIds,
            Set<String> nonVocationalAsIds,
            Set<String> providerAsIds) throws SolrServerException, IOException;
}
