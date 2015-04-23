package fi.vm.sade.koulutusinformaatio.service.builder.partial.impl;

import java.util.Arrays;
import java.util.List;

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
import static org.mockito.Matchers.any;

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
    
    @Test
    public void usesTarjontaServiceToFetchEducation() throws Exception {
        indexer.update(APPLICATION_OID);
        verify(tarjontaService).getHaku(APPLICATION_OID);
    }
    
    @Test
    public void updatesAllApplicationOptionsAssociatedWithSystem() throws Exception {        
        when(tarjontaService.getHakukohdesByHaku(APPLICATION_OID)).thenReturn(givenApplicationOptionsResponse());
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
