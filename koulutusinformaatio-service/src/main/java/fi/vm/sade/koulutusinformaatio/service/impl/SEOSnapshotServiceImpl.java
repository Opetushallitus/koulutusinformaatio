package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.dao.SnapshotDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.SnapshotEntity;
import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SEOSnapshotServiceImpl implements SEOSnapshotService {
    private final SnapshotDAO snapshotDAO;
    private ModelMapper modelMapper;

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
    public void createSnapshot(final SnapshotDTO snapshot) {
        SnapshotEntity snapshotEntity = modelMapper.map(snapshot, SnapshotEntity.class);
        snapshotDAO.save(snapshotEntity);
    }
}
