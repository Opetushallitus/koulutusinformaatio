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
package fi.vm.sade.koulutusinformaatio.dao;

import java.util.List;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import org.mongodb.morphia.query.Query;

/**
 * 
 * @author Markus
 */
public class HigherEducationLOSDAO extends BasicDAO<HigherEducationLOSEntity, String> {
    
    public HigherEducationLOSDAO(MongoClient mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

    public List<HigherEducationLOSEntity> findByProviderId(String providerId) {
        Query<HigherEducationLOSEntity> query = createQuery();
        query.field("provider").equal(new Key<>(LearningOpportunityProviderEntity.class, "learningOpportunityProviders", providerId));
        query.project("id", true);
        query.project("name", true);
        return find(query).asList();
    }
}
