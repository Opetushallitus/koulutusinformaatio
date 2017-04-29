package fi.vm.sade.koulutusinformaatio.dao;

import fi.vm.sade.koulutusinformaatio.dao.entity.KoulutusLOSEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityProviderEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class KoulutusLOSDAO extends LearningOpportunitySpecificationDAO<KoulutusLOSEntity, String>{

    public KoulutusLOSDAO(
            Datastore primaryDatastore, Datastore secondaryDatastore) {
        super(primaryDatastore, secondaryDatastore);
        ensureIndexes();
    }

    public List<KoulutusLOSEntity> getKoulutusLos(ToteutustyyppiEnum toteutusTyyppi, String tarjoaja, String koulutusKoodi)
            throws ResourceNotFoundException {
        Query<KoulutusLOSEntity> q = this.getDatastore().createQuery(KoulutusLOSEntity.class)
                .filter("provider", new Key<>(LearningOpportunityProviderEntity.class, "learningOpportunityProviders", tarjoaja))
                .filter("toteutustyyppi = ", toteutusTyyppi)
                .filter("educationCode.uri = ", koulutusKoodi);
        return this.find(q).asList();
    }

}
