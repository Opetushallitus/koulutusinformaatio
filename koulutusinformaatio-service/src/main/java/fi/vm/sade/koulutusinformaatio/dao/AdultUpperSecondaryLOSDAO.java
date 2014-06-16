package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Datastore;

import fi.vm.sade.koulutusinformaatio.dao.entity.AdultUpperSecondaryLOSEntity;

public class AdultUpperSecondaryLOSDAO extends LearningOpportunitySpecificationDAO<AdultUpperSecondaryLOSEntity, String>{

    public AdultUpperSecondaryLOSDAO(
            Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
        ensureIndexes();
    }

}
