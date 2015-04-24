package fi.vm.sade.koulutusinformaatio.service.builder.partial.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationOptionIndexer;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental.IncrementalApplicationSystemIndexer;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartialApplicationSystemUpdateIndexerTest {

    private final static String APPLICATION_OID = "121.21321.41.2312";
    
    private final static List<String> ASSOCIATED_APPLICATION_OPTION_OIDS = Arrays.asList("123.12", "414.123", "345.2.3");
    
    @Mock
    private TarjontaRawService tarjontaService;
    
    @Mock
    private IncrementalApplicationSystemIndexer asIndexer; 
    
    @Mock
    private IncrementalApplicationOptionIndexer aoIndexer;
    
    @InjectMocks
    private PartialApplicationSystemUpdateIndexer indexer;
    
    @Before
    public void init() {
        when(tarjontaService.getV1EducationHakuByOid(APPLICATION_OID)).thenReturn(new ResultV1RDTO<HakuV1RDTO>(new HakuV1RDTO()));
        when(tarjontaService.getV1EducationHakukohode(any(String.class))).thenReturn(new ResultV1RDTO<HakukohdeV1RDTO>());
    }
    
    @Test
    public void usesTarjontaServiceToFetchEducation() throws Exception {
        indexer.update(APPLICATION_OID);
        verify(tarjontaService).getV1EducationHakuByOid(APPLICATION_OID);
    }
    
    @Test
    public void updatesAllApplicationOptionsAssociatedWithSystem() throws Exception {        
        when(tarjontaService.getHakukohdesByHaku(APPLICATION_OID)).thenReturn(givenApplicationOptionsResponse());
        indexer.update(APPLICATION_OID);
        verify(aoIndexer, times(ASSOCIATED_APPLICATION_OPTION_OIDS.size())).indexApplicationOptionData(any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class));
    }
    
    @Test
    public void exceptionCausedByApplicationOptionIsCaught() throws Exception {
        when(tarjontaService.getHakukohdesByHaku(APPLICATION_OID)).thenReturn(givenApplicationOptionsResponse());
        doThrow(new RuntimeException()).when(aoIndexer).indexApplicationOptionData(any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class));
        indexer.update(APPLICATION_OID);
        verify(aoIndexer, times(ASSOCIATED_APPLICATION_OPTION_OIDS.size())).indexApplicationOptionData(any(HakukohdeV1RDTO.class), any(HakuV1RDTO.class));
    }

    private List<OidRDTO> givenApplicationOptionsResponse() {
        return Lists.transform(ASSOCIATED_APPLICATION_OPTION_OIDS, new Function<String, OidRDTO>() {

            @Override
            public OidRDTO apply(String input) {
                return new OidRDTO(input);
            }
            
        });
    }
    
}
