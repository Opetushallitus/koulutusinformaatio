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

import com.google.common.base.Strings;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionDAO extends BasicDAO<ApplicationOptionEntity, String> {

    public ApplicationOptionDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

    public List<ApplicationOptionEntity> find(final String asId, final String lopId, final String baseEducation) {
        Query<ApplicationOptionEntity> query = createQuery();
        query.field("applicationSystem.id").equal(asId);
        query.field("provider").equal(new Key(LearningOpportunityProviderEntity.class, lopId));
        if (!Strings.isNullOrEmpty(baseEducation)) {
            query.field("requiredBaseEducations").contains(baseEducation);
        }
        return find(query).asList();
    }

    public List<ApplicationOptionEntity> find(final List<String> aoIds) {
        Query<ApplicationOptionEntity> query = getDatastore().get(ApplicationOptionEntity.class, aoIds);
        return find(query).asList();
    }
}
