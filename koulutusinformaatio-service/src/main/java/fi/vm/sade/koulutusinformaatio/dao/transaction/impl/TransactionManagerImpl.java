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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service
public class TransactionManagerImpl implements TransactionManager {

    private Mongo mongo;
    private final String transactionDbName;
    private final String dbName;
    private DataStatusDAO dataStatusTransactionDAO;

    @Autowired
    public TransactionManagerImpl(Mongo mongo, @Value("${mongo.transaction-db.name}") String transactionDbName,
                                  @Value("${mongo.db.name}") String dbName, DataStatusDAO dataStatusTransactionDAO) {
        this.mongo = mongo;
        this.transactionDbName = transactionDbName;
        this.dbName = dbName;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
    }

    @Override
    public void beginTransaction() {
        mongo.dropDatabase(transactionDbName);
    }

    @Override
    public void rollBack() {
        mongo.dropDatabase(transactionDbName);
    }

    @Override
    public void commit() {
        dataStatusTransactionDAO.save(new DataStatusEntity());
        DBObject cmd = new BasicDBObject("copydb", 1).append("fromdb", transactionDbName).append("todb", dbName);
        mongo.dropDatabase(dbName);
        CommandResult result = mongo.getDB("admin").command(cmd);
        mongo.dropDatabase(transactionDbName);
    }
}
