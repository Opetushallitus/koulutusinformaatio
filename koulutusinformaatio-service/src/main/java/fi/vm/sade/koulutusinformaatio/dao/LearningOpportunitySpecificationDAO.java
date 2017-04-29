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

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;

/**
 * @author Hannu Lyytikainen
 */
public abstract class LearningOpportunitySpecificationDAO<T, K> extends SecondaryAwareDAO<T, K> {


    protected LearningOpportunitySpecificationDAO(Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
    }

    public List<T> findByProviderId(String providerId) {
        Query<T> query = createQuery();
        query.field("provider").equal(new Key<>(LearningOpportunityProviderEntity.class, "learningOpportunityProviders", providerId));
        query.project("id", true);
        query.project("name", true);
        return find(query).asList();
    }
    
    public List<T> findByLoiId(String loiId) {
        Query<T> q = createQuery();
        q.field("lois.id").equal(loiId);
        return find(q).asList();
    }

}


