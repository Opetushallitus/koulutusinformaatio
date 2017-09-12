package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.dao.SnapshotDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.SnapshotEntity;
import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.modelmapper.ModelMapper;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;

@Service
public class SEOSnapshotServiceImpl implements SEOSnapshotService {
    private final SnapshotDAO snapshotDAO;
    private ModelMapper modelMapper;

    private final String SITEMAP_OID = "sitemap";
    private final String TIMESTAMP_OID = "timestamp";


    @Autowired
    public SEOSnapshotServiceImpl(SnapshotDAO snapshotDAO, ModelMapper modelMapper) {
        this.snapshotDAO = snapshotDAO;
        this.modelMapper = modelMapper;
    }

    @Override
    public SnapshotDTO getSnapshot(final String oid) {
        SnapshotEntity snapshotEntity = snapshotDAO.get(oid);
        if (snapshotEntity != null) {
            return modelMapper.map(snapshotEntity, SnapshotDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public SnapshotDTO getSitemap() {
        return getSnapshot(SITEMAP_OID);
    }

    @Override
    public void setSitemap(String content) {
        createSnapshot(new SnapshotDTO(SITEMAP_OID, content, new Date()));
    }

    @Override
    public Date getLastSeoIndexingDate() {
        SnapshotDTO timestamp = getSnapshot(TIMESTAMP_OID);
        return timestamp == null ? null : timestamp.getSnapshotCreated();
    }

    @Override
    public void setLastSeoIndexingDate(Date date) {
        createSnapshot(new SnapshotDTO(TIMESTAMP_OID, TIMESTAMP_OID, date));
    }

    @Override
    public void createSnapshot(final SnapshotDTO snapshot) {
        SnapshotEntity snapshotEntity = modelMapper.map(snapshot, SnapshotEntity.class);
        snapshotDAO.save(snapshotEntity);
    }

    @Override
    public void deleteOldSnapshots() {
        Query<SnapshotEntity> q = snapshotDAO.createQuery();
        q.field("snapshotCreated").lessThan(dateTenMonthsAgo());
        snapshotDAO.deleteByQuery(q);
    }

    private Date dateTenMonthsAgo() {
        Calendar i = Calendar.getInstance();
        i.add(Calendar.MONTH, -10);
        return i.getTime();
    }

}
