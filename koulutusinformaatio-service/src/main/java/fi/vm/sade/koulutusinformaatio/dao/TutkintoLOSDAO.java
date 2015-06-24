package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Datastore;

import fi.vm.sade.koulutusinformaatio.dao.entity.TutkintoLOSEntity;

public class TutkintoLOSDAO extends LearningOpportunitySpecificationDAO<TutkintoLOSEntity, String> {

    public TutkintoLOSDAO(
            Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
        ensureIndexes();
    }

}
