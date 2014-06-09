package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import fi.vm.sade.koulutusinformaatio.dao.entity.AdultUpperSecondaryLOSEntity;

public class AdultUpperSecondaryLOSDAO extends BasicDAO<AdultUpperSecondaryLOSEntity, String>{

    public AdultUpperSecondaryLOSDAO(
            Class<AdultUpperSecondaryLOSEntity> entityClass, Datastore ds) {
        super(entityClass, ds);
        // TODO Auto-generated constructor stub
    }

}
