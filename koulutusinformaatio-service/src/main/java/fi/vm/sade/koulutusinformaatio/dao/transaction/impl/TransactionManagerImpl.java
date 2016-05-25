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

package fi.vm.sade.koulutusinformaatio.dao.transaction.impl;

import java.io.IOException;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.MongoClient;

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.exception.KICommitException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ParameterService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;

/**
 * @author Mikko Majapuro
 */
@Service
public class TransactionManagerImpl implements TransactionManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(TransactionManagerImpl.class);
    private final OphHttpClient httpclient;

    private MongoClient mongo;
    private final String transactionDbName;
    private final String dbName;
    private final String providerUpdateCoreName;
    private final String providerCoreName;
    private final String learningopportunityUpdateCoreName;
    private final String learningopportunityCoreName;
    private final String locationUpdateCoreName;
    private final String locationCoreName;
    
    private HttpSolrServer adminHttpSolrServer;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private PictureDAO pictureTransactionDAO;
    private HigherEducationLOSDAO higherEducationLOSTransactionDAO;
    private AdultVocationalLOSDAO adultVocationalLOSTransactionDAO;
    private DataStatusDAO dataStatusTransactionDAO;
    private KoulutusLOSDAO koulutusLOSTransactionDAO;
    private TutkintoLOSDAO tutkintoLOSTransactionDAO;

    private ApplicationOptionDAO applicationOptionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private AdultVocationalLOSDAO adultVocationalLOSDAO;
    private KoulutusLOSDAO koulutusLOSDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;
    
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private ParameterService parameterService;

    @Value("${solr.learningopportunity.alias.url:learning_opportunity}")
    private String loHttpAliasName;

    @Value("${solr.provider.alias.url:provider}")
    private String lopHttpAliasName;

    @Value("${solr.location.alias.url:location}")
    private String locationHttpAliasName;

    @Value("${solr.learningopportunity.url:learning_opportunity}")
    private String loHttpSolrName;

    @Autowired
    public TransactionManagerImpl(MongoClient mongo, @Value("${mongo.transaction-db.name}") String transactionDbName,
            @Value("${mongo.db.name}") String dbName,
            @Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
            @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer,
            @Qualifier("locationUpdateHttpSolrServer") HttpSolrServer locationUpdateHttpSolrServer,
            @Qualifier("adminHttpSolrServer") HttpSolrServer adminHttpSolrServer,
            @Value("${solr.provider.url}") String providerCoreName,
            @Value("${solr.provider.update.url}") String providerUpdateCoreName,
            @Value("${solr.learningopportunity.url}") String learningopportunityCoreName,
            @Value("${solr.learningopportunity.update.url}") String learningopportunityUpdateCoreName,
            @Value("${solr.location.url}") String locationCoreName,
            @Value("${solr.location.update.url}") String locationUpdateCoreName,
            ApplicationOptionDAO applicationOptionTransactionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
            PictureDAO pictureTransactionDAO,
            HigherEducationLOSDAO higherEducationLOSTransactionDAO,
            AdultVocationalLOSDAO adultVocationalLOSTransactionDAO,
            KoulutusLOSDAO koulutusLOSTransactionDAO,
            TutkintoLOSDAO tutkintoLOSTransactionDAO,
            DataStatusDAO dataStatusTransactionDAO,
            ApplicationOptionDAO applicationOptionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            DataStatusDAO dataStatusDAO,
            PictureDAO pictureDAO,
            HigherEducationLOSDAO higherEducationLOSDAO,
            AdultVocationalLOSDAO adultVocationalLOSDAO,
            KoulutusLOSDAO koulutusLOSDAO,
            TutkintoLOSDAO tutkintoLOSDAO,
            KoodistoService koodistoService,
            ProviderService providerService,
            ParameterService parameterService,
            HttpClient client) {

        this.mongo = mongo;
        this.transactionDbName = transactionDbName;
        this.dbName = dbName;
        this.providerCoreName = providerCoreName;
        this.providerUpdateCoreName = providerUpdateCoreName;
        this.learningopportunityUpdateCoreName = learningopportunityUpdateCoreName;
        this.learningopportunityCoreName = learningopportunityCoreName;
        this.locationCoreName = locationCoreName;
        this.locationUpdateCoreName = locationUpdateCoreName;
        this.adminHttpSolrServer = adminHttpSolrServer;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.higherEducationLOSTransactionDAO = higherEducationLOSTransactionDAO;
        this.adultVocationalLOSTransactionDAO = adultVocationalLOSTransactionDAO;
        this.koulutusLOSTransactionDAO = koulutusLOSTransactionDAO;
        this.tutkintoLOSTransactionDAO = tutkintoLOSTransactionDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.adultVocationalLOSDAO = adultVocationalLOSDAO;
        this.koulutusLOSDAO = koulutusLOSDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.parameterService = parameterService;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.httpclient = client.getClient();
    }

    @Override
    public void beginTransaction(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        this.koodistoService.clearCache();
        this.providerService.clearCache();
        this.parameterService.clearCache();
    }

    @Override
    public void rollBack(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        
        this.koodistoService.clearCache();
        this.providerService.clearCache();
        this.parameterService.clearCache();
        
    }

    @Override
    public void commit(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws KICommitException {
        //CollectionAdminRequest
        //If solr is not in cloud mode doing swap using CoreAdminRequest
        try {
            if (this.loHttpAliasName.equals(this.loHttpSolrName)) {
                CoreAdminRequest lopCar = getCoreSwapRequest(providerUpdateCoreName, providerCoreName);
                lopCar.process(adminHttpSolrServer);

                CoreAdminRequest loCar = getCoreSwapRequest(learningopportunityUpdateCoreName, learningopportunityCoreName);
                loCar.process(adminHttpSolrServer);

                CoreAdminRequest locationCar = getCoreSwapRequest(locationUpdateCoreName, locationCoreName);
                locationCar.process(adminHttpSolrServer);

                //Otherwise using collections api
            } else {
                swapAliases(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            }

            BasicDBObject cmd = new BasicDBObject("copydb", 1).append("fromdb", transactionDbName).append("todb", dbName);
            dropDbCollections();
            CommandResult result = mongo.getDB("admin").command(cmd);
            if (!result.ok()) {
                LOG.error("Collection copy failed, transactiondb left intact: " + result.getErrorMessage());
                throw new KICommitException("Collection copy failed: " + result.getErrorMessage());
            }
            dropTransactionDbCollections();
            this.koodistoService.clearCache();
            this.providerService.clearCache();
            this.parameterService.clearCache();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new KICommitException(ex);
        }
    }

    private void dropUpdateData(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        try {
            mongo.dropDatabase(transactionDbName);

            loUpdateSolr.deleteByQuery("*:*");
            loUpdateSolr.commit();
            loUpdateSolr.optimize();
            lopUpdateSolr.deleteByQuery("*:*");
            lopUpdateSolr.commit();
            lopUpdateSolr.optimize();
            locationUpdateSolr.deleteByQuery("*:*");
            locationUpdateSolr.commit();
            locationUpdateSolr.optimize();

        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    private void dropTransactionDbCollections() {
        applicationOptionTransactionDAO.getCollection().drop();
        learningOpportunityProviderTransactionDAO.getCollection().drop();
        pictureTransactionDAO.getCollection().drop();
        higherEducationLOSTransactionDAO.getCollection().drop();
        adultVocationalLOSTransactionDAO.getCollection().drop();
        dataStatusTransactionDAO.getCollection().drop();
        koulutusLOSTransactionDAO.getCollection().drop();
        tutkintoLOSTransactionDAO.getCollection().drop();
    }

    private void dropDbCollections() {
        applicationOptionDAO.getCollection().drop();
        dataStatusDAO.getCollection().drop();
        pictureDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        higherEducationLOSDAO.getCollection().drop();
        adultVocationalLOSDAO.getCollection().drop();
        koulutusLOSDAO.getCollection().drop();
        tutkintoLOSDAO.getCollection().drop();
        
    }

    private CoreAdminRequest getCoreSwapRequest(final String fromCore, final String toCore) {
        CoreAdminRequest car = new CoreAdminRequest();
        car.setCoreName(fromCore);
        car.setOtherCoreName(toCore);
        car.setAction(CoreAdminParams.CoreAdminAction.SWAP);
        return car;
    }

    private void swapAliases(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws KICommitException {


        boolean ok = swapAlias(getCollectionName(lopUpdateSolr), lopHttpAliasName);
        ok = ok ? swapAlias(getCollectionName(loUpdateSolr), loHttpAliasName) : ok;
        ok = ok ? swapAlias(getCollectionName(locationUpdateSolr), locationHttpAliasName) : ok;

        if (!ok) {
            //Rollbacking the failed swap
            if (getCollectionName(loUpdateSolr).equals(this.learningopportunityCoreName)) {
                swapAlias(this.learningopportunityUpdateCoreName, loHttpAliasName);
                swapAlias(this.providerUpdateCoreName, lopHttpAliasName);
                swapAlias(this.locationUpdateCoreName, locationHttpAliasName);
            } else {
                swapAlias(this.learningopportunityCoreName, loHttpAliasName);
                swapAlias(this.providerCoreName, lopHttpAliasName);
                swapAlias(this.locationCoreName, locationHttpAliasName);
            }

            throw new KICommitException("Alias swap failed");
        }
    }

    private boolean swapAlias(String solrToSwapName, String aliasName) throws KICommitException {
        try {
            return httpclient.get("solr.swap", aliasName, solrToSwapName).execute(new OphHttpResponseHandler<Boolean>() {
                @Override
                public Boolean handleResponse(OphHttpResponse response) throws IOException {
                    return response.getStatusCode() < 400;
                }
            });
        } catch (Exception ex) {
            throw new KICommitException(ex);
        }
    }

    private String getCollectionName(HttpSolrServer solrServer) {
        return solrServer.getBaseURL().substring(solrServer.getBaseURL().lastIndexOf('/') + 1);
    }
}
