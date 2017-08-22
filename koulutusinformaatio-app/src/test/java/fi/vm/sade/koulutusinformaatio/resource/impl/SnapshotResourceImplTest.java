package fi.vm.sade.koulutusinformaatio.resource.impl;

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;
import fi.vm.sade.koulutusinformaatio.service.SEOSnapshotService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SnapshotResourceImplTest {
    @Mock
    private SEOSnapshotService seoSnapshotService;
    private SnapshotResourceImpl snapshotResource;
    private String snapshotContent;
    private SnapshotDTO snapshotDTO;

    @Before
    public void setUp() {
        seoSnapshotService = mock(SEOSnapshotService.class);

        snapshotContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Demo snapshot</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Demo snapshot</h1>\n" +
                "    <p>Demo snapshot</p>\n" +
                "</body>\n" +
                "</html>";
        snapshotDTO = new SnapshotDTO("1.2.246.562.17.10107541848", snapshotContent, new Date());

        when(seoSnapshotService.getSnapshot(anyString())).thenReturn(snapshotDTO);

        snapshotResource = new SnapshotResourceImpl(seoSnapshotService);
    }

    @Test
    public void testGetSnapshot() {
        Response response = snapshotResource.getSnapshotContent("1.2.246.562.17.10107541848");
        String content = (String) response.getEntity();
        assertEquals(content, snapshotContent);
    }
}
