package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Datastore;

import fi.vm.sade.koulutusinformaatio.dao.entity.ValmaLOSEntity;

public class ValmaLOSDAO extends LearningOpportunitySpecificationDAO<ValmaLOSEntity, String>{

    public ValmaLOSDAO(
            Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
        ensureIndexes();
    }

}
