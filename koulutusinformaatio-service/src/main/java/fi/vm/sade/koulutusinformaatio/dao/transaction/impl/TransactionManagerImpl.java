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
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Mikko Majapuro
 */
@Service
public class TransactionManagerImpl implements TransactionManager {

    private Mongo mongo;
    private final String transactionDbName;
    private final String dbName;
    private final String providerUpdateCoreName;
    private final String providerCoreName;
    private final String learningopportunityUpdateCoreName;
    private final String learningopportunityCoreName;
    private DataStatusDAO dataStatusTransactionDAO;
    private HttpSolrServer loUpdateHttpSolrServer;
    private HttpSolrServer lopUpdateHttpSolrServer;
    private HttpSolrServer adminHttpSolrServer;
    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private ChildLearningOpportunityDAO childLOTransactionDAO;
    private PictureDAO pictureTransactionDAO;

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;

    @Autowired
    public TransactionManagerImpl(Mongo mongo, @Value("${mongo.transaction-db.name}") String transactionDbName,
                                  @Value("${mongo.db.name}") String dbName, DataStatusDAO dataStatusTransactionDAO,
                                  @Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
                                  @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer,
                                  @Qualifier("adminHttpSolrServer") HttpSolrServer adminHttpSolrServer,
                                  @Value("${solr.provider.url}") String providerCoreName,
                                  @Value("${solr.provider.update.url}") String providerUpdateCoreName,
                                  @Value("${solr.learningopportunity.url}") String learningopportunityCoreName,
                                  @Value("${solr.learningopportunity.update.url}") String learningopportunityUpdateCoreName,
                                  ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
                                  ApplicationOptionDAO applicationOptionTransactionDAO,
                                  LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
                                  ChildLearningOpportunityDAO childLOTransactionDAO,
                                  PictureDAO pictureTransactionDAO,
                                  ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
                                  ApplicationOptionDAO applicationOptionDAO,
                                  ChildLearningOpportunityDAO childLearningOpportunityDAO,
                                  LearningOpportunityProviderDAO learningOpportunityProviderDAO,
                                  DataStatusDAO dataStatusDAO,
                                  PictureDAO pictureDAO) {
        this.mongo = mongo;
        this.transactionDbName = transactionDbName;
        this.dbName = dbName;
        this.providerCoreName = providerCoreName;
        this.providerUpdateCoreName = providerUpdateCoreName;
        this.learningopportunityUpdateCoreName = learningopportunityUpdateCoreName;
        this.learningopportunityCoreName = learningopportunityCoreName;
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
        this.adminHttpSolrServer = adminHttpSolrServer;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.childLOTransactionDAO = childLOTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
    }

    @Override
    public void beginTransaction() {
        dropUpdateData();
    }

    @Override
    public void rollBack() {
        dropUpdateData();
    }

    @Override
    public void commit() throws IOException, SolrServerException {
        CoreAdminRequest lopCar = getCoreSwapRequest(providerUpdateCoreName, providerCoreName);
        lopCar.process(adminHttpSolrServer);

        CoreAdminRequest loCar = getCoreSwapRequest(learningopportunityUpdateCoreName, learningopportunityCoreName);
        loCar.process(adminHttpSolrServer);

        dataStatusTransactionDAO.save(new DataStatusEntity());
        DBObject cmd = new BasicDBObject("copydb", 1).append("fromdb", transactionDbName).append("todb", dbName);
        dropDbCollections();
        mongo.getDB("admin").command(cmd);
        dropTransactionDbCollections();
    }

    private void dropUpdateData() {
        try {
            dropTransactionDbCollections();
            loUpdateHttpSolrServer.deleteByQuery("*:*");
            loUpdateHttpSolrServer.commit();
            loUpdateHttpSolrServer.optimize();
            lopUpdateHttpSolrServer.deleteByQuery("*:*");
            lopUpdateHttpSolrServer.commit();
            lopUpdateHttpSolrServer.optimize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dropTransactionDbCollections() {
        parentLOSTransactionDAO.getCollection().drop();
        applicationOptionTransactionDAO.getCollection().drop();
        learningOpportunityProviderTransactionDAO.getCollection().drop();
        childLOTransactionDAO.getCollection().drop();
        pictureTransactionDAO.getCollection().drop();
    }

    private void dropDbCollections() {
        parentLearningOpportunitySpecificationDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        childLearningOpportunityDAO.getCollection().drop();
        dataStatusDAO.getCollection().drop();
        pictureDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
    }

    private CoreAdminRequest getCoreSwapRequest(final String fromCore, final String toCore) {
        CoreAdminRequest car = new CoreAdminRequest();
        car.setCoreName(fromCore);
        car.setOtherCoreName(toCore);
        car.setAction(CoreAdminParams.CoreAdminAction.SWAP);
        return car;
    }
}
