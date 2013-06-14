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
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
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

    @Autowired
    public TransactionManagerImpl(Mongo mongo, @Value("${mongo.transaction-db.name}") String transactionDbName,
                                  @Value("${mongo.db.name}") String dbName, DataStatusDAO dataStatusTransactionDAO,
                                  @Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
                                  @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer,
                                  @Qualifier("adminHttpSolrServer") HttpSolrServer adminHttpSolrServer,
                                  @Value("${solr.provider.url}") String providerCoreName,
                                  @Value("${solr.provider.update.url}") String providerUpdateCoreName,
                                  @Value("${solr.learningopportunity.url}") String learningopportunityCoreName,
                                  @Value("${solr.learningopportunity.update.url}") String learningopportunityUpdateCoreName) {
        this.mongo = mongo;
        this.transactionDbName = transactionDbName;
        this.dbName = dbName;
        this.providerCoreName = providerCoreName;
        this.providerUpdateCoreName = providerUpdateCoreName;
        this.learningopportunityUpdateCoreName = learningopportunityUpdateCoreName;
        this.learningopportunityCoreName = learningopportunityCoreName;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
        this.adminHttpSolrServer = adminHttpSolrServer;
    }

    @Override
    public void beginTransaction() throws IOException, SolrServerException {
        dropUpdateData();
    }

    @Override
    public void rollBack() {
        try {
            dropUpdateData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() throws IOException, SolrServerException {
        CoreAdminRequest lopCar = getCoreSwapRequest(providerUpdateCoreName, providerCoreName);
        lopCar.process(adminHttpSolrServer);

        CoreAdminRequest loCar = getCoreSwapRequest(learningopportunityUpdateCoreName, learningopportunityCoreName);
        loCar.process(adminHttpSolrServer);

        dataStatusTransactionDAO.save(new DataStatusEntity());
        DBObject cmd = new BasicDBObject("copydb", 1).append("fromdb", transactionDbName).append("todb", dbName);
        mongo.dropDatabase(dbName);
        mongo.getDB("admin").command(cmd);
        mongo.dropDatabase(transactionDbName);
    }

    private void dropUpdateData() throws IOException, SolrServerException {
        mongo.dropDatabase(transactionDbName);
        loUpdateHttpSolrServer.deleteByQuery("*:*");
        loUpdateHttpSolrServer.commit();
        loUpdateHttpSolrServer.optimize();
        lopUpdateHttpSolrServer.deleteByQuery("*:*");
        lopUpdateHttpSolrServer.commit();
        lopUpdateHttpSolrServer.optimize();
    }

    private CoreAdminRequest getCoreSwapRequest(final String fromCore, final String toCore) {
        CoreAdminRequest car = new CoreAdminRequest();
        car.setCoreName(fromCore);
        car.setOtherCoreName(toCore);
        car.setAction(CoreAdminParams.CoreAdminAction.SWAP);
        return car;
    }
}
