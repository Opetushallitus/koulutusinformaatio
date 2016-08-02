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

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KISolrException;
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
                                             HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr) throws KISolrException;

    /**
     * Commits learning opportunities from memory to index.
     * @param lopUpdateSolr 
     * @param loUpdateSolr 
     */
    void commitLOChanges(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr, 
                         boolean createTimestamp) throws KISolrException;
    
    void addLocations(List<Location> locations, HttpSolrServer locationUpdateSolr) throws KISolrException;
    
    HttpSolrServer getLoCollectionToUpdate();

	HttpSolrServer getLopCollectionToUpdate(HttpSolrServer loUpdateSolr);

	HttpSolrServer getLocationCollectionToUpdate(HttpSolrServer loUpdateSolr);

    void addFacetCodes(List<Code> edTypeCodes, HttpSolrServer loUpdateSolr) throws KISolrException;
    
    void addArticles(HttpSolrServer loUpdateSolr, List<Article> articles) throws KISolrException;

    void removeLos(LOS curLos, HttpSolrServer loHttpSolrServer) throws KISolrException;

    void removeArticles() throws KISolrException, SearchException;

    void addArticles(List<Article> articles) throws KISolrException;

    void rollbackIncrementalSolrChanges() throws KISolrException;

    void indexASToSolr(CalendarApplicationSystem curAs, HttpSolrServer loUpdateSolr) throws KISolrException;
    
    /**
     * Tests whether a document with give id is in the solr given as parameter.
     * 
     * @param docId The document id to test
     * @param server The solr to test
     * @return true if document is in index, false otherwise.
     */
    boolean isDocumentInIndex(String docId, HttpSolrServer server);
    
    /**
     * 
     * Indexes the given provider to solr.
     * 
     * @param provider - the provider to index.
     * @param lopSolr - the solr to index.
     * @param requiredBaseEducations - Base educations to which are required to be a student in this provider.
     * @param vocationalAsIds - the vocational application system ids relevant for this provider.
     * @param nonVocationalAsIds - the non-vocational application system ids relevant for this provider.
     * @param providerAsIds - application system ids relevant for this provider, irrelevant of type. 
     * @throws SolrServerException
     * @throws IOException
     */
    void createProviderDocs(Provider provider, 
            HttpSolrServer lopSolr, 
            Set<String> requiredBaseEducations, 
            Set<String> vocationalAsIds,
            Set<String> nonVocationalAsIds,
            Set<String> providerAsIds) throws KISolrException;

}
