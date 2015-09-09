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

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionDAO extends SecondaryAwareDAO<ApplicationOptionEntity, String> {

    public ApplicationOptionDAO(Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
    }


    public List<ApplicationOptionEntity> findFromSecondary(final String asId, final String lopId, final String baseEducation,
            boolean vocational, boolean nonVocational) {
        Query<ApplicationOptionEntity> query = createSecondaryQuery();
        query.field("applicationSystem.id").equal(asId);
        query.field("provider").equal(new Key<LearningOpportunityProviderEntity>(LearningOpportunityProviderEntity.class, lopId));
        if (!Strings.isNullOrEmpty(baseEducation)) {
            query.field("requiredBaseEducations").equal(baseEducation);
        }
        if (!vocational) {
            query.field("vocational").equal(false);
        }
        if (!nonVocational) {
            query.field("vocational").equal(true);
        }
        return find(query).asList();
    }

    public List<ApplicationOptionEntity> findFromSecondary(final List<String> aoIds) {
        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity> ();
        for (String curId : aoIds) {
            ApplicationOptionEntity curAo = getSecondaryDatastore().get(ApplicationOptionEntity.class, curId);
            if (curAo != null) {
                aos.add(curAo);
            }
        }
        return aos;
    }

    public Query<ApplicationOptionEntity> createSecondaryQuery() {
        return getSecondaryDatastore().createQuery(entityClazz);
    }


    public List<ApplicationOptionEntity> find(final String asId, final String lopId, final String baseEducation,
            boolean vocational, boolean nonVocational) {
        Query<ApplicationOptionEntity> query = createQuery();
        query.field("applicationSystem.id").equal(asId);
        query.field("provider").equal(new Key(LearningOpportunityProviderEntity.class, lopId));
        if (!Strings.isNullOrEmpty(baseEducation)) {
            query.field("requiredBaseEducations").equal(baseEducation);
        }
        if (!vocational) {
            query.field("vocational").equal(false);
        }
        if (!nonVocational) {
            query.field("vocational").equal(true);
        }
        return find(query).asList();
    }

    public List<ApplicationOptionEntity> find(final List<String> aoIds) {
        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity> ();
        for (String curId : aoIds) {
            ApplicationOptionEntity curAo = getDatastore().get(ApplicationOptionEntity.class, curId);
            if (curAo != null) {
                aos.add(curAo);
            }
        }
        return aos;
    }
    
    public List<Key<ApplicationOptionEntity>> findByAS(final String asId) {
        Query<ApplicationOptionEntity> query= createQuery();
        query.field("applicationSystem.id").equal(asId);
        //query.
        return find(query).asKeyList();
    }

}
