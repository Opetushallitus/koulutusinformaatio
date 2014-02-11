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

import com.mongodb.Mongo;
import com.mongodb.ReadPreference;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;

/**
 * Datastore implementation that reads from secondary node.
 *
 * @author Hannu Lyytikainen
 */
public class SecondaryDatastoreImpl extends DatastoreImpl {

    public SecondaryDatastoreImpl(Morphia morphia, Mongo mongo, String dbName) {
        super(morphia, mongo, dbName);
    }

    @Override
    public <T, V> T get(final Class<T> clazz, final V id) {
        return find(getCollection(clazz).getName(), clazz, Mapper.ID_KEY, id, 0, 1, true)
                .useReadPreference(ReadPreference.secondaryPreferred()).get();
    }
}
