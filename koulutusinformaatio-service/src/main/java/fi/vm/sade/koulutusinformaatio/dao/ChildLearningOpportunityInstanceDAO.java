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

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityInstanceEntity;

/**
 * @author Mikko Majapuro
 */
public class ChildLearningOpportunityInstanceDAO extends BasicDAO<ChildLearningOpportunityInstanceEntity, String> {

    public ChildLearningOpportunityInstanceDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }
}
