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

package fi.vm.sade.koulutusinformaatio.dao;

import java.util.Arrays;

import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;

/**
 * @author Mikko Majapuro
 */
public class DataStatusDAO extends BasicDAO<DataStatusEntity, ObjectId> {

    public DataStatusDAO(MongoClient mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

    public DataStatusEntity getLatest() {
        Query<DataStatusEntity> query = createQuery();
        query.order("-lastUpdateFinished");
        return find(query).get();
    }
    
    public DataStatusEntity getLatestSuccessOrIncremental() {
        Query<DataStatusEntity> query = createQuery();
        query.field("lastUpdateOutcome").in(Arrays.asList("SUCCESS", "SUCCESS-INCREMENTAL"));
        query.order("-lastUpdateFinished");
        return find(query).get();
    }

    public DataStatusEntity getLatestSEOIndexingSuccessOrIncremental() {
        Query<DataStatusEntity> query = createQuery().disableValidation();
        query.field("lastUpdateOutcome").in(Arrays.asList("SUCCESS", "SUCCESS-INCREMENTAL"));
        query.order("-lastSEOIndexingFinished");
        return find(query).get();
    }

    public DataStatusEntity getLatestSuccess() {
        Query<DataStatusEntity> query = createQuery();
        query.field("lastUpdateOutcome").equal("SUCCESS");
        query.order("-lastUpdateFinished");
        return find(query).get();
    }
}
