package fi.vm.sade.koulutusinformaatio.integrationtest;

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;

import java.util.Date;

public class SEOSnapshotServiceMock implements SEOSnapshotService {
    @Override
    public SnapshotDTO getSnapshot(String oid) {
        SnapshotDTO snapshotDTO = new SnapshotDTO("1.2.246.562.17.10107541848", "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Demo snapshot</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Demo snapshot</h1>\n" +
                "    <p>Demo snapshot</p>\n" +
                "</body>\n" +
                "</html>", new Date());
        return snapshotDTO;
    }

    @Override
    public void createSnapshot(SnapshotDTO snapshot) {

    }
}
