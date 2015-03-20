package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Datastore;

import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;

public class KoulutusLOSDAO extends LearningOpportunitySpecificationDAO<KoulutusLOSEntity, String>{

    public KoulutusLOSDAO(
            Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
        ensureIndexes();
    }

}
