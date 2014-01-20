package fi.vm.sade.koulutusinformaatio.dao;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import com.mongodb.Mongo;

import fi.vm.sade.koulutusinformaatio.dao.entity.UniversityAppliedScienceLOSEntity;

public class UniversityAppliedScienceLOSDAO extends BasicDAO<UniversityAppliedScienceLOSEntity, String> {
	
    public UniversityAppliedScienceLOSDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

}
