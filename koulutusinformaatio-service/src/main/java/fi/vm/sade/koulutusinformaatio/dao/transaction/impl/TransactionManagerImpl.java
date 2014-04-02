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

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.exception.KICommitException;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Mikko Majapuro
 */
@Service
public class TransactionManagerImpl implements TransactionManager {
    
    public static final Logger LOG = LoggerFactory.getLogger(TransactionManagerImpl.class);
    private static final int ERROR_STATUS = 400;

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
    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private ChildLearningOpportunityDAO childLOTransactionDAO;
    private PictureDAO pictureTransactionDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO;
    private HigherEducationLOSDAO higherEducationLOSTransactionDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO;

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO;
    private HigherEducationLOSDAO higherEducationLOSDAO;
    private SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO;


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
            ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
            ApplicationOptionDAO applicationOptionTransactionDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
            ChildLearningOpportunityDAO childLOTransactionDAO,
            PictureDAO pictureTransactionDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLOSTransactionDAO,
            HigherEducationLOSDAO higherEducationLOSTransactionDAO,
            SpecialLearningOpportunitySpecificationDAO specialLOSTransactionDAO,
            ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
            ApplicationOptionDAO applicationOptionDAO,
            ChildLearningOpportunityDAO childLearningOpportunityDAO,
            LearningOpportunityProviderDAO learningOpportunityProviderDAO,
            DataStatusDAO dataStatusDAO,
            PictureDAO pictureDAO,
            UpperSecondaryLearningOpportunitySpecificationDAO upperSecondaryLearningOpportunitySpecificationDAO,
            HigherEducationLOSDAO higherEducationLOSDAO, 
            SpecialLearningOpportunitySpecificationDAO specialLearningOpportunitySpecificationDAO) {

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
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.childLOTransactionDAO = childLOTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.upperSecondaryLOSTransactionDAO = upperSecondaryLOSTransactionDAO;
        this.higherEducationLOSTransactionDAO = higherEducationLOSTransactionDAO;
        this.specialLOSTransactionDAO = specialLOSTransactionDAO;
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
        this.upperSecondaryLearningOpportunitySpecificationDAO = upperSecondaryLearningOpportunitySpecificationDAO;
        this.higherEducationLOSDAO = higherEducationLOSDAO;
        this.specialLearningOpportunitySpecificationDAO = specialLearningOpportunitySpecificationDAO;
    }

    @Override
    public void beginTransaction(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
    }

    @Override
    public void rollBack(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
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
            mongo.getDB("admin").command(cmd);
            dropTransactionDbCollections();
        } catch (Exception ex) {
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
        parentLOSTransactionDAO.getCollection().drop();
        applicationOptionTransactionDAO.getCollection().drop();
        learningOpportunityProviderTransactionDAO.getCollection().drop();
        childLOTransactionDAO.getCollection().drop();
        pictureTransactionDAO.getCollection().drop();
        upperSecondaryLOSTransactionDAO.getCollection().drop();
        higherEducationLOSTransactionDAO.getCollection().drop();
        specialLOSTransactionDAO.getCollection().drop();
    }

    private void dropDbCollections() {
        parentLearningOpportunitySpecificationDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        childLearningOpportunityDAO.getCollection().drop();
        dataStatusDAO.getCollection().drop();
        pictureDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
        upperSecondaryLearningOpportunitySpecificationDAO.getCollection().drop();
        higherEducationLOSDAO.getCollection().drop();
        specialLearningOpportunitySpecificationDAO.getCollection().drop();
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
            URL myURL = new URL(String.format("%s%s%s%s%s", 
                    adminHttpSolrServer.getBaseURL(), 
                    SolrConstants.ALIAS_ACTION,
                    aliasName, 
                    SolrConstants.COLLECTIONS, 
                    solrToSwapName));

            HttpURLConnection myURLConnection = (HttpURLConnection)(myURL.openConnection());
            myURLConnection.setRequestMethod(SolrConstants.GET);
            myURLConnection.connect();
            return myURLConnection.getResponseCode() < ERROR_STATUS;
        } catch (Exception ex) {
            throw new KICommitException(ex);
        }
    }

    private String getCollectionName(HttpSolrServer solrServer) {
        return solrServer.getBaseURL().substring(solrServer.getBaseURL().lastIndexOf('/') + 1);
    }
}
